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

package org.opensaml.saml.common.binding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.saml.criterion.BestMatchLocationCriterion;
import org.opensaml.saml.criterion.BindingCriterion;
import org.opensaml.saml.criterion.EndpointCriterion;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.IndexedEndpoint;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.AbstractIdentifiedInitializableComponent;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/**
 * Base implementation that resolves and validates protocol/profile endpoints using a combination of supplied
 * parameters and SAML metadata.
 * 
 * <p>SAML metadata rules are followed for deriving candidate endpoints to evaluate. The base class implements
 * only a subset of required functionality, then extracts a set of candidates from metadata if present, and
 * delegates to a subclass to actually evaluate each one for acceptability.</p>
 * 
 * <p>The supported {@link net.shibboleth.shared.resolver.Criterion} types and their use follows:</p>
 * 
 * <dl>
 *  <dt>{@link EndpointCriterion} (required)</dt>
 *  <dd>Contains a "template" for the eventual {@link Endpoint}(s) to resolve that identifies at minimum the
 *  type of endpoint object (via schema type or element name) to resolve. It MAY contain other attributes that
 *  will be used in matching candidate endpoints for suitability, such as index, binding, location, etc. If so
 *  marked, it may also be resolved as a trusted endpoint without additional verification required.</dd>
 *  
 *  <dt>{@link BestMatchLocationCriterion}</dt>
 *  <dd>Prioritizes endpoint whose Location matches the most characters of the input criterion location. Only
 *  applied to the {@link #resolveSingle(CriteriaSet)} method.</dd>
 *  
 *  <dt>{@link BindingCriterion}</dt>
 *  <dd>Ordered list of bindings to filter and sort the endpoints. This overrides the ordering from the
 *  metadata and possibly overrides the normal default endpoint in favor of higher-precedence bindings.</dd>
 *  
 *  <dt>{@link RoleDescriptorCriterion}</dt>
 *  <dd>If present, provides access to the candidate endpoint(s) to attempt resolution against. Strictly optional,
 *  but if absent, the supplied endpoint (from {@link EndpointCriterion}) is returned as the sole result,
 *  whatever its completeness/usability, allowing for subclass validation.</dd>
 * </dl>
 * 
 * <p>Subclasses should override the {{@link #doCheckEndpoint(CriteriaSet, Endpoint)} method to implement
 * further criteria.</p>
 * 
 * @param <EndpointType> type of endpoint
 */
public abstract class AbstractEndpointResolver<EndpointType extends Endpoint>
        extends AbstractIdentifiedInitializableComponent implements EndpointResolver<EndpointType> {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(AbstractEndpointResolver.class);
    
    /** Sorting rule for results. */
    private boolean inMetadataOrder;
    
    /** Constructor. */
    public AbstractEndpointResolver() {
        super.setId(getClass().getName());
        inMetadataOrder = true;
    }

    /**
     * Get whether the results should be sorted by metadata order or based on the order of
     * bindings provided to the lookup.
     * 
     * @return true iff the {@link BindingCriterion} should be ignored for the purposes of sorting the results
     * 
     * @since 4.1.0
     */
    public boolean isInMetadataOrder() {
        return inMetadataOrder;
    }
    
    /**
     * Set whether the results should be sorted by metadata order or based on the order of
     * bindings provided to the lookup.
     * 
     * <p>Defaults to true</p>
     * 
     * @param flag flag to set
     * 
     * @since 4.1.0
     */
    public void setInMetadataOrder(final boolean flag) {
        checkSetterPreconditions();
        
        inMetadataOrder = flag;
    }
    
    /** {@inheritDoc} */
    @Nonnull @NonnullElements public Iterable<EndpointType> resolve(@Nullable final CriteriaSet criteria)
            throws ResolverException {
        final EndpointCriterion<EndpointType> endpointCriterion = validateCriteria(criteria);
        assert criteria != null;
        
        if (canUseRequestedEndpoint(endpointCriterion)) {
            final EndpointType endpoint = endpointCriterion.getEndpoint();
            if (doCheckEndpoint(criteria, endpoint)) {
                return CollectionSupport.singletonList(endpoint);
            }
            log.debug("{} Requested endpoint was rejected by extended validation process", getLogPrefix());
            return CollectionSupport.emptyList();
        }
        
        final List<EndpointType> candidates = getCandidatesFromMetadata(criteria);
        final Iterator<EndpointType> i = candidates.iterator();
        while (i.hasNext()) {
            if (!doCheckEndpoint(criteria, i.next())) {
                i.remove();
            }
        }
        
        log.debug("{} {} endpoints remain after filtering process", getLogPrefix(), candidates.size());
        return candidates;
    }

// Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    @Nullable public EndpointType resolveSingle(@Nullable final CriteriaSet criteria) throws ResolverException {
        final EndpointCriterion<EndpointType> endpointCriterion = validateCriteria(criteria);
        assert criteria != null;

        if (canUseRequestedEndpoint(endpointCriterion)) {
            final EndpointType endpoint = endpointCriterion.getEndpoint();
            if (doCheckEndpoint(criteria, endpoint)) {
                return endpoint;
            }
            log.debug("{} Requested endpoint was rejected by extended validation process", getLogPrefix());
            return null;
        }
        
        // Starting at -1 ensures the first candidate automatically starts as the best match.
        int bestMatchLen = -1;
        EndpointType bestMatch = null;
        
        final BestMatchLocationCriterion startsWith = criteria.get(BestMatchLocationCriterion.class);
        
        for (final EndpointType candidate : getCandidatesFromMetadata(criteria)) {
            assert candidate != null;
            if (doCheckEndpoint(criteria, candidate)) {
                if (startsWith != null) {
                    // Evaluate how good a match it is.
                    String candidateLocation = candidate.getLocation();
                    if (candidateLocation == null) {
                        candidateLocation = candidate.getResponseLocation();
                    }
                    if (candidateLocation == null) {
                        log.debug("Skipping endpoint with no Location or ResponseLocation");
                        continue;
                    }
                    int i = 0;
                    for (; i < candidateLocation.length() && i < startsWith.getLocation().length(); ++i) {
                        if (candidateLocation.charAt(i) != startsWith.getLocation().charAt(i)) {
                            break;
                        }
                    }
                    
                    // If the match is better, reset.
                    if (i > bestMatchLen) {
                        bestMatchLen = i;
                        bestMatch = candidate;
                    }
                } else {
                    // Not testing for overlap with input criterion, so just return the first match.
                    return candidate;
                }
            }
        }
        
        if (bestMatch != null) {
            return bestMatch;
        }
        
        log.debug("{} No candidate endpoints met criteria", getLogPrefix());
        return null;
    }
