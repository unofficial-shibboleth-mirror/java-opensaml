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

package org.opensaml.xmlsec.encryption.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xmlsec.encryption.CarriedKeyName;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.ReferenceList;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link EncryptedKey} objects.
 */
public class EncryptedKeyUnmarshaller extends EncryptedTypeUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final EncryptedKey ek = (EncryptedKey) xmlObject;

        if (attribute.getLocalName().equals(EncryptedKey.RECIPIENT_ATTRIB_NAME)) {
            ek.setRecipient(attribute.getValue());
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final EncryptedKey ek = (EncryptedKey) parentXMLObject;

        if (childXMLObject instanceof ReferenceList) {
            ek.setReferenceList((ReferenceList) childXMLObject);
        } else if (childXMLObject instanceof CarriedKeyName) {
            ek.setCarriedKeyName((CarriedKeyName) childXMLObject);
        } else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}