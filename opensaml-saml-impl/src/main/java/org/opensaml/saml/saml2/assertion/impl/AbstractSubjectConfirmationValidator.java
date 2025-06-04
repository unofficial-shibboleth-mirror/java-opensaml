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

package org.opensaml.saml.saml2.assertion.impl;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.SAML20AssertionValidator;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.assertion.SubjectConfirmationValidator;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.ObjectSupport;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * A base class for {@link SubjectConfirmationValidator} implementations. 
 * 
 * <p>
 * This class takes care of processing the <code>NotBefore</code>, <code>NotOnOrAfter</code>, 
 * <code>Recipient</code>, and <code>Address</code> checks.
 * </p>
 * 
 * <p>
 * Supports the following {@link ValidationContext} static parameters:
 * </p>
 * <ul>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_ADDRESS_REQUIRED}:
 * Optional.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_CHECK_ADDRESS}:
 * Optional.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_VALID_ADDRESSES}:
 * Required if {@link SAML2AssertionValidationParameters#SC_CHECK_ADDRESS} is true or omitted,
 * otherwise optional.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_RECIPIENT_REQUIRED}:
 * Optional.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_VALID_RECIPIENTS}:
 * Required.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_IN_RESPONSE_TO_REQUIRED}:
 * Optional.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_VALID_IN_RESPONSE_TO}:
 * Required.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_NOT_BEFORE_REQUIRED}:
 * Optional.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SC_NOT_ON_OR_AFTER_REQUIRED}:
 * Optional.
 * </li>
 * </ul>
 * 
 * <p>
 * Supports the following {@link ValidationContext} dynamic parameters:
 * </p>
 * <ul>
 *   <li>None.</li>
 * </ul>
 */
@ThreadSafe
public abstract class AbstractSubjectConfirmationValidator implements SubjectConfirmationValidator {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(AbstractSubjectConfirmationValidator.class);

    /** {@inheritDoc} */
    // Checkstyle: CyclomaticComplexity OFF
    @Nonnull public ValidationResult validate(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context)
            throws AssertionValidationException {
        
        final boolean addressRequired = isAddressRequired(context);
        final boolean inResponseToIgnored = isInResponseToIgnored(context);
        final boolean inResponseToRequired = isInResponseToRequired(context);
        final boolean recipientRequired = isRecipientRequired(context);
        final boolean notOnOrAfterRequired = isNotOnOrAfterRequired(context);
        final boolean notBeforeRequired = isNotBeforeRequired(context);

        final SubjectConfirmationData confirmationData = confirmation.getSubjectConfirmationData();
        if (confirmationData != null) {
            ValidationResult result = validateNotBefore(confirmationData, assertion, context, notBeforeRequired);
            if (result != ValidationResult.VALID) {
                return result;
            }

            result = validateNotOnOrAfter(confirmationData, assertion, context, notOnOrAfterRequired);
            if (result != ValidationResult.VALID) {
                return result;
            }

            result = validateRecipient(confirmationData, assertion, context, recipientRequired);
            if (result != ValidationResult.VALID) {
                return result;
            }

            result = validateAddress(confirmationData, assertion, context, addressRequired);
            if (result != ValidationResult.VALID) {
                return result;
            }

            if (!inResponseToIgnored) {
                result = validateInResponseTo(confirmationData, assertion, context, inResponseToRequired);
                if (result != ValidationResult.VALID) {
                    return result;
                }
            }
        } else {
            if ((!inResponseToIgnored && inResponseToRequired) || recipientRequired || notOnOrAfterRequired
                    || notBeforeRequired || addressRequired) {
                context.getValidationFailureMessages().add(
                        "SubjectConfirmationData was null and one or more data elements were required");
                return ValidationResult.INVALID;
            }
        }

        return doValidate(confirmation, assertion, context);
    }
    // Checkstyle: CyclomaticComplexity ON

    /**
     * Determine whether Address is required.
     * 
     * @param context current validation context
     * 
     * @return true if required, false if not
     */
    protected boolean isAddressRequired(final ValidationContext context) {
        final Boolean flag = ObjectSupport.firstNonNull(
                (Boolean) context.getStaticParameters().get(
                        SAML2AssertionValidationParameters.SC_ADDRESS_REQUIRED),
                Boolean.FALSE);
        return flag != null ? flag : false;
    }

    /**
     * Determine whether Recipient is required.
     * 
     * @param context current validation context
     * 
     * @return true if required, false if not
     */
    protected boolean isRecipientRequired(final ValidationContext context) {
        final Boolean flag = ObjectSupport.firstNonNull(
                (Boolean) context.getStaticParameters().get(
                        SAML2AssertionValidationParameters.SC_RECIPIENT_REQUIRED),
                Boolean.FALSE);
        return flag != null ? flag : false;
    }

    /**
     * Determine whether NotBefore is required.
     * 
     * @param context current validation context
     * 
     * @return true if required, false if not
     */
    protected boolean isNotBeforeRequired(final ValidationContext context) {
        final Boolean flag = ObjectSupport.firstNonNull(
                (Boolean) context.getStaticParameters().get(
                        SAML2AssertionValidationParameters.SC_NOT_BEFORE_REQUIRED),
                Boolean.FALSE);
        return flag != null ? flag : false;
    }

