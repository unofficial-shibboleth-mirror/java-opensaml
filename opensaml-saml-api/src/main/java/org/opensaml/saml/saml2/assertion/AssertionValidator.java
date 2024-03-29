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

package org.opensaml.saml.saml2.assertion;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.core.Assertion;


/**
 * A validator that evaluates an {@link Assertion} generically.
 * 
 * <p>This is a generic extension point for deployer-provided logic outside normal constraints.</p>
 * 
 * @since 4.1.0
 */
@ThreadSafe
public interface AssertionValidator {

    /**
     * Validates the given assertion.
     * 
     * @param assertion assertion being evaluated
     * @param context current Assertion validation context
     * 
     * @return the result of the evaluation
     * 
     * @throws AssertionValidationException if there is a problem processing the validation operation
     */
    @Nonnull ValidationResult validate(@Nonnull final Assertion assertion, 
            @Nonnull final ValidationContext context) throws AssertionValidationException;

}