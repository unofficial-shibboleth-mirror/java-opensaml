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
import org.opensaml.profile.context.ProxiedRequesterContext;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.RequesterID;
import org.opensaml.saml.saml2.core.Scoping;

import net.shibboleth.shared.logic.Constraint;

/**
 * MessageHandler to extract the proxied chain of requesters from an {@link AuthnRequest} message's
 * {@link Scoping} element.
 * 
 * @since 3.4.0
 */
public class ExtractProxiedRequestersHandler extends AbstractMessageHandler {
    
    /** Strategy for creating {@link ProxiedRequesterContext}. */
    @Nonnull private Function<MessageContext,ProxiedRequesterContext> proxiedContextCreationStrategy;

    /** Constructor. */
    public ExtractProxiedRequestersHandler() {
        proxiedContextCreationStrategy = new ChildContextLookup<>(ProxiedRequesterContext.class, true);
    }
    
    /**
     * Set the strategy for creating {@link ProxiedRequesterContext}.
     * 
     * @param strategy  lookup strategy
     */
    public void setProxiedRequesterContextCreationStrategy(
            @Nonnull final Function<MessageContext,ProxiedRequesterContext> strategy) {
        proxiedContextCreationStrategy = Constraint.isNotNull(strategy,
                "ProxiedRequesterContext creation strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {

        final Object request = messageContext.getMessage();
        if (request == null) {
            throw new MessageHandlerException("Message not found");
        } else if (!(request instanceof AuthnRequest)) {
            throw new MessageHandlerException("Message was not an AuthnRequest");
        }
        
        final Scoping scoping = ((AuthnRequest) request).getScoping();
        if (scoping != null && !scoping.getRequesterIDs().isEmpty()) {
            final ProxiedRequesterContext proxyContext = proxiedContextCreationStrategy.apply(messageContext);
            if (proxyContext == null) {
                throw new MessageHandlerException("Failed to create/locate ProxiedRequesterContext");
            }
            
            for (final RequesterID id : scoping.getRequesterIDs()) {
                if (id.getURI() != null) {
                    proxyContext.getRequesters().add(id.getURI());
                }
            }
        }
    }
    
}