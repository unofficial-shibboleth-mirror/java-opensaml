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

package org.opensaml.saml.metadata.resolver.index.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.metadata.resolver.index.MetadataIndex;
import org.opensaml.saml.metadata.resolver.index.MetadataIndexKey;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.collection.LazySet;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.resolver.CriteriaSet;

/**
 * High-level component which handles index and lookup of instances of particular type of data item,
 * for example {@link org.opensaml.saml.saml2.metadata.EntityDescriptor},
 * based on a set of {@link MetadataIndex} instances currently held.
 * 
 * @param <T> the type of data being indexed
 */
public class MetadataIndexManager<T> {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(MetadataIndexManager.class);
    
    /** Storage for secondary indexes. */
    @Nonnull private final Map<MetadataIndex, MetadataIndexStore<T>> indexes;
    
    /** Function to extract the data item to be indexed from an EntityDescriptor. */
    @Nonnull private final Function<EntityDescriptor,T> entityDescriptorFunction;
    
    /**
     * Constructor.
     *
     * @param initIndexes indexes for which to initialize storage
     * @param extractionFunction function to extract the indexed data item from an EntityDescriptor
     */
    public MetadataIndexManager(@Nullable final Set<MetadataIndex> initIndexes,
            @Nonnull final Function<EntityDescriptor, T> extractionFunction) {
        
        entityDescriptorFunction = Constraint.isNotNull(extractionFunction, 
                "EntityDescriptor extraction function was null");
        
        indexes = new ConcurrentHashMap<>();
        if (initIndexes != null) {
            for (final MetadataIndex index : initIndexes) {
                log.trace("Initializing manager for index: {}", index);
                indexes.put(index, new MetadataIndexStore<T>());
            }
        }
    }
    
    /**
     * Get the set of all {@link MetadataIndex} instances currently initialized.
     * 
     * @return the set of all current indexes
     */
    @SuppressWarnings("null")
    @Nonnull @Unmodifiable @NotLive public Set<MetadataIndex> getIndexes() {
        return CollectionSupport.copyToSet(indexes.keySet());
    }
    
    /**
     * Get the {@link MetadataIndexStore} for the specified {@link MetadataIndex}.
     * 
     * @param index the index for which the store is desired
     * @return the index store for the index, may be null if index was not initialized 
     *         for this manager instance
     */
    @Nullable protected MetadataIndexStore<T> getStore(@Nonnull final MetadataIndex index) {
        Constraint.isNotNull(index, "MetadataIndex was null");
        return indexes.get(index);
    }
    
    /**
     * Resolve the set of indexed data items based on the indexes currently held.
     * 
     * @param criteria the criteria set to process
     * 
     * @return an {@link Optional} instance containing the indexed data items resolved via indexes, 
     *          and based on the input criteria set. If the Optional instance indicates 'absent',
     *          there were either no indexes configured, or no criteria were applicable/understood
     *          by any indexes.  If 'present' is indicated, then there were applicable/understood criteria,
     *          and the wrapped set contains the indexed data, which may be empty.
     */
    @Nonnull public Optional<Set<T>> lookupIndexedItems(@Nullable final CriteriaSet criteria) {
        final Set<T> items = new HashSet<>();
        for (final MetadataIndex index : indexes.keySet()) {
            final Set<MetadataIndexKey> keys = index.generateKeys(criteria);
            if (keys != null && !keys.isEmpty()) {
                final LazySet<T> indexResult = new LazySet<>();
                final MetadataIndexStore<T> indexStore = getStore(index);
                if (indexStore != null) {
                    for (final MetadataIndexKey key : keys) {
                        indexResult.addAll(indexStore.lookup(key));
                    }
                }
                log.trace("MetadataIndex '{}' produced results: {}", index, indexResult);
                if (items.isEmpty()) {
                    items.addAll(indexResult);
                } else {
                    items.retainAll(indexResult);
                }
                if (items.isEmpty()) {
                    log.trace("Accumulator intersected with MetadataIndex '{}' result produced empty result, " 
                            + "terminating early and returning empty result set", index);
                    // Return present+empty here to indicate there were applicable indexes for the criteria,
                    // but no indexed data.
                    return Optional.of(CollectionSupport.emptySet());
                }
            }
        }
        
        if (items.isEmpty()) {
            // Because of the handling above, if we reach here it was because either:
            //   1) no indexes are configured
            //   2) no criteria was supplied applicable for any indexes 
            //      (i.e. no MetadataIndexKeys were generated for any criteria)
            // Returning absent here allows to distinguish these cases from the empty set case above.
            return Optional.empty();
        }
        
        return Optional.of(items);
    }
    
    /**
     * Index the specified {@link EntityDescriptor} based on the indexes currently held.
     * 
     * @param descriptor the entity descriptor to index
     */
    public void indexEntityDescriptor(@Nonnull final EntityDescriptor descriptor) {
        final T item = entityDescriptorFunction.apply(descriptor);
        if (item != null) {
            for (final MetadataIndex index : indexes.keySet()) {
                final Set<MetadataIndexKey> keys = index.generateKeys(descriptor);
                if (keys != null && !keys.isEmpty()) {
                    final MetadataIndexStore<T> store = getStore(index);
                    if (store != null) {
                        for (final MetadataIndexKey key : keys) {
                            log.trace("Indexing metadata: index '{}', key '{}', data item '{}'", 
                                    index, key, item);
                            store.add(key, item);
                        }
                    }
                }
            }
        } else {
            log.trace("Unable to extract indexed data item from EntityDescriptor");
        }
    }
    
    /**
     * Remove from the index the specified {@link EntityDescriptor} based on the indexes currently held.
     * 
     * @param descriptor the entity descriptor to index
     */
    public void deindexEntityDescriptor(@Nonnull final EntityDescriptor descriptor) {
        final T item = entityDescriptorFunction.apply(descriptor);
        if (item != null) {
            for (final MetadataIndex index : indexes.keySet()) {
                final Set<MetadataIndexKey> keys = index.generateKeys(descriptor);
                if (keys != null && !keys.isEmpty()) {
                    final MetadataIndexStore<T> store = getStore(index);
                    if (store != null) {
                        for (final MetadataIndexKey key : keys) {
                            log.trace("De-indexing metadata: index '{}', key '{}', data item '{}'", 
                                    index, key, item);
                            store.remove(key, item);
                        }
                    }
                }
            }
        } else {
            log.trace("Unable to extract indexed data item from EntityDescriptor");
        }
    }
    
    
    /** Extraction function which simply returns the input {@link EntityDescriptor}. */
    public static class IdentityExtractionFunction implements Function<EntityDescriptor, EntityDescriptor> {

        /** {@inheritDoc} */
        public EntityDescriptor apply(final EntityDescriptor input) {
            return input;
        }
        
    }
    
    /** Extraction function which returns the entityID of the input {@link EntityDescriptor}. */
    public static class EntityIDExtractionFunction implements Function<EntityDescriptor, String> {

        /** {@inheritDoc} */
        public String apply(final EntityDescriptor input) {
            if (input == null) {
                return null;
            }
            return StringSupport.trimOrNull(input.getEntityID());
        }
        
    }

}