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
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml2.metadata.EncryptionMethod;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.w3c.dom.Attr;

/**
 * A thread-safe unmarshaller for {@link KeyDescriptor}.
 */
public class KeyDescriptorUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final KeyDescriptor keyDescriptor = (KeyDescriptor) parentObject;

        if (childObject instanceof KeyInfo) {
            keyDescriptor.setKeyInfo((KeyInfo) childObject);
        } else if (childObject instanceof EncryptionMethod) {
            keyDescriptor.getEncryptionMethods().add((EncryptionMethod) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final KeyDescriptor keyDescriptor = (KeyDescriptor) xmlObject;

        if (attribute.getName().equals(KeyDescriptor.USE_ATTRIB_NAME) && attribute.getNamespaceURI() == null) {
            if (UsageType.SIGNING.getValue().equals(attribute.getValue())) {
                keyDescriptor.setUse(UsageType.SIGNING);
            } else if (UsageType.ENCRYPTION.getValue().equals(attribute.getValue())) {
                keyDescriptor.setUse(UsageType.ENCRYPTION);
            } else {
                throw new UnmarshallingException("Invalid key usage type: " + attribute.getValue());
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}