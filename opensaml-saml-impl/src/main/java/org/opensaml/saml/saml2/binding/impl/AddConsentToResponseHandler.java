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

import java.util.function.Function;

import javax.annotation.Nonnull;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.SAMLConsentContext;
import org.opensaml.saml.saml2.core.StatusResponseType;

import net.shibboleth.shared.logic.Constraint;

/**
 * MessageHandler to set the Consent attribute on a {@link StatusResponseType} message.
 */
public class AddConsentToResponseHandler extends AbstractMessageHandler {
    
    /** Strategy for locating {@link SAMLConsentContext}. */
    @Nonnull private Function<MessageContext,SAMLConsentContext> consentContextStrategy;

    /** Constructor. */
    public AddConsentToResponseHandler() {
        consentContextStrategy = new ChildContextLookup<>(SAMLConsentContext.class);
    }
    
    /**
     * Set the strategy for locating {@link SAMLConsentContext}.
     * 
     * @param strategy  lookup strategy
     */
    public void setConsentContextLookupStrategy(@Nonnull final Function<MessageContext,SAMLConsentContext> strategy) {
        consentContextStrategy = Constraint.isNotNull(strategy, "SAMLConsentContext lookup strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {

        final Object response = messageContext.getMessage();
        if (response == null) {
            throw new MessageHandlerException("Message not found");
        } else if (!(response instanceof StatusResponseType)) {
            throw new MessageHandlerException("Message was not a StatusResponseType");
        }
        
        final SAMLConsentContext consentContext = consentContextStrategy.apply(messageContext);
        if (consentContext == null || consentContext.getConsent() == null) {
            throw new MessageHandlerException("Consent value not found");
        }
        
        ((StatusResponseType) response).setConsent(consentContext.getConsent());
    }
    
}