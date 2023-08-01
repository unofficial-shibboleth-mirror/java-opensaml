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
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NewEncryptedID;
import org.opensaml.saml.saml2.core.NewID;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.encryption.Encrypter;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.support.DataEncryptionParameters;
import org.opensaml.xmlsec.encryption.support.KeyEncryptionParameters;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoGenerator;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyName;

import com.google.common.base.Strings;

/**
 * Simple tests for encryption.
 */
@SuppressWarnings({"null", "javadoc"})
public class SimpleEncryptionTest extends XMLObjectBaseTestCase {
    
    private Encrypter encrypter;
    private DataEncryptionParameters encParams;
    private KeyEncryptionParameters kekParamsRSA;
    private List<KeyEncryptionParameters> kekParamsList;
    
    private KeyInfo keyInfo;
    
    private String algoURI;
    private String expectedKeyName;
    
    private String kekURIRSA;
    /**
     * Constructor.
     *
     */
    public SimpleEncryptionTest() {
        expectedKeyName = "SuperSecretKey";
        algoURI = EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128;
        
        kekURIRSA = EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15;
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        encParams = new DataEncryptionParameters();
        encParams.setAlgorithm(algoURI);
        encParams.setEncryptionCredential(AlgorithmSupport.generateSymmetricKeyAndCredential(algoURI));
        
        kekParamsRSA = new KeyEncryptionParameters();
        kekParamsRSA.setAlgorithm(kekURIRSA);
        kekParamsRSA.setEncryptionCredential(AlgorithmSupport.generateKeyPairAndCredential(kekURIRSA, 1024, false));
        
        kekParamsList = new ArrayList<>();
        
        keyInfo = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
    }

