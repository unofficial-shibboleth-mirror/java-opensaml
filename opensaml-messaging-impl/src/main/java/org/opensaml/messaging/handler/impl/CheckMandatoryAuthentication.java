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

package org.opensaml.messaging.handler.impl;

import java.util.function.Function;

import javax.annotation.Nonnull;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;

/**
 * Message handler that checks that a message context is authenticated.
 */
public final class CheckMandatoryAuthentication extends AbstractMessageHandler {

    /** Strategy used to look up the authentication state associated with the message context. */
    @NonnullAfterInit private Function<MessageContext,Boolean> authenticationLookupStrategy;
    
    /**
     * Set the strategy used to look up the authentication state associated with the message context.
     * 
     * @param strategy lookup strategy
     */
    public void setAuthenticationLookupStrategy(@Nonnull final Function<MessageContext,Boolean> strategy) {
        checkSetterPreconditions();

        authenticationLookupStrategy = Constraint.isNotNull(strategy,
                "Message context authentication lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (authenticationLookupStrategy == null) {
            throw new ComponentInitializationException("Message context authentication lookup strategy cannot be null");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        final Boolean authenticated = authenticationLookupStrategy.apply(messageContext);
        if (authenticated == null) {
            throw new MessageHandlerException("Message context did not contain any authentication state");
        } else if (!authenticated) {
            throw new MessageHandlerException("Message context was not authenticated");
        }
    }
    
}