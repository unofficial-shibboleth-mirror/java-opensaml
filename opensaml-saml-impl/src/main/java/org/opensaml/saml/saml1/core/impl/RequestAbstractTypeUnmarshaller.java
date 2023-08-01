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

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml1.core.RequestAbstractType;
import org.opensaml.saml.saml1.core.RespondWith;
import org.opensaml.xmlsec.signature.Signature;
import org.slf4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.xml.DOMTypeSupport;

/**
 * A thread safe Unmarshaller for {@link RequestAbstractType} objects.
 */
public abstract class RequestAbstractTypeUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(RequestAbstractType.class);

    /** {@inheritDoc} */
    @Override
    @Nonnull public XMLObject unmarshall(@Nonnull final Element domElement) throws UnmarshallingException {
        // After regular unmarshalling, check the minor version and set ID-ness if not SAML 1.0
        final RequestAbstractType request = (RequestAbstractType) super.unmarshall(domElement);
        if (request.getVersion() != SAMLVersion.VERSION_10 && !Strings.isNullOrEmpty(request.getID())) {
            XMLObjectSupport.marshallAttributeIDness(null, RequestAbstractType.ID_ATTRIB_NAME, domElement, true);
        }
        return request;
    }

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final RequestAbstractType request = (RequestAbstractType) parentObject;

        if (childObject instanceof Signature) {
            request.setSignature((Signature) childObject);
        } else if (childObject instanceof RespondWith) {
            request.getRespondWiths().add((RespondWith) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

// Checkstyle: CyclomaticComplexity OFF
    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final RequestAbstractType request = (RequestAbstractType) xmlObject;

        if (attribute.getNamespaceURI() == null) {
            if (RequestAbstractType.ID_ATTRIB_NAME.equals(attribute.getLocalName())) {
                request.setID(attribute.getValue());
            } else if (RequestAbstractType.ISSUEINSTANT_ATTRIB_NAME.equals(attribute.getLocalName())
                    && !Strings.isNullOrEmpty(attribute.getValue())) {
                request.setIssueInstant(DOMTypeSupport.stringToInstant(attribute.getValue()));
            } else if (attribute.getLocalName().equals(RequestAbstractType.MAJORVERSION_ATTRIB_NAME)) {
                final int major;
                try {
                    major = Integer.parseInt(attribute.getValue());
                    if (major != 1) {
                        throw new UnmarshallingException("MajorVersion was invalid, must be 1");
                    }
                } catch (final NumberFormatException n) {
                    log.error("Failed to parse major version string: {}", n.getMessage());
                    throw new UnmarshallingException(n);
                }
            } else if (RequestAbstractType.MINORVERSION_ATTRIB_NAME.equals(attribute.getLocalName())) {
                final int minor;
                try {
                    minor = Integer.parseInt(attribute.getValue());
                } catch (final NumberFormatException n) {
                    log.error("Unable to parse minor version string: {}", n.getMessage());
                    throw new UnmarshallingException(n);
                }
                if (minor == 0) {
                    request.setVersion(SAMLVersion.VERSION_10);
                } else if (minor == 1) {
                    request.setVersion(SAMLVersion.VERSION_11);
                }
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
        
    }
// Checkstyle: CyclomaticComplexity OFF
    
}