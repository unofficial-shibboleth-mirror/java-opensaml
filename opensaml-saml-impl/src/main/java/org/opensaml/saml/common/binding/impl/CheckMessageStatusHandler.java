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

package org.opensaml.saml.common.binding.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml2.core.StatusResponseType;

/**
 * Message handler that raises an exception if a SAML message is a response containing an
 * error status.
 * 
 * @since 5.2.0
 */
public class CheckMessageStatusHandler extends AbstractMessageHandler {

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        if (isErrorResponse(messageContext.getMessage())) {
            throw new MessageHandlerException("Message was an error response.");
        }
    }


    /**
     * Get whether the message is a SAML response containing an error status.
     * 
     * @param message   message to check
     * 
     * @return  true iff the message is a SAML response containing an error status
     */
    private boolean isErrorResponse(@Nullable final Object message) {
        if (message != null) {
            if (message instanceof Response resp) {
                final org.opensaml.saml.saml1.core.Status status = resp.getStatus();
                if (status != null) {
                    final org.opensaml.saml.saml1.core.StatusCode s1 = status.getStatusCode();
                    return s1 != null && s1.getValue() != null
                            && !org.opensaml.saml.saml1.core.StatusCode.SUCCESS.equals(s1.getValue());
                }
            } else if (message instanceof StatusResponseType resp) {
                final org.opensaml.saml.saml2.core.Status status = resp.getStatus();
                if (status != null) {
                    final org.opensaml.saml.saml2.core.StatusCode s2 = status.getStatusCode();
                    return s2 != null && s2.getValue() != null
                            && !org.opensaml.saml.saml2.core.StatusCode.SUCCESS.equals(s2.getValue());
                }
            }
        }
        
        return false;
    }

}