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
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.xacml.ctx.AttributeType;
import org.opensaml.xacml.impl.AbstractXACMLObjectMarshaller;
import org.w3c.dom.Element;

/** Marshaller for {@link AttributeType} objects. */
public class AttributeTypeMarshaller extends AbstractXACMLObjectMarshaller {

    /** Constructor. */
    public AttributeTypeMarshaller() {
        super();
    }

    /** {@inheritDoc} */
    protected void marshallAttributes(final XMLObject samlElement, final Element domElement)
            throws MarshallingException {
        final AttributeType attribute = (AttributeType) samlElement;

        if (attribute.getIssuer() != null) {
            domElement.setAttributeNS(null, AttributeType.ISSUER_ATTRIB_NAME, attribute.getIssuer());
        }

        if (attribute.getDataType() != null) {
            domElement.setAttributeNS(null, AttributeType.DATATYPE_ATTRIB_NAME, attribute.getDataType());
        }

        if (attribute.getAttributeId() != null) {
            domElement.setAttributeNS(null, AttributeType.ATTRIBUTEID_ATTTRIB_NAME, attribute.getAttributeId());
        }
    }

}