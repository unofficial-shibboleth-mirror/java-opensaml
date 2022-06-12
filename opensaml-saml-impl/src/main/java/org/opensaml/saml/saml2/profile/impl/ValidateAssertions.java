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

package org.opensaml.saml.saml2.profile.impl;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationProcessingData;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.saml2.assertion.SAML20AssertionValidator;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.collection.Pair;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.Constraint;

/**
 * A profile action which resolves SAML 2.0 Assertions from the profile request context
 * and validates them using a resolved or configured instance of {@link SAML20AssertionValidator}.
 * 
 * <p>
 * The {@link ValidationResult} along with the {@link ValidationContext} used are stored in the assertion's
 * {@link XMLObject#getObjectMetadata()} as instance of {@link ValidationProcessingData}.
 * 
 * </p>
 */
public class ValidateAssertions extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ValidateAssertions.class);
    
    /** The HttpServletRequest being processed. */
    @NonnullAfterInit private HttpServletRequest httpServletRequest;
    
    /** Flag which indicates whether a failure of Assertion validation should be considered fatal. */
    private boolean invalidFatal;
    
    /** The SAML 2.0 Assertion validator, may be null.*/
    @Nullable private SAML20AssertionValidator assertionValidator;
    
    /** The SAML 2.0 Assertion validator lookup function, may be null.*/
    @Nullable
    private Function<Pair<ProfileRequestContext, Assertion>, SAML20AssertionValidator> assertionValidatorLookup;
    
    /** Function that builds a {@link ValidationContext} instance based on a 
     * {@link AssertionValidationInput} instance. */
    @NonnullAfterInit private Function<AssertionValidationInput, ValidationContext> validationContextBuilder;
    
    /** The resolver for the list of assertions to be validated. */
    @Nonnull private Function<ProfileRequestContext, List<Assertion>> assertionResolver;
    
    /** The resolved assertions to be validated. */
    private List<Assertion> assertions;
    
    /** Constructor. */
    public ValidateAssertions() {
        super();
        setInvalidFatal(true);
        setValidationContextBuilder(new DefaultAssertionValidationContextBuilder());
        setAssertionResolver(new DefaultAssertionResolver());
    }

    /**
     * Get the function which resolves the list of assertions to validate.
     * 
     * @return the assertion resolver function
     */
    @Nonnull public Function<ProfileRequestContext, List<Assertion>> getAssertionResolver() {
        return assertionResolver;
    }

    /**
     * Set the function which resolves the list of assertions to validate.
     * 
     * @param function the new assertion resolver function
     */
    public void setAssertionResolver(@Nonnull final Function<ProfileRequestContext, List<Assertion>> function) {
        throwSetterPreconditionExceptions();
        assertionResolver = function;
    }

    /**
     * Get the function that builds a {@link ValidationContext} instance based on a 
     * {@link AssertionValidationInput} instance.
     * 
     * <p>
     * Defaults to an instance of {@link DefaultAssertionValidationContextBuilder}.
     * </p>
     * 
     * @return the builder function
     */
    @NonnullAfterInit
    public Function<AssertionValidationInput, ValidationContext> getValidationContextBuilder() {
        return validationContextBuilder;
    }

    /**
     * Set the function that builds a {@link ValidationContext} instance based on a 
     * {@link AssertionValidationInput} instance.
     * 
     * <p>
     * Defaults to an instance of {@link DefaultAssertionValidationContextBuilder}.
     * </p>
     * 
     * @param builder the builder function
     */
    public void setValidationContextBuilder(
            @Nonnull final Function<AssertionValidationInput, ValidationContext> builder) {
        throwSetterPreconditionExceptions();
        validationContextBuilder = builder;
    }

    /**
     * Get the HTTP servlet request being processed.
     * 
     * @return the HTTP servlet request
     */
    @NonnullAfterInit public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    /**
     * Set the HTTP servlet request being processed.
     * 
     * @param request The HTTP servlet request
     */
    public void setHttpServletRequest(@Nonnull final HttpServletRequest request) {
        throwSetterPreconditionExceptions();
        httpServletRequest = request;
    }
    
    /**
     * Get flag which indicates whether a failure of Assertion validation should be considered a fatal processing error.
     * 
     * <p>
     * Defaults to: {@code true}.
     * </p>
     * 
     * @return Returns the invalidFatal.
     */
    public boolean isInvalidFatal() {
        return invalidFatal;
    }

    /**
     * Set flag which indicates whether a failure of Assertion validation should be considered a fatal processing error.
     * 
     * <p>
     * Defaults to: {@code true}.
     * </p>
     * 
     * @param flag The invalidFatal to set.
     */
    public void setInvalidFatal(final boolean flag) {
        throwSetterPreconditionExceptions();
        invalidFatal = flag;
    }
    
    /**
     * Get the locally-configured Assertion validator.
     * 
     * @return the local Assertion validator, or null
     */
    @Nullable public SAML20AssertionValidator getAssertionValidator() {
        return assertionValidator;
    }

    /**
     * Set the locally-configured Assertion validator.
     * 
     * @param validator the local Assertion validator, may be null
     */
    public void setAssertionValidator(@Nullable final SAML20AssertionValidator validator) {
        throwSetterPreconditionExceptions();
        assertionValidator = validator;
    }
    
    /**
     * Get the Assertion validator lookup function.
     * 
     * @return the Assertion validator lookup function, or null
     */
    @Nullable
    public Function<Pair<ProfileRequestContext, Assertion>, SAML20AssertionValidator> getAssertionValidatorLookup() {
        return assertionValidatorLookup;
    }

    /**
     * Set the Assertion validator lookup function.
     * 
     * @param function the Assertion validator lookup function, may be null
     */
    public void setAssertionValidatorLookup(
            @Nullable final Function<Pair<ProfileRequestContext, Assertion>, SAML20AssertionValidator> function) {
        throwSetterPreconditionExceptions();
        assertionValidatorLookup = function;
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (getAssertionResolver() == null) {
            throw new ComponentInitializationException("Assertion resolver function cannot be null");
        }

        if (getValidationContextBuilder() == null) {
            throw new ComponentInitializationException("ValidationContext builder cannot be null");
        }
        
        if (getHttpServletRequest() == null) {
            throw new ComponentInitializationException("HttpServletRequest cannot be null");
        }
        
        if (getAssertionValidator() == null) {
            if (getAssertionValidatorLookup() == null) {
                throw new ComponentInitializationException("Both Assertion validator and lookup function were null");
            }
            log.info("{} Assertion validator is null, must be resovleable via the lookup function", getLogPrefix());
        }
    }

    /** {@inheritDoc} */
    protected void doDestroy() {
        httpServletRequest = null;
        
        super.doDestroy();
    }

    /** {@inheritDoc} */
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }

        assertions = getAssertionResolver().apply(profileRequestContext);
        if (assertions == null || assertions.isEmpty()) {
            log.info("{} Profile context contained no Assertions to validate. Skipping further processing",
                    getLogPrefix());
            return false;
        }

        return true;
    }

    /** {@inheritDoc} */
    protected void doExecute(@Nonnull final ProfileRequestContext profileContext) {
        boolean sawNonValid = false;
        for (final Assertion assertion : assertions) {
            final SAML20AssertionValidator validator = resolveValidator(profileContext, assertion);
            if (validator == null) {
                log.warn("{} No SAML20AssertionValidator was available, terminating", getLogPrefix());
                ActionSupport.buildEvent(profileContext, SAMLEventIds.UNABLE_VALIDATE_ASSERTION);
                return;
            }
        
            try { 
                final ValidationContext validationContext = buildValidationContext(profileContext, assertion);
            
                final ValidationResult validationResult = validator.validate(assertion, validationContext);
                if (validationResult != ValidationResult.VALID) {
                    sawNonValid = true;
                }
                processResult(validationContext, validationResult, assertion, profileContext);
            } catch (final Throwable t) {
                log.warn("{} There was a problem determining Assertion validity", getLogPrefix(), t);
                ActionSupport.buildEvent(profileContext, SAMLEventIds.UNABLE_VALIDATE_ASSERTION);
                return;
            }
        }
        
        if (sawNonValid && isInvalidFatal()) {
            ActionSupport.buildEvent(profileContext, SAMLEventIds.ASSERTION_INVALID);
        } else {
            ActionSupport.buildProceedEvent(profileContext);
        }
    }

    /**
     * Process the result of the assertion validation.
     * 
     * @param validationContext the Assertion validation context
     * @param validationResult the Assertion validation result
     * @param assertion the assertion being evaluated produced
     * @param profileContext the current profile request context
     */
    protected void processResult(@Nonnull final ValidationContext validationContext, 
            @Nonnull final ValidationResult validationResult, @Nonnull final Assertion assertion, 
            @Nonnull final ProfileRequestContext profileContext) {

        log.debug("{} Assertion validation result was: {}", getLogPrefix(), validationResult);
        if (validationResult != ValidationResult.VALID) {
            log.info("{} Assertion validation failure msg was: {}",
                    getLogPrefix(), validationContext.getValidationFailureMessage());
        }

        assertion.getObjectMetadata().put(new ValidationProcessingData(validationContext, validationResult));
    }

    /**
     * Resolve the Assertion token validator to use with the specified Assertion.
     * 
     * @param profileContext the current profile context
     * @param assertion the assertion being evaluated
     * 
     * @return the token validator
     */
    @Nullable protected SAML20AssertionValidator resolveValidator(@Nonnull final ProfileRequestContext profileContext, 
            @Nonnull final Assertion assertion) {
        
        if (getAssertionValidatorLookup() != null) {
            log.debug("{} Attempting to resolve SAML 2 Assertion validator via lookup function", getLogPrefix());
            final SAML20AssertionValidator validator = getAssertionValidatorLookup().apply(
                    new Pair<>(profileContext, assertion));
            if (validator != null) {
                log.debug("{} Resolved SAML 2 Assertion validator via lookup function", getLogPrefix());
                return validator;
            }
        }
        
        if (getAssertionValidator() != null) {
            log.debug("{} Resolved locally configured SAML 2 Assertion validator", getLogPrefix());
            return getAssertionValidator();
        }
        
        log.debug("{} No SAML 2 Assertion validator could be resolved", getLogPrefix());
        return null;
    }

    /**
     * Build the Assertion ValidationContext.
     * 
     * @param profileContext the current profile context
     * @param assertion the assertion which is to be validated
     * 
     * @return the new Assertion validation context to use
     * 
     * @throws AssertionValidationException if no validation context instance could be built
     */
    @Nonnull protected ValidationContext buildValidationContext(@Nonnull final ProfileRequestContext profileContext, 
            @Nonnull final Assertion assertion) throws AssertionValidationException {
        
        final ValidationContext validationContext = getValidationContextBuilder().apply(
                new AssertionValidationInput(profileContext, getHttpServletRequest(), assertion));
        
        if (validationContext == null) {
            log.warn("{} ValidationContext produced was null", getLogPrefix());
            throw new AssertionValidationException("Assertion ValidationContext was null");
        }
        
        return validationContext;
    }
    
    /**
     * The default assertion resolver function.
     */
    public class DefaultAssertionResolver implements Function<ProfileRequestContext, List<Assertion>> {

        /** {@inheritDoc} */
        public List<Assertion> apply(@Nonnull final ProfileRequestContext profileContext) {
            final SAMLObject message = (SAMLObject) profileContext.getInboundMessageContext().getMessage();
            if (message instanceof Response) {
                return ((Response) message).getAssertions();
            }
            
            return null;
        }
        
    }
    
    /**
     * Class which holds data relevant to validating a SAML 2.0 Assertion.
     */
    public class AssertionValidationInput {
        
        /** The profile request context input. */
        private ProfileRequestContext profileContext;
        
        /** The HTTP request input. */
        private HttpServletRequest httpServletRequest;
        
        /** The Assertion being evaluated. */
        private Assertion assertion;

        /**
         * Constructor.
         *
         * @param context the profile request context being evaluated
         * @param request the HTTP request being evaluated
         * @param samlAssertion the assertion being evaluated
         */
        public AssertionValidationInput(@Nonnull final ProfileRequestContext context,
                @Nonnull final HttpServletRequest request, @Nonnull final Assertion samlAssertion) {
            profileContext = Constraint.isNotNull(context, "ProfileRequestContext may not be null");
            httpServletRequest = Constraint.isNotNull(request, "HttpServletRequest may not be null");
            assertion = Constraint.isNotNull(samlAssertion, "Assertion may not be null");
        }

        /**
         * Get the {@link ProfileRequestContext} input.
         * 
         * @return the message context input
         */
        @Nonnull public ProfileRequestContext getProfileRequestContext() {
            return profileContext;
        }

        /**
         * Get the {@link HttpServletRequest} input.
         * 
         * @return the HTTP servlet request input
         */
        @Nonnull public HttpServletRequest getHttpServletRequest() {
            return httpServletRequest;
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

}
