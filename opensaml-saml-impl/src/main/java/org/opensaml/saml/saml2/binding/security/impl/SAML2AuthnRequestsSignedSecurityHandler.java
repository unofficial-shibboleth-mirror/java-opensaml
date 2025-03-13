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

package org.opensaml.saml.saml2.binding.security.impl;

import javax.annotation.Nonnull;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.slf4j.Logger;

import com.google.common.base.Strings;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Message handler implementation that enforces the AuthnRequestsSigned flag of 
 * SAML 2 metadata element @{link {@link SPSSODescriptor}.
 */
public class SAML2AuthnRequestsSignedSecurityHandler extends AbstractMessageHandler{
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SAML2AuthnRequestsSignedSecurityHandler.class);

    /** {@inheritDoc} */
    public void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        final Object samlMessage = messageContext.getMessage();
        if (!(samlMessage instanceof AuthnRequest) ) {
            log.debug("Inbound message is not an instance of AuthnRequest, skipping evaluation...");
            return;
        }
        
        if (isRequestSigningRequired(messageContext)) {
            if (!isMessageSigned(messageContext)) {
                log.warn("Inbound AuthnRequest message was not signed");
                throw new MessageHandlerException("Inbound AuthnRequest was required to be signed but was not");
            }
        }

    }
    
    /**
     * Determine whether the inbound message is signed.
     * 
     * @param messageContext the message context being evaluated
     * @return true if the inbound message is signed, otherwise false
     */
    protected boolean isMessageSigned(@Nonnull final MessageContext messageContext) {
        return SAMLBindingSupport.isMessageSigned(messageContext);
    }

    
    /**
     * Determine whether a signature is required.
     * 
     * @param messageContext message context
     * 
     * @return true iff the request must be signed
     * 
     * @since 4.3.0
     */
    protected boolean isRequestSigningRequired(@Nonnull final MessageContext messageContext) {
        
        final SAMLPeerEntityContext peerContext = messageContext.ensureSubcontext(SAMLPeerEntityContext.class);
        if (peerContext == null || Strings.isNullOrEmpty(peerContext.getEntityId())) {
            log.warn("SAML peer entityID was not available, unable to evaluate rule");
            return false;
        }
        final String messageIssuer = peerContext.getEntityId();
        
        final SAMLMetadataContext metadataContext = peerContext.ensureSubcontext(SAMLMetadataContext.class);
        if (metadataContext == null || metadataContext.getRoleDescriptor() == null) {
            log.warn("SAMLPeerContext did not contain either a SAMLMetadataContext or a RoleDescriptor, " 
                    + "unable to evaluate rule");
            return false;
        }
        
        if (!(metadataContext.getRoleDescriptor() instanceof SPSSODescriptor)) {
            final RoleDescriptor role = metadataContext.getRoleDescriptor();
            log.warn("RoleDescriptor was not an SPSSODescriptor, it was a {}. Unable to evaluate rule", 
                    role != null ? role.getClass().getName() : "(null)");
            return false;
        }
        
        final SPSSODescriptor spssoRole = (SPSSODescriptor) metadataContext.getRoleDescriptor();
        assert spssoRole != null;
        if (Boolean.TRUE.equals(spssoRole.isAuthnRequestsSigned())) {
            log.debug("SPSSODescriptor for entity ID '{}' indicates AuthnRequests must be signed", messageIssuer);
            return true;
        }
        
        log.debug("SPSSODescriptor for entity ID '{}' does not require AuthnRequests to be signed", messageIssuer);
        return false;
    }

}