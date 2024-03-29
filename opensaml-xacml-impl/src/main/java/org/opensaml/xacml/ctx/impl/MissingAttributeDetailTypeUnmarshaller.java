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
import org.opensaml.xacml.ctx.AttributeValueType;
import org.opensaml.xacml.ctx.MissingAttributeDetailType;
import org.opensaml.xacml.impl.AbstractXACMLObjectUnmarshaller;
import org.w3c.dom.Attr;

/** Unmarshaller for {@link MissingAttributeDetailType} objects. */
public class MissingAttributeDetailTypeUnmarshaller extends AbstractXACMLObjectUnmarshaller {

    /** Constructor. */
    public MissingAttributeDetailTypeUnmarshaller() {
        super();
    }

    /** {@inheritDoc} */
    protected void processAttribute(final XMLObject xmlObject, final Attr attribute) throws UnmarshallingException {
        final MissingAttributeDetailType madt = (MissingAttributeDetailType) xmlObject;

        if (attribute.getLocalName().equals(MissingAttributeDetailType.ATTRIBUTE_ID_ATTRIB_NAME)) {
            madt.setAttributeId(attribute.getValue());
        } else if (attribute.getLocalName().equals(MissingAttributeDetailType.DATA_TYPE_ATTRIB_NAME)) {
            madt.setDataType(attribute.getValue());
        } else if (attribute.getLocalName().equals(MissingAttributeDetailType.ISSUER_ATTRIB_NAME)) {
            madt.setIssuer(attribute.getValue());
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

    /** {@inheritDoc} */
    protected void processChildElement(final XMLObject parentXMLObject, final XMLObject childXMLObject)
            throws UnmarshallingException {
        final MissingAttributeDetailType madt = (MissingAttributeDetailType) parentXMLObject;
        if (childXMLObject instanceof AttributeValueType) {
            madt.getAttributeValues().add((AttributeValueType) childXMLObject);
        } else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}