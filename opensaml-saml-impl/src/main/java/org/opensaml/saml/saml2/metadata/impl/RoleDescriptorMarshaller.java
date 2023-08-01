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

package org.opensaml.saml.saml2.metadata.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.saml2.common.CacheableSAMLObject;
import org.opensaml.saml.saml2.common.TimeBoundSAMLObject;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.w3c.dom.Element;

import net.shibboleth.shared.xml.AttributeSupport;

/**
 * A thread safe Marshaller for {@link RoleDescriptor} objects.
 */
public abstract class RoleDescriptorMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final RoleDescriptor roleDescriptor = (RoleDescriptor) xmlObject;

        // Set the ID attribute
        if (roleDescriptor.getID() != null) {
            domElement.setAttributeNS(null, RoleDescriptor.ID_ATTRIB_NAME, roleDescriptor.getID());
        }

        // Set the validUntil attribute
        final Instant i = roleDescriptor.getValidUntil();
        if (i != null) {
            AttributeSupport.appendDateTimeAttribute(domElement, TimeBoundSAMLObject.VALID_UNTIL_ATTRIB_QNAME, i);
        }

        // Set the cacheDuration attribute
        final Duration d = roleDescriptor.getCacheDuration();
        if (d != null) {
            AttributeSupport.appendDurationAttribute(domElement, CacheableSAMLObject.CACHE_DURATION_ATTRIB_QNAME, d);
        }

        // Set the protocolSupportEnumeration attribute
        final List<String> supportedProtocols = roleDescriptor.getSupportedProtocols();
        if (supportedProtocols != null && !supportedProtocols.isEmpty()) {

            final StringBuilder builder = new StringBuilder();
            for (final String protocol : supportedProtocols) {
                builder.append(protocol);
                builder.append(" ");
            }

            domElement.setAttributeNS(null, RoleDescriptor.PROTOCOL_ENUMERATION_ATTRIB_NAME, builder.toString().trim());
        }

        // Set errorURL attribute
        if (roleDescriptor.getErrorURL() != null) {
            domElement.setAttributeNS(null, RoleDescriptor.ERROR_URL_ATTRIB_NAME, roleDescriptor.getErrorURL());
        }

        marshallUnknownAttributes(roleDescriptor, domElement);
    }

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributeIDness(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {

        XMLObjectSupport.marshallAttributeIDness(null, RoleDescriptor.ID_ATTRIB_NAME, domElement, true);

        super.marshallAttributeIDness(xmlObject, domElement);
    }

}