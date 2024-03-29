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
import org.opensaml.saml.ext.saml2mdui.Logo;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.OrganizationName}.
 */
public class LogoTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected URL. */
    private final String expectedURL;
    
    /** expected language. */
    private final String expectedLang;
    
    /** expected height. */
    private final Integer expectedHeight;
    
    /** expected width. */
    private final Integer expectedWidth;
    
    /**
     * Constructor.
     */
    public LogoTest() {
        singleElementFile = "/org/opensaml/saml/ext/saml2mdui/Logo.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/ext/saml2mdui/LogoWithLang.xml";
        expectedURL = "http://exaple.org/Logo";
        expectedHeight = Integer.valueOf(10);
        expectedWidth = Integer.valueOf(23);
        expectedLang = "logoLang";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final Logo logo = (Logo) unmarshallElement(singleElementFile);
        assert logo != null;
        
        Assert.assertEquals(logo.getURI(), expectedURL, "URL was not expected value");
        Assert.assertEquals(logo.getHeight(), expectedHeight, "height was not expected value");
        Assert.assertEquals(logo.getWidth(), expectedWidth, "width was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final Logo logo = (Logo) unmarshallElement(singleElementOptionalAttributesFile);
        assert logo != null;
        
        Assert.assertEquals(logo.getURI(), expectedURL, "URL was not expected value");
        Assert.assertEquals(logo.getHeight(), expectedHeight, "height was not expected value");
        Assert.assertEquals(logo.getWidth(), expectedWidth, "width was not expected value");
        Assert.assertEquals(logo.getXMLLang(), expectedLang, "xml:lang was not the expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final Logo logo = (Logo) buildXMLObject(Logo.DEFAULT_ELEMENT_NAME);
        
        logo.setURI(expectedURL);
        logo.setWidth(expectedWidth);
        logo.setHeight(expectedHeight);

        assertXMLEquals(expectedDOM, logo);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final Logo logo = (Logo) buildXMLObject(Logo.DEFAULT_ELEMENT_NAME);
        
        logo.setURI(expectedURL);
        logo.setWidth(expectedWidth);
        logo.setHeight(expectedHeight);
        logo.setXMLLang(expectedLang);

        assertXMLEquals(expectedOptionalAttributesDOM, logo);
    }
}