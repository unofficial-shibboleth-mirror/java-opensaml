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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSString;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/** Interface to define how a NameIdentifier element behaves. */
public interface NameIdentifier extends SAMLObject, XSString {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "NameIdentifier";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML1_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "NameIdentifierType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML1_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);

    /** Name for the attribute which defines Name Qualifier. */
    @Nonnull @NotEmpty static final String NAMEQUALIFIER_ATTRIB_NAME = "NameQualifier";

    /** Name for the attribute which defines Name Qualifier. */
    @Nonnull @NotEmpty static final String FORMAT_ATTRIB_NAME = "Format";

    /** URI for unspecified name format. */
    @Nonnull @NotEmpty static final String UNSPECIFIED = "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified";

    /** URI for email name format. */
    @Nonnull @NotEmpty static final String EMAIL = "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress";

    /** URI for X509 subject name format. */
    @Nonnull @NotEmpty
    static final String X509_SUBJECT = "urn:oasis:names:tc:SAML:1.1:nameid-format:X509SubjectName";

    /** URI for windows domain qualified name name format. */
    @Nonnull @NotEmpty static final String WIN_DOMAIN_QUALIFIED =
            "urn:oasis:names:tc:SAML:1.1:nameid-format:WindowsDomainQualifiedName";
    
    /**
     * Gets the name qualifier for this identifier.
     * 
     * @return name qualifier for this identifier
     */
    @Nullable String getNameQualifier();

    /**
     * Sets the name qualifier for this identifier.
     * 
     * @param nameQualifier name qualifier for this identifier
     */
    void setNameQualifier(@Nullable final String nameQualifier);

    /**
     * Gets the format of this identifier.
     * 
     * @return format of this identifier
     */
    @Nullable String getFormat();

    /**
     * Sets the format of this identifier.
     * 
     * @param format format of this identifier
     */
    void setFormat(@Nullable final String format);

}