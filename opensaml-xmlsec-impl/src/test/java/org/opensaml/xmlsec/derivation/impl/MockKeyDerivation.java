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

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.SecretKey;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.derivation.KeyDerivation;
import org.opensaml.xmlsec.derivation.KeyDerivationException;
import org.opensaml.xmlsec.encryption.KeyDerivationMethod;

/**
 * Mock key derivation for testing.
 */
public class MockKeyDerivation implements KeyDerivation {

    /** {@inheritDoc} */
    public XMLObject buildXMLObject() {
        final KeyDerivationMethod method = (KeyDerivationMethod) XMLObjectSupport.buildXMLObject(KeyDerivationMethod.DEFAULT_ELEMENT_NAME);
        method.setAlgorithm(getAlgorithm());
        return method;
    }

    /** {@inheritDoc} */
    public String getAlgorithm() {
        return "urn:test:MockKeyDerivation";
    }

    /** {@inheritDoc} */
    public SecretKey derive(byte[] secret, String keyAlgorithm, Integer keyLength) throws KeyDerivationException {
        try {
            return KeySupport.generateKey(keyAlgorithm, keyLength, null);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new KeyDerivationException("Error generating mock derived key", e);
        }
    }

}
