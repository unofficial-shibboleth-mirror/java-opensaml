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
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.ConfirmationMethod;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.core.Subject;
import org.opensaml.saml.saml1.core.SubjectConfirmation;
import org.opensaml.saml.saml1.testing.SAML1ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;


/** Test for {@link AddSubjectConfirmationToSubjects}. */
@SuppressWarnings({"null", "javadoc"})
public class AddSubjectConfirmationToSubjectsTest extends OpenSAMLInitBaseTestCase {
    
    private ProfileRequestContext prc;
    
    private AddSubjectConfirmationToSubjects action;
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        prc = new RequestContextBuilder().buildProfileRequestContext();
        
        action = new AddSubjectConfirmationToSubjects();
    }

    @Test(expectedExceptions = ComponentInitializationException.class)
    public void testBadConfig() throws ComponentInitializationException {
        action.initialize();
    }
    
    @Test
    public void testNoMessage() throws ComponentInitializationException {
        action.setMethods(CollectionSupport.singleton(ConfirmationMethod.METHOD_BEARER));
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    @Test
    public void testNoAssertions() throws ComponentInitializationException {
        prc.ensureOutboundMessageContext().setMessage(SAML1ActionTestingSupport.buildResponse());
        
        action.setMethods(CollectionSupport.singleton(ConfirmationMethod.METHOD_BEARER));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertTrue(prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().isEmpty());
    }

    @Test
    public void testNoStatements() throws ComponentInitializationException {
        prc.ensureOutboundMessageContext().setMessage(SAML1ActionTestingSupport.buildResponse());
        prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        
        action.setMethods(CollectionSupport.singleton(ConfirmationMethod.METHOD_BEARER));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        Assert.assertTrue(prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().get(0).getStatements().isEmpty());
    }

    @Test void testSingle() throws ComponentInitializationException {
        addStatements();
        
        action.setMethods(CollectionSupport.singleton(ConfirmationMethod.METHOD_BEARER));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().get(0);
        Subject subject = assertion.getAuthenticationStatements().get(0).getSubject();
        assert subject != null;
        SubjectConfirmation sc = subject.getSubjectConfirmation();
        assert sc != null;
        Assert.assertEquals(sc.getConfirmationMethods().size(), 1);
        Assert.assertEquals(sc.getConfirmationMethods().get(0).getURI(),
                ConfirmationMethod.METHOD_BEARER);

        assertion = prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().get(1);
        subject = assertion.getAttributeStatements().get(0).getSubject();
        assert subject != null;
        sc = subject.getSubjectConfirmation();
        assert sc != null;
        Assert.assertEquals(sc.getConfirmationMethods().size(), 1);
        Assert.assertEquals(sc.getConfirmationMethods().get(0).getURI(),
                ConfirmationMethod.METHOD_BEARER);
    }

    @Test void testMultiple() throws ComponentInitializationException {
        addStatements();
        
        action.setMethods(CollectionSupport.listOf(ConfirmationMethod.METHOD_BEARER, ConfirmationMethod.METHOD_SENDER_VOUCHES));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().get(0);
        Subject subject = assertion.getAuthenticationStatements().get(0).getSubject();
        assert subject != null;
        SubjectConfirmation sc = subject.getSubjectConfirmation();
        assert sc != null;
        Assert.assertEquals(sc.getConfirmationMethods().size(), 2);
        Assert.assertEquals(sc.getConfirmationMethods().get(0).getURI(),
                ConfirmationMethod.METHOD_BEARER);
        Assert.assertEquals(sc.getConfirmationMethods().get(1).getURI(),
                ConfirmationMethod.METHOD_SENDER_VOUCHES);

        assertion = prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().get(1);
        subject = assertion.getAttributeStatements().get(0).getSubject();
        assert subject != null;
        sc = subject.getSubjectConfirmation();
        assert sc != null;
        Assert.assertEquals(sc.getConfirmationMethods().size(), 2);
        Assert.assertEquals(sc.getConfirmationMethods().get(0).getURI(),
                ConfirmationMethod.METHOD_BEARER);
        Assert.assertEquals(sc.getConfirmationMethods().get(1).getURI(),
                ConfirmationMethod.METHOD_SENDER_VOUCHES);
    }
    
    @Test void testArtifact() throws ComponentInitializationException {
        addStatements();
        prc.ensureOutboundMessageContext().ensureSubcontext(SAMLBindingContext.class).setBindingUri(
                SAMLConstants.SAML1_ARTIFACT_BINDING_URI);
        
        action.setMethods(CollectionSupport.singleton(ConfirmationMethod.METHOD_BEARER));
        action.initialize();
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assertion assertion = prc.ensureOutboundMessageContext().ensureMessage(Response.class).getAssertions().get(0);
        Subject subject = assertion.getAuthenticationStatements().get(0).getSubject();
        assert subject != null;
        final SubjectConfirmation sc = subject.getSubjectConfirmation();
        assert sc != null;
        Assert.assertEquals(sc.getConfirmationMethods().size(), 1);
        Assert.assertEquals(sc.getConfirmationMethods().get(0).getURI(), ConfirmationMethod.METHOD_ARTIFACT);
    }
    
    /** Set up the test message with some statements. */
    private void addStatements() {
        final Response response = SAML1ActionTestingSupport.buildResponse();
        response.getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML1ActionTestingSupport.buildAssertion());
        response.getAssertions().get(0).getAuthenticationStatements().add(SAML1ActionTestingSupport.buildAuthenticationStatement());
        response.getAssertions().get(1).getAttributeStatements().add(SAML1ActionTestingSupport.buildAttributeStatement());
        prc.ensureOutboundMessageContext().setMessage(response);
    }
    
}