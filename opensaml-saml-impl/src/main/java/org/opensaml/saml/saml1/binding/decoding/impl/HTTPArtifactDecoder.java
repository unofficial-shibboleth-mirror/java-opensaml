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

package org.opensaml.saml.saml1.binding.decoding.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.binding.BindingDescriptor;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.binding.decoding.SAMLMessageDecoder;
import org.opensaml.saml.common.binding.impl.BaseSAMLHttpServletRequestDecoder;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.slf4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * SAML 1.X HTTP Artifact message decoder.
 * 
 * <strong>NOTE: This decoder is not yet implemented.</strong>
 */
public class HTTPArtifactDecoder extends BaseSAMLHttpServletRequestDecoder
        implements SAMLMessageDecoder {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(HTTPArtifactDecoder.class);

    /** Optional {@link BindingDescriptor} to inject into {@link SAMLBindingContext} created. */
    @Nullable private BindingDescriptor bindingDescriptor;
    
    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getBindingURI() {
        return SAMLConstants.SAML1_ARTIFACT_BINDING_URI;
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
        assert request != null;
        
        decodeTarget(messageContext, request);
        processArtifacts(messageContext, request);
        
        populateBindingContext(messageContext);
        
        setMessageContext(messageContext);
    }

    /**
     * Decodes the TARGET parameter and adds it to the message context.
     * 
     * @param messageContext current message context
     * @param request current servlet request
     * 
     * @throws MessageDecodingException thrown if there is a problem decoding the TARGET parameter.
     */
    protected void decodeTarget(@Nonnull final MessageContext messageContext, @Nonnull final HttpServletRequest request)
            throws MessageDecodingException {
        final String target = StringSupport.trim(request.getParameter("TARGET"));
        if (target == null) {
            log.error("URL TARGET parameter was missing or did not contain a value.");
            throw new MessageDecodingException("URL TARGET parameter was missing or did not contain a value.");
        }
        SAMLBindingSupport.setRelayState(messageContext, target);
    }

    /**
     * Process the incoming artifacts by decoding the artifacts, dereferencing them from the artifact source and 
     * storing the resulting response (with assertions) in the message context.
     * 
     * @param messageContext current message context
     * @param request current servlet request
     * 
     * @throws MessageDecodingException thrown if there is a problem decoding or dereferencing the artifacts
     */
    protected void processArtifacts(@Nonnull final MessageContext messageContext,
            @Nonnull final HttpServletRequest request) throws MessageDecodingException {
        final String[] encodedArtifacts = request.getParameterValues("SAMLart");
        if (encodedArtifacts == null || encodedArtifacts.length == 0) {
            log.error("URL SAMLart parameter was missing or did not contain a value");
            throw new MessageDecodingException("URL SAMLart parameter was missing or did not contain a value");
        }
        
        // TODO decode artifact(s); resolve issuer resolution endpoint; dereference using 
        // Request/AssertionArtifact(s) over synchronous backchannel binding;
        // store response as the inbound SAML message.
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
        bindingContext.setHasBindingSignature(false);
        bindingContext.setIntendedDestinationEndpointURIRequired(false);
    }
    
}