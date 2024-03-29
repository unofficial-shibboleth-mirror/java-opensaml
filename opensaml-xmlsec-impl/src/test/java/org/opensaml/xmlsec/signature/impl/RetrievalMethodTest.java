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


import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.signature.RetrievalMethod;
import org.opensaml.xmlsec.signature.Transforms;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class RetrievalMethodTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedURI;
    private String expectedType;
    
    /**
     * Constructor
     *
     */
    public RetrievalMethodTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/RetrievalMethod.xml";
        childElementsFile = "/org/opensaml/xmlsec/signature/impl/RetrievalMethodChildElements.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/xmlsec/signature/impl/RetrievalMethodOptionalAttributes.xml"; 
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedURI = "urn:string:foo";
        expectedType = "someType";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final RetrievalMethod rm = (RetrievalMethod) unmarshallElement(singleElementFile);
        
        assert rm != null;
        Assert.assertEquals(rm.getURI(), expectedURI, "URI attribute");
        Assert.assertNull(rm.getTransforms(), "Transforms child element");
    }
    
    

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final RetrievalMethod rm = (RetrievalMethod) unmarshallElement(singleElementOptionalAttributesFile);
        
        assert rm != null;
        Assert.assertEquals(rm.getURI(), expectedURI, "URI attribute");
        Assert.assertEquals(rm.getType(), expectedType, "Type attribute");
        Assert.assertNull(rm.getTransforms(), "Transforms child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final RetrievalMethod rm = (RetrievalMethod) unmarshallElement(childElementsFile);
        
        assert rm != null;
        Assert.assertEquals(rm.getURI(), expectedURI, "URI attribute");
        Assert.assertNotNull(rm.getTransforms(), "Transforms child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final RetrievalMethod rm = (RetrievalMethod) buildXMLObject(RetrievalMethod.DEFAULT_ELEMENT_NAME);
        
        rm.setURI(expectedURI);
        
        assertXMLEquals(expectedDOM, rm);
    }
    
    

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final RetrievalMethod rm = (RetrievalMethod) buildXMLObject(RetrievalMethod.DEFAULT_ELEMENT_NAME);
        
        rm.setURI(expectedURI);
        rm.setType(expectedType);
        
        assertXMLEquals(expectedOptionalAttributesDOM, rm);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final RetrievalMethod rm = (RetrievalMethod) buildXMLObject(RetrievalMethod.DEFAULT_ELEMENT_NAME);
        
        rm.setURI(expectedURI);
        rm.setTransforms((Transforms) buildXMLObject(Transforms.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, rm);
    }

}
