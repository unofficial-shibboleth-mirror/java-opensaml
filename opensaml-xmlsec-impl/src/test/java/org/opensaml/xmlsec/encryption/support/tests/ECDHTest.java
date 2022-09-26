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

package org.opensaml.xmlsec.encryption.support.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyPair;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
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
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.KeyDerivationMethod;
import org.opensaml.xmlsec.encryption.support.DataEncryptionParameters;
import org.opensaml.xmlsec.encryption.support.Decrypter;
import org.opensaml.xmlsec.encryption.support.Encrypter;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.KeyAgreementEncryptionConfiguration;
import org.opensaml.xmlsec.encryption.support.KeyEncryptionParameters;
import org.opensaml.xmlsec.impl.BasicDecryptionConfiguration;
import org.opensaml.xmlsec.impl.BasicDecryptionParametersResolver;
import org.opensaml.xmlsec.impl.BasicEncryptionConfiguration;
import org.opensaml.xmlsec.impl.BasicEncryptionParametersResolver;
import org.opensaml.xmlsec.keyinfo.impl.KeyInfoProvider;
import org.opensaml.xmlsec.keyinfo.impl.LocalKeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.provider.AgreementMethodKeyInfoProvider;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObject;
import org.opensaml.xmlsec.testing.XMLSecurityTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import net.shibboleth.shared.xml.SerializeSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

/**
 *
 */
public class ECDHTest extends XMLObjectBaseTestCase {
    
    private String targetFile;
    
    private Credential recipientCredPrivate;
    private Credential recipientCredPublic;
    private CollectionCredentialResolver localCredentialResolver;
    private LocalKeyInfoCredentialResolver localKeyInfoResolver;
    
    private Encrypter encrypter;
    private EncryptionParametersResolver encParamsResolver;
    private CriteriaSet encCriteria;
    private BasicEncryptionConfiguration encConfig;
    
    private DecryptionParametersResolver decryptParamsResolver;
    private CriteriaSet decryptCriteria;
    private BasicDecryptionConfiguration decryptConfig;
    
    @BeforeClass
    public void beforeClass() throws Exception {
        targetFile = "/org/opensaml/xmlsec/encryption/support/SimpleEncryptionTest.xml";
        
        KeyPair kp = KeySupport.generateKeyPair("EC", new ECGenParameterSpec("secp256r1"), null);
        recipientCredPrivate = new BasicCredential(kp.getPublic(), kp.getPrivate());
        recipientCredPublic = new BasicCredential(kp.getPublic());
        
        encrypter = new Encrypter();
        encParamsResolver = new BasicEncryptionParametersResolver();
        
        decryptParamsResolver = new BasicDecryptionParametersResolver();
        
        localCredentialResolver = new CollectionCredentialResolver(Set.of(recipientCredPrivate));
        
        List<KeyInfoProvider> keyInfoProviders = new ArrayList<>(XMLSecurityTestingSupport.getBasicInlineKeyInfoProviders());
        keyInfoProviders.add(new AgreementMethodKeyInfoProvider());
        localKeyInfoResolver = new LocalKeyInfoCredentialResolver(keyInfoProviders, localCredentialResolver);
    }
    
    @BeforeMethod
    public void beforeMethod() throws Exception {
        encConfig = new BasicEncryptionConfiguration();
        encCriteria = new CriteriaSet(new EncryptionConfigurationCriterion(encConfig,
                ConfigurationService.get(EncryptionConfiguration.class)));
        
        decryptConfig = new BasicDecryptionConfiguration();
        decryptConfig.setDataKeyInfoCredentialResolver(localKeyInfoResolver);
        decryptConfig.setKEKKeyInfoCredentialResolver(localKeyInfoResolver);
        
        decryptCriteria = new CriteriaSet(new DecryptionConfigurationCriterion(decryptConfig,
                ConfigurationService.get(DecryptionConfiguration.class)));
    }
    
    @Test
    public void roundtripDirectDataEncryption() throws Exception {
        encConfig.setDataEncryptionCredentials(List.of(recipientCredPublic));
        
        testRoundtrip(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, null);
    }

    @Test
    public void roundtripDirectDataEncryptionWithAlgorithmOverrides() throws Exception {
        encConfig.setDataEncryptionCredentials(List.of(recipientCredPublic));
        encConfig.setDataEncryptionAlgorithms(List.of(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM));
        
        testRoundtrip(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM, null);
    }

    @Test
    public void roundtripWithKeyWrap() throws Exception {
        encConfig.setKeyTransportEncryptionCredentials(List.of(recipientCredPublic));
        
        testRoundtrip(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, EncryptionConstants.ALGO_ID_KEYWRAP_AES128);
    }

