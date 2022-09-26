/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
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

import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.FunctionSupport;

import java.util.Set;
import java.util.stream.Collectors;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.ProxyRestriction;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.testing.SAML2ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** {@link AddProxyRestrictionToAssertions} unit test. */
public class AddProxyRestrictionToAssertionsTest extends OpenSAMLInitBaseTestCase {

    private static final String AUDIENCE1 = "foo";
    private static final String AUDIENCE2 = "foo2";
    
    private AddProxyRestrictionToAssertions action;
    
    @BeforeMethod public void setUp() {
        action = new AddProxyRestrictionToAssertions();
        action.setProxyRestrictionLookupStrategy(FunctionSupport.constant(new Pair<>(1,Set.of(AUDIENCE1, AUDIENCE2))));
    }
    
    /**
     * Test that action errors out properly if there is no response.
     * 
     * @throws Exception if something goes wrong
     */
    @Test public void testNoResponse() throws Exception {
        final ProfileRequestContext prc = new RequestContextBuilder().buildProfileRequestContext();
        
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, EventIds.INVALID_MSG_CTX);
    }

    /**
     * Test that action behaves properly if there is no assertion in the response.
     * 
     * @throws Exception if something goes wrong
     */
    @Test public void testNoAssertion() throws Exception {
        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(
                SAML2ActionTestingSupport.buildResponse()).buildProfileRequestContext();

        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
    }

    /**
     * Test that the condition is properly added if there is a single assertion, without a Conditions element, in the
     * response with a count of zero.
     * 
     * @throws ComponentInitializationException ...
     */
    @Test public void testZeroCount() throws ComponentInitializationException {
        final Assertion assertion = SAML2ActionTestingSupport.buildAssertion();

        final Response response = SAML2ActionTestingSupport.buildResponse();
        response.getAssertions().add(assertion);

        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();

        action.setProxyRestrictionLookupStrategy(FunctionSupport.constant(new Pair<>(0,Set.of(AUDIENCE1, AUDIENCE2))));
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(response.getAssertions());
        Assert.assertEquals(response.getAssertions().size(), 1);

        Assert.assertNotNull(assertion.getConditions());
        Assert.assertNotNull(assertion.getConditions().getProxyRestriction());
        final ProxyRestriction proxy = assertion.getConditions().getProxyRestriction();
        Assert.assertEquals(proxy.getProxyCount(), Integer.valueOf(0));
        Assert.assertTrue(proxy.getAudiences().isEmpty());
    }

    /**
     * Test that the condition is properly added if there is a single assertion, without a Conditions element, in the
     * response with no audiences.
     * 
     * @throws ComponentInitializationException ...
     */
    @Test public void testCountOnly() throws ComponentInitializationException {
        final Assertion assertion = SAML2ActionTestingSupport.buildAssertion();

        final Response response = SAML2ActionTestingSupport.buildResponse();
        response.getAssertions().add(assertion);

        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();

        action.setProxyRestrictionLookupStrategy(FunctionSupport.constant(new Pair<>(1,null)));
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(response.getAssertions());
        Assert.assertEquals(response.getAssertions().size(), 1);

        Assert.assertNotNull(assertion.getConditions());
        Assert.assertNotNull(assertion.getConditions().getProxyRestriction());
        final ProxyRestriction proxy = assertion.getConditions().getProxyRestriction();
        Assert.assertEquals(proxy.getProxyCount(), Integer.valueOf(1));
        Assert.assertTrue(proxy.getAudiences().isEmpty());
    }
    
    /**
     * Test that the condition is properly added if there is a single assertion, without a Conditions element, in the
     * response with no count.
     * 
     * @throws ComponentInitializationException ...
     */
    @Test public void testAudiencesOnly() throws ComponentInitializationException {
        final Assertion assertion = SAML2ActionTestingSupport.buildAssertion();

        final Response response = SAML2ActionTestingSupport.buildResponse();
        response.getAssertions().add(assertion);

        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();

        action.setProxyRestrictionLookupStrategy(FunctionSupport.constant(new Pair<>(null, Set.of(AUDIENCE1, AUDIENCE2))));
        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(response.getAssertions());
        Assert.assertEquals(response.getAssertions().size(), 1);

        Assert.assertNotNull(assertion.getConditions());
        Assert.assertNotNull(assertion.getConditions().getProxyRestriction());
        final ProxyRestriction proxy = assertion.getConditions().getProxyRestriction();
        Assert.assertNull(proxy.getProxyCount());
        Assert.assertEquals(proxy.getAudiences().stream().map(Audience::getURI).collect(Collectors.toUnmodifiableSet()),
                Set.of(AUDIENCE1, AUDIENCE2));
    }
    
    /**
     * Test that the condition is properly added if there is a single assertion, without a Conditions element, in the
     * response.
     * 
     * @throws ComponentInitializationException ...
     */
    @Test public void testSingleAssertion() throws ComponentInitializationException {
        final Assertion assertion = SAML2ActionTestingSupport.buildAssertion();

        final Response response = SAML2ActionTestingSupport.buildResponse();
        response.getAssertions().add(assertion);

        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();

        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(response.getAssertions());
        Assert.assertEquals(response.getAssertions().size(), 1);

        Assert.assertNotNull(assertion.getConditions());
        Assert.assertNotNull(assertion.getConditions().getProxyRestriction());
        final ProxyRestriction proxy = assertion.getConditions().getProxyRestriction();
        Assert.assertEquals(proxy.getProxyCount(), Integer.valueOf(1));
        Assert.assertEquals(proxy.getAudiences().stream().map(Audience::getURI).collect(Collectors.toUnmodifiableSet()),
                Set.of(AUDIENCE1, AUDIENCE2));
    }

    /**
     * Test that the condition is properly added if there is a single assertion, with a Conditions element, in the
     * response.
     * 
     * @throws ComponentInitializationException ...
     */
    @Test public void testSingleAssertionWithExistingCondition() throws ComponentInitializationException {
        final SAMLObjectBuilder<Conditions> conditionsBuilder = (SAMLObjectBuilder<Conditions>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Conditions>getBuilderOrThrow(
                        Conditions.DEFAULT_ELEMENT_NAME);
        final Conditions conditions = conditionsBuilder.buildObject();

        final Assertion assertion = SAML2ActionTestingSupport.buildAssertion();
        assertion.setConditions(conditions);

        final Response response = SAML2ActionTestingSupport.buildResponse();
        response.getAssertions().add(assertion);

        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();

        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(assertion.getConditions());
        Assert.assertNotNull(assertion.getConditions().getProxyRestriction());
        final ProxyRestriction proxy = assertion.getConditions().getProxyRestriction();
        Assert.assertEquals(proxy.getProxyCount(), Integer.valueOf(1));
        Assert.assertEquals(proxy.getAudiences().stream().map(Audience::getURI).collect(Collectors.toUnmodifiableSet()),
                Set.of(AUDIENCE1, AUDIENCE2));
    }

    /** Test that the condition is properly added if there are multiple assertions in the response.
     * 
     * @throws ComponentInitializationException ...
     */
    @Test public void testMultipleAssertion() throws ComponentInitializationException {
        final Response response = SAML2ActionTestingSupport.buildResponse();
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());
        response.getAssertions().add(SAML2ActionTestingSupport.buildAssertion());

        final ProfileRequestContext prc = new RequestContextBuilder().setOutboundMessage(response).buildProfileRequestContext();

        action.initialize();
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);

        Assert.assertNotNull(response.getAssertions());
        Assert.assertEquals(response.getAssertions().size(), 3);

        for (Assertion assertion : response.getAssertions()) {
            Assert.assertNotNull(assertion.getConditions());
            Assert.assertNotNull(assertion.getConditions().getProxyRestriction());
            final ProxyRestriction proxy = assertion.getConditions().getProxyRestriction();
            Assert.assertEquals(proxy.getProxyCount(), Integer.valueOf(1));
            Assert.assertEquals(proxy.getAudiences().stream().map(Audience::getURI).collect(Collectors.toUnmodifiableSet()),
                    Set.of(AUDIENCE1, AUDIENCE2));
        }
    }

}
