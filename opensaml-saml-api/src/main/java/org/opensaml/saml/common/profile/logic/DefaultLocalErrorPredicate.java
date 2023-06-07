/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.common.profile.logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.context.EventContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.CurrentOrPreviousEventLookup;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.Endpoint;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

import java.util.function.Predicate;

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.slf4j.Logger;

/**
 * Predicate that decides whether to handle an error by returning a SAML response to a requester
 * or fail locally.
 * 
 * <p>This is principally determined based on whether or not the necessary message context children
 * are present so that a response can be delivered, but is also tunable based on the error event
 * being handled.</p>
 */
public class DefaultLocalErrorPredicate implements Predicate<ProfileRequestContext> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(DefaultLocalErrorPredicate.class);
    
    /** Strategy function for access to {@link SAMLBindingContext} to check. */
    @Nonnull private Function<ProfileRequestContext,SAMLBindingContext> bindingContextLookupStrategy;

    /** Strategy function for access to {@link SAMLEndpointContext} to check. */
    @Nonnull private Function<ProfileRequestContext,SAMLEndpointContext> endpointContextLookupStrategy;

    /** Strategy function for access to {@link EventContext} to check. */
    @Nonnull private Function<ProfileRequestContext,EventContext> eventContextLookupStrategy;
    
    /** Error events to handle locally, even if possible to do so with a response. */
    @Nonnull private Set<String> localEvents;
    
    /** Constructor. */
    public DefaultLocalErrorPredicate() {
        // Default: outbound msg context -> SAMLBindingContext
        bindingContextLookupStrategy =
                new ChildContextLookup<>(SAMLBindingContext.class).compose(
                        new OutboundMessageContextLookup());
        
        // Default: outbound msg context -> SAMLPeerEntityContext -> SAMLEndpointContext
        endpointContextLookupStrategy =
                new ChildContextLookup<>(SAMLEndpointContext.class).compose(
                        new ChildContextLookup<>(SAMLPeerEntityContext.class)).compose(
                                new OutboundMessageContextLookup());
        
        eventContextLookupStrategy = new CurrentOrPreviousEventLookup();
        
        localEvents = CollectionSupport.emptySet();
    }
    
    /**
     * Set lookup strategy for {@link SAMLBindingContext} to check.
     * 
     * @param strategy  lookup strategy
     */
    public void setBindingContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,SAMLBindingContext> strategy) {
        bindingContextLookupStrategy = Constraint.isNotNull(strategy,
                "SAMLBindingContext lookup strategy cannot be null");
    }

    /**
     * Set lookup strategy for {@link SAMLEndpointContext} to check.
     * 
     * @param strategy  lookup strategy
     */
    public void setEndpointContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,SAMLEndpointContext> strategy) {
        endpointContextLookupStrategy = Constraint.isNotNull(strategy,
                "SAMLEndpointContext lookup strategy cannot be null");
    }

    /**
     * Set lookup strategy for {@link EventContext} to check.
     * 
     * @param strategy  lookup strategy
     */
    public void setEventContextLookupStrategy(@Nonnull final Function<ProfileRequestContext,EventContext> strategy) {
        eventContextLookupStrategy = Constraint.isNotNull(strategy, "EventContext lookup strategy cannot be null");
    }
    
    /**
     * Set the events to handle locally.
     * 
     * @param events locally handled events
     */
    public void setLocalEvents(@Nullable final Collection<String> events) {
        localEvents = new HashSet<>(StringSupport.normalizeStringCollection(events));
    }
    
// Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    public boolean test(@Nullable final ProfileRequestContext input) {
        
        if (input == null) {
            return true;
        }
        
        final SAMLBindingContext bindingCtx = bindingContextLookupStrategy.apply(input);
        if (bindingCtx == null || bindingCtx.getBindingUri() == null) {
            log.debug("No SAMLBindingContext or binding URI available, error must be handled locally");
            return true;
        }
        
        final SAMLEndpointContext endpointCtx = endpointContextLookupStrategy.apply(input);
        final Endpoint endpoint = endpointCtx != null ? endpointCtx.getEndpoint() : null;
        if (endpoint == null || (endpoint.getLocation() == null && endpoint.getResponseLocation() == null)) {
            log.debug("No SAMLEndpointContext or endpoint location available, error must be handled locally");
            return true;
        }

        final AuthnRequest authnRequest = new MessageLookup<>(AuthnRequest.class).apply(
                new InboundMessageContextLookup().apply(input));
        if (authnRequest != null) {
            final Boolean isPassive = authnRequest.isPassive();
            if (isPassive != null && isPassive) {
                log.debug("Request was a SAML 2 AuthnRequest with IsPassive set, handling error with response");
                return false;
            }
        }
        
        final EventContext eventCtx = eventContextLookupStrategy.apply(input);
        final Object eObject = eventCtx != null ? eventCtx.getEvent() : null;
        if (eObject == null) {
            log.debug("No event found, assuming error handled with response");
            return false;
        }
        
        final String event = eObject.toString();
        if (localEvents.contains(event)) {
            log.debug("Error event {} will be handled locally", event);
            return true;
        }
        log.debug("Error event {} will be handled with response", event);
        return false;
    }
// Checkstyle: CyclomaticComplexity ON

}