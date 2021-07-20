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

package org.opensaml.xmlsec.config.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Nonnull;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.xmlsec.agreement.impl.DigestMethod;
import org.opensaml.xmlsec.agreement.impl.KANonce;
import org.opensaml.xmlsec.derivation.impl.ConcatKDF;
import org.opensaml.xmlsec.derivation.impl.PBKDF2;
import org.opensaml.xmlsec.encryption.support.ChainingEncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.InlineEncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.KeyAgreementEncryptionConfiguration;
import org.opensaml.xmlsec.encryption.support.RSAOAEPParameters;
import org.opensaml.xmlsec.encryption.support.SimpleKeyInfoReferenceEncryptedKeyResolver;
import org.opensaml.xmlsec.encryption.support.SimpleRetrievalMethodEncryptedKeyResolver;
import org.opensaml.xmlsec.impl.BasicDecryptionConfiguration;
import org.opensaml.xmlsec.impl.BasicEncryptionConfiguration;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.opensaml.xmlsec.impl.BasicSignatureValidationConfiguration;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.impl.BasicProviderKeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.KeyAgreementKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.impl.KeyInfoProvider;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.impl.provider.DEREncodedKeyValueProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.DSAKeyValueProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.ECKeyValueProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.InlineX509DataProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.RSAKeyValueProvider;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

/**
 * A utility class which programmatically builds basic instances of various components 
 * related to security configuration which have reasonable default values for their 
 * various configuration parameters.
 */
public class DefaultSecurityConfigurationBootstrap {
    
    /** Config property name for ECDH default Key Derivation Function (KDF). */
    public static final String CONFIG_PROPERTY_ECDH_DEFAULT_KDF = "opensaml.config.ecdh.defaultKDF";
    
    /** Config property value for default KDF: ConcatKDF. */
    public static final String CONCATKDF = "ConcatKDF";
    