    /**
     * Determine whether NotOnOrAfter is required.
     * 
     * @param context current validation context
     * 
     * @return true if required, false if not
     */
    protected boolean isNotOnOrAfterRequired(final ValidationContext context) {
        final Boolean flag = ObjectSupport.firstNonNull(
                (Boolean) context.getStaticParameters().get(
                        SAML2AssertionValidationParameters.SC_NOT_ON_OR_AFTER_REQUIRED),
                Boolean.FALSE);
        return flag != null ? flag : false;
    }

    /**
     * Determine whether InResponseTo is ignored.
     * 
     * @param context current validation context
     * 
     * @return true if ignored, false if not
     * 
     * @since 5.2.0
     */
    protected boolean isInResponseToIgnored(final ValidationContext context) {
        final Boolean flag = ObjectSupport.firstNonNull(
                (Boolean) context.getStaticParameters().get(
                        SAML2AssertionValidationParameters.SC_IN_RESPONSE_TO_IGNORED),
                Boolean.FALSE);
        return flag != null ? flag : false;
    }
    
    /**
     * Determine whether InResponseTo is required.
     * 
     * @param context current validation context
     * 
     * @return true if required, false if not
     */
    protected boolean isInResponseToRequired(final ValidationContext context) {
        final Boolean flag = ObjectSupport.firstNonNull(
                (Boolean) context.getStaticParameters().get(
                        SAML2AssertionValidationParameters.SC_IN_RESPONSE_TO_REQUIRED),
                Boolean.FALSE);
        return flag != null ? flag : false;
    }

    /**
     * Validates the <code>InResponseTo</code> value of the {@link SubjectConfirmationData}, if any is present.
     * 
     * @param confirmationData confirmation data being validated
     * @param assertion assertion bearing the confirmation method
     * @param context current validation context
     * @param required whether the InResponseTo value is required
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the NotBefore
     */
    @Nonnull protected ValidationResult validateInResponseTo(@Nonnull final SubjectConfirmationData confirmationData,
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context, final boolean required)
                    throws AssertionValidationException {
        
        final String inResponseTo = 
                StringSupport.trimOrNull(confirmationData.getInResponseTo());
        log.debug("Evaluating SubjectConfirmationData@InResponseTo of: {}", inResponseTo);

        final String validInResponseTo;
        try {
            validInResponseTo = (String) context.getStaticParameters().get(
                    SAML2AssertionValidationParameters.SC_VALID_IN_RESPONSE_TO);
        } catch (final ClassCastException e) {
            context.getValidationFailureMessages().add(
                    "Unable to determine valid subject confirmation InResponseTo");
            return ValidationResult.INDETERMINATE;
        }

        if (inResponseTo == null && required) {
            context.getValidationFailureMessages().add(
                    "SubjectConfirmationData/@InResponseTo was missing and was required");
            return ValidationResult.INVALID;
        }

        if (Objects.equals(inResponseTo, validInResponseTo)) {
            log.debug("Matched valid InResponseTo: {}", inResponseTo);
            return ValidationResult.VALID;
        }
        
        context.getValidationFailureMessages().add(String.format(
                "SubjectConfirmationData/@InResponseTo for assertion '%s' did not match the valid value: %s",
                assertion.getID(), validInResponseTo));
        return ValidationResult.INVALID;
    }

    /**
     * Validates the <code>NotBefore</code> condition of the {@link SubjectConfirmationData}, if any is present.
     * 
     * @param confirmationData confirmation data being validated
     * @param assertion assertion bearing the confirmation method
     * @param context current validation context
     * @param required whether the NotBefore value is required
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the NotBefore
     */
    @Nonnull protected ValidationResult validateNotBefore(@Nonnull final SubjectConfirmationData confirmationData, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context,
            final boolean required) throws AssertionValidationException {
        
        final Instant notBefore = confirmationData.getNotBefore();
        if (notBefore == null) {
            if (required) {
                context.getValidationFailureMessages().add(
                        "SubjectConfirmationData/@NotBefore was missing and was required");
                return ValidationResult.INVALID;
            }
            return ValidationResult.VALID;
        }
        
        final Instant skewedNow = Instant.now().plus(SAML20AssertionValidator.getClockSkew(context));
        
        log.debug("Evaluating SubjectConfirmationData NotBefore '{}' against 'skewed now' time '{}'",
                notBefore, skewedNow);
        if (notBefore != null && notBefore.isAfter(skewedNow)) {
            context.getValidationFailureMessages().add(String.format(
                    "SubjectConfirmationData in assertion '%s', with NotBefore condition of '%s' is not yet valid",
                    assertion.getID(), notBefore));
            return ValidationResult.INVALID;
        }

        return ValidationResult.VALID;
    }

