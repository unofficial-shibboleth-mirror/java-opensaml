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
import org.opensaml.xmlsec.signature.DigestMethod;

@SuppressWarnings({"javadoc", "null"})
public class DigestMethodTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedAlgorithm;
    private int expectedTotalChildren;
    
    /**
     * Constructor.
     *
     */
    public DigestMethodTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/DigestMethod.xml";
        childElementsFile = "/org/opensaml/xmlsec/signature/impl/DigestMethodChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAlgorithm = "urn:string:foo";
        expectedTotalChildren = 3;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final DigestMethod digestMethod = (DigestMethod) unmarshallElement(singleElementFile);
        
        assert digestMethod != null;
        Assert.assertEquals(digestMethod.getAlgorithm(), expectedAlgorithm, "Algorithm attribute");
        Assert.assertEquals(digestMethod.getUnknownXMLObjects().size(), 0, "Total children");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final DigestMethod digestMethod = (DigestMethod) unmarshallElement(childElementsFile);
        
        assert digestMethod != null;
        Assert.assertEquals(digestMethod.getAlgorithm(), expectedAlgorithm, "Algorithm attribute");
        Assert.assertEquals(digestMethod.getUnknownXMLObjects().size(), expectedTotalChildren, "Total children");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final DigestMethod digestMethod = (DigestMethod) buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        
        digestMethod.setAlgorithm(expectedAlgorithm);
        
        assertXMLEquals(expectedDOM, digestMethod);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final DigestMethod digestMethod = (DigestMethod) buildXMLObject(DigestMethod.DEFAULT_ELEMENT_NAME);
        
        digestMethod.setAlgorithm(expectedAlgorithm);
        digestMethod.getUnknownXMLObjects().add( buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        digestMethod.getUnknownXMLObjects().add( buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        digestMethod.getUnknownXMLObjects().add( buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, digestMethod);
    }

}
