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

package org.opensaml.messaging.logic;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageChannelSecurityContext;
import org.opensaml.messaging.context.MessageContext;

import java.util.function.Predicate;

/**
 * A predicate implementation that indicates whether the message channel does
 * <strong>NOT</strong> support confidentiality end-to-end.
 * 
 * <p>Typically but not exclusively used as a predicate for whether to encrypt something.</p>
 */
public class NoConfidentialityMessageChannelPredicate implements Predicate<MessageContext> {

    /** {@inheritDoc} */
    public boolean test(@Nullable final MessageContext input) {
        return input == null
                || !input.ensureSubcontext(MessageChannelSecurityContext.class).isConfidentialityActive();
    }
    
}