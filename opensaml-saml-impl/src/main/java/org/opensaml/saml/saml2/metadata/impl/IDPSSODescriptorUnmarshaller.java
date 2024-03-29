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

package org.opensaml.saml.saml2.metadata.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.metadata.AssertionIDRequestService;
import org.opensaml.saml.saml2.metadata.AttributeProfile;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.NameIDMappingService;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.w3c.dom.Attr;

/**
 * A thread safe Unmarshaller for {@link IDPSSODescriptor} objects.
 */
public class IDPSSODescriptorUnmarshaller extends SSODescriptorUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final IDPSSODescriptor descriptor = (IDPSSODescriptor) parentObject;

        if (childObject instanceof SingleSignOnService) {
            descriptor.getSingleSignOnServices().add((SingleSignOnService) childObject);
        } else if (childObject instanceof NameIDMappingService) {
            descriptor.getNameIDMappingServices().add((NameIDMappingService) childObject);
        } else if (childObject instanceof AssertionIDRequestService) {
            descriptor.getAssertionIDRequestServices().add((AssertionIDRequestService) childObject);
        } else if (childObject instanceof AttributeProfile) {
            descriptor.getAttributeProfiles().add((AttributeProfile) childObject);
        } else if (childObject instanceof Attribute) {
            descriptor.getAttributes().add((Attribute) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final IDPSSODescriptor descriptor = (IDPSSODescriptor) xmlObject;

        if (attribute.getLocalName().equals(IDPSSODescriptor.WANT_AUTHN_REQ_SIGNED_ATTRIB_NAME)
                && attribute.getNamespaceURI() == null) {
            descriptor.setWantAuthnRequestsSigned(XSBooleanValue.valueOf(attribute.getValue()));
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}