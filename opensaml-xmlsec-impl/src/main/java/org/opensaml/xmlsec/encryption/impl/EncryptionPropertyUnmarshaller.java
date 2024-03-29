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
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xmlsec.encryption.EncryptionProperty;
import org.w3c.dom.Attr;

import net.shibboleth.shared.xml.QNameSupport;

/**
 * A thread-safe Unmarshaller for {@link org.opensaml.xmlsec.encryption.EncryptionProperty} objects.
 */
public class EncryptionPropertyUnmarshaller extends AbstractXMLEncryptionUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final EncryptionProperty ep = (EncryptionProperty) xmlObject;

        if (attribute.getLocalName().equals(EncryptionProperty.ID_ATTRIB_NAME)) {
            ep.setID(attribute.getValue());
            attribute.getOwnerElement().setIdAttributeNode(attribute, true);
        } else if (attribute.getLocalName().equals(EncryptionProperty.TARGET_ATTRIB_NAME)) {
            ep.setTarget(attribute.getValue());
        } else {
            final QName attributeName = QNameSupport.getNodeQName(attribute);
            if (attribute.isId()) {
                ep.getUnknownAttributes().registerID(attributeName);
            }
            ep.getUnknownAttributes().put(attributeName, attribute.getValue());
        }
    }

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final EncryptionProperty ep = (EncryptionProperty) parentXMLObject;

        // <any> content model
        ep.getUnknownXMLObjects().add(childXMLObject);
    }

}
