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
import org.opensaml.xmlsec.signature.KeyName;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class KeyNameTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedStringContent;

    /**
     * Constructor
     *
     */
    public KeyNameTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/KeyName.xml";
        
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedStringContent = "someKeyName";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final KeyName keyName = (KeyName) unmarshallElement(singleElementFile);
        
        assert keyName != null;
        Assert.assertEquals(expectedStringContent, keyName.getValue(), "KeyName value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final KeyName keyName = (KeyName) buildXMLObject(KeyName.DEFAULT_ELEMENT_NAME);
        keyName.setValue(expectedStringContent);
        
        assertXMLEquals(expectedDOM, keyName);
    }

}
