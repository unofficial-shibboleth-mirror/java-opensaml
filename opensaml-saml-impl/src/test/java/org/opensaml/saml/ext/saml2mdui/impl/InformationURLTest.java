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
package org.opensaml.saml.ext.saml2mdui.impl;

import org.testng.annotations.Test;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.ext.saml2mdui.InformationURL;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.OrganizationName}.
 */
public class InformationURLTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected name. */
    protected String expectValue = "http://example.org/Info/URL";
    /** Expected language. */
    protected String expectLang = "infoUrlLang";
    
    /**
     * Constructor.
     */
    public InformationURLTest() {
        singleElementFile = "/org/opensaml/saml/ext/saml2mdui/InformationURL.xml";
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final InformationURL url = (InformationURL) unmarshallElement(singleElementFile);
        assert url != null;
        Assert.assertEquals(url.getURI(), expectValue, "URI was not expected value");
        Assert.assertEquals(url.getXMLLang(), expectLang, "xml:lang was not expected value");

    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final InformationURL url = (InformationURL) buildXMLObject(InformationURL.DEFAULT_ELEMENT_NAME);
        
        url.setURI(expectValue);
        url.setXMLLang(expectLang);

        assertXMLEquals(expectedDOM, url);
    }
}