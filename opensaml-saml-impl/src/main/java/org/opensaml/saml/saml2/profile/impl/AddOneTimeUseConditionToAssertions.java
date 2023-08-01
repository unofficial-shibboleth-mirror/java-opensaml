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

package org.opensaml.saml.saml2.profile.impl;

import java.util.function.Function;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.action.AbstractConditionalProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.OneTimeUse;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.profile.SAML2ActionSupport;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Action to add a {@link OneTimeUse} condition to every {@link Assertion} in a {@link Response} message.
 * If the containing {@link Conditions} is not present, it will be created.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 */
public class AddOneTimeUseConditionToAssertions extends AbstractConditionalProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AddOneTimeUseConditionToAssertions.class);

    /** Strategy used to locate the {@link Response} to operate on. */
    @Nonnull private Function<ProfileRequestContext,Response> responseLookupStrategy;

    /** Response to modify. */
    @NonnullBeforeExec private Response response;

    /** Constructor. */
    public AddOneTimeUseConditionToAssertions() {
        responseLookupStrategy = new MessageLookup<>(Response.class).compose(new OutboundMessageContextLookup());
    }
    
    /**
     * Set the strategy used to locate the {@link Response} to operate on.
     * 
     * @param strategy strategy used to locate the {@link Response} to operate on
     */
    public void setResponseLookupStrategy(@Nonnull final Function<ProfileRequestContext,Response> strategy) {
        checkSetterPreconditions();

        responseLookupStrategy = Constraint.isNotNull(strategy, "Response lookup strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        log.debug("{} Attempting to add OneTimeUse condition to every Assertion in Response", getLogPrefix());

        response = responseLookupStrategy.apply(profileRequestContext);
        if (response == null) {
            log.debug("{} No SAML response located in current profile request context", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        } else if (response.getAssertions().isEmpty()) {
            log.debug("{} No assertions in response message, nothing to do", getLogPrefix());
            return false;
        }
        
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        final SAMLObjectBuilder<OneTimeUse> conditionBuilder = (SAMLObjectBuilder<OneTimeUse>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<OneTimeUse>ensureBuilder(
                        OneTimeUse.DEFAULT_ELEMENT_NAME);

        for (final Assertion assertion : response.getAssertions()) {
            assert assertion != null;
            final Conditions conditions = SAML2ActionSupport.addConditionsToAssertion(this, assertion);
            if (conditions.getOneTimeUse() == null) {
                conditions.getConditions().add(conditionBuilder.buildObject());
                log.debug("{} Added OneTimeUse condition to Assertion {}", getLogPrefix(), assertion.getID());
            } else {
                log.debug("{} Assertion {} already contained OneTimeUse condition, another was not added",
                        getLogPrefix(), assertion.getID());
            }
        }
    }

}