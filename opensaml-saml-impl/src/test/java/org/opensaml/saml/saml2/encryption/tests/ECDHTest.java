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

package org.opensaml.saml.saml2.encryption.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.testing.SAMLTestSupport;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.encryption.Encrypter;
import org.opensaml.saml.saml2.encryption.Encrypter.KeyPlacement;
import org.opensaml.saml.saml2.metadata.EncryptionMethod;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.security.SAMLMetadataKeyAgreementEncryptionConfiguration;
import org.opensaml.saml.security.SAMLMetadataKeyAgreementEncryptionConfiguration.KeyWrap;
import org.opensaml.saml.security.impl.MetadataCredentialResolver;
import org.opensaml.saml.security.impl.SAMLMetadataEncryptionParametersResolver;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.credential.impl.CollectionCredentialResolver;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.DecryptionConfiguration;
import org.opensaml.xmlsec.DecryptionParameters;
import org.opensaml.xmlsec.DecryptionParametersResolver;
import org.opensaml.xmlsec.EncryptionConfiguration;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.EncryptionParametersResolver;
import org.opensaml.xmlsec.criterion.DecryptionConfigurationCriterion;
import org.opensaml.xmlsec.criterion.EncryptionConfigurationCriterion;
import org.opensaml.xmlsec.derivation.impl.PBKDF2;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.KeyDerivationMethod;
import org.opensaml.xmlsec.encryption.support.DataEncryptionParameters;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.KeyEncryptionParameters;
import org.opensaml.xmlsec.impl.BasicDecryptionConfiguration;
import org.opensaml.xmlsec.impl.BasicDecryptionParametersResolver;
import org.opensaml.xmlsec.impl.BasicEncryptionConfiguration;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.keyinfo.impl.KeyInfoProvider;
import org.opensaml.xmlsec.keyinfo.impl.LocalKeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.provider.AgreementMethodKeyInfoProvider;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.xml.SerializeSupport;
import net.shibboleth.utilities.java.support.codec.EncodingException;

/**
 *
 */
public class ECDHTest extends XMLObjectBaseTestCase {
    
    private String targetFile;
    
    private Credential recipientCredPrivate;
    private Credential recipientCredPublic;
    private String recipientCredKeyName = "RecipientCredName";
    
    private CollectionCredentialResolver localCredentialResolver;
    private LocalKeyInfoCredentialResolver localKeyInfoResolver;
    
    private Encrypter encrypter;
    private EncryptionParametersResolver encParamsResolver;
    private CriteriaSet encCriteria;
    private BasicEncryptionConfiguration encConfig;
    
    private DecryptionParametersResolver decryptParamsResolver;
    private CriteriaSet decryptCriteria;
    private BasicDecryptionConfiguration decryptConfig;
    
    private MetadataCredentialResolver mdCredResolver;
    
    private RoleDescriptorCriterion roleDescCriterion;
    private RoleDescriptor roleDesc;
    
    private String targetEntityID = "urn:test:foo";
    
    
    @BeforeClass
    public void beforeClass() throws Exception {
        targetFile = "/org/opensaml/saml/saml2/encryption/Assertion.xml";
        
        KeyPair kp = KeySupport.generateKeyPair("EC", new ECGenParameterSpec("secp256r1"), null);
        recipientCredPrivate = new BasicCredential(kp.getPublic(), kp.getPrivate());
        recipientCredPublic = new BasicCredential(kp.getPublic());
        
        mdCredResolver = new MetadataCredentialResolver();
        mdCredResolver.setKeyInfoCredentialResolver(SAMLTestSupport.buildBasicInlineKeyInfoResolver());
        mdCredResolver.initialize();
        
        encParamsResolver = new SAMLMetadataEncryptionParametersResolver(mdCredResolver);
        
        decryptParamsResolver = new BasicDecryptionParametersResolver();
        
        localCredentialResolver = new CollectionCredentialResolver(Set.of(recipientCredPrivate));
        
        List<KeyInfoProvider> keyInfoProviders = new ArrayList<>(XMLSecurityTestingSupport.getBasicInlineKeyInfoProviders());
        keyInfoProviders.add(new AgreementMethodKeyInfoProvider());
        localKeyInfoResolver = new LocalKeyInfoCredentialResolver(keyInfoProviders, localCredentialResolver);
    }
    