    /**
     *  Test basic encryption with symmetric key, no key wrap,
     *  set key name in passed KeyInfo object.
     */
    @Test
    public void testAssertion() {
        final Assertion target = (Assertion) unmarshallElement("/org/opensaml/saml/saml2/encryption/Assertion.xml");
        
        final KeyName keyName = (KeyName) buildXMLObject(org.opensaml.xmlsec.signature.KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(expectedKeyName);
        keyInfo.getKeyNames().add(keyName);
        encParams.setKeyInfoGenerator(new StaticKeyInfoGenerator(keyInfo));
        
        encrypter = new Encrypter(encParams, kekParamsList);
        
        EncryptedAssertion encTarget = null;
        XMLObject encObject = null;
        try {
            assert target != null;
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
        
        Assert.assertEquals(encData.getType(), EncryptionConstants.TYPE_ELEMENT, "Type attribute");
        final EncryptionMethod method = encData.getEncryptionMethod();
        assert method != null;
        Assert.assertEquals(method.getAlgorithm(), algoURI, 
                "Algorithm attribute");
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertEquals(keyInfo.getKeyNames().get(0).getValue(), expectedKeyName, 
                "KeyName");
        
        Assert.assertEquals(keyInfo.getEncryptedKeys().size(), 0, 
                "Number of EncryptedKeys");
        
        Assert.assertFalse(Strings.isNullOrEmpty(encData.getID()),
                "EncryptedData ID attribute was empty");
    }
    
    /**
     *  Test basic encryption with symmetric key, no key wrap,
     *  set key name in passed KeyInfo object.
     */
    @Test
    public void testAssertionAsID() {
        final Assertion target = (Assertion) unmarshallElement("/org/opensaml/saml/saml2/encryption/Assertion.xml");
        
        final KeyName keyName = (KeyName) buildXMLObject(org.opensaml.xmlsec.signature.KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(expectedKeyName);
        keyInfo.getKeyNames().add(keyName);
        encParams.setKeyInfoGenerator(new StaticKeyInfoGenerator(keyInfo));
        
        encrypter = new Encrypter(encParams, kekParamsList);
        
        EncryptedID encTarget = null;
        XMLObject encObject = null;
        try {
            assert target != null;
            encObject = encrypter.encryptAsID(target);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encObject, "Encrypted object was null");
        Assert.assertTrue(encObject instanceof EncryptedID, 
                "Encrypted object was not an instance of the expected type");
        encTarget = (EncryptedID) encObject;
        assert encTarget != null;
        final EncryptedData encData = encTarget.getEncryptedData();
        assert encData != null;
        
        Assert.assertEquals(encData.getType(), EncryptionConstants.TYPE_ELEMENT, "Type attribute");
        final EncryptionMethod method = encData.getEncryptionMethod();
        assert method != null;
        Assert.assertEquals(method.getAlgorithm(), algoURI, "Algorithm attribute");
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertEquals(keyInfo.getKeyNames().get(0).getValue(), expectedKeyName, 
                "KeyName");
        
        Assert.assertEquals(keyInfo.getEncryptedKeys().size(), 0, 
                "Number of EncryptedKeys");
        
        Assert.assertFalse(Strings.isNullOrEmpty(encData.getID()),
                "EncryptedData ID attribute was empty");
    }
    
    /**
     *  Test basic encryption with symmetric key, no key wrap,
     *  set key name in passed KeyInfo object.
     */
    @Test
    public void testNameID() {
        final Assertion assertion = (Assertion) unmarshallElement("/org/opensaml/saml/saml2/encryption/Assertion.xml");
        assert assertion != null;
        final Subject sub = assertion.getSubject();
        assert sub != null;
        final NameID target = sub.getNameID();
        
        final KeyName keyName = (KeyName) buildXMLObject(org.opensaml.xmlsec.signature.KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(expectedKeyName);
        keyInfo.getKeyNames().add(keyName);
        encParams.setKeyInfoGenerator(new StaticKeyInfoGenerator(keyInfo));
        
        encrypter = new Encrypter(encParams, kekParamsList);
        
        EncryptedID encTarget = null;
        XMLObject encObject = null;
        try {
            assert target != null;
            encObject = encrypter.encrypt(target);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encObject, "Encrypted object was null");
        Assert.assertTrue(encObject instanceof EncryptedID, 
                "Encrypted object was not an instance of the expected type");
        encTarget = (EncryptedID) encObject;
        assert encTarget != null;
        final EncryptedData encData = encTarget.getEncryptedData();
        assert encData != null;
        
        Assert.assertEquals(encData.getType(), EncryptionConstants.TYPE_ELEMENT, "Type attribute");
        final EncryptionMethod method = encData.getEncryptionMethod();
        assert method != null;
        Assert.assertEquals(method.getAlgorithm(), algoURI, "Algorithm attribute");
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertEquals(keyInfo.getKeyNames().get(0).getValue(), expectedKeyName, 
                "KeyName");
        
        Assert.assertEquals(keyInfo.getEncryptedKeys().size(), 0, 
                "Number of EncryptedKeys");
        
        Assert.assertFalse(Strings.isNullOrEmpty(encData.getID()),
                "EncryptedData ID attribute was empty");
    }
    
    /**
     *  Test basic encryption with symmetric key, no key wrap,
     *  set key name in passed KeyInfo object.
     */
    @Test
    public void testAttribute() {
        final Assertion assertion = (Assertion) unmarshallElement("/org/opensaml/saml/saml2/encryption/Assertion.xml");
        assert assertion != null;
        final Attribute target = assertion.getAttributeStatements().get(0).getAttributes().get(0);
        
        
        KeyName keyName = (KeyName) buildXMLObject(org.opensaml.xmlsec.signature.KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(expectedKeyName);
        keyInfo.getKeyNames().add(keyName);
        encParams.setKeyInfoGenerator(new StaticKeyInfoGenerator(keyInfo));
        
        encrypter = new Encrypter(encParams, kekParamsList);
        
        EncryptedAttribute encTarget = null;
        XMLObject encObject = null;
        try {
            encObject = encrypter.encrypt(target);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encObject, "Encrypted object was null");
        Assert.assertTrue(encObject instanceof EncryptedAttribute, 
                "Encrypted object was not an instance of the expected type");
        encTarget = (EncryptedAttribute) encObject;
        assert encTarget != null;
        final EncryptedData encData = encTarget.getEncryptedData();
        assert encData != null;
        
        Assert.assertEquals(encData.getType(), EncryptionConstants.TYPE_ELEMENT, "Type attribute");
        final EncryptionMethod method = encData.getEncryptionMethod();
        assert method != null;
        Assert.assertEquals(method.getAlgorithm(), algoURI, "Algorithm attribute");
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertEquals(keyInfo.getKeyNames().get(0).getValue(), expectedKeyName, 
                "KeyName");
        
        Assert.assertEquals(keyInfo.getEncryptedKeys().size(), 0, 
                "Number of EncryptedKeys");
        
        Assert.assertFalse(Strings.isNullOrEmpty(encData.getID()),
                "EncryptedData ID attribute was empty");
    }
    
    /**
     *  Test basic encryption with symmetric key, no key wrap,
     *  set key name in passed KeyInfo object.
     */
    @Test
    public void testNewID() {
        final NewID target = (NewID) buildXMLObject(NewID.DEFAULT_ELEMENT_NAME);
        target.setValue("SomeNewID");
        
        final KeyName keyName = (KeyName) buildXMLObject(org.opensaml.xmlsec.signature.KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(expectedKeyName);
        keyInfo.getKeyNames().add(keyName);
        encParams.setKeyInfoGenerator(new StaticKeyInfoGenerator(keyInfo));
        
        encrypter = new Encrypter(encParams, kekParamsList);
        
        NewEncryptedID encTarget = null;
        XMLObject encObject = null;
        try {
            encObject = encrypter.encrypt(target);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encObject, "Encrypted object was null");
        Assert.assertTrue(encObject instanceof NewEncryptedID, 
                "Encrypted object was not an instance of the expected type");
        encTarget = (NewEncryptedID) encObject;
        assert encTarget != null;
        final EncryptedData encData = encTarget.getEncryptedData();
        assert encData != null;
        
        Assert.assertEquals(encData.getType(), EncryptionConstants.TYPE_ELEMENT, "Type attribute");
        final EncryptionMethod method = encData.getEncryptionMethod();
        assert method != null;
        Assert.assertEquals(method.getAlgorithm(), algoURI, "Algorithm attribute");
        final KeyInfo keyInfo = encData.getKeyInfo();
        assert keyInfo != null;
        Assert.assertEquals(keyInfo.getKeyNames().get(0).getValue(), expectedKeyName, 
                "KeyName");
        
        Assert.assertEquals(keyInfo.getEncryptedKeys().size(), 0, 
                "Number of EncryptedKeys");
        
        Assert.assertFalse(Strings.isNullOrEmpty(encData.getID()),
                "EncryptedData ID attribute was empty");
        
    }
    
    /** Test that reuse of the encrypter with the same encryption and key encryption parameters is allowed. */
    @Test
    public void testReuse() {
        final Assertion assertion = (Assertion) unmarshallElement("/org/opensaml/saml/saml2/encryption/Assertion.xml");
        assert assertion != null;
        
        final Attribute target = assertion.getAttributeStatements().get(0).getAttributes().get(0);
        final Attribute target2 = assertion.getAttributeStatements().get(0).getAttributes().get(1);
        
        final KeyName keyName = (KeyName) buildXMLObject(org.opensaml.xmlsec.signature.KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(expectedKeyName);
        keyInfo.getKeyNames().add(keyName);
        encParams.setKeyInfoGenerator(new StaticKeyInfoGenerator(keyInfo));
        
        encrypter = new Encrypter(encParams, kekParamsList);
        
        XMLObject encObject = null;
        try {
            encObject = encrypter.encrypt(target);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encObject, "Encrypted object was null");
        Assert.assertTrue(encObject instanceof EncryptedAttribute, 
                "Encrypted object was not an instance of the expected type");
        
        XMLObject encObject2 = null;
        try {
            encObject2 = encrypter.encrypt(target2);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encObject2, "Encrypted object was null");
        Assert.assertTrue(encObject2 instanceof EncryptedAttribute, 
                "Encrypted object was not an instance of the expected type");
    }
    
    /** Test that a data encryption key is auto-generated if it is not supplied. */
    @Test
    public void testAutoKeyGen() {
        final Assertion target = (Assertion) unmarshallElement("/org/opensaml/saml/saml2/encryption/Assertion.xml");
        
        encParams.setEncryptionCredential(null);
        
        kekParamsList.add(kekParamsRSA);
        
        encrypter = new Encrypter(encParams, kekParamsList);
        
        XMLObject encObject = null;
        try {
            assert target != null;
            encObject = encrypter.encrypt(target);
        } catch (EncryptionException e) {
            Assert.fail("Object encryption failed: " + e);
        }
        
        Assert.assertNotNull(encObject, "Encrypted object was null");
        Assert.assertTrue(encObject instanceof EncryptedAssertion, 
                "Encrypted object was not an instance of the expected type");
    }
    
    /** Test that an error is thrown if the no data encryption credential is supplied and no KEK is specified. */
    @Test
    public void testAutoKeyGenNoKEK() {
        final Assertion target = (Assertion) unmarshallElement("/org/opensaml/saml/saml2/encryption/Assertion.xml");
        
        encParams.setEncryptionCredential(null);
        
        kekParamsList.clear();
        
        encrypter = new Encrypter(encParams, kekParamsList);
        
        try {
            assert target != null;
            encrypter.encrypt(target);
            Assert.fail("Object encryption should have failed: no KEK supplied with auto key generation for data encryption");
        } catch (EncryptionException e) {
            //do nothing, should fail
        }
    }
    
}
