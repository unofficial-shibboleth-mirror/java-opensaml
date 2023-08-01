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
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.xmlsec.signature.X509CRL;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class X509CRLTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedStringContent;

    /**
     * Constructor
     *
     */
    public X509CRLTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/X509CRL.xml";
        
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedStringContent = "someX509CRL";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final X509CRL x509Element = (X509CRL) unmarshallElement(singleElementFile);
        
        assert x509Element != null;
        Assert.assertEquals(expectedStringContent, x509Element.getValue(), "X509CRL value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final X509CRL x509Element = (X509CRL) buildXMLObject(X509CRL.DEFAULT_ELEMENT_NAME);
        x509Element.setValue(expectedStringContent);
        
        assertXMLEquals(expectedDOM, x509Element);
    }

}
