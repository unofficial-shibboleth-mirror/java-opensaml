/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
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

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.collection.LazyMap;
import net.shibboleth.utilities.java.support.primitive.DeprecationSupport;
import net.shibboleth.utilities.java.support.primitive.DeprecationSupport.ObjectType;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Condition;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Statement;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignaturePrevalidator;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/** 
 * A component capable of performing core validation of SAML version 2.0 {@link Assertion} instances.
 * 
 * <p>
 * Supports the following {@link ValidationContext} static parameters:
 * </p>
 *
 * <ul>
 * <li>
 * {@link SAML2AssertionValidationParameters#VALID_ISSUERS}:
 * Optional.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SIGNATURE_REQUIRED}:
 * Optional.
 * If not supplied, defaults to 'true'. If an Assertion is signed, the signature is always evaluated 
 * and the result factored into the overall validation result, regardless of the value of this setting.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SIGNATURE_VALIDATION_CRITERIA_SET}:
 * Optional.
 * If not supplied, a minimal criteria set will be constructed which contains an {@link EntityIdCriterion} 
 * containing the Assertion Issuer entityID, and a {@link UsageCriterion} of {@link UsageType#SIGNING}.
 * If it is supplied, but either of those criteria are absent from the criteria set, they will be added 
 * with the above values.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SIGNATURE_VALIDATION_TRUST_ENGINE}:
 * Optional.
 * If not supplied, defaults to the locally-injected instance.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#SIGNATURE_VALIDATION_PREVALIDATOR}:
 * Optional.
 * If not supplied, defaults to the locally-injected instance.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#CLOCK_SKEW}:
 * Optional.
 * If not present the default clock skew of {@link SAML20AssertionValidator#DEFAULT_CLOCK_SKEW} 
 * will be used.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#LIFETIME}:
 * Optional.
 * If not present the default lifetime of {@link SAML20AssertionValidator#DEFAULT_LIFETIME} 
 * will be used.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#COND_REQUIRED_CONDITIONS}:
 * Optional.
 * </li>
 * </ul>
 * 
 * <p>
 * Supports the following {@link ValidationContext} dynamic parameters:
 * </p>
 *
 * <ul>
 * <li>
 * {@link SAML2AssertionValidationParameters#CONFIRMED_SUBJECT_CONFIRMATION}:
 * Optional.
 * Will be present after validation iff subject confirmation was successfully performed.
 * </li>
 * </ul>
 */
public class SAML20AssertionValidator {

    /** Default clock skew of 5 minutes. */
    @Nonnull public static final Duration DEFAULT_CLOCK_SKEW = Duration.ofMinutes(5);

    /** Default lifetime for IssueInstant of 5 minutes. */
    @Nonnull public static final Duration DEFAULT_LIFETIME = Duration.ofMinutes(5);

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SAML20AssertionValidator.class);

    /** Registered {@link Condition} validators. */
    @Nonnull @NonnullElements private LazyMap<QName, ConditionValidator> conditionValidators;

    /** Registered {@link SubjectConfirmation} validators. */
    @Nonnull @NonnullElements private LazyMap<String, SubjectConfirmationValidator> subjectConfirmationValidators;

    /** Registered {@link Statement} validators. */
    @Nonnull @NonnullElements private LazyMap<QName, StatementValidator> statementValidators;
    
    /** Generic validator. */
    @Nullable private AssertionValidator assertionValidator;
    
    /** Trust engine for signature evaluation. */
    @Nullable private SignatureTrustEngine trustEngine;
    
    /** SAML signature profile validator.*/
    @Nullable private SignaturePrevalidator signaturePrevalidator;

