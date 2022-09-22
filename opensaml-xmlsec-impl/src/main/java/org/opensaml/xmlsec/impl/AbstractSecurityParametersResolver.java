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

import java.security.Key;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.collection.LazySet;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.Resolver;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.xmlsec.AlgorithmPolicyConfiguration;
import org.opensaml.xmlsec.AlgorithmPolicyConfiguration.Precedence;
import org.opensaml.xmlsec.AlgorithmPolicyParameters;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;

/**
 * Abstract base class for security parameters resolvers which supplies commonly used functionality for reuse.
 * 
 * @param <ProductType> the type of output produced by the resolver
 */
public abstract class AbstractSecurityParametersResolver<ProductType> 
        implements Resolver<ProductType, CriteriaSet>{
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(AbstractSecurityParametersResolver.class);
    
    /**
     * Resolve a {@link KeyInfoGenerator} instance based on a {@link NamedKeyInfoGeneratorManager}, 
     * {@link Credential} and optional KeyInfo generation profile name.
     * 
     * @param credential the credential for which a KeyInfo generator is needed
     * @param manager the named KeyInfo generator manager instance
     * @param keyInfoProfileName KeyInfo generation profile name
     * 
     * @return the resolved KeyInfo generator instance, or null
     */
    @Nullable protected KeyInfoGenerator lookupKeyInfoGenerator(@Nonnull final Credential credential, 
            @Nullable final NamedKeyInfoGeneratorManager manager, @Nullable final String keyInfoProfileName) {
        Constraint.isNotNull(credential, "Credential may not be null");
        
        if (manager == null) {
            log.trace("NamedKeyInfoGeneratorManger was null, can not resolve");
            return null;
        }
        
        if (log.isTraceEnabled()) {
            Key key = CredentialSupport.extractSigningKey(credential);
            if (key == null) {
                key = CredentialSupport.extractEncryptionKey(credential);
            }
            log.trace("Attempting to resolve KeyInfoGenerator for credential with key algo '{}' of impl: {}", 
                    key != null ? key.getAlgorithm() : "n/a", credential.getClass().getName());
        }
        
        return KeyInfoSupport.getKeyInfoGenerator(credential, manager, keyInfoProfileName);
    }
    
    /**
     * Resolve and populate the effective includes or excludes on the supplied instance of 
     * {@link AlgorithmPolicyParameters}.
     * 
     * @param params the include/exclude parameters instance to populate
     * @param criteria the input criteria being evaluated
     * @param configs the effective list of {@link AlgorithmPolicyConfiguration} instances to consider
     */
    protected void resolveAndPopulateIncludesExcludes(@Nonnull final AlgorithmPolicyParameters params, 
            @Nonnull final CriteriaSet criteria, 
            @Nonnull @NonnullElements @NotEmpty final List<? extends AlgorithmPolicyConfiguration> configs) {
        
        final Collection<String> includes = resolveEffectiveIncludes(criteria, configs);
        log.trace("Resolved effective includes: {}", includes);
        
        final Collection<String> excludes = resolveEffectiveExcludes(criteria, configs);
        log.trace("Resolved effective excludes: {}", excludes);
        
        if (includes.isEmpty() && excludes.isEmpty()) {
            log.trace("Both empty, nothing to populate");
            return;
        }
        
        if (includes.isEmpty()) {
            log.trace("Includes empty, populating excludes");
            params.setExcludedAlgorithms(excludes);
            return;
        }
        
        if (excludes.isEmpty()) {
            log.trace("Excludes empty, populating includes");
            params.setIncludedAlgorithms(includes);
            return;
        }
        
        final AlgorithmPolicyConfiguration.Precedence precedence =
                resolveIncludeExcludePrecedence(criteria, configs);
        log.trace("Resolved effective precedence: {}", precedence);
        switch(precedence) {
            case INCLUDE:
                log.trace("Based on precedence, populating includes");
                params.setIncludedAlgorithms(includes);
                break;
            case EXCLUDE:
                log.trace("Based on precedence, populating excludes");
                params.setExcludedAlgorithms(excludes);
                break;
            default:
                throw new IllegalArgumentException("Include/Exclude Precedence value is unknown: " + precedence);
                    
        }
        
    }
    
    /**
     * Get a predicate which operates according to the effective configured include and exclude policy.
     * 
     * @param criteria the input criteria being evaluated
     * @param configs the effective list of {@link AlgorithmPolicyConfiguration} instances to consider
     * 
     * @return a predicate instance which operates accordingly to the effective include and exclude policy
     */
    @Nonnull protected Predicate<String> resolveIncludeExcludePredicate(@Nonnull final CriteriaSet criteria, 
            @Nonnull @NonnullElements @NotEmpty final List<? extends AlgorithmPolicyConfiguration> configs) {
        
        final Collection<String> includes = resolveEffectiveIncludes(criteria, configs);
        log.trace("Resolved effective includes: {}", includes);
        
        final Collection<String> excludes = resolveEffectiveExcludes(criteria, configs);
        log.trace("Resolved effective excludes: {}", excludes);
        
        if (includes.isEmpty() && excludes.isEmpty()) {
            log.trace("Both empty, returning alwaysTrue predicate");
            return Predicates.alwaysTrue();
        }
        
        if (includes.isEmpty()) {
            log.trace("Includes empty, returning ExcludedAlgorithmsPredicate");
            return new ExcludedAlgorithmsPredicate(excludes);
        }
        
        if (excludes.isEmpty()) {
            log.trace("Excludes empty, returning IncludedAlgorithmsPredicate");
            return new IncludedAlgorithmsPredicate(includes);
        }
        
        final AlgorithmPolicyConfiguration.Precedence precedence =
                resolveIncludeExcludePrecedence(criteria, configs);
        log.trace("Resolved effective precedence: {}", precedence);
        switch(precedence) {
            case INCLUDE:
                log.trace("Based on precedence, returning IncludedAlgorithmsPredicate");
                return new IncludedAlgorithmsPredicate(includes);
            case EXCLUDE:
                log.trace("Based on precedence, returning ExcludedAlgorithmsPredicate");
                return new ExcludedAlgorithmsPredicate(excludes);
            default:
                throw new IllegalArgumentException("Include/Exclude Precedence value is unknown: " + precedence);
                    
        }
        
    }

    /**
     * Resolve and return the effective algorithm excludes based on supplied configuration.
     * 
     * @param criteria the input criteria being evaluated
     * @param configs the effective list of {@link AlgorithmPolicyConfiguration} instances to consider
     * 
     * @return the effective algorithm excludes
     */
    @Nonnull protected Collection<String> resolveEffectiveExcludes(@Nonnull final CriteriaSet criteria, 
            @Nonnull @NonnullElements @NotEmpty final List<? extends AlgorithmPolicyConfiguration> configs) {
        
        final LazySet<String> accumulator = new LazySet<>();
        for (final AlgorithmPolicyConfiguration config : configs) {
            accumulator.addAll(config.getExcludedAlgorithms());
            if (!config.isExcludeMerge()) {
                break;
            }
        }
        return accumulator;
    }

    /**
     * Resolve and return the effective algorithm includes based on supplied configuration.
     * 
     * @param criteria the input criteria being evaluated
     * @param configs the effective list of {@link AlgorithmPolicyConfiguration} instances to consider
     * 
     * @return the effective algorithm includes
     */
    @Nonnull protected Collection<String> resolveEffectiveIncludes(@Nonnull final CriteriaSet criteria, 
            @Nonnull @NonnullElements @NotEmpty final List<? extends AlgorithmPolicyConfiguration> configs) {
        
        final LazySet<String> accumulator = new LazySet<>();
        for (final AlgorithmPolicyConfiguration config : configs) {
            accumulator.addAll(config.getIncludedAlgorithms());
            if (!config.isIncludeMerge()) {
                break;
            }
        }
        return accumulator;
    }

    /**
     * Resolve and return the effective algorithm include/exclude precedence based 
     * on supplied configuration.
     * 
     * @param criteria the input criteria being evaluated
     * @param configs the effective list of {@link AlgorithmPolicyConfiguration} instances to consider
     * 
     * @return the effective algorithm include/exclude precedence
     */
    @Nonnull protected Precedence resolveIncludeExcludePrecedence(@Nonnull final CriteriaSet criteria, 
            @Nonnull @NonnullElements @NotEmpty final List<? extends AlgorithmPolicyConfiguration> configs) {
        
        return configs.get(0).getIncludeExcludePrecedence();
    }

}