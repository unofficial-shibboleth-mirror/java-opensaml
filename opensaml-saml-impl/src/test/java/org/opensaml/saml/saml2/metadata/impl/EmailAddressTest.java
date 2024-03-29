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
import org.opensaml.saml.saml2.metadata.EmailAddress;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.metadata.impl.EmailAddressImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class EmailAddressTest extends XMLObjectProviderBaseTestCase {
    
    /** Expected email address */
    protected String expectedAddress;
    
    /**
     * Constructor
     */
    public EmailAddressTest() {
        singleElementFile = "/org/opensaml/saml/saml2/metadata/impl/EmailAddress.xml";
    }
    
    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAddress = "foo@example.org";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final EmailAddress address = (EmailAddress) unmarshallElement(singleElementFile);
        assert address!=null;
        Assert.assertEquals(address.getURI(), expectedAddress, "Email address was not expected value");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        EmailAddress address = (new EmailAddressBuilder()).buildObject();
        
        address.setURI(expectedAddress);

        assertXMLEquals(expectedDOM, address);
    }
}