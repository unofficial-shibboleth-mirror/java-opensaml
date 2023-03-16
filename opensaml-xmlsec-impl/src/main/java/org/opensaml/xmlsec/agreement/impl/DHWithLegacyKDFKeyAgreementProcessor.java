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

package org.opensaml.xmlsec.agreement.impl;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;

import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.crypto.dh.DHSupport;
import org.opensaml.xmlsec.agreement.KeyAgreementException;
import org.opensaml.xmlsec.agreement.KeyAgreementParameters;
import org.opensaml.xmlsec.agreement.KeyAgreementProcessor;
import org.opensaml.xmlsec.agreement.KeyAgreementSupport;
import org.opensaml.xmlsec.derivation.KeyDerivationException;
import org.opensaml.xmlsec.derivation.impl.DHLegacyKDF;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Implementation of {@link KeyAgreementProcessor} which performs Diffie-Hellman
 * Ephemeral-Static Mode key agreement with Legacy Key Derivation Function as defined in XML Encryption 1.1.
 */
public class DHWithLegacyKDFKeyAgreementProcessor extends AbstractKeyAgreementProcessor {
    
    /** Default digest method. */
    @Nonnull @NotEmpty public static final String DEFAULT_DIGEST_METHOD = EncryptionConstants.ALGO_ID_DIGEST_SHA256;
    
    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(DHWithLegacyKDFKeyAgreementProcessor.class);

    /** {@inheritDoc} */
    public String getAlgorithm() {
        return EncryptionConstants.ALGO_ID_KEYAGREEMENT_DH;
    }

    /** {@inheritDoc} */
    protected Credential obtainPrivateCredential(@Nonnull final Credential publicCredential,
            @Nonnull final KeyAgreementParameters parameters) throws KeyAgreementException {
        
        final Credential suppliedCredential = super.obtainPrivateCredential(publicCredential, parameters);
        if (suppliedCredential != null) {
            return suppliedCredential;
        }
        
        log.debug("Found no supplied PrivateCredential in KeyAgreementParameters, generating ephemeral key pair");
        
        
        if (!DHPublicKey.class.isInstance(publicCredential.getPublicKey())) {
            throw new KeyAgreementException("Public credential's public key is not an instance of DHPublicKey");
        }
        
        final DHPublicKey publicKey = DHPublicKey.class.cast(publicCredential.getPublicKey());
        
        try {
            final KeyPair privateKeyPair = DHSupport.generateCompatibleKeyPair(publicKey, null);
            return new BasicCredential(privateKeyPair.getPublic(), privateKeyPair.getPrivate());
        } catch (final NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new KeyAgreementException("Error generating private KeyPair from DH public key", e);
        }
    }

    /** {@inheritDoc} */
    protected byte[] generateAgreementSecret(@Nonnull final Credential publicCredential,
            @Nonnull final Credential privateCredential, @Nonnull final KeyAgreementParameters parameters)
                    throws KeyAgreementException {
        
        if (!DHPublicKey.class.isInstance(publicCredential.getPublicKey())) {
            throw new KeyAgreementException("Public credential's public key is not an instance of DHPublicKey");
        }
        if (!DHPrivateKey.class.isInstance(privateCredential.getPrivateKey())) {
            throw new KeyAgreementException("Private credential's private key is not an instance of DHPrivateKey");
        }
        
        final DHPublicKey publicKey = DHPublicKey.class.cast(publicCredential.getPublicKey());
        final DHPrivateKey privateKey = DHPrivateKey.class.cast(privateCredential.getPrivateKey());
        
        try {
            return DHSupport.performKeyAgreement(publicKey, privateKey, null);
        } catch (final InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new KeyAgreementException("Error generating secret from public and private DH keys", e);
        }
    }

    /** {@inheritDoc} */
    protected SecretKey deriveSecretKey(final byte[] secret, @Nonnull final String keyAlgorithm,
            @Nonnull final KeyAgreementParameters parameters) throws KeyAgreementException {
        
        final Integer keySize = parameters.contains(KeySize.class) ? parameters.get(KeySize.class).getSize() : null;
        
        KeyAgreementSupport.validateKeyAlgorithmAndSize(keyAlgorithm, keySize);
        
        String digestMethod = null;
        if (parameters.contains(DigestMethod.class)) {
            digestMethod = parameters.get(DigestMethod.class).getAlgorithm();
        } else {
            digestMethod = DEFAULT_DIGEST_METHOD;
            // Need to add this to params so can be expressed on credential and in XML
            final DigestMethod dm = new DigestMethod();
            dm.setAlgorithm(digestMethod);
            parameters.add(dm);
        }
        
        // Nonce is optional
        String nonce = null;
        if (parameters.contains(KANonce.class)) {
            nonce = parameters.get(KANonce.class).getValue();
        }
        
        final DHLegacyKDF kdf = new DHLegacyKDF();
        kdf.setDigestMethod(digestMethod);
        kdf.setNonce(nonce);
        
        try {
            return kdf.derive(secret, keyAlgorithm, keySize);
        } catch (final KeyDerivationException e) {
            throw new KeyAgreementException("Key derivation failed using supplied KeyDerivation parameter", e);
        }
        
    }

}
