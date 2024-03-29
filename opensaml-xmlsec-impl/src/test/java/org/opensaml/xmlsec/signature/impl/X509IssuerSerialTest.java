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
import org.opensaml.xmlsec.signature.X509IssuerName;
import org.opensaml.xmlsec.signature.X509IssuerSerial;
import org.opensaml.xmlsec.signature.X509SerialNumber;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class X509IssuerSerialTest extends XMLObjectProviderBaseTestCase {
    
    /**
     * Constructor
     *
     */
    public X509IssuerSerialTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/X509IssuerSerial.xml";
        childElementsFile = "/org/opensaml/xmlsec/signature/impl/X509IssuerSerialChildElements.xml";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final X509IssuerSerial x509Element = (X509IssuerSerial) unmarshallElement(singleElementFile);
        
        assert x509Element != null;
        Assert.assertNull(x509Element.getX509IssuerName(), "X509IssuerName child element");
        Assert.assertNull(x509Element.getX509SerialNumber(), "X509SerialNumber child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final X509IssuerSerial x509Element = (X509IssuerSerial) unmarshallElement(childElementsFile);
        
        assert x509Element != null;
        Assert.assertNotNull(x509Element.getX509IssuerName(), "X509IssuerName child element");
        Assert.assertNotNull(x509Element.getX509SerialNumber(), "X509SerialNumber child element");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final X509IssuerSerial x509Element = (X509IssuerSerial) buildXMLObject(X509IssuerSerial.DEFAULT_ELEMENT_NAME);
        
        assertXMLEquals(expectedDOM, x509Element);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final X509IssuerSerial x509Element = (X509IssuerSerial) buildXMLObject(X509IssuerSerial.DEFAULT_ELEMENT_NAME);
        
        x509Element.setX509IssuerName((X509IssuerName) buildXMLObject(X509IssuerName.DEFAULT_ELEMENT_NAME));
        x509Element.setX509SerialNumber((X509SerialNumber) buildXMLObject(X509SerialNumber.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, x509Element);
    }

}
