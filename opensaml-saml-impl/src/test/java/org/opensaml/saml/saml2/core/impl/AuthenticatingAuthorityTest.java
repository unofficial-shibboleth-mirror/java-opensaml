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

package org.opensaml.saml.saml2.core.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthenticatingAuthority;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.AuthenticatingAuthorityImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class AuthenticatingAuthorityTest extends XMLObjectProviderBaseTestCase {

    /** Expected URI value */
    protected String expectedURI;

    /** Constructor */
    public AuthenticatingAuthorityTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/AuthenticatingAuthority.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedURI = "authenticating URI";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final AuthenticatingAuthority authenticatingAuthority = (AuthenticatingAuthority) unmarshallElement(singleElementFile);
        assert authenticatingAuthority !=null;
        final String assertionURI = authenticatingAuthority.getURI();
        Assert.assertEquals(assertionURI, expectedURI, "URI was " + assertionURI + ", expected " + expectedURI);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20_NS, AuthenticatingAuthority.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20_PREFIX);
        final AuthenticatingAuthority authenticatingAuthority = (AuthenticatingAuthority) buildXMLObject(qname);

        authenticatingAuthority.setURI(expectedURI);
        assertXMLEquals(expectedDOM, authenticatingAuthority);

    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        // do nothing
    }
}