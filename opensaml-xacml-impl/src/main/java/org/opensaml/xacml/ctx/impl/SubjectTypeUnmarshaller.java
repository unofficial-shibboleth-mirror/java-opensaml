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
import org.opensaml.xacml.ctx.SubjectType;
import org.opensaml.xacml.impl.AbstractXACMLObjectUnmarshaller;
import org.w3c.dom.Attr;

/** Unmarshaller for {@link SubjectType} objects. */
public class SubjectTypeUnmarshaller extends AbstractXACMLObjectUnmarshaller {

    /** Constructor. */
    public SubjectTypeUnmarshaller() {
        super();
    }

    /** {@inheritDoc} */
    protected void processAttribute(final XMLObject xmlObject, final Attr attribute) throws UnmarshallingException {
        final SubjectType attrib = (SubjectType) xmlObject;
        if (attribute.getLocalName().equals(SubjectType.SUBJECT_CATEGORY_ATTTRIB_NAME)) {
            attrib.setSubjectCategory(attribute.getValue());
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

    /** {@inheritDoc} */
    protected void processChildElement(final XMLObject parentObject, final XMLObject childObject)
            throws UnmarshallingException {
        final SubjectType subject = (SubjectType) parentObject;
        if (childObject instanceof AttributeType) {
            subject.getAttributes().add((AttributeType) childObject);
        } else {
            super.processChildElement(parentObject, childObject);
        }
    }

}