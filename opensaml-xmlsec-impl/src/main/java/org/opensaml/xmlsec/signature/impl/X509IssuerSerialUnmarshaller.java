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
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xmlsec.signature.X509IssuerName;
import org.opensaml.xmlsec.signature.X509IssuerSerial;
import org.opensaml.xmlsec.signature.X509SerialNumber;

/**
 * A thread-safe Unmarshaller for {@link org.opensaml.xmlsec.signature.X509IssuerSerial} objects.
 */
public class X509IssuerSerialUnmarshaller extends AbstractXMLSignatureUnmarshaller {

    /** {@inheritDoc} */
    protected void processChildElement(@Nonnull final XMLObject parentXMLObject,
            @Nonnull final XMLObject childXMLObject) throws UnmarshallingException {
        final X509IssuerSerial keyValue = (X509IssuerSerial) parentXMLObject;

        if (childXMLObject instanceof X509IssuerName) {
            keyValue.setX509IssuerName((X509IssuerName) childXMLObject);
        } else if (childXMLObject instanceof X509SerialNumber) {
            keyValue.setX509SerialNumber((X509SerialNumber) childXMLObject);
        } else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}
