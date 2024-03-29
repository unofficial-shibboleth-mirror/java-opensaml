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

package org.opensaml.xmlsec.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.xmlsec.SignatureValidationConfiguration;
import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.SignatureValidationParametersResolver;
import org.opensaml.xmlsec.criterion.SignatureValidationConfigurationCriterion;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.slf4j.Logger;

/**
 * Basic implementation of {@link SignatureValidationParametersResolver}.
 * 
 * <p>
 * The following {@link net.shibboleth.shared.resolver.Criterion} inputs are supported:
 * </p>
 * <ul>
 * <li>{@link SignatureValidationConfigurationCriterion} - required</li> 
 * </ul>
 */
public class BasicSignatureValidationParametersResolver 
        extends AbstractSecurityParametersResolver<SignatureValidationParameters> 
        implements SignatureValidationParametersResolver {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(BasicSignatureValidationParametersResolver.class);

    /** {@inheritDoc} */
    @Nonnull public Iterable<SignatureValidationParameters> resolve(@Nullable final CriteriaSet criteria) 
            throws ResolverException {
        
        final SignatureValidationParameters params = resolveSingle(criteria);
        if (params != null) {
            return CollectionSupport.singletonList(params);
        }
        return CollectionSupport.emptyList();
    }

    /** {@inheritDoc} */
    @Nullable public SignatureValidationParameters resolveSingle(@Nullable final CriteriaSet criteria)
            throws ResolverException {
        
        if (criteria == null) {
            log.debug("CriteriaSet was null");
            return null;
        }
        
        final SignatureValidationConfigurationCriterion criterion =
                Constraint.isNotNull(criteria.get(SignatureValidationConfigurationCriterion.class),
                        "Resolver requires an instance of SignatureValidationConfigurationCriterion");
        
        final SignatureValidationParameters params = new SignatureValidationParameters();
        
        resolveAndPopulateIncludesExcludes(params, criteria, criterion.getConfigurations());
        
        params.setSignatureTrustEngine(resolveSignatureTrustEngine(criteria));
        
        logResult(params);
        
        return params;
    }
    
    /**
     * Log the resolved parameters.
     * 
     * @param params the resolved param
     */
    protected void logResult(@Nonnull final SignatureValidationParameters params) {
        if (log.isDebugEnabled()) {
            log.debug("Resolved SignatureValidationParameters:");
            
            log.debug("\tAlgorithm includes: {}", params.getIncludedAlgorithms());
            log.debug("\tAlgorithm excludes: {}", params.getExcludedAlgorithms());
            
            log.debug("\tSignatureTrustEngine: {}", 
                    params.getSignatureTrustEngine() != null ? "present" : "null");
        }
    }

    /**
     * Resolve and return the effective {@link SignatureTrustEngine}.
     * 
     * @param criteria the input criteria being evaluated
     * 
     * @return the effective resolver, or null
     */
    @Nullable protected SignatureTrustEngine resolveSignatureTrustEngine(@Nonnull final CriteriaSet criteria) {
        
        final SignatureValidationConfigurationCriterion criterion =
                criteria.get(SignatureValidationConfigurationCriterion.class);
        assert criterion != null;
        
        for (final SignatureValidationConfiguration config : criterion.getConfigurations()) {
            if (config.getSignatureTrustEngine() != null) {
                return config.getSignatureTrustEngine();
            }
        }
        return null;
    }

}