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
import org.opensaml.xmlsec.signature.XPath;

/**
 *
 */
@SuppressWarnings({"javadoc", "null"})
public class XPathTest extends XMLObjectProviderBaseTestCase {
    
    private String expectedStringContent;

    /**
     * Constructor
     *
     */
    public XPathTest() {
        singleElementFile = "/org/opensaml/xmlsec/signature/impl/XPath.xml";
        
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedStringContent = "someXPathValue";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final XPath xp = (XPath) unmarshallElement(singleElementFile);
        
        assert xp != null;
        Assert.assertEquals(expectedStringContent, xp.getValue(), "XPath value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final XPath xp = (XPath) buildXMLObject(XPath.DEFAULT_ELEMENT_NAME);
        xp.setValue(expectedStringContent);
        
        assertXMLEquals(expectedDOM, xp);
    }

}
