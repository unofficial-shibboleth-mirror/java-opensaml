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
import org.opensaml.xmlsec.signature.Transform;
import org.opensaml.xmlsec.signature.Transforms;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class TransformsTest extends XMLObjectProviderBaseTestCase {
    
    private int expectedNumTransforms;
    
    /**
     * Constructor
     *
     */
    public TransformsTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/Transforms.xml";
        childElementsFile = "/org/opensaml/xmlsec/signature/impl/TransformsChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedNumTransforms = 2;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final Transforms em = (Transforms) unmarshallElement(singleElementFile);
        
        assert em != null;
        Assert.assertEquals(em.getTransforms().size(), 0, "Transform children");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final Transforms em = (Transforms) unmarshallElement(childElementsFile);
        
        assert em != null;
        Assert.assertEquals(em.getTransforms().size(), expectedNumTransforms, "Transform children");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final Transforms em = (Transforms) buildXMLObject(Transforms.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, em);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final Transforms em = (Transforms) buildXMLObject(Transforms.DEFAULT_ELEMENT_NAME);
        
        em.getTransforms().add( (Transform) buildXMLObject(Transform.DEFAULT_ELEMENT_NAME));
        em.getTransforms().add( (Transform) buildXMLObject(Transform.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, em);
    }

}
