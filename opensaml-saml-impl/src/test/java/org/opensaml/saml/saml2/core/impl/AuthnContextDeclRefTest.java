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
import org.opensaml.saml.saml2.core.AuthnContextDeclRef;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.AuthnContextDeclRefImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class AuthnContextDeclRefTest extends XMLObjectProviderBaseTestCase {

    /** Expected Declaration Reference value */
    protected String expectedDeclRef;

    /** Constructor */
    public AuthnContextDeclRefTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/AuthnContextDeclRef.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedDeclRef = "declaration reference";
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final AuthnContextDeclRef authnContextDeclRef = (AuthnContextDeclRef) unmarshallElement(singleElementFile);
        assert authnContextDeclRef !=null;

        final String declRef = authnContextDeclRef.getURI();
        Assert.assertEquals(declRef, expectedDeclRef, "Declartion Reference was " + declRef + ", expected " + expectedDeclRef);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20_NS, AuthnContextDeclRef.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        final AuthnContextDeclRef authnContextDeclRef = (AuthnContextDeclRef) buildXMLObject(qname);

        authnContextDeclRef.setURI(expectedDeclRef);
        assertXMLEquals(expectedDOM, authnContextDeclRef);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        // do nothing
    }
}