// Checkstyle: ParameterNumber OFF
    /**
     * Constructor.
     * 
     * @param newConditionValidators validators used to validate the {@link Condition}s within the assertion
     * @param newConfirmationValidators validators used to validate {@link SubjectConfirmation} methods within the
     *            assertion
     * @param newStatementValidators validators used to validate {@link Statement}s within the assertion
     * @param newAssertionValidator generic validator extension point
     * @param newTrustEngine the trust used to validate the Assertion signature
     * @param newSignaturePrevalidator the signature pre-validator used to pre-validate the Assertion signature
     * 
     * @since 4.1.0
     */
    public SAML20AssertionValidator(@Nullable final Collection<ConditionValidator> newConditionValidators,
            @Nullable final Collection<SubjectConfirmationValidator> newConfirmationValidators,
            @Nullable final Collection<StatementValidator> newStatementValidators,
            @Nullable final AssertionValidator newAssertionValidator,
            @Nullable final SignatureTrustEngine newTrustEngine,
            @Nullable final SignaturePrevalidator newSignaturePrevalidator) {

        conditionValidators = new LazyMap<>();
        if (newConditionValidators != null) {
            for (final ConditionValidator validator : newConditionValidators) {
                if (validator != null) {
                    conditionValidators.put(validator.getServicedCondition(), validator);
                }
            }
        }

        subjectConfirmationValidators = new LazyMap<>();
        if (newConfirmationValidators != null) {
            for (final SubjectConfirmationValidator validator : newConfirmationValidators) {
                if (validator != null) {
                    subjectConfirmationValidators.put(validator.getServicedMethod(), validator);
                }
            }
        }

        statementValidators = new LazyMap<>();
        if (newStatementValidators != null) {
            for (final StatementValidator validator : newStatementValidators) {
                if (validator != null) {
                    statementValidators.put(validator.getServicedStatement(), validator);
                }
            }
        }
        
        assertionValidator = newAssertionValidator;
        
        trustEngine = newTrustEngine;
        signaturePrevalidator = newSignaturePrevalidator;
    }
