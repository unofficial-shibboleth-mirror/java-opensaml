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

package org.opensaml.soap.soap11.encoder.http.impl;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.messaging.encoder.servlet.BaseHttpServletResponseXMLMessageEncoder;
import org.opensaml.soap.common.SOAPObjectBuilder;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.soap11.FaultCode;
import org.opensaml.soap.soap11.Header;
import org.opensaml.soap.wsaddressing.Action;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import jakarta.servlet.http.HttpServletResponse;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.servlet.HttpServletSupport;
import net.shibboleth.shared.xml.SerializeSupport;

/**
 * Basic SOAP 1.1 encoder for HTTP transport.
 */
public class HTTPSOAP11Encoder extends BaseHttpServletResponseXMLMessageEncoder {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(HTTPSOAP11Encoder.class);
    
    /** SOAP Envelope builder. */
    @Nonnull private SOAPObjectBuilder<Envelope> envBuilder;
    
    /** SOAP Body builder. */
    @Nonnull private SOAPObjectBuilder<Body> bodyBuilder;
    
    /** Constructor. */
    public HTTPSOAP11Encoder() {
        super();
        final XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
        envBuilder = (SOAPObjectBuilder<Envelope>) builderFactory.<Envelope>ensureBuilder(
                Envelope.DEFAULT_ELEMENT_NAME);
        bodyBuilder = (SOAPObjectBuilder<Body>) builderFactory.<Body>ensureBuilder(Body.DEFAULT_ELEMENT_NAME);
        setProtocolMessageLoggerCategory(BASE_PROTOCOL_MESSAGE_LOGGER_CATEGORY + ".SOAP");
    }
    
    /** {@inheritDoc} */
    public void prepareContext() throws MessageEncodingException {
        final MessageContext messageContext = getMessageContext();
        Object payload = null;
        
        assert messageContext != null;
        final Fault fault = SOAPMessagingSupport.getSOAP11Fault(messageContext);
        if (fault != null) {
            final FaultCode fcode = fault.getCode();
            log.debug("Saw SOAP 1.1 Fault payload with fault code, replacing any existing context message: {}", 
                    fcode != null ? fcode.getValue() : null);
            payload = fault;
            messageContext.setMessage(null);
        } else {
            payload = messageContext.getMessage();
        }
        
        if (payload == null || !(payload instanceof XMLObject)) {
            throw new MessageEncodingException("No outbound XML message or Fault contained in message context");
        }
        
        if (payload instanceof Envelope) {
            storeSOAPEnvelope((Envelope) payload);
        } else {
            buildAndStoreSOAPMessage((XMLObject) payload);
        }
    }

    /** {@inheritDoc} */
    protected void doEncode() throws MessageEncodingException {
        final Envelope envelope = getSOAPEnvelope();
        if (envelope == null) {
            throw new MessageEncodingException("SOAP envelope was null");
        }
        
        final Element envelopeElem = marshallMessage(envelope);
        
        final HttpServletResponse response = prepareHttpServletResponse();

        try {
            SerializeSupport.writeNode(envelopeElem, response.getOutputStream());
        } catch (final IOException e) {
            throw new MessageEncodingException("Problem writing SOAP envelope to servlet output stream", e);
        }
    }
    
    /**
     * Store the constructed SOAP envelope in the message context for later encoding.
     * 
     * @param envelope the SOAP envelope
     */
    protected void storeSOAPEnvelope(@Nullable final Envelope envelope) {
        getMessageContext().ensureSubcontext(SOAP11Context.class).setEnvelope(envelope);
    }

    /**
     * Retrieve the previously stored SOAP envelope from the message context.
     * 
     * @return the previously stored SOAP envelope
     */
    @Nullable protected Envelope getSOAPEnvelope() {
        return getMessageContext().ensureSubcontext(SOAP11Context.class).getEnvelope();
    }

