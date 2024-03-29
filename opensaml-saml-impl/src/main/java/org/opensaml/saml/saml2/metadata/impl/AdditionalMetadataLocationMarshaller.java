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

package org.opensaml.saml.saml2.metadata.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.impl.XSURIMarshaller;
import org.opensaml.saml.saml2.metadata.AdditionalMetadataLocation;
import org.w3c.dom.Element;

/**
 * A thread safe marshaller for {@link AdditionalMetadataLocation} objects.
 */
public class AdditionalMetadataLocationMarshaller extends XSURIMarshaller {

    /** {@inheritDoc} */
    @Override
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final AdditionalMetadataLocation aml = (AdditionalMetadataLocation) xmlObject;

        if (aml.getNamespaceURI() != null) {
            domElement.setAttributeNS(null, AdditionalMetadataLocation.NAMESPACE_ATTRIB_NAME, aml.getNamespaceURI());
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void marshallElementContent(@Nonnull final XMLObject samlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        
        final AdditionalMetadataLocation aml = (AdditionalMetadataLocation) samlObject;
        if (aml.getURI() != null) {
            domElement.appendChild(domElement.getOwnerDocument().createTextNode(aml.getURI()));
        }
    }
    
}