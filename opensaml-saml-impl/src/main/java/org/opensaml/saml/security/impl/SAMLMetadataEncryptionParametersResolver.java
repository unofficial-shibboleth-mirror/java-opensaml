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

package org.opensaml.saml.security.impl;

import java.security.Key;
import java.security.PublicKey;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.metadata.EncryptionMethod;
import org.opensaml.saml.security.SAMLMetadataKeyAgreementEncryptionConfiguration;
import org.opensaml.saml.security.SAMLMetadataKeyAgreementEncryptionConfiguration.KeyWrap;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialContextSet;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.EncryptionConfiguration;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.KeyTransportAlgorithmPredicate;
import org.opensaml.xmlsec.agreement.KeyAgreementSupport;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.criterion.EncryptionConfigurationCriterion;
import org.opensaml.xmlsec.encryption.KeySize;
import org.opensaml.xmlsec.encryption.MGF;
import org.opensaml.xmlsec.encryption.OAEPparams;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.KeyAgreementEncryptionConfiguration;
import org.opensaml.xmlsec.encryption.support.RSAOAEPParameters;
import org.opensaml.xmlsec.impl.BasicEncryptionParametersResolver;
import org.opensaml.xmlsec.signature.DigestMethod;
import org.slf4j.Logger;

/**
 * A specialization of {@link BasicEncryptionParametersResolver} which resolves
 * credentials and algorithm preferences against SAML metadata via a {@link MetadataCredentialResolver}.
 * 
 * <p>
 * In addition to the {@link net.shibboleth.shared.resolver.Criterion} inputs documented in 
 * {@link BasicEncryptionParametersResolver}, the inputs and associated modes of operation documented for 
 * {@link MetadataCredentialResolver} are also supported and required.
 * </p>
 * 
 * <p>The {@link CriteriaSet} instance passed to the configured metadata credential resolver will be a copy 
 * of the input criteria set, with the addition of a {@link UsageCriterion} containing the value
 * {@link UsageType#ENCRYPTION}, which will replace any existing usage criterion instance.
 * </p>
 * 
 */
public class SAMLMetadataEncryptionParametersResolver extends BasicEncryptionParametersResolver {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(SAMLMetadataEncryptionParametersResolver.class);
    
    /** Metadata credential resolver. */
    @Nonnull private MetadataCredentialResolver credentialResolver;
    
    /**
     * Flag indicating whether the resolver should attempt to merge RSAOAEPParameters
     * values resolved from metadata with additional parameters from supplied instances of
     * {@link org.opensaml.xmlsec.EncryptionConfiguration}.
     */
    private boolean mergeMetadataRSAOAEPParametersWithConfig;
    
    /** Default for usage of key wrapping with key agreement if not otherwise configured. */
    @Nonnull private KeyWrap defaultKeyAgreementUseKeyWrap = KeyWrap.Default;
    
    /**
     * Constructor.
     *
     * @param resolver the metadata credential resolver instance to use to resolve encryption credentials
     */
    public SAMLMetadataEncryptionParametersResolver(
            @Nonnull @ParameterName(name="resolver") final MetadataCredentialResolver resolver) {
        credentialResolver = Constraint.isNotNull(resolver, "MetadataCredentialResoler may not be null");
    }
    
    /**
     * Determine whether the resolver should attempt to merge RSAOAEPParameters values resolved
     * from metadata with additional parameters from supplied instances of
     * {@link org.opensaml.xmlsec.EncryptionConfiguration}.
     * 
     * <p>Defaults to: <code>false</code>
     * 
     * @return true if should merge metadata parameters with configuration, false otherwise
     */
    public boolean isMergeMetadataRSAOAEPParametersWithConfig() {
        return mergeMetadataRSAOAEPParametersWithConfig;
    }

