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

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml2.core.Extensions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.xmlsec.signature.Signature;
import org.w3c.dom.Attr;

import com.google.common.base.Strings;

import net.shibboleth.shared.xml.DOMTypeSupport;

/**
 * A thread-safe Unmarshaller for {@link RequestAbstractType} objects.
 */
public abstract class RequestAbstractTypeUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final RequestAbstractType req = (RequestAbstractType) parentObject;
    
        if (childObject instanceof Issuer) {
            req.setIssuer((Issuer) childObject);
        } else if (childObject instanceof Signature) {
            req.setSignature((Signature) childObject);
        } else if (childObject instanceof Extensions) {
            req.setExtensions((Extensions) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final RequestAbstractType req = (RequestAbstractType) xmlObject;

        if (attribute.getNamespaceURI() == null) {
            if (attribute.getLocalName().equals(RequestAbstractType.VERSION_ATTRIB_NAME)) {
                req.setVersion(parseSAMLVersion(attribute));
            } else if (attribute.getLocalName().equals(RequestAbstractType.ID_ATTRIB_NAME)) {
                req.setID(attribute.getValue());
                attribute.getOwnerElement().setIdAttributeNode(attribute, true);
            } else if (attribute.getLocalName().equals(RequestAbstractType.ISSUE_INSTANT_ATTRIB_NAME)
                    && !Strings.isNullOrEmpty(attribute.getValue())) {
                req.setIssueInstant(DOMTypeSupport.stringToInstant(attribute.getValue()));
            } else if (attribute.getLocalName().equals(RequestAbstractType.DESTINATION_ATTRIB_NAME)) {
                req.setDestination(attribute.getValue());
            } else if (attribute.getLocalName().equals(RequestAbstractType.CONSENT_ATTRIB_NAME)) {
                req.setConsent(attribute.getValue());
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}