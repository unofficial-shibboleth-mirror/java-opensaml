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
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.encryption.CipherData;
import org.opensaml.xmlsec.encryption.CipherReference;
import org.opensaml.xmlsec.encryption.CipherValue;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class CipherDataTest extends XMLObjectProviderBaseTestCase {
    
    /**
     * Constructor
     *
     */
    public CipherDataTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/CipherData.xml";
        childElementsFile = "/org/opensaml/xmlsec/encryption/impl/CipherDataChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final CipherData cipherData = (CipherData) unmarshallElement(singleElementFile);
        
        assert cipherData != null;
        Assert.assertNull(cipherData.getCipherValue(), "CipherValue child element");
        Assert.assertNull(cipherData.getCipherReference(), "CipherReference child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final CipherData cipherData = (CipherData) unmarshallElement(childElementsFile);
        
        assert cipherData != null;
        Assert.assertNotNull(cipherData.getCipherValue(), "CipherValue child element");
        Assert.assertNotNull(cipherData.getCipherReference(), "CipherReference child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final CipherData cipherData = (CipherData) buildXMLObject(CipherData.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, cipherData);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final CipherData cipherData = (CipherData) buildXMLObject(CipherData.DEFAULT_ELEMENT_NAME);
        
        cipherData.setCipherValue((CipherValue) buildXMLObject(CipherValue.DEFAULT_ELEMENT_NAME));
        cipherData.setCipherReference((CipherReference) buildXMLObject(CipherReference.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, cipherData);
    }

}