    /**
     * Builds the SOAP message to be encoded.
     * 
     * @param payload body of the SOAP message
     */
    protected void buildAndStoreSOAPMessage(@Nonnull final XMLObject payload) {
        Envelope envelope = getSOAPEnvelope();
        if (envelope == null) {
            envelope = envBuilder.buildObject();
            storeSOAPEnvelope(envelope);
        }
        
        Body body = envelope.getBody();
        if (body == null) {
            body = bodyBuilder.buildObject();
            envelope.setBody(body);
        }
        
        if (!body.getUnknownXMLObjects().isEmpty()) {
            log.warn("Existing SOAP Envelope Body already contained children");
        }
        
        body.getUnknownXMLObjects().add(payload);
    }
    
    
    /**
     * <p>
     * This implementation performs the following actions on the context's {@link HttpServletResponse}:
     * </p>
     * <ol>
     *   <li>Adds the HTTP header: "Cache-control: no-cache, no-store"</li>
     *   <li>Adds the HTTP header: "Pragma: no-cache"</li>
     *   <li>Sets the character encoding to: "UTF-8"</li>
     *   <li>Sets the content type to: "text/xml"</li>
     *   <li>Sets the SOAPAction HTTP header the value returned by {@link #getSOAPAction()}, if
     *   that returns non-null.</li>
     * </ol>
     * 
     * <p>
     * Subclasses should NOT set the SOAPAction HTTP header in this method. Instead, they should override 
     * the method {@link #getSOAPAction()}.
     * </p>
     * 
     * @return the prepared response
     * 
     * @throws MessageEncodingException thrown if there is a problem preprocessing the transport
     */
    @Nonnull protected HttpServletResponse prepareHttpServletResponse() throws MessageEncodingException {
        
        final HttpServletResponse response = getHttpServletResponse();
        if (response == null) {
            throw new MessageEncodingException("HttpServletResponse was null");
        }
        
        HttpServletSupport.addNoCacheHeaders(response);
        HttpServletSupport.setUTF8Encoding(response);
        HttpServletSupport.setContentType(response, "text/xml");
        
        final String soapAction = getSOAPAction();
        if (soapAction != null) {
            response.setHeader("SOAPAction", soapAction);
        } else {
            response.setHeader("SOAPAction", "");
        }
        
        response.setStatus(getHTTPResponseStatusCode());
        
        return response;
    }

    /**
     * Determine the value of the SOAPAction HTTP header to send.
     * 
     * <p>
     * The default behavior is to return the value of the SOAP Envelope's WS-Addressing Action header,
     * if present.
     * </p>
     * 
     * @return a SOAPAction HTTP header URI value
     */
    @Nullable protected String getSOAPAction() {
        final Envelope env = getSOAPEnvelope();
        final Header header = env != null ? env.getHeader() : null;
        if (header == null) {
            return null;
        }
        final List<XMLObject> objList = header.getUnknownXMLObjects(Action.ELEMENT_NAME);
        if (objList == null || objList.isEmpty()) {
            return null;
        }
        return ((Action)objList.get(0)).getURI();
    }
    
    /**
     * Get the HTTP response status code to return.
     * 
     * @return the HTTP response status code
     */
    protected int getHTTPResponseStatusCode() {
        final Integer contextStatus =
                getMessageContext().ensureSubcontext(SOAP11Context.class).getHTTPResponseStatus();
        if (contextStatus != null) {
            return contextStatus;
        }
        
        final Envelope envelope = getSOAPEnvelope();
        if (envelope != null) {
            final Body body = envelope.getBody();
            final List<XMLObject> faults = body != null ? body.getUnknownXMLObjects(Fault.DEFAULT_ELEMENT_NAME) : null;
            if (faults != null && !faults.isEmpty()) {
                return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            }
        }
        
        return HttpServletResponse.SC_OK;
    }
    
    /** {@inheritDoc} */
    @Nullable protected XMLObject getMessageToLog() {
        return getMessageContext().ensureSubcontext(SOAP11Context.class).getEnvelope();
    }
    
}
