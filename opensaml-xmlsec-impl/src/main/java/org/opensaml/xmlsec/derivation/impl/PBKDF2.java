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

package org.opensaml.xmlsec.derivation.impl;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.xmlsec.derivation.KeyDerivation;
import org.opensaml.xmlsec.derivation.KeyDerivationException;
import org.opensaml.xmlsec.encryption.ConcatKDFParams;
import org.opensaml.xmlsec.encryption.KeyDerivationMethod;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;

/**
 * Implementation of PBKDF2 key derivation as defined in XML Encryption 1.1.
 */
public class PBKDF2 implements KeyDerivation {

    /** {@inheritDoc} */
    public String getAlgorithm() {
        return EncryptionConstants.ALGO_ID_KEYDERIVATION_PBKDF2;
    }

    /** {@inheritDoc} */
    public SecretKey derive(@Nonnull final byte[] secret, @Nonnull final String keyAlgorithm)
            throws KeyDerivationException {
        
        // TODO Auto-generated method stub
        
        return null;
    }

    /** {@inheritDoc} */
    public XMLObject buildXMLObject() {
        final KeyDerivationMethod method =
                (KeyDerivationMethod) XMLObjectSupport.buildXMLObject(KeyDerivationMethod.DEFAULT_ELEMENT_NAME);
        method.setAlgorithm(getAlgorithm());
        
        final ConcatKDFParams params =
                (ConcatKDFParams) XMLObjectSupport.buildXMLObject(ConcatKDFParams.DEFAULT_ELEMENT_NAME);
        
        //TODO populate params based on properties 
        
        method.getUnknownXMLObjects().add(params);
        
        return method;
    }

}
