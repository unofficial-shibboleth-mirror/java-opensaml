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
import org.opensaml.saml.saml2.metadata.OrganizationDisplayName;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.OrganizationName}.
 */
@SuppressWarnings({"null", "javadoc"})
public class OrganizationDisplayNameTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected name. */
    protected String expectValue;
    /** Expected language. */
    protected String expectLang;
    
    /**
     * Constructor
     */
    public OrganizationDisplayNameTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/OrganizationDisplayName.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        expectValue = "MyOrg";
        expectLang = "Language";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final OrganizationDisplayName name = (OrganizationDisplayName) unmarshallElement(singleElementFile);
        assert name!=null;
        Assert.assertEquals(name.getValue(), expectValue, "Name was not expected value");
        Assert.assertEquals(name.getXMLLang(), expectLang, "Name was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final OrganizationDisplayName name = (OrganizationDisplayName) buildXMLObject(OrganizationDisplayName.DEFAULT_ELEMENT_NAME);
        
        name.setValue(expectValue);
        name.setXMLLang(expectLang);

        assertXMLEquals(expectedDOM, name);
    }

}