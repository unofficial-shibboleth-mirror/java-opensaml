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

package org.opensaml.saml.saml2.core.impl;

import java.time.Instant;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.w3c.dom.Element;

import net.shibboleth.shared.xml.AttributeSupport;

/**
 * A thread safe Marshaller for {@link StatusResponseType} objects.
 */
public abstract class StatusResponseTypeMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final StatusResponseType sr = (StatusResponseType) xmlObject;

        final SAMLVersion version = sr.getVersion();
        if (version != null) {
            domElement.setAttributeNS(null, StatusResponseType.VERSION_ATTRIB_NAME, version.toString());
        }

        if (sr.getID() != null) {
            domElement.setAttributeNS(null, StatusResponseType.ID_ATTRIB_NAME, sr.getID());
        }

        if (sr.getInResponseTo() != null) {
            domElement.setAttributeNS(null, StatusResponseType.IN_RESPONSE_TO_ATTRIB_NAME, sr.getInResponseTo());
        }

        final Instant i = sr.getIssueInstant();
        if (i != null) {
            AttributeSupport.appendDateTimeAttribute(domElement, StatusResponseType.ISSUE_INSTANT_ATTRIB_QNAME, i);
        }

        if (sr.getDestination() != null) {
            domElement.setAttributeNS(null, StatusResponseType.DESTINATION_ATTRIB_NAME, sr.getDestination());
        }

        if (sr.getConsent() != null) {
            domElement.setAttributeNS(null, StatusResponseType.CONSENT_ATTRIB_NAME, sr.getConsent());
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributeIDness(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {

        XMLObjectSupport.marshallAttributeIDness(null, StatusResponseType.ID_ATTRIB_NAME, domElement, true);
    }

}