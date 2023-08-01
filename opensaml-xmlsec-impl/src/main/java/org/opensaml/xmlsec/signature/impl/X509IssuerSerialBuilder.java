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
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObjectBuilder;
import org.opensaml.xmlsec.signature.X509IssuerSerial;
import org.opensaml.xmlsec.signature.XMLSignatureBuilder;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

/**
 * Builder of {@link X509IssuerSerial}.
 */
public class X509IssuerSerialBuilder extends AbstractXMLObjectBuilder<X509IssuerSerial> implements
        XMLSignatureBuilder<X509IssuerSerial> {

    /** {@inheritDoc} */
    @Nonnull public X509IssuerSerial buildObject(@Nullable final String namespaceURI, @Nonnull final String localName,
            @Nullable final String namespacePrefix) {
        return new X509IssuerSerialImpl(namespaceURI, localName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nonnull public X509IssuerSerial buildObject() {
        return buildObject(SignatureConstants.XMLSIG_NS, X509IssuerSerial.DEFAULT_ELEMENT_LOCAL_NAME,
                SignatureConstants.XMLSIG_PREFIX);
    }

}