    @BeforeMethod
    public void beforeMethod() throws Exception {
        roleDesc = buildRoleDescriptorSkeleton();
        roleDescCriterion = new RoleDescriptorCriterion(roleDesc);
        
        encConfig = new BasicEncryptionConfiguration();
        EncryptionConfigurationCriterion encConfCrit = new EncryptionConfigurationCriterion(encConfig,
                ConfigurationService.get(EncryptionConfiguration.class));
        encCriteria = new CriteriaSet(encConfCrit, roleDescCriterion);
        
        decryptConfig = new BasicDecryptionConfiguration();
        decryptConfig.setDataKeyInfoCredentialResolver(localKeyInfoResolver);
        decryptConfig.setKEKKeyInfoCredentialResolver(localKeyInfoResolver);
        
        decryptCriteria = new CriteriaSet(new DecryptionConfigurationCriterion(decryptConfig,
                ConfigurationService.get(DecryptionConfiguration.class)));
    }
    
    @Test
    public void roundtripDirectDataEncryption() throws Exception {
        KeyDescriptor kd = buildKeyDescriptor(recipientCredKeyName, UsageType.ENCRYPTION, recipientCredPublic.getPublicKey());
        roleDesc.getKeyDescriptors().add(kd);
        
        testRoundtrip(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, null);
    }

