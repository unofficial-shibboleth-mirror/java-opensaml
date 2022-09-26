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

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import net.shibboleth.shared.codec.EncodingException;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;
import net.shibboleth.utilities.java.support.logic.ConstraintViolationException;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.saml.common.testing.SAMLTestSupport;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.metadata.EncryptionMethod;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.security.SAMLMetadataKeyAgreementEncryptionConfiguration;
import org.opensaml.saml.security.SAMLMetadataKeyAgreementEncryptionConfiguration.KeyWrap;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.security.testing.SecurityProviderTestSupport;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.KeyTransportAlgorithmPredicate;
import org.opensaml.xmlsec.agreement.KeyAgreementCredential;
import org.opensaml.xmlsec.algorithm.AlgorithmRegistry;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.config.GlobalAlgorithmRegistryInitializer;
import org.opensaml.xmlsec.criterion.EncryptionConfigurationCriterion;
import org.opensaml.xmlsec.criterion.KeyInfoGenerationProfileCriterion;
import org.opensaml.xmlsec.derivation.impl.ConcatKDF;
import org.opensaml.xmlsec.derivation.impl.PBKDF2;
import org.opensaml.xmlsec.encryption.MGF;
import org.opensaml.xmlsec.encryption.OAEPparams;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.KeyAgreementEncryptionConfiguration;
import org.opensaml.xmlsec.encryption.support.RSAOAEPParameters;
import org.opensaml.xmlsec.impl.BasicEncryptionConfiguration;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.impl.KeyAgreementKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.impl.KeyAgreementKeyInfoGeneratorFactory.KeyAgreementKeyInfoGenerator;
import org.opensaml.xmlsec.signature.DigestMethod;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SAMLMetadataEncryptionParametersResolverTest extends XMLObjectBaseTestCase {
    
    private MetadataCredentialResolver mdCredResolver;
    
    private SAMLMetadataEncryptionParametersResolver resolver;
    
    private CriteriaSet criteriaSet;
    
    private EncryptionConfigurationCriterion configCriterion;
    
    private BasicEncryptionConfiguration config1, config2, config3;
    
    private Credential rsaCred1;
    private String rsaCred1KeyName = "RSACred1";
    
    private Credential dsaCred1;
    private String dsaCred1KeyName = "DSACred1";
    
    private Credential ecCred1;
    private String ecCred1KeyName = "ECCred1";
    
    private String defaultRSAKeyTransportAlgo = EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP;
    private String defaultAES128DataAlgo = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
    private String defaultAES192DataAlgo = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192;
    private String defaultAES256DataAlgo = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256;
    
    private NamedKeyInfoGeneratorManager defaultKeyTransportKeyInfoGeneratorManager = new NamedKeyInfoGeneratorManager();
    private NamedKeyInfoGeneratorManager defaultDataEncryptionKeyInfoGeneratorManager = new NamedKeyInfoGeneratorManager();
    
    private RoleDescriptorCriterion roleDescCriterion;
    
    private RoleDescriptor roleDesc;
    
    private String targetEntityID = "urn:test:foo";
    
    private SecurityProviderTestSupport providerSupport;
    
    public SAMLMetadataEncryptionParametersResolverTest() {
        providerSupport = new SecurityProviderTestSupport();
    }
    
    @BeforeClass
    public void buildCredentials() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPair rsaKeyPair = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 2048, null);
        rsaCred1 = CredentialSupport.getSimpleCredential(rsaKeyPair.getPublic(), null);
        rsaCred1.getKeyNames().add(rsaCred1KeyName);
        
        KeyPair dsaKeyPair = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_DSA, 1024, null);
        dsaCred1 = CredentialSupport.getSimpleCredential(dsaKeyPair.getPublic(), null);
        dsaCred1.getKeyNames().add(dsaCred1KeyName);
        
        KeyPair ecKeyPair = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, new ECGenParameterSpec("secp256r1"), null);
        ecCred1 = CredentialSupport.getSimpleCredential(ecKeyPair.getPublic(), ecKeyPair.getPrivate());
        ecCred1.getKeyNames().add(ecCred1KeyName);
    }
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        mdCredResolver = new MetadataCredentialResolver();
        mdCredResolver.setKeyInfoCredentialResolver(SAMLTestSupport.buildBasicInlineKeyInfoResolver());
        mdCredResolver.initialize();
        
        resolver = new SAMLMetadataEncryptionParametersResolver(mdCredResolver);
        
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
        
        SAMLMetadataKeyAgreementEncryptionConfiguration ecConfig = new SAMLMetadataKeyAgreementEncryptionConfiguration();
        ecConfig.setMetadataUseKeyWrap(KeyWrap.Default);
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
        
        configCriterion = new EncryptionConfigurationCriterion(config1, config2, config3);
        
        roleDesc = buildRoleDescriptorSkeleton();
        roleDescCriterion = new RoleDescriptorCriterion(roleDesc);
        
        criteriaSet = new CriteriaSet(configCriterion, roleDescCriterion);
    }
    

    @Test
    public void testBasic() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
        
        Assert.assertNotNull(params.getRSAOAEPParameters());
        Assert.assertTrue(params.getRSAOAEPParameters().isEmpty());
    }
    
    @Test
    public void testWithRSAOAEPParametersFromConfig() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        config3.setRSAOAEPParameters(new RSAOAEPParameters(EncryptionConstants.ALGO_ID_DIGEST_SHA256, null, "oaep-params-3"));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
        
        Assert.assertNotNull(params.getRSAOAEPParameters());
        Assert.assertEquals(params.getRSAOAEPParameters().getDigestMethod(), EncryptionConstants.ALGO_ID_DIGEST_SHA256);
        Assert.assertNull(params.getRSAOAEPParameters().getMaskGenerationFunction());
        Assert.assertEquals(params.getRSAOAEPParameters().getOAEPParams(), "oaep-params-3");
    }
    
    @Test
    public void testWithAlgorithmOverrides() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        
        config2.setDataEncryptionAlgorithms(Collections.singletonList(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));
        config2.setKeyTransportEncryptionAlgorithms(Collections.singletonList(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testWithBlacklist() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        
        config1.setExcludedAlgorithms(List.of(defaultRSAKeyTransportAlgo, defaultAES128DataAlgo, defaultAES192DataAlgo));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES256DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testWithWhitelist() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        
        config1.setIncludedAlgorithms(List.of(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testGeneratedDataCredential() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        
        resolver.setAutoGenerateDataEncryptionCredential(true);
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNotNull(params.getDataEncryptionCredential());
        Assert.assertNotNull(params.getDataEncryptionCredential().getSecretKey());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertEquals(KeySupport.getKeyLength(params.getDataEncryptionCredential().getSecretKey()), Integer.valueOf(128));
        Assert.assertNotNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testEncryptionMethod() throws ResolverException {
        KeyDescriptor keyDescriptor = buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey());
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));
        roleDesc.getKeyDescriptors().add(keyDescriptor);
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testEncryptionMethodWithRSAOAEPParameters() throws ResolverException, InitializationException {
        EncryptionParameters params;
        EncryptionMethod rsaEncryptionMethod;
        DigestMethod digestMethod;
        MGF mgf;
        OAEPparams oaepParams;
        
        KeyDescriptor keyDescriptor = buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey());
        roleDesc.getKeyDescriptors().add(keyDescriptor);
        
        // Shouldn't resolve, since not RSA OAEP 
        rsaEncryptionMethod = buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        keyDescriptor.getEncryptionMethods().clear();
        keyDescriptor.getEncryptionMethods().add(rsaEncryptionMethod);
        params = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(params.getRSAOAEPParameters());
        
        // Should resolve empty instance
        rsaEncryptionMethod = buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        keyDescriptor.getEncryptionMethods().clear();
        keyDescriptor.getEncryptionMethods().add(rsaEncryptionMethod);
        params = resolver.resolveSingle(criteriaSet);
        Assert.assertNotNull(params.getRSAOAEPParameters());
        Assert.assertTrue(params.getRSAOAEPParameters().isEmpty());
        
        
        // Load BouncyCastle so can really test RSA OAEP 1.1 stuff.
        AlgorithmRegistry originalRegistry = AlgorithmSupport.getGlobalAlgorithmRegistry();
        Assert.assertNotNull(originalRegistry);
        providerSupport.loadBC();
        new GlobalAlgorithmRegistryInitializer().init();
        resolver.setAlgorithmRegistry(AlgorithmSupport.getGlobalAlgorithmRegistry());
        
        try {
            // Should resolve digest from metadata
            rsaEncryptionMethod = buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);
            digestMethod = buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
            digestMethod.setAlgorithm(EncryptionConstants.ALGO_ID_DIGEST_SHA256);
            rsaEncryptionMethod.getUnknownXMLObjects().add(digestMethod);
            keyDescriptor.getEncryptionMethods().clear();
            keyDescriptor.getEncryptionMethods().add(rsaEncryptionMethod);
            params = resolver.resolveSingle(criteriaSet);
            Assert.assertNotNull(params.getRSAOAEPParameters());
            Assert.assertEquals(params.getRSAOAEPParameters().getDigestMethod(), EncryptionConstants.ALGO_ID_DIGEST_SHA256);
            Assert.assertNull(params.getRSAOAEPParameters().getMaskGenerationFunction());
            Assert.assertNull(params.getRSAOAEPParameters().getOAEPParams());
            
            // Should resolve all values from metadata
            rsaEncryptionMethod = buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);
            digestMethod = buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
            digestMethod.setAlgorithm(EncryptionConstants.ALGO_ID_DIGEST_SHA256);
            rsaEncryptionMethod.getUnknownXMLObjects().add(digestMethod);
            mgf = buildXMLObject(MGF.DEFAULT_ELEMENT_NAME);
            mgf.setAlgorithm(EncryptionConstants.ALGO_ID_MGF1_SHA256);
            rsaEncryptionMethod.getUnknownXMLObjects().add(mgf);
            oaepParams = buildXMLObject(OAEPparams.DEFAULT_ELEMENT_NAME);
            oaepParams.setValue("oaep-params-md");
            rsaEncryptionMethod.setOAEPparams(oaepParams);
            keyDescriptor.getEncryptionMethods().clear();
            keyDescriptor.getEncryptionMethods().add(rsaEncryptionMethod);
            params = resolver.resolveSingle(criteriaSet);
            Assert.assertNotNull(params.getRSAOAEPParameters());
            Assert.assertEquals(params.getRSAOAEPParameters().getDigestMethod(), EncryptionConstants.ALGO_ID_DIGEST_SHA256);
            Assert.assertEquals(params.getRSAOAEPParameters().getMaskGenerationFunction(), EncryptionConstants.ALGO_ID_MGF1_SHA256);
            Assert.assertEquals(params.getRSAOAEPParameters().getOAEPParams(), "oaep-params-md");
            
            // Should resolve digest from metadata, should NOT resolve OAEPParms from config by default
            config3.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA1, null, "oaep-params-3"));
            rsaEncryptionMethod = buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);
            digestMethod = buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
            digestMethod.setAlgorithm(EncryptionConstants.ALGO_ID_DIGEST_SHA256);
            rsaEncryptionMethod.getUnknownXMLObjects().add(digestMethod);
            keyDescriptor.getEncryptionMethods().clear();
            keyDescriptor.getEncryptionMethods().add(rsaEncryptionMethod);
            params = resolver.resolveSingle(criteriaSet);
            Assert.assertNotNull(params.getRSAOAEPParameters());
            Assert.assertEquals(params.getRSAOAEPParameters().getDigestMethod(), EncryptionConstants.ALGO_ID_DIGEST_SHA256);
            Assert.assertNull(params.getRSAOAEPParameters().getMaskGenerationFunction());
            Assert.assertNull(params.getRSAOAEPParameters().getOAEPParams());
            
            // Should resolve digest from metadata, should resolve OAEPParms from config3
            config3.setRSAOAEPParameters(new RSAOAEPParameters(SignatureConstants.ALGO_ID_DIGEST_SHA1, null, "oaep-params-3"));
            resolver.setMergeMetadataRSAOAEPParametersWithConfig(true);
            rsaEncryptionMethod = buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11);
            digestMethod = buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
            digestMethod.setAlgorithm(EncryptionConstants.ALGO_ID_DIGEST_SHA256);
            rsaEncryptionMethod.getUnknownXMLObjects().add(digestMethod);
            keyDescriptor.getEncryptionMethods().clear();
            keyDescriptor.getEncryptionMethods().add(rsaEncryptionMethod);
            params = resolver.resolveSingle(criteriaSet);
            Assert.assertNotNull(params.getRSAOAEPParameters());
            Assert.assertEquals(params.getRSAOAEPParameters().getDigestMethod(), EncryptionConstants.ALGO_ID_DIGEST_SHA256);
            Assert.assertNull(params.getRSAOAEPParameters().getMaskGenerationFunction());
            Assert.assertEquals(params.getRSAOAEPParameters().getOAEPParams(), "oaep-params-3");
        
        } finally {
            providerSupport.unloadBC();
            ConfigurationService.register(AlgorithmRegistry.class, originalRegistry);
        }
    }
    
    @Test
    public void testKeyTransportAlgorithmPredicate() throws ResolverException {
        KeyDescriptor keyDescriptor = buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey());
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128));
        roleDesc.getKeyDescriptors().add(keyDescriptor);
        
        
        // Data algorithm -> key transport algorithm preferences mappings
        HashMap<String,String> algoMap = new HashMap<>();
        algoMap.put(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        algoMap.put(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        KeyTransportAlgorithmPredicate predicate = new MapBasedKeyTransportAlgorithmPredicate(algoMap);
        
        // Without the predicate, for control
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        
        config1.setKeyTransportAlgorithmPredicate(predicate);
        
        // Explicit preference with predicate, mapping # 1
        params = resolver.resolveSingle(criteriaSet);
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256);
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15);
        
        // Change algo ordering
        keyDescriptor.getEncryptionMethods().clear();
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128));
        
        // Explicit preference with predicate, mapping # 2
        params = resolver.resolveSingle(criteriaSet);
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192);
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        
    }
    
    @Test
    public void testEncryptionMethodWithBlacklist() throws ResolverException {
        KeyDescriptor keyDescriptor = buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey());
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES));
        roleDesc.getKeyDescriptors().add(keyDescriptor);
        
        config1.setExcludedAlgorithms(List.of(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15, EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testEncryptionMethodWithWhitelist() throws ResolverException {
        KeyDescriptor keyDescriptor = buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey());
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15));
        keyDescriptor.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES));
        roleDesc.getKeyDescriptors().add(keyDescriptor);
        
        config1.setIncludedAlgorithms(List.of(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP, EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testEncryptionMethodWithBlacklistedDigest() throws ResolverException {
        EncryptionMethod rsaEncryptionMethod;
        DigestMethod digestMethod;
        
        KeyDescriptor keyDescriptor = buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey());
        
        // This one will be effectively blacklist due to the DigestMethod SHA-1, won't be resolved.
        rsaEncryptionMethod = buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        digestMethod = buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        digestMethod.setAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA1);
        rsaEncryptionMethod.getUnknownXMLObjects().add(digestMethod);
        keyDescriptor.getEncryptionMethods().add(rsaEncryptionMethod);
        
        // This one will be resolved with DigestMethod SHA-256.
        rsaEncryptionMethod = buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        digestMethod = buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        digestMethod.setAlgorithm(EncryptionConstants.ALGO_ID_DIGEST_SHA256);
        rsaEncryptionMethod.getUnknownXMLObjects().add(digestMethod);
        keyDescriptor.getEncryptionMethods().add(rsaEncryptionMethod);
        
        roleDesc.getKeyDescriptors().add(keyDescriptor);
        
        config1.setExcludedAlgorithms(List.of(SignatureConstants.ALGO_ID_DIGEST_SHA1));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        Assert.assertNotNull(params.getRSAOAEPParameters());
        Assert.assertEquals(params.getRSAOAEPParameters().getDigestMethod(), EncryptionConstants.ALGO_ID_DIGEST_SHA256);
        Assert.assertNull(params.getRSAOAEPParameters().getMaskGenerationFunction());
        Assert.assertNull(params.getRSAOAEPParameters().getOAEPParams());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testECDHWithNoEncryptionMethodsAndKeyWrapDefault() throws ResolverException {
        KeyDescriptor kd = buildKeyDescriptor(ecCred1KeyName, UsageType.ENCRYPTION, ecCred1.getPublicKey());
        roleDesc.getKeyDescriptors().add(kd);
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertNull(params.getKeyTransportEncryptionCredential());
        Assert.assertNull(params.getKeyTransportEncryptionAlgorithm());
        Assert.assertNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNotNull(params.getDataEncryptionCredential());
        Assert.assertTrue(KeyAgreementCredential.class.isInstance(params.getDataEncryptionCredential()));
        Assert.assertNotNull(params.getDataEncryptionCredential().getSecretKey());
        Assert.assertEquals(params.getDataEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        Assert.assertEquals(KeySupport.getKeyLength(params.getDataEncryptionCredential().getSecretKey()), Integer.valueOf(128));
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNotNull(params.getDataKeyInfoGenerator());
        Assert.assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getDataKeyInfoGenerator()));
    }
    
    @Test
    public void testECDHWithNoEncryptionMethodsAndKeyWrapAlways() throws ResolverException {
        KeyDescriptor kd = buildKeyDescriptor(ecCred1KeyName, UsageType.ENCRYPTION, ecCred1.getPublicKey());
        roleDesc.getKeyDescriptors().add(kd);
        
        SAMLMetadataKeyAgreementEncryptionConfiguration ecConfig = new SAMLMetadataKeyAgreementEncryptionConfiguration();
        ecConfig.setMetadataUseKeyWrap(KeyWrap.Always);
        config2.setKeyAgreementConfigurations(Map.of("EC", ecConfig));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertNotNull(params.getKeyTransportEncryptionCredential());
        Assert.assertTrue(KeyAgreementCredential.class.isInstance(params.getKeyTransportEncryptionCredential()));
        Assert.assertNotNull(params.getKeyTransportEncryptionCredential().getSecretKey());
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        Assert.assertEquals(KeySupport.getKeyLength(params.getKeyTransportEncryptionCredential().getSecretKey()), Integer.valueOf(128));
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        Assert.assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getKeyTransportKeyInfoGenerator()));
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testECDHWithNoEncryptionMethodsAndKeyWrapNever() throws ResolverException {
        KeyDescriptor kd = buildKeyDescriptor(ecCred1KeyName, UsageType.ENCRYPTION, ecCred1.getPublicKey());
        roleDesc.getKeyDescriptors().add(kd);
        
        SAMLMetadataKeyAgreementEncryptionConfiguration ecConfig = new SAMLMetadataKeyAgreementEncryptionConfiguration();
        ecConfig.setMetadataUseKeyWrap(KeyWrap.Never);
        config2.setKeyAgreementConfigurations(Map.of("EC", ecConfig));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params.getKeyTransportEncryptionCredential());
        Assert.assertNull(params.getKeyTransportEncryptionAlgorithm());
        Assert.assertNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNotNull(params.getDataEncryptionCredential());
        Assert.assertTrue(KeyAgreementCredential.class.isInstance(params.getDataEncryptionCredential()));
        Assert.assertNotNull(params.getDataEncryptionCredential().getSecretKey());
        Assert.assertEquals(params.getDataEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        Assert.assertEquals(KeySupport.getKeyLength(params.getDataEncryptionCredential().getSecretKey()), Integer.valueOf(128));
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNotNull(params.getDataKeyInfoGenerator());
        Assert.assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getDataKeyInfoGenerator()));
    }
    
    @Test
    public void testECDHWithNoEncryptionMethodsAndKeyWrapIfNotIndicated() throws ResolverException {
        KeyDescriptor kd = buildKeyDescriptor(ecCred1KeyName, UsageType.ENCRYPTION, ecCred1.getPublicKey());
        roleDesc.getKeyDescriptors().add(kd);
        
        SAMLMetadataKeyAgreementEncryptionConfiguration ecConfig = new SAMLMetadataKeyAgreementEncryptionConfiguration();
        ecConfig.setMetadataUseKeyWrap(KeyWrap.IfNotIndicated);
        config2.setKeyAgreementConfigurations(Map.of("EC", ecConfig));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertNotNull(params.getKeyTransportEncryptionCredential());
        Assert.assertTrue(KeyAgreementCredential.class.isInstance(params.getKeyTransportEncryptionCredential()));
        Assert.assertNotNull(params.getKeyTransportEncryptionCredential().getSecretKey());
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        Assert.assertEquals(KeySupport.getKeyLength(params.getKeyTransportEncryptionCredential().getSecretKey()), Integer.valueOf(128));
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        Assert.assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getKeyTransportKeyInfoGenerator()));
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testECDHWithBlockEncryptionMethodAndKeyWrapDefault() throws ResolverException {
        KeyDescriptor kd = buildKeyDescriptor(ecCred1KeyName, UsageType.ENCRYPTION, ecCred1.getPublicKey());
        kd.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM));
        roleDesc.getKeyDescriptors().add(kd);
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertNull(params.getKeyTransportEncryptionCredential());
        Assert.assertNull(params.getKeyTransportEncryptionAlgorithm());
        Assert.assertNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNotNull(params.getDataEncryptionCredential());
        Assert.assertTrue(KeyAgreementCredential.class.isInstance(params.getDataEncryptionCredential()));
        Assert.assertNotNull(params.getDataEncryptionCredential().getSecretKey());
        Assert.assertEquals(params.getDataEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        Assert.assertEquals(KeySupport.getKeyLength(params.getDataEncryptionCredential().getSecretKey()), Integer.valueOf(256));
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM);
        Assert.assertNotNull(params.getDataKeyInfoGenerator());
        Assert.assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getDataKeyInfoGenerator()));
    }
    
    @Test
    public void testECDHWithBlockEncryptionMethodAndKeyWrapAlways() throws ResolverException {
        KeyDescriptor kd = buildKeyDescriptor(ecCred1KeyName, UsageType.ENCRYPTION, ecCred1.getPublicKey());
        kd.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM));
        roleDesc.getKeyDescriptors().add(kd);
        
        SAMLMetadataKeyAgreementEncryptionConfiguration ecConfig = new SAMLMetadataKeyAgreementEncryptionConfiguration();
        ecConfig.setMetadataUseKeyWrap(KeyWrap.Always);
        config2.setKeyAgreementConfigurations(Map.of("EC", ecConfig));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertNotNull(params.getKeyTransportEncryptionCredential());
        Assert.assertTrue(KeyAgreementCredential.class.isInstance(params.getKeyTransportEncryptionCredential()));
        Assert.assertNotNull(params.getKeyTransportEncryptionCredential().getSecretKey());
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        Assert.assertEquals(KeySupport.getKeyLength(params.getKeyTransportEncryptionCredential().getSecretKey()), Integer.valueOf(128));
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        Assert.assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getKeyTransportKeyInfoGenerator()));
        
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }

    @Test
    public void testECDHWithKeyWrapEncryptionMethodAndKeyWrapDefault() throws ResolverException {
        KeyDescriptor kd = buildKeyDescriptor(ecCred1KeyName, UsageType.ENCRYPTION, ecCred1.getPublicKey());
        kd.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYWRAP_AES256));
        roleDesc.getKeyDescriptors().add(kd);
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params.getKeyTransportEncryptionCredential());
        Assert.assertTrue(KeyAgreementCredential.class.isInstance(params.getKeyTransportEncryptionCredential()));
        Assert.assertNotNull(params.getKeyTransportEncryptionCredential().getSecretKey());
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        Assert.assertEquals(KeySupport.getKeyLength(params.getKeyTransportEncryptionCredential().getSecretKey()), Integer.valueOf(256));
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYWRAP_AES256);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        Assert.assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getKeyTransportKeyInfoGenerator()));
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testECDHWithKeyWrapEncryptionMethodAndKeyWrapNever() throws ResolverException {
        KeyDescriptor kd = buildKeyDescriptor(ecCred1KeyName, UsageType.ENCRYPTION, ecCred1.getPublicKey());
        kd.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYWRAP_AES256));
        roleDesc.getKeyDescriptors().add(kd);
        
        SAMLMetadataKeyAgreementEncryptionConfiguration ecConfig = new SAMLMetadataKeyAgreementEncryptionConfiguration();
        ecConfig.setMetadataUseKeyWrap(KeyWrap.Never);
        config2.setKeyAgreementConfigurations(Map.of("EC", ecConfig));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params.getKeyTransportEncryptionCredential());
        Assert.assertNull(params.getKeyTransportEncryptionAlgorithm());
        Assert.assertNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNotNull(params.getDataEncryptionCredential());
        Assert.assertTrue(KeyAgreementCredential.class.isInstance(params.getDataEncryptionCredential()));
        Assert.assertNotNull(params.getDataEncryptionCredential().getSecretKey());
        Assert.assertEquals(params.getDataEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        Assert.assertEquals(KeySupport.getKeyLength(params.getDataEncryptionCredential().getSecretKey()), Integer.valueOf(128));
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNotNull(params.getDataKeyInfoGenerator());
        Assert.assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getDataKeyInfoGenerator()));
    }
    
    @Test
    public void testECDHWithBlockAndKeyWrapEncryptionMethods() throws ResolverException {
        KeyDescriptor kd = buildKeyDescriptor(ecCred1KeyName, UsageType.ENCRYPTION, ecCred1.getPublicKey());
        kd.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM));
        kd.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYWRAP_AES256));
        roleDesc.getKeyDescriptors().add(kd);
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params.getKeyTransportEncryptionCredential());
        Assert.assertTrue(KeyAgreementCredential.class.isInstance(params.getKeyTransportEncryptionCredential()));
        Assert.assertNotNull(params.getKeyTransportEncryptionCredential().getSecretKey());
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        Assert.assertEquals(KeySupport.getKeyLength(params.getKeyTransportEncryptionCredential().getSecretKey()), Integer.valueOf(256));
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYWRAP_AES256);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        Assert.assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getKeyTransportKeyInfoGenerator()));
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testECDHWithKeyWrapEncryptionMethodAndGeneratedDataCredential() throws ResolverException {
        KeyDescriptor kd = buildKeyDescriptor(ecCred1KeyName, UsageType.ENCRYPTION, ecCred1.getPublicKey());
        kd.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYWRAP_AES256));
        roleDesc.getKeyDescriptors().add(kd);
        
        resolver.setAutoGenerateDataEncryptionCredential(true);
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params.getKeyTransportEncryptionCredential());
        Assert.assertTrue(KeyAgreementCredential.class.isInstance(params.getKeyTransportEncryptionCredential()));
        Assert.assertNotNull(params.getKeyTransportEncryptionCredential().getSecretKey());
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        Assert.assertEquals(KeySupport.getKeyLength(params.getKeyTransportEncryptionCredential().getSecretKey()), Integer.valueOf(256));
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_KEYWRAP_AES256);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        Assert.assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getKeyTransportKeyInfoGenerator()));
        
        Assert.assertNotNull(params.getDataEncryptionCredential());
        Assert.assertNotNull(params.getDataEncryptionCredential().getSecretKey());
        Assert.assertEquals(params.getDataEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        Assert.assertEquals(KeySupport.getKeyLength(params.getDataEncryptionCredential().getSecretKey()), Integer.valueOf(128));
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNotNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testECDHWithKDFOverride() throws ResolverException {
        KeyDescriptor kd = buildKeyDescriptor(ecCred1KeyName, UsageType.ENCRYPTION, ecCred1.getPublicKey());
        kd.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM));
        roleDesc.getKeyDescriptors().add(kd);
        
        SAMLMetadataKeyAgreementEncryptionConfiguration ecConfig = new SAMLMetadataKeyAgreementEncryptionConfiguration();
        PBKDF2 kdf = new PBKDF2();
        ecConfig.setParameters(Set.of(kdf));
        config2.setKeyAgreementConfigurations(Map.of("EC", ecConfig));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertNull(params.getKeyTransportEncryptionCredential());
        Assert.assertNull(params.getKeyTransportEncryptionAlgorithm());
        Assert.assertNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNotNull(params.getDataEncryptionCredential());
        Assert.assertTrue(KeyAgreementCredential.class.isInstance(params.getDataEncryptionCredential()));
        Assert.assertNotNull(params.getDataEncryptionCredential().getSecretKey());
        Assert.assertEquals(params.getDataEncryptionCredential().getSecretKey().getAlgorithm(), "AES");
        Assert.assertEquals(KeySupport.getKeyLength(params.getDataEncryptionCredential().getSecretKey()), Integer.valueOf(256));
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM);
        Assert.assertNotNull(params.getDataKeyInfoGenerator());
        Assert.assertTrue(KeyAgreementKeyInfoGenerator.class.isInstance(params.getDataKeyInfoGenerator()));
        
        KeyAgreementCredential kaCred = KeyAgreementCredential.class.cast(params.getDataEncryptionCredential());
        Assert.assertEquals(kaCred.getParameters().size(), 1);
        Assert.assertTrue(kaCred.getParameters().contains(PBKDF2.class));
    }
    
    @Test
    public void testGetEffectiveKeyAgreementConfiguration() {
        SAMLMetadataKeyAgreementEncryptionConfiguration ecConfig1 = new SAMLMetadataKeyAgreementEncryptionConfiguration();
        ecConfig1.setMetadataUseKeyWrap(KeyWrap.Always);
        config1.setKeyAgreementConfigurations(Map.of("EC", ecConfig1));
        
        SAMLMetadataKeyAgreementEncryptionConfiguration ecConfig2 = new SAMLMetadataKeyAgreementEncryptionConfiguration();
        ecConfig2.setAlgorithm(EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        ecConfig2.setParameters(Set.of(new PBKDF2()));
        ecConfig2.setMetadataUseKeyWrap(KeyWrap.IfNotIndicated);
        config2.setKeyAgreementConfigurations(Map.of("EC", ecConfig2));
        
        SAMLMetadataKeyAgreementEncryptionConfiguration ecConfig3 = new SAMLMetadataKeyAgreementEncryptionConfiguration();
        ecConfig3.setAlgorithm("SomeAlgo");
        ecConfig3.setParameters(Set.of(new ConcatKDF()));
        ecConfig3.setMetadataUseKeyWrap(KeyWrap.Default);
        config3.setKeyAgreementConfigurations(Map.of("EC", ecConfig3));
        
        SAMLMetadataKeyAgreementEncryptionConfiguration config = resolver.getEffectiveKeyAgreementConfiguration(criteriaSet, ecCred1);
        
        Assert.assertEquals(config.getAlgorithm(), EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        Assert.assertEquals(config.getMetadataUseKeyWrap(), KeyWrap.Always);
        Assert.assertEquals(config.getParameters().size(), 1);
        Assert.assertTrue(PBKDF2.class.isInstance(config.getParameters().iterator().next()));
    }
    
    @Test
    public void testDefaultKeyAgreementUseKeyWrap() {
        KeyAgreementEncryptionConfiguration ecConfig = new KeyAgreementEncryptionConfiguration();
        ecConfig.setAlgorithm(EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        ecConfig.setParameters(Set.of());
        BasicEncryptionConfiguration encConfig = new BasicEncryptionConfiguration();
        encConfig.setKeyAgreementConfigurations(Map.of("EC", ecConfig));
        CriteriaSet criteria = new CriteriaSet(new EncryptionConfigurationCriterion(encConfig));
        
        // Check default value
        Assert.assertEquals(resolver.getDefaultKeyAgreemenUseKeyWrap(), KeyWrap.Default);
        
        SAMLMetadataKeyAgreementEncryptionConfiguration config = resolver.getEffectiveKeyAgreementConfiguration(criteria, ecCred1);
        Assert.assertEquals(config.getMetadataUseKeyWrap(), KeyWrap.Default);
        
        resolver.setDefaultKeyAgreementUseKeyWrap(KeyWrap.Always);
        Assert.assertEquals(resolver.getDefaultKeyAgreemenUseKeyWrap(), KeyWrap.Always);
        
        config = resolver.getEffectiveKeyAgreementConfiguration(criteria, ecCred1);
        Assert.assertEquals(config.getMetadataUseKeyWrap(), KeyWrap.Always);
    }
    
    @Test
    public void testMultipleKeyDescriptors() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(dsaCred1KeyName, UsageType.SIGNING, dsaCred1.getPublicKey()));
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testOnlySigningDescriptor() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(dsaCred1KeyName, UsageType.SIGNING, dsaCred1.getPublicKey()));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params);
    }
    
    @Test
    public void testDSACredWithUnspecifiedUse() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(dsaCred1KeyName, null, dsaCred1.getPublicKey()));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params);
    }
    
    @Test
    public void testRSACredWithUnspecifiedUse() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, null, rsaCred1.getPublicKey()));
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
    }
    
    @Test
    public void testKeyInfoGenerationProfile() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        resolver.setAutoGenerateDataEncryptionCredential(true);
        
        criteriaSet.add(new KeyInfoGenerationProfileCriterion("testKeyInfoProfile"));
        
        defaultDataEncryptionKeyInfoGeneratorManager.setUseDefaultManager(true);
        defaultKeyTransportKeyInfoGeneratorManager.setUseDefaultManager(true);
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params.getDataKeyInfoGenerator());
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        defaultDataEncryptionKeyInfoGeneratorManager.setUseDefaultManager(false);
        defaultKeyTransportKeyInfoGeneratorManager.setUseDefaultManager(false);
        
        params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params.getDataKeyInfoGenerator());
        Assert.assertNull(params.getKeyTransportKeyInfoGenerator());
        
        defaultDataEncryptionKeyInfoGeneratorManager.setUseDefaultManager(false);
        defaultKeyTransportKeyInfoGeneratorManager.setUseDefaultManager(false);
        defaultDataEncryptionKeyInfoGeneratorManager.registerFactory("testKeyInfoProfile", new BasicKeyInfoGeneratorFactory());
        defaultKeyTransportKeyInfoGeneratorManager.registerFactory("testKeyInfoProfile", new BasicKeyInfoGeneratorFactory());
        
        params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNotNull(params.getDataKeyInfoGenerator());
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
    }
    
    @Test
    public void testResolve() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey()));
        
        Iterable<EncryptionParameters> paramsIter = resolver.resolve(criteriaSet);
        Assert.assertNotNull(paramsIter);
        
        Iterator<EncryptionParameters> iterator = paramsIter.iterator();
        Assert.assertNotNull(iterator);
        
        Assert.assertTrue(iterator.hasNext());
        
        EncryptionParameters params = iterator.next();
        
        Assert.assertNotNull(params);
        Assert.assertEquals(params.getKeyTransportEncryptionCredential().getPublicKey(), rsaCred1.getPublicKey());
        Assert.assertEquals(params.getKeyTransportEncryptionAlgorithm(), defaultRSAKeyTransportAlgo);
        Assert.assertNotNull(params.getKeyTransportKeyInfoGenerator());
        
        Assert.assertNull(params.getDataEncryptionCredential());
        Assert.assertEquals(params.getDataEncryptionAlgorithm(), defaultAES128DataAlgo);
        Assert.assertNull(params.getDataKeyInfoGenerator());
        
        Assert.assertFalse(iterator.hasNext());
    }
    
    @Test
    public void testNoCredentials() throws ResolverException {
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params);
    }
    
    @Test
    public void testNoKeyTransportAlgorithms() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey())); 
        config3.setKeyTransportEncryptionAlgorithms(new ArrayList<String>());
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params);
    }
    
    @Test
    public void testNoDataEncryptionAlgorithmForEncrypterAutoGen() throws ResolverException {
        roleDesc.getKeyDescriptors().add(buildKeyDescriptor(rsaCred1KeyName, UsageType.ENCRYPTION, rsaCred1.getPublicKey())); 
        config3.setDataEncryptionAlgorithms(new ArrayList<String>());
        
        EncryptionParameters params = resolver.resolveSingle(criteriaSet);
        
        Assert.assertNull(params);
    }
    
    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testNullCriteriaSet() throws ResolverException {
        resolver.resolve(null);
    }

    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testAbsentCriterion() throws ResolverException {
        resolver.resolve(new CriteriaSet());
    }
    
    
    
    
    // Helper methods
    
    private RoleDescriptor buildRoleDescriptorSkeleton() {
        EntityDescriptor entityDesc = buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        entityDesc.setEntityID(targetEntityID);
        
        SPSSODescriptor spSSODesc = buildXMLObject(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        spSSODesc.setParent(entityDesc);
        
        return spSSODesc;
    }
    
    private KeyDescriptor buildKeyDescriptor(String keyName, UsageType use, Object ... contentItems) {
        KeyDescriptor keyDesc = buildXMLObject(KeyDescriptor.DEFAULT_ELEMENT_NAME);
        KeyInfo keyInfo = buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        
        for (Object contentItem : contentItems) {
            if (contentItem instanceof PublicKey) {
                try {
                    KeyInfoSupport.addPublicKey(keyInfo, (PublicKey) contentItem);
                } catch (EncodingException e) {
                    throw new RuntimeException("EncodingException adding public key to KeyInfo", e);
                }
            } else if (contentItem instanceof X509Certificate) {
                try {
                    KeyInfoSupport.addCertificate(keyInfo, (X509Certificate) contentItem);
                } catch (CertificateEncodingException e) {
                    throw new RuntimeException("CertificateEncodingException ading cert to KeyInfo", e);
                }
            } else {
                throw new RuntimeException("Saw unknown KeyInfo content type: " + contentItem.getClass().getName());
            }
        }
        
        if (keyName != null) {
            KeyInfoSupport.addKeyName(keyInfo, keyName);
        }
        
        keyDesc.setKeyInfo(keyInfo);
        
        if (use != null) {
            keyDesc.setUse(use);
        }
        
        return keyDesc;
    }
    
    private EncryptionMethod buildEncryptionMethod(String algorithm) {
       EncryptionMethod encMethod = buildXMLObject(EncryptionMethod.DEFAULT_ELEMENT_NAME); 
       encMethod.setAlgorithm(algorithm);
       return encMethod;
    }
    
    
    // Test utility classes
    
    public class MapBasedKeyTransportAlgorithmPredicate implements KeyTransportAlgorithmPredicate {
        private Map<String,String> algoMap;
        
        public MapBasedKeyTransportAlgorithmPredicate(Map<String,String> map) {
            algoMap = map;
        }
        
        public boolean test(@Nullable SelectionInput input) {
            return this.algoMap.get(input.getDataEncryptionAlgorithm()).equals(input.getKeyTransportAlgorithm());
        }
    }

}