// Checkstyle: ParameterNumber ON
    
    /**
     * Constructor.
     * 
     * @param newConditionValidators validators used to validate the {@link Condition}s within the assertion
     * @param newConfirmationValidators validators used to validate {@link SubjectConfirmation} methods within the
     *            assertion
     * @param newStatementValidators validators used to validate {@link Statement}s within the assertion
     * @param newTrustEngine the trust used to validate the Assertion signature
     * @param newSignaturePrevalidator the signature pre-validator used to pre-validate the Assertion signature
     * 
     * @deprecated
     */
    @Deprecated
    public SAML20AssertionValidator(@Nullable final Collection<ConditionValidator> newConditionValidators,
            @Nullable final Collection<SubjectConfirmationValidator> newConfirmationValidators,
            @Nullable final Collection<StatementValidator> newStatementValidators, 
            @Nullable final SignatureTrustEngine newTrustEngine,
            @Nullable final SignaturePrevalidator newSignaturePrevalidator) {
        
        this(newConditionValidators, newConfirmationValidators, newStatementValidators, null,
                newTrustEngine, newSignaturePrevalidator);
        
        DeprecationSupport.warn(ObjectType.METHOD, "SAML20AssertionValidator 5 argument constructor", null,
                "SAML20AssertionValidator 6 argument constructor");
    }

    /**
     * Gets the lifetime duration from the {@link ValidationContext#getStaticParameters()} parameters.
     * If the parameter is not set or is not a non-zero {@link Duration} then the {@link #DEFAULT_LIFETIME} is used.
     * 
     * @param context current validation context
     * 
     * @return the lifetime duration
     */
    @Nonnull public static Duration getLifetime(@Nonnull final ValidationContext context) {
        return getDurationParam(context, SAML2AssertionValidationParameters.LIFETIME, DEFAULT_LIFETIME);
     }
     
    /**
     * Gets the clock skew from the {@link ValidationContext#getStaticParameters()} parameters. If the parameter is not
     * set or is not a non-zero {@link Duration} then the {@link #DEFAULT_CLOCK_SKEW} is used.
     * 
     * @param context current validation context
     * 
     * @return the clock skew
     */
    @Nonnull public static Duration getClockSkew(@Nonnull final ValidationContext context) {
        return getDurationParam(context, SAML2AssertionValidationParameters.CLOCK_SKEW, DEFAULT_CLOCK_SKEW);
     }
     
    /**
     * Gets the clock skew from the {@link ValidationContext#getStaticParameters()} parameters. If the parameter is not
     * set or is not a non-zero {@link Duration} then the {@link #DEFAULT_CLOCK_SKEW} is used.
     * 
     * @param context current validation context
     * @param paramName name of the duration parameter to process
     * @param defaultDuration the default duration to use if not parameter not present in context
     * 
     * @return the clock skew
     */
    private static Duration getDurationParam(@Nonnull final ValidationContext context, @Nonnull final String paramName,
            @Nonnull final Duration defaultDuration) {
        
        Duration duration = defaultDuration;

        if (context.getStaticParameters().containsKey(paramName)) {
            try {
                final Object raw = context.getStaticParameters().get(paramName);
                if (raw instanceof Duration) {
                    duration = (Duration) raw;
                } else if (raw instanceof Long) {
                    duration = Duration.ofMillis((Long) raw);
                    // This is a V4 deprecation, remove in V5.
                    DeprecationSupport.warn(ObjectType.CONFIGURATION, paramName, null, Duration.class.getName());
                }
                
                if (duration.isZero()) {
                    duration = defaultDuration;
                } else if (duration.isNegative()) {
                    duration = duration.abs();
                }
            } catch (final ClassCastException e) {
                duration = defaultDuration;
            }
        }

        return duration;
    }

    /**
     * Validate the supplied SAML 2 {@link Assertion}, using the parameters from the supplied {@link ValidationContext}.
     * 
     * @param assertion the assertion being evaluated
     * @param context the current validation context
     * 
     * @return the validation result
     * 
     * @throws AssertionValidationException if there is a fatal error evaluating the validity of the assertion
     */
    @Nonnull public ValidationResult validate(@Nonnull final Assertion assertion, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        log(assertion, context);
        
        ValidationResult result = validateBasicData(assertion, context);
        if (result != ValidationResult.VALID) {
            return result;
        }

        result = validateSignature(assertion, context);
        if (result != ValidationResult.VALID) {
            return result;
        }

        result = validateConditions(assertion, context);
        if (result != ValidationResult.VALID) {
            return result;
        }

        result = validateSubjectConfirmation(assertion, context);
        if (result != ValidationResult.VALID) {
            return result;
        }

        result = validateStatements(assertion, context);
        if (result != ValidationResult.VALID) {
            return result;
        }
        
        if (assertionValidator != null) {
            return assertionValidator.validate(assertion, context);
        }
        
        return ValidationResult.VALID;
    }

    /**
     * Log the Assertion which is being validated, along with the supplied validation context parameters.
     * 
     * @param assertion the SAML 2 Assertion being validated
     * @param context current validation context
     */
    protected void log(@Nonnull final Assertion assertion, @Nonnull final ValidationContext context) {
        if (log.isTraceEnabled()) {
            try {
                final Element dom = XMLObjectSupport.marshall(assertion);
                log.trace("SAML 2 Assertion being validated:\n{}", SerializeSupport.prettyPrintXML(dom));
            } catch (final MarshallingException e) {
                log.error("Unable to marshall SAML 2 Assertion for logging purposes", e);
            }
            log.trace("SAML 2 Assertion ValidationContext - static parameters: {}", context.getStaticParameters());
            log.trace("SAML 2 Assertion ValidationContext - dynamic parameters: {}", context.getDynamicParameters());
        }
    }
    
    
    /**
     * Validate basic Assertion data, such as version, issuer and issue instant.
     * 
     * @param assertion the assertion being evaluated
     * @param context the current validation context
     * 
     * @return the validation result
     * 
     * @throws AssertionValidationException if there is a fatal error evaluating the validity of the assertion
     */
    @Nonnull protected ValidationResult validateBasicData(@Nonnull final Assertion assertion, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        ValidationResult result = validateVersion(assertion, context);
        if (result != ValidationResult.VALID) {
            return result;
        }

        result = validateIssueInstant(assertion, context);
        if (result != ValidationResult.VALID) {
            return result;
        }

        result = validateIssuer(assertion, context);
        if (result != ValidationResult.VALID) {
            return result;
        }
        
        return ValidationResult.VALID;
    }

    /**
     * Validates that the assertion is a {@link SAMLVersion#VERSION_20} assertion.
     * 
     * @param assertion the assertion to validate
     * @param context current validation context
     * 
     * @return result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem validating the version
     */
    @Nonnull protected ValidationResult validateVersion(@Nonnull final Assertion assertion, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        if (assertion.getVersion() != SAMLVersion.VERSION_20) {
            context.setValidationFailureMessage(String.format(
                    "Assertion '%s' is not a SAML 2.0 version Assertion", assertion.getID()));
            return ValidationResult.INVALID;
        }
        return ValidationResult.VALID;
    }

    /**
     * Validates the Assertion IssueInstant.
     * 
     * @param assertion the assertion to validate
     * @param context current validation context
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException if there is a problem validating the IssueInstant
     */
    protected ValidationResult validateIssueInstant(@Nonnull final Assertion assertion,
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        if (assertion.getIssueInstant() == null) {
            context.setValidationFailureMessage(String.format(
                    "Assertion '%s' did not contain the required IssueInstant", assertion.getID()));
            return ValidationResult.INVALID; 
        }
        final Instant issueInstant = assertion.getIssueInstant();
        
        final Duration clockSkew = getClockSkew(context);
        final Duration lifetime = getLifetime(context);
        
        final Instant now = Instant.now();
        final Instant latestValid = now.plus(clockSkew.abs());
        final Instant expiration = issueInstant.plus(clockSkew.abs()).plus(lifetime.abs());

        // Check assertion wasn't issued in the future
        if (issueInstant.isAfter(latestValid)) {
            log.warn("Assertion was not yet valid: IssueInstant: '{}', latest valid: '{}'", issueInstant, latestValid);
            context.setValidationFailureMessage("Assertion IssueInstant was invalid, issued in future");
            return ValidationResult.INVALID;
            
        }

        // Check assertion has not expired
        if (expiration.isBefore(now)) {
            log.warn("Assertion IssueInstant was expired: IssueInstant: '{}', expiration: '{}', now: '{}'",
                    issueInstant, expiration, now);
            context.setValidationFailureMessage("Assertion IssueInstant was invalid, expired");
            return ValidationResult.INVALID;
        }
        
        
        return ValidationResult.VALID;
    }
    
    /**
     * Validates the Assertion {@link Issuer}.
     * 
     * @param assertion the assertion to validate
     * @param context current validation context
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException if there is a problem validating the Issuer
     */
    protected ValidationResult validateIssuer(@Nonnull final Assertion assertion,
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        String issuer = null;
        if (assertion.getIssuer() != null) {
            issuer = StringSupport.trimOrNull(assertion.getIssuer().getValue());
        }
        if (issuer == null) {
            log.warn("Assertion Issuer was missing and was required");
            context.setValidationFailureMessage("Assertion Issuer was missing and was required");
            return ValidationResult.INVALID;
        }
        
        log.debug("Evaluating Assertion Issuer of : {}", issuer);

        final Set<String> validIssuers;
        try {
            validIssuers = (Set<String>) context.getStaticParameters()
                    .get(SAML2AssertionValidationParameters.VALID_ISSUERS);
        } catch (final ClassCastException e) {
            log.warn("The value of the static validation parameter '{}' was not java.util.Set<String>",
                    SAML2AssertionValidationParameters.VALID_ISSUERS);
            context.setValidationFailureMessage("Unable to determine list of valid issuers");
            return ValidationResult.INDETERMINATE;
        }
        if (validIssuers == null || validIssuers.isEmpty()) {
            log.warn("Set of valid issuers was not available from the validation context, unable to evaluate Issuer");
            return ValidationResult.VALID;
            /* TODO this should really be indeterminate, but would change the behavior for older code. Need to update:
               org.opensaml.saml.saml2.wssecurity.messaging.impl.DefaultSAML20AssertionValidationContextBuilder.
               Also change Javadocs on this class to indicate 'Required' rather than 'Optional'.
            context.setValidationFailureMessage("Unable to determine list of valid issuers");
            return ValidationResult.INDETERMINATE;
            */
        }

        if (validIssuers.contains(issuer)) {
            log.debug("Matched valid issuer: {}", issuer);
            return ValidationResult.VALID;
        }
        
        log.debug("Failed to match Issuer to any supplied valid issuers: {}", validIssuers);

        context.setValidationFailureMessage(String.format(
                "Issuer of Assertion '%s' did not match any valid issuers", assertion.getID()));
        return ValidationResult.INVALID;
    }

    /**
     * Validates the signature of the assertion, if it is signed.
     * 
     * @param token assertion whose signature will be validated
     * @param context current validation context
     * 
     * @return the result of the signature validation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the signature
     */
    @Nonnull protected ValidationResult validateSignature(@Nonnull final Assertion token, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        Boolean signatureRequired = (Boolean) context.getStaticParameters().get(
                SAML2AssertionValidationParameters.SIGNATURE_REQUIRED);
        if (signatureRequired == null) {
            signatureRequired = Boolean.TRUE;
        }
        
        // Validate params and requirements
        if (!token.isSigned()) {
            if (signatureRequired) {
                context.setValidationFailureMessage("Assertion was required to be signed, but was not");
                return ValidationResult.INVALID;
            }
            log.debug("Assertion was not required to be signed, and was not signed.  " 
                    + "Skipping further signature evaluation");
            return ValidationResult.VALID;
        }
        
        return performSignatureValidation(token, context);
    }
    
    /**
     * Handle the actual signature validation.
     * 
     * @param token assertion whose signature will be validated
     * @param context current validation context
     * 
     * @return the validation result
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the signature
     */
    @Nonnull protected ValidationResult performSignatureValidation(@Nonnull final Assertion token, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {

        final SignatureTrustEngine signatureTrustEngine = getSignatureValidationTrustEngine(token, context);
        if (signatureTrustEngine == null) {
            log.warn("Signature validation was necessary, but no signature trust engine was available");
            context.setValidationFailureMessage("Assertion signature could not be evaluated due to internal error");
            return ValidationResult.INDETERMINATE;
        }

        final Signature signature = token.getSignature();
        
        String tokenIssuer = null;
        if (token.getIssuer() != null) {
            tokenIssuer = token.getIssuer().getValue();
        }
        
        log.debug("Attempting signature validation on Assertion '{}' from Issuer '{}'",
                token.getID(), tokenIssuer);
        
        try {
            final SignaturePrevalidator prevalidator = getSignatureValidationPrevalidator(token, context);
            if (prevalidator != null) {
                prevalidator.validate(signature);
            } else {
                log.warn("No SignaturePrevalidator was available, skipping pre-validation");
            }
        } catch (final SignatureException e) {
            final String msg = String.format("Assertion Signature failed pre-validation: %s", e.getMessage());
            log.warn(msg);
            context.setValidationFailureMessage(msg);
            return ValidationResult.INVALID;
        }
        
        final CriteriaSet criteriaSet = getSignatureValidationCriteriaSet(token, context);
        
        try {
            if (signatureTrustEngine.validate(signature, criteriaSet)) {
                log.debug("Validation of signature of Assertion '{}' from Issuer '{}' was successful",
                        token.getID(), tokenIssuer);
                return ValidationResult.VALID;
            }
            final String msg = String.format(
                    "Signature of Assertion '%s' from Issuer '%s' was not valid", token.getID(), tokenIssuer);
            log.warn(msg);
            context.setValidationFailureMessage(msg);
            return ValidationResult.INVALID;
        } catch (final SecurityException e) {
            final String msg = String.format(
                    "A problem was encountered evaluating the signature over Assertion with ID '%s': %s",
                    token.getID(), e.getMessage());
            log.warn(msg);
            context.setValidationFailureMessage(msg);
            return ValidationResult.INDETERMINATE;
        }
        
    }

    /**
     * Get the signature trust engine that will be used in evaluating the Assertion signature.
     *
     * @param token assertion whose signature will be validated
     * @param context current validation context
     * @return the criteria set to use
     */
    @Nonnull protected SignatureTrustEngine getSignatureValidationTrustEngine(@Nonnull final Assertion token,
            @Nonnull final ValidationContext context) {

        final SignatureTrustEngine contextEngine = (SignatureTrustEngine) context.getStaticParameters()
               .get(SAML2AssertionValidationParameters.SIGNATURE_VALIDATION_TRUST_ENGINE);

        if (contextEngine != null) {
            return contextEngine;
        }

        return trustEngine;
    }

    /**
     * Get the signature trust engine that will be used in evaluating the Assertion signature.
     *
     * @param token assertion whose signature will be validated
     * @param context current validation context
     * @return the criteria set to use
     */
    @Nonnull protected SignaturePrevalidator getSignatureValidationPrevalidator(@Nonnull final Assertion token,
            @Nonnull final ValidationContext context) {

        final SignaturePrevalidator contextPrevalidator = (SignaturePrevalidator) context.getStaticParameters()
               .get(SAML2AssertionValidationParameters.SIGNATURE_VALIDATION_PREVALIDATOR);

        if (contextPrevalidator != null) {
            return contextPrevalidator;
        }

       return signaturePrevalidator;
    }

    /**
     * Get the criteria set that will be used in evaluating the Assertion signature via the supplied trust engine.
     * 
     * @param token assertion whose signature will be validated
     * @param context current validation context
     * @return the criteria set to use
     */
    @Nonnull protected CriteriaSet getSignatureValidationCriteriaSet(@Nonnull final Assertion token, 
            @Nonnull final ValidationContext context) {
        
        CriteriaSet criteriaSet = (CriteriaSet) context.getStaticParameters().get(
                SAML2AssertionValidationParameters.SIGNATURE_VALIDATION_CRITERIA_SET);
        if (criteriaSet == null)  {
            criteriaSet = new CriteriaSet();
        }
        
        if (!criteriaSet.contains(EntityIdCriterion.class)) {
            String issuer =  null;
            if (token.getIssuer() != null) {
                issuer = StringSupport.trimOrNull(token.getIssuer().getValue());
            }
            if (issuer != null) {
                criteriaSet.add(new EntityIdCriterion(issuer));
            }
        }
        
        if (!criteriaSet.contains(UsageCriterion.class)) {
            criteriaSet.add(new UsageCriterion(UsageType.SIGNING));
        }
        
        return criteriaSet;
    }

    /**
     * Validates the conditions on the assertion. Condition validators are looked up by the element QName and, if
     * present, the schema type of the condition. If no validator can be found for the Condition the validation process
     * fails.
     * 
     * @param assertion the assertion whose conditions will be validated
     * @param context current validation context
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the conditions
     */
    @Nonnull protected ValidationResult validateConditions(@Nonnull final Assertion assertion, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        final ValidationResult requiredConditionsResult = validateRequiredConditions(assertion, context);
        if (requiredConditionsResult != ValidationResult.VALID) {
            return requiredConditionsResult;
        }
                
        final Conditions conditions = assertion.getConditions();
        if (conditions == null) {
            log.debug("Assertion contained no Conditions element");
            return ValidationResult.VALID;
        }
        
        final ValidationResult timeboundsResult = validateConditionsTimeBounds(assertion, context);
        if (timeboundsResult != ValidationResult.VALID) {
            return timeboundsResult;
        }

        ConditionValidator validator;
        for (final Condition condition : conditions.getConditions()) {
            validator = conditionValidators.get(condition.getElementQName());
            if (validator == null && condition.getSchemaType() != null) {
                validator = conditionValidators.get(condition.getSchemaType());
            }

            if (validator == null) {
                final String msg = String.format(
                        "Unknown Condition '%s' of type '%s' in assertion '%s'", 
                                condition.getElementQName(), condition.getSchemaType(), assertion.getID());
                log.debug(msg);
                context.setValidationFailureMessage(msg);
                return ValidationResult.INDETERMINATE;
            }
            if (validator.validate(condition, assertion, context) != ValidationResult.VALID) {
                String msg = String.format(
                        "Condition '%s' of type '%s' in assertion '%s' was not valid.",
                                condition.getElementQName(), condition.getSchemaType(), assertion.getID());
                if (context.getValidationFailureMessage() != null) {
                    msg = msg + ": " + context.getValidationFailureMessage();
                }
                log.debug(msg);
                context.setValidationFailureMessage(msg);
                return ValidationResult.INVALID;
            }
        }

        return ValidationResult.VALID;
    }
    
    /**
     * Validate that all conditions indicated to be required are present in the assertion.
     * 
     * @param assertion the assertion whose conditions will be evaluated
     * @param context current validation context
     * 
     * @return the result of the validation evaluation
     */
    protected ValidationResult validateRequiredConditions(@Nonnull final Assertion assertion,
            @Nonnull final ValidationContext context) {
        
        @SuppressWarnings("unchecked")
        final Set<QName> requiredConditions = (Set<QName>) context.getStaticParameters()
                .get(SAML2AssertionValidationParameters.COND_REQUIRED_CONDITIONS);
        
        if (requiredConditions == null || requiredConditions.isEmpty()) {
            log.debug("No Conditions were indicated as required");
            return ValidationResult.VALID;
        }
        
        final Conditions conditions = assertion.getConditions();
        if (conditions == null || conditions.getConditions().isEmpty()) {
            log.warn("At least 1 Condition was indicated as required, but Assertion contained no Conditions");
            context.setValidationFailureMessage(
                    "At least 1 Condition was indicated as required, but Assertion contained no Conditions");
            return ValidationResult.INVALID;
        }
        
        for (final QName requiredCondition : requiredConditions) {
            final List<Condition> found = conditions.getConditions(requiredCondition);
            if (found == null || found.isEmpty()) {
                String msg = String.format("Condition '%s' was required, but was not found in assertion '%s'",
                        requiredCondition, assertion.getID());
                if (context.getValidationFailureMessage() != null) {
                    msg = msg + ": " + context.getValidationFailureMessage();
                }
                log.warn(msg);
                context.setValidationFailureMessage(msg);
                return ValidationResult.INVALID;
            }
        }
        
        return ValidationResult.VALID;
    }

    /**
     * Validates the NotBefore and NotOnOrAfter Conditions constraints on the assertion.
     * 
     * @param assertion the assertion whose conditions will be validated
     * @param context current validation context
     * 
     * @return the result of the validation evaluation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity of the conditions
     */
    @Nonnull protected ValidationResult validateConditionsTimeBounds(@Nonnull final Assertion assertion,
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        final Conditions conditions = assertion.getConditions();
        if (conditions == null) {
            return ValidationResult.VALID;
        }
        
        final Instant now = Instant.now();
        final Duration clockSkew = getClockSkew(context);

        final Instant notBefore = conditions.getNotBefore();
        log.debug("Evaluating Conditions NotBefore '{}' against 'skewed now' time '{}'",
                notBefore, now.plus(clockSkew));
        if (notBefore != null && notBefore.isAfter(now.plus(clockSkew))) {
            context.setValidationFailureMessage(String.format(
                    "Assertion '%s' with NotBefore condition of '%s' is not yet valid", assertion.getID(), notBefore));
            return ValidationResult.INVALID;
        }

        final Instant notOnOrAfter = conditions.getNotOnOrAfter();
        log.debug("Evaluating Conditions NotOnOrAfter '{}' against 'skewed now' time '{}'",
                notOnOrAfter, now.minus(clockSkew));
        if (notOnOrAfter != null && notOnOrAfter.isBefore(now.minus(clockSkew))) {
            context.setValidationFailureMessage(String.format(
                    "Assertion '%s' with NotOnOrAfter condition of '%s' is no longer valid", assertion.getID(),
                    notOnOrAfter));
            return ValidationResult.INVALID;
        }
        
        return ValidationResult.VALID;
    }

    /**
     * Validates the subject confirmations of the assertion. Validators are looked up by the subject confirmation
     * method. If any one subject confirmation is met the subject is considered confirmed per the SAML specification.
     * 
     * @param assertion assertion whose subject is being confirmed
     * @param context current validation context
     * 
     * @return the result of the validation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity the subject
     */
    @Nonnull protected ValidationResult validateSubjectConfirmation(@Nonnull final Assertion assertion, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        final Subject assertionSubject = assertion.getSubject();
        if (assertionSubject == null) {
            log.debug("Assertion contains no Subject, skipping subject confirmation");
            return ValidationResult.VALID;
        }

        final List<SubjectConfirmation> confirmations = assertionSubject.getSubjectConfirmations();
        if (confirmations == null || confirmations.isEmpty()) {
            log.debug("Assertion contains no SubjectConfirmations, skipping subject confirmation");
            return ValidationResult.VALID;
        }
        
        log.debug("Assertion contains at least 1 SubjectConfirmation, proceeding with subject confirmation");

        for (final SubjectConfirmation confirmation : confirmations) {
            final SubjectConfirmationValidator validator = subjectConfirmationValidators.get(confirmation.getMethod());
            if (validator != null) {
                try {
                    if (validator.validate(confirmation, assertion, context) == ValidationResult.VALID) {
                        context.getDynamicParameters().put(
                                SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION, confirmation);
                        return ValidationResult.VALID;
                    }
                } catch (final AssertionValidationException e) {
                    log.warn("Error while executing subject confirmation validation " + validator.getClass().getName(),
                            e);
                }
            }
        }

        final String msg = String.format(
                "No subject confirmation methods were met for assertion with ID '%s'", assertion.getID());
        log.debug(msg);
        context.setValidationFailureMessage(msg);
        return ValidationResult.INVALID;
    }

    /**
     * Validates the statements within the assertion. Validators are looked up by the Statement's element QName or, if
     * present, its schema type. Any statement for which a validator can not be found is simply ignored.
     * 
     * @param assertion assertion whose statements are being validated
     * @param context current validation context
     * 
     * @return result of the validation
     * 
     * @throws AssertionValidationException thrown if there is a problem determining the validity the statements
     */
    @Nonnull protected ValidationResult validateStatements(@Nonnull final Assertion assertion, 
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        final List<Statement> statements = assertion.getStatements();
        if (statements == null || statements.isEmpty()) {
            return ValidationResult.VALID;
        }

        ValidationResult result;
        StatementValidator validator;
        for (final Statement statement : statements) {
            validator = statementValidators.get(statement.getElementQName());
            if (validator == null && statement.getSchemaType() != null) {
                validator = statementValidators.get(statement.getSchemaType());
            }

            if (validator != null) {
                result = validator.validate(statement, assertion, context);
                if (result != ValidationResult.VALID) {
                    return result;
                }
            }
        }

        return ValidationResult.VALID;
    }

}