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

package org.opensaml.saml.saml1.core.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml1.core.AuthorityBinding;
import org.w3c.dom.Attr;

import net.shibboleth.shared.xml.AttributeSupport;

/**
 * A thread-safe Unmarshaller for {@link AuthorityBinding} objects.
 */
public class AuthorityBindingUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject samlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {

        final AuthorityBinding authorityBinding = (AuthorityBinding) samlObject;

        if (attribute.getNamespaceURI() == null) {
            if (AuthorityBinding.AUTHORITYKIND_ATTRIB_NAME.equals(attribute.getLocalName())) {
                authorityBinding.setAuthorityKind(AttributeSupport.getAttributeValueAsQName(attribute));
            } else if (AuthorityBinding.LOCATION_ATTRIB_NAME.equals(attribute.getLocalName())) {
                authorityBinding.setLocation(attribute.getValue());
            } else if (AuthorityBinding.BINDING_ATTRIB_NAME.equals(attribute.getLocalName())) {
                authorityBinding.setBinding(attribute.getValue());
            } else {
                super.processAttribute(samlObject, attribute);
            }
        } else {
            super.processAttribute(samlObject, attribute);
        }
    }
    
}