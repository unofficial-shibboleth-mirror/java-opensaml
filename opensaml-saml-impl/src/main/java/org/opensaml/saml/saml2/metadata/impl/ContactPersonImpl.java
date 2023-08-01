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

package org.opensaml.saml.saml2.metadata.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.Company;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.ContactPersonTypeEnumeration;
import org.opensaml.saml.saml2.metadata.EmailAddress;
import org.opensaml.saml.saml2.metadata.GivenName;
import org.opensaml.saml.saml2.metadata.SurName;
import org.opensaml.saml.saml2.metadata.TelephoneNumber;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link ContactPerson}.
 */
public class ContactPersonImpl extends AbstractXMLObject implements ContactPerson {

    /** Contact person type. */
    @Nullable private ContactPersonTypeEnumeration type;

    /** Extensions child object. */
    @Nullable private Extensions extensions;

    /** Company child element. */
    @Nullable private Company company;

    /** GivenName child objectobject. */
    @Nullable private GivenName givenName;

    /** SurName child object. */
    @Nullable private SurName surName;
    
    /** "anyAttribute" attributes. */
    @Nonnull private final AttributeMap unknownAttributes;

    /** Child email address. */
    @Nonnull private final XMLObjectChildrenList<EmailAddress> emailAddresses;

    /** Child telephone numbers. */
    @Nonnull private final XMLObjectChildrenList<TelephoneNumber> telephoneNumbers;

    /**
     * Constructor.
     * 
     * @param namespaceURI name space
     * @param elementLocalName local name
     * @param namespacePrefix prefix
     */
    protected ContactPersonImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
        emailAddresses = new XMLObjectChildrenList<>(this);
        telephoneNumbers = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public ContactPersonTypeEnumeration getType() {
        return type;
    }

    /** {@inheritDoc} */
    public void setType(@Nullable final ContactPersonTypeEnumeration theType) {
        type = prepareForAssignment(type, theType);
    }

    /** {@inheritDoc} */
    @Nullable public Extensions getExtensions() {
        return extensions;
    }

    /** {@inheritDoc} */
    public void setExtensions(@Nullable final Extensions theExtensions) {
        extensions = prepareForAssignment(extensions, theExtensions);
    }

    /** {@inheritDoc} */
    @Nullable public Company getCompany() {
        return company;
    }

    /** {@inheritDoc} */
    public void setCompany(@Nullable final Company theCompany) {
        company = prepareForAssignment(company, theCompany);
    }

    /** {@inheritDoc} */
    @Nullable public GivenName getGivenName() {
        return givenName;
    }

    /** {@inheritDoc} */
    public void setGivenName(@Nullable final GivenName name) {
        givenName = prepareForAssignment(givenName, name);
    }

    /** {@inheritDoc} */
    @Nullable public SurName getSurName() {
        return surName;
    }

    /** {@inheritDoc} */
    public void setSurName(@Nullable final SurName name) {
        surName = prepareForAssignment(surName, name);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<EmailAddress> getEmailAddresses() {
        return emailAddresses;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<TelephoneNumber> getTelephoneNumbers() {
        return telephoneNumbers;
    }
    
    /** {@inheritDoc} */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        if (extensions != null) {
            children.add(extensions);
        }
        
        if (company != null) {
            children.add(company);
        }
        
        if (givenName != null) {
            children.add(givenName);
        }
        
        if (surName != null) {
            children.add(surName);
        }
        
        children.addAll(emailAddresses);
        children.addAll(telephoneNumbers);

        return CollectionSupport.copyToList(children);
    }
    
}