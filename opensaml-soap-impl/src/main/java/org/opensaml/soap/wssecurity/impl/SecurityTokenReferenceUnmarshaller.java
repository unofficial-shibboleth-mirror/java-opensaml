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
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.soap.wssecurity.SecurityTokenReference;
import org.w3c.dom.Attr;

import net.shibboleth.shared.xml.AttributeSupport;
import net.shibboleth.shared.xml.QNameSupport;

/**
 * SecurityTokenReferenceUnmarshaller.
 */
public class SecurityTokenReferenceUnmarshaller extends AbstractWSSecurityObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final SecurityTokenReference str = (SecurityTokenReference) parentXMLObject;

        str.getUnknownXMLObjects().add(childXMLObject);
    }

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final SecurityTokenReference str = (SecurityTokenReference) xmlObject;
        
        final QName attribQName = 
            QNameSupport.constructQName(attribute.getNamespaceURI(), attribute.getLocalName(), attribute.getPrefix());
        if (SecurityTokenReference.WSU_ID_ATTR_NAME.equals(attribQName)) {
            str.setWSUId(attribute.getValue());
            attribute.getOwnerElement().setIdAttributeNode(attribute, true);
        } else if (SecurityTokenReference.WSSE_USAGE_ATTR_NAME.equals(attribQName)) {
            str.setWSSEUsages(AttributeSupport.getAttributeValueAsList(attribute));
        } else {
            XMLObjectSupport.unmarshallToAttributeMap(str.getUnknownAttributes(), attribute);
        }
    }

}
