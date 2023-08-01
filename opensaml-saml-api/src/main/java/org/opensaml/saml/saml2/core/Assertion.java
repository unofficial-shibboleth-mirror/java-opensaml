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
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.Live;

/**
 * SAML 2.0 Core Assertion.
 */
public interface Assertion extends SignableSAMLObject, Evidentiary {

    /** Element local name. */
    @Nonnull static final String DEFAULT_ELEMENT_LOCAL_NAME = "Assertion";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull static final String TYPE_LOCAL_NAME = "AssertionType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** Version attribute name. */
    @Nonnull static final String VERSION_ATTRIB_NAME = "Version";

    /** IssueInstant attribute name. */
    @Nonnull static final String ISSUE_INSTANT_ATTRIB_NAME = "IssueInstant";

    /** IssueInstant attribute QName. */
    @Nonnull static final QName ISSUEINSTANT_ATTRIB_QNAME =
            new QName(null, "IssueInstant", XMLConstants.DEFAULT_NS_PREFIX);
    
    /** ID attribute name. */
    @Nonnull static final String ID_ATTRIB_NAME = "ID";

    /**
     * Gets the SAML Version of this assertion.
     * 
     * @return the SAML Version of this assertion.
     */
    @Nullable SAMLVersion getVersion();

    /**
     * Sets the SAML Version of this assertion.
     * 
     * @param newVersion the SAML Version of this assertion
     */
    void setVersion(@Nullable final SAMLVersion newVersion);

    /**
     * Gets the issue instance of this assertion.
     * 
     * @return the issue instance of this assertion
     */
    @Nullable Instant getIssueInstant();

    /**
     * Sets the issue instance of this assertion.
     * 
     * @param newIssueInstance the issue instance of this assertion
     */
    void setIssueInstant(@Nullable final Instant newIssueInstance);

    /**
     * Sets the ID of this assertion.
     * 
     * @return the ID of this assertion
     */
    @Nullable String getID();

    /**
     * Sets the ID of this assertion.
     * 
     * @param newID the ID of this assertion
     */
    void setID(@Nullable final String newID);

    /**
     * Gets the Issuer of this assertion.
     * 
     * @return the Issuer of this assertion
     */
    @Nullable Issuer getIssuer();

    /**
     * Sets the Issuer of this assertion.
     * 
     * @param newIssuer the Issuer of this assertion
     */
    void setIssuer(@Nullable final Issuer newIssuer);

    /**
     * Gets the Subject of this assertion.
     * 
     * @return the Subject of this assertion
     */
    @Nullable Subject getSubject();

    /**
     * Sets the Subject of this assertion.
     * 
     * @param newSubject the Subject of this assertion
     */
    void setSubject(@Nullable final Subject newSubject);

    /**
     * Gets the Conditions placed on this assertion.
     * 
     * @return the Conditions placed on this assertion
     */
    @Nullable Conditions getConditions();

    /**
     * Sets the Conditions placed on this assertion.
     * 
     * @param newConditions the Conditions placed on this assertion
     */
    void setConditions(@Nullable final Conditions newConditions);

    /**
     * Gets the Advice for this assertion.
     * 
     * @return the Advice for this assertion
     */
    @Nullable Advice getAdvice();

    /**
     * Sets the Advice for this assertion.
     * 
     * @param newAdvice the Advice for this assertion
     */
    void setAdvice(@Nullable final Advice newAdvice);

    /**
     * Gets the list of statements attached to this assertion.
     * 
     * @return the list of statements attached to this assertion
     */
    @Nonnull @Live List<Statement> getStatements();

    /**
     * Gets the list of statements attached to this assertion that match a particular QName.
     * 
     * @param typeOrName the QName of the statements to return
     * @return the list of statements attached to this assertion
     */
    @Nonnull @Live List<Statement> getStatements(@Nonnull final QName typeOrName);

    /**
     * Gets the list of AuthnStatements attached to this assertion.
     * 
     * @return the list of AuthnStatements attached to this assertion
     */
    @Nonnull @Live List<AuthnStatement> getAuthnStatements();

    /**
     * Gets the list of AuthzDecisionStatements attached to this assertion.
     * 
     * @return the list of AuthzDecisionStatements attached to this assertion
     */
    @Nonnull @Live List<AuthzDecisionStatement> getAuthzDecisionStatements();

    /**
     * Gets the list of AttributeStatement attached to this assertion.
     * 
     * @return the list of AttributeStatement attached to this assertion
     */
    @Nonnull @Live List<AttributeStatement> getAttributeStatements();

}