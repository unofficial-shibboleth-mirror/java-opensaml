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
import org.opensaml.xmlsec.encryption.CarriedKeyName;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class CarriedKeyNameTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedStringContent;

    /**
     * Constructor
     *
     */
    public CarriedKeyNameTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/CarriedKeyName.xml";
        
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedStringContent = "someKeyName";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final CarriedKeyName ckn = (CarriedKeyName) unmarshallElement(singleElementFile);
        
        assert ckn != null;
        Assert.assertEquals(expectedStringContent, ckn.getValue(), "CarriedKeyName value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final CarriedKeyName ckn = (CarriedKeyName) buildXMLObject(CarriedKeyName.DEFAULT_ELEMENT_NAME);
        ckn.setValue(expectedStringContent);
        
        assertXMLEquals(expectedDOM, ckn);
    }

}
