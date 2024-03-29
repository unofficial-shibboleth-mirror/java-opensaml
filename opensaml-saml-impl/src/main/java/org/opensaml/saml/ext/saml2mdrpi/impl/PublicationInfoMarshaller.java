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

package org.opensaml.saml.ext.saml2mdrpi.impl;

import java.time.Instant;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.ext.saml2mdrpi.PublicationInfo;
import org.w3c.dom.Element;

import net.shibboleth.shared.xml.AttributeSupport;


/**
 * A marshaller for {@link PublicationInfo}.
 */
public class PublicationInfoMarshaller extends AbstractSAMLObjectMarshaller {
    
    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final PublicationInfo info = (PublicationInfo) xmlObject;

        if (info.getPublisher() != null) {
            domElement.setAttributeNS(null, PublicationInfo.PUBLISHER_ATTRIB_NAME, info.getPublisher());
        }

        if (info.getPublicationId() != null) {
            domElement.setAttributeNS(null, PublicationInfo.PUBLICATION_ID_ATTRIB_NAME, info.getPublicationId());
        }

        final Instant i = info.getCreationInstant();
        if (i != null) {
            AttributeSupport.appendDateTimeAttribute(domElement, PublicationInfo.CREATION_INSTANT_ATTRIB_QNAME, i);
        }
    }

}