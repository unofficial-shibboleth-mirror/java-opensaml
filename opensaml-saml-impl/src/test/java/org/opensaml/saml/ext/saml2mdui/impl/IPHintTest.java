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
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.ext.saml2mdui.IPHint;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.OrganizationName}.
 */
public class IPHintTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected name. */
    private String expectedHint;
    
    /**
     * Constructor.
     */
    public IPHintTest() {
        singleElementFile = "/org/opensaml/saml/ext/saml2mdui/IPHint.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedHint = "10.0.0.0/23";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final IPHint hint = (IPHint) unmarshallElement(singleElementFile);
        assert hint != null;
        
        Assert.assertEquals(hint.getValue(), expectedHint, "Name was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final IPHint hint = (IPHint) buildXMLObject(IPHint.DEFAULT_ELEMENT_NAME);
        
        hint.setValue(expectedHint);

        assertXMLEquals(expectedDOM, hint);
    }
}