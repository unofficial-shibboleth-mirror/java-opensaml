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

import java.time.Duration;
import java.time.Instant;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.saml2.common.CacheableSAMLObject;
import org.opensaml.saml.saml2.common.TimeBoundSAMLObject;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.w3c.dom.Element;

import net.shibboleth.shared.xml.AttributeSupport;

/**
 * A thread safe Marshaller for {@link EntityDescriptor} objects.
 */
public class EntityDescriptorMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {

        final EntityDescriptor entityDescriptor = (EntityDescriptor) xmlObject;

        // Set the entityID attribute
        if (entityDescriptor.getEntityID() != null) {
            domElement.setAttributeNS(null, EntityDescriptor.ENTITY_ID_ATTRIB_NAME, entityDescriptor.getEntityID());
        }

        // Set the ID attribute
        if (entityDescriptor.getID() != null) {
            domElement.setAttributeNS(null, EntityDescriptor.ID_ATTRIB_NAME, entityDescriptor.getID());
        }

        // Set the validUntil attribute
        final Instant i = entityDescriptor.getValidUntil();
        if (i != null) {
            AttributeSupport.appendDateTimeAttribute(domElement, TimeBoundSAMLObject.VALID_UNTIL_ATTRIB_QNAME, i);
        }

        // Set the cacheDuration attribute
        final Duration d = entityDescriptor.getCacheDuration();
        if (d != null) {
            AttributeSupport.appendDurationAttribute(domElement, CacheableSAMLObject.CACHE_DURATION_ATTRIB_QNAME, d);
        }

        marshallUnknownAttributes(entityDescriptor, domElement);
    }

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributeIDness(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {

        XMLObjectSupport.marshallAttributeIDness(null, EntityDescriptor.ID_ATTRIB_NAME, domElement, true);
        
        super.marshallAttributeIDness(xmlObject, domElement);
    }

}