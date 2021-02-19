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

import static org.testng.Assert.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.crypto.SecretKey;

import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.KeyTransportAlgorithmPredicate;
import org.opensaml.xmlsec.agreement.KeyAgreementCredential;
import org.opensaml.xmlsec.criterion.EncryptionConfigurationCriterion;
import org.opensaml.xmlsec.criterion.KeyInfoGenerationProfileCriterion;
import org.opensaml.xmlsec.derivation.impl.ConcatKDF;
import org.opensaml.xmlsec.derivation.impl.PBKDF2;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.KeyAgreementEncryptionConfiguration;
import org.opensaml.xmlsec.encryption.support.RSAOAEPParameters;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.impl.KeyAgreementKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.impl.KeyAgreementKeyInfoGeneratorFactory.KeyAgreementKeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class BasicEncryptionParametersResolverTest extends XMLObjectBaseTestCase {
    
    private BasicEncryptionParametersResolver resolver;
    
    private CriteriaSet criteriaSet;
    
    private EncryptionConfigurationCriterion criterion;
    
    private BasicEncryptionConfiguration config1, config2, config3;
    
    private Credential rsaCred1, ecCred1, aes128Cred1, aes192Cred1, aes256Cred1;
    
    private String rsaCred1KeyName = "RSACred1";
    private String ecCred1KeyName = "ECCred1";
    private String aes128Cred1KeyName = "AES128Cred1";
    private String aes192Cred1KeyName = "AES192Cred1";
    private String aes256Cred1KeyName = "AES256Cred1";
    
    private String defaultRSAKeyTransportAlgo = EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP;
    private String defaultAES128DataAlgo = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
    private String defaultAES192DataAlgo = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192;
    private String defaultAES256DataAlgo = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256;
    
    private NamedKeyInfoGeneratorManager defaultKeyTransportKeyInfoGeneratorManager = new NamedKeyInfoGeneratorManager();
    private NamedKeyInfoGeneratorManager defaultDataEncryptionKeyInfoGeneratorManager = new NamedKeyInfoGeneratorManager();
    
    @BeforeClass
    public void buildCredentials() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPair rsaKeyPair = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 2048, null);
        rsaCred1 = CredentialSupport.getSimpleCredential(rsaKeyPair.getPublic(), rsaKeyPair.getPrivate());
        rsaCred1.getKeyNames().add(rsaCred1KeyName);
        
        KeyPair ecKeyPair = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, new ECGenParameterSpec("secp256r1"), null);
        ecCred1 = CredentialSupport.getSimpleCredential(ecKeyPair.getPublic(), ecKeyPair.getPrivate());
        ecCred1.getKeyNames().add(ecCred1KeyName);
        
        SecretKey aes128Key = KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 128, null);
        aes128Cred1 = CredentialSupport.getSimpleCredential(aes128Key);
        aes128Cred1.getKeyNames().add(aes128Cred1KeyName);
        
        SecretKey aes192Key = KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 192, null);
        aes192Cred1 = CredentialSupport.getSimpleCredential(aes192Key);
        aes192Cred1.getKeyNames().add(aes192Cred1KeyName);
        
        SecretKey aes256Key = KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 256, null);
        aes256Cred1 = CredentialSupport.getSimpleCredential(aes256Key);
        aes256Cred1.getKeyNames().add(aes256Cred1KeyName);
    }
    
    @BeforeMethod
    public void setUp() {
        resolver = new BasicEncryptionParametersResolver();
        
        config1 = new BasicEncryptionConfiguration();
        config2 = new BasicEncryptionConfiguration();
        config3 = new BasicEncryptionConfiguration();
        
        // Set these as defaults on the last config in the chain, just so don't have to set in every test.
        config3.setDataEncryptionAlgorithms(List.of(
                defaultAES128DataAlgo,
                defaultAES192DataAlgo,
                defaultAES256DataAlgo,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM
                ));
        config3.setKeyTransportEncryptionAlgorithms(List.of(
                defaultRSAKeyTransportAlgo, 
                EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15,
                EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11,
                EncryptionConstants.ALGO_ID_KEYWRAP_AES128,
                EncryptionConstants.ALGO_ID_KEYWRAP_AES192,
                EncryptionConstants.ALGO_ID_KEYWRAP_AES256,
                EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES
                ));
        
        KeyAgreementEncryptionConfiguration ecConfig = new KeyAgreementEncryptionConfiguration();
        ecConfig.setAlgorithm(EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        ConcatKDF concatKDF = new ConcatKDF();
        concatKDF.setAlgorithmID("00");
        concatKDF.setPartyUInfo("00");
        concatKDF.setPartyVInfo("00");
        ecConfig.setParameters(Set.of(concatKDF));
        config3.setKeyAgreementConfigurations(Map.of("EC", ecConfig));
        
        BasicKeyInfoGeneratorFactory basicFactory1 = new BasicKeyInfoGeneratorFactory();
        X509KeyInfoGeneratorFactory x509Factory1 = new X509KeyInfoGeneratorFactory();
        KeyAgreementKeyInfoGeneratorFactory kaFactory1 = new KeyAgreementKeyInfoGeneratorFactory();
        defaultKeyTransportKeyInfoGeneratorManager = new NamedKeyInfoGeneratorManager();
        defaultKeyTransportKeyInfoGeneratorManager.registerDefaultFactory(basicFactory1);
        defaultKeyTransportKeyInfoGeneratorManager.registerDefaultFactory(x509Factory1);
        defaultKeyTransportKeyInfoGeneratorManager.registerDefaultFactory(kaFactory1);
        config3.setKeyTransportKeyInfoGeneratorManager(defaultKeyTransportKeyInfoGeneratorManager);
        
        BasicKeyInfoGeneratorFactory basicFactory2 = new BasicKeyInfoGeneratorFactory();
        X509KeyInfoGeneratorFactory x509Factory2 = new X509KeyInfoGeneratorFactory();
        KeyAgreementKeyInfoGeneratorFactory kaFactory2 = new KeyAgreementKeyInfoGeneratorFactory();
        defaultDataEncryptionKeyInfoGeneratorManager = new NamedKeyInfoGeneratorManager();
        defaultDataEncryptionKeyInfoGeneratorManager.registerDefaultFactory(basicFactory2);
        defaultDataEncryptionKeyInfoGeneratorManager.registerDefaultFactory(x509Factory2);
        defaultDataEncryptionKeyInfoGeneratorManager.registerDefaultFactory(kaFactory2);
        config3.setDataKeyInfoGeneratorManager(defaultDataEncryptionKeyInfoGeneratorManager);
        
        criterion = new EncryptionConfigurationCriterion(config1, config2, config3);
        
        criteriaSet = new CriteriaSet(criterion);
    }

    @Test
    public void testBasicRSA() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(Collections.singletonList(rsaCred1));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNotNull(params);
        assertEquals(params.getKeyTransportEncryptionCredential(), rsaCred1);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        assertNull(params.getDataEncryptionCredential());
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testRSAWithAlgorithmOverrides() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(Collections.singletonList(rsaCred1));
        
        config2.setDataEncryptionAlgorithms(Collections.singletonList(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));
        config2.setKeyTransportEncryptionAlgorithms(Collections.singletonList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNotNull(params);
        assertEquals(params.getKeyTransportEncryptionCredential(), rsaCred1);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        assertNull(params.getDataEncryptionCredential());
        assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testRSAWithBlacklist() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(Collections.singletonList(rsaCred1));
        config1.setExcludedAlgorithms(List.of(defaultRSAKeyTransportAlgo, defaultAES128DataAlgo, defaultAES192DataAlgo));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNotNull(params);
        assertEquals(params.getKeyTransportEncryptionCredential(), rsaCred1);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        assertNull(params.getDataEncryptionCredential());
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES256DataAlgo);
        assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testRSAWithWhitelist() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(Collections.singletonList(rsaCred1));
        config1.setIncludedAlgorithms(List.of(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNotNull(params);
        assertEquals(params.getKeyTransportEncryptionCredential(), rsaCred1);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        assertNull(params.getDataEncryptionCredential());
        assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testRSAWithGeneratedDataCredential() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(Collections.singletonList(rsaCred1));
        
        resolver.setAutoGenerateDataEncryptionCredential(true);
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNotNull(params);
        assertEquals(params.getKeyTransportEncryptionCredential(), rsaCred1);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        assertNotNull(params.getDataEncryptionCredential());
        assertNotNull(params.getDataEncryptionCredential().getSecretKey());
        assertEquals(KeySupport.getKeyLength(params.getDataEncryptionCredential().getSecretKey()), Integer.valueOf(128));
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        assertNotNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testRSAOAEPParameters() throws ResolverException {
        EncryptionParameters params;
        config1.setKeyTransportEncryptionCredentials(Collections.singletonList(rsaCred1));
        
        // Shouldn't resolve since not RSA OAEP
        config1.setKeyTransportEncryptionAlgorithms(Collections.singletonList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        params = resolver.resolveSingle(criteriaSet);
        assertNull(params.getRSAOAEPParameters());
        
        // Should resolve an empty instance
        config1.setKeyTransportEncryptionAlgorithms(Collections.singletonList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP));
        params = resolver.resolveSingle(criteriaSet);
        assertNotNull(params.getRSAOAEPParameters());
        assertTrue(params.getRSAOAEPParameters().isEmpty());
        
        // Should resolve full set of values from config3
        config1.setKeyTransportEncryptionAlgorithms(Collections.singletonList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP));
        config3.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA1, EncryptionConstants.ALGO_ID_MGF1_SHA1, "dummy-oaep-params-3"));
        params = resolver.resolveSingle(criteriaSet);
        assertNotNull(params.getRSAOAEPParameters());
        assertEquals(params.getRSAOAEPParameters().getDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA1);
        assertEquals(params.getRSAOAEPParameters().getMaskGenerationFunction(), EncryptionConstants.ALGO_ID_MGF1_SHA1);
        assertEquals(params.getRSAOAEPParameters().getOAEPParams(), "dummy-oaep-params-3");
        
        // Should resolve digest and mgf from config2, OAEPParams from config3 (merged)
        config1.setKeyTransportEncryptionAlgorithms(Collections.singletonList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11));
        config2.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA256, EncryptionConstants.ALGO_ID_MGF1_SHA256, null));
        config3.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA1, EncryptionConstants.ALGO_ID_MGF1_SHA1, "dummy-oaep-params-3"));
        params = resolver.resolveSingle(criteriaSet);
        assertNotNull(params.getRSAOAEPParameters());
        assertEquals(params.getRSAOAEPParameters().getDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
        assertEquals(params.getRSAOAEPParameters().getMaskGenerationFunction(), EncryptionConstants.ALGO_ID_MGF1_SHA256);
        assertEquals(params.getRSAOAEPParameters().getOAEPParams(), "dummy-oaep-params-3");
        
        // Should resolve digest from config1, and mgf from config2 (merged), but with no merging from config3 
        config1.setKeyTransportEncryptionAlgorithms(Collections.singletonList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11));
        config1.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA512, null, null));
        config2.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA256, EncryptionConstants.ALGO_ID_MGF1_SHA256, null));
        config2.setRSAOAEPParametersMerge(false);
        config3.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA1, EncryptionConstants.ALGO_ID_MGF1_SHA1, "dummy-oaep-params-3"));
        params = resolver.resolveSingle(criteriaSet);
        assertNotNull(params.getRSAOAEPParameters());
        assertEquals(params.getRSAOAEPParameters().getDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA512);
        assertEquals(params.getRSAOAEPParameters().getMaskGenerationFunction(), EncryptionConstants.ALGO_ID_MGF1_SHA256);
        assertNull(params.getRSAOAEPParameters().getOAEPParams());
        
        // Should resolve empty instance based on config1 only, with no merging
        config1.setKeyTransportEncryptionAlgorithms(Collections.singletonList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11));
        config1.setRSAOAEPParameters(null);
        config1.setRSAOAEPParametersMerge(false);
        config2.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA256, EncryptionConstants.ALGO_ID_MGF1_SHA256, "dummy-oaep-params2"));
        params = resolver.resolveSingle(criteriaSet);
        assertNotNull(params.getRSAOAEPParameters());
        assertTrue(params.getRSAOAEPParameters().isEmpty());
    }
    
    @Test
    public void testECDHWithDirectDataEncryption() throws ResolverException {
        config1.setDataEncryptionCredentials(Collections.singletonList(ecCred1));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNotNull(params);
        assertNull(params.getKeyTransportEncryptionCredential());
        assertNull(params.getKeyTransportEncryptionAlgorithm());
        assertNull(params.getKeyTransportKeyInfoGenerator());
        
        assertNotNull(params.getDataEncryptionCredential());
        assertTrue(KeyAgreementCredential.class.isInstance(params.getDataEncryptionCredential()));
        assertNotNull(params.getDataEncryptionCredential().getSecretKey());
        assertEquals(params.getDataEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        assertEquals(KeySupport.getKeyLength(params.getDataEncryptionCredential().getSecretKey()), Integer.valueOf(128));
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        assertNotNull(params.getDataKeyInfoGenerator());
        assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getDataKeyInfoGenerator()));
    }
    
    @Test
    public void testECDHWithDirectDataEncryptionAndAlgorithmOverrides() throws ResolverException {
        config1.setDataEncryptionCredentials(Collections.singletonList(ecCred1));
        
        config2.setDataEncryptionAlgorithms(Collections.singletonList(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNotNull(params);
        assertNull(params.getKeyTransportEncryptionCredential());
        assertNull(params.getKeyTransportEncryptionAlgorithm());
        assertNull(params.getKeyTransportKeyInfoGenerator());
        
        assertNotNull(params.getDataEncryptionCredential());
        assertTrue(KeyAgreementCredential.class.isInstance(params.getDataEncryptionCredential()));
        assertNotNull(params.getDataEncryptionCredential().getSecretKey());
        assertEquals(params.getDataEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        assertEquals(KeySupport.getKeyLength(params.getDataEncryptionCredential().getSecretKey()), Integer.valueOf(256));
        assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        assertNotNull(params.getDataKeyInfoGenerator());
        assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getDataKeyInfoGenerator()));
    }
    
    @Test
    public void testECDHWithKeyWrap() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(Collections.singletonList(ecCred1));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNotNull(params.getKeyTransportEncryptionCredential());
        assertTrue(KeyAgreementCredential.class.isInstance(params.getKeyTransportEncryptionCredential()));
        assertNotNull(params.getKeyTransportEncryptionCredential().getSecretKey());
        assertEquals(params.getKeyTransportEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        assertEquals(KeySupport.getKeyLength(params.getKeyTransportEncryptionCredential().getSecretKey()), Integer.valueOf(128));
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getKeyTransportKeyInfoGenerator()));
        
        assertNull(params.getDataEncryptionCredential());
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testECDHWithKeyWrapAndAlgorithmOverrides() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(Collections.singletonList(ecCred1));
        
        config2.setKeyTransportEncryptionAlgorithms(Collections.singletonList(EncryptionConstants.ALGO_ID_KEYWRAP_AES256));
        config2.setDataEncryptionAlgorithms(Collections.singletonList(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNotNull(params.getKeyTransportEncryptionCredential());
        assertTrue(KeyAgreementCredential.class.isInstance(params.getKeyTransportEncryptionCredential()));
        assertNotNull(params.getKeyTransportEncryptionCredential().getSecretKey());
        assertEquals(params.getKeyTransportEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        assertEquals(KeySupport.getKeyLength(params.getKeyTransportEncryptionCredential().getSecretKey()), Integer.valueOf(256));
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYWRAP_AES256);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getKeyTransportKeyInfoGenerator()));
        
        assertNull(params.getDataEncryptionCredential());
        assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192);
        assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testECDHWithKeyWrapAndGeneratedDataCredential() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(Collections.singletonList(ecCred1));
        
        resolver.setAutoGenerateDataEncryptionCredential(true);
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNotNull(params.getKeyTransportEncryptionCredential());
        assertTrue(KeyAgreementCredential.class.isInstance(params.getKeyTransportEncryptionCredential()));
        assertNotNull(params.getKeyTransportEncryptionCredential().getSecretKey());
        assertEquals(params.getKeyTransportEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        assertEquals(KeySupport.getKeyLength(params.getKeyTransportEncryptionCredential().getSecretKey()), Integer.valueOf(128));
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getKeyTransportKeyInfoGenerator()));
        
        assertNotNull(params.getDataEncryptionCredential());
        assertNotNull(params.getDataEncryptionCredential().getSecretKey());
        assertEquals(params.getDataEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        assertEquals(KeySupport.getKeyLength(params.getDataEncryptionCredential().getSecretKey()), Integer.valueOf(128));
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        assertNotNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testECDHWithKDFOverride() throws ResolverException {
        config1.setDataEncryptionCredentials(Collections.singletonList(ecCred1));
        
        KeyAgreementEncryptionConfiguration ecConfig = new KeyAgreementEncryptionConfiguration();
        PBKDF2 kdf = new PBKDF2();
        ecConfig.setParameters(Set.of(kdf));
        config2.setKeyAgreementConfigurations(Map.of("EC", ecConfig));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNotNull(params);
        assertNull(params.getKeyTransportEncryptionCredential());
        assertNull(params.getKeyTransportEncryptionAlgorithm());
        assertNull(params.getKeyTransportKeyInfoGenerator());
        
        assertNotNull(params.getDataEncryptionCredential());
        assertTrue(KeyAgreementCredential.class.isInstance(params.getDataEncryptionCredential()));
        assertNotNull(params.getDataEncryptionCredential().getSecretKey());
        assertEquals(params.getDataEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        assertEquals(KeySupport.getKeyLength(params.getDataEncryptionCredential().getSecretKey()), Integer.valueOf(128));
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        assertNotNull(params.getDataKeyInfoGenerator());
        assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getDataKeyInfoGenerator()));
        
        KeyAgreementCredential kaCred = KeyAgreementCredential.class.cast(params.getDataEncryptionCredential());
        assertEquals(kaCred.getParameters().size(), 1);
        assertTrue(kaCred.getParameters().contains(PBKDF2.class));
    }
    
    @Test
    public void testGetEffectiveKeyAgreementConfiguration() {
        KeyAgreementEncryptionConfiguration ecConfig1 = new KeyAgreementEncryptionConfiguration();
        ecConfig1.setParameters(Set.of(new ConcatKDF()));
        config1.setKeyAgreementConfigurations(Map.of("EC", ecConfig1));
        
        KeyAgreementEncryptionConfiguration ecConfig2 = new KeyAgreementEncryptionConfiguration();
        ecConfig2.setAlgorithm(EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        ecConfig2.setParameters(Set.of(new PBKDF2()));
        config2.setKeyAgreementConfigurations(Map.of("EC", ecConfig2));
        
        KeyAgreementEncryptionConfiguration ecConfig3 = new KeyAgreementEncryptionConfiguration();
        ecConfig3.setAlgorithm("SomeAlgo");
        ecConfig3.setParameters(Set.of(new ConcatKDF()));
        config3.setKeyAgreementConfigurations(Map.of("EC", ecConfig3));
        
        KeyAgreementEncryptionConfiguration config = resolver.getEffectiveKeyAgreementConfiguration(criteriaSet, ecCred1);
        
        Assert.assertEquals(config.getAlgorithm(), EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        Assert.assertEquals(config.getParameters().size(), 1);
        Assert.assertTrue(ConcatKDF.class.isInstance(config.getParameters().iterator().next()));
        Assert.assertSame(config.getParameters().iterator().next(), ecConfig1.getParameters().iterator().next());
    }
    
    @Test
    public void testAES128KeyWrap() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(Collections.singletonList(aes128Cred1));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNotNull(params);
        assertEquals(params.getKeyTransportEncryptionCredential(), aes128Cred1);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        assertNull(params.getDataEncryptionCredential());
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testDataCredOnly() throws ResolverException {
        config1.setDataEncryptionCredentials(Collections.singletonList(aes256Cred1));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNotNull(params);
        assertNull(params.getKeyTransportEncryptionCredential());
        assertNull(params.getKeyTransportEncryptionAlgorithm());
        assertNull(params.getKeyTransportKeyInfoGenerator());
        
        assertEquals(params.getDataEncryptionCredential(), aes256Cred1);
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES256DataAlgo);
        assertNotNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testKeyTransportCredWithBlacklistAndFallthrough() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(List.of(rsaCred1, aes256Cred1));
        
        // Blacklist all RSA algos so rsaCred1 is skipped in favor of aes256Cred1
        config1.setExcludedAlgorithms(List.of(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNotNull(params);
        assertEquals(params.getKeyTransportEncryptionCredential(), aes256Cred1);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYWRAP_AES256);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        assertNull(params.getDataEncryptionCredential());
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testDataCredOnlyWithBlacklistAndFallthrough() throws ResolverException {
        config1.setDataEncryptionCredentials(List.of(aes128Cred1, aes256Cred1));
        
        // Blacklist both AES-128 variants so aes128Cred1 is skipped in favor of aes256Cred1
        config1.setExcludedAlgorithms(List.of(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM));
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNotNull(params);
        assertNull(params.getKeyTransportEncryptionCredential());
        assertNull(params.getKeyTransportEncryptionAlgorithm());
        assertNull(params.getKeyTransportKeyInfoGenerator());
        
        assertEquals(params.getDataEncryptionCredential(), aes256Cred1);
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES256DataAlgo);
        assertNotNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testKeyTransportAlgorithmPredicate() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(Collections.singletonList(rsaCred1));
        config1.setKeyTransportEncryptionAlgorithms(List.of(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP));
        config1.setDataEncryptionAlgorithms(Collections.singletonList(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128));
        
        // Data algorithm -> key transport algorithm preferences mappings
        final HashMap<String,String> algoMap = new HashMap<>();
        algoMap.put(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        algoMap.put(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        final KeyTransportAlgorithmPredicate predicate = new MapBasedKeyTransportAlgorithmPredicate(algoMap);
        
        // Without the predicate, for control
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        
        config1.setKeyTransportAlgorithmPredicate(predicate);
        
        // Explicit preference with predicate, mapping # 1
        params = resolver.resolveSingle(criteriaSet);
        assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        
        config1.setDataEncryptionAlgorithms(Collections.singletonList(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));
        
        // Explicit preference with predicate, mapping # 2
        params = resolver.resolveSingle(criteriaSet);
        assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
    }
    
    @Test
    public void testKeyInfoGenerationProfile() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(Collections.singletonList(rsaCred1));
        config1.setDataEncryptionCredentials(Collections.singletonList(aes128Cred1));
        
        criteriaSet.add(new KeyInfoGenerationProfileCriterion("testKeyInfoProfile"));
        
        defaultDataEncryptionKeyInfoGeneratorManager.setUseDefaultManager(true);
        defaultKeyTransportKeyInfoGeneratorManager.setUseDefaultManager(true);
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNotNull(params.getDataKeyInfoGenerator());
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        defaultDataEncryptionKeyInfoGeneratorManager.setUseDefaultManager(false);
        defaultKeyTransportKeyInfoGeneratorManager.setUseDefaultManager(false);
        
        params = resolver.resolveSingle(criteriaSet);
        
        assertNull(params.getDataKeyInfoGenerator());
        assertNull(params.getKeyTransportKeyInfoGenerator());
        
        defaultDataEncryptionKeyInfoGeneratorManager.setUseDefaultManager(false);
        defaultKeyTransportKeyInfoGeneratorManager.setUseDefaultManager(false);
        defaultDataEncryptionKeyInfoGeneratorManager.registerFactory("testKeyInfoProfile", new BasicKeyInfoGeneratorFactory());
        defaultKeyTransportKeyInfoGeneratorManager.registerFactory("testKeyInfoProfile", new BasicKeyInfoGeneratorFactory());
        
        params = resolver.resolveSingle(criteriaSet);
        
        assertNotNull(params.getDataKeyInfoGenerator());
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
    }
    
    @Test
    public void testResolve() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(Collections.singletonList(rsaCred1));
        
        Iterable<EncryptionParameters> paramsIter = resolver.resolve(criteriaSet);
        assertNotNull(paramsIter);
        
        Iterator<EncryptionParameters> iterator = paramsIter.iterator();
        assertNotNull(iterator);
        
        assertTrue(iterator.hasNext());
        
        EncryptionParameters params = iterator.next();
        
        assertNotNull(params);
        assertEquals(params.getKeyTransportEncryptionCredential(), rsaCred1);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        assertNull(params.getDataEncryptionCredential());
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        assertNull(params.getDataKeyInfoGenerator());
        
        assertFalse(iterator.hasNext());
    }
    
    @Test
    public void testNoCredentials() throws ResolverException {
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNull(params);
    }
    
    @Test
    public void testNoKeyTransportAlgorithms() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(Collections.singletonList(rsaCred1));
        config3.setKeyTransportEncryptionAlgorithms(new ArrayList<>());
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNull(params);
    }
    
    @Test
    public void testNoDataEncryptionAlgorithmForResolvedDataCredential() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(Collections.singletonList(rsaCred1));
        config1.setDataEncryptionCredentials(Collections.singletonList(aes128Cred1));
        config3.setDataEncryptionAlgorithms(new ArrayList<>());
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNull(params);
    }
    
    @Test
    public void testNoDataEncryptionAlgorithmForEncrypterAutoGen() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(Collections.singletonList(rsaCred1));
        config3.setDataEncryptionAlgorithms(new ArrayList<>());
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNull(params);
    }
    
    @Test
    public void testResolveKeyTransportAlgorithmPredicate() {
        assertNull(resolver.resolveKeyTransportAlgorithmPredicate(criteriaSet));
        
        final KeyTransportAlgorithmPredicate predicate = new KeyTransportAlgorithmPredicate() {
            public boolean test(@Nullable SelectionInput input) {
                return true;
            }
        };
        
        config2.setKeyTransportAlgorithmPredicate(predicate);
        
        assertTrue(resolver.resolveKeyTransportAlgorithmPredicate(criteriaSet) == predicate);
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testNullCriteriaSet() throws ResolverException {
        resolver.resolve(null);
    }

    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testAbsentCriterion() throws ResolverException {
        resolver.resolve(new CriteriaSet());
    }
    
    // Test utility classes
    
    public class MapBasedKeyTransportAlgorithmPredicate implements KeyTransportAlgorithmPredicate {
        private Map<String,String> algoMap;
        
        public MapBasedKeyTransportAlgorithmPredicate(Map<String,String> map) {
            algoMap = map;
        }
        
        public boolean test(@Nullable SelectionInput input) {
            return algoMap.get(input.getDataEncryptionAlgorithm()).equals(input.getKeyTransportAlgorithm());
        }
    }

}
