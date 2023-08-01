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

package org.opensaml.soap.client.soap11.encoder.http.impl;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.messaging.encoder.httpclient.BaseHttpClientRequestXMLMessageEncoder;
import org.opensaml.soap.common.SOAPObjectBuilder;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Header;
import org.opensaml.soap.wsaddressing.Action;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.xml.SerializeSupport;

/**
 * Basic SOAP 1.1 encoder for HTTP transport via an HttpClient's {@link ClassicHttpRequest}.
 */
public class HttpClientRequestSOAP11Encoder extends BaseHttpClientRequestXMLMessageEncoder {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(HttpClientRequestSOAP11Encoder.class);
    
    /** SOAP Envelope builder. */
    @Nonnull private SOAPObjectBuilder<Envelope> envBuilder;
    
    /** SOAP Body builder. */
    @Nonnull private SOAPObjectBuilder<Body> bodyBuilder;
    
    /** Constructor. */
    public HttpClientRequestSOAP11Encoder() {
        final XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
        envBuilder = (SOAPObjectBuilder<Envelope>) builderFactory.<Envelope>ensureBuilder(
                Envelope.DEFAULT_ELEMENT_NAME);
        bodyBuilder = (SOAPObjectBuilder<Body>) builderFactory.<Body>ensureBuilder(Body.DEFAULT_ELEMENT_NAME);
    }
    
    /** {@inheritDoc}
     * 
     * <p>This encoder implementation only operates on instances of {@link HttpPost}.</p>
     * 
     */
    @NonnullAfterInit public HttpPost getHttpRequest() {
        return (HttpPost) super.getHttpRequest();
    }

    /** {@inheritDoc}
     * 
     * <p>This encoder implementation only operates on instances of {@link HttpPost}.</p>
     */
    public synchronized void setHttpRequest(@Nullable final ClassicHttpRequest httpRequest) {
        if (!(httpRequest instanceof HttpPost)) {
            throw new IllegalArgumentException("HttpClient SOAP message encoder only operates on HttpPost");
        }
        super.setHttpRequest(httpRequest);
    }

    /** {@inheritDoc} */
    public void prepareContext() throws MessageEncodingException {
        final MessageContext messageContext = getMessageContext();
        final Object message = messageContext.getMessage();
        if (message == null || !(message instanceof XMLObject)) {
            throw new MessageEncodingException("No outbound XML message contained in message context");
        }
        
        if (message instanceof Envelope) {
            storeSOAPEnvelope((Envelope) message);
        } else {
            buildAndStoreSOAPMessage((XMLObject) message);
        }
        
    }

    /** {@inheritDoc} */
    protected void doEncode() throws MessageEncodingException {
        final Envelope envelope = getSOAPEnvelope();
        if (envelope == null) {
            throw new MessageEncodingException("SOAP envelope was null");
        }
        
        prepareHttpRequest();

        getHttpRequest().setEntity(createRequestEntity(envelope, Charset.forName("UTF-8")));
    }
    
    /**
     * Create the request entity that makes up the POST message body.
     * 
     * @param message message to be sent
     * @param charset character set used for the message
     * 
     * @return request entity that makes up the POST message body
     * 
     * @throws MessageEncodingException thrown if the message could not be marshalled
     */
    protected HttpEntity createRequestEntity(@Nonnull final Envelope message, @Nonnull final Charset charset)
            throws MessageEncodingException {
        try {
            final ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
            SerializeSupport.writeNode(XMLObjectSupport.marshall(message), arrayOut);
            return new ByteArrayEntity(arrayOut.toByteArray(), ContentType.TEXT_XML, charset.name());
        } catch (final MarshallingException e) {
            throw new MessageEncodingException("Unable to marshall SOAP envelope", e);
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
     * This implementation performs the following actions on the context's {@link ClassicHttpRequest}:
     * </p>
     * <ol>
     *   <li>Sets the SOAPAction HTTP header the value returned by {@link #getSOAPAction()}, if
     *   that returns non-null.</li>
     * </ol>
     * 
     * <p>
     * Subclasses should NOT set the SOAPAction HTTP header in this method. Instead, they should override 
     * the method {@link #getSOAPAction()}.
     * </p>
     * 
     * @throws MessageEncodingException thrown if there is a problem preprocessing the transport
     */
    protected void prepareHttpRequest() throws MessageEncodingException {
        //TODO - need to do more here?
        final String soapAction = getSOAPAction();
        if (soapAction != null) {
            getHttpRequest().setHeader("SOAPAction", soapAction);
        } else {
            getHttpRequest().setHeader("SOAPAction", "");
        }
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
        if (env == null) {
            return null;
        }
        final Header header = env.getHeader();
        if (header == null) {
            return null;
        }
        final List<XMLObject> objList = header.getUnknownXMLObjects(Action.ELEMENT_NAME);
        if (objList == null || objList.isEmpty()) {
            return null;
        }
        return ((Action)objList.get(0)).getURI();
    }
    
    /** {@inheritDoc} */
    @Nullable protected XMLObject getMessageToLog() {
        return getMessageContext().ensureSubcontext(SOAP11Context.class).getEnvelope();
    }

}
