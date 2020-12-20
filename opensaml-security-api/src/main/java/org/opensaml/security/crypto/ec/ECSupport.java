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

package org.opensaml.security.crypto.ec;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.KeyAgreement;

import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.crypto.KeySupport;

/**
 * Cryptography support related to Elliptic Curve.
 */
public final class ECSupport {
    
    /** Constructor. */
    private ECSupport() { }
    
    /**
     * Perform ECDH key agreement between the given public and private keys.
     * 
     * @param publicKeyKey the public key
     * @param privateKey the private key
     * @param provider the optional security provider to use
     * 
     * @return the secret produced by key agreement
     * 
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidKeyException
     */
    public static byte[] performKeyAgreement(@Nonnull final ECPublicKey publicKeyKey,
            @Nonnull final ECPrivateKey privateKey, @Nullable final String provider)
                    throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        
        KeyAgreement keyAgreement = null;
        if (provider != null) {
            keyAgreement = KeyAgreement.getInstance(JCAConstants.KEY_AGREEMENT_ECDH, provider);
        } else {
            keyAgreement = KeyAgreement.getInstance(JCAConstants.KEY_AGREEMENT_ECDH);
        }
        
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKeyKey, true);
        return keyAgreement.generateSecret();
    }

    /**
     * Generate a key pair whose parameters are compatible with those of the specified EC public key.
     * 
     * @param publicKey the public key
     * @param provider the optional security provider to use
     * 
     * @return the generated key pair
     * 
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidAlgorithmParameterException
     */
    public static KeyPair generateCompatibleKeyPair(@Nonnull final ECPublicKey publicKey,
            @Nullable final String provider)
                    throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        return KeySupport.generateKeyPair(JCAConstants.KEY_ALGO_EC, publicKey.getParams(), provider);
    }
    
}
