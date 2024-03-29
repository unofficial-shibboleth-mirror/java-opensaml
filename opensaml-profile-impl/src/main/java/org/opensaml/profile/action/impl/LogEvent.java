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

package org.opensaml.profile.action.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.context.EventContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.CurrentOrPreviousEventLookup;
import org.slf4j.Logger;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;


/**
 * A profile action that logs an event if one is found in the profile request context.
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 */
public class LogEvent extends AbstractProfileAction {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(LogEvent.class);

    /** Strategy function for access to {@link EventContext} to check. */
    @Nonnull private Function<ProfileRequestContext,EventContext> eventContextLookupStrategy;

    /** Set of events to ignore for logging purposes. */
    @Nonnull private Set<String> suppressedEvents;
    
    /** Constructor. */
    public LogEvent() {
        eventContextLookupStrategy = new CurrentOrPreviousEventLookup();
        suppressedEvents = CollectionSupport.emptySet();
    }

    /**
     * Set lookup strategy for {@link EventContext} to check.
     * 
     * @param strategy  lookup strategy
     */
    public void setEventContextLookupStrategy(@Nonnull final Function<ProfileRequestContext,EventContext> strategy) {
        checkSetterPreconditions();
        
        eventContextLookupStrategy = Constraint.isNotNull(strategy, "EventContext lookup strategy cannot be null");
    }
    
    /**
     * Set a collection of events to ignore for logging purposes. 
     * 
     * @param events events to ignore
     */
    public void setSuppressedEvents(@Nullable final Collection<String> events) {
        checkSetterPreconditions();
        
        if (events != null) {
            suppressedEvents = new HashSet<>(StringSupport.normalizeStringCollection(events));
        } else {
            suppressedEvents = CollectionSupport.emptySet();
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        final EventContext eventCtx = eventContextLookupStrategy.apply(profileRequestContext);
        final Object event = eventCtx != null ? eventCtx.getEvent() : null;
        if (event != null) {
            final String eventString = event.toString();
            if (!suppressedEvents.contains(eventString)) {
                log.warn("A non-proceed event occurred while processing the request: {}", eventString);
            }
        }
    }
    
}