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

package org.opensaml.saml.common.messaging.context;

import java.time.Instant;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * A context intended to be used as a subcontext of a {@link MessageContext}  that carries 
 * some basic information about the SAML message.
 * 
 * <p>
 * The methods {@link #getMessageId()} and {@link #getMessageIssueInstant()} will attempt to 
 * dynamically resolve the appropriate data from the SAML message held in the message context
 * if the data has not been set statically by the corresponding setter method. This evaluation
 * will be attempted only if the this context instance is an immediate child of the message context,
 * as returned by {@link #getParent()}.
 * </p>
 *
 */
public final class SAMLMessageInfoContext extends BaseContext {

    /** The ID of the message. */
    @Nullable @NotEmpty private String messageId;

    /** The issue instant of the message. */
    @Nullable private Instant issueInstant;

    /**
     * Gets the ID of the message.
     * 
     * @return ID of the message, may be null
     */
    @Nullable @NotEmpty public String getMessageId() {
        if (messageId == null) {
            messageId = resolveMessageId();
        }
        return messageId;
    }

    /**
     * Sets the ID of the message.
     * 
     * @param newMessageId ID of the message
     */
    public void setMessageId(@Nullable final String newMessageId) {
        messageId = StringSupport.trimOrNull(newMessageId);
    }

    /**
     * Gets the issue instant of the message.
     * 
     * @return issue instant of the message
     */
    @Nullable public Instant getMessageIssueInstant() {
        if (issueInstant == null) {
            issueInstant = resolveIssueInstant();
        }
        return issueInstant;
    }

    /**
     * Sets the issue instant of the message.
     * 
     * @param messageIssueInstant issue instant of the message
     */
    public void setMessageIssueInstant(@Nullable final Instant messageIssueInstant) {
        issueInstant = messageIssueInstant;
    }

    /**
     * Dynamically resolve the message ID from the SAML protocol message held in 
     * {@link MessageContext#getMessage()}.
     * 
     * @return the message ID, or null if it can not be resolved
     */
    @Nullable protected String resolveMessageId() {
        final SAMLObject samlMessage = resolveSAMLMessage();
        //SAML 2 Request
        if (samlMessage instanceof org.opensaml.saml.saml2.core.RequestAbstractType msg) {
            return msg.getID();
        //SAML 2 Response
        } else if (samlMessage instanceof org.opensaml.saml.saml2.core.StatusResponseType msg) {
            return msg.getID();
        //SAML 1 Response
        } else if (samlMessage instanceof org.opensaml.saml.saml1.core.ResponseAbstractType msg) {
            return msg.getID();
        //SAML 1 Request
        } else if (samlMessage instanceof org.opensaml.saml.saml1.core.RequestAbstractType msg) {
            return msg.getID();
        }
        return null;
    }
    
    /**
     * Dynamically resolve the message issue instant from the SAML protocol message held in 
     * {@link MessageContext#getMessage()}.
     * 
     * @return the message issue instant, or null if it can not be resolved
     */
    @Nullable protected Instant resolveIssueInstant() {
        final SAMLObject samlMessage = resolveSAMLMessage();
        //SAML 2 Request
        if (samlMessage instanceof org.opensaml.saml.saml2.core.RequestAbstractType msg) {
            return msg.getIssueInstant();
        //SAML 2 Response
        } else if (samlMessage instanceof org.opensaml.saml.saml2.core.StatusResponseType msg) {
            return msg.getIssueInstant();
        //SAML 1 Response
        } else if (samlMessage instanceof org.opensaml.saml.saml1.core.ResponseAbstractType msg) {
            return msg.getIssueInstant();
        //SAML 1 Request
        } else if (samlMessage instanceof org.opensaml.saml.saml1.core.RequestAbstractType msg) {
            return msg.getIssueInstant();
        }
        
        return null;
    }
    
    /**
     * Resolve the SAML message from the message context.
     * 
     * @return the SAML message, or null if it can not be resolved
     */
    @Nullable protected SAMLObject resolveSAMLMessage() {
        if (getParent() instanceof MessageContext p) {
            if (p.getMessage() instanceof SAMLObject msg) {
                return msg;
            } 
        }
        return null;
    }

}