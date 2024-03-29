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
import org.opensaml.soap.wssecurity.Embedded;
import org.w3c.dom.Attr;

/**
 * EmbeddedUnmarshaller.
 * 
 */
public class EmbeddedUnmarshaller extends AbstractWSSecurityObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final Embedded embedded = (Embedded) xmlObject;
        final String attrName = attribute.getLocalName();
        if (Embedded.VALUE_TYPE_ATTRIB_NAME.equals(attrName)) {
            embedded.setValueType(attribute.getValue());
        } else {
            XMLObjectSupport.unmarshallToAttributeMap(embedded.getUnknownAttributes(), attribute);
        }
    }

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final Embedded embedded = (Embedded) parentXMLObject;
        embedded.getUnknownXMLObjects().add(childXMLObject);
    }

}
