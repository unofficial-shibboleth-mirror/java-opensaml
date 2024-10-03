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

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.FunctionSupport;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * A profile action which resolves SAML 2.0 Assertions from the profile request context
 * and validates them using a resolved or configured instance of {@link SAML20AssertionValidator}.
 * 
 * <p>
 * The {@link ValidationResult} along with the {@link ValidationContext} used are stored in the assertion's
 * {@link XMLObject#getObjectMetadata()} as instance of {@link ValidationProcessingData}.
 * </p>
 * 
 * @event {@link SAMLEventIds#ASSERTION_INVALID}
 * @event {@link SAMLEventIds#UNABLE_VALIDATE_ASSERTION}
 */
public class ValidateAssertions extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ValidateAssertions.class);
    
    /** Flag which indicates whether a failure of Assertion validation should be considered fatal. */
    private boolean invalidFatal;

    /** Flag for whether to check for servlet request during init. */
    private boolean checkDuringInit;

    /** The SAML 2.0 Assertion validator lookup function, may be null.*/
    @Nonnull
    private Function<Pair<ProfileRequestContext, Assertion>, SAML20AssertionValidator> assertionValidatorLookup;
    
    /** Function that builds a {@link ValidationContext} instance based on a 
     * {@link AssertionValidationInput} instance. */
    @Nonnull private Function<AssertionValidationInput, ValidationContext> validationContextBuilder;
    
    /** The resolver for the list of assertions to be validated. */
    @Nonnull private Function<ProfileRequestContext, List<Assertion>> assertionResolver;
    
    /** The resolved assertions to be validated. */
    @NonnullBeforeExec private List<Assertion> assertions;
    
    /** Constructor. */
    public ValidateAssertions() {
        setInvalidFatal(true);
        setCheckDuringInit(true);
        assertionValidatorLookup = FunctionSupport.constant(null);
        validationContextBuilder = new DefaultAssertionValidationContextBuilder();
        assertionResolver = new DefaultAssertionResolver();
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
        checkSetterPreconditions();
        assertionResolver = Constraint.isNotNull(function, "Assertion resolver cannot be null");
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
    @Nonnull
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
        checkSetterPreconditions();
        validationContextBuilder = Constraint.isNotNull(builder, "ValidationContext builder cannot be null");
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
        checkSetterPreconditions();
        invalidFatal = flag;
    }
    
    /**
     * Get whether {{@link #initialize()} should throw an exception if {@link #getHttpServletRequest()}
     * returns null.
     * 
     * @return whether a null request should fail initialization
     * 
     * @since 5.2.0
     */
    public boolean isCheckDuringInit() {
        return checkDuringInit;
    }
    
    /**
     * Set whether {{@link #initialize()} should throw an exception if {@link #getHttpServletRequest()}
     * returns null.
     * 
     * <p>Defaults to true.</p>
     * 
     * @param flag
     * 
     * @since 5.2.0
     */
    public void setCheckDuringInit(final boolean flag) {
        checkSetterPreconditions();
        
        checkDuringInit = flag;
    }
    
    /**
     * Get the configured Assertion validator.
     * 
     * @param profileRequestContext  profile request context
     * @param assertion assertion
     * 
     * @return the Assertion validator, or null
     */
    @Nullable public SAML20AssertionValidator getAssertionValidator(
            @Nonnull final ProfileRequestContext profileRequestContext, @Nonnull final Assertion assertion) {
        return assertionValidatorLookup.apply(new Pair<>(profileRequestContext, assertion));
    }

    /**
     * Set the locally-configured Assertion validator.
     * 
     * @param validator the local Assertion validator, may be null
     */
    public void setAssertionValidator(@Nullable final SAML20AssertionValidator validator) {
        checkSetterPreconditions();
        assertionValidatorLookup = FunctionSupport.constant(validator);
    }

    /**
     * Set the Assertion validator lookup function.
     * 
     * @param function the Assertion validator lookup function, may be null
     */
    public void setAssertionValidatorLookup(
            @Nonnull final Function<Pair<ProfileRequestContext, Assertion>, SAML20AssertionValidator> function) {
        checkSetterPreconditions();
        assertionValidatorLookup = Constraint.isNotNull(function, "AssertionValidator lookup function cannot be null");
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (isCheckDuringInit() && getHttpServletRequest() == null) {
            throw new ComponentInitializationException("HttpServletRequest cannot be null");
        }
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
            assert assertion != null;
            final SAML20AssertionValidator validator = getAssertionValidator(profileContext, assertion);
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
            log.info("{} Assertion validation failure(s): {}", getLogPrefix(),
                    validationContext.getValidationFailureMessages());
        }

        assertion.getObjectMetadata().put(new ValidationProcessingData(validationContext, validationResult));
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
        
        final HttpServletRequest servletRequest = getHttpServletRequest();
        assert servletRequest != null;
        final ValidationContext validationContext = getValidationContextBuilder().apply(
                new AssertionValidationInput(profileContext, servletRequest, assertion));
        
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
        @Nullable @Unmodifiable @NotLive public List<Assertion> apply(
                @Nullable final ProfileRequestContext profileContext) {
            final SAMLObject message = profileContext != null
                    ? (SAMLObject) profileContext.ensureInboundMessageContext().getMessage() : null;
            if (message instanceof Response r) {
                return r.getAssertions();
            }
            
            return null;
        }
        
    }
    
    /**
     * Class which holds data relevant to validating a SAML 2.0 Assertion.
     */
    public static class AssertionValidationInput {
        
        /** The profile request context input. */
        @Nonnull private ProfileRequestContext profileContext;
        
        /** The HTTP request input. */
        @Nonnull private HttpServletRequest httpServletRequest;
        
        /** The Assertion being evaluated. */
        @Nonnull private Assertion assertion;

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