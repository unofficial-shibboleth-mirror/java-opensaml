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

package org.opensaml.saml.saml2.core.impl;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Attribute;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link Attribute} objects.
 */
public class AttributeUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {
        final Attribute attribute = (Attribute) parentObject;

        final QName childQName = childObject.getElementQName();
        if ("AttributeValue".equals(childQName.getLocalPart())
                && childQName.getNamespaceURI().equals(SAMLConstants.SAML20_NS)) {
            attribute.getAttributeValues().add(childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final Attribute attrib = (Attribute) xmlObject;

        if (attribute.getNamespaceURI() == null) {
            if (attribute.getLocalName().equals(Attribute.NAME_ATTTRIB_NAME)) {
                attrib.setName(attribute.getValue());
            } else if (attribute.getLocalName().equals(Attribute.NAME_FORMAT_ATTRIB_NAME)) {
                attrib.setNameFormat(attribute.getValue());
            } else if (attribute.getLocalName().equals(Attribute.FRIENDLY_NAME_ATTRIB_NAME)) {
                attrib.setFriendlyName(attribute.getValue());
            } else {
                super.processAttribute(xmlObject, attribute);
           }
        } else {
            processUnknownAttribute(attrib, attribute);
        }
    }
    
}