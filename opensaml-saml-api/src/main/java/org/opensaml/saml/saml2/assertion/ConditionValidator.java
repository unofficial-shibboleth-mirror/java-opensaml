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
import javax.xml.namespace.QName;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Condition;


/** A validator that evaluates a {@link Condition} within an {@link Assertion}. */
@ThreadSafe
public interface ConditionValidator {

    /**
     * Gets the element or schema type QName of the condition handled by this validator.
     * 
     * @return element or schema type QName of the statement handled by this validator
     */
    @Nonnull QName getServicedCondition();

    /**
     * Validates the given condition.
     * 
     * @param condition condition to be evaluated
     * @param assertion assertion bearing the condition
     * @param context current Assertion validation context
     * 
     * @return the result of the condition evaluation
     * 
     * @throws AssertionValidationException if there is a problem processing the validation operation
     */
    @Nonnull ValidationResult validate(@Nonnull final Condition condition, @Nonnull final Assertion assertion, 
            @Nonnull final ValidationContext context) throws AssertionValidationException;
}