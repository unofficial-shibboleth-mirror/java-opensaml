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

package org.opensaml.saml.saml2.core;

import java.time.Instant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

/**
 * SAML 2.0 Core RequestAbstractType.
 */
public interface RequestAbstractType extends SignableSAMLObject {

    /** Local name of the XSI type. */
    @Nonnull static final String TYPE_LOCAL_NAME = "RequestAbstractType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20P_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20P_PREFIX);

    /** ID attribute name. */
    @Nonnull static final String ID_ATTRIB_NAME = "ID";

    /** Version attribute name. */
    @Nonnull static final String VERSION_ATTRIB_NAME = "Version";

    /** IssueInstant attribute name. */
    @Nonnull static final String ISSUE_INSTANT_ATTRIB_NAME = "IssueInstant";

    /** QName for the attribute which defines the IssueInstant. */
    @Nonnull static final QName ISSUE_INSTANT_ATTRIB_QNAME =
            new QName(null, "IssueInstant", XMLConstants.DEFAULT_NS_PREFIX);
    
    /** Destination attribute name. */
    @Nonnull static final String DESTINATION_ATTRIB_NAME = "Destination";

    /** Consent attribute name. */
    @Nonnull static final String CONSENT_ATTRIB_NAME = "Consent";

    /** Unspecified consent URI. */
    @Nonnull static final String UNSPECIFIED_CONSENT = "urn:oasis:names:tc:SAML:2.0:consent:unspecified";

    /** Obtained consent URI. */
    @Nonnull static final String OBTAINED_CONSENT = "urn:oasis:names:tc:SAML:2.0:consent:obtained";

    /** Prior consent URI. */
    @Nonnull static final String PRIOR_CONSENT = "urn:oasis:names:tc:SAML:2.0:consent:prior";

    /** Implicit consent URI. */
    @Nonnull static final String IMPLICIT_CONSENT = "urn:oasis:names:tc:SAML:2.0:consent:current-implicit";

    /** Explicit consent URI. */
    @Nonnull static final String EXPLICIT_CONSENT = "urn:oasis:names:tc:SAML:2.0:consent:current-explicit";

    /** Unavailable consent URI. */
    @Nonnull static final String UNAVAILABLE_CONSENT = "urn:oasis:names:tc:SAML:2.0:consent:unavailable";

    /** Inapplicable consent URI. */
    @Nonnull static final String INAPPLICABLE_CONSENT = "urn:oasis:names:tc:SAML:2.0:consent:inapplicable";

    /**
     * Gets the SAML Version of this request.
     * 
     * @return the SAML Version of this request.
     */
    @Nullable SAMLVersion getVersion();

    /**
     * Sets the SAML Version of this request.
     * 
     * @param newVersion the SAML Version of this request
     */
    void setVersion(@Nullable final SAMLVersion newVersion);

    /**
     * Gets the unique identifier of the request.
     * 
     * @return the unique identifier of the request
     */
    @Nullable String getID();

    /**
     * Sets the unique identifier of the request.
     * 
     * @param newID the unique identifier of the request
     */
    void setID(@Nullable final String newID);

    /**
     * Gets the date/time the request was issued.
     * 
     * @return the date/time the request was issued
     */
    @Nullable Instant getIssueInstant();

    /**
     * Sets the date/time the request was issued.
     * 
     * @param newIssueInstant the date/time the request was issued
     */
    void setIssueInstant(@Nullable final Instant newIssueInstant);

    /**
     * Gets the URI of the destination of the request.
     * 
     * @return the URI of the destination of the request
     */
    @Nullable String getDestination();

    /**
     * Sets the URI of the destination of the request.
     * 
     * @param newDestination the URI of the destination of the request
     */
    void setDestination(@Nullable final String newDestination);

    /**
     * Gets the consent obtained from the principal for sending this request.
     * 
     * @return the consent obtained from the principal for sending this request
     */
    @Nullable String getConsent();

    /**
     * Sets the consent obtained from the principal for sending this request.
     * 
     * @param newConsent the new consent obtained from the principal for sending this request
     */
    void setConsent(@Nullable final String newConsent);

    /**
     * Gets the issuer of this request.
     * 
     * @return the issuer of this request
     */
    @Nullable Issuer getIssuer();

    /**
     * Sets the issuer of this request.
     * 
     * @param newIssuer the issuer of this request
     */
    void setIssuer(@Nullable final Issuer newIssuer);

    /**
     * Gets the Extensions of this request.
     * 
     * @return the Status of this request
     */
    @Nullable Extensions getExtensions();

    /**
     * Sets the Extensions of this request.
     * 
     * @param newExtensions the Extensions of this request
     */
    void setExtensions(@Nullable final Extensions newExtensions);

}