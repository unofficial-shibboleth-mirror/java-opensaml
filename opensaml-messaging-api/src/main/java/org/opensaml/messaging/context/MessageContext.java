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

import net.shibboleth.shared.logic.ConstraintViolationException;

/**
 * A context component which holds the state related to the processing of a single message.
 * 
 * <p>
 * Additional information associated with the message represented by the context may be held by the context
 * as subordinate subcontext instances. Subcontext instances may simply hold state information related to the message, 
 * in which case they may be seen as a type-safe variant of the ubiquitous properties map pattern.  They may 
 * also be more functional or operational in nature, for example providing "views" onto the message 
 * and/or message context data.
 * </p>
 */
public final class MessageContext extends BaseContext {

    /** The message represented. */
    @Nullable private Object msg;

    /**
     * Get the message represented by the message context.
     * 
     * @return the message or null
     */
    @Nullable public Object getMessage() {
        return msg;
    }
    
    /**
     * Get the message represented by the message context, raising a {@link ConstraintViolationException}
     * if null.
     * 
     * @return the message
     * 
     * @since 5.0.0
     */
    @Nonnull public Object ensureMessage() {
        if (msg != null) {
            return msg;
        }
        throw new IllegalStateException("Message was null");
    }
    
    /**
     * Get the message represented by the message context, raising a {@link ConstraintViolationException}
     * or {@link ClassCastException} if the message is absent or of the incorrect type.
     * 
     * @param <T> type of message
     * @param claz class of message type
     * 
     * @return properly typed message
     * 
     * @since 5.0.0
     */
    @SuppressWarnings("null")
    @Nonnull public <T> T ensureMessage(@Nonnull final Class<T> claz) {
        return claz.cast(ensureMessage());
    }

    /**
     * Set the message represented by the message context.
     * 
     * @param message the message
     */
    public void setMessage(@Nullable final Object message) {
        msg = message;
    }

}