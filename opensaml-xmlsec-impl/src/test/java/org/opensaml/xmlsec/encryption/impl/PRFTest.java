/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
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
import org.opensaml.xmlsec.encryption.PRF;
import org.opensaml.xmlsec.encryption.Parameters;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
public class PRFTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedAlgorithm;
    
    private String expectedParametersContent;
    
    /**
     * Constructor
     *
     */
    public PRFTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/PRF.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/PRFChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAlgorithm = "urn:string:foo";
        expectedParametersContent = "MyParams";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final PRF prf = (PRF) unmarshallElement(singleElementFile);
        
        assert prf != null;
        Assert.assertEquals(prf.getAlgorithm(), expectedAlgorithm, "Algorithm attribute");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final PRF prf = (PRF) unmarshallElement(childElementsFile);
        
        assert prf != null;
        Assert.assertEquals(prf.getAlgorithm(), expectedAlgorithm, "Algorithm attribute");
        Assert.assertNotNull(prf.getParameters(), "Parameters child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final PRF prf = (PRF) buildXMLObject(PRF.DEFAULT_ELEMENT_NAME);
        
        prf.setAlgorithm(expectedAlgorithm);
        
        assertXMLEquals(expectedDOM, prf);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final PRF prf = (PRF) buildXMLObject(PRF.DEFAULT_ELEMENT_NAME);
        
        prf.setAlgorithm(expectedAlgorithm);
        
        XMLObjectBuilder<XSAny> xsAnyBuilder = builderFactory.ensureBuilder(XSAny.TYPE_NAME);
        XSAny parameters = xsAnyBuilder.buildObject(Parameters.DEFAULT_ELEMENT_NAME);
        parameters.setTextContent(expectedParametersContent);
        prf.setParameters(parameters);
        
        assertXMLEquals(expectedChildElementsDOM, prf);
    }

}
