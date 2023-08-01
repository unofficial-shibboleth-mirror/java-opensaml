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
import org.opensaml.core.xml.schema.impl.XSBase64BinaryMarshaller;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.xmlsec.signature.DEREncodedKeyValue;
import org.w3c.dom.Element;

/**
 * A thread-safe Marshaller for {@link DEREncodedKeyValue} objects.
 */
public class DEREncodedKeyValueMarshaller extends XSBase64BinaryMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final DEREncodedKeyValue der = (DEREncodedKeyValue) xmlObject;

        if (der.getID() != null) {
            domElement.setAttributeNS(null, DEREncodedKeyValue.ID_ATTRIB_NAME, der.getID());
        }
    }

    /** {@inheritDoc} */
    protected void marshallAttributeIDness(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {

        XMLObjectSupport.marshallAttributeIDness(null, DEREncodedKeyValue.ID_ATTRIB_NAME, domElement, true);
    }

}