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

import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ContextDataLookupFunction;
import org.opensaml.profile.context.ProfileRequestContext;

/**
 * A {@link ContextDataLookupFunction} that returns the outbound {@link MessageContext}
 * for a {@link ProfileRequestContext}.
 */
public class OutboundMessageContextLookup implements ContextDataLookupFunction<ProfileRequestContext,MessageContext> {

    /** {@inheritDoc} */
    @Nullable public MessageContext apply(@Nullable final ProfileRequestContext input) {
        if (input != null) {
            return input.getOutboundMessageContext();
        }
        return null;
    }

}