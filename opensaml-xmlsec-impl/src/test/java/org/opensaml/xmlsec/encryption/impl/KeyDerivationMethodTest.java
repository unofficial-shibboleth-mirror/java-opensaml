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
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.xmlsec.encryption.KeyDerivationMethod;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class KeyDerivationMethodTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedAlgorithm;
    private int expectedNumUnknownChildren;
    
    /**
     * Constructor
     *
     */
    public KeyDerivationMethodTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/KeyDerivationMethod.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/KeyDerivationMethodChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAlgorithm = "urn:string:foo";
        expectedNumUnknownChildren = 3;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final KeyDerivationMethod kdm = (KeyDerivationMethod) unmarshallElement(singleElementFile);
        
        assert kdm != null;
        Assert.assertEquals(kdm.getAlgorithm(), expectedAlgorithm, "Algorithm attribute");
        Assert.assertEquals(kdm.getUnknownXMLObjects().size(), 0, "Unknown children");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final KeyDerivationMethod kdm = (KeyDerivationMethod) unmarshallElement(childElementsFile);
        
        assert kdm != null;
        Assert.assertEquals(kdm.getAlgorithm(), expectedAlgorithm, "Algorithm attribute");
        Assert.assertEquals(kdm.getUnknownXMLObjects().size(), expectedNumUnknownChildren, "Unknown children");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final KeyDerivationMethod kdm = (KeyDerivationMethod) buildXMLObject(KeyDerivationMethod.DEFAULT_ELEMENT_NAME);
        
        kdm.setAlgorithm(expectedAlgorithm);
        
        assertXMLEquals(expectedDOM, kdm);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final KeyDerivationMethod kdm = (KeyDerivationMethod) buildXMLObject(KeyDerivationMethod.DEFAULT_ELEMENT_NAME);
        
        kdm.setAlgorithm(expectedAlgorithm);
        kdm.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        kdm.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        kdm.getUnknownXMLObjects().add(buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, kdm);
    }

}
