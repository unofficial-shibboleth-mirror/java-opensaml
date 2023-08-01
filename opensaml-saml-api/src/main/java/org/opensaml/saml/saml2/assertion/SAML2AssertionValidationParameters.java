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

import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.SubjectLocality;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Parameter keys used to store and retrieve static and dynamic parameters within a 
 * {@link org.opensaml.saml.common.assertion.ValidationContext}.
 */
public final class SAML2AssertionValidationParameters {

    /** The standard prefix for all SAML 2 parameters defined in this set. */
    @Nonnull @NotEmpty public static final String STD_PREFIX = "saml2";
    
    /** The standard infix for parameters concerned with subject confirmation. */
    @Nonnull @NotEmpty public static final String SC_INFIX = ".SubjectConfirmation";
    
    /** The standard infix for parameters concerned with conditions evaluation. */
    @Nonnull @NotEmpty public static final String COND_INFIX = ".Conditions";
    
    /** The standard infix for parameters concerned with statement evaluation. */
    @Nonnull @NotEmpty public static final String STMT_INFIX = ".Statement";
    
    /**
     * Carries a {@link java.time.Duration} specifying a clock skew value.
     */
    @Nonnull @NotEmpty public static final String CLOCK_SKEW = STD_PREFIX + ".ClockSkew";

    /**
     * Carries a {@link java.time.Duration} specifying a lifetime from 'now' for IssueInstant.
     */
    @Nonnull @NotEmpty public static final String LIFETIME = STD_PREFIX + ".Lifetime";

    /**
     * Carries the {@link org.opensaml.saml.saml2.core.SubjectConfirmation} that confirmed the subject.
     */
    @Nonnull @NotEmpty public static final String CONFIRMED_SUBJECT_CONFIRMATION =
            STD_PREFIX + ".ConfirmedSubjectConfirmation";
    
    /**
     * Carries a {@link java.util.Set}<code>&lt;</code>{@link java.lang.String}<code>&gt;</code>
     * whose values are the acceptable 
     * {@link org.opensaml.saml.saml2.core.Issuer} values.
     */
    @Nonnull @NotEmpty public static final String VALID_ISSUERS = STD_PREFIX + ".ValidIssuers";

    /**
     * Carries a {@link java.lang.Boolean} flag which indicates whether the Assertion is required to be signed.
     */
    @Nonnull @NotEmpty public static final String SIGNATURE_REQUIRED = STD_PREFIX + ".SignatureRequired";

    /**
     * Carries a {@link net.shibboleth.shared.resolver.CriteriaSet} which will be used as the 
     * input to a {@link org.opensaml.xmlsec.signature.support.SignatureTrustEngine}.
     */
    @Nonnull @NotEmpty public static final String SIGNATURE_VALIDATION_CRITERIA_SET =
            STD_PREFIX + ".SignatureValidationCriteriaSet";

    /**
     * Carries a {@link org.opensaml.xmlsec.signature.support.SignatureTrustEngine} which will be used
     * to validate the Assertion signature.
     */
    @Nonnull @NotEmpty public static final String SIGNATURE_VALIDATION_TRUST_ENGINE =
            STD_PREFIX + ".SignatureValidationTrustEngine";

    /**
     * Carries a {@link org.opensaml.xmlsec.signature.support.SignaturePrevalidator} which will be used
     * to pre-validate the Assertion signature.
     * 
     */
    @Nonnull @NotEmpty public static final String SIGNATURE_VALIDATION_PREVALIDATOR =
            STD_PREFIX + ".SignatureValidationPrevalidator";

    /**
     * Carries a {@link java.lang.Boolean} flag which indicates whether
     * {@link org.opensaml.saml.saml2.core.SubjectConfirmationData} NotBefore is required.
     */
    @Nonnull @NotEmpty public static final String SC_NOT_BEFORE_REQUIRED = STD_PREFIX + SC_INFIX + ".NotBeforeRequired";
    
    /**
     * Carries a {@link java.lang.Boolean} flag which indicates whether
     * {@link org.opensaml.saml.saml2.core.SubjectConfirmationData} NotOnOrAfter is required.
     */
    @Nonnull @NotEmpty public static final String SC_NOT_ON_OR_AFTER_REQUIRED =
            STD_PREFIX + SC_INFIX + ".NoOnOrAfterRequired";
    
    /**
     * Carries a {@link java.lang.Boolean} flag which indicates whether
     * {@link org.opensaml.saml.saml2.core.SubjectConfirmationData} Recipient is required.
     */
    @Nonnull @NotEmpty public static final String SC_RECIPIENT_REQUIRED = STD_PREFIX + SC_INFIX + ".RecipientRequired";
    
    /**
     * Carries a {@link java.util.Set}<code>&lt;</code>{@link java.lang.String}<code>&gt;</code>
     * whose values are the acceptable 
     * {@link org.opensaml.saml.saml2.core.SubjectConfirmationData} recipients.
     */
    @Nonnull @NotEmpty public static final String SC_VALID_RECIPIENTS = STD_PREFIX + SC_INFIX + ".ValidRecipients";

