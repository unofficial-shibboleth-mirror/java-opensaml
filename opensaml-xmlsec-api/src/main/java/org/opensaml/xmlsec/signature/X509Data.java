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

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/** XMLObject representing XML Digital Signature, version 20020212, X509Data element. */
public interface X509Data extends XMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "X509Data";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SignatureConstants.XMLSIG_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, SignatureConstants.XMLSIG_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "X509DataType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SignatureConstants.XMLSIG_NS, TYPE_LOCAL_NAME,
            SignatureConstants.XMLSIG_PREFIX);

    /**
     * Get the list of all XMLObject children.
     * 
     * @return the list of XMLObject children
     */
    @Nonnull @Live List<XMLObject> getXMLObjects();

    /**
     * Get the list of XMLObject children whose type or element QName matches the specified QName.
     * 
     * @param typeOrName the QName of the desired elements
     * 
     * @return the matching list of XMLObject children
     */
    @Nonnull @Live List<XMLObject> getXMLObjects(@Nonnull final QName typeOrName);

    /**
     * Get the list of X509IssuerSerial child elements.
     * 
     * @return the list of X509IssuerSerial child elements
     */
    @Nonnull @Live List<X509IssuerSerial> getX509IssuerSerials();

    /**
     * Get the list of X509SKI child elements.
     * 
     * @return the list of X509SKI child elements
     */
    @Nonnull @Live List<X509SKI> getX509SKIs();

    /**
     * Get the list of X509SubjectName child elements.
     * 
     * @return the list of X509SubjectName child elements
     */
    @Nonnull @Live List<X509SubjectName> getX509SubjectNames();

    /**
     * Get the list of X509Certificate child elements.
     * 
     * @return the list of X509Certificate child elements
     */
    @Nonnull @Live List<X509Certificate> getX509Certificates();

    /**
     * Get the list of X509CRL child elements.
     * 
     * @return the list of X509CRL child elements
     */
    @Nonnull @Live List<X509CRL> getX509CRLs();

    /**
     * Get the list of X509Digest child elements.
     * 
     * @return the list of X509Digest child elements
     */
    @Nonnull @Live List<X509Digest> getX509Digests();
}