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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.criterion.SatisfyAnyCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.metadata.criteria.role.EvaluableRoleDescriptorCriterion;
import org.opensaml.saml.metadata.criteria.role.impl.RoleDescriptorCriterionPredicateRegistry;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.RoleDescriptorResolver;
import org.opensaml.saml.saml2.common.IsTimeboundSAMLObjectValidPredicate;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.slf4j.Logger;

import com.google.common.collect.Iterables;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.AbstractIdentifiedInitializableComponent;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.CriterionPredicateRegistry;
import net.shibboleth.shared.resolver.ResolverException;
import net.shibboleth.shared.resolver.ResolverSupport;

/**
 * Implementation of {@link RoleDescriptorResolver} which wraps an instance of {@link MetadataResolver} to
 * support basic EntityDescriptor resolution, and then performs further role-related filtering over the
 * returned EntityDescriptor.
 * 
 * <p>
 * This implementation passes the input {@link CriteriaSet} through to the wrapped metadata resolver as-is.
 * </p>
 * 
 * <p>
 * This implementation also supports applying arbitrary predicates to the returned role descriptors, either passed
 * directly as instances of {@link EvaluableRoleDescriptorCriterion} in the criteria, or resolved dynamically
 * from other criteria via an instance of {@link CriterionPredicateRegistry}.
 * </p>
 */
