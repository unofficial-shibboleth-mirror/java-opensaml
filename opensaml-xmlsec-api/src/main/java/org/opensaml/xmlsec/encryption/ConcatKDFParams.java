/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xmlsec.encryption;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.signature.DigestMethod;

/**
 * XMLObject representing XML Encryption 1.1 ConcatKDFParams element.
 */
public interface ConcatKDFParams extends XMLObject {
    
    /** Element local name. */
    static final String DEFAULT_ELEMENT_LOCAL_NAME = "ConcatKDFParams";

    /** Default element name. */
    static final QName DEFAULT_ELEMENT_NAME = new QName(EncryptionConstants.XMLENC11_NS, DEFAULT_ELEMENT_LOCAL_NAME,
      EncryptionConstants.XMLENC11_PREFIX);

    /** Local name of the XSI type. */
    static final String TYPE_LOCAL_NAME = "ConcatKDFParamsType";

    /** QName of the XSI type. */
    static final QName TYPE_NAME =
            new QName(EncryptionConstants.XMLENC11_NS, TYPE_LOCAL_NAME, EncryptionConstants.XMLENC11_PREFIX);

    /** AlgorithmID attribute name. */
    static final String ALGORITHM_ID_ATTRIBUTE_NAME = "AlgorithmID";

    /** PartyUInfo attribute name. */
    static final String PARTY_U_INFO_ATTRIBUTE_NAME = "PartyUInfo";

    /** PartyVInfo attribute name. */
    static final String PARTY_V_INFO_ATTRIBUTE_NAME = "PartyVInfo";

    /** SuppPubInfo attribute name. */
    static final String SUPP_PUB_INFO_ATTRIBUTE_NAME = "SuppPubInfo";

    /** SuppPrivInfo attribute name. */
    static final String SUPP_PRIV_INFO_ATTRIBUTE_NAME = "SuppPrivInfo";

    /**
     * Gets the digest method.
     * 
     * @return the digest method
     */
    @Nullable DigestMethod getDigestMethod();

    /**
     * Sets the digest method.
     * 
     * @param digestMethod the digest method
     */
    void setDigestMethod(@Nullable final DigestMethod digestMethod);

    /**
     * Gets the AlgorithmID attribute in its padded hex-encoded form.
     * 
     * @return the {@code AlgorithmID} attribute
     */
    @Nullable String getAlgorithmID();

    /**
     * Sets the AlgorithmID attribute.
     * 
     * @param algorithmID the AlgorithmID attribute in its padded hex-encoded form
     */
    void setAlgorithmID(@Nullable final String algorithmID);
    
    /**
     * Gets the AlgorithmID attribute in its padded byte array form.
     * 
     * @return the {@code AlgorithmID} attribute
     */
    @Nullable byte[] getAlgorithmIDBytes();

    /**
     * Sets the AlgorithmID attribute.
     * 
     * @param algorithmID the AlgorithmID attribute in its padded byte array form
     */
    void setAlgorithmIDBytes(@Nullable final byte[] algorithmID);
    
    /**
     * Gets the PartyUInfo attribute in its padded hex-encoded form.
     * 
     * @return the PartyUInfo attribute
     */
    @Nullable String getPartyUInfo();

    /**
     * Sets the PartyUInfo attribute.
     * 
     * @param partyUInfo PartyUInfo attribute in its padded hex-encoded form
     */
    void setPartyUInfo(@Nullable final String partyUInfo);


    /**
     * Gets the PartyUInfo attribute in its padded byte array form.
     * 
     * @return the PartyUInfo attribute
     */
    @Nullable byte[] getPartyUInfoBytes();

    /**
     * Sets the PartyUInfo attribute.
     * 
     * @param partyUInfo PartyUInfo attribute in its padded byte array form
     */
    void setPartyUInfoBytes(@Nullable final byte[] partyUInfo);
    
    /**
     * Gets the PartyVInfo attribute in its padded hex-encoded form.
     * 
     * @return the PartyVInfo attribute
     */
    @Nullable String getPartyVInfo();

    /**
     * Sets the PartyVInfo attribute.
     * 
     * @param partyVInfo PartyVInfo attribute in its padded hex-encoded form
     */
    void setPartyVInfo(@Nullable final String partyVInfo);

    /**
     * Gets the PartyVInfo attribute in its padded byte array form.
     * 
     * @return the PartyVInfo attribute
     */
    @Nullable byte[] getPartyVInfoBytes();

    /**
     * Sets the PartyVInfo attribute.
     * 
     * @param partyVInfo PartyVInfo attribute in its padded byte array form
     */
    void setPartyVInfoBytes(@Nullable final byte[] partyVInfo);

    /**
     * Gets the SuppPubInfo attribute in its padded hex-encoded form.
     * 
     * @return the SuppPubInfo attribute
     */
    @Nullable String getSuppPubInfo();

    /**
     * Sets the SuppPubInfo attribute.
     * 
     * @param suppPubInfo SuppPubInfo attribute in its padded hex-encoded form
     */
    void setSuppPubInfo(@Nullable final String suppPubInfo);
    
    /**
     * Gets the SuppPubInfo attribute in its padded byte array form.
     * 
     * @return the SuppPubInfo attribute
     */
    @Nullable byte[] getSuppPubInfoBytes();

    /**
     * Sets the SuppPubInfo attribute.
     * 
     * @param suppPubInfo SuppPubInfo attribute in its padded byte array form
     */
    void setSuppPubInfoBytes(@Nullable final byte[] suppPubInfo);
    
    /**
     * Gets the SuppPrivInfo attribute in its padded hex-encoded form.
     * 
     * @return the SuppPrivInfo attribute
     */
    @Nullable String getSuppPrivInfo();

    /**
     * Sets the SuppPrivInfo attribute.
     * 
     * @param suppPrivInfo SuppPrivInfo attribute in its padded hex-encoded form
     */
    void setSuppPrivInfo(@Nullable final String suppPrivInfo);

    /**
     * Gets the SuppPrivInfo attribute in its padded byte array form.
     * 
     * @return the SuppPrivInfo attribute
     */
    @Nullable byte[] getSuppPrivInfoBytes();

    /**
     * Sets the SuppPrivInfo attribute.
     * 
     * @param suppPrivInfo SuppPrivInfo attribute in its padded byte array form
     */
    void setSuppPrivInfoBytes(@Nullable final byte[] suppPrivInfo);

}
