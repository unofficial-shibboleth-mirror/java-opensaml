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

package org.opensaml.saml.saml1.profile.impl;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.NameIdentifier;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.core.Subject;
import org.opensaml.saml.saml1.testing.SAML1ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;


/** Test for {@link CopyNameIdentifierFromRequest}. */
@SuppressWarnings({"null", "javadoc"})
public class CopyNameIdentifierFromRequestTest extends OpenSAMLInitBaseTestCase {

    private static final String NAME_QUALIFIER = "https://idp.example.org";

    private ProfileRequestContext prc;
    
    private CopyNameIdentifierFromRequest action;
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        
        action = new CopyNameIdentifierFromRequest();
        action.initialize();
    }
    
    @Test
    public void testNoResponse() {
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    @Test
    public void testNoAssertions() {
        prc.ensureOutboundMessageContext().setMessage(SAML1ActionTestingSupport.buildResponse());

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertTrue(prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().isEmpty());
    }

    @Test
    public void testNoRequest() {
        prc.ensureOutboundMessageContext().setMessage(SAML1ActionTestingSupport.buildResponse());
        prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        addStatements();

        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    @Test
    public void testNoName() {
        prc.ensureOutboundMessageContext().setMessage(SAML1ActionTestingSupport.buildResponse());
        prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        addStatements();

        prc.ensureInboundMessageContext().setMessage(SAML1ActionTestingSupport.buildAttributeQueryRequest(null));
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }
    
    @Test void testCopy() {
        
        Subject subject = SAML1ActionTestingSupport.buildSubject("jdoe");
        NameIdentifier nameID = subject.getNameIdentifier();
        assert nameID != null;
        nameID.setNameQualifier(NAME_QUALIFIER);
        prc.ensureInboundMessageContext().setMessage(SAML1ActionTestingSupport.buildAttributeQueryRequest(subject));
        addStatements();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().get(0);
        subject = assertion.getAttributeStatements().get(0).getSubject();
        assert subject != null;
        nameID = subject.getNameIdentifier();
        assert nameID != null;
        Assert.assertEquals(nameID.getValue(), "jdoe");
        Assert.assertEquals(nameID.getNameQualifier(), NAME_QUALIFIER);
    }

    /** Set up the test message with some statements. */
    private void addStatements() {
        final Response response = SAML1ActionTestingSupport.buildResponse();
        response.getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        response.getAssertions().get(0).getAttributeStatements().add(SAML1ActionTestingSupport.buildAttributeStatement());
        prc.ensureOutboundMessageContext().setMessage(response);
    }
    
}