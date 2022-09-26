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

package org.opensaml.saml.saml2.assertion.impl;

import java.time.Duration;
import java.time.Instant;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.SAML20AssertionValidator;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.assertion.StatementValidator;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Statement;
import org.opensaml.saml.saml2.core.SubjectLocality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.shared.primitive.StringSupport;

/**
 * {@link StatementValidator} implementation for {@link AuthnStatement} conditions.
 * 
 * <p>
 * Supports the following {@link ValidationContext} static parameters:
 * </p>
 * <ul>
 * <li>
 * {@link SAML2AssertionValidationParameters#STMT_AUTHN_CHECK_ADDRESS}:
 * Optional.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#STMT_AUTHN_VALID_ADDRESSES}:
 * Required if {@link SAML2AssertionValidationParameters#STMT_AUTHN_CHECK_ADDRESS} is true or omitted,
 * otherwise optional.
 * </li>
 * <li>
 * {@link SAML2AssertionValidationParameters#STMT_AUTHN_MAX_TIME}:
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
 * 
 */
public class AuthnStatementValidator implements StatementValidator {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(AuthnStatementValidator.class);

    /** {@inheritDoc} */
    public QName getServicedStatement() {
        return AuthnStatement.DEFAULT_ELEMENT_NAME;
    }

    /** {@inheritDoc} */
    public ValidationResult validate(@Nonnull final Statement statement, @Nonnull final Assertion assertion,
            @Nonnull final ValidationContext context) throws AssertionValidationException {
        
        if (!(statement instanceof AuthnStatement)) {
            log.warn("Statement '{}' of type '{}' in assertion '{}' was not an '{}' statement.  Unable to process.",
                    new Object[] { statement.getElementQName(), statement.getSchemaType(), assertion.getID(),
                            getServicedStatement(), });
            return ValidationResult.INDETERMINATE;
        }
        
        try {
            final AuthnStatement authnStatement = (AuthnStatement) statement;

            ValidationResult result = validateAuthnInstant(authnStatement, assertion, context);
            if (result != ValidationResult.VALID) {
                return result;
            }

            result = validateSubjectLocality(authnStatement, assertion, context);
            if (result != ValidationResult.VALID) {
                return result;
            }

            result = validateAuthnContext(authnStatement, assertion, context);
            if (result != ValidationResult.VALID) {
                return result;
            }
        } catch (final AssertionValidationException|RuntimeException e) {
            log.warn("There was a problem determining AuthnStatement validity", e);
            return ValidationResult.INDETERMINATE;
        }
        
        return ValidationResult.VALID;
    }

    /**
     * Validate the authnInstant attribute of the {@link AuthnStatement}.
     * 
     * @param authnStatement the current statement being validated
     * @param assertion the current assertion being evaluated
     * @param context the current validation context
     * 
     * @return the validation result
     * 
     * @throws AssertionValidationException if there is a fatal error during evaluation
     */
    protected ValidationResult validateAuthnInstant(@Nonnull final AuthnStatement authnStatement, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context)
                    throws AssertionValidationException {
        
        final Duration maxTimeSinceAuthn = 
                (Duration) context.getStaticParameters().get(SAML2AssertionValidationParameters.STMT_AUTHN_MAX_TIME);
        
        if (maxTimeSinceAuthn == null) {
            log.debug("Max time since authn for evaluation of AuthnStatement/@AuthnInstant not supplied, skipping");
            return ValidationResult.VALID;
        }
        log.debug("Max time since authn for evaluation of AuthnStatement/@AuthnInstant was: {}", maxTimeSinceAuthn);
        
        final Instant authnInstant = authnStatement.getAuthnInstant();
        if (authnInstant == null) {
            log.warn("AuthnStatement/@AuthnInstant is required but was not supplied, failing");
            return ValidationResult.INVALID;
        }
        
        final Duration clockSkew = SAML20AssertionValidator.getClockSkew(context);
        final Instant latestValid = authnInstant.plus(maxTimeSinceAuthn).plus(clockSkew);
        final Instant now = Instant.now();
        
        if (now.isAfter(latestValid)) {
            log.warn("AuthnStatement/@AuthnInstant '{}' eval failed, now is after latest valid (including skew) '{}'", 
                    authnInstant, latestValid); 
           return ValidationResult.INVALID;
        }
        
        return ValidationResult.VALID;
    }

    /**
     * Validate the {@link SubjectLocality}.
     * 
     * @param authnStatement the current statement being validated
     * @param assertion the current assertion being evaluated
     * @param context the current validation context
     * 
     * @return the validation result
     * 
     * @throws AssertionValidationException if there is a fatal error during evaluation
     */
    protected ValidationResult validateSubjectLocality(@Nonnull final AuthnStatement authnStatement, 
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context)
                    throws AssertionValidationException {
        
        final Boolean checkAddress = (Boolean)
                context.getStaticParameters().get(SAML2AssertionValidationParameters.STMT_AUTHN_CHECK_ADDRESS);

        if (checkAddress != null && !checkAddress) {
            log.debug("SubjectLocality/@Address check is disabled, skipping");
            return ValidationResult.VALID;
        }
        
        final SubjectLocality subjectLocality = authnStatement.getSubjectLocality();
        if (subjectLocality == null || subjectLocality.getAddress() == null) {
            log.debug("AuthnStatement contained no SubjectLocality/@Address, skipping");
            return ValidationResult.VALID;
        }

        final String address = StringSupport.trimOrNull(subjectLocality.getAddress());
        
        return AssertionValidationSupport.checkAddress(context, address, 
                SAML2AssertionValidationParameters.STMT_AUTHN_VALID_ADDRESSES,
                assertion,
                "SubjectLocality/@Address");
    }

    /**
     * Validate the {@link AuthnContext}.
     * 
     * <p>
     * The default implementation is a no-op and always valid.  Subclasses may override.
     * </p>
     * 
     * @param authnStatement the current statement being validated
     * @param assertion the current assertion being evaluated
     * @param context the current validation context
     * 
     * @return the validation result
     * 
     * @throws AssertionValidationException if there is a fatal error during evaluation
     */
    protected ValidationResult validateAuthnContext(@Nonnull final AuthnStatement authnStatement,
            @Nonnull final Assertion assertion, @Nonnull final ValidationContext context) 
                    throws AssertionValidationException {
        // Default is no-op.
        return ValidationResult.VALID;
    }

}
