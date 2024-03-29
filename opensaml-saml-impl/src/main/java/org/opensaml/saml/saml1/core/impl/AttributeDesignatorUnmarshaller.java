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
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml1.core.AttributeDesignator;
import org.w3c.dom.Attr;

/** Unmarshaller for {@link AttributeDesignator} objects. */
public class AttributeDesignatorUnmarshaller extends AbstractSAMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {

        final AttributeDesignator designator = (AttributeDesignator) xmlObject;

        if (attribute.getNamespaceURI() == null) {
            if (AttributeDesignator.ATTRIBUTENAME_ATTRIB_NAME.equals(attribute.getLocalName())) {
                designator.setAttributeName(attribute.getValue());
            } else if (AttributeDesignator.ATTRIBUTENAMESPACE_ATTRIB_NAME.equals(attribute.getLocalName())) {
                designator.setAttributeNamespace(attribute.getValue());
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

}