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

package org.opensaml.saml.metadata.resolver;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.criterion.ProfileRequestContextCriterion;
import org.opensaml.saml.metadata.criteria.entity.DetectDuplicateEntityIDsCriterion;
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
 * A metadata provider that uses registered resolvers, in turn, to answer queries.
 * 
 * The Iterable of entity descriptors returned is the first non-null and non-empty Iterable found while iterating over
 * the registered resolvers in resolver list order.
 */
public class ChainingMetadataResolver extends AbstractIdentifiableInitializableComponent implements MetadataResolver,
        RefreshableMetadataResolver, ClearableMetadataResolver {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ChainingMetadataResolver.class);

    /** Resolver type. */
    @Nullable @NotEmpty private String resolverType;
    
    /** Registered resolvers. */
    @Nonnull private List<MetadataResolver> resolvers;
    
    /** Strategy for detecting duplicate entityIDs across resolvers. */
    @Nonnull private DetectDuplicateEntityIDs detectDuplicateEntityIDs;

    /** Activation condition. */
    @Nullable private Predicate<ProfileRequestContext> activationCondition;
    
    /** Constructor. */
    public ChainingMetadataResolver() {
        resolvers = CollectionSupport.emptyList();
        detectDuplicateEntityIDs = DetectDuplicateEntityIDs.Off;
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
        resolverType = StringSupport.trimOrNull(type);
    }

    /**
     * Get an immutable the list of currently registered resolvers.
     * 
     * @return list of currently registered resolvers
     */
    @Nonnull @Unmodifiable @NotLive public List<MetadataResolver> getResolvers() {
        return resolvers;
    }

    /**
     * Set the registered metadata resolvers.
     * 
     * @param newResolvers the metadata resolvers to use
     * 
     * @throws ResolverException thrown if there is a problem adding the metadata resolvers
     */
    public void setResolvers(@Nonnull final List<? extends MetadataResolver> newResolvers) throws ResolverException {
        checkSetterPreconditions();

        if (newResolvers == null || newResolvers.isEmpty()) {
            resolvers = CollectionSupport.emptyList();
            return;
        }

        resolvers = CollectionSupport.copyToList(newResolvers);
    }

    /**
     * Get the strategy for detecting duplicate entityIDs across resolvers.
     * 
     * @return the configured strategy
     */
    @Nonnull public DetectDuplicateEntityIDs getDetectDuplicateEntityIDs() {
        return detectDuplicateEntityIDs;
    }

    /**
     * Set the strategy for detecting duplicate entityIDs across resolvers.
     * 
     * @param strategy the strategy to configure
     */
    public void setDetectDuplicateEntityIDs(@Nullable final DetectDuplicateEntityIDs strategy) {
        checkSetterPreconditions();
        detectDuplicateEntityIDs = strategy != null ? strategy : DetectDuplicateEntityIDs.Off;
    }

    /** {@inheritDoc} */
    @Override public boolean isRequireValidMetadata() {
        log.warn("Attempt to access unsupported requireValidMetadata property on ChainingMetadataResolver");
        return false;
    }

    /** {@inheritDoc} */
    @Override public void setRequireValidMetadata(final boolean requireValidMetadata) {
        throw new UnsupportedOperationException("Setting requireValidMetadata is not supported on chaining resolver");
    }

    /** {@inheritDoc} */
    @Nullable public MetadataFilter getMetadataFilter() {
        log.warn("Attempt to access unsupported MetadataFilter property on ChainingMetadataResolver");
        return null;
    }

    /** {@inheritDoc} */
    public void setMetadataFilter(@Nullable final MetadataFilter newFilter) {
        throw new UnsupportedOperationException("Metadata filters are not supported on ChainingMetadataResolver");
    }

    /**
     * Get an activation condition for this resolver.
     * 
     * @return activation condition
     */
    @Nullable public Predicate<ProfileRequestContext> getActivationCondition() {
        return activationCondition;
    }
    
    /**
     * Set an activation condition for this resolver.
     * 
     * @param condition condition to set
     */
    public void setActivationCondition(@Nullable final Predicate<ProfileRequestContext> condition) {
        checkSetterPreconditions();
        activationCondition = condition;
    }
    
    /** {@inheritDoc} */
    @Nullable public EntityDescriptor resolveSingle(@Nullable final CriteriaSet criteria) throws ResolverException {
        checkComponentActive();

        final Iterable<EntityDescriptor> iterable = resolve(criteria);
        if (iterable != null) {
            final Iterator<EntityDescriptor> iterator = iterable.iterator();
            if (iterator != null && iterator.hasNext()) {
                return iterator.next();
            }
        }
        return null;
    }

// Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    @Nonnull public Iterable<EntityDescriptor> resolve(@Nullable final CriteriaSet criteria) throws ResolverException {
        checkComponentActive();

        if (activationCondition != null && criteria != null) {
            final ProfileRequestContextCriterion prc = criteria.get(ProfileRequestContextCriterion.class);
            assert activationCondition != null;
            if (!activationCondition.test(prc != null ? prc.getProfileRequestContext() : null)) {
                log.info("Metadata Resolver {}: Bypassed due to failed activation condition", getId());
                return CollectionSupport.emptyList();
            }
        }
        
        DetectDuplicateEntityIDs detectDuplicates = getDetectDuplicateEntityIDs();
        final DetectDuplicateEntityIDsCriterion dup =
                criteria != null ? criteria.get(DetectDuplicateEntityIDsCriterion.class) : null;
        if (dup != null) {
            detectDuplicates = dup.getValue();
        }
        log.trace("Effective DetectDuplicateEntityIDs value is: {}", detectDuplicates);

        Iterable<EntityDescriptor> result = null;
        Set<String> resultEntityIDs = null;
        for (final MetadataResolver resolver : resolvers) {
            try {
                if (result != null) {
                    assert resolver != null;
                    detectDuplicateEntityIDs(resolver, criteria, resultEntityIDs, detectDuplicates);
                    continue;
                }
                
                final Iterable<EntityDescriptor> descriptors = resolver.resolve(criteria);
                if (descriptors != null && descriptors.iterator().hasNext()) {
                    if (detectDuplicates == DetectDuplicateEntityIDs.Off) {
                        log.trace("Resolved EntityDescriptor(s) from '{}', duplicate detection disabled, returning",
                                resolver.getId());
                        return descriptors;
                    }
                    
                    log.trace("Resolved EntityDescriptor(s) from '{}', duplicate detection enabled, continuing",
                            resolver.getId());
                    result = descriptors;
                    resultEntityIDs = collectEntityIDs(result);
                }
            } catch (final ResolverException e) {
                log.warn("Error retrieving metadata from resolver of type {}, proceeding to next resolver",
                        resolver.getClass().getName(), e);
                continue;
            }
        }

        if (result != null) {
            return result;
        }
        return CollectionSupport.emptyList();
    }
// Checkstyle: CyclomaticComplexity ON
    
    /**
     * Perform duplicate entityID detection.
     * 
     * @param resolver the metadata resolver over which to perform duplicate detection
     * @param criteria the current criteria set
     * @param resultEntityIDs the set of entityIDs contained in the effective results to be returned
     * @param detectDuplicates the effective strategy for duplicate detection
     */
// Checkstyle: CyclomaticComplexity OFF
    private void detectDuplicateEntityIDs(@Nonnull final MetadataResolver resolver,
            @Nullable final CriteriaSet criteria, @Nullable final Set<String> resultEntityIDs,
            @Nonnull final DetectDuplicateEntityIDs detectDuplicates) {
        
        if (resultEntityIDs == null || resultEntityIDs.isEmpty()) {
            return;
        }
        
        switch(detectDuplicates) {
            case Off:
                return;
            case Batch:
                if (!BatchMetadataResolver.class.isInstance(resolver)) {
                    return;
                }
                break;
            case Dynamic:
                if (!DynamicMetadataResolver.class.isInstance(resolver)) {
                    return;
                }
                break;
            case All:
                break;
            default:
                log.warn("Saw unknown DetectDuplicateEntityIDs value, can not process: {}", detectDuplicates);
                return; 
        }
        
        log.trace("Performing duplicate entityID detection on resolver '{} of type {}",
                resolver.getId(), resolver.getClass().getName());
        
        try {
            final Iterable<EntityDescriptor> descriptors = resolver.resolve(criteria);
            if (descriptors != null && descriptors.iterator().hasNext()) {
                final Set<String> descriptorsEnitityIDs = collectEntityIDs(descriptors);

                final Set<String> duplicates = resultEntityIDs.stream()
                        .filter(descriptorsEnitityIDs::contains)
                        .collect(Collectors.toSet());
                
                if (!duplicates.isEmpty()) {
                    log.warn("MetadataResolver '{}' contained duplicate entityIDs relative to the returned results: {}",
                            resolver.getId(), duplicates);
                }
            }
        } catch (final ResolverException e) {
            log.warn("During duplicate detection, error retrieving metadata from resolver '{}' of type {}",
                    resolver.getId(), resolver.getClass().getName(), e);
        }
        
    }
