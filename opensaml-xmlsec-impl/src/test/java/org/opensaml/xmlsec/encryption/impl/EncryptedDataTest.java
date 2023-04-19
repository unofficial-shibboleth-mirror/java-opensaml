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
import org.opensaml.xmlsec.encryption.CipherData;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.EncryptionProperties;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class EncryptedDataTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedId;
    
    private String expectedType;
    
    private String expectedMimeType;
    
    private String expectedEncoding;

    /**
     * Constructor
     *
     */
    public EncryptedDataTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/EncryptedData.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/xmlsec/encryption/impl/EncryptedDataOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/EncryptedDataChildElements.xml";
        
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedId = "abc123";
        expectedType = "someType";
        expectedMimeType = "someMimeType";
        expectedEncoding = "someEncoding";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final EncryptedData ed = (EncryptedData) unmarshallElement(singleElementFile);
        
        assert ed != null;
        Assert.assertNull(ed.getEncryptionMethod(), "EncryptionMethod child");
        Assert.assertNull(ed.getKeyInfo(), "KeyInfo child");
        Assert.assertNull(ed.getCipherData(), "CipherData child");
        Assert.assertNull(ed.getEncryptionProperties(), "EncryptionProperties child");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final EncryptedData ed = (EncryptedData) unmarshallElement(childElementsFile);
        
        assert ed != null;
        Assert.assertNotNull(ed.getEncryptionMethod(), "EncryptionMethod child");
        Assert.assertNotNull(ed.getKeyInfo(), "KeyInfo child");
        Assert.assertNotNull(ed.getCipherData(), "CipherData child");
        Assert.assertNotNull(ed.getEncryptionProperties(), "EncryptionProperties child");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final EncryptedData ed = (EncryptedData) unmarshallElement(singleElementOptionalAttributesFile);
        
        assert ed != null;
        Assert.assertEquals(ed.getID(), expectedId, "Id attribute");
        Assert.assertEquals(ed.getType(), expectedType, "Type attribute");
        Assert.assertEquals(ed.getMimeType(), expectedMimeType, "MimeType attribute");
        Assert.assertEquals(ed.getEncoding(), expectedEncoding, "Encoding attribute");
        
        Assert.assertEquals(ed.resolveID(expectedId), ed, "ID lookup failed");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        EncryptedData ed = (EncryptedData) buildXMLObject(EncryptedData.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, ed);
    }
    
    /**
     * Test marshalling of attribute IDness.
     *
     * @throws MarshallingException
     * @throws XMLParserException
     * */
    @Test
    public void testAttributeIDnessMarshall() throws MarshallingException, XMLParserException {
        final XMLObject target = buildXMLObject(EncryptedData.DEFAULT_ELEMENT_NAME);

        ((EncryptedData)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final EncryptedData ed = (EncryptedData) buildXMLObject(EncryptedData.DEFAULT_ELEMENT_NAME);
        
        ed.setEncryptionMethod((EncryptionMethod) buildXMLObject(EncryptionMethod.DEFAULT_ELEMENT_NAME));
        ed.setKeyInfo((KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME));
        ed.setCipherData((CipherData) buildXMLObject(CipherData.DEFAULT_ELEMENT_NAME));
        ed.setEncryptionProperties((EncryptionProperties) buildXMLObject(EncryptionProperties.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, ed);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final EncryptedData ed = (EncryptedData) buildXMLObject(EncryptedData.DEFAULT_ELEMENT_NAME);
        
        ed.setID(expectedId);
        ed.setType(expectedType);
        ed.setMimeType(expectedMimeType);
        ed.setEncoding(expectedEncoding);
        
        assertXMLEquals(expectedOptionalAttributesDOM, ed);
    }
    
    

}
