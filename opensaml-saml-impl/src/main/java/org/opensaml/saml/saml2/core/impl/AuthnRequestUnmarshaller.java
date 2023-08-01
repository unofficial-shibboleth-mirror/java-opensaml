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
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.Scoping;
import org.opensaml.saml.saml2.core.Subject;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link AuthnRequest} objects.
 */
public class AuthnRequestUnmarshaller extends RequestAbstractTypeUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final AuthnRequest req = (AuthnRequest) parentObject;
    
        if (childObject instanceof Subject) {
            req.setSubject((Subject) childObject);
        } else if (childObject instanceof NameIDPolicy) {
            req.setNameIDPolicy((NameIDPolicy) childObject);
        } else if (childObject instanceof Conditions) {
            req.setConditions((Conditions) childObject);
        } else if (childObject instanceof RequestedAuthnContext) {
            req.setRequestedAuthnContext((RequestedAuthnContext) childObject);
        } else if (childObject instanceof Scoping) {
            req.setScoping((Scoping) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final AuthnRequest req = (AuthnRequest) xmlObject;

        if (attribute.getNamespaceURI() == null) {
            if (attribute.getLocalName().equals(AuthnRequest.FORCE_AUTHN_ATTRIB_NAME)) {
                req.setForceAuthn(XSBooleanValue.valueOf(attribute.getValue()));
            } else if (attribute.getLocalName().equals(AuthnRequest.IS_PASSIVE_ATTRIB_NAME)) {
                req.setIsPassive(XSBooleanValue.valueOf(attribute.getValue()));
            } else if (attribute.getLocalName().equals(AuthnRequest.PROTOCOL_BINDING_ATTRIB_NAME)) {
                req.setProtocolBinding(attribute.getValue());
            } else if (attribute.getLocalName().equals(AuthnRequest.ASSERTION_CONSUMER_SERVICE_INDEX_ATTRIB_NAME)) {
                req.setAssertionConsumerServiceIndex(Integer.valueOf(attribute.getValue()));
            } else if (attribute.getLocalName().equals(AuthnRequest.ASSERTION_CONSUMER_SERVICE_URL_ATTRIB_NAME)) {
                req.setAssertionConsumerServiceURL(attribute.getValue());
            } else if (attribute.getLocalName().equals(AuthnRequest.ATTRIBUTE_CONSUMING_SERVICE_INDEX_ATTRIB_NAME)) {
                req.setAttributeConsumingServiceIndex(Integer.valueOf(attribute.getValue()));
            } else if (attribute.getLocalName().equals(AuthnRequest.PROVIDER_NAME_ATTRIB_NAME)) {
                req.setProviderName(attribute.getValue());
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}