// Checkstyle: CyclomaticComplexity ON

    /**
     * Collect the unique entityIDs from the supplied iterable of entity descriptors.
     * 
     * @param descriptors
     * @return the unique entityIDs from the supplied descriptors
     */
    private Set<String> collectEntityIDs(@Nonnull final Iterable<EntityDescriptor> descriptors) {
        return StreamSupport.stream(descriptors.spliterator(), false)
                .map(EntityDescriptor::getEntityID)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
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
    @Override public void refresh() throws ResolverException {
        for (final MetadataResolver resolver : resolvers) {
            if (resolver instanceof RefreshableMetadataResolver) {
                ((RefreshableMetadataResolver) resolver).refresh();
            }
        }
    }

    /** {@inheritDoc}
     * We iterate over all the children and return the earliest instant or null if one of them hasn't ever updated. */
    @Nullable public Instant getLastUpdate() {
        Instant ret = null;
        for (final MetadataResolver resolver : resolvers) {
            if (resolver instanceof RefreshableMetadataResolver) {
                final Instant lastUpdate = ((RefreshableMetadataResolver) resolver).getLastUpdate();
                if (lastUpdate == null) {
                    return null;
                }
                if (ret == null || ret.isBefore(lastUpdate)) {
                    ret = lastUpdate;
                }
            }
        }

        return ret;
    }

    /** {@inheritDoc}
    * We iterate over all the children and return the earliest instant or null if one of them hasn't ever refreshed. */
    @Nullable public Instant getLastRefresh() {
        Instant ret = null;
        for (final MetadataResolver resolver : resolvers) {
            if (resolver instanceof RefreshableMetadataResolver) {
                final Instant lastRefresh = ((RefreshableMetadataResolver) resolver).getLastRefresh();
                if (lastRefresh == null) {
                    return null;
                }
                if (ret == null || ret.isBefore(lastRefresh)) {
                    ret = lastRefresh;
                }
            }
        }
        
        return ret;
    }
    
    /** {@inheritDoc}
    * We iterate over all the children and return the earliest instant or null if one of them
    * hasn't ever refreshed successfully. */
    @Nullable public Instant getLastSuccessfulRefresh() {
        Instant ret = null;
        for (final MetadataResolver resolver : resolvers) {
            if (resolver instanceof RefreshableMetadataResolver) {
                final Instant lastSuccessRefresh = ((RefreshableMetadataResolver) resolver).getLastSuccessfulRefresh();
                if (lastSuccessRefresh == null) {
                    return null;
                }
                if (ret == null || ret.isBefore(lastSuccessRefresh)) {
                    ret = lastSuccessRefresh;
                }
            }
        }

        return ret;
    }

    /** {@inheritDoc}
     * We iterate over all children - a failure of any is a failure. */
    @Nullable public Boolean wasLastRefreshSuccess() {
        for (final MetadataResolver resolver : resolvers) {
            if (resolver instanceof RefreshableMetadataResolver downcast) {
                final Boolean flag = downcast.wasLastRefreshSuccess();
                if (flag != null && !flag) {
                    return false;
                }
            }
        }

        return true;
    }
    
    /** {@inheritDoc}
     * We iterate over all children and return the first failure we find. */
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

}