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

import java.util.List;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.saml2.ecp.RelayState;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Envelope;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

/** {@link AddRelayStateHeaderHandler} unit test. */
public class AddRelayStateHeaderHandlerTest extends OpenSAMLInitBaseTestCase {
    
    private MessageContext messageCtx;
    
    private AddRelayStateHeaderHandler handler;
    
    /**
     * Test set up.
     * 
     * @throws ComponentInitializationException
     */
    @BeforeMethod public void setUp() throws ComponentInitializationException {
        messageCtx = new MessageContext();
        handler = new AddRelayStateHeaderHandler();
        handler.initialize();
    }
    
    /**
     * Test that the handler does nothing on a missing Endpoint context.
     * 
     * @throws MessageHandlerException ...
     */
    @Test public void testMissingEndpointContext() throws MessageHandlerException {

        handler.invoke(messageCtx);
        
        final List<XMLObject> headers =
                SOAPMessagingSupport.getHeaderBlock(messageCtx, RelayState.DEFAULT_ELEMENT_NAME, null, true);
        
        Assert.assertTrue(headers.isEmpty());
    }
    
    /**
     * Test that the handler errors on a missing SOAP context.
     * 
     * @throws MessageHandlerException ...
     */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testMissingEnvelope() throws MessageHandlerException {

        messageCtx.ensureSubcontext(SAMLBindingContext.class).setRelayState("foo");
        
        handler.invoke(messageCtx);
    }

    /**
     * Test that the handler works.
     * 
     * @throws MessageHandlerException ...
     */
    @Test public void testSuccess() throws MessageHandlerException {

        final Envelope env = XMLObjectProviderRegistrySupport.getBuilderFactory().<Envelope>ensureBuilder(
                Envelope.DEFAULT_ELEMENT_NAME).buildObject(Envelope.DEFAULT_ELEMENT_NAME);
        messageCtx.ensureSubcontext(SOAP11Context.class).setEnvelope(env);
        
        messageCtx.ensureSubcontext(SAMLBindingContext.class).setRelayState("foo");
        
        handler.invoke(messageCtx);
        
        final List<XMLObject> headers =
                SOAPMessagingSupport.getHeaderBlock(messageCtx, RelayState.DEFAULT_ELEMENT_NAME, null, true);
        
        Assert.assertEquals(headers.size(), 1);
        Assert.assertEquals(((RelayState) headers.get(0)).getValue(), "foo");
    }
    
}