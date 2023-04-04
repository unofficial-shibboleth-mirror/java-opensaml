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

/**
 * 
 */

package org.opensaml.saml.saml2.metadata.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.Company;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.ContactPersonTypeEnumeration;
import org.opensaml.saml.saml2.metadata.EmailAddress;
import org.opensaml.saml.saml2.metadata.GivenName;
import org.opensaml.saml.saml2.metadata.SurName;
import org.opensaml.saml.saml2.metadata.TelephoneNumber;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link ContactPerson} objects.
 */
public class ContactPersonUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final ContactPerson person = (ContactPerson) parentObject;

        if (childObject instanceof Extensions) {
            person.setExtensions((Extensions) childObject);
        } else if (childObject instanceof Company) {
            person.setCompany((Company) childObject);
        } else if (childObject instanceof GivenName) {
            person.setGivenName((GivenName) childObject);
        } else if (childObject instanceof SurName) {
            person.setSurName((SurName) childObject);
        } else if (childObject instanceof EmailAddress) {
            person.getEmailAddresses().add((EmailAddress) childObject);
        } else if (childObject instanceof TelephoneNumber) {
            person.getTelephoneNumbers().add((TelephoneNumber) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final ContactPerson person = (ContactPerson) xmlObject;

        if (attribute.getNamespaceURI() == null) {
            if (attribute.getLocalName().equals(ContactPerson.CONTACT_TYPE_ATTRIB_NAME)) {
                try {
                    if (attribute.getValue() != null) {
                        person.setType(ContactPersonTypeEnumeration.valueOf(attribute.getValue().toUpperCase()));
                    } else {
                        throw new UnmarshallingException("Saw an empty value for contactType attribute");
                    }
                } catch (final IllegalArgumentException e) {
                    throw new UnmarshallingException("Saw an invalid value for contactType attribute: "
                            + attribute.getValue());
                }
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            processUnknownAttribute(person, attribute);
        }
    }
    
}