    @Test
    public void roundtripDirectDataEncryptionWithEncryptionMethod() throws Exception {
        KeyDescriptor kd = buildKeyDescriptor(recipientCredKeyName, UsageType.ENCRYPTION, recipientCredPublic.getPublicKey());
        kd.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM));
        roleDesc.getKeyDescriptors().add(kd);
        
        testRoundtrip(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM, null);
    }

    @Test
    public void roundtripWithKeyWrapAlways() throws Exception {
        KeyDescriptor kd = buildKeyDescriptor(recipientCredKeyName, UsageType.ENCRYPTION, recipientCredPublic.getPublicKey());
        roleDesc.getKeyDescriptors().add(kd);
        
        SAMLMetadataKeyAgreementEncryptionConfiguration kaConfig = new SAMLMetadataKeyAgreementEncryptionConfiguration();
        kaConfig.setMetadataUseKeyWrap(KeyWrap.Always);
        encConfig.setKeyAgreementConfigurations(Map.of("EC", kaConfig));
        
        testRoundtrip(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
    }

    @Test
    public void roundtripWithKeyWrapAndEncryptionMethods() throws Exception {
        KeyDescriptor kd = buildKeyDescriptor(recipientCredKeyName, UsageType.ENCRYPTION, recipientCredPublic.getPublicKey());
        kd.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM));
        kd.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYWRAP_AES256));
        roleDesc.getKeyDescriptors().add(kd);
        
        testRoundtrip(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM, EncryptionConstants.ALGO_ID_KEYWRAP_AES256);
    }

    @Test
    public void roundtripWithPBKDF2() throws Exception {
        KeyDescriptor kd = buildKeyDescriptor(recipientCredKeyName, UsageType.ENCRYPTION, recipientCredPublic.getPublicKey());
        roleDesc.getKeyDescriptors().add(kd);
        
        SAMLMetadataKeyAgreementEncryptionConfiguration kaConfig = new SAMLMetadataKeyAgreementEncryptionConfiguration();
        PBKDF2 kdf = new PBKDF2();
        kdf.initialize();
        kaConfig.setParameters(Set.of(kdf));
        encConfig.setKeyAgreementConfigurations(Map.of("EC", kaConfig));
        
        testRoundtrip(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, null, EncryptionConstants.ALGO_ID_KEYDERIVATION_PBKDF2);
    }

    @Test
    public void roundtripWithKeyPlacementPeer() throws Exception {
        KeyDescriptor kd = buildKeyDescriptor(recipientCredKeyName, UsageType.ENCRYPTION, recipientCredPublic.getPublicKey());
        kd.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM));
        kd.getEncryptionMethods().add(buildEncryptionMethod(EncryptionConstants.ALGO_ID_KEYWRAP_AES256));
        roleDesc.getKeyDescriptors().add(kd);
        
        testRoundtrip(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM, EncryptionConstants.ALGO_ID_KEYWRAP_AES256, KeyPlacement.PEER);
    }
    
    
    
    //
    // Helpers
    //

    private void testRoundtrip(String expectedDataAlgo, String expectedKEKAlgo, Encrypter.KeyPlacement keyPlacement) throws Exception {
        testRoundtrip(expectedDataAlgo, expectedKEKAlgo, null, keyPlacement);
    }
    
    private void testRoundtrip(String expectedDataAlgo, String expectedKEKAlgo) throws Exception {
        testRoundtrip(expectedDataAlgo, expectedKEKAlgo, null, KeyPlacement.INLINE);
    }
    
    private void testRoundtrip(String expectedDataAlgo, String expectedKEKAlgo, String expectedKDFAlgo) throws Exception {
        testRoundtrip(expectedDataAlgo, expectedKEKAlgo, expectedKEKAlgo, KeyPlacement.INLINE);
    }
    
    private void testRoundtrip(String expectedDataAlgo, String expectedKEKAlgo, String expectedKDFAlgo, Encrypter.KeyPlacement keyPlacement) throws Exception {
        // Encrypt
        Assertion assertionOrig = (Assertion) unmarshallElement(targetFile);
        
        EncryptionParameters encParams = encParamsResolver.resolveSingle(encCriteria);
        Assert.assertNotNull(encParams);
        
        DataEncryptionParameters dataEncParams = new DataEncryptionParameters(encParams);
        List<KeyEncryptionParameters> kekParams = encParams.getKeyTransportEncryptionCredential() != null ?
                List.of(new KeyEncryptionParameters(encParams, null)) : Collections.emptyList();
                
        encrypter = new Encrypter(dataEncParams, kekParams);
        encrypter.setKeyPlacement(keyPlacement);
        
        EncryptedAssertion encryptedAssertionOrig = encrypter.encrypt(assertionOrig);
        Assert.assertNotNull(encryptedAssertionOrig);
        Assert.assertNotNull(encryptedAssertionOrig.getEncryptedData().getKeyInfo());
        
        if (expectedDataAlgo != null) {
            Assert.assertEquals(encryptedAssertionOrig.getEncryptedData().getEncryptionMethod().getAlgorithm(), expectedDataAlgo);
        }
        
        EncryptedKey encryptedKey = null;
        switch(keyPlacement) {
            case INLINE:
                encryptedKey = !encryptedAssertionOrig.getEncryptedData().getKeyInfo().getEncryptedKeys().isEmpty()
                    ? encryptedAssertionOrig.getEncryptedData().getKeyInfo().getEncryptedKeys().get(0) : null;
                break;
            case PEER:
                encryptedKey = !encryptedAssertionOrig.getEncryptedKeys().isEmpty()
                    ? encryptedAssertionOrig.getEncryptedKeys().get(0) : null;
                break;
        };
            
        if (expectedKEKAlgo != null) {
            Assert.assertNotNull(encryptedKey);
            Assert.assertEquals(encryptedKey.getEncryptionMethod().getAlgorithm(), expectedKEKAlgo);
        }
        
        if (expectedKDFAlgo != null) {
            KeyDerivationMethod kdm = null;
            if (encryptedKey != null) {
                kdm = (KeyDerivationMethod) encryptedKey.getKeyInfo().getAgreementMethods().get(0).getUnknownXMLObjects(KeyDerivationMethod.DEFAULT_ELEMENT_NAME).get(0); 
            } else {
                kdm = (KeyDerivationMethod) encryptedAssertionOrig.getEncryptedData().getKeyInfo().getAgreementMethods().get(0).getUnknownXMLObjects(KeyDerivationMethod.DEFAULT_ELEMENT_NAME).get(0);
            }
            Assert.assertNotNull(kdm);
            Assert.assertEquals(kdm.getAlgorithm(), expectedKDFAlgo);
        }
        
        // Serialize out and back in
        Element domEncrypted = XMLObjectSupport.marshall(encryptedAssertionOrig);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializeSupport.writeNode(domEncrypted, baos);
        baos.flush();
        byte[] bytesEncrypted = baos.toByteArray();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytesEncrypted);
        EncryptedAssertion encryptedAssertion = (EncryptedAssertion) XMLObjectSupport.unmarshallFromInputStream(
                XMLObjectProviderRegistrySupport.getParserPool(), bais);
        Assert.assertNotNull(encryptedAssertion);
        
        // Decrypt
        DecryptionParameters decryptParams = decryptParamsResolver.resolveSingle(decryptCriteria);
        
        Decrypter decrypter = new Decrypter(decryptParams);
        
        Assertion decryptedAssertion = decrypter.decrypt(encryptedAssertion);
        Assert.assertNotNull(decryptedAssertion);
        
        assertXMLEquals(assertionOrig.getDOM().getOwnerDocument(), decryptedAssertion);
    }
    
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

}
