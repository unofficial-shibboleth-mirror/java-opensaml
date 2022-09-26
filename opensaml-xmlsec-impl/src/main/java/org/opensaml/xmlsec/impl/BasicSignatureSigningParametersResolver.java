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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.SignatureSigningParametersResolver;
import org.opensaml.xmlsec.algorithm.AlgorithmRegistry;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.criterion.KeyInfoGenerationProfileCriterion;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of {@link SignatureSigningParametersResolver}.
 * 
 * <p>
 * The following {@link net.shibboleth.shared.resolver.Criterion} inputs are supported:
 * </p>
 * <ul>
 * <li>{@link SignatureSigningConfigurationCriterion} - required</li> 
 * <li>{@link KeyInfoGenerationProfileCriterion} - optional</li> 
 * </ul>
 */
public class BasicSignatureSigningParametersResolver 
        extends AbstractSecurityParametersResolver<SignatureSigningParameters> 
        implements SignatureSigningParametersResolver {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(BasicSignatureSigningParametersResolver.class);
    
    /** The AlgorithmRegistry used when processing algorithm URIs. */
    private AlgorithmRegistry algorithmRegistry;
    
    /** Constructor. */
    public BasicSignatureSigningParametersResolver() {
        algorithmRegistry = AlgorithmSupport.getGlobalAlgorithmRegistry();
    }

    /**
     * Get the {@link AlgorithmRegistry} instance used when resolving algorithm URIs. Defaults to
     * the registry obtained via {@link AlgorithmSupport#getGlobalAlgorithmRegistry()}.
     * 
     * @return the algorithm registry instance
     */
    public AlgorithmRegistry getAlgorithmRegistry() {
        // Handle case where this resolver was constructed before the library was properly initialized.
        if (algorithmRegistry == null) {
            return AlgorithmSupport.getGlobalAlgorithmRegistry();
        }
        return algorithmRegistry;
    }

    /**
     * Set the {@link AlgorithmRegistry} instance used when resolving algorithm URIs. Defaults to
     * the registry obtained via {@link AlgorithmSupport#getGlobalAlgorithmRegistry()}.
     * 
     * @param registry the new algorithm registry instance
     */
    public void setAlgorithmRegistry(@Nonnull final AlgorithmRegistry registry) {
        algorithmRegistry = Constraint.isNotNull(registry, "AlgorithmRegistry was null");
    }

    /** {@inheritDoc} */
    @Nonnull
    public Iterable<SignatureSigningParameters> resolve(@Nonnull final CriteriaSet criteria) throws ResolverException {
        final SignatureSigningParameters params = resolveSingle(criteria);
        if (params != null) {
            return Collections.singletonList(params);
        }
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Nullable
    public SignatureSigningParameters resolveSingle(@Nonnull final CriteriaSet criteria) throws ResolverException {
        Constraint.isNotNull(criteria, "CriteriaSet was null");
        Constraint.isNotNull(criteria.get(SignatureSigningConfigurationCriterion.class), 
                "Resolver requires an instance of SignatureSigningConfigurationCriterion");
        
        final Predicate<String> includeExcludePredicate = getIncludeExcludePredicate(criteria);
        
        final SignatureSigningParameters params = new SignatureSigningParameters();
        
        resolveAndPopulateCredentialAndSignatureAlgorithm(params, criteria, includeExcludePredicate);
        
        params.setSignatureReferenceDigestMethod(resolveReferenceDigestMethod(criteria, includeExcludePredicate));
        params.setSignatureReferenceCanonicalizationAlgorithm(resolveReferenceCanonicalizationAlgorithm(criteria));
        
        params.setSignatureCanonicalizationAlgorithm(resolveCanonicalizationAlgorithm(criteria));
        
        if (params.getSigningCredential() != null) {
            params.setKeyInfoGenerator(resolveKeyInfoGenerator(criteria, params.getSigningCredential()));
            params.setSignatureHMACOutputLength(resolveHMACOutputLength(criteria, params.getSigningCredential(), 
                    params.getSignatureAlgorithm()));
        }
        
        if (validate(params)) {
            logResult(params);
            return params;
        }
        return null;
    }
    
    /**
     * Log the resolved parameters.
     * 
     * @param params the resolved param
     */
    protected void logResult(@Nonnull final SignatureSigningParameters params) {
        if (log.isDebugEnabled()) {
            log.debug("Resolved SignatureSigningParameters:");
            
            final Key signingKey = CredentialSupport.extractSigningKey(params.getSigningCredential());
            if (signingKey != null) {
                log.debug("\tSigning credential with key algorithm: {}", signingKey.getAlgorithm());
            } else {
                log.debug("\tSigning credential: null"); 
            }
            
            log.debug("\tSignature algorithm URI: {}", params.getSignatureAlgorithm()); 
            
            log.debug("\tSignature KeyInfoGenerator: {}", params.getKeyInfoGenerator() != null ?
                    params.getKeyInfoGenerator().getClass().getName() : "null");
            
            log.debug("\tReference digest method algorithm URI: {}", params.getSignatureReferenceDigestMethod()); 
            log.debug("\tReference canonicalization algorithm URI: {}", 
                    params.getSignatureReferenceCanonicalizationAlgorithm()); 
            
            log.debug("\tCanonicalization algorithm URI: {}", params.getSignatureCanonicalizationAlgorithm()); 
            log.debug("\tHMAC output length: {}", params.getSignatureHMACOutputLength()); 
        }
    }
    
    /**
     * Validate that the {@link SignatureSigningParameters} instance has all the required properties populated.
     * 
     * @param params the parameters instance to evaluate
     * 
     * @return true if parameters instance passes validation, false otherwise
     */
    protected boolean validate(@Nonnull final SignatureSigningParameters params) {
        if (params.getSigningCredential() == null) {
            log.warn("Validation failure: Unable to resolve signing credential");
            return false;
        }
        if (params.getSignatureAlgorithm() == null) {
            log.warn("Validation failure: Unable to resolve signing algorithm URI");
            return false;
        }
        if (params.getSignatureCanonicalizationAlgorithm() == null) {
            log.warn("Validation failure: Unable to resolve signing canonicalization algorithm URI");
            return false;
        }
        if (params.getSignatureReferenceDigestMethod() == null) {
            log.warn("Validation failure: Unable to resolve reference digest algorithm URI");
            return false;
        }
        return true;
    }

    /**
     * Get a predicate which implements the effective configured include/exclude policy.
     * 
     * @param criteria the input criteria being evaluated
     * 
     * @return include/exclude predicate instance
     */
    @Nonnull protected Predicate<String> getIncludeExcludePredicate(@Nonnull final CriteriaSet criteria) {
        return resolveIncludeExcludePredicate(criteria, 
                criteria.get(SignatureSigningConfigurationCriterion.class).getConfigurations());
    }

    /**
     * Get a predicate which implements the effective configured include/exclude policy.
     * 
     * @param criteria the input criteria being evaluated
     * 
     * @return include/exclude predicate instance
     * 
     * @deprecated
     */
    @Deprecated(since="4.1.0",forRemoval=true)
    @Nonnull protected Predicate<String> getWhitelistBlacklistPredicate(@Nonnull final CriteriaSet criteria) {
        return getIncludeExcludePredicate(criteria);
    }

    /**
     * Resolve and populate the signing credential and signature method algorithm URI on the 
     * supplied parameters instance.
     * 
     * @param params the parameters instance being populated
     * @param criteria the input criteria being evaluated
     * @param includeExcludePredicate the include/exclude predicate with which to evaluate the 
     *          candidate signing method algorithm URIs
     */
    protected void resolveAndPopulateCredentialAndSignatureAlgorithm(@Nonnull final SignatureSigningParameters params, 
            @Nonnull final CriteriaSet criteria, final Predicate<String> includeExcludePredicate) {
        
        final List<Credential> credentials = getEffectiveSigningCredentials(criteria);
        final List<String> algorithms = getEffectiveSignatureAlgorithms(criteria, includeExcludePredicate);
        log.trace("Resolved effective signature algorithms: {}", algorithms);
        
        for (final Credential credential : credentials) {
            if (log.isTraceEnabled()) {
                final Key key = CredentialSupport.extractSigningKey(credential);
                log.trace("Evaluating credential of type: {}", key != null ? key.getAlgorithm() : "n/a");
            }
            for (final String algorithm : algorithms) {
                log.trace("Evaluating credential against algorithm: {}", algorithm);
                if (credentialSupportsAlgorithm(credential, algorithm)) {
                    log.trace("Credential passed eval against algorithm: {}", algorithm);
                    params.setSigningCredential(credential);
                    params.setSignatureAlgorithm(algorithm);
                    return;
                }
                log.trace("Credential failed eval against algorithm: {}", algorithm);
            }
        }
        
    }
    
    /**
     * Get a predicate which evaluates whether a cryptographic algorithm is supported
     * by the runtime environment.
     * 
     * @return the predicate
     */
    @Nonnull protected Predicate<String> getAlgorithmRuntimeSupportedPredicate() {
        return new AlgorithmRuntimeSupportedPredicate(getAlgorithmRegistry());
    }

    /**
     * Evaluate whether the specified credential is supported for use with the specified algorithm URI.
     * 
     * @param credential the credential to evaluate
     * @param algorithm the algorithm URI to evaluate
     * @return true if credential may be used with the supplied algorithm URI, false otherwise
     */
    protected boolean credentialSupportsAlgorithm(@Nonnull final Credential credential, 
            @Nonnull @NotEmpty final String algorithm) {
        
        return AlgorithmSupport.credentialSupportsAlgorithmForSigning(credential, 
                getAlgorithmRegistry().get(algorithm));
    }

    /**
     * Get the effective list of signing credentials to consider.
     * 
     * @param criteria the input criteria being evaluated
     * @return the list of credentials
     */
    @Nonnull protected List<Credential> getEffectiveSigningCredentials(@Nonnull final CriteriaSet criteria) {
        final ArrayList<Credential> accumulator = new ArrayList<>();
        for (final SignatureSigningConfiguration config : criteria.get(SignatureSigningConfigurationCriterion.class)
                .getConfigurations()) {
            
            accumulator.addAll(config.getSigningCredentials());
            
        }
        return accumulator;
    }
    
    /**
     * Get the effective list of signature algorithm URIs to consider, including application of 
     * include/exclude policy.
     * 
     * @param criteria the input criteria being evaluated
     * @param includeExcludePredicate  the include/exclude predicate to use
     * @return the list of effective algorithm URIs
     */
    @Nonnull protected List<String> getEffectiveSignatureAlgorithms(@Nonnull final CriteriaSet criteria, 
            @Nonnull final Predicate<String> includeExcludePredicate) {
        final ArrayList<String> accumulator = new ArrayList<>();
        for (final SignatureSigningConfiguration config : criteria.get(SignatureSigningConfigurationCriterion.class)
                .getConfigurations()) {
            
            config.getSignatureAlgorithms()
                .stream()
                .filter(PredicateSupport.and(getAlgorithmRuntimeSupportedPredicate(), includeExcludePredicate))
                .forEach(accumulator::add);
        }
        return accumulator;
    }

    /**
     * Resolve and return the digest method algorithm URI to use, including application of include/exclude policy.
     * 
     * @param criteria the input criteria being evaluated
     * @param includeExcludePredicate  the include/exclude predicate to use
     * @return the resolved digest method algorithm URI
     */
    @Nullable protected String resolveReferenceDigestMethod(@Nonnull final CriteriaSet criteria, 
            @Nonnull final Predicate<String> includeExcludePredicate) {
        for (final SignatureSigningConfiguration config : criteria.get(SignatureSigningConfigurationCriterion.class)
                .getConfigurations()) {
            
            for (final String digestMethod : config.getSignatureReferenceDigestMethods()) {
                if (getAlgorithmRuntimeSupportedPredicate().test(digestMethod) 
                        && includeExcludePredicate.test(digestMethod)) {
                    return digestMethod;
                }
            }
            
        }
        return null;
    }

    /**
     * Resolve and return the canonicalization algorithm URI to use.
     * 
     * @param criteria the input criteria being evaluated
     * @return the canonicalization algorithm URI
     */
    @Nullable protected String resolveCanonicalizationAlgorithm(@Nonnull final CriteriaSet criteria) {
        for (final SignatureSigningConfiguration config : criteria.get(SignatureSigningConfigurationCriterion.class)
                .getConfigurations()) {
            
            if (config.getSignatureCanonicalizationAlgorithm() != null) {
                return config.getSignatureCanonicalizationAlgorithm();
            }
            
        }
        return null;
    }
    
    /**
     * Resolve and return the reference canonicalization algorithm URI to use.
     * 
     * @param criteria the input criteria being evaluated
     * @return the reference canonicalization algorithm URI
     */
    @Nullable protected String resolveReferenceCanonicalizationAlgorithm(@Nonnull final CriteriaSet criteria) {
        for (final SignatureSigningConfiguration config : criteria.get(SignatureSigningConfigurationCriterion.class)
                .getConfigurations()) {
            
            if (config.getSignatureReferenceCanonicalizationAlgorithm() != null) {
                return config.getSignatureReferenceCanonicalizationAlgorithm();
            }
            
        }
        return null;
    }

    /**
     * Resolve and return the {@link KeyInfoGenerator} instance to use with the specified credential.
     * 
     * @param criteria the input criteria being evaluated
     * @param signingCredential the credential being evaluated
     * @return KeyInfo generator instance, or null
     */
    @Nullable protected KeyInfoGenerator resolveKeyInfoGenerator(@Nonnull final CriteriaSet criteria, 
            @Nonnull final Credential signingCredential) {
        
        String name = null;
        if (criteria.get(KeyInfoGenerationProfileCriterion.class) != null) {
            name = criteria.get(KeyInfoGenerationProfileCriterion.class).getName();
        }
        
        for (final SignatureSigningConfiguration config : criteria.get(SignatureSigningConfigurationCriterion.class)
                .getConfigurations()) {
            
            final KeyInfoGenerator kig =
                    lookupKeyInfoGenerator(signingCredential, config.getKeyInfoGeneratorManager(), name);
            if (kig != null) {
                return kig;
            }
            
        }
        
        return null;
    }

    /**
     * Resolve and return the effective HMAC output length to use, if applicable to the specified signing credential
     * and signature method algorithm URI.
     * 
     * @param criteria the input criteria being evaluated
     * @param signingCredential the signing credential being evaluated
     * @param algorithmURI the signature method algorithm URI being evaluated
     * @return the HMAC output length to use, or null
     */
    @Nullable protected Integer resolveHMACOutputLength(@Nonnull final CriteriaSet criteria, 
            @Nonnull final Credential signingCredential, @Nonnull @NotEmpty final String algorithmURI) {
        
        if (AlgorithmSupport.isHMAC(algorithmURI)) {
            for (final SignatureSigningConfiguration config : criteria.get(SignatureSigningConfigurationCriterion.class)
                    .getConfigurations()) {
                if (config.getSignatureHMACOutputLength() != null) {
                    return config.getSignatureHMACOutputLength();
                }
            }
        }
        return null;
    }

}
