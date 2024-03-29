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

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.action.AbstractConditionalProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml1.profile.SAML1ActionSupport;
import org.opensaml.saml.saml2.profile.SAML2ActionSupport;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Action that adds the <code>NotBefore</code> attribute to every assertion in a SAML 1/2
 * response message. If the containing Conditions is not present, it will be created.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 */
public class AddNotOnOrAfterConditionToAssertions extends AbstractConditionalProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AddNotOnOrAfterConditionToAssertions.class);

    /** Strategy used to locate the Response to operate on. */
    @Nonnull private Function<ProfileRequestContext,SAMLObject> responseLookupStrategy;
    
    /** Strategy to obtain assertion lifetime policy. */
    @Nullable private Function<ProfileRequestContext,Duration> assertionLifetimeStrategy;
    
    /** Default lifetime to use to establish timestamp. */
    @Nonnull private Duration defaultAssertionLifetime;
    
    /** Response to modify. */
    @NonnullBeforeExec private SAMLObject response;

    /** Constructor. */
    public AddNotOnOrAfterConditionToAssertions() {
        responseLookupStrategy = new MessageLookup<>(SAMLObject.class).compose(
                new OutboundMessageContextLookup());
        
        defaultAssertionLifetime = Duration.ofMinutes(5);
    }

    /**
     * Set the strategy used to locate the Response to operate on.
     * 
     * @param strategy lookup strategy
     */
    public void setResponseLookupStrategy(@Nonnull final Function<ProfileRequestContext,SAMLObject> strategy) {
        checkSetterPreconditions();
        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }
    
    /**
     * Set strategy function to obtain assertion lifetime.
     * 
     * @param strategy strategy function
     */
    public void setAssertionLifetimeStrategy(@Nullable final Function<ProfileRequestContext,Duration> strategy) {
        checkSetterPreconditions();
        assertionLifetimeStrategy = strategy;
    }
    
    /**
     * Set the default assertion lifetime.
     * 
     * @param lifetime  default lifetime
     */
    public void setDefaultAssertionLifetime(@Nonnull final Duration lifetime) {
        checkSetterPreconditions();
        Constraint.isNotNull(lifetime, "Lifetime cannot be null");
        Constraint.isFalse(lifetime.isNegative(), "Lifetime cannot be negative");
        
        defaultAssertionLifetime = lifetime;
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        log.debug("{} Attempting to add NotOnOrAfter condition to every Assertion in outgoing Response",
                getLogPrefix());

        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null) {
            log.debug("{} No SAML Response located in current profile request context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }
        
        if (response instanceof org.opensaml.saml.saml1.core.Response saml1) {
            if (saml1.getAssertions().isEmpty()) {
                log.debug("{} No assertions available, nothing to do", getLogPrefix());
                return false;
            }
        } else if (response instanceof org.opensaml.saml.saml2.core.Response saml2) {
            if (saml2.getAssertions().isEmpty()) {
                log.debug("{} No assertions available, nothing to do", getLogPrefix());
                return false;
            }
        } else {
            log.debug("{} Message returned by lookup strategy was not a SAML Response", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }
        
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        Duration lifetime = assertionLifetimeStrategy != null
                ? assertionLifetimeStrategy.apply(profileRequestContext) : null;
        if (lifetime == null) {
            log.debug("{} No assertion lifetime supplied, using default", getLogPrefix());
            lifetime = defaultAssertionLifetime;
        }
        
        if (response instanceof org.opensaml.saml.saml1.core.Response saml1) {
            for (final var assertion : saml1.getAssertions()) {
                Instant issueInstant = assertion.getIssueInstant();
                if (issueInstant == null) {
                    issueInstant = Instant.now();
                }
                final Instant expiration = issueInstant.plus(lifetime);
                log.debug("{} Added NotOnOrAfter condition, indicating an expiration of {}, to Assertion {}",
                        new Object[] {getLogPrefix(), expiration, assertion.getID()});
                SAML1ActionSupport.addConditionsToAssertion(this, assertion).setNotOnOrAfter(expiration);
            }
        } else if (response instanceof org.opensaml.saml.saml2.core.Response saml2) {
            for (final var assertion : saml2.getAssertions()) {
                Instant issueInstant = assertion.getIssueInstant();
                if (issueInstant == null) {
                    issueInstant = Instant.now();
                }
                final Instant expiration = issueInstant.plus(lifetime);
                log.debug("{} Added NotOnOrAfter condition, indicating an expiration of {}, to Assertion {}",
                        new Object[] {getLogPrefix(), expiration, assertion.getID()});
                SAML2ActionSupport.addConditionsToAssertion(this, assertion).setNotOnOrAfter(expiration);
            }
        }
    }

}