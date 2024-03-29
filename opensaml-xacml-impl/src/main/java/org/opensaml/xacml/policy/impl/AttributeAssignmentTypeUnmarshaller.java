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

package org.opensaml.xacml.policy.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xacml.policy.AttributeAssignmentType;
import org.w3c.dom.Attr;

import net.shibboleth.shared.primitive.StringSupport;

/** UnMarshaller for {@link AttributeAssignmentType}. */
public class AttributeAssignmentTypeUnmarshaller extends AttributeValueTypeUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(final XMLObject samlObject, final Attr attribute) throws UnmarshallingException {

        final AttributeAssignmentType attrib = (AttributeAssignmentType) samlObject;

        if (attribute.getLocalName().equals(AttributeAssignmentType.ATTR_ID_ATTRIB_NAME)) {
            attrib.setAttributeId(StringSupport.trimOrNull(attribute.getValue()));
        } else{
            super.processAttribute(samlObject, attribute);
        }
    }

}