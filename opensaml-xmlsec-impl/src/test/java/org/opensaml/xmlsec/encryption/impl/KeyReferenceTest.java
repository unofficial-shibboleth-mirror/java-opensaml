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

package org.opensaml.xmlsec.encryption.impl;


import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.xmlsec.encryption.KeyReference;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class KeyReferenceTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedURI;
    private int expectedNumUnknownChildren;
    
    /**
     * Constructor
     *
     */
    public KeyReferenceTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/KeyReference.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/KeyReferenceChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedURI = "urn:string:foo";
        expectedNumUnknownChildren = 2;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final KeyReference ref = (KeyReference) unmarshallElement(singleElementFile);
        
        assert ref != null;
        Assert.assertEquals(ref.getURI(), expectedURI, "URI attribute");
        Assert.assertEquals(ref.getUnknownXMLObjects().size(), 0, "Unknown children");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final KeyReference ref = (KeyReference) unmarshallElement(childElementsFile);
        
        assert ref != null;
        Assert.assertEquals(ref.getURI(), expectedURI, "URI attribute");
        Assert.assertEquals(ref.getUnknownXMLObjects().size(), expectedNumUnknownChildren, "Unknown children");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final KeyReference ref = (KeyReference) buildXMLObject(KeyReference.DEFAULT_ELEMENT_NAME);
        
        ref.setURI(expectedURI);
        
        assertXMLEquals(expectedDOM, ref);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final KeyReference ref = (KeyReference) buildXMLObject(KeyReference.DEFAULT_ELEMENT_NAME);
        
        ref.setURI(expectedURI);
        ref.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        ref.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, ref);
    }

}
