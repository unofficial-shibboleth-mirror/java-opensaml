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

package org.opensaml.saml.saml1.core;

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
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * This interface defines how the object representing a SAML 1 <code> Assertion </code> element behaves.
 */
public interface Assertion extends SignableSAMLObject, Evidentiary {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "Assertion";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML1_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "AssertionType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML1_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);

    /** Name for the attribute which defines Major Version (attribute's value must be 1). */
    @Nonnull @NotEmpty static final String MAJORVERSION_ATTRIB_NAME = "MajorVersion";

    /** Name for the attribute which defines Minor Version. */
    @Nonnull @NotEmpty static final String MINORVERSION_ATTRIB_NAME = "MinorVersion";

    /** Name for the attribute which defines Assertion ID. */
    @Nonnull @NotEmpty static final String ASSERTIONID_ATTRIB_NAME = "AssertionID";

    /** Name for the attribute which defines Issuer. */
    @Nonnull @NotEmpty static final String ISSUER_ATTRIB_NAME = "Issuer";

    /** Name for the attribute which defines the issue instant. */
    @Nonnull @NotEmpty static final String ISSUEINSTANT_ATTRIB_NAME = "IssueInstant";

    /** QName for the attribute which defines the issue instant. */
    @Nonnull static final QName ISSUEINSTANT_ATTRIB_QNAME =
            new QName(null, "IssueInstant", XMLConstants.DEFAULT_NS_PREFIX);
    
    /** Name for the attribute which defines the Issue Instant. */
    @Nonnull @NotEmpty static final String ID_ATTRIB_NAME = "AssertionID";

    /**
     * Set the ID.
     * 
     * @return the ID
     */
    @Nullable String getID();

    /**
     * Get the ID.
     * 
     * @param id what to set
     */
    void setID(@Nullable final String id);

    /**
     * Get the MajorVersion attribute.
     * 
     * <p>Deprecated, use {@link #getVersion()}.</p>
     * 
     * @return the stored MajorVersion
     * 
     * @deprecated
     */
    @Deprecated(since="5.0.0", forRemoval=true)
    @Nullable Integer getMajorVersion();

    /**
     * Get the MinorVersion attribute.
     * 
     * <p>Deprecated, use {@link #getVersion()}.</p>
     * 
     * @return the stored MinorVersion
     * 
     * @deprecated
     */
    @Deprecated(since="5.0.0", forRemoval=true)
    @Nullable Integer getMinorVersion();

    /**
     * Gets the SAML version for this message.
     * 
     * @return SAML version for this message
     * 
     * @since 5.0.0
     */
    @Nullable SAMLVersion getVersion();
    
    /**
     * Set the SAML version of this assertion.
     * 
     * @param version the SAML version of this assertion
     */
    void setVersion(@Nullable final SAMLVersion version);

    /**
     * Get the Issuer (which is an attribute) .
     * 
     * @return the Issuer
     */
    @Nullable String getIssuer();

    /**
     * Set the Issuer (attribute).
     * 
     * @param issuer the value to set
     */
    void setIssuer(@Nullable final String issuer);

    /**
     * Get the IssueInstant (attribute).
     * 
     * @return the Issue Instant (as a Date)
     */
    @Nullable Instant getIssueInstant();

    /**
     * Set the IssueInstance (attribute).
     * 
     * @param issueInstant the issue instant value to set
     */
    void setIssueInstant(@Nullable final Instant issueInstant);

    /**
     * Return the (singleton) Object, representing the <code> Conditions </code> sub element.
     * 
     * @return the Conditions object.
     */
    @Nullable Conditions getConditions();

    /**
     * Set the Object representing the <code> Conditions </code> Sub element.
     * 
     * @param conditions the condition to List
     */
    void setConditions(@Nullable final Conditions conditions);

    /**
     * advice is a (singleton) Object, representing the <code> Advice </code> sub element.
     * 
     * @return the advice object in this assertion
     */
    @Nullable Advice getAdvice();

    /**
     * Set the Object representing the <code> Advice </code> sub element.
     * 
     * @param advice the object to set
     */
    void setAdvice(@Nullable final Advice advice);

    /**
     * Return the List representing all the <code> Statement </code> sub elements.
     * 
     * @return the List representing all the statements
     */
    @Nonnull @Live List<Statement> getStatements();

    /**
     * Return the List representing all the <code> Statement </code> sub elements with a given schema type or element
     * name.
     * 
     * @param typeOrName the schema type or element name
     * 
     * @return the List representing all the statements
     */
    @Nonnull @Live List<Statement> getStatements(@Nonnull final QName typeOrName);

    /**
     * Return the List representing all the <code> SubjectStatement </code> sub elements.
     * 
     * @return all the SubjectStatements
     */
    @Nonnull @Live List<SubjectStatement> getSubjectStatements();

    /**
     * Return the List representing all the <code> AuthenticationStatement </code> sub elements.
     * 
     * @return all the AuthenticationStatements
     */
    @Nonnull @Live List<AuthenticationStatement> getAuthenticationStatements();

    /**
     * Return the List representing all the <code> AuthorizationStatement </code> sub elements.
     * 
     * @return all the authorizationDecisionStatements.
     */
    @Nonnull @Live List<AuthorizationDecisionStatement> getAuthorizationDecisionStatements();

    /**
     * Return all the <code> AttributeStatement </code> elements.
     * 
     * @return all the attributeStatements
     */
    @Nonnull @Live List<AttributeStatement> getAttributeStatements();
    
}