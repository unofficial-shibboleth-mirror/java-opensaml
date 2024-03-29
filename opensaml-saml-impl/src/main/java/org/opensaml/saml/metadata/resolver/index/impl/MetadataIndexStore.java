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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import org.opensaml.saml.metadata.resolver.index.MetadataIndexKey;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;

/**
 * Component which stores indexed instances of a particular type of data, 
 * for example {@link org.opensaml.saml.saml2.metadata.EntityDescriptor},
 * under one or more instances of {@link MetadataIndexKey}.
 * 
 * @param <T> the type of data being indexed
 */
public class MetadataIndexStore<T> {
    
    /** The indexed storage of data. */
    @Nonnull private final Map<MetadataIndexKey, Set<T>> index;
    
    /**
     * Constructor.
     */
    public MetadataIndexStore() {
        index = new ConcurrentHashMap<>();
    }
    
    /**
     * Get the set of all {@link MetadataIndexKey} instances currently indexed.
     * 
     * @return the set of all currently indexed keys
     */
    @SuppressWarnings("null")
    @Nonnull @Unmodifiable @NotLive public Set<MetadataIndexKey> getKeys() {
        return CollectionSupport.copyToSet(index.keySet());
    }
    
    /**
     * Lookup the instances of data indexed under the supplied {@link MetadataIndexKey}.
     * 
     * @param key the index key to lookup
     * @return the set of data items indexed under that key
     */
    @Nonnull @Unmodifiable @NotLive public Set<T> lookup(@Nonnull final MetadataIndexKey key) {
        Constraint.isNotNull(key, "IndexKey was null");
        final Set<T> items = index.get(key);
        if (items == null) {
            return CollectionSupport.emptySet();
        }
        return CollectionSupport.copyToSet(items);
    }
    
    /**
     * Add the supplied data item to the index under the supplied {@link MetadataIndexKey}.
     * 
     * @param key the index key
     * @param item the data item to index
     */
    public void add(@Nonnull final MetadataIndexKey key, @Nonnull final T item) {
        Constraint.isNotNull(key, "IndexKey was null");
        Constraint.isNotNull(item, "The indexed data element was null");
        Set<T> items = index.get(key);
        if (items == null) {
            items = new HashSet<>();
            index.put(key, items);
        }
        items.add(item);
    }
    
    /**
     * Remove the supplied data item from the index under the supplied {@link MetadataIndexKey}.
     * 
     * @param key the index key
     * @param item the data item to index
     */
    public void remove(@Nonnull final MetadataIndexKey key, @Nonnull final T item) {
        Constraint.isNotNull(key, "IndexKey was null");
        Constraint.isNotNull(item, "The indexed data element was null");
        final Set<T> items = index.get(key);
        if (items == null) {
            return;
        }
        items.remove(item);
    }
    
    /**
     * Clear all data items indexed under the supplied {@link MetadataIndexKey}.
     * 
     * @param key the index key
     */
    public void clear(@Nonnull final MetadataIndexKey key) {
        Constraint.isNotNull(key, "IndexKey was null");
        index.remove(key);
    }
    
    /**
     * Clear all indexed data items from the store.
     */
    public void clear() {
        index.clear();
    }

}