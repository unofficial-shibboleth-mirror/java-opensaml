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

package org.opensaml.saml.saml2.metadata.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml2.common.CacheableSAMLObject;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.common.TimeBoundSAMLObject;
import org.opensaml.saml.saml2.metadata.AdditionalMetadataLocation;
import org.opensaml.saml.saml2.metadata.AffiliationDescriptor;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.xmlsec.signature.Signature;
import org.w3c.dom.Attr;

import com.google.common.base.Strings;

import net.shibboleth.shared.xml.DOMTypeSupport;

/**
 * A thread safe Unmarshaller for {@link EntityDescriptor}s.
 */
public class EntityDescriptorUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final EntityDescriptor entityDescriptor = (EntityDescriptor) parentObject;

        if (childObject instanceof Extensions) {
            entityDescriptor.setExtensions((Extensions) childObject);
        } else if (childObject instanceof Signature) {
            entityDescriptor.setSignature((Signature) childObject);
        } else if (childObject instanceof RoleDescriptor) {
            entityDescriptor.getRoleDescriptors().add((RoleDescriptor) childObject);
        } else if (childObject instanceof AffiliationDescriptor) {
            entityDescriptor.setAffiliationDescriptor((AffiliationDescriptor) childObject);
        } else if (childObject instanceof Organization) {
            entityDescriptor.setOrganization((Organization) childObject);
        } else if (childObject instanceof ContactPerson) {
            entityDescriptor.getContactPersons().add((ContactPerson) childObject);
        } else if (childObject instanceof AdditionalMetadataLocation) {
            entityDescriptor.getAdditionalMetadataLocations().add((AdditionalMetadataLocation) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final EntityDescriptor entityDescriptor = (EntityDescriptor) xmlObject;

        if (attribute.getNamespaceURI() == null) {
            if (attribute.getLocalName().equals(EntityDescriptor.ENTITY_ID_ATTRIB_NAME)) {
                entityDescriptor.setEntityID(attribute.getValue());
            } else if (attribute.getLocalName().equals(EntityDescriptor.ID_ATTRIB_NAME)) {
                entityDescriptor.setID(attribute.getValue());
                attribute.getOwnerElement().setIdAttributeNode(attribute, true);
            } else if (attribute.getLocalName().equals(TimeBoundSAMLObject.VALID_UNTIL_ATTRIB_NAME)
                    && !Strings.isNullOrEmpty(attribute.getValue())) {
                entityDescriptor.setValidUntil(DOMTypeSupport.stringToInstant(attribute.getValue()));
            } else if (attribute.getLocalName().equals(CacheableSAMLObject.CACHE_DURATION_ATTRIB_NAME)) {
                entityDescriptor.setCacheDuration(DOMTypeSupport.stringToDuration(attribute.getValue()));
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            processUnknownAttribute(entityDescriptor, attribute);
        }
    }
}