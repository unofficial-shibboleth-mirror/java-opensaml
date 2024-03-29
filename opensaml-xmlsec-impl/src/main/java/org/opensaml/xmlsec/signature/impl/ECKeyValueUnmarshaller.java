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

package org.opensaml.xmlsec.signature.impl;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xmlsec.signature.ECKeyValue;
import org.opensaml.xmlsec.signature.NamedCurve;
import org.opensaml.xmlsec.signature.PublicKey;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.w3c.dom.Attr;


/**
 * A thread-safe Unmarshaller for {@link ECKeyValue} objects.
 */
public class ECKeyValueUnmarshaller extends AbstractXMLSignatureUnmarshaller {

    /** ECParameters element name. */
    @Nonnull public static final QName ECPARAMETERS_ELEMENT_NAME =
            new QName(SignatureConstants.XMLSIG11_NS, "ECParameters");

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final ECKeyValue ec = (ECKeyValue) xmlObject;

        if (attribute.getLocalName().equals(ECKeyValue.ID_ATTRIB_NAME)) {
            ec.setID(attribute.getValue());
            attribute.getOwnerElement().setIdAttributeNode(attribute, true);
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final ECKeyValue keyValue = (ECKeyValue) parentXMLObject;

        if (childXMLObject instanceof NamedCurve) {
            keyValue.setNamedCurve((NamedCurve) childXMLObject);
        } else if (childXMLObject instanceof PublicKey) {
            keyValue.setPublicKey((PublicKey) childXMLObject);
        } else if (childXMLObject.getElementQName().equals(ECPARAMETERS_ELEMENT_NAME)) {
            keyValue.setECParameters(childXMLObject);
        } else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}