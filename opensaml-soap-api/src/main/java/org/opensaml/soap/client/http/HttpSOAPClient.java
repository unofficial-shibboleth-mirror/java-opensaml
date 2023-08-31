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

package org.opensaml.soap.client.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.namespace.QName;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.security.SecurityException;
import org.opensaml.soap.client.SOAPClient;
import org.opensaml.soap.client.SOAPClientContext;
import org.opensaml.soap.client.SOAPClientException;
import org.opensaml.soap.client.SOAPFaultException;
import org.opensaml.soap.common.SOAPException;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.soap11.FaultCode;
import org.opensaml.soap.soap11.FaultString;
import org.slf4j.Logger;
import org.w3c.dom.Element;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.component.AbstractInitializableComponent;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.xml.ParserPool;
import net.shibboleth.shared.xml.SerializeSupport;
import net.shibboleth.shared.xml.XMLParserException;

/**
 * SOAP client that uses HTTP as the underlying transport and POST as the binding.
 */
@ThreadSafe
public class HttpSOAPClient extends AbstractInitializableComponent implements SOAPClient {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(HttpSOAPClient.class);

    /** HTTP client used to send requests and receive responses. */
    @NonnullAfterInit private HttpClient httpClient;

    /** Pool of XML parsers used to parser incoming responses. */
    @NonnullAfterInit private ParserPool parserPool;

    /**
     * Strategy used to look up the {@link SOAPClientContext} associated with the
     * outbound message context.
     */
    @Nonnull private Function<MessageContext, SOAPClientContext> soapClientContextLookupStrategy;

    /**
     * Strategy used to look up the {@link SOAP11Context} associated with the
     * outbound message context.
     */
    @Nonnull private Function<MessageContext, SOAP11Context> soap11ContextLookupStrategy;
    
    /** Constructor. */
    public HttpSOAPClient() {
        soapClientContextLookupStrategy = new ChildContextLookup<>(SOAPClientContext.class);
        soap11ContextLookupStrategy = new ChildContextLookup<>(SOAP11Context.class);
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (httpClient == null) {
            throw new ComponentInitializationException("HttpClient cannot be null");
        } else if (parserPool == null) {
            throw new ComponentInitializationException("ParserPool cannot be null");
        }
    }
    
    /**
     * Set the client used to make outbound HTTP requests.
     * 
     * <p>This client SHOULD employ a thread-safe {@link HttpClient} and may be shared with other objects.</p>
     * 
     * @param client client object
     */
    public void setHttpClient(@Nonnull final HttpClient client) {
        checkSetterPreconditions();
        
        httpClient = Constraint.isNotNull(client, "HttpClient cannot be null");
    }
    
    /**
     * Set the pool of XML parsers used to parse incoming responses.
     * 
     * @param parser parser pool
     */
    public void setParserPool(@Nonnull final ParserPool parser) {
        checkSetterPreconditions();
        
        parserPool = Constraint.isNotNull(parser, "ParserPool cannot be null");
    }
    



    /**
     * Get the strategy used to look up the {@link SOAPClientContext} associated with the outbound message
     * context.
     * 
     * @return strategy used to look up the {@link SOAPClientContext} associated with the outbound message
     *         context
     */
    @Nonnull public Function<MessageContext,SOAPClientContext> getSOAPClientContextLookupStrategy() {
        return soapClientContextLookupStrategy;
    }

    /**
     * Set the strategy used to look up the {@link SOAPClientContext} associated with the outbound message
     * context.
     * 
     * @param strategy strategy used to look up the {@link SOAPClientContext} associated with the outbound
     *            message context
     */
    public void setSOAPClientContextLookupStrategy(@Nonnull final Function<MessageContext,SOAPClientContext> strategy) {
        checkSetterPreconditions();
        
        soapClientContextLookupStrategy =
                Constraint.isNotNull(strategy, "SOAP client context lookup strategy cannot be null");
    }

    /**
     * Get the strategy used to look up the {@link SOAP11Context} associated with the outbound message
     * context.
     * 
     * @return strategy used to look up the {@link SOAP11Context} associated with the outbound message
     *         context
     */
    @Nonnull public Function<MessageContext,SOAP11Context> getSOAP11ContextLookupStrategy() {
        return soap11ContextLookupStrategy;
    }

