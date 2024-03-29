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

package org.opensaml.saml.common.binding.impl;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.opensaml.saml.common.binding.AbstractEndpointResolver;
import org.opensaml.saml.criterion.BindingCriterion;
import org.opensaml.saml.criterion.EndpointCriterion;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.IndexedEndpoint;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;

/**
 * Default implementation that performs additional endpoint validation.
 * 
 * <p>The supported {@link net.shibboleth.shared.resolver.Criterion} types and their use follows:</p>
 * 
 * <dl>
 *  <dt> {@link EndpointCriterion} </dt>
 *  <dd> Requires that the candidate endpoint's various attributes match the attributes found in the criterion. </dd> 
 *  
 *  <dt> {@link BindingCriterion} </dt>
 *  <dd> Requires that the candidate endpoint's Binding attribute is among the bindings included in the criterion. </dd>
 * </dl>
 * 
 * @param <EndpointType> type of endpoint
 */
public class DefaultEndpointResolver<EndpointType extends Endpoint> extends AbstractEndpointResolver<EndpointType> {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(DefaultEndpointResolver.class);

    /** {@inheritDoc} */
    @Override
    protected boolean doCheckEndpoint(@Nonnull final CriteriaSet criteria, @Nonnull final EndpointType endpoint) {
        
        if (isInMetadataOrder()) {
            // Make sure the candidate binding, if set, is one of the bindings specified.
            // When the sort isn't based on metadata, this has already happened.
            final BindingCriterion bindingCriterion = criteria.get(BindingCriterion.class);
            if (bindingCriterion != null && !checkBindingCriterion(bindingCriterion, endpoint)) {
                return false;
            }
        }
        
        // Compare individual fields to a comparison template.
        final EndpointCriterion<EndpointType> epCriterion = criteria.get(EndpointCriterion.class);
        if (epCriterion != null && !checkEndpointCriterion(epCriterion, endpoint)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Verify the candidate's Binding attribute, if set, is among the set in the supplied criterion.
     * 
     * @param bindings  the bindings to allow
     * @param endpoint  the candidate endpoint
     * 
     * @return true iff the candidate has no Binding, or its Binding is permitted
     */
    private boolean checkBindingCriterion(@Nonnull final BindingCriterion bindings,
            @Nonnull final EndpointType endpoint) {
        
        if (endpoint.getBinding() != null) {
            if (!bindings.getBindings().contains(endpoint.getBinding())) {
                log.debug("{} Candidate endpoint binding '{}' not permitted by input criteria", getLogPrefix(),
                        endpoint.getBinding());
                return false;
            }
        }
        
        return true;
    }

// Checkstyle: CyclomaticComplexity OFF
    /**
     * Verify the candidate's attributes match any attributes supplied in the criterion.
     * 
     * @param comparison    the endpoint to compare against
     * @param endpoint      the candidate endpoint
     * @return  true iff the candidate's attributes match those of the criterion
     */
    private boolean checkEndpointCriterion(@Nonnull final EndpointCriterion<EndpointType> comparison,
            @Nonnull final EndpointType endpoint) {

        final EndpointType comparisonEndpoint = comparison.getEndpoint();

        // Are we comparing ourselves, as in a signed request case?
        if (comparisonEndpoint == endpoint) {
            log.debug("{} Candidate endpoint was supplied by the criterion, skipping check", getLogPrefix());
            return true;
        }
        
        // Check binding.
        if (comparisonEndpoint.getBinding() != null &&
                !Objects.equals(comparisonEndpoint.getBinding(), endpoint.getBinding())) {
            log.debug("{} Candidate endpoint binding '{}' did not match '{}'", getLogPrefix(),
                    endpoint.getBinding(), comparisonEndpoint.getBinding());
            return false;
        }
        
        // Check location.
        if (comparisonEndpoint.getLocation() != null) {
            if (!Objects.equals(comparisonEndpoint.getLocation(), endpoint.getLocation())
                    && !Objects.equals(comparisonEndpoint.getLocation(), endpoint.getResponseLocation())) {
                log.debug("{} Neither candidate endpoint location '{}' nor response location '{}' matched '{}' ",
                        getLogPrefix(), endpoint.getLocation(), endpoint.getResponseLocation(), 
                        comparisonEndpoint.getLocation());
                return false;
            }
        }
        
        // Check index.
        if (comparisonEndpoint instanceof IndexedEndpoint
                && ((IndexedEndpoint) comparisonEndpoint).getIndex() != null) {
            if (!(endpoint instanceof IndexedEndpoint)) {
                log.debug("{} Candidate endpoint was not indexed, so did not match", getLogPrefix());
                return false;
            } else if (!Objects.equals(((IndexedEndpoint) comparisonEndpoint).getIndex(),
                    ((IndexedEndpoint) endpoint).getIndex())) {
                log.debug("{} Candidate endpoint index {} did not match {}", getLogPrefix(),
                        ((IndexedEndpoint) endpoint).getIndex(),
                        ((IndexedEndpoint) comparisonEndpoint).getIndex());
                return false;
            }
        }
        
        return true;
    }
// Checkstyle: CyclomaticComplexity ON
    
}