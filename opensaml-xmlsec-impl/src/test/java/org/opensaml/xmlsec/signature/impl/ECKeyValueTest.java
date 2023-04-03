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

package org.opensaml.xmlsec.signature.impl;


import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.xmlsec.signature.ECKeyValue;
import org.opensaml.xmlsec.signature.NamedCurve;
import org.opensaml.xmlsec.signature.PublicKey;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 *
 */
public class ECKeyValueTest extends XMLObjectProviderBaseTestCase {

    private String expectedID;
    
    /**
     * Constructor
     *
     */
    public ECKeyValueTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/ECKeyValue.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/xmlsec/signature/impl/ECKeyValueOptionalAttributes.xml"; 
        childElementsFile = "/org/opensaml/xmlsec/signature/impl/ECKeyValueChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedID = "bar";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final ECKeyValue keyValue = (ECKeyValue) unmarshallElement(singleElementFile);
        
        assert keyValue != null;
        Assert.assertNull(keyValue.getNamedCurve(), "NamedCurve child element");
        Assert.assertNull(keyValue.getPublicKey(), "PublicKey child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final ECKeyValue keyValue = (ECKeyValue) unmarshallElement(singleElementOptionalAttributesFile);
        
        assert keyValue != null;
        Assert.assertEquals(expectedID, keyValue.getID(), "Id attribute");
        Assert.assertEquals(keyValue.resolveIDFromRoot(expectedID), keyValue);
        Assert.assertNull(keyValue.getNamedCurve(), "NamedCurve child element");
        Assert.assertNull(keyValue.getPublicKey(), "PublicKey child element");
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final ECKeyValue keyValue = (ECKeyValue) unmarshallElement(childElementsFile);
        
        assert keyValue != null;
        Assert.assertNotNull(keyValue.getNamedCurve(), "NamedCurve child element");
        Assert.assertNotNull(keyValue.getPublicKey(), "PublicKey child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final ECKeyValue keyValue = (ECKeyValue) buildXMLObject(ECKeyValue.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, keyValue);
    }

    /**
     * Test marshalling of attribute IDness.
     *
     * @throws MarshallingException
     * @throws XMLParserException
     * */
    @Test
    public void testAttributeIDnessMarshall() throws MarshallingException, XMLParserException {
        final XMLObject target = buildXMLObject(ECKeyValue.DEFAULT_ELEMENT_NAME);

        ((ECKeyValue)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final ECKeyValue keyValue = (ECKeyValue) buildXMLObject(ECKeyValue.DEFAULT_ELEMENT_NAME);

        keyValue.setID(expectedID);
        
        assertXMLEquals(expectedOptionalAttributesDOM, keyValue);
        Assert.assertEquals(keyValue.resolveIDFromRoot(expectedID), keyValue);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final ECKeyValue keyValue = (ECKeyValue) buildXMLObject(ECKeyValue.DEFAULT_ELEMENT_NAME);
        
        keyValue.setNamedCurve((NamedCurve) buildXMLObject(NamedCurve.DEFAULT_ELEMENT_NAME));
        keyValue.setPublicKey((PublicKey) buildXMLObject(PublicKey.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, keyValue);
    }

}