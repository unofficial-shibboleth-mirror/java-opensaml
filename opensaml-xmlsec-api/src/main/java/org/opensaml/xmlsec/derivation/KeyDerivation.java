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

package org.opensaml.xmlsec.derivation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;

import org.opensaml.xmlsec.agreement.KeyAgreementParameter;
import org.opensaml.xmlsec.algorithm.KeyLengthSpecifiedAlgorithm;

/**
 * Component which represents a specific key derivation algorithm, and supports deriving a new {@link SecretKey}
 * via that algorithm.
 * 
 * <p>
 * Sub-types will usually contain additional configurable property inputs to the derivation operation.
 * </p>
 */
public interface KeyDerivation extends KeyAgreementParameter {
    
    /**
     * The key derivation algorithm URI.
     * 
     * @return the algorithm
     */
    @Nonnull public String getAlgorithm();

    /**
     * Derive a {@link SecretKey} from the specified secret.
     * 
     * @param secret the input secret from which to derive the key.
     * @param keyAlgorithm the algorithm URI for which the derived key will be used
     * @param keyLength the length of the derived key.  This may be null if the keyAlgorithm URI
     *                  implies a key length, for example if the URI represents a {@link KeyLengthSpecifiedAlgorithm}.
     *                  However if the URI implies a key length and this parameter value does not match that length,
     *                  that is an error and and exception will be thrown
     * 
     * @return the derived key
     * 
     * @throws KeyDerivationException
     */
    @Nonnull public SecretKey derive(@Nonnull final byte[] secret, @Nonnull final String keyAlgorithm,
            @Nullable final Integer keyLength) throws KeyDerivationException;
    
}
