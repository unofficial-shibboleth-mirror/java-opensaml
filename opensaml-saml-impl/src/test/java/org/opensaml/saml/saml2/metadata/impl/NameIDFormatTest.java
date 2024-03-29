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

/**
 * 
 */
package org.opensaml.saml.saml2.metadata.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.metadata.NameIDFormat;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.NameIDFormat}.
 */
@SuppressWarnings({"null", "javadoc"})
public class NameIDFormatTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected format */
    protected String expectFormat;
    
    /**
     * Constructor
     */
    public NameIDFormatTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/NameIDFormat.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        expectFormat = "urn:name:format";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final NameIDFormat format = (NameIDFormat) unmarshallElement(singleElementFile);
        assert format!=null;
        Assert.assertEquals(format.getURI(), expectFormat, "Format was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final NameIDFormat format = (NameIDFormat) buildXMLObject(NameIDFormat.DEFAULT_ELEMENT_NAME);
        
        format.setURI(expectFormat);

        assertXMLEquals(expectedDOM, format);
    }

}