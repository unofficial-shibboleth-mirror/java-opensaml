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
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.impl.XSStringMarshaller;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDType;
import org.w3c.dom.Element;

import net.shibboleth.shared.xml.ElementSupport;

/**
 * A thread safe Marshaller for {@link NameIDType} objects.
 */
public class NameIDTypeMarshaller extends XSStringMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final NameIDType nameID = (NameIDType) xmlObject;

        if (nameID.getNameQualifier() != null) {
            domElement.setAttributeNS(null, NameID.NAME_QUALIFIER_ATTRIB_NAME, nameID.getNameQualifier());
        }

        if (nameID.getSPNameQualifier() != null) {
            domElement.setAttributeNS(null, NameID.SP_NAME_QUALIFIER_ATTRIB_NAME, nameID.getSPNameQualifier());
        }

        if (nameID.getFormat() != null) {
            domElement.setAttributeNS(null, NameID.FORMAT_ATTRIB_NAME, nameID.getFormat());
        }

        if (nameID.getSPProvidedID() != null) {
            domElement.setAttributeNS(null, NameID.SPPROVIDED_ID_ATTRIB_NAME, nameID.getSPProvidedID());
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void marshallElementContent(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final NameIDType nameID = (NameIDType) xmlObject;
        
        ElementSupport.appendTextContent(domElement, nameID.getValue());
    }

}