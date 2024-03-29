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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.ext.saml2mdui.Keywords;
import org.testng.annotations.Test;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.OrganizationName}.
 */
public class KeywordsTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected Keywords. */
    private final List<String> expectedWords;
    /** Expected Language.*/
    private final String expectedLang;
    
    /**
     * Constructor.
     */
    public KeywordsTest() {
        singleElementFile = "/org/opensaml/saml/ext/saml2mdui/Keywords.xml";
        String[] contents = {"This", "is", "a", "six", "element", "keyword"}; 
        expectedWords = new ArrayList<>(contents.length);
        for (String s : contents) {
            expectedWords.add(s);
        }
        expectedLang = "en";
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final Keywords name = (Keywords) unmarshallElement(singleElementFile);
        assert name != null;
        
        assertEquals(name.getKeywords(), expectedWords, "Keyworks were not expected value");
        assertEquals(name.getXMLLang(), expectedLang, "Language was not expected value");

        final Keywords keywords = (Keywords) buildXMLObject(Keywords.DEFAULT_ELEMENT_NAME);
        assertNotEquals(keywords, name);
        keywords.setXMLLang(expectedLang);
        assertNotEquals(keywords, name);
        keywords.setKeywords(expectedWords);
        assertEquals(keywords, name);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final Keywords keywords = (Keywords) buildXMLObject(Keywords.DEFAULT_ELEMENT_NAME);
        keywords.setXMLLang(expectedLang);
        keywords.setKeywords(expectedWords);

        assertXMLEquals(expectedDOM, keywords);
    }
}