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

package org.opensaml.xmlsec.signature;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/** XMLObject representing XML Digital Signature, version 20020212, X509IssuerSerial element. */
public interface X509IssuerSerial extends XMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "X509IssuerSerial";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SignatureConstants.XMLSIG_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, SignatureConstants.XMLSIG_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "X509IssuerSerialType";

    /** QName of the XSI type. */
    @Nonnull @NotEmpty static final QName TYPE_NAME = new QName(SignatureConstants.XMLSIG_NS, TYPE_LOCAL_NAME,
            SignatureConstants.XMLSIG_PREFIX);

    /**
     * Get the X509IssuerName child element.
     * 
     * @return the X509Issuername child element
     */
    @Nullable X509IssuerName getX509IssuerName();

    /**
     * Set the X509IssuerName child element.
     * 
     * @param newX509IssuerName the new X509IssuerName child element
     */
    void setX509IssuerName(@Nullable final X509IssuerName newX509IssuerName);

    /**
     * Get the X509SerialNumber child element.
     * 
     * @return the X509SerialNumber child element
     */
    @Nullable X509SerialNumber getX509SerialNumber();

    /**
     * Set the X509SerialNumber child element.
     * 
     * @param newX509SerialNumber the new X509SerialNumber child element
     */
    void setX509SerialNumber(@Nullable final X509SerialNumber newX509SerialNumber);

}
