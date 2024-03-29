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
import org.opensaml.xmlsec.signature.KeyInfoReference;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class KeyInfoReferenceTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedID;
    private String expectedURI;
    
    /**
     * Constructor
     *
     */
    public KeyInfoReferenceTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/KeyInfoReference.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/xmlsec/signature/impl/KeyInfoReferenceOptionalAttributes.xml"; 
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedID = "bar";
        expectedURI = "#foo";
    }

    /** {@inheritDoc} */
    public void testSingleElementUnmarshall() {
        final KeyInfoReference ref = (KeyInfoReference) unmarshallElement(singleElementFile);
        
        assert ref != null;
        Assert.assertEquals(ref.getURI(), expectedURI, "URI attribute");
    }
    
    /** {@inheritDoc} */
    public void testSingleElementOptionalAttributesUnmarshall() {
        final KeyInfoReference ref = (KeyInfoReference) unmarshallElement(singleElementOptionalAttributesFile);
        
        assert ref != null;
        Assert.assertEquals(ref.getID(), expectedID, "Id attribute");
        Assert.assertEquals(ref.getURI(), expectedURI, "URI attribute");
        Assert.assertEquals(ref.resolveIDFromRoot(expectedID), ref);
    }

    /** {@inheritDoc} */
    public void testSingleElementMarshall() {
        final KeyInfoReference ref = (KeyInfoReference) buildXMLObject(KeyInfoReference.DEFAULT_ELEMENT_NAME);
        
        ref.setURI(expectedURI);
        
        assertXMLEquals(expectedDOM, ref);
    }

    /**
     * Test marshalling of attribute IDness.
     *
     * @throws MarshallingException
     * @throws XMLParserException
     * */
    @Test
    public void testAttributeIDnessMarshall() throws MarshallingException, XMLParserException {
        final XMLObject target = buildXMLObject(KeyInfoReference.DEFAULT_ELEMENT_NAME);

        ((KeyInfoReference)target).setID("id123");

        testAttributeIDnessMarshall(target, "id123");
    }
    
    /** {@inheritDoc} */
    public void testSingleElementOptionalAttributesMarshall() {
        final KeyInfoReference ref = (KeyInfoReference) buildXMLObject(KeyInfoReference.DEFAULT_ELEMENT_NAME);

        ref.setID(expectedID);
        ref.setURI(expectedURI);
        
        assertXMLEquals(expectedOptionalAttributesDOM, ref);
        Assert.assertEquals(ref.resolveIDFromRoot(expectedID), ref);
    }

}