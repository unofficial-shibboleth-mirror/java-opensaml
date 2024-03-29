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
package org.opensaml.saml.saml2.core.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.IDPEntry;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.IDPEntryImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class IDPEntryTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected ProviderID */
    private String expectedProviderID;

    /** Expected ProviderID */
    private String expectedName;
    
    /** Expected ProviderID */
    private String expectedLocation;
    
    /**
     * Constructor
     *
     */
    public IDPEntryTest() {
        super();
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/IDPEntry.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/IDPEntryOptionalAttributes.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedProviderID = "urn:string:providerid";
        expectedName = "Example IdP";
        expectedLocation = "http://idp.example.org/endpoint";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final IDPEntry entry = (IDPEntry) buildXMLObject(IDPEntry.DEFAULT_ELEMENT_NAME);
        
        entry.setProviderID(expectedProviderID);

        assertXMLEquals(expectedDOM, entry);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final IDPEntry entry = (IDPEntry) buildXMLObject(IDPEntry.DEFAULT_ELEMENT_NAME);
        
        entry.setProviderID(expectedProviderID);
        entry.setName(expectedName);
        entry.setLoc(expectedLocation);
        
        assertXMLEquals(expectedOptionalAttributesDOM, entry);
    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final IDPEntry entry = (IDPEntry) unmarshallElement(singleElementFile);
        assert entry!=null;
        Assert.assertEquals(entry.getProviderID(), expectedProviderID, "The unmarshalled ProviderID attribute was not the expected value");

    }


    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final IDPEntry entry = (IDPEntry) unmarshallElement(singleElementOptionalAttributesFile);
        assert entry!=null;
        Assert.assertEquals(entry.getName(), expectedName, "The unmarshalled Name attribute was not the expected value");
        Assert.assertEquals(entry.getLoc(), expectedLocation, "The unmarshalled Loc (location) attribute was not the expected value");
    }
}