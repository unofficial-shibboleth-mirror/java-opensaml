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

/**
 * 
 */

package org.opensaml.saml.saml2.core;

import java.time.Instant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Core StatusResponseType.
 */
public interface StatusResponseType extends SignableSAMLObject {

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "StatusResponseType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20P_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20P_PREFIX);

    /** ID attribute name. */
    @Nonnull @NotEmpty static final String ID_ATTRIB_NAME = "ID";

    /** InResponseTo attribute name. */
    @Nonnull @NotEmpty static final String IN_RESPONSE_TO_ATTRIB_NAME = "InResponseTo";

    /** Version attribute name. */
    @Nonnull @NotEmpty static final String VERSION_ATTRIB_NAME = "Version";

    /** IssueInstant attribute name. */
    @Nonnull @NotEmpty static final String ISSUE_INSTANT_ATTRIB_NAME = "IssueInstant";

    /** QName for the attribute which defines the IssueInstant. */
    @Nonnull static final QName ISSUE_INSTANT_ATTRIB_QNAME =
            new QName(null, "IssueInstant", XMLConstants.DEFAULT_NS_PREFIX);
    
    /** Destination attribute name. */
    @Nonnull @NotEmpty static final String DESTINATION_ATTRIB_NAME = "Destination";

    /** Consent attribute name. */
    @Nonnull @NotEmpty static final String CONSENT_ATTRIB_NAME = "Consent";

    /** Unspecified consent URI. */
    @Nonnull @NotEmpty static final String UNSPECIFIED_CONSENT = "urn:oasis:names:tc:SAML:2.0:consent:unspecified";

    /** Obtained consent URI. */
    @Nonnull @NotEmpty static final String OBTAINED_CONSENT = "urn:oasis:names:tc:SAML:2.0:consent:obtained";

    /** Prior consent URI. */
    @Nonnull @NotEmpty static final String PRIOR_CONSENT = "urn:oasis:names:tc:SAML:2.0:consent:prior";

    /** Implicit consent URI. */
    @Nonnull @NotEmpty static final String IMPLICIT_CONSENT = "urn:oasis:names:tc:SAML:2.0:consent:current-implicit";

    /** Explicit consent URI. */
    @Nonnull @NotEmpty static final String EXPLICIT_CONSENT = "urn:oasis:names:tc:SAML:2.0:consent:current-explicit";

    /** Unavailable consent URI. */
    @Nonnull @NotEmpty static final String UNAVAILABLE_CONSENT = "urn:oasis:names:tc:SAML:2.0:consent:unavailable";

    /** Inapplicable consent URI. */
    @Nonnull @NotEmpty static final String INAPPLICABLE_CONSENT = "urn:oasis:names:tc:SAML:2.0:consent:inapplicable";

    /**
     * Gets the SAML Version of this response.
     * 
     * @return the SAML Version of this response.
     */
    @Nullable SAMLVersion getVersion();

    /**
     * Sets the SAML Version of this response.
     * 
     * @param newVersion the SAML Version of this response
     */
    void setVersion(@Nullable final SAMLVersion newVersion);

    /**
     * Gets the unique identifier of the response.
     * 
     * @return the unique identifier of the response
     */
    @Nullable String getID();

    /**
     * Sets the unique identifier of the response.
     * 
     * @param newID the unique identifier of the response
     */
    void setID(@Nullable final String newID);

    /**
     * Gets the unique request identifier for which this is a response.
     * 
     * @return the unique identifier of the originating request
     */
    @Nullable String getInResponseTo();

    /**
     * Sets the unique request identifier for which this is a response.
     * 
     * @param newInResponseTo the unique identifier of the originating request
     */
    void setInResponseTo(@Nullable final String newInResponseTo);

    /**
     * Gets the date/time the response was issued.
     * 
     * @return the date/time the response was issued
     */
    @Nullable Instant getIssueInstant();

    /**
     * Sets the date/time the response was issued.
     * 
     * @param newIssueInstant the date/time the response was issued
     */
    void setIssueInstant(@Nullable final Instant newIssueInstant);

    /**
     * Gets the URI of the destination of the response.
     * 
     * @return the URI of the destination of the response
     */
    @Nullable String getDestination();

    /**
     * Sets the URI of the destination of the response.
     * 
     * @param newDestination the URI of the destination of the response
     */
    void setDestination(@Nullable final String newDestination);

    /**
     * Gets the consent obtained from the principal for sending this response.
     * 
     * @return the consent obtained from the principal for sending this response
     */
    @Nullable String getConsent();

    /**
     * Sets the consent obtained from the principal for sending this response.
     * 
     * @param newConsent the consent obtained from the principal for sending this response
     */
    void setConsent(@Nullable final String newConsent);

    /**
     * Gets the issuer of this response.
     * 
     * @return the issuer of this response
     */
    @Nullable Issuer getIssuer();

    /**
     * Sets the issuer of this response.
     * 
     * @param newIssuer the issuer of this response
     */
    void setIssuer(@Nullable final Issuer newIssuer);

    /**
     * Gets the Status of this response.
     * 
     * @return the Status of this response
     */
    @Nullable Status getStatus();

    /**
     * Sets the Status of this response.
     * 
     * @param newStatus the Status of this response
     */
    void setStatus(@Nullable final Status newStatus);

    /**
     * Gets the Extensions of this response.
     * 
     * @return the Status of this response
     */
    @Nullable Extensions getExtensions();

    /**
     * Sets the Extensions of this response.
     * 
     * @param newExtensions the Extensions of this response
     */
    void setExtensions(@Nullable final Extensions newExtensions);

}