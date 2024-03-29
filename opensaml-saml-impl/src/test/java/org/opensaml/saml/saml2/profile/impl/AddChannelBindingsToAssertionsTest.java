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
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.messaging.context.ChannelBindingsContext;
import org.opensaml.saml.ext.saml2cb.ChannelBindings;
import org.opensaml.saml.saml2.core.Advice;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.profile.SAML2ActionSupport;
import org.opensaml.saml.saml2.testing.SAML2ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

/** {@link AddChannelBindingsToAssertions} unit test. */
@SuppressWarnings({"null", "javadoc"})
public class AddChannelBindingsToAssertionsTest  extends OpenSAMLInitBaseTestCase {

    private ChannelBindingsContext cbc;
    
    private AddChannelBindingsToAssertions action;
    
    /**
     * Test set up.
     * 
     * @throws ComponentInitializationException
     */
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        
        final SAMLObjectBuilder<ChannelBindings> cbBuilder = (SAMLObjectBuilder<ChannelBindings>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<ChannelBindings>ensureBuilder(
                        ChannelBindings.DEFAULT_ELEMENT_NAME);
        final ChannelBindings cb = cbBuilder.buildObject();
        cb.setType("foo");
        cbc = new ChannelBindingsContext();
        cbc.getChannelBindings().add(cb);
        
        action = new AddChannelBindingsToAssertions();
        action.initialize();
    }
    
    /** Test that action errors out properly if there is no response. */
    @Test
    public void testNoResponse() {
        final ProfileRequestContext prc = new RequestContextBuilder().buildProfileRequestContext();
        prc.ensureOutboundMessageContext().addSubcontext(cbc);

        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    /** Test that action does nothing if there are no bindings. */
    @Test
    public void testNoBindings() {
        final ProfileRequestContext prc = new RequestContextBuilder().buildProfileRequestContext();

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }
    
    /** Test that action errors out properly if there is no assertion in the response. */
    @Test
    public void testNoAssertion() {
        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(
                SAML2ActionTestingSupport.buildResponse()).buildProfileRequestContext();
        prc.ensureOutboundMessageContext().addSubcontext(cbc);

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }

    /**
     * Test that the advice is properly added if there is a single assertion, without an Advice element, in the
     * response.
     */
    @Test
    public void testSingleAssertion() {
        final Assertion assertion = SAML2ActionTestingSupport.buildAssertion();

        final Response response = SAML2ActionTestingSupport.buildResponse();
        response.getAssertions().add(assertion);

        final ProfileRequestContext prc =
                new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();
        prc.ensureOutboundMessageContext().addSubcontext(cbc);

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(response.getAssertions());
        Assert.assertEquals(response.getAssertions().size(), 1);

        final Advice advice = assertion.getAdvice();
        assert advice != null;
        Assert.assertEquals(advice.getChildren(ChannelBindings.DEFAULT_ELEMENT_NAME).size(), 1);
        Assert.assertEquals(((ChannelBindings) advice.getChildren(ChannelBindings.DEFAULT_ELEMENT_NAME).get(0)).getType(), "foo");
    }

    /**
     * Test that the advice is properly added if there is a single assertion, with an Advice element, in the
     * response.
     */
    @Test
    public void testSingleAssertionWithExistingCondition() {

        final Assertion assertion = SAML2ActionTestingSupport.buildAssertion();
        SAML2ActionSupport.addAdviceToAssertion(action, assertion);

        final Response response = SAML2ActionTestingSupport.buildResponse();
        response.getAssertions().add(assertion);

        final ProfileRequestContext prc =
                new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();
        prc.ensureOutboundMessageContext().addSubcontext(cbc);

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        final Advice advice = assertion.getAdvice();
        assert advice != null;
        Assert.assertEquals(advice.getChildren(ChannelBindings.DEFAULT_ELEMENT_NAME).size(), 1);
        Assert.assertEquals(((ChannelBindings) advice.getChildren(ChannelBindings.DEFAULT_ELEMENT_NAME).get(0)).getType(), "foo");
    }

    /** Test that the advice is properly added if there are multiple assertions in the response. */
    @Test
    public void testMultipleAssertion() {
        final Response response = SAML2ActionTestingSupport.buildResponse();
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());

        final ProfileRequestContext prc =
                new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();
        prc.ensureOutboundMessageContext().addSubcontext(cbc);
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(response.getAssertions());
        Assert.assertEquals(response.getAssertions().size(), 3);

        for (final Assertion assertion : response.getAssertions()) {
            final Advice advice = assertion.getAdvice();
            assert advice != null;
            Assert.assertEquals(advice.getChildren(ChannelBindings.DEFAULT_ELEMENT_NAME).size(), 1);
            Assert.assertEquals(((ChannelBindings) advice.getChildren(ChannelBindings.DEFAULT_ELEMENT_NAME).get(0)).getType(), "foo");
        }
    }
    
}