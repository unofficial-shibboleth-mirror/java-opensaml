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

package org.opensaml.saml.saml2.binding.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.StatusResponseType;

/**
 * Message handler that raises an exception if a SAML message is a response containing the
 * {@link StatusCode#NO_PASSIVE} status code.
 * 
 * @since 5.2.0
 */
public class CheckNoPassiveHandler extends AbstractMessageHandler {

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        if (isNoPassiveResponse(messageContext.getMessage())) {
            throw new MessageHandlerException("Message contained NoPassive error.");
        }
    }


    /**
     * Get whether the message is a SAML response containing an error status.
     * 
     * @param message   message to check
     * 
     * @return  true iff the message is a SAML response containing an error status
     */
    private boolean isNoPassiveResponse(@Nullable final Object message) {
        if (message instanceof StatusResponseType resp) {
            final Status status = resp.getStatus();
            if (status != null) {
                StatusCode code = status.getStatusCode();
                if (code != null && !StatusCode.SUCCESS.equals(code.getValue())) {
                    code = code.getStatusCode();
                    return code != null && StatusCode.NO_PASSIVE.equals(code.getValue());
                }
            }
        }
        
        return false;
    }

}