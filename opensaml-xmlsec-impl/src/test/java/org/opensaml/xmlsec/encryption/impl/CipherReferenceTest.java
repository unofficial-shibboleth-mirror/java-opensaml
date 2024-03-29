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
import org.opensaml.xmlsec.encryption.CipherReference;
import org.opensaml.xmlsec.encryption.Transforms;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class CipherReferenceTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedURI;
    
    /**
     * Constructor
     *
     */
    public CipherReferenceTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/CipherReference.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/CipherReferenceChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedURI = "urn:string:foo";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final CipherReference cr = (CipherReference) unmarshallElement(singleElementFile);
        
        assert cr != null;
        Assert.assertEquals(cr.getURI(), expectedURI, "URI attribute");
        Assert.assertNull(cr.getTransforms(), "Transforms child");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final CipherReference cr = (CipherReference) unmarshallElement(childElementsFile);
        
        assert cr != null;
        Assert.assertEquals(cr.getURI(), expectedURI, "URI attribute");
        Assert.assertNotNull(cr.getTransforms(), "Transforms child");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final CipherReference cr = (CipherReference) buildXMLObject(CipherReference.DEFAULT_ELEMENT_NAME);
        
        cr.setURI(expectedURI);
        
        assertXMLEquals(expectedDOM, cr);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final CipherReference cr = (CipherReference) buildXMLObject(CipherReference.DEFAULT_ELEMENT_NAME);
        
        cr.setURI(expectedURI);
        cr.setTransforms((Transforms) buildXMLObject(Transforms.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, cr);
    }

}
