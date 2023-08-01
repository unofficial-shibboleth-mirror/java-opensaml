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

package org.opensaml.soap.client.messaging;

import java.util.function.Function;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.soap.client.SOAPClientContext;

/**
 * Function which resolves and returns the intended SOAP client message destination URI
 * via the {@link SOAPClientContext#getDestinationURI()} of the message context's
 * parent {@link InOutOperationContext}.
 */
public class SOAPClientDestinationURILookup implements Function<MessageContext, String> {

    /** {@inheritDoc} */
    @Nullable public String apply(@Nullable final MessageContext messageContext) {
        if (messageContext != null && messageContext.getParent() instanceof InOutOperationContext inout) {
            final SOAPClientContext soap = inout.getSubcontext(SOAPClientContext.class);
            if (soap != null) {
                return soap.getDestinationURI();
            }
        }
        
        return null;
    }

}