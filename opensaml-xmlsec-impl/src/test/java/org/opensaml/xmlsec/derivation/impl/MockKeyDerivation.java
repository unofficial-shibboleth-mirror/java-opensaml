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

import javax.crypto.SecretKey;

import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.derivation.KeyDerivation;
import org.opensaml.xmlsec.derivation.KeyDerivationException;

/**
 * Mock key derivation for testing.
 */
public class MockKeyDerivation implements KeyDerivation {

    /** {@inheritDoc} */
    public String getAlgorithm() {
        return "urn:test:MockKeyDerivation";
    }

    /** {@inheritDoc} */
    public SecretKey derive(byte[] secret, String keyAlgorithm, Integer keyLength) throws KeyDerivationException {
        try {
            String algo = AlgorithmSupport.getKeyAlgorithm(keyAlgorithm);
            Integer length = AlgorithmSupport.getKeyLength(keyAlgorithm);
            return KeySupport.generateKey(algo, length, null);
        } catch (Exception e) {
            throw new KeyDerivationException("Error generating mock derived key", e);
        }
    }

}