    @Test
    public void roundtripWithKeyWrapAndAlgorithmOverrides() throws Exception {
        encConfig.setKeyTransportEncryptionCredentials(List.of(recipientCredPublic));
        encConfig.setKeyTransportEncryptionAlgorithms(List.of(EncryptionConstants.ALGO_ID_KEYWRAP_AES256));
        encConfig.setDataEncryptionAlgorithms(List.of(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM));
        
        testRoundtrip(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM, EncryptionConstants.ALGO_ID_KEYWRAP_AES256);
    }

    @Test
    public void roundtripWithPBKDF2() throws Exception {
        encConfig.setDataEncryptionCredentials(List.of(recipientCredPublic));
        
        KeyAgreementEncryptionConfiguration kaConfig = new KeyAgreementEncryptionConfiguration();
        PBKDF2 kdf = new PBKDF2();
        kdf.initialize();
        kaConfig.setParameters(Set.of(kdf));
        encConfig.setKeyAgreementConfigurations(Map.of("EC", kaConfig));
        
        testRoundtrip(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128, null, EncryptionConstants.ALGO_ID_KEYDERIVATION_PBKDF2);
    }

    private void testRoundtrip(String expectedDataAlgo, String expectedKEKAlgo) throws Exception {
        testRoundtrip(expectedDataAlgo, expectedKEKAlgo, null);
    }
    
    private void testRoundtrip(String expectedDataAlgo, String expectedKEKAlgo, String expectedKDFAlgo) throws Exception {
        // Encrypt
        SignableSimpleXMLObject sxoOrig = (SignableSimpleXMLObject) unmarshallElement(targetFile);
        
        EncryptionParameters encParams = encParamsResolver.resolveSingle(encCriteria);
        Assert.assertNotNull(encParams);
        
        DataEncryptionParameters dataEncParams = new DataEncryptionParameters(encParams);
        List<KeyEncryptionParameters> kekParams = encParams.getKeyTransportEncryptionCredential() != null ?
                List.of(new KeyEncryptionParameters(encParams, null)) : Collections.emptyList();
        
        EncryptedData encryptedDataOrig = encrypter.encryptElement(sxoOrig, dataEncParams, kekParams);
        Assert.assertNotNull(encryptedDataOrig);
        Assert.assertNotNull(encryptedDataOrig.getKeyInfo());
        
        if (expectedDataAlgo != null) {
            Assert.assertEquals(encryptedDataOrig.getEncryptionMethod().getAlgorithm(), expectedDataAlgo);
        }
        
        if (expectedKEKAlgo != null) {
            Assert.assertNotNull(encryptedDataOrig.getKeyInfo().getEncryptedKeys().get(0));
            Assert.assertEquals(encryptedDataOrig.getKeyInfo().getEncryptedKeys().get(0).getEncryptionMethod().getAlgorithm(), expectedKEKAlgo);
        }
        
        AgreementMethod agreementMethod = null;
        if (!encryptedDataOrig.getKeyInfo().getEncryptedKeys().isEmpty())  {
            agreementMethod = encryptedDataOrig.getKeyInfo().getEncryptedKeys().get(0).getKeyInfo().getAgreementMethods().get(0);
        } else {
            agreementMethod = encryptedDataOrig.getKeyInfo().getAgreementMethods().get(0);
        }
        Assert.assertNotNull(agreementMethod);
        Assert.assertEquals(agreementMethod.getAlgorithm(), EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES);
        
        if (expectedKDFAlgo != null) {
            KeyDerivationMethod kdm = (KeyDerivationMethod) agreementMethod.getUnknownXMLObjects(KeyDerivationMethod.DEFAULT_ELEMENT_NAME).get(0); 
            Assert.assertNotNull(kdm);
            Assert.assertEquals(kdm.getAlgorithm(), expectedKDFAlgo);
        }
        
        // Serialize out and back in
        Element domEncrypted = XMLObjectSupport.marshall(encryptedDataOrig);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializeSupport.writeNode(domEncrypted, baos);
        baos.flush();
        byte[] bytesEncrypted = baos.toByteArray();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytesEncrypted);
        EncryptedData encryptedData = (EncryptedData) XMLObjectSupport.unmarshallFromInputStream(
                XMLObjectProviderRegistrySupport.getParserPool(), bais);
        Assert.assertNotNull(encryptedData);
        
        // Decrypt
        DecryptionParameters decryptParams = decryptParamsResolver.resolveSingle(decryptCriteria);
        
        Decrypter decrypter = new Decrypter(decryptParams);
        
        XMLObject decryptedXMLObject = decrypter.decryptData(encryptedData);
        Assert.assertNotNull(decryptedXMLObject);
        Assert.assertTrue(decryptedXMLObject instanceof SignableSimpleXMLObject);
        
        assertXMLEquals(sxoOrig.getDOM().getOwnerDocument(), decryptedXMLObject);
    }

}
