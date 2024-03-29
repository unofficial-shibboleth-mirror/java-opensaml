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
import org.opensaml.xmlsec.encryption.MGF;
import org.opensaml.xmlsec.encryption.Parameters;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class MGFTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedAlgorithm;
    
    private String expectedParametersContent;
    
    /**
     * Constructor
     *
     */
    public MGFTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/MGF.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/MGFChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAlgorithm = "urn:string:foo";
        expectedParametersContent = "MyParams";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final MGF mgf = (MGF) unmarshallElement(singleElementFile);
        
        assert mgf != null;
        Assert.assertEquals(mgf.getAlgorithm(), expectedAlgorithm, "Algorithm attribute");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final MGF mgf = (MGF) unmarshallElement(childElementsFile);
        
        assert mgf != null;
        Assert.assertEquals(mgf.getAlgorithm(), expectedAlgorithm, "Algorithm attribute");
        
        Assert.assertNotNull(mgf.getParameters(), "Parameters child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final MGF mgf = (MGF) buildXMLObject(MGF.DEFAULT_ELEMENT_NAME);
        
        mgf.setAlgorithm(expectedAlgorithm);
        
        assertXMLEquals(expectedDOM, mgf);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final MGF mgf = (MGF) buildXMLObject(MGF.DEFAULT_ELEMENT_NAME);
        
        mgf.setAlgorithm(expectedAlgorithm);
        
        XMLObjectBuilder<XSAny> xsAnyBuilder = builderFactory.ensureBuilder(XSAny.TYPE_NAME);
        XSAny parameters = xsAnyBuilder.buildObject(Parameters.DEFAULT_ELEMENT_NAME);
        parameters.setTextContent(expectedParametersContent);
        mgf.setParameters(parameters);
        
        assertXMLEquals(expectedChildElementsDOM, mgf);
    }

}
