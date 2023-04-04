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

import java.util.StringTokenizer;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml2.common.CacheableSAMLObject;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.common.TimeBoundSAMLObject;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.xmlsec.signature.Signature;
import org.w3c.dom.Attr;

import com.google.common.base.Strings;

import net.shibboleth.shared.xml.DOMTypeSupport;

/**
 * A thread safe Unmarshaller for {@link RoleDescriptor} objects.
 */
public abstract class RoleDescriptorUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final RoleDescriptor roleDescriptor = (RoleDescriptor) parentObject;

        if (childObject instanceof Extensions) {
            roleDescriptor.setExtensions((Extensions) childObject);
        } else if (childObject instanceof Signature) {
            roleDescriptor.setSignature((Signature) childObject);
        } else if (childObject instanceof KeyDescriptor) {
            roleDescriptor.getKeyDescriptors().add((KeyDescriptor) childObject);
        } else if (childObject instanceof Organization) {
            roleDescriptor.setOrganization((Organization) childObject);
        } else if (childObject instanceof ContactPerson) {
            roleDescriptor.getContactPersons().add((ContactPerson) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final RoleDescriptor roleDescriptor = (RoleDescriptor) xmlObject;

        if (attribute.getNamespaceURI() == null) {
            if (attribute.getLocalName().equals(RoleDescriptor.ID_ATTRIB_NAME)) {
                roleDescriptor.setID(attribute.getValue());
                attribute.getOwnerElement().setIdAttributeNode(attribute, true);
            } else if (attribute.getLocalName().equals(TimeBoundSAMLObject.VALID_UNTIL_ATTRIB_NAME)
                    && !Strings.isNullOrEmpty(attribute.getValue())) {
                roleDescriptor.setValidUntil(DOMTypeSupport.stringToInstant(attribute.getValue()));
            } else if (attribute.getLocalName().equals(CacheableSAMLObject.CACHE_DURATION_ATTRIB_NAME)) {
                roleDescriptor.setCacheDuration(DOMTypeSupport.stringToDuration(attribute.getValue()));
            } else if (attribute.getLocalName().equals(RoleDescriptor.PROTOCOL_ENUMERATION_ATTRIB_NAME)) {
                final StringTokenizer protocolTokenizer = new StringTokenizer(attribute.getValue(), " ");
                while (protocolTokenizer.hasMoreTokens()) {
                    roleDescriptor.addSupportedProtocol(protocolTokenizer.nextToken());
                }
            } else if (attribute.getLocalName().equals(RoleDescriptor.ERROR_URL_ATTRIB_NAME)) {
                roleDescriptor.setErrorURL(attribute.getValue());
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            processUnknownAttribute(roleDescriptor, attribute);
        }
    }
    
}