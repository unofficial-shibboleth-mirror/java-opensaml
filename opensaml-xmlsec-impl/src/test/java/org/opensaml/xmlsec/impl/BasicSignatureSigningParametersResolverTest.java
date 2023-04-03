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

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.crypto.SecretKey;

import net.shibboleth.shared.logic.ConstraintViolationException;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.criterion.KeyInfoGenerationProfileCriterion;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.impl.BasicKeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("javadoc")
public class BasicSignatureSigningParametersResolverTest extends XMLObjectBaseTestCase {
    
    private BasicSignatureSigningParametersResolver resolver;
    
    private CriteriaSet criteriaSet;
    
    private SignatureSigningConfigurationCriterion criterion;
    
    private BasicSignatureSigningConfiguration config1, config2, config3;
    
    private Credential rsaCred, dsaCred, ecCred, hmacCred;
    
    private String defaultReferenceDigest = SignatureConstants.ALGO_ID_DIGEST_SHA1;
    
    private String defaultC14N = SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS;
    
    private String defaultRSAAlgo = SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1;
    private String defaultDSAAlgo = SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1;
    private String defaultECAlgo = SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1;
    private String defaultHMACAlgo = SignatureConstants.ALGO_ID_MAC_HMAC_SHA1;
    
    private Integer defaultHMACOutputLength = 128;
     
    private NamedKeyInfoGeneratorManager defaultKeyInfoGeneratorManager = new NamedKeyInfoGeneratorManager();
    
