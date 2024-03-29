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
import org.opensaml.core.xml.schema.impl.XSStringUnmarshaller;
import org.opensaml.saml.saml1.core.NameIdentifier;
import org.w3c.dom.Attr;

/**
 * A thread safe Unmarshaller for {@link NameIdentifier} objects.
 */
public class NameIdentifierUnmarshaller extends XSStringUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final NameIdentifier nameIdentifier = (NameIdentifier) xmlObject;

        if (attribute.getNamespaceURI() == null) {
            if (NameIdentifier.FORMAT_ATTRIB_NAME.equals(attribute.getLocalName())) {
                nameIdentifier.setFormat(attribute.getValue());
            } else if (NameIdentifier.NAMEQUALIFIER_ATTRIB_NAME.equals(attribute.getLocalName())) {
                nameIdentifier.setNameQualifier(attribute.getValue());
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
    
}