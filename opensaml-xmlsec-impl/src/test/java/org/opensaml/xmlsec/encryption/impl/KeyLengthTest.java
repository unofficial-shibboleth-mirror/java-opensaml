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
import org.opensaml.xmlsec.encryption.KeyLength;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class KeyLengthTest extends XMLObjectProviderBaseTestCase {
    
    private Integer expectedIntegerContent;

    /**
     * Constructor
     *
     */
    public KeyLengthTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/KeyLength.xml";
        
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedIntegerContent = 256;
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final KeyLength kl = (KeyLength) unmarshallElement(singleElementFile);
        
        assert kl != null;
        Assert.assertEquals(expectedIntegerContent, kl.getValue(), "KeyLength value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final KeyLength kl = (KeyLength) buildXMLObject(KeyLength.DEFAULT_ELEMENT_NAME);
        kl.setValue(expectedIntegerContent);
        
        assertXMLEquals(expectedDOM, kl);
    }

}
