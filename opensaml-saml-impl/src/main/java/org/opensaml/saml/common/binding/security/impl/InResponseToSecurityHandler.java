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

package org.opensaml.saml.common.binding.security.impl;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Client-side message handler for validating that the inbound SAML response inResponseTo ID matches the corresponding
 * outbound request ID.
 */
public class InResponseToSecurityHandler extends AbstractMessageHandler {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(InResponseToSecurityHandler.class);

    /** {@inheritDoc} */
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        final String outboundRequestID = StringSupport.trimOrNull(resolveOutboundRequestID(messageContext));
        log.debug("Resolved outbound request ID: {}", outboundRequestID);
        
        final String inboundInResponseTo = StringSupport.trimOrNull(resolveInboundInResponseTo(messageContext));
        log.debug("Resolved inbound inResponseTo: {}", inboundInResponseTo);
        
        if (!Objects.equals(outboundRequestID, inboundInResponseTo)) {
            log.warn("Inbound inResponseTo '{}' did not match outbound request ID '{}'", 
                    inboundInResponseTo, outboundRequestID);
            throw new MessageHandlerException("Inbound inResponseTo did not match outbound request ID");
        }
    }

    /**
     * Resolve the outbound request ID.
     * 
     * @param messageContext the message context
     * @return the outbound request ID, or null
     */
    @Nullable private String resolveOutboundRequestID(@Nonnull final MessageContext messageContext) {
        if (messageContext.getParent() instanceof InOutOperationContext inout) {
            final MessageContext outboundContext = inout.getOutboundMessageContext();
            if (outboundContext != null && outboundContext.getMessage() instanceof SAMLObject outboundMessage) {
                if (outboundMessage instanceof org.opensaml.saml.saml2.core.RequestAbstractType req) {
                    return req.getID();
                } else if (outboundMessage instanceof org.opensaml.saml.saml1.core.RequestAbstractType req) {
                    return req.getID();
                }
            }
        }
        return null;
    }

    /**
     * Resolve the inbound inResponseTo ID.
     * 
     * @param messageContext the message context
     * @return the inbound inResponseTo, or null
     */
    @Nullable private String resolveInboundInResponseTo(@Nonnull final MessageContext messageContext) {
        if (messageContext.getParent() instanceof InOutOperationContext inout) {
            final MessageContext inboundContext = inout.getInboundMessageContext();
            if (inboundContext != null && inboundContext.getMessage() instanceof SAMLObject inboundMessage) {
                if (inboundMessage instanceof org.opensaml.saml.saml2.core.StatusResponseType resp) {
                    return resp.getInResponseTo();
                } else if (inboundMessage instanceof org.opensaml.saml.saml1.core.ResponseAbstractType resp) {
                    return resp.getInResponseTo();
                }
            }
        }
        return null;
    }

}
