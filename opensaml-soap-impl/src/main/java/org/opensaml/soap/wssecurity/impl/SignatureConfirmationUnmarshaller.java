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

package org.opensaml.soap.wssecurity.impl;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.soap.wssecurity.SignatureConfirmation;
import org.w3c.dom.Attr;

import net.shibboleth.shared.xml.QNameSupport;

/**
 * SignatureConfirmationUnmarshaller.
 */
public class SignatureConfirmationUnmarshaller extends AbstractWSSecurityObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final SignatureConfirmation sc = (SignatureConfirmation) xmlObject;
        
        final QName attrName =
            QNameSupport.constructQName(attribute.getNamespaceURI(), attribute.getLocalName(), attribute.getPrefix());
        if (SignatureConfirmation.WSU_ID_ATTR_NAME.equals(attrName)) {
            sc.setWSUId(attribute.getValue());
            attribute.getOwnerElement().setIdAttributeNode(attribute, true);
        } else if (SignatureConfirmation.VALUE_ATTRIB_NAME.equals(attribute.getLocalName())) {
            sc.setValue(attribute.getValue());
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

}
