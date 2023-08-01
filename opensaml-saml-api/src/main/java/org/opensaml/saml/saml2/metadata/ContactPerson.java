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

package org.opensaml.saml.saml2.metadata;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Metadata ContactPerson.
 */
public interface ContactPerson extends SAMLObject, AttributeExtensibleXMLObject {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "ContactPerson";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "ContactPersonType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MD_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** "contactType" attribute's local name. */
    @Nonnull @NotEmpty static final String CONTACT_TYPE_ATTRIB_NAME = "contactType";

    /**
     * Gets the type of contact this person.
     * 
     * @return the type of contact this person
     */
    @Nullable ContactPersonTypeEnumeration getType();

    /**
     * Sets the type of contact this person.
     * 
     * @param type the type of contact this person
     */
    void setType(@Nullable final ContactPersonTypeEnumeration type);

    /**
     * Gets the Extensions child of this object.
     * 
     * @return the Extensions child of this object
     */
    @Nullable Extensions getExtensions();

    /**
     * Sets the Extensions child of this object.
     * 
     * @param extensions the Extensions child of this object
     */
    void setExtensions(@Nullable final Extensions extensions);

    /**
     * Gets the company this contact person is associated with.
     * 
     * @return the company this contact person is associated with
     */
    @Nullable Company getCompany();

    /**
     * Sets the company this contact person is associated with.
     * 
     * @param company the company this contact person is associated with
     */
    void setCompany(@Nullable final Company company);

    /**
     * Gets the given name for this person.
     * 
     * @return the given name for this person
     */
    @Nullable GivenName getGivenName();

    /**
     * Sets the given name for this person.
     * 
     * @param name the given name for this person
     */
    void setGivenName(@Nullable final GivenName name);

    /**
     * Gets the surname for this person.
     * 
     * @return the surname for this person
     */
    @Nullable SurName getSurName();

    /**
     * Sets the surname for this person.
     * 
     * @param name the surname for this person
     */
    void setSurName(@Nullable final SurName name);

    /**
     * Gets a list of email addresses for this person.
     * 
     * @return list of email addresses for this person
     */
    @Nonnull @Live List<EmailAddress> getEmailAddresses();

    /**
     * Gets an immutable list of telephone numbers for this person.
     * 
     * @return list of telephone numbers for this person
     */
    @Nonnull @Live List<TelephoneNumber> getTelephoneNumbers();
}
