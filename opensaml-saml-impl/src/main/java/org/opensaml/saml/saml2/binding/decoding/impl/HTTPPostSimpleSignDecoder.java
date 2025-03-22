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

import java.io.UnsupportedEncodingException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.slf4j.Logger;

import com.google.common.base.Strings;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.DecodingException;
import net.shibboleth.shared.primitive.LoggerFactory;

/** Message decoder implementing the SAML 2.0 HTTP POST-SimpleSign binding. */
public class HTTPPostSimpleSignDecoder extends HTTPPostDecoder {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(HTTPPostSimpleSignDecoder.class);

    /** {@inheritDoc} */
    @Nonnull @NotEmpty public String getBindingURI() {
        return SAMLConstants.SAML2_POST_SIMPLE_SIGN_BINDING_URI;
    }
    
    /**
     * Populate the context which carries information specific to this binding.
     * 
     * @param messageContext the current message context
     */
    protected void populateBindingContext(@Nonnull final MessageContext messageContext) {
        final SAMLBindingContext bindingContext = messageContext.ensureSubcontext(SAMLBindingContext.class);
        bindingContext.setBindingUri(getBindingURI());
        bindingContext.setBindingDescriptor(getBindingDescriptor());
        bindingContext.setHasBindingSignature(
                !Strings.isNullOrEmpty(getHttpServletRequest().getParameter("Signature")));
        bindingContext.setIntendedDestinationEndpointURIRequired(SAMLBindingSupport.isMessageSigned(messageContext));
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doDecode() throws MessageDecodingException {
        super.doDecode();
        
        final byte[] signedContent = getSignedContent();
        if (signedContent == null) {
            log.warn("Failed to build signed content data, signature evaluation will be skipped");
            return;
        }

        getMessageContext().ensureSubcontext(SimpleSignatureContext.class).setSignedContent(signedContent);
    }

    /**
     * Get the signed content data.
     * 
     * @return the signed content
     * 
     * @throws MessageDecodingException if there is a fatal issue building the signed content
     */
    @Nullable protected byte[] getSignedContent() throws MessageDecodingException {
        final HttpServletRequest request = getHttpServletRequest();
        
        final StringBuilder builder = new StringBuilder();
        final String samlMsg;
        try {
            if (request.getParameter("SAMLRequest") != null) {
                samlMsg = new String(Base64Support.decode(request.getParameter("SAMLRequest")), "UTF-8");
                builder.append("SAMLRequest=" + samlMsg);
            } else if (request.getParameter("SAMLResponse") != null) {
                samlMsg = new String(Base64Support.decode(request.getParameter("SAMLResponse")), "UTF-8");
                builder.append("SAMLResponse=" + samlMsg);
            } else {
                log.warn("Could not extract either a SAMLRequest or a SAMLResponse from the form control data");
                return null;
            }
        } catch (final UnsupportedEncodingException e) {
            log.error("UTF-8 encoding is not supported, this VM is not Java compliant");
            throw new MessageDecodingException("Unable to process message, UTF-8 encoding is not supported");
        } catch (final DecodingException e) {
            log.error("Unable to Base64 decode either a SAMLRequest or a SAMLResponse from the form control data");
            throw new MessageDecodingException("Unable to Base64 decode either a SAMLRequest or a SAMLResponse "
                    + "from the form control data",e);
        }

        // Optional
        if (request.getParameter("RelayState") != null) {
            builder.append("&RelayState=" + request.getParameter("RelayState"));
        }

        // Mandatory
        if (request.getParameter("SigAlg") == null) {
            log.warn("Signature algorithm could not be extracted from request, cannot build simple signature content");
            return null;
        }
        builder.append("&SigAlg=" + request.getParameter("SigAlg"));

        final String constructed = builder.toString();
        if (Strings.isNullOrEmpty(constructed)) {
            log.warn("Could not construct signed content string from form control data");
            return null;
        }
        log.debug("Constructed signed content string for HTTP-Post-SimpleSign {}", constructed);

        try {
            return constructed.getBytes("UTF-8");
        } catch (final UnsupportedEncodingException e) {
            log.error("UTF-8 encoding is not supported, this VM is not Java compliant");
            throw new MessageDecodingException("Unable to process message, UTF-8 encoding is not supported");
        }
    }
    
}