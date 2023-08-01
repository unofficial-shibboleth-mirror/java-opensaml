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

package org.opensaml.saml.saml2.core;

import java.time.Instant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Core AuthnStatement.
 */
public interface AuthnStatement extends Statement {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "AuthnStatement";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "AuthnStatementType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** AuthnInstant attribute name. */
    @Nonnull @NotEmpty static final String AUTHN_INSTANT_ATTRIB_NAME = "AuthnInstant";

    /** AuthnInstant attribute QName. */
    @Nonnull static final QName AUTHN_INSTANT_ATTRIB_QNAME =
            new QName(null, "AuthnInstant", XMLConstants.DEFAULT_NS_PREFIX);
    
    /** SessionIndex attribute name. */
    @Nonnull @NotEmpty static final String SESSION_INDEX_ATTRIB_NAME = "SessionIndex";

    /** SessionNoOnOrAfter attribute name. */
    @Nonnull @NotEmpty static final String SESSION_NOT_ON_OR_AFTER_ATTRIB_NAME = "SessionNotOnOrAfter";

    /** SessionNotOnOrAfter attribute QName. */
    @Nonnull static final QName SESSION_NOT_ON_OR_AFTER_ATTRIB_QNAME =
            new QName(null, "SessionNotOnOrAfter", XMLConstants.DEFAULT_NS_PREFIX);

    /**
     * Gets the time when the authentication took place.
     * 
     * @return the time when the authentication took place
     */
    @Nullable Instant getAuthnInstant();

    /**
     * Sets the time when the authentication took place.
     * 
     * @param newAuthnInstant the time when the authentication took place
     */
    void setAuthnInstant(@Nullable final Instant newAuthnInstant);

    /**
     * Get the session index between the principal and the authenticating authority.
     * 
     * @return the session index between the principal and the authenticating authority
     */
    @Nullable String getSessionIndex();

    /**
     * Sets the session index between the principal and the authenticating authority.
     * 
     * @param newIndex the session index between the principal and the authenticating authority
     */
    void setSessionIndex(@Nullable final String newIndex);

    /**
     * Get the time when the session between the principal and the SAML authority ends.
     * 
     * @return the time when the session between the principal and the SAML authority ends
     */
    @Nullable Instant getSessionNotOnOrAfter();

    /**
     * Set the time when the session between the principal and the SAML authority ends.
     * 
     * @param newSessionNotOnOrAfter the time when the session between the principal and the SAML authority ends
     */
    void setSessionNotOnOrAfter(@Nullable final Instant newSessionNotOnOrAfter);

    /**
     * Get the DNS domain and IP address of the system where the principal was authenticated.
     * 
     * @return the DNS domain and IP address of the system where the principal was authenticated
     */
    @Nullable SubjectLocality getSubjectLocality();

    /**
     * Set the DNS domain and IP address of the system where the principal was authenticated.
     * 
     * @param newLocality the DNS domain and IP address of the system where the principal was authenticated
     */
    void setSubjectLocality(@Nullable final SubjectLocality newLocality);

    /**
     * Gets the context used to authenticate the subject.
     * 
     * @return the context used to authenticate the subject
     */
    @Nullable AuthnContext getAuthnContext();

    /**
     * Sets the context used to authenticate the subject.
     * 
     * @param newAuthnContext the context used to authenticate the subject
     */
    void setAuthnContext(@Nullable final AuthnContext newAuthnContext);
}