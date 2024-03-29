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

package org.opensaml.xacml.ctx.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.ctx.AttributeValueType;
import org.opensaml.xacml.impl.AbstractXACMLObjectUnmarshaller;
import org.w3c.dom.Attr;

/** Unmarshaller for {@link AttributeType} objects. */
public class AttributeTypeUnmarshaller extends AbstractXACMLObjectUnmarshaller {

    /** Constructor. */
    public AttributeTypeUnmarshaller() {
        super();
    }

    /** {@inheritDoc} */
    protected void processChildElement(final XMLObject parentObject, final XMLObject childObject)
            throws UnmarshallingException {
        final AttributeType attribute = (AttributeType) parentObject;
     
        if (childObject instanceof AttributeValueType) {
            attribute.getAttributeValues().add((AttributeValueType)childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

    /** {@inheritDoc} */
    protected void processAttribute(final XMLObject xmlObject, final Attr attribute) throws UnmarshallingException {

        final AttributeType attrib = (AttributeType) xmlObject;

        if (attribute.getLocalName().equals(AttributeType.ATTRIBUTEID_ATTTRIB_NAME)) {
            attrib.setAttributeID(attribute.getValue());
        } else if (attribute.getLocalName().equals(AttributeType.DATATYPE_ATTRIB_NAME)) {
            attrib.setDataType(attribute.getValue());
        } else if (attribute.getLocalName().equals(AttributeType.ISSUER_ATTRIB_NAME)) {
            attrib.setIssuer(attribute.getValue());
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}