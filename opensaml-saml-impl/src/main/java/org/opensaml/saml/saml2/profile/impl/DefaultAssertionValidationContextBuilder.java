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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.MessageException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.messaging.context.navigate.MessageContextLookup;
import org.opensaml.messaging.context.navigate.MessageContextLookup.Direction;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.context.SAMLMessageInfoContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.messaging.context.navigate.SAMLEntityIDFunction;
import org.opensaml.saml.common.messaging.context.navigate.SAMLMessageInfoContextIDFunction;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.profile.impl.ValidateAssertions.AssertionValidationInput;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.messaging.ServletRequestX509CredentialAdapter;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.signature.support.SignatureValidationParametersCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.collection.LazySet;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.net.HttpServletSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.logic.FunctionSupport;
import net.shibboleth.utilities.java.support.primitive.StringSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

/**
 *  Function which implements default behavior for building an instance of {@link ValidationContext}
 *  from an instance of {@link AssertionValidationInput}.
 */
public class DefaultAssertionValidationContextBuilder 
        implements Function<AssertionValidationInput, ValidationContext> {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(DefaultAssertionValidationContextBuilder.class);
    
    /** A function for resolving the clock skew to apply. */
    @Nullable private Function<ProfileRequestContext, Duration> clockSkew;
    
    /** A function for resolving the lifetime to apply. */
    @Nullable private Function<ProfileRequestContext, Duration> lifetime;
    
    /** A function for resolving the signature validation CriteriaSet for a particular function. */
    @Nullable private Function<Pair<ProfileRequestContext, Assertion>, CriteriaSet> signatureCriteriaSetFunction;
    
    /** Predicate for determining whether an Assertion signature is required. */
    @Nonnull private Predicate<ProfileRequestContext> signatureRequired;
    
    /** Predicate for determining whether an Assertion's network address(es) should be checked. */
    @Nonnull private Predicate<ProfileRequestContext> checkAddress;
    
    /** Function for determining the max allowed time since authentication. */
    @Nullable private Function<ProfileRequestContext, Duration> maximumTimeSinceAuthn;

    /** Predicate for determining whether to include the self entityID as a valid Recipient. */
    @Nonnull private Predicate<ProfileRequestContext> includeSelfEntityIDAsRecipient;
    
    /** Function for determining additional valid audience values. */
    @Nullable private Function<ProfileRequestContext, Set<String>> additionalAudiences;
    
    /** Function for determining additional valid Issuer values. */
    @Nonnull private Function<ProfileRequestContext, Set<String>> validIssuers;
    
    /** Function for determining the valid InResponseTo value. */
    @Nullable private Function<ProfileRequestContext, String> inResponseTo;
    
    /** Predicate for determining whether an Assertion SubjectConfirmationData InResponseTo is required. */
    @Nonnull private Predicate<ProfileRequestContext> inResponseToRequired;
    
    /** Predicate for determining whether an Assertion SubjectConfirmationData Recipient is required. */
    @Nonnull private Predicate<ProfileRequestContext> recipientRequired;
    
    /** Predicate for determining whether an Assertion SubjectConfirmationData NotBefore is required. */
    @Nonnull private Predicate<ProfileRequestContext> notBeforeRequired;
    
    /** Predicate for determining whether an Assertion SubjectConfirmationData NotOnOrAfter is required. */
    @Nonnull private Predicate<ProfileRequestContext> notOnOrAfterRequired;
    
    /** Predicate for determining whether an Assertion SubjectConfirmationData Address is required. */
    @Nonnull private Predicate<ProfileRequestContext> addressRequired;
    
    /** The set of required Conditions. */
    @Nonnull private Set<QName> requiredConditions;

    /** Resolver for security parameters context. */
    @Nonnull private Function<ProfileRequestContext, SecurityParametersContext> securityParametersLookupStrategy;

    /**
     * Constructor.
     */
    public DefaultAssertionValidationContextBuilder() {
        signatureRequired = Predicates.alwaysTrue();
        includeSelfEntityIDAsRecipient = Predicates.alwaysFalse();
        checkAddress = Predicates.alwaysTrue();
        inResponseTo = new DefaultValidInResponseToLookupFunction();
        inResponseToRequired = Predicates.alwaysFalse();
        recipientRequired = Predicates.alwaysFalse();
        notOnOrAfterRequired = Predicates.alwaysFalse();
        notBeforeRequired = Predicates.alwaysFalse();
        addressRequired = Predicates.alwaysFalse();
        requiredConditions = Collections.emptySet();
        validIssuers = new DefaultValidIssuersLookupFunction();

        securityParametersLookupStrategy = new ChildContextLookup<>(SecurityParametersContext.class)
                .compose(new InboundMessageContextLookup());
    }
    
    /**
     * Get the strategy by which to resolve the clock skew.
     * 
     * @return lookup strategy
     * 
     * @since 4.1.0
     */
    @Nullable public Function<ProfileRequestContext, Duration> getClockSkew() {
        return clockSkew;
    }

    /**
     * Set the clock skew.
     * 
     * @param skew clock skew
     * 
     * @since 4.1.0
     */
    public void setClockSkew(@Nullable final Duration skew) {
        clockSkew = FunctionSupport.constant(skew);
    }

    /**
     * Set the strategy by which to resolve the clock skew.
     * 
     * @param strategy lookup strategy
     * 
     * @since 4.1.0
     */
    public void setClockSkewLookupStrategy(@Nullable final Function<ProfileRequestContext, Duration> strategy) {
        clockSkew = strategy;
    }

    /**
     * Get the strategy by which to resolve the lifetime.
     * 
     * @return lookup strategy
     * 
     * @since 4.2.0
     */
    @Nullable public Function<ProfileRequestContext, Duration> getLifetime() {
        return lifetime;
    }

    /**
     * Set the lifetime.
     * 
     * @param duration lifetime
     * 
     * @since 4.2.0
     */
    public void setLifetime(@Nullable final Duration duration) {
        lifetime = FunctionSupport.constant(duration);
    }

    /**
     * Set the strategy by which to resolve the lifetime.
     * 
     * @param strategy lookup strategy
     * 
     * @since 4.2.0
     */
    public void setLifetimeLookupStrategy(@Nullable final Function<ProfileRequestContext, Duration> strategy) {
        lifetime = strategy;
    }

    /**
     * Get the strategy by which to resolve a {@link SecurityParametersContext}.
     *
     * @return the lookup strategy
     */
    @Nonnull public Function<ProfileRequestContext, SecurityParametersContext> getSecurityParametersLookupStrategy() {
        return securityParametersLookupStrategy;
    }

    /**
     * Set the strategy by which to resolve a {@link SecurityParametersContext}.
     *
     * @param strategy the strategy function
     */
    public void setSecurityParametersLookupStrategy(
            @Nonnull final Function<ProfileRequestContext, SecurityParametersContext> strategy) {
        securityParametersLookupStrategy =
                Constraint.isNotNull(strategy, "SecurityParametersContext lookup strategy was null") ;
    }

    /**
     * Get the set of required Conditions.
     * 
     * @return the required conditions, may be null
     */
    @Nonnull public Set<QName> getRequiredConditions() {
        return requiredConditions;
    }

    /**
     * Set the set of required Conditions.
     * 
     * @param conditions the required conditions
     */
    public void setRequiredConditions(@Nullable final Set<QName> conditions) {
        if (conditions != null) {
            requiredConditions = conditions.stream().filter(Objects::nonNull).collect(Collectors.toUnmodifiableSet());
        } else {
            requiredConditions = Collections.emptySet();
        }
    }

    /**
     * Get the predicate which determines whether to include the self entityID as a valid Recipient.
     * 
     * <p>
     * Defaults to an always false predicate;
     * </p>
     * 
     * @return the predicate
     */
    @Nonnull public Predicate<ProfileRequestContext> getIncludeSelfEntityIDAsRecipient() {
        return includeSelfEntityIDAsRecipient;
    }

    /**
     * Set the predicate which determines whether to include the self entityID as a valid Recipient.
     * 
     * <p>
     * Defaults to an always false predicate.
     * </p>
     * 
     * @param predicate the predicate, must be non-null
     */
    public void setIncludeSelfEntityIDAsRecipient(@Nonnull final Predicate<ProfileRequestContext> predicate) {
        includeSelfEntityIDAsRecipient = Constraint.isNotNull(predicate, "Signature required predicate was null");
    }
    
    /**
     * Get the predicate which determines whether an Assertion signature is required.
     * 
     * <p>
     * Defaults to an always true predicate;
     * </p>
     * 
     * @return the predicate
     */
    @Nonnull public Predicate<ProfileRequestContext> getSignatureRequired() {
        return signatureRequired;
    }

    /**
     * Set the predicate which determines whether an Assertion signature is required.
     * 
     * <p>
     * Defaults to an always true predicate.
     * </p>
     * 
     * @param predicate the predicate, must be non-null
     */
    public void setSignatureRequired(@Nonnull final Predicate<ProfileRequestContext> predicate) {
        signatureRequired = Constraint.isNotNull(predicate, "Signature required predicate was null");
    }

    /**
     * Set the function for determining the valid InResponseTo.
     *
     * <p>
     * Defaults to null.
     * </p>
     *
     * @param function the function, may be null
     */
    public void setInResponseTo(final @Nullable Function<ProfileRequestContext,String> function) {
        inResponseTo = function;
    }
    
    /**
     * Get the function for determining the valid InResponseTo.
     *
     * <p>
     * Defaults to null.
     * </p>
     *
     * @return the function
     */
    @Nullable public Function<ProfileRequestContext,String> getInResponseTo() {
        return inResponseTo;
    }
    
    /**
     * Get the predicate which determines whether an Assertion SubjectConfirmationData InResponseTo is required.
     * 
     * <p>
     * Defaults to an always false predicate;
     * </p>
     * 
     * @return the predicate
     */
    @Nonnull public Predicate<ProfileRequestContext> getInResponseToRequired() {
        return inResponseToRequired;
    }

    /**
     * Set the predicate which determines whether an Assertion SubjectConfirmationData InResponseTo is required.
     * 
     * <p>
     * Defaults to an always false predicate.
     * </p>
     * 
     * @param predicate the predicate, must be non-null
     */
    public void setInResponseToRequired(@Nonnull final Predicate<ProfileRequestContext> predicate) {
        inResponseToRequired = Constraint.isNotNull(predicate, "InResponseTo required predicate was null");
    }

    /**
     * Get the predicate which determines whether an Assertion SubjectConfirmationData Recipient is required.
     * 
     * <p>
     * Defaults to an always false predicate;
     * </p>
     * 
     * @return the predicate
     */
    @Nonnull public Predicate<ProfileRequestContext> getRecipientRequired() {
        return recipientRequired;
    }

    /**
     * Set the predicate which determines whether an Assertion SubjectConfirmationData Recipient is required.
     * 
     * <p>
     * Defaults to an always false predicate.
     * </p>
     * 
     * @param predicate the predicate, must be non-null
     */
    public void setRecipientRequired(@Nonnull final Predicate<ProfileRequestContext> predicate) {
        recipientRequired = Constraint.isNotNull(predicate, "Recipient required predicate was null");
    }

    /**
     * Get the predicate which determines whether an Assertion SubjectConfirmationData NotBefore is required.
     * 
     * <p>
     * Defaults to an always false predicate;
     * </p>
     * 
     * @return the predicate
     */
    @Nonnull public Predicate<ProfileRequestContext> getNotBeforeRequired() {
        return notBeforeRequired;
    }

    /**
     * Set the predicate which determines whether an Assertion SubjectConfirmationData NotBefore is required.
     * 
     * <p>
     * Defaults to an always false predicate.
     * </p>
     * 
     * @param predicate the predicate, must be non-null
     */
    public void setNotBeforeRequired(@Nonnull final Predicate<ProfileRequestContext> predicate) {
        notBeforeRequired = Constraint.isNotNull(predicate, "NotBefore required predicate was null");
    }

    /**
     * Get the predicate which determines whether an Assertion SubjectConfirmationData NotOnOrAfter is required.
     * 
     * <p>
     * Defaults to an always false predicate;
     * </p>
     * 
     * @return the predicate
     */
    @Nonnull public Predicate<ProfileRequestContext> getNotOnOrAfterRequired() {
        return notOnOrAfterRequired;
    }

    /**
     * Set the predicate which determines whether an Assertion SubjectConfirmationData NotOnOrAfter is required.
     * 
     * <p>
     * Defaults to an always false predicate.
     * </p>
     * 
     * @param predicate the predicate, must be non-null
     */
    public void setNotOnOrAfterRequired(@Nonnull final Predicate<ProfileRequestContext> predicate) {
        notOnOrAfterRequired = Constraint.isNotNull(predicate, "NotOnOrAfter required predicate was null");
    }

    /**
     * Get the predicate which determines whether an Assertion SubjectConfirmationData Address is required.
     * 
     * <p>
     * Defaults to an always false predicate;
     * </p>
     * 
     * @return the predicate
     */
    @Nonnull public Predicate<ProfileRequestContext> getAddressRequired() {
        return addressRequired;
    }

    /**
     * Set the predicate which determines whether an Assertion SubjectConfirmationData Address is required.
     * 
     * <p>
     * Defaults to an always false predicate.
     * </p>
     * 
     * @param predicate the predicate, must be non-null
     */
    public void setAddressRequired(final @Nonnull Predicate<ProfileRequestContext> predicate) {
        addressRequired = Constraint.isNotNull(predicate, "Address required predicate was null");
    }

    /**
     * Get the predicate which determines whether an Assertion's network address(es) should be checked.
     *
     * <p>
     * Defaults to an always true predicate;
     * </p>
     *
     * @return the predicate
     */
    @Nonnull public Predicate<ProfileRequestContext> getCheckAddress() {
        return checkAddress;
    }

    /**
     * Set the predicate which determines whether an Assertion's network address(es) should be checked.
     *
     * <p>
     * Defaults to an always true predicate.
     * </p>
     *
     * @param predicate the predicate, must be non-null
     */
    public void setCheckAddress(@Nonnull final Predicate<ProfileRequestContext> predicate) {
        checkAddress = Constraint.isNotNull(predicate, "Check address predicate was null");
    }

    /**
     * Get the function for determining additional audience values.
     *
     * <p>
     * Defaults to null.
     * </p>
     *
     * @return the function
     */
    @Nullable public Function<ProfileRequestContext,Set<String>> getAdditionalAudiences() {
        return additionalAudiences;
    }

    /**
     * Set the function for determining additional audience values.
     *
     * <p>
     * Defaults to null.
     * </p>
     *
     * @param function the function, may be null
     */
    public void setAdditionalAudiences(@Nullable final Function<ProfileRequestContext,Set<String>> function) {
        additionalAudiences = function;
    }

    /**
     * Get the function for determining the valid Issuer values
     *
     * <p>
     * Defaults to an implementation which resolves the outbound SAML peer entityID.
     * </p>
     *
     * @return the function
     */
    @Nonnull public Function<ProfileRequestContext,Set<String>> getValidIssuers() {
        return validIssuers;
    }

    /**
     * Set the function for determining the valid Issuer values
     *
     * <p>
     * Defaults to an implementation which resolves the outbound SAML peer entityID.
     * </p>
     *
     * @param function the function, may be null
     */
    public void setValidIssuers(@Nonnull final Function<ProfileRequestContext,Set<String>> function) {
        validIssuers = Constraint.isNotNull(function, "Valied Issuers function was null");
    }

    /**
     * Get the function for determining the max allowed time since authentication.
     *
     * <p>
     * Defaults to null.
     * </p>
     *
     * @return the function
     */
    @Nullable public Function<ProfileRequestContext,Duration> getMaximumTimeSinceAuthn() {
        return maximumTimeSinceAuthn;
    }

    /**
     * Set the function for determining the max allowed time since authentication.
     *
     * <p>
     * Defaults to null.
     * </p>
     *
     * @param function the function, may be null
     */
    public void setMaximumTimeSinceAuthn(@Nullable final Function<ProfileRequestContext,Duration> function) {
        maximumTimeSinceAuthn = function;
    }

    /**
     * Get the function for resolving the signature validation CriteriaSet for a particular function.
     * 
     * <p>
     * Defaults to: {@code null}.
     * </p>
     * 
     * @return a criteria set instance, or null
     */
    @Nullable public Function<Pair<ProfileRequestContext, Assertion>, CriteriaSet> getSignatureCriteriaSetFunction() {
        return signatureCriteriaSetFunction;
    }

    /**
     * Set the function for resolving the signature validation CriteriaSet for a particular function.
     * 
     * <p>
     * Defaults to: {@code null}.
     * </p>
     * 
     * @param function the resolving function, may be null
     */
    public void setSignatureCriteriaSetFunction(
            @Nullable final Function<Pair<ProfileRequestContext, Assertion>, CriteriaSet> function) {
        signatureCriteriaSetFunction = function;
    }

    /** {@inheritDoc} */
    @Nullable public ValidationContext apply(@Nullable final AssertionValidationInput input) {
        if (input == null) {
            return null;
        }
        
        return new ValidationContext(buildStaticParameters(input));
    }
    
    /**
     * Build the static parameters map for input to the {@link ValidationContext}.
     * 
     * @param input the assertion validation input
     * 
     * @return the static parameters map
     */
    @Nonnull protected Map<String,Object> buildStaticParameters(
            @Nonnull final AssertionValidationInput input) {
        
        final TreeMap<String, Object> staticParams = new TreeMap<>();
        
        // Clock skew
        if (getClockSkew() != null) {
            staticParams.put(SAML2AssertionValidationParameters.CLOCK_SKEW,
                    getClockSkew().apply(input.getProfileRequestContext()));
        }
        
        // Lifetime (for IssueInstant)
        if (getLifetime() != null) {
            staticParams.put(SAML2AssertionValidationParameters.LIFETIME,
                    getLifetime().apply(input.getProfileRequestContext()));
        }
        
        // Issuer
        staticParams.put(SAML2AssertionValidationParameters.VALID_ISSUERS,
                getValidIssuers().apply(input.getProfileRequestContext()));
        
        // Signature
        populateSignatureParameters(staticParams, input);
        
        // Conditions
        populateConditionsParameters(staticParams, input);
        
        final Set<InetAddress> validAddresses = getValidAddresses(input);
        final Boolean checkAddressEnabled = Boolean.valueOf(getCheckAddress().test(input.getProfileRequestContext()));
        
        // SubjectConfirmation
        populateSubjectConfirmationParameters(staticParams, input, validAddresses, checkAddressEnabled);
        
        // Statements
        populateStatementParams(staticParams, input, validAddresses, checkAddressEnabled);
        
        log.trace("Built static parameters map: {}", staticParams);
        
        return staticParams;
    }


    /**
     *  Populate the static signature parameters.
     * @param staticParams the parameters being populated
     * @param input validation input
     */
    private void populateSignatureParameters(@Nonnull final Map<String, Object> staticParams,
            @Nonnull final AssertionValidationInput input) {
        
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, 
                Boolean.valueOf(getSignatureRequired().test(input.getProfileRequestContext())));
        staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_VALIDATION_CRITERIA_SET, 
                getSignatureCriteriaSet(input));
        final SecurityParametersContext securityParameters = getSecurityParametersLookupStrategy()
                .apply(input.getProfileRequestContext());
        if (securityParameters != null && securityParameters.getSignatureValidationParameters() != null) {
            staticParams.put(SAML2AssertionValidationParameters.SIGNATURE_VALIDATION_TRUST_ENGINE,
                    securityParameters.getSignatureValidationParameters().getSignatureTrustEngine());
        }
    }
    
    /**
     * Populate the static Conditions parameters.
     * @param staticParams the parameters being populated
     * @param input validation input
     */
    private void populateConditionsParameters(@Nonnull final Map<String, Object> staticParams,
            @Nonnull final AssertionValidationInput input) {
        
        // For general Conditions
        staticParams.put(SAML2AssertionValidationParameters.COND_REQUIRED_CONDITIONS, getRequiredConditions(input));
        
        // For Audience Condition
        staticParams.put(SAML2AssertionValidationParameters.COND_VALID_AUDIENCES, getValidAudiences(input));
    }

    /**
     * Populate the static SubjectConfirmation parameters.
     * 
     * @param staticParams the parameters being populated
     * @param input validation input
     * @param validAddresses the valid addresses
     * @param checkAddressEnabled whether address checking is enabled
     */
    private void populateSubjectConfirmationParameters(@Nonnull final Map<String, Object> staticParams,
            @Nonnull final AssertionValidationInput input, @Nonnull final Set<InetAddress> validAddresses,
            @Nonnull final Boolean checkAddressEnabled) {
        
        // For HoK subject confirmation
        final X509Certificate attesterCertificate = getAttesterCertificate(input);
        if (attesterCertificate != null) {
            staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_CERT, attesterCertificate);
        }
        final PublicKey attesterPublicKey = getAttesterPublicKey(input);
        if (attesterPublicKey != null) {
            staticParams.put(SAML2AssertionValidationParameters.SC_HOK_PRESENTER_KEY, attesterPublicKey);
        }
        
        // For SubjectConfirmationData
        staticParams.put(SAML2AssertionValidationParameters.SC_RECIPIENT_REQUIRED,
                Boolean.valueOf(getRecipientRequired().test(input.getProfileRequestContext())));
        staticParams.put(SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS, getValidRecipients(input));
        
        staticParams.put(SAML2AssertionValidationParameters.SC_ADDRESS_REQUIRED,
                Boolean.valueOf(getAddressRequired().test(input.getProfileRequestContext())));
        staticParams.put(SAML2AssertionValidationParameters.SC_VALID_ADDRESSES, validAddresses);
        staticParams.put(SAML2AssertionValidationParameters.SC_CHECK_ADDRESS, checkAddressEnabled);
        
        staticParams.put(SAML2AssertionValidationParameters.SC_IN_RESPONSE_TO_REQUIRED,
                Boolean.valueOf(getInResponseToRequired().test(input.getProfileRequestContext())));
        if (getInResponseTo() != null) {
            staticParams.put(SAML2AssertionValidationParameters.SC_VALID_IN_RESPONSE_TO,
                    getInResponseTo().apply(input.getProfileRequestContext()));
        }
        
        staticParams.put(SAML2AssertionValidationParameters.SC_NOT_BEFORE_REQUIRED,
                Boolean.valueOf(getNotBeforeRequired().test(input.getProfileRequestContext())));
        staticParams.put(SAML2AssertionValidationParameters.SC_NOT_ON_OR_AFTER_REQUIRED,
                Boolean.valueOf(getNotOnOrAfterRequired().test(input.getProfileRequestContext())));
    }

    /**
     * Populate the static Statement params.
     * @param staticParams the parameters being populated
     * @param input validation input
     * @param validAddresses the valid addresses
     * @param checkAddressEnabled whether address checking is enabled
     */
    private void populateStatementParams(@Nonnull final Map<String, Object> staticParams,
            @Nonnull final AssertionValidationInput input, @Nonnull final Set<InetAddress> validAddresses,
            @Nonnull final Boolean checkAddressEnabled) {
        
        // For AuthnStatement
        staticParams.put(SAML2AssertionValidationParameters.STMT_AUTHN_VALID_ADDRESSES, validAddresses);
        staticParams.put(SAML2AssertionValidationParameters.STMT_AUTHN_CHECK_ADDRESS, checkAddressEnabled);
        if (getMaximumTimeSinceAuthn() != null) {
            staticParams.put(SAML2AssertionValidationParameters.STMT_AUTHN_MAX_TIME, 
                    getMaximumTimeSinceAuthn().apply(input.getProfileRequestContext()));
        }
    }
    
    /**
     * Get the set of required Conditions.
     * 
     * <p>
     * The default behavior is to return the locally-configured data via {@link #getRequiredConditions()}.
     * </p>
     * 
     * @param input the assertion validation input
     * 
     * @return the set of required Condition names, may be null
     */
    @Nonnull protected Set<QName> getRequiredConditions(@Nonnull final AssertionValidationInput input) {
        // Subclasses may override
        return getRequiredConditions();
    }

    /**
     * Get the signature validation criteria set.
     * 
     * <p>
     * This implementation first evaluates the result of applying the function 
     * {@link #getSignatureCriteriaSetFunction()}, if configured. If that evaluation did not
     * produce an {@link EntityIdCriterion}, one is added based on the issuer of the {@link Assertion}.
     * If that evaluation did not produce an instance of {@link UsageCriterion}, one is added with
     * the value of {@link UsageType#SIGNING}.
     * </p>
     * 
     * <p>
     * Finally the following criteria are added if not already present and if the corresponding data
     * is available in the inbound {@link MessageContext}:
     * </p>
     * <ul>
     *  <li>{@link RoleDescriptorCriterion}</li>
     *  <li>{@link EntityRoleCriterion}</li>
     *  <li>{@link ProtocolCriterion}</li>
     * </ul>
     * 
     * @param input the assertion validation input
     *
     * @return the criteria set based on the message context data
     */
    @Nonnull protected CriteriaSet getSignatureCriteriaSet(@Nonnull final AssertionValidationInput input) {
        final CriteriaSet criteriaSet = new CriteriaSet();
        
        if (getSignatureCriteriaSetFunction() != null) {
            final CriteriaSet dynamicCriteria = getSignatureCriteriaSetFunction().apply(
                    new Pair<>(input.getProfileRequestContext(), input.getAssertion()));
            if (dynamicCriteria != null) {
                criteriaSet.addAll(dynamicCriteria);
            }
        }
        
        if (!criteriaSet.contains(EntityIdCriterion.class)) {
            String issuer = null;
            if (input.getAssertion().getIssuer() != null) {
                issuer = StringSupport.trimOrNull(input.getAssertion().getIssuer().getValue());
            }
            if (issuer != null) {
                log.debug("Adding internally-generated EntityIdCriterion with value of: {}", issuer);
                criteriaSet.add(new EntityIdCriterion(issuer));
            }
        }
        
        if (!criteriaSet.contains(UsageCriterion.class)) {
            log.debug("Adding internally-generated UsageCriterion with value of: {}", UsageType.SIGNING);
            criteriaSet.add(new UsageCriterion(UsageType.SIGNING));
        }
        
        final MessageContext inboundContext = input.getProfileRequestContext().getInboundMessageContext();
        if (inboundContext != null) {
            populateSignatureCriteriaFromInboundContext(criteriaSet, inboundContext);
        }

        log.debug("Resolved Signature validation CriteriaSet: {}", criteriaSet);
        
        return criteriaSet;
    }

    /**
     * Populate signature criteria from the specified {@link MessageContext}.
     *
     * <ul>
     *  <li>{@link RoleDescriptorCriterion}</li>
     *  <li>{@link EntityRoleCriterion}</li>
     *  <li>{@link ProtocolCriterion}</li>
     * </ul>
     *
     * @param criteriaSet the criteria set to populate
     * @param inboundContext the inbound message context
     */
    // Checkstyle: CyclomaticComplexity OFF
    protected void populateSignatureCriteriaFromInboundContext(@Nonnull final CriteriaSet criteriaSet,
            @Nonnull final MessageContext inboundContext) {

        final SAMLPeerEntityContext peerContext = inboundContext.getSubcontext(SAMLPeerEntityContext.class);
        if (peerContext != null) {
            if (!criteriaSet.contains(RoleDescriptorCriterion.class)) {
                final SAMLMetadataContext metadataContext = peerContext.getSubcontext(SAMLMetadataContext.class);
                if (metadataContext != null && metadataContext.getRoleDescriptor() != null) {
                    criteriaSet.add(new RoleDescriptorCriterion(metadataContext.getRoleDescriptor()));
                }
            }
            if (!criteriaSet.contains(EntityRoleCriterion.class)) {
                final QName role = peerContext.getRole();
                if (role != null) {
                    criteriaSet.add(new EntityRoleCriterion(role));
                }
            }
        }

        final SAMLProtocolContext protocolContext = inboundContext.getSubcontext(SAMLProtocolContext.class);
        if (!criteriaSet.contains(ProtocolCriterion.class)
                && protocolContext != null && protocolContext.getProtocol() != null) {
            criteriaSet.add(new ProtocolCriterion(protocolContext.getProtocol()));
        }
        
        if (!criteriaSet.contains(SignatureValidationParametersCriterion.class)) {
            final SecurityParametersContext secParamsContext =
                    inboundContext.getSubcontext(SecurityParametersContext.class);
            if (secParamsContext != null && secParamsContext.getSignatureValidationParameters() != null) {
                criteriaSet.add(new SignatureValidationParametersCriterion(
                        secParamsContext.getSignatureValidationParameters()));
            }
        }
    }
    // Checkstyle: CyclomaticComplexity ON

    /**
     * Get the attesting entity's {@link X509Certificate}.
     * 
     * <p>
     * This implementation returns the client TLS certificate present in the 
     * {@link jakarta.servlet.http.HttpServletRequest}, or null if one is not present.
     * </p>
     * 
     * @param input the assertion validation input
     * 
     * @return the entity certificate, or null
     */
    @Nullable protected X509Certificate getAttesterCertificate(
            @Nonnull final AssertionValidationInput input) {
        try {
            final X509Credential credential = new ServletRequestX509CredentialAdapter(input.getHttpServletRequest());
            return credential.getEntityCertificate();
        } catch (final SecurityException e) {
            log.debug("Peer TLS X.509 certificate was not present. " 
                    + "Holder-of-key proof-of-possession via client TLS cert will not be possible");
            return null;
        }
    }

    /**
     * Get the attesting entity's {@link PublicKey}.
     * 
     * <p>
     * This implementation returns null. Subclasses should override to implement specific logic.
     * </p>
     * 
     * @param input the assertion validation input
     * 
     * @return the entity public key, or null
     */
    @Nullable protected PublicKey getAttesterPublicKey(@Nonnull final AssertionValidationInput input) {
        return null;
    }
    
    /**
     * Get the valid recipient endpoints for attestation.
     * 
     * <p>
     * This implementation returns a set containing the 2 values;
     * </p>
     * <ol>
     * <li>
     * the result of evaluating
     * {@link SAMLBindingSupport#getActualReceiverEndpointURI(MessageContext, HttpServletRequest)}
     * </li>
     * <li>
     * if enabled via the eval of {@link #getIncludeSelfEntityIDAsRecipient()}, the value from evaluating
     * {@link #getSelfEntityID(AssertionValidationInput)} if non-null
     * 
     * </li>
     * </ol>
     * 
     * @param input the assertion validation input
     * 
     * @return set of recipient endpoint URI's
     */
    @Nonnull protected Set<String> getValidRecipients(@Nonnull final AssertionValidationInput input) {
        final LazySet<String> validRecipients = new LazySet<>();
        
        try {
            final String endpoint = SAMLBindingSupport.getActualReceiverEndpointURI(
                    input.getProfileRequestContext().getInboundMessageContext(), input.getHttpServletRequest());
            if (endpoint != null) {
                validRecipients.add(endpoint);
            }
        } catch (final MessageException e) {
            log.warn("Attempt to resolve recipient endpoint failed", e);
        }
        
        if (getIncludeSelfEntityIDAsRecipient().test(input.getProfileRequestContext())) {
            final String selfEntityID = getSelfEntityID(input);
            if (selfEntityID != null) {
                validRecipients.add(selfEntityID);
            }
        }
        
        log.debug("Resolved valid subject confirmation recipients set: {}", validRecipients);
        return validRecipients;
    }

    /**
     * Get the set of addresses which are valid for subject confirmation.
     * 
     * <p>
     * This implementation simply returns the set based on 
     * {@link #getAttesterIPAddress(AssertionValidationInput)}, if that produces a value.
     * Otherwise an empty set is returned.
     * </p>
     * 
     * @param input the assertion validation input
     * 
     * @return the set of valid addresses
     */
    @Nonnull protected Set<InetAddress> getValidAddresses(@Nonnull final AssertionValidationInput input) {
        try {
            final LazySet<InetAddress> validAddresses = new LazySet<>();
            InetAddress[] addresses = null;
            final String attesterIPAddress = getAttesterIPAddress(input);
            log.debug("Saw attester IP address: {}", attesterIPAddress);
            if (attesterIPAddress != null) {
                addresses = InetAddress.getAllByName(attesterIPAddress);
                validAddresses.addAll(Arrays.asList(addresses));
                log.debug("Resolved valid subject confirmation InetAddress set: {}", validAddresses);
                return validAddresses;
            }
            log.warn("Could not determine attester IP address. Validation of Assertion may or may not succeed");
            return Collections.emptySet();
        } catch (final UnknownHostException e) {
            log.warn("Processing of attester IP address failed. Validation of Assertion may or may not succeed", e);
            return Collections.emptySet();
        }
    }
    
    /**
     * Get the attester's IP address.
     * 
     * <p>
     * This implementation returns the value of {@link jakarta.servlet.http.HttpServletRequest#getRemoteAddr()}.
     * </p>
     * 
     * @param input the assertion validation input
     * 
     * @return the IP address of the attester
     */
    @Nonnull protected String getAttesterIPAddress(@Nonnull final AssertionValidationInput input) {
        //TODO support indirection via SAMLBindingSupport and use of SAMLMessageReceivedEndpointContext?
        return HttpServletSupport.getRemoteAddr(input.getHttpServletRequest());
    }
    
    /**
     * Get the valid audiences for attestation.
     * 
     * <p>
     * This implementation returns a set containing the union of:
     * </p>
     * <ol>
     * <li>the result of {@link #getSelfEntityID(AssertionValidationInput)}, if non-null</li>
     * <li>the result of evaluating {@link #getAdditionalAudiences()}, if non-null</li>
     * </ol>
     * 
     * @param input the assertion validation input
     * 
     * @return set of audience URI's
     */
    @Nonnull protected Set<String> getValidAudiences(@Nonnull final AssertionValidationInput input) {
        final LazySet<String> validAudiences = new LazySet<>();
        
        final String selfEntityID = getSelfEntityID(input);
        if (selfEntityID != null) {
            validAudiences.add(selfEntityID);
        }
        
        if (getAdditionalAudiences() != null) {
            final Set<String> additional = getAdditionalAudiences().apply(input.getProfileRequestContext());
            if (additional != null) {
                validAudiences.addAll(additional);
            }
        }
        
        log.debug("Resolved valid audiences set: {}", validAudiences);
        return validAudiences;
    }
    
    /**
     * Get the self entityID.
     * 
     * @param input the assertion validation input
     * 
     * @return the self entityID, or null if could not be resolved
     */
    @Nullable protected String getSelfEntityID(@Nonnull final AssertionValidationInput input) {
        final SAMLSelfEntityContext selfContext = input.getProfileRequestContext()
                .getInboundMessageContext()
                .getSubcontext(SAMLSelfEntityContext.class);
        
        if (selfContext != null) {
            return selfContext.getEntityId();
        }
        
        return null;
    }
    
    /** Default strategy for resolving the valid InResponseTo value. */
    public static class DefaultValidInResponseToLookupFunction implements Function<ProfileRequestContext, String> {

        /** The lookup delegate. */
        private Function<MessageContext, String> delegate;

        /** Constructor. */
        public DefaultValidInResponseToLookupFunction() {
            delegate = new SAMLMessageInfoContextIDFunction().compose(
                    new ChildContextLookup<>(SAMLMessageInfoContext.class, true).compose(
                            new MessageContextLookup<>(Direction.OUTBOUND)));
        }

        /** {@inheritDoc} */
        public String apply(@Nullable final ProfileRequestContext prc) {
            if (prc == null || prc.getInboundMessageContext() == null) {
                return null;
            }

            //Note: Doesn't matter whether we apply to inbound or outbound
            return delegate.apply(prc.getInboundMessageContext());
        }

    }
    
    /** 
     * Default strategy for resolving the valid Issuers.
     * 
     * <p>
     * Resolves the entityID from the {@link SAMLPeerEntityContext} child of the outbound {@link MessageContext}.
     * </p>
     * */
    public static class DefaultValidIssuersLookupFunction implements Function<ProfileRequestContext, Set<String>> {
        
        /** The lookup delegate. */
        private Function<MessageContext, String> delegate;

        /** Constructor. */
        public DefaultValidIssuersLookupFunction() {
            delegate = new SAMLEntityIDFunction().compose(
                    new ChildContextLookup<>(SAMLPeerEntityContext.class).compose(
                            new MessageContextLookup<>(Direction.OUTBOUND)));
        }

        /** {@inheritDoc} */
        public Set<String> apply(@Nullable final ProfileRequestContext prc) {
            if (prc == null || prc.getInboundMessageContext() == null) {
                return null;
            }
            
            // Note: Doesn't matter whether we apply to inbound or outbound
            final String entityID = delegate.apply(prc.getInboundMessageContext());
            if (entityID != null) {
                return Collections.singleton(entityID);
            }
            return Collections.emptySet();
        }
        
    }

}
