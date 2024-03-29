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
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.action.AbstractConditionalProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml1.core.AudienceRestrictionCondition;
import org.opensaml.saml.saml1.profile.SAML1ActionSupport;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.profile.SAML2ActionSupport;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Action adds an audience restriction condition to every assertion contained in a SAML 1/2
 * response, with the audiences obtained from a lookup function. If the containing Conditions is not present,
 * it will be created.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 */
public class AddAudienceRestrictionToAssertions extends AbstractConditionalProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AddAudienceRestrictionToAssertions.class);

    /**
     * Whether, if an assertion already contains an audience restriction, this action will add its audiences to that
     * restriction or create another one.
     */
    private boolean addingAudiencesToExistingRestriction;

    /** Strategy used to locate the Response to operate on. */
    @Nonnull private Function<ProfileRequestContext,SAMLObject> responseLookupStrategy;

    /** Strategy used to obtain the audiences to add. */
    @NonnullAfterInit private Function<ProfileRequestContext,Collection<String>> audienceRestrictionsLookupStrategy;
    
    /** Response to modify. */
    @NonnullBeforeExec private SAMLObject response;
    
    /** Audiences to add. */
    @NonnullBeforeExec private Collection<String> audiences; 

    /**
     * Constructor.
     */
    public AddAudienceRestrictionToAssertions() {
        addingAudiencesToExistingRestriction = true;

        responseLookupStrategy = new MessageLookup<>(SAMLObject.class).compose(
                new OutboundMessageContextLookup());
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
     * Set whether, if an assertion already contains an audience restriction, this action will add its audiences to
     * that restriction or create another one.
     * 
     * @param addingToExistingRestriction whether this action will add its audiences to that restriction or create
     *            another one
     */
    public void setAddingAudiencesToExistingRestriction(final boolean addingToExistingRestriction) {
        checkSetterPreconditions();
        addingAudiencesToExistingRestriction = addingToExistingRestriction;
    }

    /**
     * Set the strategy used to obtain the audience restrictions to apply.
     * 
     * @param strategy lookup strategy
     */
    public void setAudienceRestrictionsLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,Collection<String>> strategy) {
        checkSetterPreconditions();
        audienceRestrictionsLookupStrategy =
                Constraint.isNotNull(strategy, "Audience restriction lookup strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (audienceRestrictionsLookupStrategy == null) {
            throw new ComponentInitializationException("Audience restriction lookup strategy cannot be null");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        audiences = audienceRestrictionsLookupStrategy.apply(profileRequestContext);
        if (audiences == null || audiences.isEmpty()) {
            log.debug("{} No audiences to add, nothing to do", getLogPrefix());
            return false;
        }
        
        log.debug("{} Attempting to add an AudienceRestrictionCondition to every Assertion in Response",
                getLogPrefix());

        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null) {
            log.debug("{} No SAML Response located in current profile request context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }
        
        if (response instanceof org.opensaml.saml.saml1.core.Response) {
            if (((org.opensaml.saml.saml1.core.Response) response).getAssertions().isEmpty()) {
                log.debug("{} No assertions available, nothing to do", getLogPrefix());
                return false;
            }
        } else if (response instanceof org.opensaml.saml.saml2.core.Response) {
            if (((org.opensaml.saml.saml2.core.Response) response).getAssertions().isEmpty()) {
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
        
        if (response instanceof org.opensaml.saml.saml1.core.Response saml1) {
            for (final var assertion : saml1.getAssertions()) {
                assert assertion != null;
                addAudienceRestriction(profileRequestContext,
                        SAML1ActionSupport.addConditionsToAssertion(this, assertion));
                log.debug("{} Added AudienceRestrictionCondition to Assertion {}", getLogPrefix(), assertion.getID());
            }
        } else if (response instanceof org.opensaml.saml.saml2.core.Response saml2) {
            for (final var assertion : saml2.getAssertions()) {
                assert assertion != null;
                addAudienceRestriction(profileRequestContext,
                        SAML2ActionSupport.addConditionsToAssertion(this, assertion));
                log.debug("{} Added AudienceRestrictionCondition to Assertion {}", getLogPrefix(), assertion.getID());
            }
        }
    }

    /**
     * Add the audiences obtained from a lookup function to the {@link AudienceRestrictionCondition}. If no
     * {@link AudienceRestrictionCondition} exists on the given Conditions one is created and added.
     * 
     * @param profileRequestContext current profile request context
     * @param conditions condition that has, or will receive the created, {@link AudienceRestrictionCondition}
     */
    private void addAudienceRestriction(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final org.opensaml.saml.saml1.core.Conditions conditions) {
        final AudienceRestrictionCondition condition = getAudienceRestrictionCondition(conditions);

        final SAMLObjectBuilder<org.opensaml.saml.saml1.core.Audience> audienceBuilder =
                (SAMLObjectBuilder<org.opensaml.saml.saml1.core.Audience>) 
                XMLObjectProviderRegistrySupport.getBuilderFactory(
                        ).<org.opensaml.saml.saml1.core.Audience>ensureBuilder(
                                org.opensaml.saml.saml1.core.Audience.DEFAULT_ELEMENT_NAME);
        for (final String audienceId : audiences) {
            log.debug("{} Adding {} as an Audience of the AudienceRestrictionCondition", getLogPrefix(), audienceId);
            final var audience = audienceBuilder.buildObject();
            audience.setURI(audienceId);
            condition.getAudiences().add(audience);
        }
    }

    /**
     * Add the audiences obtained from a lookup function to the {@link AudienceRestriction}. If no
     * {@link AudienceRestriction} exists on the given Conditions one is created and added.
     * 
     * @param profileRequestContext current profile request context
     * @param conditions condition that has, or will receive the created, {@link AudienceRestriction}
     */
    private void addAudienceRestriction(@Nonnull final ProfileRequestContext profileRequestContext,
            @Nonnull final org.opensaml.saml.saml2.core.Conditions conditions) {
        final AudienceRestriction condition = getAudienceRestriction(conditions);

        final SAMLObjectBuilder<org.opensaml.saml.saml2.core.Audience> audienceBuilder =
                (SAMLObjectBuilder<org.opensaml.saml.saml2.core.Audience>) 
                XMLObjectProviderRegistrySupport.getBuilderFactory(
                        ).<org.opensaml.saml.saml2.core.Audience>ensureBuilder(
                                org.opensaml.saml.saml2.core.Audience.DEFAULT_ELEMENT_NAME);
        for (final String audienceId : audiences) {
            log.debug("{} Adding {} as an Audience of the AudienceRestriction", getLogPrefix(), audienceId);
            final var audience = audienceBuilder.buildObject();
            audience.setURI(audienceId);
            condition.getAudiences().add(audience);
        }
    }
    
    /**
     * Get the {@link AudienceRestrictionCondition} to which audiences will be added.
     * 
     * @param conditions existing set of conditions
     * 
     * @return the condition to which audiences will be added
     */
    @Nonnull private AudienceRestrictionCondition getAudienceRestrictionCondition(
            @Nonnull final org.opensaml.saml.saml1.core.Conditions conditions) {
        
        final AudienceRestrictionCondition condition;

        if (!addingAudiencesToExistingRestriction || conditions.getAudienceRestrictionConditions().isEmpty()) {
            final SAMLObjectBuilder<AudienceRestrictionCondition> conditionBuilder =
                    (SAMLObjectBuilder<AudienceRestrictionCondition>) XMLObjectProviderRegistrySupport
                            .getBuilderFactory().<AudienceRestrictionCondition>ensureBuilder(
                                    AudienceRestrictionCondition.DEFAULT_ELEMENT_NAME);
            log.debug("{} Adding new AudienceRestrictionCondition", getLogPrefix());
            condition = conditionBuilder.buildObject();
            conditions.getAudienceRestrictionConditions().add(condition);
        } else {
            log.debug("{} Conditions already contained an AudienceRestrictionCondition, using it", getLogPrefix());
            condition = conditions.getAudienceRestrictionConditions().get(0);
        }

        assert condition != null;
        return condition;
    }
    
    /**
     * Get the {@link AudienceRestriction} to which audiences will be added.
     * 
     * @param conditions existing set of conditions
     * 
     * @return the condition to which audiences will be added
     */
    @Nonnull private AudienceRestriction getAudienceRestriction(
            @Nonnull final org.opensaml.saml.saml2.core.Conditions conditions) {
        
        final AudienceRestriction condition;

        if (!addingAudiencesToExistingRestriction || conditions.getAudienceRestrictions().isEmpty()) {
            final SAMLObjectBuilder<AudienceRestriction> conditionBuilder =
                    (SAMLObjectBuilder<AudienceRestriction>) XMLObjectProviderRegistrySupport
                            .getBuilderFactory().<AudienceRestriction>ensureBuilder(
                                    AudienceRestriction.DEFAULT_ELEMENT_NAME);
            log.debug("{} Adding new AudienceRestriction", getLogPrefix());
            condition = conditionBuilder.buildObject();
            conditions.getAudienceRestrictions().add(condition);
        } else {
            log.debug("{} Conditions already contained an AudienceRestriction, using it", getLogPrefix());
            condition = conditions.getAudienceRestrictions().get(0);
        }

        assert condition != null;
        return condition;
    }

}