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

package org.opensaml.saml.ext.saml2alg.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for {@link SigningMethod}.
 */
public class SigningMethodUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final SigningMethod signingMethod = (SigningMethod) parentObject;
        signingMethod.getUnknownXMLObjects().add(childObject);
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute) 
            throws UnmarshallingException {
        final SigningMethod signingMethod = (SigningMethod) xmlObject;
        
        if (attribute.getNamespaceURI() == null) {
            if (attribute.getLocalName().equals(SigningMethod.ALGORITHM_ATTRIB_NAME)) {
                signingMethod.setAlgorithm(attribute.getValue());
            } else if (attribute.getLocalName().equals(SigningMethod.MIN_KEY_SIZE_ATTRIB_NAME)) {
                signingMethod.setMinKeySize(Integer.valueOf(attribute.getValue()));
            } else if (attribute.getLocalName().equals(SigningMethod.MAX_KEY_SIZE_ATTRIB_NAME)) {
                signingMethod.setMaxKeySize(Integer.valueOf(attribute.getValue()));
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

}