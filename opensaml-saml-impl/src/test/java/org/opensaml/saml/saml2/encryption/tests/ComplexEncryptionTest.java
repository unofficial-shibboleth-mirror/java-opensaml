/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import java.util.ArrayList;
import java.util.List;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.EncryptedAttribute;
import org.opensaml.saml.saml2.encryption.Encrypter;
import org.opensaml.saml.saml2.encryption.Encrypter.KeyPlacement;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.CarriedKeyName;
import org.opensaml.xmlsec.encryption.DataReference;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.ReferenceList;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.encryption.support.DataEncryptionParameters;
import org.opensaml.xmlsec.encryption.support.KeyEncryptionParameters;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoGenerator;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyName;
import org.opensaml.xmlsec.signature.RetrievalMethod;

import com.google.common.base.Strings;

/**
 * Tests for encryption using single and multicast key encryption keys, 
 * and peer vs. inline key placement.
 */
@SuppressWarnings({"null", "javadoc"})
public class ComplexEncryptionTest extends XMLObjectBaseTestCase {
    
    private Encrypter encrypter;
    private DataEncryptionParameters encParams;
    private List<KeyEncryptionParameters> kekParamsList;
    private KeyEncryptionParameters kekParamsRSA, kekParamsAES;
    
    private KeyInfo keyInfo, kekKeyInfoRSA;
    
    private String algoURI, kekURIRSA, kekURIAES;
    private String expectedKeyNameRSA;
    private String expectedRecipientRSA, expectedRecipientAES;

    /**
     * Constructor.
     *
     */
    public ComplexEncryptionTest() {
        expectedKeyNameRSA = "RSAKeyWrapper";
        expectedRecipientRSA = "RSARecipient";
        expectedRecipientAES = "AESRecipient";
        algoURI = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
        kekURIRSA = EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15;
        kekURIAES = EncryptionConstants.ALGO_ID_KEYWRAP_AES128;
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        Credential encCred = AlgorithmSupport.generateSymmetricKeyAndCredential(algoURI);
        Credential kekCredAES = AlgorithmSupport.generateSymmetricKeyAndCredential(kekURIAES);
        Credential kekCredRSA = AlgorithmSupport.generateKeyPairAndCredential(kekURIRSA, 2048, false);
        
        encParams = new DataEncryptionParameters();
        encParams.setAlgorithm(algoURI);
        encParams.setEncryptionCredential(encCred);
        
        kekParamsAES = new KeyEncryptionParameters();
        kekParamsAES.setAlgorithm(kekURIAES);
        kekParamsAES.setEncryptionCredential(kekCredAES);
        
        kekParamsRSA = new KeyEncryptionParameters();
        kekParamsRSA.setAlgorithm(kekURIRSA);
        kekParamsRSA.setEncryptionCredential(kekCredRSA);
        
        kekParamsList = new ArrayList<>();
        
        keyInfo = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        kekKeyInfoRSA = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
    }

