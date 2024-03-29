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

package org.opensaml.messaging.context.navigate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.logic.Constraint;

/**
 * A {@link ContextDataLookupFunction} that returns the message from a {@link MessageContext}.
 * 
 * @param <T> type of message
 */
public class MessageLookup<T> implements ContextDataLookupFunction<MessageContext, T> {

    /** Child context type to look up. */
    @Nonnull private final Class<T> messageType;
    
    /**
     * Constructor.
     * 
     * @param type message type to look up
     */
    public MessageLookup(@Nonnull @ParameterName(name="type") final Class<T> type) {
        messageType = Constraint.isNotNull(type, "Message type cannot be null");
    }
    
    /** {@inheritDoc} */
    @Nullable public T apply(@Nullable final MessageContext input) {
        if (input != null && messageType.isInstance(input.getMessage())) {
            return messageType.cast(input.getMessage());
        }
        return null;
    }

}