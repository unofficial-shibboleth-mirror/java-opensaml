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

/**
 * 
 */

package org.opensaml.core.xml.mock;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.AbstractXMLObjectUnmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.w3c.dom.Attr;

/**
 * Unmarshaller for {@link org.opensaml.core.xml.mock.SimpleXMLObject}.
 */
public class SimpleXMLObjectUnmarshaller extends AbstractXMLObjectUnmarshaller {

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {

        final SimpleXMLObject simpleXMLObject = (SimpleXMLObject) parentXMLObject;

        if (childXMLObject instanceof SimpleXMLObject) {
            simpleXMLObject.getSimpleXMLObjects().add((SimpleXMLObject) childXMLObject);
        } else {
            simpleXMLObject.getUnknownXMLObjects().add(childXMLObject);
        }
    }

    /** {@inheritDoc} */
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final SimpleXMLObject simpleXMLObject = (SimpleXMLObject) xmlObject;

        if (attribute.getLocalName().equals(SimpleXMLObject.ID_ATTRIB_NAME)) {
            simpleXMLObject.setId(attribute.getValue());
            attribute.getOwnerElement().setIdAttributeNode(attribute, true);
        } else {
            XMLObjectSupport.unmarshallToAttributeMap(simpleXMLObject.getUnknownAttributes(), attribute);
        }
    }

    /** {@inheritDoc} */
    protected void processElementContent(@Nonnull final XMLObject xmlObject, @Nonnull final String elementContent) {
        final SimpleXMLObject simpleXMLObject = (SimpleXMLObject) xmlObject;

        simpleXMLObject.setValue(elementContent);
    }
}