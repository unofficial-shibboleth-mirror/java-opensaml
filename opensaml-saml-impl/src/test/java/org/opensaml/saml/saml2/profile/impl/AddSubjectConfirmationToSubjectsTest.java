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

package org.opensaml.saml.saml2.profile.impl;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.saml.common.messaging.context.SAMLMessageInfoContext;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.testing.SAML2ActionTestingSupport;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.FunctionSupport;
import net.shibboleth.shared.testing.ConstantSupplier;


/** Test for {@link AddSubjectConfirmationToSubjects}. */
@SuppressWarnings({"null", "javadoc"})
public class AddSubjectConfirmationToSubjectsTest extends OpenSAMLInitBaseTestCase {
    
    private ProfileRequestContext prc;
    
    private AddSubjectConfirmationToSubjects action;
    
    @BeforeMethod
    public void setUp() {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        
        action = new AddSubjectConfirmationToSubjects();
        final MockHttpServletRequest request = new MockHttpServletRequest();
        action.setHttpServletRequestSupplier(new ConstantSupplier<>(request));
    }

    @Test(expectedExceptions = ComponentInitializationException.class)
    public void testBadConfig() throws ComponentInitializationException {
        action.initialize();
    }
    
    @Test
    public void testNoMessage() throws ComponentInitializationException {
        action.setMethod(SubjectConfirmation.METHOD_BEARER);
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    @Test
    public void testNoAssertions() throws ComponentInitializationException {
        prc.ensureOutboundMessageContext().setMessage(SAML2ActionTestingSupport.buildResponse());
        
        action.setMethod(SubjectConfirmation.METHOD_BEARER);
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertTrue(((Response) prc.ensureOutboundMessageContext().ensureMessage()).getAssertions().isEmpty());
    }

    @Test void testSuccess() throws ComponentInitializationException {
        addAssertions();
        
        action.setMethod(SubjectConfirmation.METHOD_BEARER);
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = ((Response) prc.ensureOutboundMessageContext().ensureMessage()).getAssertions().get(0);
        Subject subject = assertion.getSubject();
        assert subject != null;
        Assert.assertEquals(subject.getSubjectConfirmations().size(), 1);
        Assert.assertEquals(subject.getSubjectConfirmations().get(0).getMethod(), SubjectConfirmation.METHOD_BEARER);
        
        assertion = ((Response) prc.ensureOutboundMessageContext().ensureMessage()).getAssertions().get(1);
        subject = assertion.getSubject();
        assert subject != null;
        Assert.assertEquals(subject.getSubjectConfirmations().size(), 1);
        Assert.assertEquals(subject.getSubjectConfirmations().get(0).getMethod(), SubjectConfirmation.METHOD_BEARER);
        
        final SubjectConfirmationData data = subject.getSubjectConfirmations().get(0).getSubjectConfirmationData();
        assert data != null;
        Assert.assertNull(data.getRecipient());
        Assert.assertNotNull(data.getNotOnOrAfter());
        Assert.assertEquals(data.getAddress(), "127.0.0.1");
        Assert.assertEquals(data.getInResponseTo(), ((AuthnRequest) prc.ensureInboundMessageContext().ensureMessage()).getID());
    }

    @Test void testNoAddress() throws ComponentInitializationException {
        addAssertions();
        
        action.setMethod(SubjectConfirmation.METHOD_BEARER);
        action.setAddressLookupStrategy(FunctionSupport.constant(null));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final Assertion assertion = ((Response) prc.ensureOutboundMessageContext().ensureMessage()).getAssertions().get(0);
        final Subject subject = assertion.getSubject();
        assert subject != null;
        Assert.assertEquals(subject.getSubjectConfirmations().size(), 1);
        
        final SubjectConfirmationData data = subject.getSubjectConfirmations().get(0).getSubjectConfirmationData();
        assert data != null;
        Assert.assertNull(data.getAddress());
    }

    /** Set up the test message with some assertions. */
    private void addAssertions() {
        final Response response = SAML2ActionTestingSupport.buildResponse();
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        prc.ensureOutboundMessageContext().setMessage(response);
        prc.ensureInboundMessageContext().setMessage(SAML2ActionTestingSupport.buildAuthnRequest());
        prc.ensureInboundMessageContext().ensureSubcontext(SAMLMessageInfoContext.class);
        response.setInResponseTo(((AuthnRequest) prc.ensureInboundMessageContext().ensureMessage()).getID());
    }
    
}
