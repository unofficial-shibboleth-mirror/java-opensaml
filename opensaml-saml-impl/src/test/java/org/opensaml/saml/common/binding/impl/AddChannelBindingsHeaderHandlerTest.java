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

package org.opensaml.saml.common.binding.impl;

import java.util.List;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.messaging.context.ChannelBindingsContext;
import org.opensaml.saml.ext.saml2cb.ChannelBindings;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Envelope;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

/** {@link AddChannelBindingsHeaderHandler} unit test. */
@SuppressWarnings({"null", "javadoc"})
public class AddChannelBindingsHeaderHandlerTest extends OpenSAMLInitBaseTestCase {
    
    private ChannelBindingsContext cbc;
    
    private MessageContext messageCtx;
    
    private AddChannelBindingsHeaderHandler handler;
    
    @BeforeMethod public void setUp() throws ComponentInitializationException {
        messageCtx = new MessageContext();
        
        final ChannelBindings cb = ((SAMLObjectBuilder<ChannelBindings>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<ChannelBindings>ensureBuilder(
                        ChannelBindings.DEFAULT_ELEMENT_NAME)).buildObject();
        cb.setType("foo");
        cbc = new ChannelBindingsContext();
        cbc.getChannelBindings().add(cb);
        
        handler = new AddChannelBindingsHeaderHandler();
        handler.initialize();
    }
    
    /**
     * Test that the handler does nothing on a missing CB context.
     * 
     * @throws MessageHandlerException ...
     */
    @Test public void testNoBindings() throws MessageHandlerException {
        
        handler.invoke(messageCtx);
        
        final List<XMLObject> headers =
                SOAPMessagingSupport.getHeaderBlock(messageCtx, ChannelBindings.DEFAULT_ELEMENT_NAME, null, true);
        
        Assert.assertTrue(headers.isEmpty());
    }
    
    /**
     * Test that the handler errors on a missing SOAP context.
     * 
     * @throws MessageHandlerException ...
     * @throws ComponentInitializationException ...
     */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testMissingEnvelope() throws MessageHandlerException, ComponentInitializationException {
        messageCtx.addSubcontext(cbc);

        handler.invoke(messageCtx);
    }

    /**
     * Test that the handler works.
     * 
     * @throws MessageHandlerException ...
     * @throws ComponentInitializationException ...
     */
    @Test public void testSuccess() throws MessageHandlerException, ComponentInitializationException {
        messageCtx.addSubcontext(cbc);

        final Envelope env = XMLObjectProviderRegistrySupport.getBuilderFactory().<Envelope>ensureBuilder(
                Envelope.DEFAULT_ELEMENT_NAME).buildObject(Envelope.DEFAULT_ELEMENT_NAME);
        messageCtx.ensureSubcontext(SOAP11Context.class).setEnvelope(env);
        
        handler.invoke(messageCtx);
        
        final List<XMLObject> headers =
                SOAPMessagingSupport.getHeaderBlock(messageCtx, ChannelBindings.DEFAULT_ELEMENT_NAME, null, true);
        
        Assert.assertEquals(headers.size(), 1);
        Assert.assertEquals(((ChannelBindings) headers.get(0)).getType(), "foo");
    }
    
}
