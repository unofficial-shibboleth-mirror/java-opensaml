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
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.SubjectConfirmation;

/**
 * Validator that confirms the {@link org.opensaml.saml.saml2.core.Subject}
 * of the issuer by evaluating the {@link SubjectConfirmation}s within that
 * subject.
 */
@ThreadSafe
public interface SubjectConfirmationValidator {

    /**
     * Gets the subject confirmation method handled by this validator.
     * 
     * @return subject confirmation method handled by this validator
     */
    @Nullable String getServicedMethod();

    /**
     * Confirms the {@link org.opensaml.saml.saml2.core.Subject}
     * by means of the given {@link SubjectConfirmation}.
     * 
     * @param confirmation the subject confirmation information
     * @param assertion the assertion bearing the subject
     * @param context the current Assertion validation context
     * 
     * @return the validation result
     * 
     * @throws AssertionValidationException if there is a problem processing the validation operation
     */
    @Nonnull ValidationResult validate(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context)
            throws AssertionValidationException;

}