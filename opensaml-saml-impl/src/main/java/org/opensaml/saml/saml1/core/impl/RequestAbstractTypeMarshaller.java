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

package org.opensaml.saml.saml1.core.impl;

import java.time.Instant;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml1.core.RequestAbstractType;
import org.w3c.dom.Element;

import net.shibboleth.shared.xml.AttributeSupport;

/**
 * A thread safe Marshaller for {@link RequestAbstractType} objects.
 */
public class RequestAbstractTypeMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final RequestAbstractType request = (RequestAbstractType) xmlObject;

        if (request.getID() != null) {
            domElement.setAttributeNS(null, RequestAbstractType.ID_ATTRIB_NAME, request.getID());
        }

        final Instant i = request.getIssueInstant();
        if (i != null) {
            AttributeSupport.appendDateTimeAttribute(domElement, RequestAbstractType.ISSUEINSTANT_ATTRIB_QNAME, i);
        }

        final SAMLVersion version = request.getVersion();
        if (version != null) {
            domElement.setAttributeNS(null, RequestAbstractType.MAJORVERSION_ATTRIB_NAME,
                    Integer.toString(version.getMajorVersion()));
            domElement.setAttributeNS(null, RequestAbstractType.MINORVERSION_ATTRIB_NAME,
                    Integer.toString(version.getMinorVersion()));
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributeIDness(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {

        if (((RequestAbstractType)xmlObject).getVersion() != SAMLVersion.VERSION_10) {
            XMLObjectSupport.marshallAttributeIDness(null, RequestAbstractType.ID_ATTRIB_NAME, domElement, true);
        }
    }

}