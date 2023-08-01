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

import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.EncodingException;
import net.shibboleth.shared.component.ComponentInitializationException;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.saml.common.messaging.context.ECPContext;
import org.opensaml.saml.ext.samlec.GeneratedKey;
import org.opensaml.saml.saml2.core.Advice;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.profile.SAML2ActionSupport;
import org.opensaml.saml.saml2.testing.SAML2ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** {@link AddGeneratedKeyToAssertions} unit test. */
@SuppressWarnings({"null", "javadoc"})
public class AddGeneratedKeyToAssertionsTest  extends OpenSAMLInitBaseTestCase {

    private ECPContext ecp;
    
    private String encodedKey;
    
    private AddGeneratedKeyToAssertions action;
    
    /**
     * Test set up.
     * 
     * @throws ComponentInitializationException
     * @throws EncodingException 
     */
    @BeforeMethod
    public void setUp() throws ComponentInitializationException, EncodingException {
        
        ecp = new ECPContext();
        final byte[] key = new byte[]{1, 2, 3};
        ecp.setSessionKey(key);
        
        encodedKey = Base64Support.encode(key, false);
        
        action = new AddGeneratedKeyToAssertions();
        action.initialize();
    }
    
    /** Test that action errors out properly if there is no response. */
    @Test
    public void testNoResponse() {
        final ProfileRequestContext prc = new RequestContextBuilder().buildProfileRequestContext();
        prc.ensureOutboundMessageContext().addSubcontext(ecp);

        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    /** Test that action does nothing if there's no key. */
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
        prc.ensureOutboundMessageContext().addSubcontext(ecp);

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }

    /**
     * Test that the advice is properly added if there is a single assertion, without an Advice element, in the
     * response.
     * @throws EncodingException if the ecp session key can not be base64 encoded.
     */
    @Test
    public void testSingleAssertion() throws EncodingException {
        final Assertion assertion = SAML2ActionTestingSupport.buildAssertion();

        final Response response = SAML2ActionTestingSupport.buildResponse();
        response.getAssertions().add(assertion);

        final ProfileRequestContext prc =
                new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();
        prc.ensureOutboundMessageContext().addSubcontext(ecp);

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(response.getAssertions());
        Assert.assertEquals(response.getAssertions().size(), 1);

        final Advice advice = assertion.getAdvice();
        assert advice != null;
        Assert.assertEquals(advice.getChildren(GeneratedKey.DEFAULT_ELEMENT_NAME).size(), 1);
        final GeneratedKey key = ((GeneratedKey) advice.getChildren(GeneratedKey.DEFAULT_ELEMENT_NAME).get(0));
        Assert.assertEquals(key.getValue(), encodedKey);
    }

    /**
     * Test that the advice is properly added if there is a single assertion, with an Advice element, in the
     * response.
     * @throws EncodingException if the ecp session key can not be base64 encoded.
     */
    @Test
    public void testSingleAssertionWithExistingCondition() throws EncodingException {

        final Assertion assertion = SAML2ActionTestingSupport.buildAssertion();
        SAML2ActionSupport.addAdviceToAssertion(action, assertion);

        final Response response = SAML2ActionTestingSupport.buildResponse();
        response.getAssertions().add(assertion);

        final ProfileRequestContext prc =
                new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();
        prc.ensureOutboundMessageContext().addSubcontext(ecp);

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        final Advice advice = assertion.getAdvice();
        assert advice != null;
        Assert.assertEquals(advice.getChildren(GeneratedKey.DEFAULT_ELEMENT_NAME).size(), 1);
        final GeneratedKey key = ((GeneratedKey) advice.getChildren(GeneratedKey.DEFAULT_ELEMENT_NAME).get(0));
        Assert.assertEquals(key.getValue(), encodedKey);
    }

    /** Test that the advice is properly added if there are multiple assertions in the response. 
     * @throws EncodingException if the ecp session key can not be base64 encoded.*/
    @Test
    public void testMultipleAssertion() throws EncodingException {
        final Response response = SAML2ActionTestingSupport.buildResponse();
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());

        final ProfileRequestContext prc =
                new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();
        prc.ensureOutboundMessageContext().addSubcontext(ecp);
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(response.getAssertions());
        Assert.assertEquals(response.getAssertions().size(), 3);

        for (final Assertion assertion : response.getAssertions()) {
            final Advice advice = assertion.getAdvice();
            assert advice != null;
            Assert.assertEquals(advice.getChildren(GeneratedKey.DEFAULT_ELEMENT_NAME).size(), 1);
            final GeneratedKey key = ((GeneratedKey) advice.getChildren(GeneratedKey.DEFAULT_ELEMENT_NAME).get(0));
            Assert.assertEquals(key.getValue(), encodedKey);
        }
    }
    
}