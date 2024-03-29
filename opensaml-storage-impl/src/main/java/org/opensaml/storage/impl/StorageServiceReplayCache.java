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

package org.opensaml.storage.impl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import javax.annotation.Nonnull;

import org.opensaml.storage.ReplayCache;
import org.opensaml.storage.StorageCapabilities;
import org.opensaml.storage.StorageRecord;
import org.opensaml.storage.StorageService;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.ThreadSafeAfterInit;
import net.shibboleth.shared.codec.StringDigester;
import net.shibboleth.shared.codec.StringDigester.OutputFormat;
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * {@link ReplayCache} implementation backed by a {@link StorageService}.
 * 
 * <p>This class is thread-safe and uses a synchronized method to prevent race conditions within the underlying
 * store (lacking an atomic "check and insert" operation).</p>
 * 
 * @since 5.0.0
 */
@ThreadSafeAfterInit
public class StorageServiceReplayCache extends AbstractIdentifiableInitializableComponent implements ReplayCache {

    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(StorageServiceReplayCache.class);

    /** Backing storage for the replay cache. */
    @NonnullAfterInit private StorageService storage;

    /** Digester if key is too long. */
    @NonnullAfterInit private StringDigester digester;
    
    /** Flag controlling behavior on storage failure. */
    private boolean strict;
    
    /**
     * Get the backing store for the cache.
     * 
     * @return the backing store.
     */
    @NonnullAfterInit public StorageService getStorage() {
        return storage;
    }
    
    /**
     * Set the backing store for the cache.
     * 
     * @param storageService backing store to use
     */
    public void setStorage(@Nonnull final StorageService storageService) {
        checkSetterPreconditions();
        
        storage = Constraint.isNotNull(storageService, "StorageService cannot be null");
        Constraint.isTrue(storage.getCapabilities().isServerSide(), "StorageService cannot be client-side");
    }
    
    /**
     * Get the strictness flag.
     * 
     * @return true iff we should treat storage failures as a replay
     */
    public boolean isStrict() {
        return strict;
    }

    /**
     * Set the strictness flag.
     * 
     * @param flag true iff we should treat storage failures as a replay
     */
    public void setStrict(final boolean flag) {
        checkSetterPreconditions();
        
        strict = flag;
    }


    /** {@inheritDoc} */
    @Override
    public void doInitialize() throws ComponentInitializationException {
        if (storage == null) {
            throw new ComponentInitializationException("StorageService cannot be null");
        }

        try {
            digester = new StringDigester("SHA", OutputFormat.HEX_LOWER);
        } catch (final NoSuchAlgorithmException e) {
            throw new ComponentInitializationException(e);
        }
    }

    /** {@inheritDoc} */
    public synchronized boolean check(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String s,
            @Nonnull final Instant expires) {

        final String key;
        
        final StorageCapabilities caps = storage.getCapabilities();
        if (context.length() > caps.getContextSize()) {
            log.error("Context '{}' too long for StorageService (limit {})", context, caps.getContextSize());
            return false;
        } else if (s.length() > caps.getKeySize()) {
            key = digester.apply(s);
            if (key == null) {
                log.error("Result of digesting key was null");
                return false;
            }
        } else {
            key = s;
        }

        try {
            final StorageRecord<?> entry = storage.read(context, key);
            if (entry == null) {
                log.debug("Value '{}' was not a replay, adding to cache with expiration time {}", s, expires);
                storage.create(context, key, "x", expires.toEpochMilli());
                return true;
            }

            final Long existingExp = entry.getExpiration();
            log.debug("Replay of value '{}' detected in cache, expires at {}", s,
                    Instant.ofEpochMilli(existingExp != null ? existingExp : 0));
            return false;
            
        } catch (final IOException e) {
            log.error("Exception reading/writing to storage service, returning {}", strict ? "failure" : "success", e);
            return !strict;
        }
    }

}