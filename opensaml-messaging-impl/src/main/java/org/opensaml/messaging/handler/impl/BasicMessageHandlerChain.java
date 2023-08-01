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

package org.opensaml.messaging.handler.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandler;
import org.opensaml.messaging.handler.MessageHandlerChain;
import org.opensaml.messaging.handler.MessageHandlerException;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * A basic implementation of {@link MessageHandlerChain}.
 */
public class BasicMessageHandlerChain extends AbstractMessageHandler 
    implements MessageHandlerChain {

    /** The list of members of the handler chain. */
    @NonnullAfterInit private List<MessageHandler> members;
    
    /** 
     * {@inheritDoc}
     * 
     * <p>
     * The returned list is immutable.  Changes to the list
     * should be accomplished through {@link BasicMessageHandlerChain#setHandlers(List)}.
     * </p>
     * 
     * */
    @NonnullAfterInit @Unmodifiable @NotLive public List<MessageHandler> getHandlers() {
        return members;
    }
    
    /**
     * Set the list of message handler chain members.
     * 
     * <p>
     * The supplied list is copied before being stored.  Later modifications to 
     * the originally supplied list will not be reflected in the handler chain membership.
     * </p>
     * 
     * @param handlers the list of message handler members
     */
    public void setHandlers(@Nullable final List<MessageHandler> handlers) {
        if (handlers != null) {
            members = CollectionSupport.copyToList(handlers);
        } else {
            members = CollectionSupport.emptyList();
        }
    }

    /** {@inheritDoc} */
    public void doInvoke(@Nonnull final MessageContext msgContext) throws MessageHandlerException {
        if (members != null) {
            for (final MessageHandler handler : members) {
                handler.invoke(msgContext);
            }
        }
    }

}