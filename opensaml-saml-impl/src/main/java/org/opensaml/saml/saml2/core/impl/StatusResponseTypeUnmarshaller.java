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
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.xmlsec.signature.Signature;
import org.w3c.dom.Attr;

import com.google.common.base.Strings;

import net.shibboleth.shared.xml.DOMTypeSupport;

/**
 * A thread-safe Unmarshaller for {@link StatusResponseType} objects.
 */
public abstract class StatusResponseTypeUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final StatusResponseType sr = (StatusResponseType) parentObject;
    
        if (childObject instanceof Issuer) {
            sr.setIssuer((Issuer) childObject);
        } else if (childObject instanceof Signature) {
            sr.setSignature((Signature) childObject);
        } else if (childObject instanceof Extensions) {
            sr.setExtensions((Extensions) childObject);
        } else if (childObject instanceof Status) {
            sr.setStatus((Status) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final StatusResponseType sr = (StatusResponseType) xmlObject;

        if (attribute.getNamespaceURI() == null) {
            if (attribute.getLocalName().equals(StatusResponseType.VERSION_ATTRIB_NAME)) {
                sr.setVersion(parseSAMLVersion(attribute));
            } else if (attribute.getLocalName().equals(StatusResponseType.ID_ATTRIB_NAME)) {
                sr.setID(attribute.getValue());
                attribute.getOwnerElement().setIdAttributeNode(attribute, true);
            } else if (attribute.getLocalName().equals(StatusResponseType.IN_RESPONSE_TO_ATTRIB_NAME)) {
                sr.setInResponseTo(attribute.getValue());
            } else if (attribute.getLocalName().equals(StatusResponseType.ISSUE_INSTANT_ATTRIB_NAME)
                    && !Strings.isNullOrEmpty(attribute.getValue())) {
                sr.setIssueInstant(DOMTypeSupport.stringToInstant(attribute.getValue()));
            } else if (attribute.getLocalName().equals(StatusResponseType.DESTINATION_ATTRIB_NAME)) {
                sr.setDestination(attribute.getValue());
            } else if (attribute.getLocalName().equals(StatusResponseType.CONSENT_ATTRIB_NAME)) {
                sr.setConsent(attribute.getValue());
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}