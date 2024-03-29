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

package org.opensaml.soap.wsaddressing.messaging.impl;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.messaging.AbstractHeaderGeneratingMessageHandler;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.wsaddressing.MessageID;
import org.opensaml.soap.wsaddressing.messaging.WSAddressingContext;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.security.IdentifierGenerationStrategy;

/**
 * Handler implementation that adds a wsa:MessageID header to the outbound SOAP envelope.
 * 
 * <p>
 * The value from {@link WSAddressingContext#getMessageIDURI()} will be used, if present. If not,
 * then the value generated via the locally-configured strategy {@link #getIdentifierGenerationStrategy()}
 * will be used. If neither of those sources are available, then a random "urn:uuid:..." URI value will be
 * generated internally using {@link UUID}.
 * </p>
 */
public class AddMessageIDHandler extends AbstractHeaderGeneratingMessageHandler {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(AddMessageIDHandler.class);
    
    /** Strategy for generating identifiers. */
    @Nullable private IdentifierGenerationStrategy identifierGenerationStrategy;
    
    /**
     * Get the identifier generation strategy.
     * 
     * @return the strategy, or null
     */
    @Nullable public IdentifierGenerationStrategy getIdentifierGenerationStrategy() {
        return identifierGenerationStrategy;
    }

    /**
     * Set the identifier generation strategy.
     * 
     * @param strategy the new strategy
     */
    public void setIdentifierGenerationStrategy(@Nullable final IdentifierGenerationStrategy strategy) {
        checkSetterPreconditions();
        identifierGenerationStrategy = strategy;
    }

    /** {@inheritDoc} */
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        final String id = getMessageID(messageContext);
        log.debug("Issuing WS-Addressing MessageID: {}", id);
        final MessageID messageID = (MessageID) XMLObjectSupport.buildXMLObject(MessageID.ELEMENT_NAME);
        messageID.setURI(id);
        decorateGeneratedHeader(messageContext, messageID);
        SOAPMessagingSupport.addHeaderBlock(messageContext, messageID);
    }

    /**
     * Get the effective message ID value to issue in the outbound message. 
     * 
     * @param messageContext the current message context
     * 
     * @return the retrieved or generated message ID
     */
    @Nonnull protected String getMessageID(final MessageContext messageContext) {
        final WSAddressingContext addressing = messageContext.getSubcontext(WSAddressingContext.class);
        final String id = addressing != null ? addressing.getMessageIDURI() : null;
        if (id != null) {
            return id;
        } else if (identifierGenerationStrategy != null) {
            return identifierGenerationStrategy.generateIdentifier(false);
        } else {
            return "urn:uuid:" + UUID.randomUUID().toString();
        }
    }
    
}