    /**
     * Set the strategy used to look up the {@link SOAP11Context} associated with the outbound message
     * context.
     * 
     * @param strategy strategy used to look up the {@link SOAP11Context} associated with the outbound
     *            message context
     */
    public void setSOAP11ContextLookupStrategy(@Nonnull final Function<MessageContext,SOAP11Context> strategy) {
        checkSetterPreconditions();
        
        soap11ContextLookupStrategy =
                Constraint.isNotNull(strategy, "SOAP 1.1 context lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    public void send(@Nonnull @NotEmpty final String endpoint, @Nonnull final InOutOperationContext context)
            throws SOAPException, SecurityException {
        Constraint.isNotNull(endpoint, "Endpoint cannot be null");
        Constraint.isNotNull(context, "Operation context cannot be null");
        
        final SOAP11Context soapCtx = soap11ContextLookupStrategy.apply(context.getOutboundMessageContext());
        final SOAPClientContext clientCtx = soapClientContextLookupStrategy.apply(context.getOutboundMessageContext());

        HttpSOAPRequestParameters soapRequestParams = null;
        
        final Envelope env = soapCtx != null ? soapCtx.getEnvelope() : null;
        if (env == null) {
            throw new SOAPClientException("Operation context did not contain an outbound SOAP Envelope");
        } else if (clientCtx != null) {
            soapRequestParams = (HttpSOAPRequestParameters) clientCtx.getSOAPRequestParameters();
        }
        
        final HttpPost post = createPostMethod(endpoint, soapRequestParams, env);

        try (final ClassicHttpResponse response = httpClient.executeOpen(null, post, null)) {
            final int code = response.getCode();
            log.debug("Received HTTP status code of {} when POSTing SOAP message to {}", code, endpoint);

            if (code == HttpStatus.SC_OK) {
                processSuccessfulResponse(response, context);
            } else if (code == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                processFaultResponse(response, context);
            } else {
                throw new SOAPClientException("Received " + code +
                        " HTTP response status code from HTTP request to " + endpoint);
            }
        } catch (final IOException e) {
            throw new SOAPClientException("Unable to send request to " + endpoint, e);
        }
    }

    /**
     * Create the post method used to send the SOAP request.
     * 
     * @param endpoint endpoint to which the message is sent
     * @param requestParams HTTP request parameters
     * @param message message to be sent
     * 
     * @return the post method to be used to send this message
     * 
     * @throws SOAPClientException thrown if the message could not be marshalled
     */
    protected HttpPost createPostMethod(@Nonnull @NotEmpty final String endpoint,
            @Nullable final HttpSOAPRequestParameters requestParams, @Nonnull final Envelope message)
            throws SOAPClientException {
        log.debug("POSTing SOAP message to {}", endpoint);

        final HttpPost post = new HttpPost(endpoint);
        post.setEntity(createRequestEntity(message, Charset.forName("UTF-8")));
        if (requestParams != null && requestParams.getSOAPAction() != null) {
            post.setHeader(HttpSOAPRequestParameters.SOAP_ACTION_HEADER, requestParams.getSOAPAction());
        }

        return post;
    }

    /**
     * Create the request entity that makes up the POST message body.
     * 
     * @param message message to be sent
     * @param charset character set used for the message
     * 
     * @return request entity that makes up the POST message body
     * 
     * @throws SOAPClientException thrown if the message could not be marshalled
     */
    @Nonnull protected HttpEntity createRequestEntity(@Nonnull final Envelope message, @Nullable final Charset charset)
            throws SOAPClientException {
        try {
            final Marshaller marshaller =
                    XMLObjectProviderRegistrySupport.getMarshallerFactory().ensureMarshaller(message);
            final ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();

            if (log.isDebugEnabled()) {
                log.debug("Outbound SOAP message is:\n" +
                        SerializeSupport.prettyPrintXML(marshaller.marshall(message)));
            }
            SerializeSupport.writeNode(marshaller.marshall(message), arrayOut);
            return new ByteArrayEntity(arrayOut.toByteArray(), ContentType.TEXT_XML);
        } catch (final MarshallingException e) {
            throw new SOAPClientException("Unable to marshall SOAP envelope", e);
        }
    }

    /**
     * Process a successful, as determined by an HTTP 200 status code, response.
     * 
     * @param httpResponse the HTTP response
     * @param context current operation context
     * 
     * @throws SOAPClientException thrown if there is a problem reading the response from the {@link HttpPost}
     */
    protected void processSuccessfulResponse(@Nonnull final ClassicHttpResponse httpResponse,
            @Nonnull final InOutOperationContext context) throws SOAPClientException {
        try (final HttpEntity entity = httpResponse.getEntity()) {
            if (entity == null) {
                throw new SOAPClientException("No response body from server");
            }

            final Envelope response = unmarshallResponse(entity.getContent());
            context.setInboundMessageContext(new MessageContext());
            context.ensureInboundMessageContext().ensureSubcontext(SOAP11Context.class).setEnvelope(response);
            //TODO: goes away?
            //evaluateSecurityPolicy(messageContext);
        } catch (final IOException e) {
            throw new SOAPClientException("Unable to read response", e);
        }
    }

    /**
     * Process a SOAP fault, as determined by an HTTP 500 status code, response.
     * 
     * @param httpResponse the HTTP response
     * @param context current operation context
     * 
     * @throws SOAPClientException thrown if the response can not be read from the {@link HttpPost}
     * @throws SOAPFaultException an exception containing the SOAP fault
     */
    protected void processFaultResponse(@Nonnull final ClassicHttpResponse httpResponse,
            @Nonnull final InOutOperationContext context) throws SOAPClientException, SOAPFaultException {

        try (final HttpEntity entity = httpResponse.getEntity()){
            if (entity == null) {
                throw new SOAPClientException("No response body from server");
            }

            final Envelope response = unmarshallResponse(entity.getContent());
            context.setInboundMessageContext(new MessageContext());
            context.ensureInboundMessageContext().ensureSubcontext(SOAP11Context.class).setEnvelope(response);

            final Body body = response.getBody();
            if (body != null) {
                final List<XMLObject> faults = body.getUnknownXMLObjects(Fault.DEFAULT_ELEMENT_NAME);
                if (faults.size() < 1) {
                    throw new SOAPClientException("HTTP status code was 500 but SOAP response did not contain a Fault");
                }
                
                String code = "(not set)";
                String msg = "(not set)";
                final Fault fault = (Fault) faults.get(0);
                final FaultCode fcode = fault.getCode();
                if (fcode != null) {
                    final QName fcodeval = fcode.getValue();
                    if (fcodeval != null) {
                        code = fcodeval.toString();
                    }
                }
                
                final FaultString fstring = fault.getMessage();
                if (fstring != null && fstring.getValue() != null) {
                    msg = fstring.getValue();
                }
                
                log.debug("SOAP fault code {} with message {}", code, msg);
                final SOAPFaultException faultException = new SOAPFaultException("SOAP Fault: " + code
                        + " Fault Message: " + msg);
                faultException.setFault(fault);
                throw faultException;
            }
            throw new SOAPClientException("HTTP status code was 500 but SOAP response did not contain a Body");
        } catch (final IOException e) {
            throw new SOAPClientException("Unable to read response", e);
        }
    }

    /**
     * Unmarshall the incoming response from a POST request.
     * 
     * @param responseStream input stream bearing the response
     * 
     * @return the response
     * 
     * @throws SOAPClientException thrown if the incoming response can not be unmarshalled into an {@link Envelope}
     */
    @Nonnull protected Envelope unmarshallResponse(@Nonnull final InputStream responseStream)
            throws SOAPClientException {
        try (responseStream) {
            final Element responseElem = parserPool.parse(responseStream).getDocumentElement();
            assert responseElem != null;
            if (log.isDebugEnabled()) {
                log.debug("Inbound SOAP message was:\n" + SerializeSupport.prettyPrintXML(responseElem));
            }
            final Unmarshaller unmarshaller = Constraint.isNotNull(
                    XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(responseElem),
                    "SOAP envelope unmarshaller not available");
            return (Envelope) unmarshaller.unmarshall(responseElem);
        } catch (final XMLParserException|IOException e) {
            throw new SOAPClientException("Unable to parse the XML within the response", e);
        } catch (final UnmarshallingException e) {
            throw new SOAPClientException("Unable to unmarshall the response DOM", e);
        }
    }
    
}