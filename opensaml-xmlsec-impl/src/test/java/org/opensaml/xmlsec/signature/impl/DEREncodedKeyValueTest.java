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

package org.opensaml.xmlsec.signature.impl;


import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.xmlsec.signature.DEREncodedKeyValue;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class DEREncodedKeyValueTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedID;
    
    private String expectedStringContent;
    
    /**
     * Constructor
     *
     */
    public DEREncodedKeyValueTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/DEREncodedKeyValue.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/xmlsec/signature/impl/DEREncodedKeyValueOptionalAttributes.xml"; 
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedID = "bar";
        expectedStringContent = "someDEREncodedKey";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final DEREncodedKeyValue der = (DEREncodedKeyValue) unmarshallElement(singleElementFile);
        
        assert der != null;
        Assert.assertEquals(der.getValue(), expectedStringContent, "DEREncodedKeyValue value");
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final DEREncodedKeyValue der = (DEREncodedKeyValue) unmarshallElement(singleElementOptionalAttributesFile);
        
        assert der != null;
        Assert.assertEquals(expectedID, der.getID(), "Id attribute");
        Assert.assertEquals(der.getValue(), expectedStringContent, "DEREncodedKeyValue value");
        Assert.assertEquals(der.resolveIDFromRoot(expectedID), der);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final DEREncodedKeyValue der = (DEREncodedKeyValue) buildXMLObject(DEREncodedKeyValue.DEFAULT_ELEMENT_NAME);
        
        der.setValue(expectedStringContent);
        
        assertXMLEquals(expectedDOM, der);
    }

    /**
     * Test marshalling of attribute IDness.
     *
     * @throws MarshallingException
     * @throws XMLParserException
     * */
    @Test
    public void testAttributeIDnessMarshall() throws MarshallingException, XMLParserException {
        final XMLObject target = buildXMLObject(DEREncodedKeyValue.DEFAULT_ELEMENT_NAME);

        ((DEREncodedKeyValue)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final DEREncodedKeyValue der = (DEREncodedKeyValue) buildXMLObject(DEREncodedKeyValue.DEFAULT_ELEMENT_NAME);

        der.setID(expectedID);
        der.setValue(expectedStringContent);
        
        assertXMLEquals(expectedOptionalAttributesDOM, der);
        Assert.assertEquals(der.resolveIDFromRoot(expectedID), der);
    }

}