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

package org.opensaml.xmlsec.encryption.impl;


import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.xmlsec.encryption.CarriedKeyName;
import org.opensaml.xmlsec.encryption.CipherData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.EncryptionProperties;
import org.opensaml.xmlsec.encryption.ReferenceList;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class EncryptedKeyTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedId;
    
    private String expectedType;
    
    private String expectedMimeType;
    
    private String expectedEncoding;
    
    private String expectedRecipient;

    /**
     * Constructor
     *
     */
    public EncryptedKeyTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/EncryptedKey.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/xmlsec/encryption/impl/EncryptedKeyOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/EncryptedKeyChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedId = "abc123";
        expectedType = "someType";
        expectedMimeType = "someMimeType";
        expectedEncoding = "someEncoding";
        expectedRecipient = "someRecipient";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final EncryptedKey ek = (EncryptedKey) unmarshallElement(singleElementFile);
        
        assert ek != null;
        Assert.assertNull(ek.getEncryptionMethod(), "EncryptionMethod child");
        Assert.assertNull(ek.getKeyInfo(), "KeyInfo child");
        Assert.assertNull(ek.getCipherData(), "CipherData child");
        Assert.assertNull(ek.getEncryptionProperties(), "EncryptionProperties child");
        Assert.assertNull(ek.getReferenceList(), "ReferenceList child");
        Assert.assertNull(ek.getCarriedKeyName(), "CarriedKeyName child");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final EncryptedKey ek = (EncryptedKey) unmarshallElement(childElementsFile);
        
        assert ek != null;
        Assert.assertNotNull(ek.getEncryptionMethod(), "EncryptionMethod child");
        Assert.assertNotNull(ek.getKeyInfo(), "KeyInfo child");
        Assert.assertNotNull(ek.getCipherData(), "CipherData child");
        Assert.assertNotNull(ek.getEncryptionProperties(), "EncryptionProperties child");
        Assert.assertNotNull(ek.getReferenceList(), "ReferenceList child");
        Assert.assertNotNull(ek.getCarriedKeyName(), "CarriedKeyName child");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final EncryptedKey ek = (EncryptedKey) unmarshallElement(singleElementOptionalAttributesFile);
        
        assert ek != null;
        Assert.assertEquals(ek.getID(), expectedId, "Id attribute");
        Assert.assertEquals(ek.getType(), expectedType, "Type attribute");
        Assert.assertEquals(ek.getMimeType(), expectedMimeType, "MimeType attribute");
        Assert.assertEquals(ek.getEncoding(), expectedEncoding, "Encoding attribute");
        Assert.assertEquals(ek.getRecipient(), expectedRecipient, "Recipient attribute");
        
        Assert.assertEquals(ek.resolveID(expectedId), ek, "ID lookup failed");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final EncryptedKey ek = (EncryptedKey) buildXMLObject(EncryptedKey.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, ek);
    }
    
    /**
     * Test marshalling of attribute IDness.
     *
     * @throws MarshallingException
     * @throws XMLParserException
     * */
    @Test
    public void testAttributeIDnessMarshall() throws MarshallingException, XMLParserException {
        final XMLObject target = buildXMLObject(EncryptedKey.DEFAULT_ELEMENT_NAME);

        ((EncryptedKey)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final EncryptedKey ek = (EncryptedKey) buildXMLObject(EncryptedKey.DEFAULT_ELEMENT_NAME);
        
        
        ek.setEncryptionMethod((EncryptionMethod) buildXMLObject(EncryptionMethod.DEFAULT_ELEMENT_NAME));
        ek.setKeyInfo((KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME));
        ek.setCipherData((CipherData) buildXMLObject(CipherData.DEFAULT_ELEMENT_NAME));
        ek.setEncryptionProperties((EncryptionProperties) buildXMLObject(EncryptionProperties.DEFAULT_ELEMENT_NAME));
        ek.setReferenceList((ReferenceList) buildXMLObject(ReferenceList.DEFAULT_ELEMENT_NAME));
        ek.setCarriedKeyName((CarriedKeyName) buildXMLObject(CarriedKeyName.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, ek);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final EncryptedKey ek = (EncryptedKey) buildXMLObject(EncryptedKey.DEFAULT_ELEMENT_NAME);
        
        ek.setID(expectedId);
        ek.setType(expectedType);
        ek.setMimeType(expectedMimeType);
        ek.setEncoding(expectedEncoding);
        ek.setRecipient(expectedRecipient);
        
        assertXMLEquals(expectedOptionalAttributesDOM, ek);
    }

}