// Checkstyle: CyclomaticComplexity ON
    
    /**
     * Apply the supplied criteria to a candidate endpoint to determine its suitability. 
     * 
     * @param criteria  input criteria set
     * @param endpoint  candidate endpoint
     * 
     * @return  true iff the endpoint meets the supplied criteria
     */
    protected boolean doCheckEndpoint(@Nonnull final CriteriaSet criteria, @Nonnull final EndpointType endpoint) {
        return true;
    }

    /**
     * Verify that the required {@link EndpointCriterion} is present.
     * 
     * @param criteria  input criteria set
     * 
     * @return the {@link EndpointCriterion} 
     * 
     * @throws ResolverException if the input set is null or no {@link EndpointCriterion} is present
     */
    @Nonnull private EndpointCriterion<EndpointType> validateCriteria(@Nullable final CriteriaSet criteria)
            throws ResolverException {
        if (criteria == null) {
            throw new ResolverException("CriteriaSet cannot be null");
        }

        @SuppressWarnings("unchecked")
        final EndpointCriterion<EndpointType> epCriterion = criteria.get(EndpointCriterion.class);
        if (epCriterion == null) {
            throw new ResolverException("EndpointCriterion not supplied");
        }
        return epCriterion;
    }
    
    /**
     * Optimize the case of resolving a single endpoint if a populated endpoint is supplied via
     * criteria, and validation is unnecessary due to a signed request. Note that this endpoint may
     * turn out to be unusable by the caller, but that's immaterial because the requester must have
     * dictated the binding and location, so we're not allowed to ignore that.
     * 
     * @param criterion  the input {@link EndpointCriterion}
     * 
     * @return true iff the supplied endpoint via {@link EndpointCriterion} should be returned
     */
    private boolean canUseRequestedEndpoint(@Nonnull final EndpointCriterion<EndpointType> criterion) {
        if (criterion.isTrusted()) {
            final EndpointType requestedEndpoint = criterion.getEndpoint();
            if (requestedEndpoint.getBinding() != null && (requestedEndpoint.getLocation() != null
                    || requestedEndpoint.getResponseLocation() != null)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get a mutable list of endpoints of a given type found in the metadata role contained in a
     * {@link RoleDescriptorCriterion} (or an empty list if no metadata exists).
     * 
     * <p>The endpoint type to extract is based on the candidate endpoint in an
     * {@link EndpointCriterion}. If the endpoints are indexed, the first list entry will
     * contain the default endpoint to use in the absence of other limiting criteria.</p>
     * 
     * @param criteria input criteria set
     * 
     * @return mutable list of endpoints from the metadata
     */
    @Nonnull @NonnullElements private List<EndpointType> getCandidatesFromMetadata(
            @Nonnull final CriteriaSet criteria) {
        
        // Check for metadata.
        final RoleDescriptorCriterion role = criteria.get(RoleDescriptorCriterion.class);
        if (role == null) {
            log.debug("{} No metadata supplied, no candidate endpoints to return", getLogPrefix());
            return new ArrayList<>();
        }
        
        // Determine the QName type of endpoints to extract based on candidate type.
        @SuppressWarnings("unchecked")
        final EndpointCriterion<EndpointType> epCriterion = criteria.get(EndpointCriterion.class);
        assert epCriterion != null;
        QName endpointType = epCriterion.getEndpoint().getSchemaType();
        if (endpointType == null) {
            endpointType = epCriterion.getEndpoint().getElementQName();
        }
        
        final List<Endpoint> endpoints = role.getRole().getEndpoints(endpointType);
        
        // Check for none.
        if (endpoints.isEmpty()) {
            log.debug("{} No endpoints in metadata of type {}", getLogPrefix(), endpointType);
            return new ArrayList<>();
        }

        final BindingCriterion bindingCriterion = criteria.get(BindingCriterion.class);
        if (inMetadataOrder || bindingCriterion == null || bindingCriterion.getBindings().isEmpty()) {
            // No second-level sort. Return the endpoints in the metadata of the candidate type,
            // default endpoint first.
            log.debug("{} Returning {} candidate endpoints of type {}", getLogPrefix(), endpoints.size(),
                    endpointType);
            return sortCandidates(endpoints);
        }
        
        // The binding(s) enforce a top-level sort on the endpoints, which is achieved
        // by iterating over the full set multiple times with a binding filter applied.
        
        final List<EndpointType> sortedResults = new ArrayList<>(endpoints.size());
        for (final String binding : bindingCriterion.getBindings()) {
            sortedResults.addAll(
                    sortCandidates(
                            endpoints.stream()
                                .filter(ep -> binding.equals(ep.getBinding()))
                                .collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableList())).get()));
        }
        log.debug("{} Returning {} candidate endpoints of type {}", getLogPrefix(), sortedResults.size(),
                endpointType);
        return sortedResults;
    }
    
    /**
     * Copy and sort the endpoints such that the default endpoint by SAML rules comes first.
     * 
     * @param candidates input list of endpoints
     * 
     * @return a new list containing the endpoints such that the default is first
     */
    // Checkstyle: CyclomaticComplexity OFF
    @SuppressWarnings("unchecked")
    @Nonnull @NonnullElements private List<EndpointType> sortCandidates(
            @Nonnull @NonnullElements final List<Endpoint> candidates) {
        
        // Use a linked list, and move the default endpoint to the head of the list.
        // SAML defaulting rules apply to IndexedEnpdoint types, and require checking
        // for the isDefault attribute. The default is the one marked true, or if none are,
        // the first not marked false.
        EndpointType hardDefault = null;
        EndpointType softDefault = null;
        final LinkedList<EndpointType> toReturn = new LinkedList<>();
        for (final Endpoint endpoint : candidates) {
            if (hardDefault == null && endpoint instanceof IndexedEndpoint) {
                final Boolean flag = ((IndexedEndpoint) endpoint).isDefault();
                if (flag != null) {
                    if (flag.booleanValue()) {
                        hardDefault = (EndpointType) endpoint;
                        if (softDefault != null) {
                            toReturn.addFirst(softDefault);
                            softDefault = null;
                        }
                    } else {
                        toReturn.addLast((EndpointType) endpoint);
                    }
                } else if (hardDefault == null && softDefault == null) {
                    softDefault = (EndpointType) endpoint;
                } else {
                    toReturn.addLast((EndpointType) endpoint);
                }
            } else {
                toReturn.addLast((EndpointType) endpoint);
            }
        }
        
        if (hardDefault != null) {
            toReturn.addFirst(hardDefault);
        } else if (softDefault != null) {
            toReturn.addFirst(softDefault);
        }
       
        return toReturn;
    }
    // Checkstyle: CyclomaticComplexity ON

    /**
     * Return a prefix for logging messages for this component.
     * 
     * @return a string for insertion at the beginning of any log messages
     */
    @Nonnull protected String getLogPrefix() {
        return "Endpoint Resolver " + getId() + ":";
    }

}