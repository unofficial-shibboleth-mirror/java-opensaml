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


import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.xmlsec.encryption.OtherSource;
import org.opensaml.xmlsec.encryption.Parameters;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class OtherSourceTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedAlgorithm;
    
    private String expectedParametersContent;
    
    /**
     * Constructor
     *
     */
    public OtherSourceTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/OtherSource.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/OtherSourceChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAlgorithm = "urn:string:foo";
        expectedParametersContent = "MyParams";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final OtherSource otherSource = (OtherSource) unmarshallElement(singleElementFile);
        
        assert otherSource != null;
        Assert.assertEquals(otherSource.getAlgorithm(), expectedAlgorithm, "Algorithm attribute");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final OtherSource otherSource = (OtherSource) unmarshallElement(childElementsFile);
        
        assert otherSource != null;
        Assert.assertEquals(otherSource.getAlgorithm(), expectedAlgorithm, "Algorithm attribute");
        Assert.assertNotNull(otherSource.getParameters(), "Parameters child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final OtherSource otherSource = (OtherSource) buildXMLObject(OtherSource.DEFAULT_ELEMENT_NAME);
        
        otherSource.setAlgorithm(expectedAlgorithm);
        
        assertXMLEquals(expectedDOM, otherSource);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final OtherSource otherSource = (OtherSource) buildXMLObject(OtherSource.DEFAULT_ELEMENT_NAME);
        
        otherSource.setAlgorithm(expectedAlgorithm);
        
        XMLObjectBuilder<XSAny> xsAnyBuilder = builderFactory.ensureBuilder(XSAny.TYPE_NAME);
        XSAny parameters = xsAnyBuilder.buildObject(Parameters.DEFAULT_ELEMENT_NAME);
        parameters.setTextContent(expectedParametersContent);
        otherSource.setParameters(parameters);
        
        assertXMLEquals(expectedChildElementsDOM, otherSource);
    }

}
