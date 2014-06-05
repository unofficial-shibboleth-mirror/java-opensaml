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

package org.opensaml.saml.saml2.binding.impl;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.binding.BindingException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.saml2.ecp.Response;
import org.opensaml.soap.soap11.ActorBearing;
import org.opensaml.soap.util.SOAPSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MessageHandler to add the ECP {@link Response} header to an outgoing SOAP envelope.
 */
public class AddECPResponseHeaderHandler extends AbstractMessageHandler {
   
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AddECPResponseHeaderHandler.class);
    
    /** Builder for response header. */
    @Nonnull private final SAMLObjectBuilder<Response> responseBuilder;

    /** The location to record in the header. */
    @Nullable private URI assertionConsumerURL;
    
    /** Constructor. */
    public AddECPResponseHeaderHandler() {
        responseBuilder = (SAMLObjectBuilder<Response>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Response>getBuilderOrThrow(
                        Response.DEFAULT_ELEMENT_NAME);
    }
    

    /** {@inheritDoc} */
    @Override
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        try {
            assertionConsumerURL = SAMLBindingSupport.getEndpointURL(messageContext);
        } catch (final BindingException e) {
            log.warn(getLogPrefix() + " Error extracting ACS location from message context", e);
            return false;
        }
        
        return super.doPreInvoke(messageContext);
    }

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        final Response header = responseBuilder.buildObject();
        header.setAssertionConsumerServiceURL(assertionConsumerURL.toString());
        
        SOAPSupport.addSOAP11MustUnderstandAttribute(header, true);
        SOAPSupport.addSOAP11ActorAttribute(header, ActorBearing.SOAP11_ACTOR_NEXT);
        
        SOAPSupport.addHeaderBlock(messageContext, header);
    }
    
}