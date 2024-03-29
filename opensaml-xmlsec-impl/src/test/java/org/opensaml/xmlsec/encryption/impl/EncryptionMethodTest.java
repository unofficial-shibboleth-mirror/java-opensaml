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
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.KeySize;
import org.opensaml.xmlsec.encryption.OAEPparams;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class EncryptionMethodTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedAlgorithm;
    
    private int expectedNumUnknownChildren;
    
    /**
     * Constructor
     *
     */
    public EncryptionMethodTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/EncryptionMethod.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/EncryptionMethodChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAlgorithm = "urn:string:foo";
        expectedNumUnknownChildren = 2;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final EncryptionMethod em = (EncryptionMethod) unmarshallElement(singleElementFile);
        
        assert em != null;
        Assert.assertEquals(em.getAlgorithm(), expectedAlgorithm, "Algorithm attribute");
        Assert.assertNull(em.getKeySize(), "KeySize child");
        Assert.assertNull(em.getOAEPparams(), "OAEPparams child");
        Assert.assertEquals(em.getUnknownXMLObjects().size(), 0, "Unknown children");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final EncryptionMethod em = (EncryptionMethod) unmarshallElement(childElementsFile);
        
        assert em != null;
        Assert.assertEquals(em.getAlgorithm(), expectedAlgorithm, "Algorithm attribute");
        Assert.assertNotNull(em.getKeySize(), "KeySize child");
        Assert.assertNotNull(em.getOAEPparams(), "OAEPparams child");
        Assert.assertEquals(em.getUnknownXMLObjects().size(), expectedNumUnknownChildren, "Unknown children");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final EncryptionMethod em = (EncryptionMethod) buildXMLObject(EncryptionMethod.DEFAULT_ELEMENT_NAME);
        
        em.setAlgorithm(expectedAlgorithm);
        
        assertXMLEquals(expectedDOM, em);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final EncryptionMethod em = (EncryptionMethod) buildXMLObject(EncryptionMethod.DEFAULT_ELEMENT_NAME);
        
        em.setAlgorithm(expectedAlgorithm);
        em.setKeySize((KeySize) buildXMLObject(KeySize.DEFAULT_ELEMENT_NAME));
        em.setOAEPparams((OAEPparams) buildXMLObject(OAEPparams.DEFAULT_ELEMENT_NAME));
        em.getUnknownXMLObjects().add( buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        em.getUnknownXMLObjects().add( buildXMLObject(SimpleXMLObject.ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, em);
    }

}
