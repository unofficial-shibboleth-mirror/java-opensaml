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

package org.opensaml.saml.saml1.profile;

import java.time.Instant;

import javax.annotation.Nonnull;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.security.IdentifierGenerationStrategy;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.Conditions;
import org.opensaml.saml.saml1.core.Response;
import org.slf4j.Logger;

/** Helper methods for SAML 1 profile actions. */
public final class SAML1ActionSupport {
    
    /** Logger. */
    @Nonnull private static final Logger LOG = LoggerFactory.getLogger(SAML1ActionSupport.class);

    /** Constructor. */
    private SAML1ActionSupport() {

    }
    
    /**
     * Constructs an {@link Assertion} using the parameters supplied, with its issue instant set to the
     * current time.
     * 
     * @param action the current action
     * @param idGenerator source of assertion ID
     * @param issuer value for assertion
     * 
     * @return the assertion
     */
    @Nonnull public static Assertion buildAssertion(@Nonnull final AbstractProfileAction action,
            @Nonnull final IdentifierGenerationStrategy idGenerator, @Nonnull @NotEmpty final String issuer) {
        
        final SAMLObjectBuilder<Assertion> assertionBuilder = (SAMLObjectBuilder<Assertion>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Assertion>ensureBuilder(
                        Assertion.DEFAULT_ELEMENT_NAME);

        final Assertion assertion = assertionBuilder.buildObject();
        assertion.setID(idGenerator.generateIdentifier());
        assertion.setIssueInstant(Instant.now());
        assertion.setIssuer(issuer);
        assertion.setVersion(SAMLVersion.VERSION_11);
        
        LOG.debug("Profile Action {}: Created Assertion {}", action.getClass().getSimpleName(),
                assertion.getID());

        return assertion;
    }

    /**
     * Constructs and adds a {@link Assertion} to the given {@link Response}. The {@link Assertion} is constructed
     * using the parameters supplied, and its issue instant is set to the issue instant of the given {@link Response}.
     * 
     * @param action the current action
     * @param response the response to which the assertion will be added
     * @param idGenerator source of assertion ID
     * @param issuer value for assertion
     * 
     * @return the assertion that was added to the response
     */
    @Nonnull public static Assertion addAssertionToResponse(@Nonnull final AbstractProfileAction action,
            @Nonnull final Response response, @Nonnull final IdentifierGenerationStrategy idGenerator,
            @Nonnull @NotEmpty final String issuer) {

        final Assertion assertion = buildAssertion(action, idGenerator, issuer);
        assertion.setIssueInstant(response.getIssueInstant());
        
        LOG.debug("Profile Action {}: Added Assertion {} to Response {}",
                new Object[] {action.getClass().getSimpleName(), assertion.getID(), response.getID(),});
        response.getAssertions().add(assertion);

        return assertion;
    }

    /**
     * Creates and adds a {@link Conditions} to a given {@link Assertion}. If the {@link Assertion} already contains an
     * {@link Conditions} this method just returns.
     * 
     * @param action current action
     * @param assertion assertion to which the condition will be added
     * 
     * @return the {@link Conditions} that already existed on, or the one that was added to, the {@link Assertion}
     */
    @Nonnull public static Conditions addConditionsToAssertion(@Nonnull final AbstractProfileAction action,
            @Nonnull final Assertion assertion) {
        Conditions conditions = assertion.getConditions();
        if (conditions == null) {
            final SAMLObjectBuilder<Conditions> conditionsBuilder = (SAMLObjectBuilder<Conditions>)
                    XMLObjectProviderRegistrySupport.getBuilderFactory().<Conditions>ensureBuilder(
                            Conditions.DEFAULT_ELEMENT_NAME);
            conditions = conditionsBuilder.buildObject();
            assertion.setConditions(conditions);
            LOG.debug("Profile Action {}: Assertion {} did not already contain Conditions, added",
                    action.getClass().getSimpleName(), assertion.getID());
        } else {
            LOG.debug("Profile Action {}: Assertion {} already contains Conditions, nothing was done",
                    action.getClass().getSimpleName(), assertion.getID());
        }

        return conditions;
    }
    
}