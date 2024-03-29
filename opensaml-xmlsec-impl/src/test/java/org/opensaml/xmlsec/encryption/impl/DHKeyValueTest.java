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
import org.opensaml.xmlsec.encryption.DHKeyValue;
import org.opensaml.xmlsec.encryption.Generator;
import org.opensaml.xmlsec.encryption.P;
import org.opensaml.xmlsec.encryption.PgenCounter;
import org.opensaml.xmlsec.encryption.Public;
import org.opensaml.xmlsec.encryption.Q;
import org.opensaml.xmlsec.encryption.Seed;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class DHKeyValueTest extends XMLObjectProviderBaseTestCase {
    
    /**
     * Constructor
     *
     */
    public DHKeyValueTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/DHKeyValue.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/DHKeyValueChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final DHKeyValue keyValue = (DHKeyValue) unmarshallElement(singleElementFile);
        
        assert keyValue != null;
        Assert.assertNull(keyValue.getP(), "P child element");
        Assert.assertNull(keyValue.getQ(), "Q child element");
        Assert.assertNull(keyValue.getGenerator(), "Generator child element");
        Assert.assertNull(keyValue.getPublic(), "Public child element");
        Assert.assertNull(keyValue.getSeed(), "seed element");
        Assert.assertNull(keyValue.getPgenCounter(), "pgenCounter element");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final DHKeyValue keyValue = (DHKeyValue) unmarshallElement(childElementsFile);
        
        assert keyValue != null;
        Assert.assertNotNull(keyValue.getP(), "P child element");
        Assert.assertNotNull(keyValue.getQ(), "Q child element");
        Assert.assertNotNull(keyValue.getGenerator(), "Generator child element");
        Assert.assertNotNull(keyValue.getPublic(), "Public child element");
        Assert.assertNotNull(keyValue.getSeed(), "seed element");
        Assert.assertNotNull(keyValue.getPgenCounter(), "pgenCounter element");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final DHKeyValue keyValue = (DHKeyValue) buildXMLObject(DHKeyValue.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, keyValue);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final DHKeyValue keyValue = (DHKeyValue) buildXMLObject(DHKeyValue.DEFAULT_ELEMENT_NAME);
        
        keyValue.setP((P) buildXMLObject(P.DEFAULT_ELEMENT_NAME));
        keyValue.setQ((Q) buildXMLObject(Q.DEFAULT_ELEMENT_NAME));
        keyValue.setGenerator((Generator) buildXMLObject(Generator.DEFAULT_ELEMENT_NAME));
        keyValue.setPublic((Public) buildXMLObject(Public.DEFAULT_ELEMENT_NAME));
        keyValue.setSeed((Seed) buildXMLObject(Seed.DEFAULT_ELEMENT_NAME));
        keyValue.setPgenCounter((PgenCounter) buildXMLObject(PgenCounter.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, keyValue);
    }

}
