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
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.crypto.ec.ECSupport;
import org.opensaml.xmlsec.agreement.KeyAgreementException;
import org.opensaml.xmlsec.agreement.KeyAgreementParameters;
import org.opensaml.xmlsec.agreement.KeyAgreementProcessor;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Implementation of {@link KeyAgreementProcessor} which performs Elliptic Curve Diffie-Hellman (ECDH)
 * Ephemeral-Static Mode key agreement as defined in XML Encryption 1.1.
 */
public class ECDHKeyAgreementProcessor extends AbstractDerivationKeyAgreementProcessor {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ECDHKeyAgreementProcessor.class);

    /** {@inheritDoc} */
    @Nonnull public String getAlgorithm() {
        return EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES;
    }

    /** {@inheritDoc} */
    @Nullable protected Credential obtainPrivateCredential(@Nonnull final Credential publicCredential,
            @Nonnull final KeyAgreementParameters parameters) throws KeyAgreementException {
        
        final Credential suppliedCredential = super.obtainPrivateCredential(publicCredential, parameters);
        if (suppliedCredential != null) {
            return suppliedCredential;
        }
        
        log.debug("Found no supplied PrivateCredential in KeyAgreementParameters, generating ephemeral key pair");
        
        
        if (!ECPublicKey.class.isInstance(publicCredential.getPublicKey())) {
            throw new KeyAgreementException("Public credential's public key is not an instance of ECPublicKey");
        }
        
        final ECPublicKey publicKey = ECPublicKey.class.cast(publicCredential.getPublicKey());
        
        try {
            final KeyPair privateKeyPair = ECSupport.generateCompatibleKeyPair(publicKey, null);
            return new BasicCredential(privateKeyPair.getPublic(), privateKeyPair.getPrivate());
        } catch (final NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new KeyAgreementException("Error generating private KeyPair from EC public key", e);
        }
    }

    /** {@inheritDoc} */
    @Nonnull protected byte[] generateAgreementSecret(@Nonnull final Credential publicCredential,
            @Nonnull final Credential privateCredential, @Nonnull final KeyAgreementParameters parameters)
                    throws KeyAgreementException {
        
        if (!ECPublicKey.class.isInstance(publicCredential.getPublicKey())) {
            throw new KeyAgreementException("Public credential's public key is not an instance of ECPublicKey");
        }
        if (!ECPrivateKey.class.isInstance(privateCredential.getPrivateKey())) {
            throw new KeyAgreementException("Private credential's private key is not an instance of ECPrivateKey");
        }
        
        final ECPublicKey publicKey = ECPublicKey.class.cast(publicCredential.getPublicKey());
        final ECPrivateKey privateKey = ECPrivateKey.class.cast(privateCredential.getPrivateKey());
        
        try {
            return ECSupport.performKeyAgreement(publicKey, privateKey, null);
        } catch (final InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new KeyAgreementException("Error generating secret from public and private EC keys", e);
        }
    }

}