    /**
     * Carries a {@link java.lang.Boolean} flag which indicates whether the
     * {@link org.opensaml.saml.saml2.core.SubjectConfirmationData} Address is required.
     */
    @Nonnull @NotEmpty public static final String SC_ADDRESS_REQUIRED = STD_PREFIX + SC_INFIX + ".AddressRequired";

    /**
     * Carries a {@link java.lang.Boolean} flag which indicates whether the
     * {@link org.opensaml.saml.saml2.core.SubjectConfirmationData} address should be checked.
     */
    @Nonnull @NotEmpty public static final String SC_CHECK_ADDRESS = STD_PREFIX + SC_INFIX + ".CheckAddress";

    /**
     * Carries a {@link java.util.Set}<code>&lt;</code>{@link java.net.InetAddress}<code>&gt;</code>
     * whose values are the acceptable 
     * {@link org.opensaml.saml.saml2.core.SubjectConfirmationData} addresses.
     */
    @Nonnull @NotEmpty public static final String SC_VALID_ADDRESSES = STD_PREFIX + SC_INFIX + ".ValidAddresses";
    
    /**
     * Carries a {@link java.lang.Boolean} flag which indicates whether
     * {@link org.opensaml.saml.saml2.core.SubjectConfirmationData} InResponseTo is required.
     */
    @Nonnull @NotEmpty public static final String SC_IN_RESPONSE_TO_REQUIRED =
            STD_PREFIX + SC_INFIX + ".InResponseToRequired";
    
    /**
     * Carries a {@link java.lang.String} whose value is the acceptable 
     * {@link org.opensaml.saml.saml2.core.SubjectConfirmationData} InResponseTo.
     */
    @Nonnull @NotEmpty public static final String SC_VALID_IN_RESPONSE_TO =
            STD_PREFIX + SC_INFIX + ".ValidInResponseTo";
    
    /**
     * Carries the {@link java.security.PublicKey} used by the presenter.
     */
    @Nonnull @NotEmpty public static final String SC_HOK_PRESENTER_KEY = STD_PREFIX + SC_INFIX + ".HoK.PresenterKey";

    /**
     * Carries the {@link java.security.cert.X509Certificate} used by the presenter.
     */
    @Nonnull @NotEmpty public static final String SC_HOK_PRESENTER_CERT =
            STD_PREFIX + SC_INFIX + ".HoK.PresenterCertificate";

    /**
     * Carries the {@link org.opensaml.xmlsec.signature.KeyInfo} that successfully confirmed the subject 
     * via holder-of-key subject confirmation.
     */
    @Nonnull @NotEmpty public static final String SC_HOK_CONFIRMED_KEYINFO =
            STD_PREFIX + SC_INFIX + ".HoK.ConfirmedKeyInfo";

    /**
     * Carries a {@link java.util.Set}<code>&lt;</code>{@link javax.xml.namespace.QName}<code>&gt;</code>
     * of required conditions which must be present in the assertion.
     */
    @Nonnull @NotEmpty public static final String COND_REQUIRED_CONDITIONS =
            STD_PREFIX + COND_INFIX + ".RequiredConditions";

    /**
     * Carries a {@link java.util.Set}<code>&lt;</code>{@link java.lang.String}<code>&gt;</code>
     * whose values are the acceptable 
     * {@link org.opensaml.saml.saml2.core.AudienceRestriction} {@link org.opensaml.saml.saml2.core.Audience}
     * values for evaluating the Assertion.
     */
    @Nonnull @NotEmpty public static final String COND_VALID_AUDIENCES = STD_PREFIX + COND_INFIX + ".ValidAudiences";

    /**
     * Carries a {@link java.time.Duration} representing the per-invocation value for the Assertion 
     * replay cache expiration.
     */
    @Nonnull @NotEmpty public static final String COND_ONE_TIME_USE_EXPIRES =
            STD_PREFIX + COND_INFIX + ".OneTimeUseExpires";

    /**
     * Carries a {@link java.lang.Boolean} flag which indicates whether the
     * Address attribute of the {@link SubjectLocality} of an {@link AuthnStatement}
     * should be checked.
     */
    @Nonnull @NotEmpty public static final String STMT_AUTHN_CHECK_ADDRESS = 
            STD_PREFIX + STMT_INFIX + ".Authn.SubjectLocality.CheckAddress";

    /**
     * Carries a {@link java.util.Set}<code>&lt;</code>{@link java.net.InetAddress}<code>&gt;</code>
     * whose values are the acceptable 
     * values for the Address attribute of the {@link SubjectLocality} of an {@link AuthnStatement}.
     */
    @Nonnull @NotEmpty public static final String STMT_AUTHN_VALID_ADDRESSES = 
            STD_PREFIX + STMT_INFIX + ".Authn.SubjectLocality.ValidAddresses";
    
    /**
     * Carries a {@link java.time.Duration} used to evaluate the 
     * allowed value of an AuthnInstant attribute of an {@link AuthnStatement}.
     */
    @Nonnull @NotEmpty public static final String STMT_AUTHN_MAX_TIME =
            STD_PREFIX + STMT_INFIX + ".Authn.MaxTimeSinceAuthn";
    
    
    /** Constructor. */
    private SAML2AssertionValidationParameters() {}

}
