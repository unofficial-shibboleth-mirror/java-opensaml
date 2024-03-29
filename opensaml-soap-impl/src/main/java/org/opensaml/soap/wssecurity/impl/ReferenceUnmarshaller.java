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

package org.opensaml.soap.wssecurity.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.soap.wssecurity.Reference;
import org.w3c.dom.Attr;

/**
 * ReferenceUnmarshaller.
 * 
 */
public class ReferenceUnmarshaller extends AbstractWSSecurityObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final Reference reference = (Reference) xmlObject;
        final String attrName = attribute.getLocalName();
        if (Reference.URI_ATTRIB_NAME.equals(attrName)) {
            reference.setURI(attribute.getValue());
        } else if (Reference.VALUE_TYPE_ATTRIB_NAME.equals(attrName)) {
            reference.setValueType(attribute.getValue());
        } else {
            XMLObjectSupport.unmarshallToAttributeMap(reference.getUnknownAttributes(), attribute);
        }
    }

}