    /**
     * Set whether the resolver should attempt to merge RSAOAEPParameters values resolved
     * from metadata with additional parameters from supplied instances of
     * {@link org.opensaml.xmlsec.EncryptionConfiguration}.
     * 
     * <p>Defaults to: <code>false</code>
     * 
     * @param flag true if should merge metadata parameters with configuration, false otherwise
     */
    public void setMergeMetadataRSAOAEPParametersWithConfig(final boolean flag) {
        mergeMetadataRSAOAEPParametersWithConfig = flag;
    }
    
    /**
     * Get the default for usage of key wrapping with key agreement if not otherwise configured.
     * 
     * <p>
     * The default is: {@link KeyWrap#Default}.
     * </p>
     * 
     * @return the default value
     */
    @Nonnull public KeyWrap getDefaultKeyAgreemenUseKeyWrap() {
        return defaultKeyAgreementUseKeyWrap;
    }
    
    /**
     * Set the default for usage of key wrapping with key agreement if not otherwise configured.
     * 
     * <p>
     * The default is: {@link KeyWrap#Default}.
     * </p>
     * 
     * @param keyWrap the value to set; null implies {@link KeyWrap#Default}
     */
    public void setDefaultKeyAgreementUseKeyWrap(@Nullable final KeyWrap keyWrap) {
        if (keyWrap == null) {
            defaultKeyAgreementUseKeyWrap = KeyWrap.Default;
        } else {
            defaultKeyAgreementUseKeyWrap = keyWrap;
        }
    }

    /**
     * Get the metadata credential resolver instance to use to resolve encryption credentials.
     * 
     * @return the configured metadata credential resolver instance
     */
    @Nonnull protected MetadataCredentialResolver getMetadataCredentialResolver() {
        return credentialResolver;
    }