    /**
     * Validates the <code>NotOnOrAfter</code> condition of the {@link SubjectConfirmationData}, if any is present.
     * 
     * @param confirmationData confirmation data being validated
     * @param assertion assertion bearing the confirmation method
     * @param context current validation context
     * @param required whether the NotOnOrAfter value is required
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the NotOnOrAFter
     */
    @Nonnull protected ValidationResult validateNotOnOrAfter(@Nonnull final SubjectConfirmationData confirmationData, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context, final boolean required) 
                    throws AssertionValidationException {
        
        final Instant notOnOrAfter = confirmationData.getNotOnOrAfter();
        if (notOnOrAfter == null) {
            if (required) {
                context.getValidationFailureMessages().add(
                        "SubjectConfirmationData/@NotOnOrAfter was missing and was required");
                return ValidationResult.INVALID;
            }
            return ValidationResult.VALID;
        }
        
        final Instant skewedNow = Instant.now().minus(SAML20AssertionValidator.getClockSkew(context));
        
        log.debug("Evaluating SubjectConfirmationData NotOnOrAfter '{}' against 'skewed now' time '{}'",
                notOnOrAfter, skewedNow);
        if (notOnOrAfter != null && notOnOrAfter.isBefore(skewedNow)) {
            context.getValidationFailureMessages().add(String.format(
                    "SubjectConfirmationData, in assertion '%s', with NotOnOrAfter condition of '%s' is no longer valid",
                    assertion.getID(), notOnOrAfter));
            return ValidationResult.INVALID;
        }

        return ValidationResult.VALID;
    }

    /**
     * Validates the <code>Recipient</code> condition of the {@link SubjectConfirmationData}, if any is present.
     * 
     * @param confirmationData confirmation data being validated
     * @param assertion assertion bearing the confirmation method
     * @param context current validation context
     * @param required whether the Recipient value is required
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the recipient
     */
    @Nonnull protected ValidationResult validateRecipient(@Nonnull final SubjectConfirmationData confirmationData, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context, final boolean required)
                    throws AssertionValidationException {
        
        final String recipient = 
                StringSupport.trimOrNull(confirmationData.getRecipient());
        if (recipient == null) {
            if (required) {
                context.getValidationFailureMessages().add(
                        "SubjectConfirmationData/@Recipient was missing and was required");
                return ValidationResult.INVALID;
            }
            return ValidationResult.VALID;
        }
        
        log.debug("Evaluating SubjectConfirmationData@Recipient of: {}", recipient);

        final Set<String> validRecipients;
        try {
            validRecipients = (Set<String>) context.getStaticParameters().get(
                    SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS);
        } catch (final ClassCastException e) {
            context.getValidationFailureMessages().add(
                    "Unable to determine list of valid subject confirmation recipient endpoints");
            return ValidationResult.INDETERMINATE;
        }
        if (validRecipients == null || validRecipients.isEmpty()) {
            context.getValidationFailureMessages().add(
                    "Set of valid recipient URI's was not available from the validation context, " 
                            + "unable to evaluate SubjectConfirmationData@Recipient");
            return ValidationResult.INDETERMINATE;
        }

        if (validRecipients.contains(recipient)) {
            log.debug("Matched valid recipient: {}", recipient);
            return ValidationResult.VALID;
        }

        context.getValidationFailureMessages().add(String.format(
                "SubjectConfirmationData/@Recipient for assertion '%s' did not match any valid recipients: %s",
                assertion.getID(), validRecipients));
        return ValidationResult.INVALID;
    }

    /**
     * Validates the <code>Address</code> condition of the {@link SubjectConfirmationData}, if any is present.
     * 
     * @param confirmationData confirmation data being validated
     * @param assertion assertion bearing the confirmation method
     * @param context current validation context
     * @param required whether the Address value is required
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the address
     */
    @Nonnull protected ValidationResult validateAddress(@Nonnull final SubjectConfirmationData confirmationData, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context, final boolean required) 
                    throws AssertionValidationException {

        final Boolean checkAddress =
                (Boolean) context.getStaticParameters().get(SAML2AssertionValidationParameters.SC_CHECK_ADDRESS);

        if (checkAddress != null && !checkAddress) {
            log.debug("SubjectConfirmationData/@Address check is disabled, skipping");
            return ValidationResult.VALID;
        }

        final String address = StringSupport.trimOrNull(confirmationData.getAddress());
        if (address == null) {
            if (required) {
                context.getValidationFailureMessages().add(
                        "SubjectConfirmationData/@Address was missing and was required");
                return ValidationResult.INVALID;
            }
            return ValidationResult.VALID;
        }
        
        return AssertionValidationSupport.checkAddress(context, address, 
                SAML2AssertionValidationParameters.SC_VALID_ADDRESSES,
                assertion,
                "SubjectConfirmationData/@Address");
    }

    /**
     * Performs any further validation required for the specific confirmation method implementation.
     * 
     * @param confirmation confirmation method being validated
     * @param assertion assertion bearing the confirmation method
     * @param context current validation context
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if further validation finds the confirmation method to be invalid
     */
    @Nonnull protected abstract ValidationResult doValidate(@Nonnull final SubjectConfirmation confirmation, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context) 
                    throws AssertionValidationException;
    
}