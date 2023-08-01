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

package org.opensaml.saml.common.profile.impl;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.opensaml.profile.testing.RequestContextBuilder;
import org.opensaml.saml.common.messaging.context.ChannelBindingsContext;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.ext.saml2cb.ChannelBindings;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

/** {@link VerifyChannelBindings} unit test. */
@SuppressWarnings({"null", "javadoc"})
public class VerifyChannelBindingsTest extends OpenSAMLInitBaseTestCase {
    
    private ProfileRequestContext prc;
    
    private VerifyChannelBindings action;
    
    @BeforeMethod public void setUp() throws ComponentInitializationException {
        action = new VerifyChannelBindings();
        action.initialize();
        
        prc = new RequestContextBuilder().buildProfileRequestContext();
    }
    
    @Test public void testNoBindings() throws MessageHandlerException {

        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        Assert.assertNull(prc.getSubcontext(ChannelBindingsContext.class));
    }
    
    @Test public void testMatch() throws MessageHandlerException {
        
        final ChannelBindings cb = XMLObjectProviderRegistrySupport.getBuilderFactory().<ChannelBindings>ensureBuilder(
                ChannelBindings.DEFAULT_ELEMENT_NAME).buildObject(ChannelBindings.DEFAULT_ELEMENT_NAME);
        cb.setType("foo");
        cb.setValue("foo");
        
        prc.ensureInboundMessageContext().ensureSubcontext(ChannelBindingsContext.class).getChannelBindings().add(cb);
        prc.ensureInboundMessageContext().ensureSubcontext(SOAP11Context.class).ensureSubcontext(
                ChannelBindingsContext.class).getChannelBindings().add(cb);
        
        action.execute(prc);
        ActionTestingSupport.assertProceedEvent(prc);
        
        final ChannelBindingsContext cbCtx = prc.ensureOutboundMessageContext().getSubcontext(ChannelBindingsContext.class);
        assert cbCtx != null;
        Assert.assertEquals(cbCtx.getChannelBindings().size(), 1);
        
        final ChannelBindings[] array = cbCtx.getChannelBindings().toArray(new ChannelBindings[1]);
        Assert.assertTrue("foo".equals(array[0].getValue()));
    }

    @Test public void testNoMatch() throws MessageHandlerException {
        
        final ChannelBindings cb = XMLObjectProviderRegistrySupport.getBuilderFactory().<ChannelBindings>ensureBuilder(
                ChannelBindings.DEFAULT_ELEMENT_NAME).buildObject(ChannelBindings.DEFAULT_ELEMENT_NAME);
        cb.setType("foo");
        cb.setValue("foo");

        final ChannelBindings cb2 = XMLObjectProviderRegistrySupport.getBuilderFactory().<ChannelBindings>ensureBuilder(
                ChannelBindings.DEFAULT_ELEMENT_NAME).buildObject(ChannelBindings.DEFAULT_ELEMENT_NAME);
        cb.setType("bar");
        cb.setValue("foo");
        
        prc.ensureInboundMessageContext().ensureSubcontext(ChannelBindingsContext.class).getChannelBindings().add(cb);
        prc.ensureInboundMessageContext().ensureSubcontext(SOAP11Context.class).ensureSubcontext(
                ChannelBindingsContext.class).getChannelBindings().add(cb2);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.CHANNEL_BINDINGS_ERROR);
        Assert.assertNull(prc.ensureOutboundMessageContext().getSubcontext(ChannelBindingsContext.class));
    }
    
    @Test public void testNoMatch2() throws MessageHandlerException {
        
        final ChannelBindings cb = XMLObjectProviderRegistrySupport.getBuilderFactory().<ChannelBindings>ensureBuilder(
                ChannelBindings.DEFAULT_ELEMENT_NAME).buildObject(ChannelBindings.DEFAULT_ELEMENT_NAME);
        cb.setType("foo");
        cb.setValue("foo");
        
        prc.ensureInboundMessageContext().ensureSubcontext(ChannelBindingsContext.class).getChannelBindings().add(cb);
        
        action.execute(prc);
        ActionTestingSupport.assertEvent(prc, SAMLEventIds.CHANNEL_BINDINGS_ERROR);
        Assert.assertNull(prc.ensureOutboundMessageContext().getSubcontext(ChannelBindingsContext.class));
    }

}