    /** {@inheritDoc} */
    @Override
    protected void resolveAndPopulateCredentialsAndAlgorithms(@Nonnull final EncryptionParameters params,
            @Nonnull final CriteriaSet criteria, @Nonnull final Predicate<String> includeExcludePredicate) {
        
        // Create a new CriteriaSet for input to the metadata credential resolver, explicitly 
        // setting/forcing an encryption usage criterion.
        final CriteriaSet mdCredResolverCriteria = new CriteriaSet();
        mdCredResolverCriteria.addAll(criteria);
        mdCredResolverCriteria.add(new UsageCriterion(UsageType.ENCRYPTION), true);
        
        // Note: Here we primarily assume that we will resolve a key transport credential from metadata.
        // Even if it's a symmetric key credential (e.g. resolved from a KeyName, etc),
        // it will be used for symmetric key wrap, not direct data encryption.
        // The exception is key agreement (e.g. ECDH), which is handled as a special case and may be
        // either, determined by both metadata and local configuration.
        try {
            for (final Credential credential : getMetadataCredentialResolver().resolve(mdCredResolverCriteria)) {
                assert credential != null;
                
                if (log.isTraceEnabled()) {
                    final Key key = CredentialSupport.extractEncryptionKey(credential);
                    log.trace("Evaluating candidate encryption credential from SAML metadata of type: {}", 
                            key != null ? key.getAlgorithm() : "n/a");
                }
                
                if (checkAndProcessKeyAgreement(params, criteria, includeExcludePredicate, credential)) {
                    return;
                }
                
                final CredentialContextSet credContextSet = credential.getCredentialContextSet();
                final SAMLMDCredentialContext metadataCredContext = 
                        credContextSet != null ? credContextSet.get(SAMLMDCredentialContext.class) : null;
                
                final Pair<String,EncryptionMethod> dataEncryptionAlgorithmAndMethod = resolveDataEncryptionAlgorithm(
                        criteria, includeExcludePredicate, metadataCredContext);
                
                final Pair<String,EncryptionMethod> keyTransportAlgorithmAndMethod = resolveKeyTransportAlgorithm(
                        credential, criteria, includeExcludePredicate, 
                        dataEncryptionAlgorithmAndMethod.getFirst(), metadataCredContext);
                if (keyTransportAlgorithmAndMethod.getFirst() == null) {
                    if (log.isDebugEnabled()) {
                        final Key key = CredentialSupport.extractEncryptionKey(credential); 
                        log.debug("Unable to resolve key transport algorithm for credential with key type '{}', " 
                                + "considering other credentials", 
                                key != null ? key.getAlgorithm() : "n/a");
                    }
                    continue;
                }
                
                params.setKeyTransportEncryptionCredential(credential);
                params.setKeyTransportEncryptionAlgorithm(keyTransportAlgorithmAndMethod.getFirst());
                params.setDataEncryptionAlgorithm(dataEncryptionAlgorithmAndMethod.getFirst());
                
                resolveAndPopulateRSAOAEPParams(params, criteria, includeExcludePredicate, 
                        keyTransportAlgorithmAndMethod.getSecond());
                
                processDataEncryptionCredentialAutoGeneration(params);
                
                return;
            }
        } catch (final ResolverException e) {
            log.warn("Problem resolving credentials from metadata, falling back to local configuration", e);
        }
        
        log.debug("Could not resolve encryption parameters based on SAML metadata, " 
                + "falling back to locally configured credentials and algorithms");
        
        super.resolveAndPopulateCredentialsAndAlgorithms(params, criteria, includeExcludePredicate);
    }
    
// Checkstyle: CyclomaticComplexity OFF
    /**
     * 
     * Check for a credential type that implies a key agreement operation, and process if so indicated.
     * 
     * @param params the params instance being populated
     * @param criteria the input criteria being evaluated
     * @param includeExcludePredicate the include/exclude predicate
     * @param credential the credential being evaluated
     * 
     * @return true if all required parameters were supplied, key agreement was successfully performed,
     *         and the {@link EncryptionParameters} instance's credential and algorithms properties are fully populated,
     *         otherwise false
     */
    protected boolean checkAndProcessKeyAgreement(@Nonnull final EncryptionParameters params,
            @Nonnull final CriteriaSet criteria, @Nonnull final Predicate<String> includeExcludePredicate,
            @Nonnull final Credential credential) {
        
        if (!KeyAgreementSupport.supportsKeyAgreement(credential) ) {
            log.trace("Specified Credential does not support key agreement");
            return false; 
        }
        
        final SAMLMetadataKeyAgreementEncryptionConfiguration config =
                getEffectiveKeyAgreementConfiguration(criteria, credential);
        if (config == null) {
            final PublicKey pkey = credential.getPublicKey();
            log.warn("Unable to get effective KeyAgreementEncryptionConfiguration for credential with key type: {}",
                    pkey != null ? pkey.getAlgorithm() : "n/a");
            return false;
        }
        
        final List<String> criteriaKeyTransportAlgorithms = getEffectiveKeyTransportAlgorithms(criteria,
                includeExcludePredicate);
        
        final List<String> criteriaDataEncryptionAlgorithms = getEffectiveDataEncryptionAlgorithms(criteria, 
                includeExcludePredicate);
        
        final CredentialContextSet credContextSet = credential.getCredentialContextSet();
        final SAMLMDCredentialContext metadataCredContext = 
                credContextSet != null ? credContextSet.get(SAMLMDCredentialContext.class) : null;
        
        List<String> metadataKeyWrapAlgorithms = CollectionSupport.emptyList();
        List<String> metadataDataEncryptionAlgorithms = CollectionSupport.emptyList(); 
        if (metadataCredContext != null) {
            final List<EncryptionMethod> methods = metadataCredContext.getEncryptionMethods();
            if (methods != null) {
                final List<String> metadataAlgorithms = methods.stream()
                        .map(EncryptionMethod::getAlgorithm)
                        .filter(Objects::nonNull)
                        .filter(PredicateSupport.and(getAlgorithmRuntimeSupportedPredicate(), includeExcludePredicate))
                        .collect(Collectors.toList());
                
                metadataKeyWrapAlgorithms = metadataAlgorithms.stream()
                        .filter(AlgorithmSupport::isSymmetricKeyWrap)
                        .collect(Collectors.toList());
                
                metadataDataEncryptionAlgorithms = metadataAlgorithms.stream()
                        .filter(AlgorithmSupport::isBlockEncryption)
                        .collect(Collectors.toList());
            }
        }
        
        log.debug("Evaling useKeyWrap: # key wrap algos '{}', # direct data algos '{}', config '{}'",
                metadataKeyWrapAlgorithms.size(), metadataDataEncryptionAlgorithms.size(),
                config.getMetadataUseKeyWrap());
        
        boolean useKeyWrap = false;
        if (KeyWrap.Never == config.getMetadataUseKeyWrap()) {
            useKeyWrap = false;
        } else if (KeyWrap.Always == config.getMetadataUseKeyWrap() || !metadataKeyWrapAlgorithms.isEmpty()) {
            useKeyWrap = true;
        } else {
            useKeyWrap = metadataDataEncryptionAlgorithms.isEmpty()
                    && KeyWrap.IfNotIndicated == config.getMetadataUseKeyWrap();
        }
        
        return checkAndProcessKeyAgreement(params, criteria, credential,
                concatLists(metadataDataEncryptionAlgorithms, criteriaDataEncryptionAlgorithms),
                useKeyWrap ? concatLists(metadataKeyWrapAlgorithms, criteriaKeyTransportAlgorithms)
                        : CollectionSupport.emptyList());
    }
// Checkstyle: CyclomaticComplexity ON
    
