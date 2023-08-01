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
import org.opensaml.xmlsec.encryption.DHKeyValue;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * XMLObject representing XML Digital Signature, version 20020212, KeyValue element.
 */
public interface KeyValue extends XMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "KeyValue";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SignatureConstants.XMLSIG_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, SignatureConstants.XMLSIG_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "KeyValueType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SignatureConstants.XMLSIG_NS, TYPE_LOCAL_NAME,
            SignatureConstants.XMLSIG_PREFIX);

    /**
     * Get the DSAKeyValue child element.
     * 
     * @return DSAKeyValue child element
     */
    @Nullable DSAKeyValue getDSAKeyValue();

    /**
     * Set the DSAKeyValue child element.
     * 
     * @param newDSAKeyValue the new DSAKeyValue child element
     */
    void setDSAKeyValue(@Nullable final DSAKeyValue newDSAKeyValue);

    /**
     * Get the RSAKeyValue child element.
     * 
     * @return the RSAKeyValue child element
     */
    @Nullable RSAKeyValue getRSAKeyValue();

    /**
     * Set the RSAKeyValue child element.
     * 
     * @param newRSAKeyValue the new RSAKeyValue child element
     */
    void setRSAKeyValue(@Nullable final RSAKeyValue newRSAKeyValue);

    /**
     * Get the ECKeyValue child element.
     * 
     * @return the ECKeyValue child element
     */
    @Nullable ECKeyValue getECKeyValue();

    /**
     * Set the ECKeyValue child element.
     * 
     * @param newECKeyValue the new ECKeyValue child element
     */
    void setECKeyValue(@Nullable final ECKeyValue newECKeyValue);
    
    /**
     * Get the DHKeyValue child element.
     * 
     * @return DHKeyValue child element
     */
    @Nullable DHKeyValue getDHKeyValue();

    /**
     * Set the DHKeyValue child element.
     * 
     * @param newDHKeyValue the new DHKeyValue child element
     */
    void setDHKeyValue(@Nullable final DHKeyValue newDHKeyValue);

    /**
     * Get the wildcard &lt;any&gt; XMLObject child element.
     * 
     * @return the wildcard XMLObject child element
     */
    @Nullable XMLObject getUnknownXMLObject();

    /**
     * Set the wildcard &lt;any&gt; XMLObject child element.
     * 
     * @param newXMLObject the wildcard XMLObject child element
     */
    void setUnknownXMLObject(@Nullable final XMLObject newXMLObject);

}