    @BeforeClass
    public void buildCredentials() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair rsaKeyPair = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_RSA, 2048, null);
        rsaCred = CredentialSupport.getSimpleCredential(rsaKeyPair.getPublic(), rsaKeyPair.getPrivate());
        
        KeyPair dsaKeyPair = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_DSA, 1024, null);
        dsaCred = CredentialSupport.getSimpleCredential(dsaKeyPair.getPublic(), dsaKeyPair.getPrivate()); 
        
        try {
            KeyPair ecKeyPair = KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, 256, null);
            ecCred = CredentialSupport.getSimpleCredential(ecKeyPair.getPublic(), ecKeyPair.getPrivate()); 
        } catch (NoSuchAlgorithmException e) {
            // EC support isn't universal, e.g. OpenJDK 7 doesn't ship with an EC provider out-of-the-box.
            // Just ignore unsupported algorithm failures here for now.
        }
        
        SecretKey hmacKey = KeySupport.generateKey(JCAConstants.KEY_ALGO_AES, 128, null);
        hmacCred = CredentialSupport.getSimpleCredential(hmacKey);
    }
    
    @BeforeMethod
    public void setUp() {
        resolver = new BasicSignatureSigningParametersResolver();
        
        config1 = new BasicSignatureSigningConfiguration();
        config2 = new BasicSignatureSigningConfiguration();
        config3 = new BasicSignatureSigningConfiguration();
        
        // Set these as defaults on the last config in the chain, just so don't have to set in every test.
        config3.setSignatureAlgorithms(List.of(defaultRSAAlgo, defaultDSAAlgo, defaultECAlgo, defaultHMACAlgo));
        config3.setSignatureReferenceDigestMethods(Collections.singletonList(defaultReferenceDigest));
        config3.setSignatureCanonicalizationAlgorithm(defaultC14N);
        config3.setSignatureHMACOutputLength(defaultHMACOutputLength);
        
        BasicKeyInfoGeneratorFactory basicFactory = new BasicKeyInfoGeneratorFactory();
        X509KeyInfoGeneratorFactory x509Factory = new X509KeyInfoGeneratorFactory();
        defaultKeyInfoGeneratorManager = new NamedKeyInfoGeneratorManager();
        defaultKeyInfoGeneratorManager.registerDefaultFactory(basicFactory);
        defaultKeyInfoGeneratorManager.registerDefaultFactory(x509Factory);
        config3.setKeyInfoGeneratorManager(defaultKeyInfoGeneratorManager);
        
        criterion = new SignatureSigningConfigurationCriterion(config1, config2, config3);
        
        criteriaSet = new CriteriaSet(criterion);
    }
    
    @Test
    public void testBasicRSA() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred));
        
        final SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertEquals(params.getSigningCredential(), rsaCred);
        assertEquals(params.getSignatureAlgorithm(), defaultRSAAlgo);
        assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        assertNull(params.getSignatureReferenceCanonicalizationAlgorithm());
        assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        assertNull(params.getSignatureHMACOutputLength());
        assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testRSAWithAlgorithmOverride() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred));
        
        config2.setSignatureAlgorithms(Collections.singletonList(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
        
        final SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertEquals(params.getSigningCredential(), rsaCred);
        assertEquals(params.getSignatureAlgorithm(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        assertNull(params.getSignatureReferenceCanonicalizationAlgorithm());
        assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        assertNull(params.getSignatureHMACOutputLength());
        assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testRSAWithBlacklist() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred));
        config1.setExcludedAlgorithms(List.of(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1, SignatureConstants.ALGO_ID_DIGEST_SHA1));
        
        // Deliberately putting SHA-1 variants first here.  They should be filtered out.
        config2.setSignatureAlgorithms(List.of(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1, SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
        config2.setSignatureReferenceDigestMethods(List.of(SignatureConstants.ALGO_ID_DIGEST_SHA1, SignatureConstants.ALGO_ID_DIGEST_SHA256));
        
        final SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertEquals(params.getSigningCredential(), rsaCred);
        assertEquals(params.getSignatureAlgorithm(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        assertEquals(params.getSignatureReferenceDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
        assertNull(params.getSignatureReferenceCanonicalizationAlgorithm());
        assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        assertNull(params.getSignatureHMACOutputLength());
        assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testRSAWithWhitelist() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred));
        config1.setIncludedAlgorithms(List.of(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256, SignatureConstants.ALGO_ID_DIGEST_SHA256));
        
        // Deliberately putting SHA-1 variants first here.  They should be filtered out.
        config2.setSignatureAlgorithms(List.of(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1, SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
        config2.setSignatureReferenceDigestMethods(List.of(SignatureConstants.ALGO_ID_DIGEST_SHA1, SignatureConstants.ALGO_ID_DIGEST_SHA256));
        
        final SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertEquals(params.getSigningCredential(), rsaCred);
        assertEquals(params.getSignatureAlgorithm(), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        assertEquals(params.getSignatureReferenceDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
        assertNull(params.getSignatureReferenceCanonicalizationAlgorithm());
        assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        assertNull(params.getSignatureHMACOutputLength());
        assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testBasicDSA() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(dsaCred));
        
        final SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertEquals(params.getSigningCredential(), dsaCred);
        assertEquals(params.getSignatureAlgorithm(), defaultDSAAlgo);
        assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        assertNull(params.getSignatureReferenceCanonicalizationAlgorithm());
        assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        assertNull(params.getSignatureHMACOutputLength());
        assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testBasicEC() throws ResolverException {
        // EC support isn't universal
        if (ecCred != null) {
            config1.setSigningCredentials(Collections.singletonList(ecCred));
            
            final SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
            assert params != null;
            
            assertEquals(params.getSigningCredential(), ecCred);
            assertEquals(params.getSignatureAlgorithm(), defaultECAlgo);
            assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
            assertNull(params.getSignatureReferenceCanonicalizationAlgorithm());
            assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
            assertNull(params.getSignatureHMACOutputLength());
            assertNotNull(params.getKeyInfoGenerator());
        }
    }
    
    @Test
    public void testBasicHMAC() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(hmacCred));
        
        final SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertEquals(params.getSigningCredential(), hmacCred);
        assertEquals(params.getSignatureAlgorithm(), defaultHMACAlgo);
        assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        assertNull(params.getSignatureReferenceCanonicalizationAlgorithm());
        assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        assertEquals(params.getSignatureHMACOutputLength(), defaultHMACOutputLength);
        assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testHMACWithOverrides() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(hmacCred));
        
        config2.setSignatureAlgorithms(Collections.singletonList(SignatureConstants.ALGO_ID_MAC_HMAC_SHA256));
        config2.setSignatureReferenceDigestMethods(Collections.singletonList(SignatureConstants.ALGO_ID_DIGEST_SHA256));
        config2.setSignatureHMACOutputLength(160);
        
        final SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertEquals(params.getSigningCredential(), hmacCred);
        assertEquals(params.getSignatureAlgorithm(), SignatureConstants.ALGO_ID_MAC_HMAC_SHA256);
        assertEquals(params.getSignatureReferenceDigestMethod(), SignatureConstants.ALGO_ID_DIGEST_SHA256);
        assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        assertNull(params.getSignatureReferenceCanonicalizationAlgorithm());
        assertEquals(params.getSignatureHMACOutputLength(), Integer.valueOf(160));
        assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testC14NOverride() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred));
        
        config2.setSignatureCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N11_WITH_COMMENTS);
        
        final SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertEquals(params.getSigningCredential(), rsaCred);
        assertEquals(params.getSignatureAlgorithm(), defaultRSAAlgo);
        assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        assertEquals(params.getSignatureCanonicalizationAlgorithm(), SignatureConstants.ALGO_ID_C14N11_WITH_COMMENTS);
        assertNull(params.getSignatureReferenceCanonicalizationAlgorithm());
        assertNull(params.getSignatureHMACOutputLength());
        assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testReferenceC14NOverride() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred));
        
        config2.setSignatureReferenceCanonicalizationAlgorithm(SignatureConstants.TRANSFORM_C14N_EXCL_WITH_COMMENTS);
        
        final SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertEquals(params.getSigningCredential(), rsaCred);
        assertEquals(params.getSignatureAlgorithm(), defaultRSAAlgo);
        assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        assertEquals(params.getSignatureReferenceCanonicalizationAlgorithm(), SignatureConstants.TRANSFORM_C14N_EXCL_WITH_COMMENTS);
        assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        assertNull(params.getSignatureHMACOutputLength());
        assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testMultipleCreds() throws ResolverException {
        config1.setSigningCredentials(List.of(rsaCred, dsaCred));
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertEquals(params.getSigningCredential(), rsaCred);
        assertEquals(params.getSignatureAlgorithm(), defaultRSAAlgo);
        assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        assertNull(params.getSignatureReferenceCanonicalizationAlgorithm());
        assertNull(params.getSignatureHMACOutputLength());
        assertNotNull(params.getKeyInfoGenerator());
        
        config1.setSigningCredentials(List.of(dsaCred, rsaCred));
        
        params = resolver.resolveSingle(criteriaSet);
        assert params != null;        

        assertEquals(params.getSigningCredential(), dsaCred);
        assertEquals(params.getSignatureAlgorithm(), defaultDSAAlgo);
        assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        assertNull(params.getSignatureReferenceCanonicalizationAlgorithm());
        assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        assertNull(params.getSignatureHMACOutputLength());
        assertNotNull(params.getKeyInfoGenerator());
        
        // EC not supported in all JREs out-of-the-box.
        if (ecCred != null) {
            config1.setSigningCredentials(List.of(ecCred, dsaCred, rsaCred));
            
            params = resolver.resolveSingle(criteriaSet);
            assert params != null;            

            assertEquals(params.getSigningCredential(), ecCred);
            assertEquals(params.getSignatureAlgorithm(), defaultECAlgo);
            assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
            assertNull(params.getSignatureReferenceCanonicalizationAlgorithm());
            assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
            assertNull(params.getSignatureHMACOutputLength());
            assertNotNull(params.getKeyInfoGenerator());
        }
        
        config1.setSigningCredentials(List.of(hmacCred, dsaCred, rsaCred));
        
        params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertEquals(params.getSigningCredential(), hmacCred);
        assertEquals(params.getSignatureAlgorithm(), defaultHMACAlgo);
        assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        assertNull(params.getSignatureReferenceCanonicalizationAlgorithm());
        assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        assertEquals(params.getSignatureHMACOutputLength(), defaultHMACOutputLength);
        assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testCredOverrides() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(dsaCred));
        
        config2.setSigningCredentials(List.of(rsaCred, dsaCred, hmacCred));
        config2.setSignatureAlgorithms(Collections.singletonList(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256));
        
        final SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertEquals(params.getSigningCredential(), dsaCred);
        assertEquals(params.getSignatureAlgorithm(), defaultDSAAlgo);
        assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        assertNull(params.getSignatureReferenceCanonicalizationAlgorithm());
        assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        assertNull(params.getSignatureHMACOutputLength());
        assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testKeyInfoGenerationProfile() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred));
        
        criteriaSet.add(new KeyInfoGenerationProfileCriterion("testKeyInfoProfile"));
        
        defaultKeyInfoGeneratorManager.setUseDefaultManager(true);
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertNotNull(params.getKeyInfoGenerator());
        
        defaultKeyInfoGeneratorManager.setUseDefaultManager(false);
        
        params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertNull(params.getKeyInfoGenerator());
        
        defaultKeyInfoGeneratorManager.setUseDefaultManager(false);
        defaultKeyInfoGeneratorManager.registerFactory("testKeyInfoProfile", new BasicKeyInfoGeneratorFactory());
        
        params = resolver.resolveSingle(criteriaSet);
        assert params != null;
        
        assertNotNull(params.getKeyInfoGenerator());
    }
    
    @Test
    public void testResolve() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred));
        
        Iterable<SignatureSigningParameters> paramsIter = resolver.resolve(criteriaSet);
        assertNotNull(paramsIter);
        
        Iterator<SignatureSigningParameters> iterator = paramsIter.iterator();
        assertNotNull(iterator);
        
        assertTrue(iterator.hasNext());
        
        final SignatureSigningParameters params =iterator.next();
        
        assertNotNull(params);
        assertEquals(params.getSigningCredential(), rsaCred);
        assertEquals(params.getSignatureAlgorithm(), defaultRSAAlgo);
        assertEquals(params.getSignatureReferenceDigestMethod(), defaultReferenceDigest);
        assertEquals(params.getSignatureCanonicalizationAlgorithm(), defaultC14N);
        assertNull(params.getSignatureHMACOutputLength());
        assertNotNull(params.getKeyInfoGenerator());
        
        assertFalse(iterator.hasNext());
    }
    
    @Test
    public void testNoCredentials() throws ResolverException {
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        assertNull(params);
        
    }
    
    @Test
    public void testNoAlgorithms() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred));
        config3.setSignatureAlgorithms(new ArrayList<String>());
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNull(params);
    }
    
    @Test
    public void testNoReferenceDigestMethods() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred));
        config3.setSignatureReferenceDigestMethods(new ArrayList<String>());
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNull(params);
    }
    
    @Test
    public void testNoC14NAlgorithm() throws ResolverException {
        config1.setSigningCredentials(Collections.singletonList(rsaCred));
        config3.setSignatureCanonicalizationAlgorithm(null);
        
        SignatureSigningParameters params = resolver.resolveSingle(criteriaSet);
        
        assertNull(params);
    }
    
    @Test
    public void testNullCriteriaSet() throws ResolverException {
        assertNull(resolver.resolveSingle(null));
    }

    @Test(expectedExceptions=ConstraintViolationException.class)
    public void testAbsentCriterion() throws ResolverException {
        resolver.resolve(new CriteriaSet());
    }

}
