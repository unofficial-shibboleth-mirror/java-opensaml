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

package org.opensaml.saml.saml2.assertion.messaging;

import javax.annotation.Nonnull;

import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.saml.saml2.core.Assertion;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.logic.Constraint;

/**
 * Class which holds data relevant to validating a SAML 2.0 Assertion.
 */
public class AssertionValidationInput {
    
    /** The profile request context input. */
    @Nonnull private InOutOperationContext operationContext;
    
    /** The HTTP request input. */
    @Nonnull private AssertionValidationNetworkInformationSupplier networkInformationSupplier;
    
    /** The Assertion being evaluated. */
    @Nonnull private Assertion assertion;

    /**
     * Constructor.
     * @param samlAssertion the assertion being evaluated
     * @param context the profile request context being evaluated
     * @param networkInformation the supplier of network information
     */
    public AssertionValidationInput(@Nonnull final Assertion samlAssertion,
            @Nonnull final InOutOperationContext context,
            @Nonnull final AssertionValidationNetworkInformationSupplier networkInformation) {
        operationContext = Constraint.isNotNull(context, "InOutOperationContext may not be null");
        networkInformationSupplier = Constraint.isNotNull(networkInformation, "HttpServletRequest may not be null");
        assertion = Constraint.isNotNull(samlAssertion, "Assertion may not be null");
    }

    /**
     * Get the {@link InOutOperationContext} input.
     * 
     * @return the message context input
     */
    @Nonnull public InOutOperationContext getOperationContext() {
        return operationContext;
    }

    /**
     * Get the {@link HttpServletRequest} input.
     * 
     * @return the HTTP servlet request input
     */
    @Nonnull public AssertionValidationNetworkInformationSupplier getNetworkInformationSupplier() {
        return networkInformationSupplier;
    }

    /**
     * Get the {@link Assertion} being evaluated.
     * 
     * @return the Assertion being validated
     */
    @Nonnull public Assertion getAssertion() {
        return assertion;
    }

}
