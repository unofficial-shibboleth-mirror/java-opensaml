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

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

/**
 * Test the {@link SAMLOutboundDestinationHandler}.
 */
@SuppressWarnings({"null", "javadoc"})
public class SAMLOutboundDestinationHandlerTest extends XMLObjectBaseTestCase {
    
    private SAMLOutboundDestinationHandler handler;
    private MessageContext messageContext;
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        SAMLObjectBuilder<AssertionConsumerService> endpointBuilder =
                (SAMLObjectBuilder<AssertionConsumerService>) builderFactory.<AssertionConsumerService>ensureBuilder(
                        AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        AssertionConsumerService samlEndpoint = endpointBuilder.buildObject();
        samlEndpoint.setLocation("http://example.org");
        
        handler = new SAMLOutboundDestinationHandler();
        handler.initialize();
        messageContext = new MessageContext();
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class).
            ensureSubcontext(SAMLEndpointContext.class).setEndpoint(samlEndpoint);
    }
    
    @Test
    public void testSAML1Response() throws MessageHandlerException {
        SAMLObjectBuilder<org.opensaml.saml.saml1.core.Response> requestBuilder = 
                (SAMLObjectBuilder<org.opensaml.saml.saml1.core.Response>)
                builderFactory.<org.opensaml.saml.saml1.core.Response>ensureBuilder(
                        org.opensaml.saml.saml1.core.Response.DEFAULT_ELEMENT_NAME);
        org.opensaml.saml.saml1.core.Response samlMessage = requestBuilder.buildObject();
        messageContext.setMessage(samlMessage);
        
        Assert.assertNull(samlMessage.getRecipient(), "Recipient was not null");
        
        handler.invoke(messageContext);
        
        Assert.assertNotNull(samlMessage.getRecipient(), "Recipient was null");
    }
    
    @Test
    public void testSAML2Request() throws MessageHandlerException {
        SAMLObjectBuilder<AuthnRequest> requestBuilder =
                (SAMLObjectBuilder<AuthnRequest>) builderFactory.<AuthnRequest>ensureBuilder(
                        AuthnRequest.DEFAULT_ELEMENT_NAME);
        AuthnRequest samlMessage = requestBuilder.buildObject();
        messageContext.setMessage(samlMessage);
        
        Assert.assertNull(samlMessage.getDestination(), "Destination was not null");
        
        handler.invoke(messageContext);
        
        Assert.assertNotNull(samlMessage.getDestination(), "Destination was null");
    }

    @Test
    public void testSAML2Response() throws MessageHandlerException {
        SAMLObjectBuilder<org.opensaml.saml.saml2.core.Response> requestBuilder = 
                (SAMLObjectBuilder<org.opensaml.saml.saml2.core.Response>)
                builderFactory.<org.opensaml.saml.saml2.core.Response>ensureBuilder(
                        org.opensaml.saml.saml2.core.Response.DEFAULT_ELEMENT_NAME);
        org.opensaml.saml.saml2.core.Response samlMessage = requestBuilder.buildObject();
        messageContext.setMessage(samlMessage);
        
        Assert.assertNull(samlMessage.getDestination(), "Destination was not null");
        
        handler.invoke(messageContext);
        
        Assert.assertNotNull(samlMessage.getDestination(), "Destination was null");
    }
    
}