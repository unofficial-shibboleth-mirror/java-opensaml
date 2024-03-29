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

package org.opensaml.saml.saml1.binding.impl;

import java.io.IOException;

import javax.annotation.Nonnull;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.binding.artifact.SAMLArtifactMap;
import org.opensaml.saml.common.binding.artifact.SAMLArtifactMap.SAMLArtifactMapEntry;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml1.core.Request;
import org.slf4j.Logger;


/**
 * SAML {@link org.opensaml.messaging.handler.MessageHandler} that addresses the SAML 1.x
 * limitation that omitted an Issuer value from protocol messages, for the specific case
 * of artifact resolution requests.
 * 
 * <p>The issuer/requester is deduced in this case by resolving an artifact from the request
 * and assuming the issuer is the intended recipient of the artifact.</p> 
 */
public class SAML1ArtifactRequestIssuerHandler extends AbstractMessageHandler {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SAML1ArtifactRequestIssuerHandler.class);
    
    /** Protocol value to add to context. */
    @NonnullAfterInit private SAMLArtifactMap artifactMap;

    /**
     * Set the artifact map to use.
     * 
     * @param map the artifact map
     */
    public void setArtifactMap(@Nonnull final SAMLArtifactMap map) {
        artifactMap = Constraint.isNotNull(map, "SAMLArtifactMap cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (artifactMap == null) {
            throw new ComponentInitializationException("SAMLArtifactMap cannot be null");
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        if (messageContext.getMessage() == null || !(messageContext.getMessage() instanceof Request)) {
            log.trace("{} Request message not set, or not of an applicable type", getLogPrefix());
            return;
        }
        
        final Request request = (Request) messageContext.getMessage();
        assert request != null;
        if (request.getAssertionArtifacts().isEmpty()) {
            log.trace("{} Request did not contain any artifacts", getLogPrefix());
            return;
        }
        
        final String artifact = request.getAssertionArtifacts().get(0).getValue();
        if (artifact == null) {
            log.trace("{} Request did not contain any artifacts", getLogPrefix());
            return;
        }
        
        try {
            final SAMLArtifactMapEntry entry = artifactMap.get(artifact);
            if (entry == null) {
                log.warn("{} Unable to resolve first artifact in request: {}", getLogPrefix(), artifact);
                return;
            }
            
            log.debug("{} Derived issuer of aritfact resolution request as {}", getLogPrefix(),
                    entry.getRelyingPartyId());
            messageContext.ensureSubcontext(SAMLPeerEntityContext.class).setEntityId(entry.getRelyingPartyId());
        } catch (final IOException e) {
            log.error("{} Error resolving first artifact in request: {}", getLogPrefix(), artifact, e);
        }
    }
    
}