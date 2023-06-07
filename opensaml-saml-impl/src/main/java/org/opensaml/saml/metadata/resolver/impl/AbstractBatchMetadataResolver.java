/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.metadata.resolver.impl;

import java.time.Instant;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.IterableMetadataSource;
import org.opensaml.saml.metadata.resolver.BatchMetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.index.MetadataIndex;
import org.opensaml.saml.metadata.resolver.index.impl.MetadataIndexManager;
import org.opensaml.saml.saml2.common.TimeBoundSAMLObject;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;

import com.google.common.collect.Iterables;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/**
 * Abstract subclass for metadata resolvers that process and resolve metadata at a given point 
 * in time from a single metadata source document.
 */
public abstract class AbstractBatchMetadataResolver extends AbstractMetadataResolver 
        implements BatchMetadataResolver, IterableMetadataSource {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractBatchMetadataResolver.class);
    
    /** Flag indicating whether to cache the original source metadata document. */
    private boolean cacheSourceMetadata;
    
    /** The set of indexes configured. */
    @Nonnull private Set<MetadataIndex> indexes;
    
    /** Flag indicating whether resolution may be performed solely by applying predicates to the
     * entire metadata collection. Defaults to false. */
    private boolean resolveViaPredicatesOnly;
    
    /** Constructor. */
    public AbstractBatchMetadataResolver() {
        indexes = CollectionSupport.emptySet();
        
        setCacheSourceMetadata(true);
    }
    
    /** {@inheritDoc} */
    @Override public Iterator<EntityDescriptor> iterator() {
        checkComponentActive();
        return CollectionSupport.copyToList(ensureBackingStore().getOrderedDescriptors()).iterator();
    }

    /**
     * Get whether to cache the original source metadata document.
     * 
     * @return true if source should be cached, false otherwise
     */
    protected boolean isCacheSourceMetadata() {
        return cacheSourceMetadata;
    }
    
    /**
     * Set whether to cache the original source metadata document.
     * 
     * @param flag true if source should be cached, false otherwise
     */
    protected void setCacheSourceMetadata(final boolean flag) {
        checkSetterPreconditions();
        cacheSourceMetadata = flag; 
    }
    
    /**
     * Get the configured indexes.
     * 
     * @return the set of configured indexes
     */
    @Nonnull @Unmodifiable @NotLive public Set<MetadataIndex> getIndexes() {
        return indexes;
    }

    /**
     * Set the configured indexes.
     * 
     * @param newIndexes the new indexes to set
     */
    public void setIndexes(@Nullable final Set<MetadataIndex> newIndexes) {
        checkSetterPreconditions();
        if (newIndexes == null) {
            indexes = CollectionSupport.emptySet();
        } else {
            indexes = CollectionSupport.copyToSet(newIndexes);
        }
    }

    /**
     * Get the flag indicating whether resolution may be performed solely 
     * by applying predicates to the entire metadata collection.
     * 
     * @return true if resolution may be attempted solely via predicates, false if not
     */
    public boolean isResolveViaPredicatesOnly() {
        return resolveViaPredicatesOnly;
    }

    /**
     * Set the flag indicating whether resolution may be performed solely 
     * by applying predicates to the entire metadata collection.
     * 
     * @param flag true if resolution may be attempted solely via predicates, false if not
     */
    public void setResolveViaPredicatesOnly(final boolean flag) {
        checkSetterPreconditions();
        resolveViaPredicatesOnly = flag;
    }

    /** {@inheritDoc} */
    @Nullable public Instant getRootValidUntil() {
        final XMLObject cached = ensureBackingStore().getCachedOriginalMetadata();
        if (cached != null && cached instanceof TimeBoundSAMLObject) {
            return ((TimeBoundSAMLObject)cached).getValidUntil();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Nullable public Boolean isRootValid() {
        final XMLObject cached = ensureBackingStore().getCachedOriginalMetadata();
        if (cached == null) {
            return null;
        }
        return isValid(cached);
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull protected Iterable<EntityDescriptor> doResolve(@Nullable final CriteriaSet criteria)
            throws ResolverException {
        checkComponentActive();
        
        final EntityIdCriterion entityIdCriterion = criteria != null ? criteria.get(EntityIdCriterion.class) : null;
        if (entityIdCriterion != null) {
            final Iterable<EntityDescriptor> entityIdcandidates = lookupEntityID(entityIdCriterion.getEntityId());
            if (log.isDebugEnabled()) {
                log.debug("{} Resolved {} candidates via EntityIdCriterion: {}", 
                        getLogPrefix(), Iterables.size(entityIdcandidates), entityIdCriterion);
            }
            return predicateFilterCandidates(entityIdcandidates, criteria, false);
        }
        
        final Optional<Set<EntityDescriptor>> indexedCandidates = lookupByIndexes(criteria);
        if (log.isDebugEnabled()) {
            if (indexedCandidates.isPresent()) {
                log.debug("{} Resolved {} candidates via secondary index lookup", 
                        getLogPrefix(), Iterables.size(indexedCandidates.get()));
            } else {
                log.debug("{} Resolved no candidates via secondary index lookup (Optional indicated result was absent)",
                        getLogPrefix());
            }
        }
        
        if (indexedCandidates.isPresent()) {
            log.debug("{} Performing predicate filtering of resolved secondary indexed candidates", getLogPrefix());
            return predicateFilterCandidates(indexedCandidates.get(), criteria, false);
        } else if (isResolveViaPredicatesOnly()) {
            log.debug("{} Performing predicate filtering of entire metadata collection", getLogPrefix());
            return predicateFilterCandidates(this, criteria, true);
        } else {
            log.debug("{} Resolved no secondary indexed candidates, returning empty result", getLogPrefix());
            return CollectionSupport.emptySet();
        }
        
    }
    
    /**
     * Resolve the set up descriptors based on the indexes currently held.
     * 
     * @param criteria the criteria set to process
     * 
     * @return an {@link Optional} instance containing the descriptors resolved via indexes, 
     *          and based on the input criteria set. If the Optional instance indicates 'absent',
     *          there were either no indexes configured, or no criteria were applicable/understood
     *          by any indexes.  If 'present' is indicated, then there were applicable/understood criteria,
     *          and the wrapped set contains the indexed data, which may be empty.
     */
    @Nonnull protected Optional<Set<EntityDescriptor>> lookupByIndexes(@Nullable final CriteriaSet criteria) {
        return ensureBackingStore().getSecondaryIndexManager().lookupIndexedItems(criteria);
    }
    
    /** {@inheritDoc} */
    @Override protected void indexEntityDescriptor(@Nonnull final EntityDescriptor entityDescriptor, 
            @Nonnull final EntityBackingStore backingStore) {
        super.indexEntityDescriptor(entityDescriptor, backingStore);
        
        ((BatchEntityBackingStore)backingStore).getSecondaryIndexManager().indexEntityDescriptor(entityDescriptor);
    }

    /** {@inheritDoc} */
    @Override @Nonnull protected BatchEntityBackingStore createNewBackingStore() {
        return new BatchEntityBackingStore(getIndexes());
    }
    
    /** {@inheritDoc} */
    @Override @Nonnull protected BatchEntityBackingStore ensureBackingStore() {
        return (BatchEntityBackingStore) super.ensureBackingStore();
    }
    
    /** {@inheritDoc} */
    @Override protected void initMetadataResolver() throws ComponentInitializationException {
        super.initMetadataResolver();
        // Init this to an empty instance to ensure we always have a non-null instance,
        // even if initialization in the subclass fails for whatever reason.
        // Most subclasses will replace this with a new populated instance.
        setBackingStore(createNewBackingStore());
    }

    /**
     * Convenience method for getting the current effective cached original metadata.
     * 
     * <p>
     * Note: may or may not be the same as that obtained from {@link #getCachedFilteredMetadata()},
     * depending on what metadata filtering produced from the original metadata document.
     * </p>
     * 
     * @return the current effective cached metadata document
     */
    @Nullable protected XMLObject getCachedOriginalMetadata() {
       return ensureBackingStore().getCachedOriginalMetadata(); 
    }
    
    /**
     * Convenience method for getting the current effective cached filtered metadata.
     * 
     * <p>
     * Note: may or may not be the same as that obtained from {@link #getCachedOriginalMetadata()},
     * depending on what metadata filtering produced from the original metadata document.
     * </p>
     * 
     * @return the current effective cached metadata document
     */
    @Nullable protected XMLObject getCachedFilteredMetadata() {
       return ensureBackingStore().getCachedFilteredMetadata(); 
    }

    /**
     * Process the specified new metadata document, including metadata filtering 
     * and return its data in a new entity backing store instance.
     * 
     * @param root the root of the new metadata document being processed
     * 
     * @return the new backing store instance
     * 
     * @throws FilterException if there is a problem filtering the metadata
     */
    @Nonnull protected BatchEntityBackingStore preProcessNewMetadata(@Nonnull final XMLObject root) 
            throws FilterException {
        
        final BatchEntityBackingStore newBackingStore = createNewBackingStore();
        
        final XMLObject filteredMetadata = filterMetadata(root);
        
        if (isCacheSourceMetadata()) {
            newBackingStore.setCachedOriginalMetadata(root);
            newBackingStore.setCachedFilteredMetadata(filteredMetadata);
        } 
        
        if (filteredMetadata == null) {
            log.info("{} Metadata filtering process produced a null document, resulting in an empty data set", 
                    getLogPrefix());
            return newBackingStore;
        }
        
        if (filteredMetadata instanceof EntityDescriptor) {
            preProcessEntityDescriptor((EntityDescriptor)filteredMetadata, newBackingStore);
        } else if (filteredMetadata instanceof EntitiesDescriptor) {
            preProcessEntitiesDescriptor((EntitiesDescriptor)filteredMetadata, newBackingStore);
        } else {
            log.warn("{} Document root was neither an EntityDescriptor nor an EntitiesDescriptor: {}", 
                    getLogPrefix(), root.getClass().getName());
        }
        
        return newBackingStore;
    }

    /**
     * Specialized entity backing store implementation for batch metadata resolvers.
     * 
     * <p>
     * Adds the following to parent impl:
     * </p>
     * <ol>
     * <li>capable of storing the original metadata document on which the backing store is based</li>
     * <li>stores data for any secondary indexes defined</li>
     * </ol>
     */
    protected class BatchEntityBackingStore extends EntityBackingStore {
        
        /** The cached original source metadata document. */
        @Nullable private XMLObject cachedOriginalMetadata;
        
        /** The cached original source metadata document. */
        @Nullable private XMLObject cachedFilteredMetadata;
        
        /** Manager for secondary indexes. */
        @Nonnull private final MetadataIndexManager<EntityDescriptor> secondaryIndexManager;
        
        /**
         * Constructor.
         *
         * @param initIndexes secondary indexes for which to initialize storage
         */
        protected BatchEntityBackingStore(@Nullable final Set<MetadataIndex> initIndexes) {
            secondaryIndexManager =
                    new MetadataIndexManager<>(initIndexes, new MetadataIndexManager.IdentityExtractionFunction());
        }

        /**
         * Get the cached original source metadata.
         * 
         * @return the cached metadata
         */
        @Nullable public XMLObject getCachedOriginalMetadata() {
            return cachedOriginalMetadata;
        }

        /**
         * Get the cached original source metadata, raising an {@link IllegalStateException} if null.
         * 
         * @return the cached metadata
         * 
         * @since 5.0.0
         */
        @Nonnull public XMLObject ensureCachedOriginalMetadata() {
            if (cachedOriginalMetadata != null) {
                return cachedOriginalMetadata;
            }
            throw new IllegalStateException("Original metadata was absent.");
        }

        /**
         * Set the cached original source metadata.
         * 
         * @param metadata The new cached metadata
         */
        public void setCachedOriginalMetadata(@Nullable final XMLObject metadata) {
            cachedOriginalMetadata = metadata;
        }
        
        /**
         * Get the cached filtered source metadata.
         * 
         * @return the cached metadata
         */
        @Nullable public XMLObject getCachedFilteredMetadata() {
            return cachedFilteredMetadata;
        }

        /**
         * Set the cached filtered source metadata.
         * 
         * @param metadata The new cached metadata
         */
        public void setCachedFilteredMetadata(@Nullable final XMLObject metadata) {
            cachedFilteredMetadata = metadata;
        }
        
        /**
         * Get the secondary index manager.
         * 
         * @return the manager for secondary indexes
         */
        @Nonnull public MetadataIndexManager<EntityDescriptor> getSecondaryIndexManager() {
            return secondaryIndexManager;
        }
    }

}