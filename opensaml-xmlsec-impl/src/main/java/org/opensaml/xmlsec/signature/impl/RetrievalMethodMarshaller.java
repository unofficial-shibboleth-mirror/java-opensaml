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

package org.opensaml.xmlsec.signature.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.xmlsec.signature.RetrievalMethod;
import org.w3c.dom.Element;

/**
 * A thread-safe Marshaller for {@link org.opensaml.xmlsec.signature.RetrievalMethod} objects.
 */
public class RetrievalMethodMarshaller extends AbstractXMLSignatureMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final RetrievalMethod rm = (RetrievalMethod) xmlObject;

        if (rm.getURI() != null) {
            domElement.setAttributeNS(null, RetrievalMethod.URI_ATTRIB_NAME, rm.getURI());
        }
        if (rm.getType() != null) {
            domElement.setAttributeNS(null, RetrievalMethod.TYPE_ATTRIB_NAME, rm.getType());
        }
    }

}
