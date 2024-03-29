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

package org.opensaml.messaging.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
* An operation context which represents concretely a message exchange pattern involving an 
* inbound message and an outbound message. This is the typical request-response
* pattern seen in messaging environments, and might be either server-side or client-side.
*/
public class InOutOperationContext extends BaseContext {

    /** The inbound message context. */
    @Nullable private MessageContext inboundContext;

    /** The outbound message context. */
    @Nullable private MessageContext outboundContext;

    /** Constructor. Sets ID to a generated UUID and creation time to now. */
    protected InOutOperationContext() {
    }

    /**
     * Constructor.
     * 
     * @param inbound the inbound message context
     * @param outbound the outbound message context
     */
    public InOutOperationContext(@Nullable final MessageContext inbound, @Nullable final MessageContext outbound) {

        setInboundMessageContext(inbound);
        setOutboundMessageContext(outbound);

    }

    /**
     * The inbound message context instance.
     * 
     * @return the inbound message context
     */
    @Nullable public MessageContext getInboundMessageContext() {
        return inboundContext;
    }
    
    /**
     * Gets the inbound message context, creating an empty one if it does not already exist.
     * 
     * @return an existing, or new, inbound message context
     * 
     * @since 5.0.0
     */
    @Nonnull public MessageContext ensureInboundMessageContext() {
        if (inboundContext != null) {
            return inboundContext;
        }

        setInboundMessageContext(new MessageContext());
        return ensureInboundMessageContext();
    }
    
    /**
     * Sets the inbound message context.
     * 
     * @param context inbound message context, may be null
     */
    public void setInboundMessageContext(@Nullable final MessageContext context) {
        // Unlink the old context from this parent
        if (inboundContext != null) {
            inboundContext.setParent(null);
        }
        
        inboundContext = context;
        
        // Link the new context to this parent
        if (inboundContext != null) {
            inboundContext.setParent(this);
        }
    }

    /**
     * The outbound message context instance.
     * 
     * @return the outbound message context
     */
    @Nullable public MessageContext getOutboundMessageContext() {
        return outboundContext;
    }

    /**
     * Gets the outbound message context, creating an empty one if it does not already exist.
     * 
     * @return an existing, or new, outbound message context
     * 
     * @since 5.0.0
     */
    @Nonnull public MessageContext ensureOutboundMessageContext() {
        if (outboundContext != null) {
            return outboundContext;
        }

        setOutboundMessageContext(new MessageContext());
        return ensureOutboundMessageContext();
    }
    
    /**
     * Sets the outbound message context.
     * 
     * @param context outbound message context, may be null
     */
    public void setOutboundMessageContext(@Nullable final MessageContext context) {
        // Unlink the old context from this parent
        if (outboundContext != null) {
            outboundContext.setParent(null);
        }
        
        outboundContext = context;
        
        // Link the new context to this parent
        if (outboundContext != null) {
            outboundContext.setParent(this);
        }
    }

}