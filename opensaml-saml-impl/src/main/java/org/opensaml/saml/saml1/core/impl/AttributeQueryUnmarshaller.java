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

package org.opensaml.saml.saml1.core.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml1.core.AttributeDesignator;
import org.opensaml.saml.saml1.core.AttributeQuery;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link AttributeQuery} objects.
 */
public class AttributeQueryUnmarshaller extends SubjectQueryUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(@Nonnull final XMLObject parentObject, @Nonnull final XMLObject childObject)
            throws UnmarshallingException {

        final AttributeQuery attributeQuery = (AttributeQuery) parentObject;

        if (childObject instanceof AttributeDesignator) {
            attributeQuery.getAttributeDesignators().add((AttributeDesignator) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {

        final AttributeQuery attributeQuery = (AttributeQuery) xmlObject;

        if (attribute.getLocalName().equals(AttributeQuery.RESOURCE_ATTRIB_NAME)
                && attribute.getNamespaceURI() == null) {
            attributeQuery.setResource(attribute.getValue());
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}