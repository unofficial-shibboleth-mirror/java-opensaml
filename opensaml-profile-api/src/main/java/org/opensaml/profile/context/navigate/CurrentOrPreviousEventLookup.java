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

package org.opensaml.profile.context.navigate;

import java.util.function.Function;

import javax.annotation.Nullable;

import org.opensaml.profile.context.EventContext;
import org.opensaml.profile.context.PreviousEventContext;
import org.opensaml.profile.context.ProfileRequestContext;

/** Access either current or previous event from context tree. */
public class CurrentOrPreviousEventLookup implements Function<ProfileRequestContext,EventContext> {

    /** {@inheritDoc} */
    @Nullable public EventContext apply(@Nullable final ProfileRequestContext input) {
        if (input != null) {
            final EventContext eventCtx = input.getSubcontext(EventContext.class);
            if (eventCtx != null && eventCtx.getEvent() != null) {
                return eventCtx;
            }
            return input.getSubcontext(PreviousEventContext.class);
        }
        return null;
    }
    
}