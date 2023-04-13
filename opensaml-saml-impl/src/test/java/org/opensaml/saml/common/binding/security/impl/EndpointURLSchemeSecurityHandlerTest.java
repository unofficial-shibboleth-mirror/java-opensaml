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

package org.opensaml.saml.common.binding.security.impl;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

/**
 * Test the security handler which evaluates message context endpoint URL schemes.
 */
@SuppressWarnings("javadoc")
public class EndpointURLSchemeSecurityHandlerTest extends XMLObjectBaseTestCase {
    
    private EndpointURLSchemeSecurityHandler handler;
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        handler = new EndpointURLSchemeSecurityHandler();
        handler.initialize();
    }
    
    @Test
    public void testValidRequestLocation() throws MessageHandlerException {
        AssertionConsumerService endpoint = buildXMLObject(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        endpoint.setLocation("http://idp.example.com/sso");
        
        final MessageContext messageContext = new MessageContext();
        messageContext.setMessage(buildXMLObject(AuthnRequest.DEFAULT_ELEMENT_NAME));
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class).ensureSubcontext(SAMLEndpointContext.class).setEndpoint(endpoint);
        
        handler.invoke(messageContext);
    }
    
    @Test
    public void testValidResponseLocation() throws MessageHandlerException {
        AssertionConsumerService endpoint = buildXMLObject(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        endpoint.setResponseLocation("http://sp.example.com/acs");
        
        final MessageContext messageContext = new MessageContext();
        messageContext.setMessage(buildXMLObject(Response.DEFAULT_ELEMENT_NAME));
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class).ensureSubcontext(SAMLEndpointContext.class).setEndpoint(endpoint);
        
        handler.invoke(messageContext);
    }
    
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testInvalidRequestLocation() throws MessageHandlerException {
        AssertionConsumerService endpoint = buildXMLObject(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        endpoint.setLocation("badscheme://idp.example.com/sso");
        
        final MessageContext messageContext = new MessageContext();
        messageContext.setMessage(buildXMLObject(AuthnRequest.DEFAULT_ELEMENT_NAME));
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class).ensureSubcontext(SAMLEndpointContext.class).setEndpoint(endpoint);
        
        handler.invoke(messageContext);
    }
    
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testInvalidResponseLocation() throws MessageHandlerException {
        AssertionConsumerService endpoint = buildXMLObject(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        endpoint.setResponseLocation("badscheme://sp.example.com/acs");
        
        final MessageContext messageContext = new MessageContext();
        messageContext.setMessage(buildXMLObject(Response.DEFAULT_ELEMENT_NAME));
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class).ensureSubcontext(SAMLEndpointContext.class).setEndpoint(endpoint);
        
        handler.invoke(messageContext);
    }

}