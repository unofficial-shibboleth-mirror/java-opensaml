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

package org.opensaml.security.x509.tls.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.tls.CertificateNameOptions;
import org.opensaml.security.x509.tls.ClientTLSValidationConfiguration;
import org.opensaml.security.x509.tls.ClientTLSValidationConfigurationCriterion;
import org.opensaml.security.x509.tls.ClientTLSValidationParameters;
import org.opensaml.security.x509.tls.ClientTLSValidationParametersResolver;

/**
 * Basic implementation of {@link ClientTLSValidationParametersResolver}.
 * 
 * <p>
 * The following {@link net.shibboleth.shared.resolver.Criterion} inputs are supported:
 * </p>
 * <ul>
 * <li>{@link ClientTLSValidationConfigurationCriterion} - required</li> 
 * </ul>
 */
public class BasicClientTLSValidationParametersResolver implements ClientTLSValidationParametersResolver {

    /** {@inheritDoc} */
    @Nonnull public Iterable<ClientTLSValidationParameters> resolve(@Nullable final CriteriaSet criteria) 
            throws ResolverException {
        final ClientTLSValidationParameters params = resolveSingle(criteria);
        if (params != null) {
            return CollectionSupport.singletonList(params);
        }
        return CollectionSupport.emptyList();
    }

    /** {@inheritDoc} */
    @Nullable public ClientTLSValidationParameters resolveSingle(@Nullable final CriteriaSet criteria)
            throws ResolverException {
        final CriteriaSet localCriteria = Constraint.isNotNull(criteria, "CriteriaSet was null");
        
        final ClientTLSValidationParameters params = new ClientTLSValidationParameters();
        
        params.setX509TrustEngine(resolveTrustEngine(localCriteria));
        
        params.setCertificateNameOptions(resolveNameOptions(localCriteria));
        
        return params;
    }
    
    /**
     * Resolve and return the effective
     * {@link TrustEngine}<code>&lt;? super </code>{@link X509Credential}<code>&gt;</code>.
     * 
     * @param criteria the input criteria being evaluated
     * 
     * @return the effective resolver, or null
     */
    @Nullable protected TrustEngine<? super X509Credential> resolveTrustEngine(@Nonnull final CriteriaSet criteria) {
        
        final var tlsCriterion = Constraint.isNotNull(criteria.get(ClientTLSValidationConfigurationCriterion.class), 
                "Resolver requires an instance of ClientTLSValidationConfigurationCriterion");
        
        for (final ClientTLSValidationConfiguration config : tlsCriterion.getConfigurations()) {
            if (config.getX509TrustEngine() != null) {
                return config.getX509TrustEngine();
            }
        }
        return null;
    }

    /**
     * Resolve and return the effective {@link CertificateNameOptions}.
     * 
     * @param criteria the input criteria being evaluated
     * 
     * @return the effective name options, or null
     */
    @Nullable protected CertificateNameOptions resolveNameOptions(@Nonnull final CriteriaSet criteria) {

        final var tlsCriterion = Constraint.isNotNull(criteria.get(ClientTLSValidationConfigurationCriterion.class), 
                "Resolver requires an instance of ClientTLSValidationConfigurationCriterion");

        for (final ClientTLSValidationConfiguration config : tlsCriterion.getConfigurations()) {
            if (config.getCertificateNameOptions() != null) {
                return config.getCertificateNameOptions();
            }
        }
        return null;
    }

}