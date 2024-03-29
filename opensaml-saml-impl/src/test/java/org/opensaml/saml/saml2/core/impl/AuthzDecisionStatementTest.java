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

import javax.xml.namespace.QName;

import org.opensaml.core.testing.XMLObjectProviderBaseTestCase;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Action;
import org.opensaml.saml.saml2.core.AuthzDecisionStatement;
import org.opensaml.saml.saml2.core.DecisionTypeEnumeration;
import org.opensaml.saml.saml2.core.Evidence;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.xml.XMLParserException;

/**
 * Test case for creating, marshalling, and unmarshalling
 * {@link org.opensaml.saml.saml2.core.impl.AuthzDecisionStatementImpl}.
 */
@SuppressWarnings({"null", "javadoc"})
public class AuthzDecisionStatementTest extends XMLObjectProviderBaseTestCase {

    /** Expected Resource value */
    protected String expectedResource;

    /** Expected Decision value */
    protected DecisionTypeEnumeration expectedDecision;

    /** Count of Action subelements */
    protected int expectedActionCount = 3;

    /** Constructor */
    public AuthzDecisionStatementTest() {
        singleElementFile = "/org/opensaml/saml/saml2/core/impl/AuthzDecisionStatement.xml";
        singleElementOptionalAttributesFile = "/org/opensaml/saml/saml2/core/impl/AuthzDecisionStatementOptionalAttributes.xml";
        childElementsFile = "/org/opensaml/saml/saml2/core/impl/AuthzDecisionStatementChildElements.xml";
        invalidFile = "/org/opensaml/saml/saml2/core/impl/AuthzDecisionStatementInvalidDecision.xml";
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        expectedResource = "resource name";
        expectedDecision = DecisionTypeEnumeration.DENY;
    }
    
    @Test(expectedExceptions=UnmarshallingException.class)
    public void testInvalidUnmarshall() throws XMLParserException, UnmarshallingException {
        unmarshallElement(invalidFile, true);
    }
    
    /** {@inheritDoc} */
    @Test
    public void testSingleElementUnmarshall() {
        final AuthzDecisionStatement authzDecisionStatement = (AuthzDecisionStatement) unmarshallElement(singleElementFile);
        assert authzDecisionStatement != null;

        String resource = authzDecisionStatement.getResource();
        Assert.assertEquals(resource, expectedResource, "Resource not as expected");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesUnmarshall() {
        final AuthzDecisionStatement authzDecisionStatement = (AuthzDecisionStatement) unmarshallElement(singleElementOptionalAttributesFile);
        assert authzDecisionStatement != null;

        String resource = authzDecisionStatement.getResource();
        Assert.assertEquals(resource, expectedResource, "Resource not as expected");

        final DecisionTypeEnumeration decision = authzDecisionStatement.getDecision();
        assert decision != null;

        Assert.assertEquals(decision.toString(), expectedDecision.toString(), "Decision not as expected");
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20_NS, AuthzDecisionStatement.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        final AuthzDecisionStatement authzDecisionStatement = (AuthzDecisionStatement) buildXMLObject(qname);

        authzDecisionStatement.setResource(expectedResource);
        assertXMLEquals(expectedDOM, authzDecisionStatement);
    }

    /** {@inheritDoc} */
    @Test
    public void testSingleElementOptionalAttributesMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20_NS, AuthzDecisionStatement.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        final AuthzDecisionStatement authzDecisionStatement = (AuthzDecisionStatement) buildXMLObject(qname);

        authzDecisionStatement.setResource(expectedResource);
        authzDecisionStatement.setDecision(expectedDecision);

        assertXMLEquals(expectedOptionalAttributesDOM, authzDecisionStatement);
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsUnmarshall() {
        final AuthzDecisionStatement authzDecisionStatement = (AuthzDecisionStatement) unmarshallElement(childElementsFile);
        assert authzDecisionStatement != null;
        Assert.assertEquals(authzDecisionStatement.getActions().size(), expectedActionCount, "Action Count");
        Assert.assertNotNull(authzDecisionStatement.getEvidence(), "Evidence element not present");
    }

    /** {@inheritDoc} */
    @Test
    public void testChildElementsMarshall() {
        final QName qname = new QName(SAMLConstants.SAML20_NS, AuthzDecisionStatement.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        final AuthzDecisionStatement authzDecisionStatement = (AuthzDecisionStatement) buildXMLObject(qname);

        QName actionQName = new QName(SAMLConstants.SAML20_NS, Action.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        for (int i = 0; i < expectedActionCount; i++) {
            authzDecisionStatement.getActions().add((Action) buildXMLObject(actionQName));
        }
        
        QName evidenceQName = new QName(SAMLConstants.SAML20_NS, Evidence.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        authzDecisionStatement.setEvidence((Evidence) buildXMLObject(evidenceQName));
        
        assertXMLEquals(expectedChildElementsDOM, authzDecisionStatement);
    }
    
    public void testResource() {
        AuthzDecisionStatement authzDecisionStatement = (AuthzDecisionStatement) buildXMLObject(AuthzDecisionStatement.DEFAULT_ELEMENT_NAME);
        
        authzDecisionStatement.setResource("urn:test:foo");
        Assert.assertEquals(authzDecisionStatement.getResource(), "urn:test:foo");
        
        authzDecisionStatement.setResource("");
        Assert.assertEquals(authzDecisionStatement.getResource(), "");
        
        // 3 spaces
        authzDecisionStatement.setResource("   ");
        Assert.assertEquals(authzDecisionStatement.getResource(), "   ");
    }
    
}