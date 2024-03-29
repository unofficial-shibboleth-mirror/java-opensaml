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

import java.math.BigInteger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSBase64Binary;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.codec.EncodingException;

/**
 * XMLObject representing XML Digital Signature, version 20020212, CryptoBinary simple type.
 */
public interface CryptoBinary extends XSBase64Binary {

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "CryptoBinary";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SignatureConstants.XMLSIG_NS, TYPE_LOCAL_NAME,
            SignatureConstants.XMLSIG_PREFIX);

    /**
     * Convenience method to get the value of the element as a BigInteger type.
     * 
     * @return the BigInteger representation of the element's content
     */
    @Nullable BigInteger getValueBigInt();

    /**
     * Convenience method to set the value of the element as a BigInteger type.
     * 
     * @param bigInt the new BigInteger representation of the element's content
     * @throws EncodingException if the byte value of the BigInteger can not be base64 encoded.
     */
    void setValueBigInt(@Nullable final BigInteger bigInt) throws EncodingException;

}