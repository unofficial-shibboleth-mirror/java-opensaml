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
import org.opensaml.xmlsec.encryption.KANonce;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class KANonceTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedBase64Content;

    /**
     * Constructor
     *
     */
    public KANonceTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/KANonce.xml";
        
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedBase64Content = "someBase64==";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final KANonce kan = (KANonce) unmarshallElement(singleElementFile);
        
        assert kan != null;
        Assert.assertEquals(expectedBase64Content, kan.getValue(), "KANonce value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final KANonce kan = (KANonce) buildXMLObject(KANonce.DEFAULT_ELEMENT_NAME);
        kan.setValue(expectedBase64Content);
        
        assertXMLEquals(expectedDOM, kan);
    }

}
