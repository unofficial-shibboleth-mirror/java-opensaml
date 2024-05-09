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

package org.opensaml.saml.metadata.resolver.impl;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.metadata.resolver.ClearableMetadataResolver;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.RefreshableMetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/**
 * A {@link MetadataResolver} implementation that answers requests by composing the answers of child
 * {@link MetadataResolver}s.
 * 
 * <p>Note: This appears to be unused, and contained a number of bugs that may or may not have been
 * fully corrected.</p>
 */
public class CompositeMetadataResolver extends AbstractIdentifiableInitializableComponent implements MetadataResolver,
        RefreshableMetadataResolver, ClearableMetadataResolver {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(CompositeMetadataResolver.class);

    /** Resolver type. */
    @Nullable @NotEmpty private String resolverType;

    /** Resolvers composed by this resolver. */
    @Nonnull private List<MetadataResolver> resolvers;

    /** Constructor. */
    public CompositeMetadataResolver() {
        resolvers = CollectionSupport.emptyList();
    }

    /**
     * Gets an immutable the list of currently registered resolvers.
     * 
     * @return list of currently registered resolvers
     */
    @Nonnull @Unmodifiable @NotLive public List<MetadataResolver> getResolvers() {
        return resolvers;
    }

    /**
     * Sets the current set of metadata resolvers.
     * 
     * @param newResolvers the metadata resolvers to use
     * 
     * @throws ResolverException thrown if there is a problem adding the metadata provider
     */
    public void setResolvers(@Nullable final List<MetadataResolver> newResolvers) 
            throws ResolverException {
        checkSetterPreconditions();

        if (newResolvers == null || newResolvers.isEmpty()) {
            resolvers = CollectionSupport.emptyList();
        } else {
            resolvers = CollectionSupport.copyToList(newResolvers);
        }
    }

    /** {@inheritDoc} */
    @Nullable @NotEmpty public String getType() {
        return resolverType;
    }
    
    /**
     * Sets the type of this resolver for reporting/logging.
     * 
     * @param type type to set
     * 
     * @since 5.0.0
     */
    public void setType(@Nullable @NotEmpty final String type) {
        resolverType = StringSupport.trimOrNull(type);    }
    
    /** {@inheritDoc} */
    public boolean isRequireValidMetadata() {
        log.warn("Attempt to access unsupported requireValidMetadata property on ChainingMetadataResolver");
        return false;
    }

    /** {@inheritDoc} */
    public void setRequireValidMetadata(final boolean requireValidMetadata) {
        throw new UnsupportedOperationException("Setting require valid metadata is not supported on chaining resolver");
    }

    /** {@inheritDoc} */
    @Nullable public MetadataFilter getMetadataFilter() {
        log.warn("Attempt to access unsupported MetadataFilter property on ChainingMetadataResolver");
        return null;
    }

    /** {@inheritDoc} */
    public void setMetadataFilter(@Nullable final MetadataFilter newFilter) {
        throw new UnsupportedOperationException("Metadata filters are not supported on ChainingMetadataProviders");
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull public Iterable<EntityDescriptor> resolve(@Nullable final CriteriaSet criteria) throws ResolverException {
        checkComponentActive();
        return new CompositeMetadataResolverIterable(resolvers, criteria);
    }

    /** {@inheritDoc} */
    public EntityDescriptor resolveSingle(@Nullable final CriteriaSet criteria) throws ResolverException {
        checkComponentActive();
        EntityDescriptor metadata = null;
        for (final MetadataResolver resolver : resolvers) {
            metadata = resolver.resolveSingle(criteria);
            if (metadata != null) {
                return metadata;
            }
        }

        return null;
    }
    
    /** {@inheritDoc} */
    public void clear() throws ResolverException {
        for (final MetadataResolver resolver : resolvers) {
            if (resolver instanceof ClearableMetadataResolver) {
                ((ClearableMetadataResolver) resolver).clear();
            }
        }
    }

    /** {@inheritDoc} */
    public void clear(@Nonnull final String entityID) throws ResolverException {
        for (final MetadataResolver resolver : resolvers) {
            if (resolver instanceof ClearableMetadataResolver) {
                ((ClearableMetadataResolver) resolver).clear(entityID);
            }
        }
    }

    /** {@inheritDoc} */
    public void refresh() throws ResolverException {
        for (final MetadataResolver resolver : resolvers) {
            if (resolver instanceof RefreshableMetadataResolver) {
                ((RefreshableMetadataResolver) resolver).refresh();
            }
        }
    }

    /** {@inheritDoc} */
    @Nullable public Instant getLastUpdate() {
        Instant ret = null;
        for (final MetadataResolver resolver : resolvers) {
            if (resolver instanceof RefreshableMetadataResolver) {
                final Instant lastUpdate = ((RefreshableMetadataResolver) resolver).getLastUpdate();
                if (ret == null || ret.isBefore(lastUpdate)) {
                    ret = lastUpdate;
                }
            }
        }
        
        return ret;
    }

    /** {@inheritDoc} */
    @Nullable public Instant getLastRefresh() {
        Instant ret = null;
        for (final MetadataResolver resolver : resolvers) {
            if (resolver instanceof RefreshableMetadataResolver) {
                final Instant lastRefresh = ((RefreshableMetadataResolver) resolver).getLastRefresh();
                if (ret == null || ret.isBefore(lastRefresh)) {
                    ret = lastRefresh;
                }
            }
        }
        
        return ret;
    }
    
    /** {@inheritDoc} */
    @Nullable public Instant getLastSuccessfulRefresh() {
        Instant ret = null;
        for (final MetadataResolver resolver : resolvers) {
            if (resolver instanceof RefreshableMetadataResolver) {
                final Instant lastSuccessRefresh = ((RefreshableMetadataResolver) resolver).getLastSuccessfulRefresh();
                if (ret == null || ret.isBefore(lastSuccessRefresh)) {
                    ret = lastSuccessRefresh;
                }
            }
        }
        
        return ret;
    }

    /** {@inheritDoc} */
    @Nullable public Boolean wasLastRefreshSuccess() {
        for (final MetadataResolver resolver : resolvers) {
            if (resolver instanceof RefreshableMetadataResolver) {
                final RefreshableMetadataResolver refreshable = (RefreshableMetadataResolver) resolver;
                final Boolean flag = refreshable.wasLastRefreshSuccess();
                if (flag != null && !flag) {
                    return false;
                }
            }
        }
        
        return true;
    }

    /** {@inheritDoc} */
    @Nullable public Throwable getLastFailureCause() {
        for (final MetadataResolver resolver : resolvers) {
            if (resolver instanceof RefreshableMetadataResolver) {
                final RefreshableMetadataResolver refreshable = (RefreshableMetadataResolver) resolver;
                if (refreshable.getLastFailureCause() != null) {
                    return refreshable.getLastFailureCause();
                }
            }
        }

        return null;
    }

    /**
     * {@link Iterable} implementation that provides an {@link Iterator} that lazily iterates over each composed
     * resolver.
     */
    private static class CompositeMetadataResolverIterable implements Iterable<EntityDescriptor> {

        /** Class logger. */
        @Nonnull private final Logger log = LoggerFactory.getLogger(CompositeMetadataResolverIterable.class);

        /** Resolvers over which to iterate. */
        @Nonnull private final List<MetadataResolver> resolvers;

        /** Criteria being search for. */
        @Nullable private final CriteriaSet criteria;

        /**
         * Constructor.
         * 
         * @param composedResolvers resolvers from which results will be pulled
         * @param metadataCritiera criteria for the resolver query
         */
        public CompositeMetadataResolverIterable(@Nonnull final List<MetadataResolver> composedResolvers,
                @Nullable final CriteriaSet metadataCritiera) {
            resolvers = CollectionSupport.copyToList(composedResolvers);

            criteria = metadataCritiera;
        }

        /** {@inheritDoc} */
        public Iterator<EntityDescriptor> iterator() {
            return new CompositeMetadataResolverIterator();
        }

        /** {@link Iterator} implementation that lazily iterates over each composed resolver. */
        private class CompositeMetadataResolverIterator implements Iterator<EntityDescriptor> {

            /** Iterator over the composed resolvers. */
            @Nonnull private Iterator<MetadataResolver> resolverIterator;

            /** Current resolver from which we are getting results. */
            private MetadataResolver currentResolver;

            /** Iterator over the results of the current resolver. */
            private Iterator<EntityDescriptor> currentResolverMetadataIterator;

            /** Constructor. */
            public CompositeMetadataResolverIterator() {
                resolverIterator = resolvers.iterator();
            }

            /** {@inheritDoc} */
            public boolean hasNext() {
                if (currentResolverMetadataIterator == null || !currentResolverMetadataIterator.hasNext()) {
                    proceedToNextResolverIterator();
                }

                return currentResolverMetadataIterator != null && currentResolverMetadataIterator.hasNext();
            }

            /** {@inheritDoc} */
            public EntityDescriptor next() {
                if (currentResolverMetadataIterator == null || !currentResolverMetadataIterator.hasNext()) {
                    proceedToNextResolverIterator();
                }
                
                if (currentResolverMetadataIterator != null) {
                    return currentResolverMetadataIterator.next();
                }
                
                throw new NoSuchElementException("No further elements");
            }

            /** {@inheritDoc} */
            public void remove() {
                throw new UnsupportedOperationException();
            }

            /**
             * Proceed to the next composed resolvers that has a response to the resolution query.
             */
            private void proceedToNextResolverIterator() {
                try {
                    while (resolverIterator.hasNext()) {
                        currentResolver = resolverIterator.next();
                        currentResolverMetadataIterator = currentResolver.resolve(criteria).iterator();
                        if (currentResolverMetadataIterator.hasNext()) {
                            return;
                        }
                    }
                } catch (final ResolverException e) {
                    log.debug("Error encountered attempting to fetch results from resolver", e);
                }
            }
        }
    }
    
}