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

package org.opensaml.saml.saml2.binding.decoding.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.BindingDescriptor;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.binding.decoding.SAMLMessageDecoder;
import org.opensaml.saml.common.binding.impl.BaseSAMLHttpServletRequestDecoder;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.slf4j.Logger;

import com.google.common.base.Strings;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * SAML 2.0 HTTP Redirect decoder using the DEFLATE encoding method.
 * 
 * This decoder only supports DEFLATE compression.
 */
public class HTTPRedirectDeflateDecoder extends BaseSAMLHttpServletRequestDecoder implements SAMLMessageDecoder {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(HTTPRedirectDeflateDecoder.class);

    /** Optional {@link BindingDescriptor} to inject into {@link SAMLBindingContext} created. */
    @Nullable private BindingDescriptor bindingDescriptor;
    
    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getBindingURI() {
        return SAMLConstants.SAML2_REDIRECT_BINDING_URI;
    }
    
    /**
     * Get an optional {@link BindingDescriptor} to inject into {@link SAMLBindingContext} created.
     * 
     * @return binding descriptor
     */
    @Nullable public BindingDescriptor getBindingDescriptor() {
        return bindingDescriptor;
    }
    
    /**
     * Set an optional {@link BindingDescriptor} to inject into {@link SAMLBindingContext} created.
     * 
     * @param descriptor a binding descriptor
     */
    public void setBindingDescriptor(@Nullable final BindingDescriptor descriptor) {
        bindingDescriptor = descriptor;
    }

    /** {@inheritDoc} */
    protected void doDecode() throws MessageDecodingException {
        final MessageContext messageContext = new MessageContext();
        final HttpServletRequest request = getHttpServletRequest();
        
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            throw new MessageDecodingException("This message decoder only supports the HTTP GET method");
        }
        
        final String samlEncoding = StringSupport.trimOrNull(request.getParameter("SAMLEncoding"));
        if (samlEncoding != null && !SAMLConstants.SAML2_BINDING_URL_ENCODING_DEFLATE_URI.equals(samlEncoding)) {
            throw new MessageDecodingException("Request indicated an unsupported SAMLEncoding: " + samlEncoding);
        }

        final String relayState = request.getParameter("RelayState");
        log.debug("Decoded RelayState: {}", relayState);
        SAMLBindingSupport.setRelayState(messageContext, relayState);

        final String samlMessageEncoded = !Strings.isNullOrEmpty(request.getParameter("SAMLRequest"))
                ? request.getParameter("SAMLRequest") : request.getParameter("SAMLResponse");

        if (samlMessageEncoded != null) {
            try (final InputStream samlMessageIns = decodeMessage(samlMessageEncoded)) {
                final SAMLObject samlMessage = (SAMLObject) unmarshallMessage(samlMessageIns);
                messageContext.setMessage(samlMessage);
                log.debug("Decoded SAML message");
            } catch (final IOException e) {
                throw new MessageDecodingException("InputStream exception decoding SAML message", e);
            }
        } else {
            throw new MessageDecodingException(
                    "No SAMLRequest or SAMLResponse query path parameter, invalid SAML 2 HTTP Redirect message");
        }

        populateBindingContext(messageContext);

        setMessageContext(messageContext);
    }

    /**
     * Base64 decodes the SAML message and then decompresses the message.
     * 
     * @param message Base64 encoded, DEFALTE compressed, SAML message
     * 
     * @return the SAML message
     * 
     * @throws MessageDecodingException thrown if the message can not be decoded
     */
    @Nonnull protected InputStream decodeMessage(@Nonnull final String message) throws MessageDecodingException {
        log.debug("Base64 decoding and inflating SAML message");

        try {
            final byte[] decodedBytes = Base64Support.decode(message);
            return new NoWrapAutoEndInflaterInputStream(new ByteArrayInputStream(decodedBytes));
        } catch (final Exception e) {
            log.error("Unable to Base64 decode and inflate SAML message: {}", e.getMessage());
            throw new MessageDecodingException("Unable to Base64 decode and inflate SAML message", e);
        }
    }
    
    /**
     * Populate the context which carries information specific to this binding.
     * 
     * @param messageContext the current message context
     */
    protected void populateBindingContext(@Nonnull final MessageContext messageContext) {
        final SAMLBindingContext bindingContext = messageContext.ensureSubcontext(SAMLBindingContext.class);
        bindingContext.setBindingUri(getBindingURI());
        bindingContext.setBindingDescriptor(bindingDescriptor);
        bindingContext.setHasBindingSignature(
                !Strings.isNullOrEmpty(getHttpServletRequest().getParameter("Signature")));
        bindingContext.setIntendedDestinationEndpointURIRequired(SAMLBindingSupport.isMessageSigned(messageContext));
    }
    
    /** A subclass of {@link InflaterInputStream} which defaults in a no-wrap {@link Inflater} instance and
     * closes it when the stream is closed.
     */
    private class NoWrapAutoEndInflaterInputStream extends InflaterInputStream {

        /**
         * Creates a new input stream with a default no-wrap decompressor and buffer size.
         *
         * @param is the input stream
         */
        public NoWrapAutoEndInflaterInputStream(@Nonnull final InputStream is) {
            super(is, new Inflater(true));
        }

        /** {@inheritDoc} */
        public void close() throws IOException {
            if (inf != null) {
                inf.end();
            }
            super.close();
        }

    }

}