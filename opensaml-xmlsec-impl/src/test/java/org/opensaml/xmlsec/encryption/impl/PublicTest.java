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
import org.opensaml.xmlsec.encryption.Public;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class PublicTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedCryptoBinaryContent;

    /**
     * Constructor
     *
     */
    public PublicTest() {
        singleElementFile = "/org/opensaml/xmlsec/encryption/impl/Public.xml";
        
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedCryptoBinaryContent = "someCryptoBinaryValue";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final Public cbType = (Public) unmarshallElement(singleElementFile);
        
        assert cbType != null;
        Assert.assertEquals(expectedCryptoBinaryContent, cbType.getValue(), "Public");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final Public cbType = (Public) buildXMLObject(Public.DEFAULT_ELEMENT_NAME);
        cbType.setValue(expectedCryptoBinaryContent);
        
        assertXMLEquals(expectedDOM, cbType);
    }

}
