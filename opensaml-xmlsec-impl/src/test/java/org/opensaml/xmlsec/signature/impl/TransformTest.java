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
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.xmlsec.signature.Transform;
import org.opensaml.xmlsec.signature.XPath;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class TransformTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedAlgorithm;
    private int expectedTotalChildren;
    private int expectedXPathChildren;
    
    /**
     * Constructor
     *
     */
    public TransformTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/Transform.xml";
        childElementsFile = "/org/opensaml/xmlsec/signature/impl/TransformChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAlgorithm = "urn:string:foo";
        expectedTotalChildren = 5;
        expectedXPathChildren = 2;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final Transform transform = (Transform) unmarshallElement(singleElementFile);
        
        assert transform != null;
        Assert.assertEquals(transform.getAlgorithm(), expectedAlgorithm, "Algorithm attribute");
        Assert.assertEquals(transform.getAllChildren().size(), 0, "Total children");
        Assert.assertEquals(transform.getXPaths().size(), 0, "XPath children");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final Transform transform = (Transform) unmarshallElement(childElementsFile);
        
        assert transform != null;
        Assert.assertEquals(transform.getAlgorithm(), expectedAlgorithm, "Algorithm attribute");
        Assert.assertEquals(transform.getAllChildren().size(), expectedTotalChildren, "Total children");
        Assert.assertEquals(transform.getXPaths().size(), expectedXPathChildren, "XPath children");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final Transform transform = (Transform) buildXMLObject(Transform.DEFAULT_ELEMENT_NAME);
        
        transform.setAlgorithm(expectedAlgorithm);
        
        assertXMLEquals(expectedDOM, transform);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final Transform transform = (Transform) buildXMLObject(Transform.DEFAULT_ELEMENT_NAME);
        
        transform.setAlgorithm(expectedAlgorithm);
        transform.getAllChildren().add( buildXMLObject(XPath.DEFAULT_ELEMENT_NAME));
        transform.getAllChildren().add( buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        transform.getAllChildren().add( buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        transform.getAllChildren().add( buildXMLObject(XPath.DEFAULT_ELEMENT_NAME));
        transform.getAllChildren().add( buildXMLObject(SimpleXMLObject.ELEMENT_NAME));

        assertXMLEquals(expectedChildElementsDOM, transform);
    }

}
