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

import java.time.Instant;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.SubjectLocality;

/**
 * Test case for creating, marshalling, and unmarshalling {@link org.opensaml.saml.saml2.core.impl.AuthnStatementImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class AuthnStatementTest extends XMLObjectProviderBaseTestCase {

    /** Expected AuthnInstant value */
    private Instant expectedAuthnInstant;

    /** Expected SessionIndex value */
    private String expectedSessionIndex;

    /** Expected SessionNotOnOrAfter value */
    private Instant expectedSessionNotOnOrAfter;

    /** Constructor */
    public AuthnStatementTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/AuthnStatement.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/AuthnStatementOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/AuthnStatementChildElements.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedAuthnInstant = Instant.parse("1984-08-26T10:01:30.043Z");
        expectedSessionIndex = "index";
        expectedSessionNotOnOrAfter = Instant.parse("1984-08-26T10:11:30.043Z");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        AuthnStatement authnStatement = (AuthnStatement) unmarshallElement(singleElementFile);
        assert authnStatement != null;

        final Instant authnInstant = authnStatement.getAuthnInstant();
        Assert.assertEquals(authnInstant, expectedAuthnInstant,
                "AuthnInstant was " + authnInstant + ", expected " + expectedAuthnInstant);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        AuthnStatement authnStatement = (AuthnStatement) unmarshallElement(singleElementOptionalAttributesFile);
        assert authnStatement != null;

        final Instant authnInstant = authnStatement.getAuthnInstant();
        Assert.assertEquals(authnInstant, expectedAuthnInstant,
                "AuthnInstant was " + authnInstant + ", expected " + expectedAuthnInstant);

        final String sessionIndex = authnStatement.getSessionIndex();
        Assert.assertEquals(sessionIndex, expectedSessionIndex,
                "SessionIndex was " + sessionIndex + ", expected " + expectedSessionIndex);

        final Instant sessionNotOnOrAfter = authnStatement.getSessionNotOnOrAfter();
        Assert.assertEquals(sessionNotOnOrAfter,
                expectedSessionNotOnOrAfter, "SessionNotOnOrAfter was " + sessionNotOnOrAfter + ", expected " + expectedSessionNotOnOrAfter);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final AuthnStatement authnStatement = (AuthnStatement) buildXMLObject(AuthnStatement.DEFAULT_ELEMENT_NAME);

        authnStatement.setAuthnInstant(expectedAuthnInstant);
        assertXMLEquals(expectedDOM, authnStatement);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final AuthnStatement authnStatement = (AuthnStatement) buildXMLObject(AuthnStatement.DEFAULT_ELEMENT_NAME);

        authnStatement.setAuthnInstant(expectedAuthnInstant);
        authnStatement.setSessionIndex(expectedSessionIndex);
        authnStatement.setSessionNotOnOrAfter(expectedSessionNotOnOrAfter);

        assertXMLEquals(expectedOptionalAttributesDOM, authnStatement);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final AuthnStatement authnStatement = (AuthnStatement) unmarshallElement(childElementsFile);
        assert authnStatement != null;
        Assert.assertNotNull(authnStatement.getAuthnContext(), "AuthnContext element not present");
        Assert.assertNotNull(authnStatement.getSubjectLocality(), "SubjectLocality element not present");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final AuthnStatement authnStatement = (AuthnStatement) buildXMLObject(AuthnStatement.DEFAULT_ELEMENT_NAME);

        authnStatement.setSubjectLocality((SubjectLocality) buildXMLObject(SubjectLocality.DEFAULT_ELEMENT_NAME));
        
        authnStatement.setAuthnContext((AuthnContext) buildXMLObject(AuthnContext.DEFAULT_ELEMENT_NAME));
        
        assertXMLEquals(expectedChildElementsDOM, authnStatement);
    }
}