    /**
     * Get the effective {@link SAMLMetadataKeyAgreementEncryptionConfiguration} to use with the specified credential.
     * 
     * @param criteria the criteria
     * @param credential the credential to evaluate
     * @return the key agreement configuration for the credential, or null if could not be resolved
     */
    @Nullable protected SAMLMetadataKeyAgreementEncryptionConfiguration getEffectiveKeyAgreementConfiguration(
            @Nonnull final CriteriaSet criteria, @Nonnull final Credential credential) {
        
        final KeyAgreementEncryptionConfiguration baseConfig =
                super.getEffectiveKeyAgreementConfiguration(criteria, credential);
        if (baseConfig == null) {
            return null;
        }
        
        final SAMLMetadataKeyAgreementEncryptionConfiguration config =
                new SAMLMetadataKeyAgreementEncryptionConfiguration();
        
        config.setAlgorithm(baseConfig.getAlgorithm());
        config.setParameters(baseConfig.getParameters());
        
        final PublicKey pkey = credential.getPublicKey();
        if (pkey == null) {
            log.warn("Key agreement public key was null");
            return null;
        }
        final String keyType = pkey.getAlgorithm();
        
        final EncryptionConfigurationCriterion encryptionConfigCriterion =
                criteria.get(EncryptionConfigurationCriterion.class);
        if (encryptionConfigCriterion == null) {
            log.warn("EncryptionConfigurationCriterion was absent");
            return null;
        }
        
        final List<EncryptionConfiguration> encConfigs = encryptionConfigCriterion.getConfigurations();
        
        config.setMetadataUseKeyWrap(
                encConfigs.stream()
                    .map(c -> c.getKeyAgreementConfigurations().get(keyType))
                    .filter(Objects::nonNull)
                    .filter(SAMLMetadataKeyAgreementEncryptionConfiguration.class::isInstance)
                    .map(SAMLMetadataKeyAgreementEncryptionConfiguration.class::cast)
                    .map(SAMLMetadataKeyAgreementEncryptionConfiguration::getMetadataUseKeyWrap)
                    .filter(Objects::nonNull)
                    .findFirst().orElse(getDefaultKeyAgreemenUseKeyWrap())
                );
        
        return config;
        
    }
    
