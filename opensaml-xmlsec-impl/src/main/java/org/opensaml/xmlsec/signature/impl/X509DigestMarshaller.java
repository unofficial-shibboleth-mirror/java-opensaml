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
import org.opensaml.xmlsec.signature.X509Digest;
import org.w3c.dom.Element;

/**
 * A thread-safe Marshaller for {@link X509Digest} objects.
 */
public class X509DigestMarshaller extends XSBase64BinaryMarshaller {

    /** {@inheritDoc} */
    protected void marshallAttributes(@Nonnull final XMLObject xmlObject, @Nonnull final Element domElement)
            throws MarshallingException {
        final X509Digest xd = (X509Digest) xmlObject;

        if (xd.getAlgorithm() != null) {
            domElement.setAttributeNS(null, X509Digest.ALGORITHM_ATTRIB_NAME, xd.getAlgorithm());
        }
    }

}