public class PredicateRoleDescriptorResolver extends AbstractIdentifiedInitializableComponent 
        implements RoleDescriptorResolver {
    
    /** Predicate for evaluating whether a TimeboundSAMLObject is valid. */
    @Nonnull private static final Predicate<XMLObject> IS_VALID_PREDICATE = new IsTimeboundSAMLObjectValidPredicate();
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(PredicateRoleDescriptorResolver.class);
    
    /** Whether metadata is required to be valid. */
    private boolean requireValidMetadata;
 
    /** Resolver of EntityDescriptors. */
    @Nonnull private MetadataResolver entityDescriptorResolver;
    
    /** Flag which determines whether predicates used in filtering are connected by 
     * a logical 'OR' (true) or by logical 'AND' (false). Defaults to false. */
    private boolean satisfyAnyPredicates;
    
    /** Registry used in resolving predicates from criteria. */
    @Nullable private CriterionPredicateRegistry<RoleDescriptor> criterionPredicateRegistry;
    
    /** Flag which determines whether the default predicate registry will be used if no one is supplied explicitly.
     * Defaults to true. */
    private boolean useDefaultPredicateRegistry;
    
    /** Flag indicating whether resolution may be performed solely by applying predicates to the
     * entire metadata collection. Defaults to false. */
    private boolean resolveViaPredicatesOnly;
    
    /**
     * Constructor.
     *
     * @param mdResolver the resolver of EntityDescriptors
     */
    public PredicateRoleDescriptorResolver(
            @Nonnull @ParameterName(name="mdResolver") final MetadataResolver mdResolver) {
        entityDescriptorResolver = Constraint.isNotNull(mdResolver, "Resolver for EntityDescriptors may not be null");
        setId(UUID.randomUUID().toString()); 
        requireValidMetadata = true;
        useDefaultPredicateRegistry = true;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isRequireValidMetadata() {
        return requireValidMetadata;
    }

    /** {@inheritDoc} */
    @Override
    public void setRequireValidMetadata(final boolean require) {
        checkSetterPreconditions();
        requireValidMetadata = require;
    }
    
    /**
     * Get the flag indicating whether resolved credentials may satisfy any predicates 
     * (i.e. connected by logical 'OR') or all predicates (connected by logical 'AND').
     * 
     * <p>Defaults to false.</p>
     * 
     * @return true if must satisfy all, false otherwise
     */
    public boolean isSatisfyAnyPredicates() {
        return satisfyAnyPredicates;
    }

    /**
     * Set the flag indicating whether resolved credentials may satisfy any predicates 
     * (i.e. connected by logical 'OR') or all predicates (connected by logical 'AND').
     * 
     * <p>Defaults to false.</p>
     * 
     * @param flag true if must satisfy all, false otherwise
     */
    public void setSatisfyAnyPredicates(final boolean flag) {
        checkSetterPreconditions();
        satisfyAnyPredicates = flag;
    }

    /**
     * Get the registry used in resolving predicates from criteria.
     * 
     * @return the effective registry instance used
     */
    @NonnullAfterInit public CriterionPredicateRegistry<RoleDescriptor> getCriterionPredicateRegistry() {
        return criterionPredicateRegistry;
    }

    /**
     * Set the registry used in resolving predicates from criteria.
     * 
     * @param registry the registry instance to use
     */
    public void setCriterionPredicateRegistry(@Nullable final CriterionPredicateRegistry<RoleDescriptor> registry) {
        checkSetterPreconditions();
        criterionPredicateRegistry = registry;
    }

    /**
     * Get the flag which determines whether the default predicate registry will be used 
     * if one is not supplied explicitly.
     * 
     * <p>Defaults to true.</p>
     * 
     * @return true if should use default registry, false otherwise
     */
    public boolean isUseDefaultPredicateRegistry() {
        return useDefaultPredicateRegistry;
    }

    /**
     * Set the flag which determines whether the default predicate registry will be used 
     * if one is not supplied explicitly.
     * 
     * <p>Defaults to true.</p>
     * 
     * @param flag true if should use default registry, false otherwise
     */
    public void setUseDefaultPredicateRegistry(final boolean flag) {
        checkSetterPreconditions();
        useDefaultPredicateRegistry = flag;
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
    
    /**
     * Subclasses should override this method to perform any initialization logic necessary. Default implementation is a
     * no-op.
     * 
     * @throws ComponentInitializationException thrown if there is a problem initializing the provider
     */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (getCriterionPredicateRegistry() == null && isUseDefaultPredicateRegistry()) {
            setCriterionPredicateRegistry(new RoleDescriptorCriterionPredicateRegistry());
        }
    }
    
    /** {@inheritDoc} */
    @Nullable public RoleDescriptor resolveSingle(@Nullable final CriteriaSet criteria) throws ResolverException {
        checkComponentActive();
        final Iterable<RoleDescriptor> iterable = resolve(criteria);
        if (iterable != null) {
            final Iterator<RoleDescriptor> iterator = iterable.iterator();
            if (iterator != null && iterator.hasNext()) {
                return iterator.next();
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    @Nonnull public Iterable<RoleDescriptor> resolve(@Nullable final CriteriaSet criteria) throws ResolverException {
        checkComponentActive();
        
        final Iterable<EntityDescriptor> entityDescriptorsSource = entityDescriptorResolver.resolve(criteria);
        if (!entityDescriptorsSource.iterator().hasNext()) {
            log.debug("Resolved no EntityDescriptors via underlying MetadataResolver, returning empty collection");
            return CollectionSupport.emptySet();
        }
        
        if (log.isDebugEnabled()) {
            log.debug("Resolved {} source EntityDescriptors", Iterables.size(entityDescriptorsSource));
        }
        
        final Predicate<? super RoleDescriptor> predicate = isRequireValidMetadata() ? IS_VALID_PREDICATE 
                : PredicateSupport.alwaysTrue();
            
        if (haveRoleCriteria(criteria)) {
            final Iterable<RoleDescriptor> candidates =
                    getCandidatesByRoleAndProtocol(entityDescriptorsSource, criteria);
            if (log.isDebugEnabled()) {
                log.debug("Resolved {} RoleDescriptor candidates via role criteria, performing predicate filtering", 
                        Iterables.size(candidates));
            }
            return predicateFilterCandidates(Iterables.filter(candidates, predicate::test), criteria, false);
        } else if (isResolveViaPredicatesOnly()) {
            final Iterable<RoleDescriptor> candidates = getAllCandidates(entityDescriptorsSource);
            if (log.isDebugEnabled()) {
                log.debug("Resolved {} RoleDescriptor total candidates for predicate-only resolution", 
                        Iterables.size(candidates));
            }
            return predicateFilterCandidates(Iterables.filter(candidates, predicate::test), criteria, true);
        } else {
            log.debug("Found no role criteria and predicate-only resolution is disabled, returning empty collection");
            return CollectionSupport.emptySet();
        }
        
    }
    
    /**
     * Determine if have entity role criteria.
     * 
     * @param criteria the current criteria set
     * 
     * @return true if have role criteria, false otherwise
     */
    protected boolean haveRoleCriteria(@Nullable final CriteriaSet criteria) {
        return criteria != null && criteria.contains(EntityRoleCriterion.class);
    }

    /**
     * Obtain the role descriptors contained by the input entity descriptors which match 
     * the specified role and protocol criteria.
     * 
     * <p>
     * This method should only be called if {@link #haveRoleCriteria(CriteriaSet)} evaluates to true.
     * </p>
     * 
     * @param entityDescriptors the entity descriptors on which to operate
     * @param criteria the current criteria set
     * 
     * @return the role descriptors corresponding to the input entity role and protocol
     */
    @Nonnull protected Iterable<RoleDescriptor> getCandidatesByRoleAndProtocol(
            @Nonnull final Iterable<EntityDescriptor> entityDescriptors, @Nullable final CriteriaSet criteria) {
        
        final EntityRoleCriterion roleCriterion =
                Constraint.isNotNull(criteria != null ? criteria.get(EntityRoleCriterion.class) : null,
                        "EntityRoleCriterion was not supplied");
        
        assert criteria != null;
        final ProtocolCriterion protocolCriterion = criteria.get(ProtocolCriterion.class);
        
        final ArrayList<Iterable<RoleDescriptor>> aggregate = new ArrayList<>();
        for (final EntityDescriptor entityDescriptor : entityDescriptors) {
            if (protocolCriterion != null) {
                aggregate.add(entityDescriptor.getRoleDescriptors(roleCriterion.getRole(), 
                        protocolCriterion.getProtocol()));
            } else {
                aggregate.add(entityDescriptor.getRoleDescriptors(roleCriterion.getRole()));
            }
        }
        return Iterables.concat(aggregate);
    }

    /**
     * Obtain all role descriptors contained by the input entity descriptors.
     * 
     * @param entityDescriptors the entity descriptors on which to operate 
     * 
     * @return all role descriptors contained by the input entity descriptors
     */
    @Nonnull protected Iterable<RoleDescriptor> getAllCandidates(
            @Nonnull final Iterable<EntityDescriptor> entityDescriptors) {
        
        final ArrayList<Iterable<RoleDescriptor>> aggregate = new ArrayList<>();
        for (final EntityDescriptor entityDescriptor : entityDescriptors) {
            aggregate.add(entityDescriptor.getRoleDescriptors());
        }
        return Iterables.concat(aggregate);
    }
    
    /**
     * Filter the supplied candidates by resolving predicates from the supplied criteria and applying
     * the predicates to return a filtered {@link Iterable}.
     * 
     * @param candidates the candidates to evaluate
     * @param criteria the criteria set to evaluate
     * @param onEmptyPredicatesReturnEmpty if true and no predicates are supplied, then return an empty iterable;
     *          otherwise return the original input candidates
     * 
     * @return an iterable of the candidates filtered by the resolved predicates
     * 
     * @throws ResolverException if there is a fatal error during resolution
     */
    @Nonnull protected Iterable<RoleDescriptor> predicateFilterCandidates(
            @Nonnull final Iterable<RoleDescriptor> candidates, @Nullable final CriteriaSet criteria,
            final boolean onEmptyPredicatesReturnEmpty) throws ResolverException {
        
        if (!candidates.iterator().hasNext()) {
            log.debug("Candidates iteration was empty, nothing to filter via predicates");
            return CollectionSupport.emptySet();
        }
        
        log.debug("Attempting to filter candidate RoleDescriptors via resolved Predicates");
        
        final Set<Predicate<RoleDescriptor>> predicates = ResolverSupport.getPredicates(criteria, 
                EvaluableRoleDescriptorCriterion.class, getCriterionPredicateRegistry());
        
        log.trace("Resolved {} Predicates: {}", predicates.size(), predicates);
        
        final boolean satisfyAny;
        final SatisfyAnyCriterion satisfyAnyCriterion =
                criteria != null ? criteria.get(SatisfyAnyCriterion.class) : null;
        if (satisfyAnyCriterion  != null) {
            log.trace("CriteriaSet contained SatisfyAnyCriterion");
            satisfyAny = satisfyAnyCriterion.isSatisfyAny();
        } else {
            log.trace("CriteriaSet did NOT contain SatisfyAnyCriterion");
            satisfyAny = isSatisfyAnyPredicates();
        }
        
        log.trace("Effective satisyAny value: {}", satisfyAny);
        
        final Iterable<RoleDescriptor> result = 
                ResolverSupport.getFilteredIterable(candidates, predicates, satisfyAny, onEmptyPredicatesReturnEmpty);
        if (log.isDebugEnabled()) {
            log.debug("After predicate filtering {} RoleDescriptors remain", Iterables.size(result));
        }
        return result;
    }

}