    /** Config property value for default KDF: PBKDF2. */
    public static final String PBKDF2 = "PBKDF2";
    
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSecurityConfigurationBootstrap.class);
    
    /** Constructor. */
    protected DefaultSecurityConfigurationBootstrap() {}
    
    /**
     * Build and return a default encryption configuration.
     * 
     * @return a new basic configuration with reasonable default values
     */
    @Nonnull public static BasicEncryptionConfiguration buildDefaultEncryptionConfiguration() {
        final BasicEncryptionConfiguration config = new BasicEncryptionConfiguration();
        
        config.setExcludedAlgorithms(Collections.singletonList(
                EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15
                ));
        
        config.setDataEncryptionAlgorithms(List.of(
                // The order of these is significant.
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES
                ));
        
        config.setKeyTransportEncryptionAlgorithms(List.of(
                // The order of the RSA algos is significant.
                EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP,
                
                // The order of these is only significant when doing key agreement with key wrap.
                // Otherwise the order is not significant, they just need to be registered 
                // so that they can be used if a credential with a key of that type and size is seen.
                EncryptionConstants.ALGO_ID_KEYWRAP_AES128,
                EncryptionConstants.ALGO_ID_KEYWRAP_AES192,
                EncryptionConstants.ALGO_ID_KEYWRAP_AES256,
                EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES
                ));
        
        config.setRSAOAEPParameters(new RSAOAEPParameters(
                SignatureConstants.ALGO_ID_DIGEST_SHA1, 
                EncryptionConstants.ALGO_ID_MGF1_SHA1, 
                null
                ));
        
        config.setKeyAgreementConfigurations(buildKeyAgreementConfigurations());
        
        config.setDataKeyInfoGeneratorManager(buildDataEncryptionKeyInfoGeneratorManager());
        config.setKeyTransportKeyInfoGeneratorManager(buildKeyTransportEncryptionKeyInfoGeneratorManager());
        
        return config;
    }
    
    /**
     * Build key agreement configurations.
     * 
     * @return key agreement configurations.
     */
    @Nonnull protected static Map<String, KeyAgreementEncryptionConfiguration> buildKeyAgreementConfigurations() {
        
        final Map<String, KeyAgreementEncryptionConfiguration> kaConfigs = new HashMap<>();
        try {
            
            final Properties props = ConfigurationService.getConfigurationProperties(); 
            
            final KeyAgreementEncryptionConfiguration ecConfig = new KeyAgreementEncryptionConfiguration();
            ecConfig.setAlgorithm(EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
            
            final String ecKDF = 
                    props != null ? props.getProperty(CONFIG_PROPERTY_ECDH_DEFAULT_KDF, CONCATKDF) : CONCATKDF;
                    
            if (CONCATKDF.equals(ecKDF)) {
                final ConcatKDF ecConcatKDF = new ConcatKDF();
                // Need to set these 3 to something to confirm to NIST spec requirements. Actual deployments
                // can and should override in a custom config with specific parameter values, if needed.
                ecConcatKDF.setAlgorithmID("00");
                ecConcatKDF.setPartyUInfo("00");
                ecConcatKDF.setPartyVInfo("00");
                ecConcatKDF.initialize();
                ecConfig.setParameters(Set.of(ecConcatKDF));
            } else if (PBKDF2.equals(ecKDF)) {
                final PBKDF2 ecPBKDF2 = new PBKDF2();
                ecPBKDF2.initialize();
                ecConfig.setParameters(Set.of(ecPBKDF2));
            } else {
                LOG.warn("Saw unknown value for ECDH KDF '{}', omitting global ECDH KDF configuration", ecKDF);
                ecConfig.setParameters(Collections.emptySet());
            }
            kaConfigs.put(JCAConstants.KEY_ALGO_EC, ecConfig);
            
            // For DH we default the Legacy KDF variant as that is mandatory for DH support.
            final KeyAgreementEncryptionConfiguration dhConfig = new KeyAgreementEncryptionConfiguration();
            dhConfig.setAlgorithm(EncryptionConstants.ALGO_ID_KEYAGREEMENT_DH);
            final DigestMethod digestMethod = new DigestMethod();
            digestMethod.setAlgorithm(EncryptionConstants.ALGO_ID_DIGEST_SHA256);
            digestMethod.initialize();
            final KANonce nonce = new KANonce();
            // This will use an auto-generated nonce value each time
            nonce.initialize();
            dhConfig.setParameters(Set.of(digestMethod, nonce));
            kaConfigs.put(JCAConstants.KEY_ALGO_DH, dhConfig);
            
        } catch (final ComponentInitializationException e) {
            LOG.error("Initialization failure on global key agreement encryption configuration, will be unusable", e);
        }
        
        return kaConfigs;
    }
    
    /**
     * Build and return a default decryption configuration.
     * 
     * @return a new basic configuration with reasonable default values
     */
    @Nonnull public static BasicDecryptionConfiguration buildDefaultDecryptionConfiguration() {
        final BasicDecryptionConfiguration config = new BasicDecryptionConfiguration();
        
        config.setExcludedAlgorithms(Collections.singletonList(
                EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15
                ));
        
        config.setEncryptedKeyResolver(buildBasicEncryptedKeyResolver());
        
        return config;
    }

    /**
     * Build and return a default signature signing configuration.
     * 
     * @return a new basic configuration with reasonable default values
     */
    @Nonnull public static BasicSignatureSigningConfiguration buildDefaultSignatureSigningConfiguration() {
        final BasicSignatureSigningConfiguration config = new BasicSignatureSigningConfiguration();
        
        config.setExcludedAlgorithms(List.of(
                SignatureConstants.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5,
                SignatureConstants.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5,
                SignatureConstants.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5
                ));
        
        config.setSignatureAlgorithms(List.of(
                // The order within each key group is significant.
                // The order of the key groups themselves is not significant.
                
                // RSA
                SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256,
                SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA384,
                SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512,
                SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1,
                
                // ECDSA
                SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA256,
                SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA384,
                SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA512,
                SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1,
                
                // DSA
                SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1,
                
                // HMAC (all symmetric keys)
                SignatureConstants.ALGO_ID_MAC_HMAC_SHA256,
                SignatureConstants.ALGO_ID_MAC_HMAC_SHA384,
                SignatureConstants.ALGO_ID_MAC_HMAC_SHA512,
                SignatureConstants.ALGO_ID_MAC_HMAC_SHA1
                ));
        
        config.setSignatureReferenceDigestMethods(List.of(
                // The order of these is significant.
                SignatureConstants.ALGO_ID_DIGEST_SHA256,
                SignatureConstants.ALGO_ID_DIGEST_SHA384,
                SignatureConstants.ALGO_ID_DIGEST_SHA512,
                SignatureConstants.ALGO_ID_DIGEST_SHA1
                ));
        
        config.setSignatureCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        
        config.setKeyInfoGeneratorManager(buildSignatureKeyInfoGeneratorManager());
        
        return config;
    }
    
    /**
     * Build and return a default signature validation configuration.
     * 
     * @return a new basic configuration with reasonable default values
     */
    @Nonnull public static BasicSignatureValidationConfiguration buildDefaultSignatureValidationConfiguration() {
        final BasicSignatureValidationConfiguration config = new BasicSignatureValidationConfiguration();
        
        config.setExcludedAlgorithms(List.of(
                SignatureConstants.ALGO_ID_DIGEST_NOT_RECOMMENDED_MD5,
                SignatureConstants.ALGO_ID_SIGNATURE_NOT_RECOMMENDED_RSA_MD5,
                SignatureConstants.ALGO_ID_MAC_HMAC_NOT_RECOMMENDED_MD5
                ));
        
        return config;
    }
    
    /**
     * Build a basic instance of {@link EncryptedKeyResolver}.
     * 
     * @return an EncryptedKey resolver instance
     */
    protected static EncryptedKeyResolver buildBasicEncryptedKeyResolver() {
        final List<EncryptedKeyResolver> resolverChain = new ArrayList<>();
        resolverChain.add(new InlineEncryptedKeyResolver()); 
        resolverChain.add(new SimpleRetrievalMethodEncryptedKeyResolver());
        resolverChain.add(new SimpleKeyInfoReferenceEncryptedKeyResolver());
        
        return new ChainingEncryptedKeyResolver(resolverChain);
    }

    /**
     * Build a basic instance of {@link KeyInfoCredentialResolver}.
     * 
     * @return a KeyInfo credential resolver instance
     */
    public static KeyInfoCredentialResolver buildBasicInlineKeyInfoCredentialResolver() {
        // Basic resolver for inline info
        final ArrayList<KeyInfoProvider> providers = new ArrayList<>();
        providers.add( new RSAKeyValueProvider() );
        providers.add( new DSAKeyValueProvider() );
        providers.add( new ECKeyValueProvider() );
        providers.add( new DEREncodedKeyValueProvider() );
        providers.add( new InlineX509DataProvider() );
        
        final KeyInfoCredentialResolver resolver = new BasicProviderKeyInfoCredentialResolver(providers);
        return resolver;
    }

    /**
     * Build a basic {@link NamedKeyInfoGeneratorManager} for use when generating an
     * {@link org.opensaml.xmlsec.encryption.EncryptedData}.
     * 
     * @return a named KeyInfo generator manager instance
     */
    protected static NamedKeyInfoGeneratorManager buildDataEncryptionKeyInfoGeneratorManager() {
        // Generator for KeyAgreementCredentials. This factory already defaults the usually desired settings.
        final KeyAgreementKeyInfoGeneratorFactory keyAgreementFactory = new KeyAgreementKeyInfoGeneratorFactory();
        
        final NamedKeyInfoGeneratorManager manager = buildBasicKeyInfoGeneratorManager();
        manager.getDefaultManager().registerFactory(keyAgreementFactory);
        return manager;
    }
    
    /**
     * Build a basic {@link NamedKeyInfoGeneratorManager} for use when generating an
     * {@link org.opensaml.xmlsec.encryption.EncryptedKey}.
     * 
     * @return a named KeyInfo generator manager instance
     */
    protected static NamedKeyInfoGeneratorManager buildKeyTransportEncryptionKeyInfoGeneratorManager() {
        // Generator for KeyAgreementCredentials. This factory already defaults the usually desired settings.
        final KeyAgreementKeyInfoGeneratorFactory keyAgreementFactory = new KeyAgreementKeyInfoGeneratorFactory();
        
        final NamedKeyInfoGeneratorManager manager = buildBasicKeyInfoGeneratorManager();
        manager.getDefaultManager().registerFactory(keyAgreementFactory);
        return manager;
    }
    
    /**
     * Build a basic {@link NamedKeyInfoGeneratorManager} for use when generating an
     * {@link org.opensaml.xmlsec.signature.Signature}.
     * 
     * @return a named KeyInfo generator manager instance
     */
    protected static NamedKeyInfoGeneratorManager buildSignatureKeyInfoGeneratorManager() {
        final NamedKeyInfoGeneratorManager namedManager = new NamedKeyInfoGeneratorManager();
        
        namedManager.setUseDefaultManager(true);
        final KeyInfoGeneratorManager defaultManager = namedManager.getDefaultManager();
        
        // Generator for basic Credentials
        final BasicKeyInfoGeneratorFactory basicFactory = new BasicKeyInfoGeneratorFactory();
        basicFactory.setEmitPublicKeyValue(true);
        basicFactory.setEmitPublicDEREncodedKeyValue(true);
        basicFactory.setEmitKeyNames(true);
        
        // Generator for X509Credentials
        final X509KeyInfoGeneratorFactory x509Factory = new X509KeyInfoGeneratorFactory();
        x509Factory.setEmitEntityCertificate(true);
        x509Factory.setEmitEntityCertificateChain(true);
        
        defaultManager.registerFactory(basicFactory);
        defaultManager.registerFactory(x509Factory);
        
        return namedManager;
    }
    
    /**
     * Build a basic {@link NamedKeyInfoGeneratorManager}.
     * 
     * @return a named KeyInfo generator manager instance
     */
    public static NamedKeyInfoGeneratorManager buildBasicKeyInfoGeneratorManager() {
        final NamedKeyInfoGeneratorManager namedManager = new NamedKeyInfoGeneratorManager();
        
        namedManager.setUseDefaultManager(true);
        final KeyInfoGeneratorManager defaultManager = namedManager.getDefaultManager();
        
        // Generator for basic Credentials
        final BasicKeyInfoGeneratorFactory basicFactory = new BasicKeyInfoGeneratorFactory();
        basicFactory.setEmitPublicKeyValue(true);
        basicFactory.setEmitPublicDEREncodedKeyValue(true);
        basicFactory.setEmitKeyNames(true);
        
        // Generator for X509Credentials
        final X509KeyInfoGeneratorFactory x509Factory = new X509KeyInfoGeneratorFactory();
        x509Factory.setEmitEntityCertificate(true);
        
        defaultManager.registerFactory(basicFactory);
        defaultManager.registerFactory(x509Factory);
        
        return namedManager;
    }

}