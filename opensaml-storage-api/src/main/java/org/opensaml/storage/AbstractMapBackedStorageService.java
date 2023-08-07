/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.storage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.Positive;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.primitive.LoggerFactory;

import org.slf4j.Logger;

/**
 * Partial implementation of {@link StorageService} that stores data in-memory with no persistence
 * using a simple map.
 * 
 * <p>Abstract methods supply the map of data to manipulate and the lock to use, which allows
 * optimizations in cases where locking isn't required or data isn't shared.</p> 
 */
public abstract class AbstractMapBackedStorageService extends AbstractStorageService
        implements EnumeratableStorageService {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractMapBackedStorageService.class);

    /** Constructor. */
    public AbstractMapBackedStorageService() {
        setContextSize(Integer.MAX_VALUE);
        setKeySize(Integer.MAX_VALUE);
        setValueSize(Integer.MAX_VALUE);
    }

    /** {@inheritDoc} */
    @Override
    public boolean create(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value, @Nullable final Long expiration) throws IOException {
        final Lock writeLock = getLock().writeLock();
        
        try {
            writeLock.lock();
            
            final Map<String,Map<String,MutableStorageRecord<?>>> contextMap;
            try {
                contextMap = getContextMap();
            } catch (final Exception e) {
                throw new IOException(e);
            }
            
            // Create new context if necessary.
            Map<String, MutableStorageRecord<?>> dataMap = contextMap.get(context);
            if (dataMap == null) {
                dataMap = new HashMap<>();
                contextMap.put(context, dataMap);
            }
            
            // Check for a duplicate.
            final StorageRecord<?> record = dataMap.get(key);
            if (record != null) {
                // Not yet expired?
                if (record.isValid(System.currentTimeMillis())) {
                    return false;
                }
                
                // It's dead, so we can just remove it now and create the new record.
            }
            
            dataMap.put(key, new MutableStorageRecord<>(value, expiration));
            log.trace("Inserted record '{}' in context '{}' with expiration '{}'",
                    new Object[] { key, context, expiration });
            
            setDirty();
            return true;
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable public <T> StorageRecord<T> read(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key) throws IOException {
        return this.<T>readImpl(context, key, null).getSecond();
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull public <T> Pair<Long, StorageRecord<T>> read(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, final long version) throws IOException {
        return readImpl(context, key, version);
    }

    /** {@inheritDoc} */
    @Override
    public boolean update(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value, @Nullable final Long expiration) throws IOException {
        try {
            return updateImpl(null, context, key, value, expiration) != null;
        } catch (final VersionMismatchException e) {
            throw new IOException("Unexpected exception thrown by update.", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public Long updateWithVersion(final long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nonnull @NotEmpty final String value, @Nullable final Long expiration)
                    throws IOException, VersionMismatchException {
        return updateImpl(version, context, key, value, expiration);
    }

    /** {@inheritDoc} */
    @Override
    public boolean updateExpiration(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nullable final Long expiration) throws IOException {
        try {
            return updateImpl(null, context, key, null, expiration) != null;
        } catch (final VersionMismatchException e) {
            throw new IOException("Unexpected exception thrown by update.", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean deleteWithVersion(final long version, @Nonnull final String context, @Nonnull final String key)
            throws IOException, VersionMismatchException {
        return deleteImpl(version, context, key);
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean delete(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key)
            throws IOException {
        try {
            return deleteImpl(null, context, key);
        } catch (final VersionMismatchException e) {
            throw new IOException("Unexpected exception thrown by delete.", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateContextExpiration(@Nonnull @NotEmpty final String context, @Nullable final Long expiration)
            throws IOException {
        final Lock writeLock = getLock().writeLock();
        
        try {
            writeLock.lock();
            
            final Map<String,Map<String,MutableStorageRecord<?>>> contextMap;
            try {
                contextMap = getContextMap();
            } catch (final Exception e) {
                throw new IOException(e);
            }

            final Map<String, MutableStorageRecord<?>> dataMap = contextMap.get(context);
            if (dataMap != null) {    
                setDirty();
                final long now = System.currentTimeMillis();
                for (final MutableStorageRecord<?> record : dataMap.values()) {
                    if (record.isValid(now)) {
                        record.setExpiration(expiration);
                    }
                }
                log.debug("Updated expiration of valid records in context '{}' to '{}'", context, expiration);
            }
        } finally {
            writeLock.unlock();
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void deleteContext(@Nonnull @NotEmpty final String context) throws IOException {
        
        final Lock writeLock = getLock().writeLock();
        
        try {
            writeLock.lock();
            setDirty();
            try {
                getContextMap().remove(context);
            } catch (final Exception e) {
                throw new IOException(e);
            }
        } finally {
            writeLock.unlock();
        }
        
        log.debug("Deleted context '{}'", context);
    }

    /** {@inheritDoc} */
    @Override
    public void reap(@Nonnull @NotEmpty final String context) throws IOException {

        final Lock writeLock = getLock().writeLock();
        
        try {
            writeLock.lock();
            
            final Map<String,Map<String,MutableStorageRecord<?>>> contextMap;
            
            try {
                contextMap = getContextMap();
            } catch (final Exception e) {
                throw new IOException(e);
            }
            
            final Map<String, MutableStorageRecord<?>> dataMap = contextMap.get(context);
            if (dataMap != null) {
                if (reapWithLock(dataMap, System.currentTimeMillis())) {
                    setDirty();
                    if (dataMap.isEmpty()) {
                        contextMap.remove(context);
                    }
                }
            }
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /** {@inheritDoc} */
    @Nonnull public Iterable<String> getContextKeys(@Nonnull @NotEmpty final String context, @Nullable final String prefix)
            throws IOException {
        final Lock readLock = getLock().readLock();
        
        try {
            readLock.lock();
            
            final Map<String,Map<String,MutableStorageRecord<?>>> contextMap;
            
            try {
                contextMap = getContextMap();
            } catch (final Exception e) {
                throw new IOException(e);
            }
            
            final Map<String, MutableStorageRecord<?>> dataMap = contextMap.get(context);
            if (dataMap == null) {
                log.debug("Read failed, context '{}' not found", context);
                return CollectionSupport.emptyList();
            }
            
            final long now = System.currentTimeMillis();         
            return dataMap.entrySet().stream()
                    .filter(e -> e.getValue().isValid(now) && (prefix != null ? e.getKey().startsWith(prefix) : true))
                    .map(Map.Entry::getKey)
                    .collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableList())).get();
            
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Get the shared lock to synchronize access.
     * 
     * @return shared lock
     */
    @Nonnull protected abstract ReadWriteLock getLock();

    /**
     * Get the map of contexts to manipulate during operations.
     * 
     * <p>This method is guaranteed to be called under cover the lock returned by {{@link #getLock()}.</p>
     * 
     * @return map of contexts to manipulate
     * 
     * @throws IOException to signal errors
     */
    @Nonnull @Live protected abstract Map<String, Map<String, MutableStorageRecord<?>>> getContextMap()
            throws IOException;

    /**
     * A callback to indicate that data has been modified.
     * 
     * <p>This method is guaranteed to be called under cover the lock returned by {{@link #getLock()}.</p>
     * 
     * @throws IOException to signal an error
     */
    protected void setDirty() throws IOException {
        
    }
    
    /**
     * Internal method to implement read functions.
     *
     * @param <T>           type of object 
     * @param context       a storage context label
     * @param key           a key unique to context
     * @param version       only return record if newer than optionally supplied version
     * 
     * @return  a pair consisting of the version of the record read back, if any, and the record itself
     * @throws IOException  if errors occur in the read process 
     */
    @SuppressWarnings("unchecked")
    @Nonnull protected <T> Pair<Long,StorageRecord<T>> readImpl(@Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nullable final Long version) throws IOException {

        final Lock readLock = getLock().readLock();
        try {
            readLock.lock();
            
            final Map<String,Map<String,MutableStorageRecord<?>>> contextMap;
            
            try {
                contextMap = getContextMap();
            } catch (final Exception e) {
                throw new IOException(e);
            }
            
            final Map<String, MutableStorageRecord<?>> dataMap = contextMap.get(context);
            if (dataMap == null) {
                log.debug("Read failed, context '{}' not found", context);
                return new Pair<>();
            }

            final StorageRecord<?> record = dataMap.get(key);
            if (record == null) {
                log.debug("Read failed, key '{}' not found in context '{}'", key, context);
                return new Pair<>();
            }
            
            if (record.isExpired(System.currentTimeMillis())) {
                log.debug("Read failed, key '{}' expired in context '{}'", key, context);
                return new Pair<>();
            }
            
            if (version != null && record.getVersion() == version) {
                // Nothing's changed, so just echo back the version.
                return new Pair<>(version, null);
            }
            
            return new Pair<>(record.getVersion(), (StorageRecord<T>) record);
            
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Internal method to implement update functions.
     * 
     * @param version       only update if the current version matches this value
     * @param context       a storage context label
     * @param key           a key unique to context
     * @param value         updated value
     * @param expiration    expiration for record. or null
     * 
     * @return the version of the record after update, null if no record exists
     * @throws IOException  if errors occur in the update process
     * @throws VersionMismatchException if the record has already been updated to a newer version
     */
    @Nullable protected Long updateImpl(@Nullable final Long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nullable final String value, @Nullable final Long expiration)
                    throws IOException, VersionMismatchException {

        final Lock writeLock = getLock().writeLock();
        
        try {
            writeLock.lock();
            
            final Map<String,Map<String,MutableStorageRecord<?>>> contextMap;
            try {
                contextMap = getContextMap();
            } catch (final Exception e) {
                throw new IOException(e);
            }

            final Map<String, MutableStorageRecord<?>> dataMap = contextMap.get(context);
            if (dataMap == null) {
                log.debug("Update failed, context '{}' not found", context);
                return null;
            }
            
            final MutableStorageRecord<?> record = dataMap.get(key);
            if (record == null) {
                log.debug("Update failed, key '{}' not found in context '{}'", key, context);
                return null;
            }
            
            if (record.isExpired(System.currentTimeMillis())) {
                log.debug("Update failed, key '{}' expired in context '{}'", key, context);
                return null;
            }
    
            if (version != null && version != record.getVersion()) {
                // Caller is out of sync.
                throw new VersionMismatchException();
            }
    
            setDirty();
            
            if (value != null) {
                record.setValue(value);
                record.incrementVersion();
            }
    
            record.setExpiration(expiration);
    
            log.trace("Updated record '{}' in context '{}' with expiration '{}'",
                    new Object[] { key, context, expiration });

            return record.getVersion();
            
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Internal method to implement delete functions.
     * 
     * @param version       only update if the current version matches this value
     * @param context       a storage context label
     * @param key           a key unique to context
     * 
     * @return true iff the record existed and was deleted
     * @throws IOException  if errors occur in the update process
     * @throws VersionMismatchException if the record has already been updated to a newer version
     */
    protected boolean deleteImpl(@Nullable @Positive final Long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key) throws IOException, VersionMismatchException {

        final Lock writeLock = getLock().writeLock();
        
        try {
            writeLock.lock();

            final Map<String,Map<String,MutableStorageRecord<?>>> contextMap;
            try {
                contextMap = getContextMap();
            } catch (final Exception e) {
                throw new IOException(e);
            }
            
            final Map<String, MutableStorageRecord<?>> dataMap = contextMap.get(context);
            if (dataMap == null) {
                log.debug("Deleting record '{}' in context '{}'....context not found", key, context);
                return false;
            }

            final MutableStorageRecord<?> record = dataMap.get(key);
            if (record == null) {
                log.debug("Deleting record '{}' in context '{}'....key not found", key, context);
                return false;
            } else if (version != null && record.getVersion() != version) {
                throw new VersionMismatchException();
            } else {
                setDirty();
                dataMap.remove(key);
                log.trace("Deleted record '{}' in context '{}'", key, context);
                if (dataMap.isEmpty()) {
                    contextMap.remove(context);
                }
                return true;
            }
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * Locates and removes expired records from the input map.
     * 
     * <p>This method <strong>MUST</strong> be called while holding a write lock, if locking is required.</p>
     * 
     * @param dataMap       the map to reap
     * @param expiration    time at which to consider records expired
     * 
     * @return  true iff anything was purged
     */
    protected boolean reapWithLock(@Nonnull final Map<String, MutableStorageRecord<?>> dataMap, final long expiration) {
        return dataMap.entrySet().removeIf(e -> e.getValue().isExpired(expiration));
    }
    
}