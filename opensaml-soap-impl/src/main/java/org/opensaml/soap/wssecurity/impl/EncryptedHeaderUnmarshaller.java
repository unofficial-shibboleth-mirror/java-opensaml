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
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.soap.wssecurity.EncryptedHeader;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.w3c.dom.Attr;

import net.shibboleth.shared.xml.QNameSupport;

/**
 * Unmarshaller for instances of {@link EncryptedHeader}.
 */
public class EncryptedHeaderUnmarshaller extends AbstractWSSecurityObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final EncryptedHeader eh = (EncryptedHeader) xmlObject;
        final QName attrName = QNameSupport.getNodeQName(attribute);
        if (EncryptedHeader.WSU_ID_ATTR_NAME.equals(attrName)) {
            eh.setWSUId(attribute.getValue());
            attribute.getOwnerElement().setIdAttributeNode(attribute, true);
        } else if (EncryptedHeader.SOAP11_MUST_UNDERSTAND_ATTR_NAME.equals(attrName)) {
            eh.setSOAP11MustUnderstand(XSBooleanValue.valueOf(attribute.getValue()));
        } else if (EncryptedHeader.SOAP11_ACTOR_ATTR_NAME.equals(attrName)) {
            eh.setSOAP11Actor(attribute.getValue());
        } else if (EncryptedHeader.SOAP12_MUST_UNDERSTAND_ATTR_NAME.equals(attrName)) {
            eh.setSOAP12MustUnderstand(XSBooleanValue.valueOf(attribute.getValue()));
        } else if (EncryptedHeader.SOAP12_ROLE_ATTR_NAME.equals(attrName)) {
            eh.setSOAP12Role(attribute.getValue());
        } else if (EncryptedHeader.SOAP12_RELAY_ATTR_NAME.equals(attrName)) {
            eh.setSOAP12Relay(XSBooleanValue.valueOf(attribute.getValue()));
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final EncryptedHeader eh = (EncryptedHeader) parentXMLObject;
        if (childXMLObject instanceof EncryptedData) {
            eh.setEncryptedData((EncryptedData) childXMLObject);
        } else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }
    
}
