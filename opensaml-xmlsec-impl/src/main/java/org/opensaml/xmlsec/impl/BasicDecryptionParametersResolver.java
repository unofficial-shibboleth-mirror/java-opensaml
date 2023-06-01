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

package org.opensaml.xmlsec.impl;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.xmlsec.DecryptionConfiguration;
import org.opensaml.xmlsec.DecryptionParameters;
import org.opensaml.xmlsec.DecryptionParametersResolver;
import org.opensaml.xmlsec.criterion.DecryptionConfigurationCriterion;
import org.opensaml.xmlsec.criterion.DecryptionRecipientsCriterion;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.slf4j.Logger;

/**
 * Basic implementation of {@link DecryptionParametersResolver}.
 * 
 * <p>
 * The following {@link net.shibboleth.shared.resolver.Criterion} inputs are supported:
 * </p>
 * <ul>
 * <li>{@link DecryptionConfigurationCriterion} - required</li> 
 * </ul>
 */
public class BasicDecryptionParametersResolver extends AbstractSecurityParametersResolver<DecryptionParameters> 
        implements DecryptionParametersResolver {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(BasicDecryptionParametersResolver.class);

    /** {@inheritDoc} */
    @Nonnull public Iterable<DecryptionParameters> resolve(@Nullable final CriteriaSet criteria) 
            throws ResolverException {
        
        final DecryptionParameters params = resolveSingle(criteria);
        if (params != null) {
            return CollectionSupport.singletonList(params);
        }
        return CollectionSupport.emptyList();
    }

    /** {@inheritDoc} */
    @Nullable public DecryptionParameters resolveSingle(@Nullable final CriteriaSet criteria) throws ResolverException {
        if (criteria == null) {
            log.debug("CriteriaSet was null");
            return null;
        }

        final DecryptionConfigurationCriterion criterion = Constraint.isNotNull(
                criteria.get(DecryptionConfigurationCriterion.class),
                "Resolver requires an instance of DecryptionConfigurationCriterion");
        
        final DecryptionParameters params = new DecryptionParameters();
        
        resolveAndPopulateIncludesExcludes(params, criteria, criterion.getConfigurations());
        
        params.setDataKeyInfoCredentialResolver(resolveDataKeyInfoCredentialResolver(criteria));
        params.setKEKKeyInfoCredentialResolver(resolveKEKKeyInfoCredentialResolver(criteria));
        params.setEncryptedKeyResolver(resolveEncryptedKeyResolver(criteria));
        params.setRecipients(resolveRecipients(criteria));
        
        logResult(params);
        
        return params;
    }
    
    /**
     * Log the resolved parameters.
     * 
     * @param params the resolved param
     */
    protected void logResult(@Nonnull final DecryptionParameters params) {
        if (log.isDebugEnabled()) {
            log.debug("Resolved DecryptionParameters:");
            
            log.debug("\tAlgorithm includes: {}", params.getIncludedAlgorithms());
            log.debug("\tAlgorithm excludes: {}", params.getExcludedAlgorithms());
            
            log.debug("\tData KeyInfoCredentialResolver: {}", 
                    params.getDataKeyInfoCredentialResolver() != null ? "present" : "null");
            log.debug("\tKEK KeyInfoCredentialResolver: {}", 
                    params.getKEKKeyInfoCredentialResolver() != null ? "present" : "null");
            log.debug("\tEncryptedKeyResolver: {}", 
                    params.getEncryptedKeyResolver() != null ? "present" : "null");

            log.debug("\tRecipients: {}", params.getRecipients());
            
        }
    }

    /**
     * Resolve and return the effective {@link EncryptedKeyResolver}.
     * 
     * @param criteria the input criteria being evaluated
     * 
     * @return the effective resolver, or null
     */
    @Nullable protected EncryptedKeyResolver resolveEncryptedKeyResolver(@Nonnull final CriteriaSet criteria) {
        
        final DecryptionConfigurationCriterion criterion = criteria.get(DecryptionConfigurationCriterion.class);
        assert criterion != null;

        for (final DecryptionConfiguration config : criterion.getConfigurations()) {
            if (config.getEncryptedKeyResolver() != null) {
                return config.getEncryptedKeyResolver();
            }
        }
        return null;
    }

    /**
     * Resolve and return the effective {@link KeyInfoCredentialResolver} used with 
     * {@link org.opensaml.xmlsec.encryption.EncryptedKey} instances.
     * 
     * @param criteria the input criteria being evaluated
     * @return the effective resolver, or null
     */
    @Nullable protected KeyInfoCredentialResolver resolveKEKKeyInfoCredentialResolver(
            @Nonnull final CriteriaSet criteria) {
        
        final DecryptionConfigurationCriterion criterion = criteria.get(DecryptionConfigurationCriterion.class);
        assert criterion != null;

        for (final DecryptionConfiguration config : criterion.getConfigurations()) {
            if (config.getKEKKeyInfoCredentialResolver() != null) {
                return config.getKEKKeyInfoCredentialResolver();
            }
        }
        return null;
    }

    /**
     * Resolve and return the effective {@link KeyInfoCredentialResolver} used with 
     * {@link org.opensaml.xmlsec.encryption.EncryptedData} instances.
     * 
     * @param criteria the input criteria being evaluated
     * @return the effective resolver, or null
     */
    @Nullable protected KeyInfoCredentialResolver resolveDataKeyInfoCredentialResolver(
            @Nonnull final CriteriaSet criteria) {
        
        final DecryptionConfigurationCriterion criterion = criteria.get(DecryptionConfigurationCriterion.class);
        assert criterion != null;

        for (final DecryptionConfiguration config : criterion.getConfigurations()) {
            if (config.getDataKeyInfoCredentialResolver() != null) {
                return config.getDataKeyInfoCredentialResolver();
            }
        }
        return null;
    }

    /**
     * Resolve the effective set of recipients against which to evaluate candidate EncryptedKey elements.
     * 
     * @param criteria the input criteria being evaluated
     * @return the recipients set, or null
     */
    private Set<String> resolveRecipients(@Nonnull final CriteriaSet criteria) {
        final DecryptionConfigurationCriterion configCriterion = criteria.get(DecryptionConfigurationCriterion.class);
        assert configCriterion != null;
        
        final Set<String> recipients = new HashSet<>();

        for (final DecryptionConfiguration config : configCriterion.getConfigurations()) {
            if (config.getRecipients() != null) {
                recipients.addAll(config.getRecipients());
                break;
            }
        }
        
        final DecryptionRecipientsCriterion recipientsCriterion = criteria.get(DecryptionRecipientsCriterion.class);
        if (recipientsCriterion != null) {
            recipients.addAll(recipientsCriterion.getRecipients());
        }

        return recipients.isEmpty() ? null : recipients;
    }

}