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

package org.opensaml.saml.saml2.core.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.impl.XSStringUnmarshaller;
import org.opensaml.saml.saml2.core.NameIDType;
import org.w3c.dom.Attr;

/**
 * A thread-safe Unmarshaller for {@link NameIDType} objects.
 */
public class NameIDTypeUnmarshaller extends XSStringUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(@Nonnull final XMLObject xmlObject, @Nonnull final Attr attribute)
            throws UnmarshallingException {
        final NameIDType nameID = (NameIDType) xmlObject;
        
        if (attribute.getNamespaceURI() == null) {
            if (attribute.getLocalName().equals(NameIDType.NAME_QUALIFIER_ATTRIB_NAME)) {
                nameID.setNameQualifier(attribute.getValue());
            } else if (attribute.getLocalName().equals(NameIDType.SP_NAME_QUALIFIER_ATTRIB_NAME)) {
                nameID.setSPNameQualifier(attribute.getValue());
            } else if (attribute.getLocalName().equals(NameIDType.FORMAT_ATTRIB_NAME)) {
                nameID.setFormat(attribute.getValue());
            } else if (attribute.getLocalName().equals(NameIDType.SPPROVIDED_ID_ATTRIB_NAME)) {
                nameID.setSPProvidedID(attribute.getValue());
            } else {
                super.processAttribute(xmlObject, attribute);
            }
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }
}