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

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.metadata.AssertionIDRequestService;
import org.opensaml.saml.saml2.metadata.AuthnAuthorityDescriptor;
import org.opensaml.saml.saml2.metadata.AuthnQueryService;
import org.opensaml.saml.saml2.metadata.NameIDFormat;

/**
 * A thread-safe Unmarshaller for {@link AuthnAuthorityDescriptor} objects.
 */
public class AuthnAuthorityDescriptorUnmarshaller extends RoleDescriptorUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final AuthnAuthorityDescriptor descriptor = (AuthnAuthorityDescriptor) parentObject;

        if (childObject instanceof AuthnQueryService) {
            descriptor.getAuthnQueryServices().add((AuthnQueryService) childObject);
        } else if (childObject instanceof AssertionIDRequestService) {
            descriptor.getAssertionIDRequestServices().add((AssertionIDRequestService) childObject);
        } else if (childObject instanceof NameIDFormat) {
            descriptor.getNameIDFormats().add((NameIDFormat) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

}