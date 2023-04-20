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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nullable;
import javax.crypto.SecretKey;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.ConstraintViolationException;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.KeyTransportAlgorithmPredicate;
import org.opensaml.xmlsec.agreement.KeyAgreementCredential;
import org.opensaml.xmlsec.agreement.KeyAgreementParameter;
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

@SuppressWarnings({"javadoc", "null"})
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
        config3.setDataEncryptionAlgorithms(CollectionSupport.listOf(
                defaultAES128DataAlgo,
                defaultAES192DataAlgo,
                defaultAES256DataAlgo,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM,
                EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM
                ));
        config3.setKeyTransportEncryptionAlgorithms(CollectionSupport.listOf(
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
        ecConfig.setParameters(CollectionSupport.singletonList(concatKDF));
        config3.setKeyAgreementConfigurations(CollectionSupport.singletonMap("EC", ecConfig));
        
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
        config1.setKeyTransportEncryptionCredentials(CollectionSupport.singletonList(rsaCred1));
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
        
        assertEquals(params.getKeyTransportEncryptionCredential(), rsaCred1);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        assertNull(params.getDataEncryptionCredential());
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testRSAWithAlgorithmOverrides() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(CollectionSupport.singletonList(rsaCred1));
        
        config2.setDataEncryptionAlgorithms(CollectionSupport.singletonList(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));
        config2.setKeyTransportEncryptionAlgorithms(CollectionSupport.singletonList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
        
        assertEquals(params.getKeyTransportEncryptionCredential(), rsaCred1);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        assertNull(params.getDataEncryptionCredential());
        assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testRSAWithBlacklist() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(CollectionSupport.singletonList(rsaCred1));
        config1.setExcludedAlgorithms(CollectionSupport.listOf(defaultRSAKeyTransportAlgo, defaultAES128DataAlgo, defaultAES192DataAlgo));
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
        
        assertEquals(params.getKeyTransportEncryptionCredential(), rsaCred1);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        assertNull(params.getDataEncryptionCredential());
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES256DataAlgo);
        assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testRSAWithWhitelist() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(CollectionSupport.singletonList(rsaCred1));
        config1.setIncludedAlgorithms(CollectionSupport.listOf(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
        
        assertEquals(params.getKeyTransportEncryptionCredential(), rsaCred1);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        assertNull(params.getDataEncryptionCredential());
        assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testRSAWithGeneratedDataCredential() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(CollectionSupport.singletonList(rsaCred1));
        
        resolver.setAutoGenerateDataEncryptionCredential(true);
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
        
        assertEquals(params.getKeyTransportEncryptionCredential(), rsaCred1);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        final Credential dataCred = params.getDataEncryptionCredential();
        assert dataCred != null;
        
        final SecretKey skey = dataCred.getSecretKey();
        assert skey != null;
        assertEquals(KeySupport.getKeyLength(skey), 128);
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        assertNotNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testRSAOAEPParameters() throws ResolverException {
        EncryptionParameters params;
        config1.setKeyTransportEncryptionCredentials(CollectionSupport.singletonList(rsaCred1));
        
        // Shouldn't resolve since not RSA OAEP
        config1.setKeyTransportEncryptionAlgorithms(CollectionSupport.singletonList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        assertNull(params.getRSAOAEPParameters());
        
        // Should resolve an empty instance
        config1.setKeyTransportEncryptionAlgorithms(CollectionSupport.singletonList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP));
        params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        RSAOAEPParameters oaep = params.getRSAOAEPParameters();
        assert oaep != null;
        assertTrue(oaep.isEmpty());
        
        // Should resolve full set of values from config3
        config1.setKeyTransportEncryptionAlgorithms(CollectionSupport.singletonList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP));
        config3.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA1, EncryptionConstants.ALGO_ID_MGF1_SHA1, "dummy-oaep-params-3"));
        params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        oaep = params.getRSAOAEPParameters();
        assert oaep != null;
        assertEquals(oaep.getDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA1);
        assertEquals(oaep.getMaskGenerationFunction(), EncryptionConstants.ALGO_ID_MGF1_SHA1);
        assertEquals(oaep.getOAEPParams(), "dummy-oaep-params-3");
        
        // Should resolve digest and mgf from config2, OAEPParams from config3 (merged)
        config1.setKeyTransportEncryptionAlgorithms(CollectionSupport.singletonList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11));
        config2.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA256, EncryptionConstants.ALGO_ID_MGF1_SHA256, null));
        config3.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA1, EncryptionConstants.ALGO_ID_MGF1_SHA1, "dummy-oaep-params-3"));
        params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        oaep = params.getRSAOAEPParameters();
        assert oaep != null;
        assertNotNull(params.getRSAOAEPParameters());
        assertEquals(oaep.getDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
        assertEquals(oaep.getMaskGenerationFunction(), EncryptionConstants.ALGO_ID_MGF1_SHA256);
        assertEquals(oaep.getOAEPParams(), "dummy-oaep-params-3");
        
        // Should resolve digest from config1, and mgf from config2 (merged), but with no merging from config3 
        config1.setKeyTransportEncryptionAlgorithms(CollectionSupport.singletonList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11));
        config1.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA512, null, null));
        config2.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA256, EncryptionConstants.ALGO_ID_MGF1_SHA256, null));
        config2.setRSAOAEPParametersMerge(false);
        config3.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA1, EncryptionConstants.ALGO_ID_MGF1_SHA1, "dummy-oaep-params-3"));
        params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        oaep = params.getRSAOAEPParameters();
        assert oaep != null;
        assertNotNull(params.getRSAOAEPParameters());
        assertEquals(oaep.getDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA512);
        assertEquals(oaep.getMaskGenerationFunction(), EncryptionConstants.ALGO_ID_MGF1_SHA256);
        assertNull(oaep.getOAEPParams());
        
        // Should resolve empty instance based on config1 only, with no merging
        config1.setKeyTransportEncryptionAlgorithms(CollectionSupport.singletonList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11));
        config1.setRSAOAEPParameters(null);
        config1.setRSAOAEPParametersMerge(false);
        config2.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA256, EncryptionConstants.ALGO_ID_MGF1_SHA256, "dummy-oaep-params2"));
        params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        oaep = params.getRSAOAEPParameters();
        assert oaep != null;
        assertNotNull(params.getRSAOAEPParameters());
        assertTrue(oaep.isEmpty());
    }
    
    @Test
    public void testECDHWithDirectDataEncryption() throws ResolverException {
        config1.setDataEncryptionCredentials(CollectionSupport.singletonList(ecCred1));
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertNull(params.getKeyTransportEncryptionCredential());
        assertNull(params.getKeyTransportEncryptionAlgorithm());
        assertNull(params.getKeyTransportKeyInfoGenerator());
        
        final Credential dataCred = params.getDataEncryptionCredential();
        assert dataCred != null;
        assertTrue(KeyAgreementCredential.class.isInstance(params.getDataEncryptionCredential()));
        
        final SecretKey skey = dataCred.getSecretKey();
        assert skey != null;
        assertEquals(skey.getAlgorithm(), "AES");
        assertEquals(KeySupport.getKeyLength(skey), 128);
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        assertNotNull(params.getDataKeyInfoGenerator());
        assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getDataKeyInfoGenerator()));
    }
    
    @Test
    public void testECDHWithDirectDataEncryptionAndAlgorithmOverrides() throws ResolverException {
        config1.setDataEncryptionCredentials(CollectionSupport.singletonList(ecCred1));
        
        config2.setDataEncryptionAlgorithms(CollectionSupport.singletonList(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertNull(params.getKeyTransportEncryptionCredential());
        assertNull(params.getKeyTransportEncryptionAlgorithm());
        assertNull(params.getKeyTransportKeyInfoGenerator());
        
        final Credential dataCred = params.getDataEncryptionCredential();
        assert dataCred != null;
        assertTrue(KeyAgreementCredential.class.isInstance(params.getDataEncryptionCredential()));
        
        final SecretKey skey = dataCred.getSecretKey();
        assert skey != null;

        assertEquals(skey.getAlgorithm(), "AES");
        assertEquals(KeySupport.getKeyLength(skey), 256);
        assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        assertNotNull(params.getDataKeyInfoGenerator());
        assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getDataKeyInfoGenerator()));
    }
    
    @Test
    public void testECDHWithKeyWrap() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(CollectionSupport.singletonList(ecCred1));
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;

        final Credential keyCred = params.getKeyTransportEncryptionCredential();
        assert keyCred != null;
        assertTrue(KeyAgreementCredential.class.isInstance(params.getKeyTransportEncryptionCredential()));
        
        final SecretKey skey = keyCred.getSecretKey();
        assert skey != null;
        assertEquals(skey.getAlgorithm(), "AES");
        assertEquals(KeySupport.getKeyLength(skey), 128);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getKeyTransportKeyInfoGenerator()));
        
        assertNull(params.getDataEncryptionCredential());
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testECDHWithKeyWrapAndAlgorithmOverrides() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(CollectionSupport.singletonList(ecCred1));
        
        config2.setKeyTransportEncryptionAlgorithms(CollectionSupport.singletonList(EncryptionConstants.ALGO_ID_KEYWRAP_AES256));
        config2.setDataEncryptionAlgorithms(CollectionSupport.singletonList(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192));
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
                
        final Credential keyCred = params.getKeyTransportEncryptionCredential();
        assert keyCred != null;
        assertTrue(KeyAgreementCredential.class.isInstance(params.getKeyTransportEncryptionCredential()));

        final SecretKey skey = keyCred.getSecretKey();
        assert skey != null;
        
        assertEquals(skey.getAlgorithm(), "AES");
        assertEquals(KeySupport.getKeyLength(skey), 256);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYWRAP_AES256);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getKeyTransportKeyInfoGenerator()));
        
        assertNull(params.getDataEncryptionCredential());
        assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192);
        assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testECDHWithKeyWrapAndGeneratedDataCredential() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(CollectionSupport.singletonList(ecCred1));
        
        resolver.setAutoGenerateDataEncryptionCredential(true);
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
        
        final Credential keyCred = params.getKeyTransportEncryptionCredential();
        assert keyCred != null;
        assertTrue(KeyAgreementCredential.class.isInstance(params.getKeyTransportEncryptionCredential()));

        final SecretKey skey = keyCred.getSecretKey();
        assert skey != null;

        assertEquals(skey.getAlgorithm(), "AES");
        assertEquals(KeySupport.getKeyLength(skey), 128);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getKeyTransportKeyInfoGenerator()));
        
        final Credential dataCred = params.getDataEncryptionCredential();
        assert dataCred != null;
        final SecretKey dataKey = dataCred.getSecretKey();
        assert dataKey != null;

        assertEquals(dataKey.getAlgorithm(), "AES");
        assertEquals(KeySupport.getKeyLength(dataKey), 128);
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        assertNotNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testECDHWithKDFOverride() throws ResolverException {
        config1.setDataEncryptionCredentials(CollectionSupport.singletonList(ecCred1));
        
        KeyAgreementEncryptionConfiguration ecConfig = new KeyAgreementEncryptionConfiguration();
        PBKDF2 kdf = new PBKDF2();
        ecConfig.setParameters(CollectionSupport.singletonList(kdf));
        config2.setKeyAgreementConfigurations(CollectionSupport.singletonMap("EC", ecConfig));
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
        
        assertNull(params.getKeyTransportEncryptionCredential());
        assertNull(params.getKeyTransportEncryptionAlgorithm());
        assertNull(params.getKeyTransportKeyInfoGenerator());

        assertTrue(KeyAgreementCredential.class.isInstance(params.getDataEncryptionCredential()));
        final Credential dataCred = params.getDataEncryptionCredential();
        assert dataCred != null;
        final SecretKey dataKey = dataCred.getSecretKey();
        assert dataKey != null;

        assertEquals(dataKey.getAlgorithm(), "AES");
        assertEquals(KeySupport.getKeyLength(dataKey), 128);
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        assertNotNull(params.getDataKeyInfoGenerator());
        assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getDataKeyInfoGenerator()));
        
        KeyAgreementCredential kaCred = KeyAgreementCredential.class.cast(params.getDataEncryptionCredential());
        assertEquals(kaCred.getParameters().size(), 1);
        assertTrue(kaCred.getParameters().contains(PBKDF2.class));
    }
    
    @Test
    public void testGetEffectiveKeyAgreementConfiguration() {
        final KeyAgreementEncryptionConfiguration ecConfig1 = new KeyAgreementEncryptionConfiguration();
        ecConfig1.setParameters(CollectionSupport.singletonList(new ConcatKDF()));
        config1.setKeyAgreementConfigurations(CollectionSupport.singletonMap("EC", ecConfig1));
        
        final KeyAgreementEncryptionConfiguration ecConfig2 = new KeyAgreementEncryptionConfiguration();
        ecConfig2.setAlgorithm(EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        ecConfig2.setParameters(CollectionSupport.singletonList(new PBKDF2()));
        config2.setKeyAgreementConfigurations(CollectionSupport.singletonMap("EC", ecConfig2));
        
        final KeyAgreementEncryptionConfiguration ecConfig3 = new KeyAgreementEncryptionConfiguration();
        ecConfig3.setAlgorithm("SomeAlgo");
        ecConfig3.setParameters(CollectionSupport.singletonList(new ConcatKDF()));
        config3.setKeyAgreementConfigurations(CollectionSupport.singletonMap("EC", ecConfig3));
        
        final KeyAgreementEncryptionConfiguration config = resolver.getEffectiveKeyAgreementConfiguration(criteriaSet, ecCred1);
        assert config != null;
        
        Assert.assertEquals(config.getAlgorithm(), EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        
        final Collection<KeyAgreementParameter> agreementParams = config.getParameters();
        assert agreementParams != null;
        Assert.assertEquals(agreementParams.size(), 1);
        Assert.assertTrue(ConcatKDF.class.isInstance(agreementParams.iterator().next()));
        
        final Collection<KeyAgreementParameter> ec1Params = ecConfig1.getParameters();
        assert ec1Params != null;
        Assert.assertSame(agreementParams.iterator().next(), ec1Params.iterator().next());
    }
    
    @Test
    public void testAES128KeyWrap() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(CollectionSupport.singletonList(aes128Cred1));
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
        
        assertEquals(params.getKeyTransportEncryptionCredential(), aes128Cred1);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        assertNull(params.getDataEncryptionCredential());
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testDataCredOnly() throws ResolverException {
        config1.setDataEncryptionCredentials(CollectionSupport.singletonList(aes256Cred1));
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
        
        assertNull(params.getKeyTransportEncryptionCredential());
        assertNull(params.getKeyTransportEncryptionAlgorithm());
        assertNull(params.getKeyTransportKeyInfoGenerator());
        
        assertEquals(params.getDataEncryptionCredential(), aes256Cred1);
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES256DataAlgo);
        assertNotNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testKeyTransportCredWithBlacklistAndFallthrough() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(CollectionSupport.listOf(rsaCred1, aes256Cred1));
        
        // Blacklist all RSA algos so rsaCred1 is skipped in favor of aes256Cred1
        config1.setExcludedAlgorithms(CollectionSupport.listOf(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11));
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
        
        assertEquals(params.getKeyTransportEncryptionCredential(), aes256Cred1);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYWRAP_AES256);
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        assertNull(params.getDataEncryptionCredential());
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testDataCredOnlyWithBlacklistAndFallthrough() throws ResolverException {
        config1.setDataEncryptionCredentials(CollectionSupport.listOf(aes128Cred1, aes256Cred1));
        
        // Blacklist both AES-128 variants so aes128Cred1 is skipped in favor of aes256Cred1
        config1.setExcludedAlgorithms(CollectionSupport.listOf(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM));
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
        
        assertNull(params.getKeyTransportEncryptionCredential());
        assertNull(params.getKeyTransportEncryptionAlgorithm());
        assertNull(params.getKeyTransportKeyInfoGenerator());
        
        assertEquals(params.getDataEncryptionCredential(), aes256Cred1);
        assertEquals(params.getDataEncryptionAlgorithm(), defaultAES256DataAlgo);
        assertNotNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testKeyTransportAlgorithmPredicate() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(CollectionSupport.singletonList(rsaCred1));
        config1.setKeyTransportEncryptionAlgorithms(CollectionSupport.listOf(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP));
        config1.setDataEncryptionAlgorithms(CollectionSupport.singletonList(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128));
        
        // Data algorithm -> key transport algorithm preferences mappings
        final HashMap<String,String> algoMap = new HashMap<>();
        algoMap.put(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        algoMap.put(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        final KeyTransportAlgorithmPredicate predicate = new MapBasedKeyTransportAlgorithmPredicate(algoMap);
        
        // Without the predicate, for control
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
        
        assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        
        config1.setKeyTransportAlgorithmPredicate(predicate);
        
        // Explicit preference with predicate, mapping # 1
        params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
        
        assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        
        config1.setDataEncryptionAlgorithms(CollectionSupport.singletonList(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));
        
        // Explicit preference with predicate, mapping # 2
        params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
        
        assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
    }
    
    @Test
    public void testKeyInfoGenerationProfile() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(CollectionSupport.singletonList(rsaCred1));
        config1.setDataEncryptionCredentials(CollectionSupport.singletonList(aes128Cred1));
        
        criteriaSet.add(new KeyInfoGenerationProfileCriterion("testKeyInfoProfile"));
        
        defaultDataEncryptionKeyInfoGeneratorManager.setUseDefaultManager(true);
        defaultKeyTransportKeyInfoGeneratorManager.setUseDefaultManager(true);
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
        
        assertNotNull(params.getDataKeyInfoGenerator());
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        defaultDataEncryptionKeyInfoGeneratorManager.setUseDefaultManager(false);
        defaultKeyTransportKeyInfoGeneratorManager.setUseDefaultManager(false);
        
        params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
        
        assertNull(params.getDataKeyInfoGenerator());
        assertNull(params.getKeyTransportKeyInfoGenerator());
        
        defaultDataEncryptionKeyInfoGeneratorManager.setUseDefaultManager(false);
        defaultKeyTransportKeyInfoGeneratorManager.setUseDefaultManager(false);
        defaultDataEncryptionKeyInfoGeneratorManager.registerFactory("testKeyInfoProfile", new BasicKeyInfoGeneratorFactory());
        defaultKeyTransportKeyInfoGeneratorManager.registerFactory("testKeyInfoProfile", new BasicKeyInfoGeneratorFactory());
        
        params = resolver.resolveSingle(criteriaSet);
        assert params != null;        
        
        assertNotNull(params.getDataKeyInfoGenerator());
        assertNotNull(params.getKeyTransportKeyInfoGenerator());
    }
    
    @Test
    public void testResolve() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(CollectionSupport.singletonList(rsaCred1));
        
        Iterable<EncryptionParameters> paramsIter = resolver.resolve(criteriaSet);
        assertNotNull(paramsIter);
        
        Iterator<EncryptionParameters> iterator = paramsIter.iterator();
        assertNotNull(iterator);
        
        assertTrue(iterator.hasNext());
        
        final EncryptionParameters params = iterator.next();
        
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
        config1.setKeyTransportEncryptionCredentials(CollectionSupport.singletonList(rsaCred1));
        config3.setKeyTransportEncryptionAlgorithms(CollectionSupport.emptyList());
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNull(params);
    }
    
    @Test
    public void testNoDataEncryptionAlgorithmForResolvedDataCredential() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(CollectionSupport.singletonList(rsaCred1));
        config1.setDataEncryptionCredentials(CollectionSupport.singletonList(aes128Cred1));
        config3.setDataEncryptionAlgorithms(CollectionSupport.emptyList());
        
        final EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNull(params);
    }
    
    @Test
    public void testNoDataEncryptionAlgorithmForEncrypterAutoGen() throws ResolverException {
        config1.setKeyTransportEncryptionCredentials(CollectionSupport.singletonList(rsaCred1));
        config3.setDataEncryptionAlgorithms(CollectionSupport.emptyList());
        
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
    
    @Test
    public void testNullCriteriaSet() throws ResolverException {
        assertNull(resolver.resolveSingle(null));
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
            assert input != null;
            return algoMap.get(input.getDataEncryptionAlgorithm()).equals(input.getKeyTransportAlgorithm());
        }
    }

}
