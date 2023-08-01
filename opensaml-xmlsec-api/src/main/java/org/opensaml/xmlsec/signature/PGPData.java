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

import org.opensaml.core.xml.ElementExtensibleXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * XMLObject representing XML Digital Signature, version 20020212, PGPData element.
 */
public interface PGPData extends XMLObject, ElementExtensibleXMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "PGPData";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SignatureConstants.XMLSIG_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, SignatureConstants.XMLSIG_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "PGPDataType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SignatureConstants.XMLSIG_NS, TYPE_LOCAL_NAME,
            SignatureConstants.XMLSIG_PREFIX);

    /**
     * Get PGPKeyID child element.
     * 
     * @return the PGPKeyID child element
     */
    @Nullable PGPKeyID getPGPKeyID();

    /**
     * Set PGPKeyID child element.
     * 
     * @param newPGPKeyID the new PGPKeyID
     */
    void setPGPKeyID(@Nullable final PGPKeyID newPGPKeyID);

    /**
     * Get PGPKeyPacket child element.
     * 
     * @return the PGPKeyPacket child element
     */
    @Nullable PGPKeyPacket getPGPKeyPacket();

    /**
     * Set PGPKeyPacket child element.
     * 
     * @param newPGPKeyPacket the new PGPKeyPacket
     */
    void setPGPKeyPacket(@Nullable final PGPKeyPacket newPGPKeyPacket);

}