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
import org.opensaml.saml.ext.saml2mdui.Description;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.OrganizationName}.
 */
public class DescriptionTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected name. */
    protected String expectValue = "Textual Desriptice prose";
    /** Expected language. */
    protected String expectLang =  "lang";
    
    /**
     * Constructor.
     */
    public DescriptionTest() {
        singleElementFile = "/org/opensaml/saml/ext/saml2mdui/Description.xml";
    }
    

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final Description name = (Description) unmarshallElement(singleElementFile);
        assert name != null;
        
        Assert.assertEquals(name.getValue(), expectValue, "Name was not expected value");
        Assert.assertEquals(name.getXMLLang(), expectLang, "xml:lang was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {        
        final Description name = (Description) buildXMLObject(Description.DEFAULT_ELEMENT_NAME);
        
        name.setValue(expectValue);
        name.setXMLLang(expectLang);

        assertXMLEquals(expectedDOM, name);
    }
}