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

package org.opensaml.profile.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;


/**
 * A context component which holds the result of a profile action that produces an "event".
 * 
 * <p>Actions that operate on contexts and want to signal the result in the form of an "event" to
 * a sibling or parent component can create or modify an EventContext. The context contains a
 * generic type that represents an event of meaning to a surrounding processing model, such as
 * a workflow.</p>
 */
public class EventContext extends BaseContext {

    /** The event represented. */
    @Nullable private Object event;

    /**
     * Get the event represented by the context.
     * 
     * @return the event
     */
    @Nullable public Object getEvent() {
        return event;
    }

    /**
     * Set the event represented by the context.
     * 
     * @param newEvent the event
     * 
     * @return this context
     */
    @Nonnull public EventContext setEvent(@Nullable final Object newEvent) {
        event = newEvent;
        
        return this;
    }

}