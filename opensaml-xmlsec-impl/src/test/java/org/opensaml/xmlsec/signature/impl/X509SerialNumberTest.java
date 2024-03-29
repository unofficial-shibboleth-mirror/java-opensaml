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
import java.math.BigInteger;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.signature.X509SerialNumber;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class X509SerialNumberTest extends XMLObjectProviderBaseTestCase {
    
    private BigInteger expectedBigIntegerContent;

    /**
     * Constructor.
     *
     */
    public X509SerialNumberTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/X509SerialNumber.xml";
        
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedBigIntegerContent = new BigInteger("123456789");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final X509SerialNumber x509Element = (X509SerialNumber) unmarshallElement(singleElementFile);
        
        assert x509Element != null;
        Assert.assertEquals(expectedBigIntegerContent, x509Element.getValue(), "X509SerialNumber value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final X509SerialNumber x509Element = (X509SerialNumber) buildXMLObject(X509SerialNumber.DEFAULT_ELEMENT_NAME);
        x509Element.setValue(expectedBigIntegerContent);
        
        assertXMLEquals(expectedDOM, x509Element);
    }

}
