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
import org.opensaml.xmlsec.signature.SPKISexp;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class SPKISexpTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedStringContent;

    /**
     * Constructor
     *
     */
    public SPKISexpTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/SPKISexp.xml";
        
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedStringContent = "someSPKISexp";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final SPKISexp spkiElement = (SPKISexp) unmarshallElement(singleElementFile);
        
        assert spkiElement != null;
        Assert.assertEquals(expectedStringContent, spkiElement.getValue(), "SPKISexp value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final SPKISexp spkiElement = (SPKISexp) buildXMLObject(SPKISexp.DEFAULT_ELEMENT_NAME);
        spkiElement.setValue(expectedStringContent);
        
        assertXMLEquals(expectedDOM, spkiElement);
    }

}
