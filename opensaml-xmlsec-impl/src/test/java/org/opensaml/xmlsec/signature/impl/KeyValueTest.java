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
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.signature.KeyValue;
import org.opensaml.xmlsec.signature.RSAKeyValue;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class KeyValueTest extends XMLObjectProviderBaseTestCase {
    
    /**
     * Constructor
     *
     */
    public KeyValueTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/KeyValue.xml";
        childElementsFile = "/org/opensaml/xmlsec/signature/impl/KeyValueChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final KeyValue keyValue = (KeyValue) unmarshallElement(singleElementFile);
        
        assert keyValue != null;
        Assert.assertNull(keyValue.getRSAKeyValue(), "RSAKeyValue child element");
        Assert.assertNull(keyValue.getDSAKeyValue(), "DSAKeyValue child element");
        Assert.assertNull(keyValue.getUnknownXMLObject(), "Wildcard child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final KeyValue keyValue = (KeyValue) unmarshallElement(childElementsFile);
        
        assert keyValue != null;
        Assert.assertNotNull(keyValue.getRSAKeyValue(), "RSAKeyValue child element");
        Assert.assertNull(keyValue.getDSAKeyValue(), "DSAKeyValue child element");
        Assert.assertNull(keyValue.getUnknownXMLObject(), "Wildcard child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final KeyValue keyValue = (KeyValue) buildXMLObject(KeyValue.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, keyValue);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final KeyValue keyValue = (KeyValue) buildXMLObject(KeyValue.DEFAULT_ELEMENT_NAME);
        
        keyValue.setRSAKeyValue((RSAKeyValue) buildXMLObject(RSAKeyValue.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, keyValue);
    }

}
