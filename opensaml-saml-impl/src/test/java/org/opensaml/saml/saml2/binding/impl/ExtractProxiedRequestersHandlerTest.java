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

package org.opensaml.saml.saml2.binding.impl;

import java.util.Arrays;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.profile.context.ProxiedRequesterContext;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.RequesterID;
import org.opensaml.saml.saml2.core.Scoping;
import org.opensaml.saml.saml2.testing.SAML2ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

/** {@link ExtractProxiedRequestersHandler} unit test. */
public class ExtractProxiedRequestersHandlerTest extends OpenSAMLInitBaseTestCase {
    
    SAMLObjectBuilder<Scoping> scopingBuilder;
    
    SAMLObjectBuilder<RequesterID> requesterIDBuilder;
    
    /**
     * Test set up.
     */
    @BeforeClass public void setUp() {
        scopingBuilder = (SAMLObjectBuilder<Scoping>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Scoping>ensureBuilder(
                        Scoping.DEFAULT_ELEMENT_NAME);
        requesterIDBuilder = (SAMLObjectBuilder<RequesterID>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<RequesterID>ensureBuilder(
                        RequesterID.DEFAULT_ELEMENT_NAME);
    }
    
    
    /**
     * Test that the handler errors on a missing request.
     * 
     * @throws MessageHandlerException ...
     * @throws ComponentInitializationException ...
     */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testMissingRequest() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext messageCtx = new MessageContext();

        final ExtractProxiedRequestersHandler handler = new ExtractProxiedRequestersHandler();
        handler.initialize();
        
        handler.invoke(messageCtx);
    }

    /**
     * Test that the handler works.
     * 
     * @throws MessageHandlerException ...
     * @throws ComponentInitializationException ...
     */
    @Test public void testSuccess() throws MessageHandlerException, ComponentInitializationException {
        final MessageContext messageCtx = new MessageContext();
        messageCtx.setMessage(SAML2ActionTestingSupport.buildAuthnRequest());
        
        final Scoping scoping = scopingBuilder.buildObject();
        final RequesterID one = requesterIDBuilder.buildObject();
        one.setURI("one");
        final RequesterID two = requesterIDBuilder.buildObject();
        two.setURI("two");
        scoping.getRequesterIDs().addAll(Arrays.asList(one, two));
        
        ((AuthnRequest) messageCtx.ensureMessage()).setScoping(scoping);
        
        final ExtractProxiedRequestersHandler handler = new ExtractProxiedRequestersHandler();
        handler.initialize();
        
        handler.invoke(messageCtx);
        
        final ProxiedRequesterContext ctx = messageCtx.getSubcontext(ProxiedRequesterContext.class);
        assert ctx != null;
        Assert.assertEquals(ctx.getRequesters().size(), 2);
        Assert.assertTrue(ctx.getRequesters().contains("one"));
        Assert.assertTrue(ctx.getRequesters().contains("two"));
        Assert.assertFalse(ctx.getRequesters().contains("foo"));
    }
    
}