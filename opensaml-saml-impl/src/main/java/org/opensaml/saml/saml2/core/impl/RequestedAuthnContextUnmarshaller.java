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
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnContextDeclRef;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link RequestedAuthnContext} objects.
 */
public class RequestedAuthnContextUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final RequestedAuthnContext rac = (RequestedAuthnContext) parentObject;
        
        if (childObject instanceof AuthnContextClassRef) {
            rac.getAuthnContextClassRefs().add((AuthnContextClassRef) childObject);
        } else if (childObject instanceof AuthnContextDeclRef) {
            rac.getAuthnContextDeclRefs().add((AuthnContextDeclRef) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final RequestedAuthnContext rac = (RequestedAuthnContext) xmlObject;

        if (attribute.getLocalName().equals(RequestedAuthnContext.COMPARISON_ATTRIB_NAME)
                && attribute.getNamespaceURI() == null) {
            try {
                if (attribute.getValue() != null) {
                    rac.setComparison(
                            AuthnContextComparisonTypeEnumeration.valueOf(attribute.getValue().toUpperCase()));
                } else {
                    throw new UnmarshallingException("Saw an empty value for Comparison attribute");
                }
            } catch (final IllegalArgumentException e) {
                throw new UnmarshallingException("Saw an invalid value for Comparison attribute: "
                        + attribute.getValue());
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}