    /**
     * Concatenate multiple lists into one list.
     * 
     * @param lists the lists to process
     * 
     * @return the concatenation of the supplied lists
     */
    @SafeVarargs
    @Nonnull private List<String> concatLists(@Nonnull final List<String> ... lists) {
        return Stream.of(lists)
                .filter(Objects::nonNull)
                .flatMap(x -> x.stream())
                .collect(Collectors.toList());
    }
    
    /**
     * Resolve and populate an instance of {@link RSAOAEPParameters}, if appropriate for the selected
     * key transport encryption algorithm.
     * 
     * <p>
     * This method itself resolves the parameters data from the metadata {@link EncryptionMethod}.  If
     * this results in a non-complete RSAOAEPParameters instance and if 
     * {@link #isMergeMetadataRSAOAEPParametersWithConfig()} evaluates true, 
     * then the resolver will delegate to the local config resolution process via the superclass
     * to attempt to resolve and merge any null parameter values.
     * (see {@link #resolveAndPopulateRSAOAEPParams(EncryptionParameters, CriteriaSet, Predicate)}).
     * </p>
     * 
     * @param params the current encryption parameters instance being resolved
     * @param criteria  the criteria instance being evaluated
     * @param includeExcludePredicate the include/exclude predicate with which to evaluate the 
     *          candidate data encryption and key transport algorithm URIs
     * @param encryptionMethod the method encryption method that was resolved along with the key transport 
     *          encryption algorithm URI, if any.  May be null.
     */
     //CheckStyle: ReturnCount OFF
     protected void resolveAndPopulateRSAOAEPParams(@Nonnull final EncryptionParameters params, 
             @Nonnull final CriteriaSet criteria, 
             @Nonnull final Predicate<String> includeExcludePredicate, 
             @Nullable final EncryptionMethod encryptionMethod) {
         
         final String alg = params.getKeyTransportEncryptionAlgorithm();
         if (alg == null || !AlgorithmSupport.isRSAOAEP(alg)) {
             return;
         }
         
         if (encryptionMethod == null) {
             super.resolveAndPopulateRSAOAEPParams(params, criteria, includeExcludePredicate);
             return;
         }
         
         RSAOAEPParameters oaepParams = params.getRSAOAEPParameters();
         if (oaepParams == null) {
             oaepParams = new RSAOAEPParameters();
             params.setRSAOAEPParameters(oaepParams);
         }
         
         populateRSAOAEPParamsFromEncryptionMethod(oaepParams, encryptionMethod, includeExcludePredicate);
        
         if (oaepParams.isComplete()) {
             return;
         } else if (oaepParams.isEmpty()) {
             super.resolveAndPopulateRSAOAEPParams(params, criteria, includeExcludePredicate);
         } else {
             if (isMergeMetadataRSAOAEPParametersWithConfig()) {
                 super.resolveAndPopulateRSAOAEPParams(params, criteria, includeExcludePredicate);
             }
         }
    }
//CheckStyle: ReturnCount ON

    /**
     * Extract {@link DigestMethod}, {@link MGF} and {@link OAEPparams} data present on the supplied
     * instance of {@link EncryptionMethod} and populate it on the supplied instance of of 
     * {@link RSAOAEPParameters}.
     * 
     * <p>
     * Include/exclude evaluation is applied to the digest method and MGF algorithm URIs.
     * </p>
     * 
     * @param params the existing RSAOAEPParameters instance being populated
     * @param encryptionMethod the method encryption method that was resolved along with the key transport 
     *          encryption algorithm URI, if any.  May be null.
     * @param includeExcludePredicate the include/exclude predicate with which to evaluate the 
     *          candidate data encryption and key transport algorithm URIs
     */
// Checkstyle: CyclomaticComplexity OFF -- more readable not split up
    protected void populateRSAOAEPParamsFromEncryptionMethod(@Nonnull final RSAOAEPParameters params, 
            @Nonnull final EncryptionMethod encryptionMethod, 
            @Nonnull final Predicate<String> includeExcludePredicate) {
        
        final Predicate<String> algoSupportPredicate = getAlgorithmRuntimeSupportedPredicate();
        
        final List<XMLObject> digestMethods = encryptionMethod.getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME);
        if (digestMethods.size() > 0) {
            final DigestMethod digestMethod = (DigestMethod) digestMethods.get(0);
            final String digestAlgorithm = StringSupport.trimOrNull(digestMethod.getAlgorithm());
            if (digestAlgorithm != null && includeExcludePredicate.test(digestAlgorithm)
                    && algoSupportPredicate.test(digestAlgorithm)) {
                params.setDigestMethod(digestAlgorithm);
            }
        }
        
