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

package org.opensaml.saml.ext.samlec.impl;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.ext.samlec.EncType;
import org.opensaml.saml.ext.samlec.SessionKey;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.w3c.dom.Attr;

import net.shibboleth.shared.xml.QNameSupport;

/**
 * A thread-safe Unmarshaller for {@link SessionKey} objects.
 */
public class SessionKeyUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final SessionKey key = (SessionKey) parentObject;

        if (childObject instanceof EncType) {
            key.getEncTypes().add((EncType) childObject);
        } else if (childObject instanceof KeyInfo) {
            key.setKeyInfo((KeyInfo) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final SessionKey key = (SessionKey) xmlObject;

        final QName attrName = QNameSupport.getNodeQName(attribute);
        if (SessionKey.SOAP11_MUST_UNDERSTAND_ATTR_NAME.equals(attrName)) {
            key.setSOAP11MustUnderstand(XSBooleanValue.valueOf(attribute.getValue()));
        } else if (SessionKey.SOAP11_ACTOR_ATTR_NAME.equals(attrName)) {
            key.setSOAP11Actor(attribute.getValue()); 
        } else if (attribute.getLocalName().equals(SessionKey.ALGORITHM_ATTRIB_NAME)
                && attribute.getNamespaceURI() == null) {
            key.setAlgorithm(attribute.getValue());
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}