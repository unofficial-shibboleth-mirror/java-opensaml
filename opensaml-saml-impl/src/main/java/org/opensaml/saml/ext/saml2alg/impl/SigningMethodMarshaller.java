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
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.w3c.dom.Element;

/**
 * Marshaller for {@link SigningMethod}.
 */
public class SigningMethodMarshaller extends AbstractSAMLObjectMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final SigningMethod signingMethod = (SigningMethod) xmlObject;
        
        if (signingMethod.getAlgorithm() != null) {
            domElement.setAttributeNS(null, SigningMethod.ALGORITHM_ATTRIB_NAME, signingMethod.getAlgorithm());
        }
        
        Integer size = signingMethod.getMinKeySize();
        if (size != null) {
            domElement.setAttributeNS(null, SigningMethod.MIN_KEY_SIZE_ATTRIB_NAME, size.toString());
        }
        
        size = signingMethod.getMaxKeySize();
        if (size != null) {
            domElement.setAttributeNS(null, SigningMethod.MAX_KEY_SIZE_ATTRIB_NAME, size.toString());
        }
    }

}