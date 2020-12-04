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

/**
 * XMLObject representing XML Encryption 1.1 DerivedKey element.
 */
public interface DerivedKey extends XMLObject {
    
    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "DerivedKey";

    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME =
            new QName(EncryptionConstants.XMLENC11_NS, DEFAULT_ELEMENT_LOCAL_NAME, EncryptionConstants.XMLENC11_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "DerivedKeyType";

    /** QName of the XSI type. */
    public static final QName TYPE_NAME =
            new QName(EncryptionConstants.XMLENC11_NS, TYPE_LOCAL_NAME, EncryptionConstants.XMLENC11_PREFIX);

    /** Recipient attribute name. */
    public static final String RECIPIENT_ATTRIBUTE_NAME = "Recipient";

    /** Algorithm attribute name. */
    public static final String ID_ATTRIBUTE_NAME = "Id";

    /** Algorithm attribute name. */
    public static final String TYPE_ATTRIBUTE_NAME = "Type";
    
    /**
     * Get the KeyDerivationMethod child element.
     * 
     * @return the element
     */
    @Nullable public KeyDerivationMethod getKeyDerivationMethod();
    
    /**
     * Set the KeyDerivationMethod child element.
     * 
     * @param method the key derivation method
     */
    public void setKeyDerivationMethod(@Nullable final KeyDerivationMethod method);

    /**
     * Get the ReferenceList child element.
     * 
     * @return the element
     */
    @Nullable public ReferenceList getReferenceList();
    
    /**
     * Set the ReferenceList child element.
     * 
     * @param referenceList the list
     */
    public void setReferenceList(@Nullable final ReferenceList referenceList);

    /**
     * Get the DerivedKeyName child element.
     * 
     * @return the element
     */
    @Nullable public DerivedKeyName getDerivedKeyName();
    
    /**
     * Set the DerivedKeyName child element.
     * 
     * @param name the key name
     */
    public void setDerivedKeyName(@Nullable final DerivedKeyName name);

    /**
     * Get the MasterKeyName child element.
     * 
     * @return the element
     */
    @Nullable public MasterKeyName getMasterKeyName();
    
    /**
     * Set the MasterKeyName child element.
     * 
     * @param name the key name
     */
    public void setMasterKeyName(@Nullable final MasterKeyName name);

    /**
     * Gets the Recipient attribute.
     * 
     * @return the recipient
     */
    @Nullable String getRecipient();

    /**
     * Sets the Recipient attribute.
     * 
     * @param recipient the recipient
     */
    void setRecipient(@Nullable final String recipient);

    /**
     * Gets the Id attribute.
     * 
     * @return the id
     */
    @Nullable String getId();

    /**
     * Sets the Id attribute.
     * 
     * @param id the id
     */
    void setId(@Nullable final String id);

    /**
     * Gets the Type attribute.
     * 
     * @return the type
     */
    @Nullable String getType();

    /**
     * Sets the Type attribute.
     * 
     * @param type the type
     */
    void setType(@Nullable final String type);

}
