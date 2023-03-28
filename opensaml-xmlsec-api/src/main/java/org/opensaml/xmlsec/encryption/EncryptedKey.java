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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.xmlsec.encryption.support.EncryptionConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * XMLObject representing XML Encryption, version 20021210, EncryptedKey element.
 */
public interface EncryptedKey extends EncryptedType {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "EncryptedKey";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(EncryptionConstants.XMLENC_NS,
            DEFAULT_ELEMENT_LOCAL_NAME, EncryptionConstants.XMLENC_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "EncryptedKeyType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(EncryptionConstants.XMLENC_NS, TYPE_LOCAL_NAME,
            EncryptionConstants.XMLENC_PREFIX);

    /** Recipient attribute name. */
    @Nonnull @NotEmpty static final String RECIPIENT_ATTRIB_NAME = "Recipient";

    /**
     * Gets the hint about for whom this encrypted key is intended.
     * 
     * @return the hint about who this encrypted key is intended for
     */
    @Nullable String getRecipient();

    /**
     * Sets the hint about for whom this encrypted key is intended.
     * 
     * @param newRecipient the hint about who this encrypted key is intended for
     */
    void setRecipient(@Nullable final String newRecipient);

    /**
     * Gets the child element containing pointers to EncryptedData and EncryptedKey elements encrypted using this key.
     * 
     * @return the element containing a list of pointers to encrypted elements
     */
    @Nullable ReferenceList getReferenceList();

    /**
     * Sets the child element containing pointers to EncryptedData and EncryptedKey elements encrypted using this key.
     * 
     * @param newReferenceList the new reference list for this encrypted key
     */
    void setReferenceList(@Nullable final ReferenceList newReferenceList);

    /**
     * Gets the child element carrying the human readable name for this key.
     * 
     * @return the human readable name for this key
     */
    @Nullable CarriedKeyName getCarriedKeyName();

    /**
     * Sets the child element carrying the human readable name for this key.
     * 
     * @param newCarriedKeyName the human readable name for this key
     */
    void setCarriedKeyName(@Nullable final CarriedKeyName newCarriedKeyName);

}