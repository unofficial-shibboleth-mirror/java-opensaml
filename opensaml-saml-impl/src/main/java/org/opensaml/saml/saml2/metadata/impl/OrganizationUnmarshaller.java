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
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.OrganizationDisplayName;
import org.opensaml.saml.saml2.metadata.OrganizationName;
import org.opensaml.saml.saml2.metadata.OrganizationURL;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link Organization} objects.
 */
public class OrganizationUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final Organization org = (Organization) parentObject;

        if (childObject instanceof Extensions) {
            org.setExtensions((Extensions) childObject);
        } else if (childObject instanceof OrganizationName) {
            org.getOrganizationNames().add((OrganizationName) childObject);
        } else if (childObject instanceof OrganizationDisplayName) {
            org.getDisplayNames().add((OrganizationDisplayName) childObject);
        } else if (childObject instanceof OrganizationURL) {
            org.getURLs().add((OrganizationURL) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {

        if (attribute.getNamespaceURI() == null) {
            super.processAttribute(xmlObject, attribute);
        } else {
            processUnknownAttribute((Organization) xmlObject, attribute);
        }
    }
    
}