        if (EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11.equals(encryptionMethod.getAlgorithm())) {
            final List<XMLObject> mgfs = encryptionMethod.getUnknownXMLObjects(MGF.DEFAULT_ELEMENT_NAME);
            if (mgfs.size() > 0) {
                final MGF mgf = (MGF) mgfs.get(0);
                final String mgfAlgorithm = StringSupport.trimOrNull(mgf.getAlgorithm());
                if (mgfAlgorithm != null && includeExcludePredicate.test(mgfAlgorithm)) {
                    params.setMaskGenerationFunction(mgfAlgorithm);
                }
            }
        }
        
        final OAEPparams oaepParams = encryptionMethod.getOAEPparams();
        if (oaepParams != null) {
            final String value = StringSupport.trimOrNull(oaepParams.getValue());
            if (value != null) {
                params.setOAEPparams(value);
            }
        }
        
    }

    /**
     * Determine the key transport algorithm URI to use with the specified credential, also returning the associated
     * {@link EncryptionMethod} from metadata if relevant.
     * 
     * <p>
     * Any algorithms specified in metadata via the passed {@link SAMLMDCredentialContext} are considered first, 
     * followed by locally configured algorithms.
     * </p>
     * 
     * @param keyTransportCredential the key transport credential to evaluate
     * @param criteria  the criteria instance being evaluated
     * @param includeExcludePredicate the include/exclude predicate with which to evaluate the 
     *          candidate data encryption and key transport algorithm URIs
     * @param dataEncryptionAlgorithm the optional data encryption algorithm URI to consider
     * @param metadataCredContext the credential context extracted from metadata
     * @return the selected algorithm URI and the associated encryption method from metadata, if any. 
     */
    @Nonnull protected Pair<String, EncryptionMethod> resolveKeyTransportAlgorithm(
            @Nonnull final Credential keyTransportCredential, 
            @Nonnull final CriteriaSet criteria, @Nonnull final Predicate<String> includeExcludePredicate,
            @Nullable final String dataEncryptionAlgorithm,
            @Nullable final SAMLMDCredentialContext metadataCredContext) {
        
        if (metadataCredContext != null) {
            final KeyTransportAlgorithmPredicate keyTransportPredicate =
                        resolveKeyTransportAlgorithmPredicate(criteria);
            final List<EncryptionMethod> methods = metadataCredContext.getEncryptionMethods();
            if (methods != null) {
                for (final EncryptionMethod encryptionMethod : methods) {
                    final String algorithm = encryptionMethod.getAlgorithm();
                    log.trace("Evaluating SAML metadata EncryptionMethod algorithm for key transport: {}", algorithm);
                    if (algorithm != null
                            && isKeyTransportAlgorithm(algorithm) 
                            && includeExcludePredicate.test(algorithm) 
                            && getAlgorithmRuntimeSupportedPredicate().test(algorithm)
                            && credentialSupportsEncryptionMethod(keyTransportCredential, encryptionMethod)
                            && evaluateEncryptionMethodChildren(encryptionMethod, criteria, includeExcludePredicate)) {
                        
                        boolean accepted = true;
                        if (keyTransportPredicate != null) {
                            accepted = keyTransportPredicate.test(new KeyTransportAlgorithmPredicate.SelectionInput(
                                    algorithm, dataEncryptionAlgorithm, keyTransportCredential));
                        }
                        
                        if (accepted) {
                            log.debug("Resolved key transport algorithm URI from SAML metadata EncryptionMethod: {}",
                                    algorithm);
                            return new Pair<>(algorithm, encryptionMethod);
                        }
                        
                    }
                }
            }
        }
        
        log.debug("Could not resolve key transport algorithm based on SAML metadata, " 
                + "falling back to locally configured algorithms");
        
        return new Pair<>(
                super.resolveKeyTransportAlgorithm(keyTransportCredential, criteria, includeExcludePredicate, 
                        dataEncryptionAlgorithm),
                null);
    }
