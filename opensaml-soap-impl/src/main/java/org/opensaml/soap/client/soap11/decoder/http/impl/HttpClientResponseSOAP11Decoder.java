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

package org.opensaml.soap.client.soap11.decoder.http.impl;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.decoder.httpclient.BaseHttpClientResponseXMLMessageDecoder;
import org.opensaml.messaging.handler.MessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.common.SOAP11FaultDecodingException;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.soap11.FaultCode;
import org.opensaml.soap.soap11.FaultString;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Basic SOAP 1.1 decoder for HTTP transport via an HttpClient's {@link ClassicHttpResponse}.
 * 
 * <p>
 * This decoder takes a mandatory {@link MessageHandler} instance which is used to
 * populate the message that is returned as the {@link MessageContext#getMessage()}.
 * </p>
 * 
 *  <p>
 *  A SOAP message oriented message exchange style might just populate the Envelope as the message.
 *  An application-specific payload-oriented message exchange would handle a specific type
 * of payload structure.  
 * </p>
 */
public class HttpClientResponseSOAP11Decoder extends BaseHttpClientResponseXMLMessageDecoder {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(HttpClientResponseSOAP11Decoder.class);
    
    /** Message handler to use in processing the message body. */
    @NonnullAfterInit private MessageHandler bodyHandler;
    
    /**
     * Get the configured body handler MessageHandler.
     * 
     * @return Returns the bodyHandler.
     */
    @NonnullAfterInit public MessageHandler getBodyHandler() {
        return bodyHandler;
    }

    /**
     * Set the configured body handler MessageHandler.
     * 
     * @param newBodyHandler The bodyHandler to set.
     */
    public void setBodyHandler(@Nonnull final MessageHandler newBodyHandler) {
        bodyHandler = Constraint.isNotNull(newBodyHandler, "MessageHandler cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (bodyHandler == null) {
            throw new ComponentInitializationException("Body handler MessageHandler cannot be null");
        }
    }    
    
    /** {@inheritDoc} */
    protected void doDecode() throws MessageDecodingException {
        final MessageContext messageContext = new MessageContext();
        final ClassicHttpResponse response = getHttpResponse();
        if (response == null) {
            throw new MessageDecodingException("No HttpResponse available");
        }
        
        log.debug("Unmarshalling SOAP message");
        try {
            final int responseStatusCode = response.getCode();
            
            switch(responseStatusCode) {
                case HttpStatus.SC_OK:
                    final SOAP11Context soapContext = messageContext.ensureSubcontext(SOAP11Context.class);
                    processSuccessResponse(response, soapContext);
                    break;
                case HttpStatus.SC_INTERNAL_SERVER_ERROR:
                    throw buildFaultException(response);
                default:
                    throw new MessageDecodingException("Received non-success HTTP response status code from SOAP call: "
                            + responseStatusCode);
            }
            
        } catch (final IOException e) {
            log.error("Unable to obtain input stream from HttpResponse: {}", e.getMessage());
            throw new MessageDecodingException("Unable to obtain input stream from HttpResponse", e);
        } finally {
            if (response instanceof CloseableHttpResponse) {
                try {
                    ((CloseableHttpResponse)response).close();
                } catch (final IOException e) {
                    log.warn("Error closing HttpResponse", e);
                }
            }
        }
        
        try {
            getBodyHandler().invoke(messageContext);
        } catch (final MessageHandlerException e) {
            log.error("Error processing SOAP Envelope body: {}", e.getMessage());
            throw new MessageDecodingException("Error processing SOAP Envelope body", e);
        }
        
        if (messageContext.getMessage() == null) {
            log.warn("Body handler did not properly populate the message in message context");
            throw new MessageDecodingException("Body handler did not properly populate the message in message context");
        }
        
        setMessageContext(messageContext);
        
    }
    
    /**
     * Process a successful response, i.e. one where the HTTP response code was 200.
     * 
     * @param httpResponse the HTTP client response
     * @param soapContext the SOAP11Context instance
     * 
     * @throws MessageDecodingException  if message can not be unmarshalled
     * @throws IOException if there is a problem with the response entity input stream
     */
    protected void processSuccessResponse(@Nonnull final ClassicHttpResponse httpResponse,
            @Nonnull final SOAP11Context soapContext) 
            throws MessageDecodingException, IOException {
        
        if (httpResponse.getEntity() == null) {
            throw new MessageDecodingException("No response body from server");
        }
        final Envelope soapMessage = (Envelope) unmarshallMessage(httpResponse.getEntity().getContent());
        
        // Defensive sanity check, otherwise body handler could later fail non-gracefully with runtime exception
        final Fault fault = getFault(soapMessage);
        if (fault != null) {
            throw new SOAP11FaultDecodingException(fault);
        }
        
        soapContext.setEnvelope(soapMessage);
        soapContext.setHTTPResponseStatus(httpResponse.getCode());
    }

    /**
     * Build an exception by processing a fault response, i.e. one where the HTTP response code was 500.
     * 
     * @param response the HTTP client response
     * @return the message decoding exception representing the SOAP fault
     * 
     * @throws MessageDecodingException if message can not be unmarshalled
     * @throws IOException if there is a problem with the response entity input stream
     */
    @Nonnull protected MessageDecodingException buildFaultException(@Nonnull final ClassicHttpResponse response) 
            throws MessageDecodingException, IOException {
        
        if (response.getEntity() == null) {
            throw new MessageDecodingException("No response body from server");
        }
        final Envelope soapMessage = (Envelope) unmarshallMessage(response.getEntity().getContent());
        
        final Fault fault = getFault(soapMessage);
        if (fault == null) {
            throw new MessageDecodingException("HTTP status code was 500 but SOAP response did not contain a Fault");
        }
        
        final FaultCode fcode = fault.getCode();
        final QName code = fcode != null ? fcode.getValue() : null;
        
        final FaultString fmsg = fault.getMessage();
        final String msg = fmsg != null ? fmsg.getValue() : null;
        log.debug("SOAP fault code '{}' with message '{}'", code != null ? code.toString() : "(not set)", msg);
        
        return new SOAP11FaultDecodingException(fault);
    }
    
    /**
     * Return the Fault element from the SOAP message, if any.
     * 
     * @param soapMessage the SOAP 1.1. Envelope being processed
     * @return the first Fault element found, or null
     */
    @Nullable protected Fault getFault(@Nonnull final Envelope soapMessage) {
        final Body body = soapMessage.getBody();
        if (body != null) {
            final List<XMLObject> faults = body.getUnknownXMLObjects(Fault.DEFAULT_ELEMENT_NAME);
            if (!faults.isEmpty()) {
                return (Fault) faults.get(0);
            }
        }
        return null;
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable protected XMLObject getMessageToLog() {
        final MessageContext mc = getMessageContext();
        return mc != null ? mc.ensureSubcontext(SOAP11Context.class).getEnvelope() : null;
    }
    
}