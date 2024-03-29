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

package org.opensaml.saml.common.profile.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.action.AbstractConditionalProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLMessageInfoContext;
import org.opensaml.saml.saml1.core.ResponseAbstractType;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Action that adds the <code>InResponseTo</code> attribute to a response message if a SAML message ID is set on
 * the inbound message context.
 * 
 * <p>Supports all of the abstract types in SAML that carry this attribute.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 */
public class AddInResponseToToResponse extends AbstractConditionalProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AddInResponseToToResponse.class);

    /** Strategy used to locate the message to operate on. */
    @Nonnull private Function<ProfileRequestContext,SAMLObject> responseLookupStrategy;
    
    /** Strategy used to locate request ID to correlate. */
    @Nonnull private Function<ProfileRequestContext,String> requestIdLookupStrategy;
    
    /** Message to modify. */
    @NonnullBeforeExec private SAMLObject response;
    
    /** Request ID to populate from. */
    @NonnullBeforeExec private String requestId;
    
    /** Constructor. */
    public AddInResponseToToResponse() {
        responseLookupStrategy = new MessageLookup<>(SAMLObject.class).compose(
                new OutboundMessageContextLookup());
        requestIdLookupStrategy = new DefaultRequestIdLookupStrategy();
    }
    
    /**
     * Set the strategy used to locate the message to operate on.
     * 
     * @param strategy strategy used to locate the message to operate on
     */
    public void setResponseLookupStrategy(@Nonnull final Function<ProfileRequestContext,SAMLObject> strategy) {
        checkSetterPreconditions();
        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to locate the request ID.
     * 
     * @param strategy lookup strategy
     */
    public void setRequestIdLookupStrategy(@Nonnull final Function<ProfileRequestContext,String> strategy) {
        checkSetterPreconditions();
        requestIdLookupStrategy = Constraint.isNotNull(strategy, "Request ID lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext) ) {
            return false;
        }
        
        log.debug("{} Attempting to add InResponseTo to outgoing Response", getLogPrefix());

        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null) {
            log.debug("{} No SAML message located in current profile request context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }

        requestId = requestIdLookupStrategy.apply(profileRequestContext);
        if (requestId == null) {
            log.debug("{} No request ID, nothing to do", getLogPrefix());
            return false;
        }
                
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        if (response instanceof ResponseAbstractType) {
            ((ResponseAbstractType) response).setInResponseTo(requestId);
        } else if (response instanceof StatusResponseType) {
            ((StatusResponseType) response).setInResponseTo(requestId);
        } else {
            log.debug("{} Message type {} is not supported", getLogPrefix(), response.getElementQName());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
        }
    }

    /**
     * Default lookup of request ID from inbound message context, suppressing lookup for bindings
     * known to be supplying artificial IDs.
     */
    public static class DefaultRequestIdLookupStrategy implements Function<ProfileRequestContext,String> {

        /** Class logger. */
        @Nonnull private final Logger log = LoggerFactory.getLogger(AddInResponseToToResponse.class);
        
        /** Set of bindings to ignore request ID for. */
        @Nonnull private Set<String> suppressForBindings;
        
        /** Constructor. */
        public DefaultRequestIdLookupStrategy() {
            suppressForBindings = CollectionSupport.emptySet();
        }
        
        /**
         * Set the collection of bindings to suppress the lookup of a request ID for.
         * 
         * @param bindings collection of bindings
         */
        public void setSuppressForBindings(@Nonnull final Collection<String> bindings) {
            Constraint.isNotNull(bindings, "Bindings collection cannot be null");
            
            suppressForBindings = new HashSet<>();
            for (final String b : bindings) {
                final String trimmed = StringSupport.trimOrNull(b);
                if (trimmed != null) {
                    suppressForBindings.add(trimmed);
                }
            }
        }
        
        /** {@inheritDoc} */
        @Override
        @Nullable public String apply(@Nullable final ProfileRequestContext input) {
            final MessageContext inMsgCtx = input != null ? input.getInboundMessageContext() : null;
            if (inMsgCtx == null) {
                log.debug("No inbound message context available");
                return null;
            }

            if (!suppressForBindings.isEmpty()) {
                final SAMLBindingContext bindingCtx = inMsgCtx.getSubcontext(SAMLBindingContext.class);
                if (bindingCtx != null && bindingCtx.getBindingUri() != null
                        && suppressForBindings.contains(bindingCtx.getBindingUri())) {
                    log.debug("Inbound binding {} is suppressed, ignoring request ID",
                            bindingCtx.getBindingUri());
                    return null;
                }
            }
            
            final SAMLMessageInfoContext infoCtx = inMsgCtx.ensureSubcontext(SAMLMessageInfoContext.class);
            return infoCtx.getMessageId();
        }
    }
    
}