    /**
     * Test encryption with a single key encryption key with key placement inline.
     */
    @Test
    public void testSingleKEKInline() {
        final Assertion target = (Assertion) unmarshallElement("/org/opensaml/saml/saml2/encryption/Assertion.xml");
        assert target != null;
        
        KeyName keyName = (KeyName) buildXMLObject(KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(expectedKeyNameRSA);
        kekKeyInfoRSA.getKeyNames().add(keyName);
        kekParamsRSA.setKeyInfoGenerator(new StaticKeyInfoGenerator(kekKeyInfoRSA));
        kekParamsList.add(kekParamsRSA);
        
        encrypter = new Encrypter(encParams, kekParamsList);
        encrypter.setKeyPlacement(Encrypter.KeyPlacement.INLINE);
        
        EncryptedAssertion encTarget = null;
        XMLObject encObject = null;
        try {
            encObject = encrypter.encrypt(target);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encObject, "Encrypted object was null");
        Assert.assertTrue(encObject instanceof EncryptedAssertion, 
                "Encrypted object was not an instance of the expected type");
        encTarget = (EncryptedAssertion) encObject;
        assert encTarget != null;
        
        final EncryptedData encData = encTarget.getEncryptedData();
        assert encData != null;
        KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        
        Assert.assertEquals(keyInfo.getEncryptedKeys().size(), 1, 
                "Number of inline EncryptedKeys");
        Assert.assertEquals(encTarget.getEncryptedKeys().size(), 0, 
                "Number of peer EncryptedKeys");
        Assert.assertEquals(keyInfo.getRetrievalMethods().size(), 0,
                "EncryptedData improperly contained a RetrievalMethod");
        
        
        final EncryptedKey encKey = keyInfo.getEncryptedKeys().get(0);
        Assert.assertNotNull(encKey, "EncryptedKey was null");
        
        final EncryptionMethod method = encKey.getEncryptionMethod();
        assert method != null;
        Assert.assertEquals(method.getAlgorithm(), kekURIRSA, "Algorithm attribute");
        keyInfo = encKey.getKeyInfo();
        assert keyInfo != null;
        Assert.assertEquals(keyInfo.getKeyNames().get(0).getValue(), expectedKeyNameRSA, 
                "KeyName");
        
        Assert.assertFalse(Strings.isNullOrEmpty(encKey.getID()),
                "EncryptedKey ID attribute was empty");
        
        Assert.assertNull(encKey.getReferenceList(), "EncryptedKey ReferenceList wasn't null");
        Assert.assertNull(encKey.getCarriedKeyName(), "EncryptedKey CarriedKeyName wasn't null");
    }
    
    /**
     * Test encryption with a single key encryption key with key placement as peer.
     */
    @Test
    public void testSingleKEKPeer() {
        final Assertion target = (Assertion) unmarshallElement("/org/opensaml/saml/saml2/encryption/Assertion.xml");
        assert target != null;

        KeyName keyName = (KeyName) buildXMLObject(KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(expectedKeyNameRSA);
        kekKeyInfoRSA.getKeyNames().add(keyName);
        kekParamsRSA.setKeyInfoGenerator(new StaticKeyInfoGenerator(kekKeyInfoRSA));
        kekParamsList.add(kekParamsRSA);
        
        encrypter = new Encrypter(encParams, kekParamsList);
        encrypter.setKeyPlacement(Encrypter.KeyPlacement.PEER);
        
        EncryptedAssertion encTarget = null;
        XMLObject encObject = null;
        try {
            encObject = encrypter.encrypt(target);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encObject, "Encrypted object was null");
        Assert.assertTrue(encObject instanceof EncryptedAssertion, 
                "Encrypted object was not an instance of the expected type");
        encTarget = (EncryptedAssertion) encObject;
        assert encTarget != null;
        
        final EncryptedData encData = encTarget.getEncryptedData();
        assert encData != null;
        
        KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertEquals(keyInfo.getEncryptedKeys().size(), 0, 
                "Number of inline EncryptedKeys");
        Assert.assertEquals(encTarget.getEncryptedKeys().size(), 1, 
                "Number of peer EncryptedKeys");
        
        
        final EncryptedKey encKey = encTarget.getEncryptedKeys().get(0);
        assert encKey != null;
        
        final EncryptionMethod method = encKey.getEncryptionMethod();
        assert method != null;
        Assert.assertEquals(method.getAlgorithm(), kekURIRSA, "Algorithm attribute");
        keyInfo = encKey.getKeyInfo();
        assert keyInfo != null;
        Assert.assertEquals(keyInfo.getKeyNames().get(0).getValue(), expectedKeyNameRSA, "KeyName");
        
        Assert.assertFalse(Strings.isNullOrEmpty(encKey.getID()),
                "EncryptedKey ID attribute was empty");
        
        keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertEquals(keyInfo.getRetrievalMethods().size(), 1,
                "EncryptedData contained invalid number RetrievalMethods");
        RetrievalMethod rm = keyInfo.getRetrievalMethods().get(0);
        Assert.assertEquals(rm.getType(),
                EncryptionConstants.TYPE_ENCRYPTED_KEY, "EncryptedData RetrievalMethod had incorrect type attribute");
        Assert.assertEquals(rm.getURI(),
                "#" + encKey.getID(), "EncryptedData RetrievalMethod had incorrect URI value");
        
        final ReferenceList reflist = encKey.getReferenceList();
        assert reflist != null;
        Assert.assertEquals(reflist.getDataReferences().size(), 1,
                "EncryptedKey contained invalid number DataReferences");
        DataReference dr = reflist.getDataReferences().get(0);
        Assert.assertEquals(dr.getURI(),
                "#" + encData.getID(), "EncryptedKey DataReference had incorrect URI value");
        Assert.assertNull(encKey.getCarriedKeyName(), "EncryptedKey CarriedKeyName wasn't null");
    }
    
    /** Test encryption with multicast key encryption keys with key placement as peer. */
    @Test
    public void testMulticastKEKPeer() {
        final Assertion target = (Assertion) unmarshallElement("/org/opensaml/saml/saml2/encryption/Assertion.xml");
        assert target != null;
        
        String multicastKeyNameValue = "MulticastDataEncryptionKeyName";
        KeyName keyName = (KeyName) buildXMLObject(KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(multicastKeyNameValue);
        keyInfo.getKeyNames().add(keyName);
        encParams.setKeyInfoGenerator(new StaticKeyInfoGenerator(keyInfo));
        
        kekParamsRSA.setRecipient(expectedRecipientRSA);
        kekParamsList.add(kekParamsRSA);
        kekParamsAES.setRecipient(expectedRecipientAES);
        kekParamsList.add(kekParamsAES);
        
        encrypter = new Encrypter(encParams, kekParamsList);
        encrypter.setKeyPlacement(Encrypter.KeyPlacement.PEER);
        
        EncryptedAssertion encTarget = null;
        XMLObject encObject = null;
        try {
            encObject = encrypter.encrypt(target);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encObject, "Encrypted object was null");
        Assert.assertTrue(encObject instanceof EncryptedAssertion, 
                "Encrypted object was not an instance of the expected type");
        encTarget = (EncryptedAssertion) encObject;
        assert encTarget != null;
        
        final EncryptedData encData = encTarget.getEncryptedData();
        assert encData != null;
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertEquals(keyInfo.getEncryptedKeys().size(), 0, 
                "Number of inline EncryptedKeys");
        Assert.assertEquals(encTarget.getEncryptedKeys().size(), 2, 
                "Number of peer EncryptedKeys");
        
        
        EncryptedKey encKeyRSA = encTarget.getEncryptedKeys().get(0);
        EncryptedKey encKeyAES = encTarget.getEncryptedKeys().get(1);
        Assert.assertNotNull(encKeyRSA, "EncryptedKey was null");
        Assert.assertNotNull(encKeyAES, "EncryptedKey was null");
        
        EncryptionMethod method = encKeyRSA.getEncryptionMethod();
        assert method != null;
        Assert.assertEquals(method.getAlgorithm(), kekURIRSA, "Algorithm attribute");
        method = encKeyAES.getEncryptionMethod();
        assert method != null;
        Assert.assertEquals(method.getAlgorithm(), kekURIAES, "Algorithm attribute");
        
        Assert.assertFalse(Strings.isNullOrEmpty(encKeyRSA.getID()),
                "EncryptedKey ID attribute was empty");
        Assert.assertFalse(Strings.isNullOrEmpty(encKeyAES.getID()),
                "EncryptedKey ID attribute was empty");
        
        Assert.assertEquals(keyInfo.getRetrievalMethods().size(), 0,
                "EncryptedData contained invalid number RetrievalMethods");
        Assert.assertEquals(keyInfo.getKeyNames().size(), 1,
                "EncryptedData contained invalid number KeyNames");
        KeyName encDataKeyName = keyInfo.getKeyNames().get(0);
        Assert.assertEquals(encDataKeyName.getValue(), multicastKeyNameValue, "EncryptedData KeyName value");
        
        DataReference dr = null;
        
        Assert.assertEquals(encKeyRSA.getRecipient(), expectedRecipientRSA,
                "EncryptedKey recipient attribute had invalid value");
        ReferenceList reflist = encKeyRSA.getReferenceList();
        assert reflist != null;
        Assert.assertEquals(reflist.getDataReferences().size(), 1,
                "EncryptedKey contained invalid number DataReferences");
        dr = reflist.getDataReferences().get(0);
        Assert.assertEquals(dr.getURI(),
                "#" + encData.getID(), "EncryptedKey DataReference had incorrect URI value");
        CarriedKeyName carried = encKeyRSA.getCarriedKeyName();
        assert carried != null;
        Assert.assertEquals(carried.getValue(), multicastKeyNameValue,
                "EncrypteKey CarriedKeyName had incorrect value");
        
        Assert.assertEquals(encKeyAES.getRecipient(), expectedRecipientAES,
                "EncryptedKey recipient attribute had invalid value");
        reflist = encKeyAES.getReferenceList();
        assert reflist != null;
        Assert.assertEquals(reflist.getDataReferences().size(), 1,
                "EncryptedKey contained invalid number DataReferences");
        dr = reflist.getDataReferences().get(0);
        Assert.assertEquals(dr.getURI(),
                "#" + encData.getID(), "EncryptedKey DataReference had incorrect URI value");
        carried = encKeyAES.getCarriedKeyName();
        assert carried != null;
        Assert.assertEquals(carried.getValue(), multicastKeyNameValue,
                "EncrypteKey CarriedKeyName had incorrect value");
    }
    
    /** Test that reuse is allowed with same key encryption parameters. */
    @Test
    public void testReuse() {
        final Assertion assertion = (Assertion) unmarshallElement("/org/opensaml/saml/saml2/encryption/Assertion.xml");
        assert assertion != null;
        
        Attribute target = assertion.getAttributeStatements().get(0).getAttributes().get(0);
        Attribute target2 = assertion.getAttributeStatements().get(0).getAttributes().get(1);
        
        KeyName keyName = (KeyName) buildXMLObject(KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(expectedKeyNameRSA);
        kekKeyInfoRSA.getKeyNames().add(keyName);
        kekParamsRSA.setKeyInfoGenerator(new StaticKeyInfoGenerator(kekKeyInfoRSA));
        
        kekParamsList.add(kekParamsRSA);
        
        encrypter = new Encrypter(encParams, kekParamsList);
        encrypter.setKeyPlacement(KeyPlacement.PEER);
        
        XMLObject encObject = null;
        try {
            encObject = encrypter.encrypt(target);
        } catch (final EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encObject, "Encrypted object was null");
        Assert.assertTrue(encObject instanceof EncryptedAttribute, 
                "Encrypted object was not an instance of the expected type");
        
        XMLObject encObject2 = null;
        try {
            encObject2 = encrypter.encrypt(target2);
        } catch (final EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encObject2, "Encrypted object was null");
        Assert.assertTrue(encObject2 instanceof EncryptedAttribute, 
                "Encrypted object was not an instance of the expected type");
    }

}