// Checkstyle:CyclomaticComplexity ON

    /**
     * Determine the data encryption algorithm URI to use, also returning the associated
     * {@link EncryptionMethod} from metadata if relevant.
     * 
     * <p>
     * Any algorithms specified in metadata via the passed {@link SAMLMDCredentialContext} are considered first, 
     * followed by locally configured algorithms.
     * </p>
     * 
     * @param criteria  the criteria instance being evaluated
     * @param includeExcludePredicate the include/exclude predicate with which to evaluate the 
     *          candidate data encryption and key transport algorithm URIs
     * @param metadataCredContext the credential context extracted from metadata
     * @return the selected algorithm URI and the associated encryption method from metadata, if any
     */
    @Nonnull protected Pair<String, EncryptionMethod> resolveDataEncryptionAlgorithm(
            @Nonnull final CriteriaSet criteria, 
            @Nonnull final Predicate<String> includeExcludePredicate,
            @Nullable final SAMLMDCredentialContext metadataCredContext) {
        
        if (metadataCredContext != null) {
            final List<EncryptionMethod> methods = metadataCredContext.getEncryptionMethods();
            if (methods != null) {
                for (final EncryptionMethod encryptionMethod : methods) {
                    final String algorithm = encryptionMethod.getAlgorithm();
                    log.trace("Evaluating SAML metadata EncryptionMethod algorithm for data encryption: {}", algorithm);
                    if (isDataEncryptionAlgorithm(algorithm) 
                            && includeExcludePredicate.test(algorithm)
                            && getAlgorithmRuntimeSupportedPredicate().test(algorithm)
                            && evaluateEncryptionMethodChildren(encryptionMethod, criteria, includeExcludePredicate)) {
                        log.debug("Resolved data encryption algorithm URI from SAML metadata EncryptionMethod: {}",
                                algorithm);
                        return new Pair<>(algorithm, encryptionMethod);
                    }
                }
            }
        }
        
        log.debug("Could not resolve data encryption algorithm based on SAML metadata, " 
                + "falling back to locally configured algorithms");
        
        return new Pair<>(
                super.resolveDataEncryptionAlgorithm(null, criteria, includeExcludePredicate),
                null);
    }

    /**
     * Evaluate the child elements of an EncryptionMethod for acceptability based on for example
     * include/exclude policy and algorithm runtime support.
     * 
     * @param encryptionMethod the EncryptionMethod being evaluated
     * @param criteria  the criteria instance being evaluated
     * @param includeExcludePredicate the include/exclude predicate with which to evaluate the 
     *          candidate data encryption and key transport algorithm URIs
     *          
     * @return true if the EncryptionMethod children are acceptable
     */
    protected boolean evaluateEncryptionMethodChildren(@Nonnull final EncryptionMethod encryptionMethod, 
            @Nonnull final CriteriaSet criteria, @Nonnull final Predicate<String> includeExcludePredicate) {
        
        final String alg = encryptionMethod.getAlgorithm();
        if (alg == null) {
            return false;
        }
        
        switch(alg) {
            
            case EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP:
            case EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11:
                return evaluateRSAOAEPChildren(encryptionMethod, criteria, includeExcludePredicate);
                
            default:
                return true;
        }
        
    }

    /**
     * Evaluate the child elements of an RSA OAEP EncryptionMethod for acceptability based on for example
     * include/exclude policy and algorithm runtime support.
     * 
     * @param encryptionMethod the EncryptionMethod being evaluated
     * @param criteria  the criteria instance being evaluated
     * @param includeExcludePredicate the include/exclude predicate with which to evaluate the 
     *          candidate data encryption and key transport algorithm URIs
     *          
     * @return true if the EncryptionMethod children are acceptable
     */
    protected boolean evaluateRSAOAEPChildren(@Nonnull final EncryptionMethod encryptionMethod, 
            @Nonnull final CriteriaSet criteria, @Nonnull final Predicate<String> includeExcludePredicate) {
        
        final Predicate<String> algoSupportPredicate = getAlgorithmRuntimeSupportedPredicate();
        
        final List<XMLObject> digestMethods = encryptionMethod.getUnknownXMLObjects(DigestMethod.DEFAULT_ELEMENT_NAME);
        if (digestMethods.size() > 0) {
            final DigestMethod digestMethod = (DigestMethod) digestMethods.get(0);
            final String digestAlgorithm = StringSupport.trimOrNull(digestMethod.getAlgorithm());
            if (digestAlgorithm != null) {
                if (!includeExcludePredicate.test(digestAlgorithm) 
                        || !algoSupportPredicate.test(digestAlgorithm)) {
                    log.debug("Rejecting RSA OAEP EncryptionMethod due to unsupported or disallowed DigestMethod: {}",
                            digestAlgorithm);
                    return false;
                }
            }
        }
        
        if (EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11.equals(encryptionMethod.getAlgorithm())) {
            final List<XMLObject> mgfs = encryptionMethod.getUnknownXMLObjects(MGF.DEFAULT_ELEMENT_NAME);
            if (mgfs.size() > 0) {
                final MGF mgf = (MGF) mgfs.get(0);
                final String mgfAlgorithm = StringSupport.trimOrNull(mgf.getAlgorithm());
                if (mgfAlgorithm != null) {
                    if (!includeExcludePredicate.test(mgfAlgorithm)) {
                        log.debug("Rejecting RSA OAEP EncryptionMethod due to disallowed MGF: {}", mgfAlgorithm);
                        return false;
                    }
                }
            }
        }
        
        return true;
    }

    /**
     * Evaluate whether the specified credential is supported for use with the specified {@link EncryptionMethod}.
     * 
     * @param credential the credential to evaluate
     * @param encryptionMethod the encryption method to evaluate
     * @return true if credential may be used with the supplied encryption method, false otherwise
     */
    protected boolean credentialSupportsEncryptionMethod(@Nonnull final Credential credential, 
            @Nonnull final EncryptionMethod encryptionMethod) {
        
        final String alg = encryptionMethod.getAlgorithm();
        if (alg == null || !credentialSupportsAlgorithm(credential, alg)) {
            return false;
        }
        
        final KeySize keySize = encryptionMethod.getKeySize(); 
        if (keySize != null && keySize.getValue() != null) {
            final Key encryptionKey = CredentialSupport.extractEncryptionKey(credential);
            if (encryptionKey == null) {
                log.warn("Could not extract encryption key from credential. Failing evaluation");
                return false;
            }
            
            final Integer keyLength = KeySupport.getKeyLength(encryptionKey);
            if (keyLength == null) {
                log.warn("Could not determine key length of candidate encryption credential. Failing evaluation");
                return false;
            }
        
            if (!keyLength.equals(keySize.getValue())) {
                return false;
            }
        }
        
        //TODO anything else?  OAEPParams?
        
        return true;
    }

}