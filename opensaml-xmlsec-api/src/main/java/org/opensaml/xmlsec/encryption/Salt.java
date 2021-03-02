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
 * XMLObject representing XML Encryption 1.1 Salt element.
 */
public interface Salt extends XMLObject {
    
    /** Element local name. */
    public static final String DEFAULT_ELEMENT_LOCAL_NAME = "Salt";

    /** Default element name. */
    public static final QName DEFAULT_ELEMENT_NAME =
            new QName(EncryptionConstants.XMLENC11_NS, DEFAULT_ELEMENT_LOCAL_NAME, EncryptionConstants.XMLENC11_PREFIX);
    
    /**
     * Get the Specified child element.
     * 
     * @return the element
     */
    @Nullable public Specified getSpecified();
    
    /**
     * Set the Specified child element.
     * 
     * @param specified the element
     */
    public void setSpecified(@Nullable final Specified specified);
    
    /**
     * Get the OtherSource child element.
     * 
     * @return the element
     */
    @Nullable public OtherSource getOtherSource();
    
    /**
     * Set the OtherSource child element.
     * 
     * @param source value
     */
    public void setOtherSource(@Nullable final OtherSource source);
    
}
