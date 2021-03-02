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
 * XMLObject representing XML Encryption 1.1 PBKDF2-params element.
 */
public interface PBKDF2Params extends XMLObject {
    
    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "PBKDF2-params";

    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME =
            new QName(EncryptionConstants.XMLENC11_NS, DEFAULT_ELEMENT_LOCAL_NAME, EncryptionConstants.XMLENC11_PREFIX);

    /** Local name of the XSI type. */
    public static final String TYPE_LOCAL_NAME = "PBKDF2ParameterType";

    /** QName of the XSI type. */
    public static final QName TYPE_NAME =
            new QName(EncryptionConstants.XMLENC11_NS, TYPE_LOCAL_NAME, EncryptionConstants.XMLENC11_PREFIX);

    /**
     * Get the Salt child element.
     * 
     * @return the element
     */
    @Nullable public Salt getSalt();
    
    /**
     * Set the Salt child element.
     * 
     * @param salt the salt
     */
    public void setSalt(@Nullable final Salt salt);
    
    /**
     * Get the IterationCount child element.
     * 
     * @return the element
     */
    @Nullable public IterationCount getIterationCount();
    
    /**
     * Set the IterationCount child element.
     * 
     * @param count the new iteration count
     */
    public void setIterationCount(@Nullable final IterationCount count);
    
    /**
     * Get the KeyLength child element.
     * 
     * @return the element
     */
    @Nullable public KeyLength getKeyLength();
    
    /**
     * Set the KeyLength child element.
     * 
     * @param length the new key length
     */
    public void setKeyLength(@Nullable final KeyLength length);
    
    /**
     * Get the PRF child element.
     * 
     * @return the element
     */
    @Nullable public PRF getPRF();
    
    /**
     * Set the PRF child element.
     * 
     * @param prf the new PRF element
     */
    public void setPRF(@Nullable final PRF prf);

}
