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

import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.crypto.ec.ECSupport;
import org.opensaml.xmlsec.agreement.KeyAgreementException;
import org.opensaml.xmlsec.agreement.KeyAgreementParameters;
import org.opensaml.xmlsec.agreement.KeyAgreementProcessor;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;

/**
 * Implementation of {@link KeyAgreementProcessor} which performs Elliptic Curve Diffie-Hellman (ECDH)
 * Ephemeral-Static Mode key agreement as defined in XML Encryption 1.1.
 */
public class ECDHKeyAgreementProcessor extends AbstractDerivationKeyAgreementProcessor {

    /** {@inheritDoc} */
    public String getAlgorithm() {
        return EncryptionConstants.ALGO_ID_KEYAGREEMENT_ECDH_ES;
    }

    /** {@inheritDoc} */
    protected Credential obtainOriginatorCredential(@Nonnull final Credential recipientCredential,
            @Nonnull final KeyAgreementParameters parameters) throws KeyAgreementException {
        
        if (!ECPublicKey.class.isInstance(recipientCredential.getPublicKey())) {
            throw new KeyAgreementException("Recipient credential's public key is not an instance of ECPublicKey");
        }
        
        final ECPublicKey recipientPublicKey = ECPublicKey.class.cast(recipientCredential.getPublicKey());
        
        try {
            final KeyPair originatorKeyPair = ECSupport.generateCompatibleKeyPair(recipientPublicKey, null);
            return new BasicCredential(originatorKeyPair.getPublic(), originatorKeyPair.getPrivate());
        } catch (final NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new KeyAgreementException("Error generating originator KeyPair from recipient EC public key", e);
        }
    }

    /** {@inheritDoc} */
    protected byte[] generateAgreementSecret(@Nonnull final Credential recipientCredential,
            @Nonnull final Credential originatorCredential, @Nonnull final KeyAgreementParameters parameters)
                    throws KeyAgreementException {
        
        if (!ECPublicKey.class.isInstance(recipientCredential.getPublicKey())) {
            throw new KeyAgreementException("Recipient credential's public key is not an instance of ECPublicKey");
        }
        if (!ECPrivateKey.class.isInstance(originatorCredential.getPrivateKey())) {
            throw new KeyAgreementException("Originator credential's private key is not an instance of ECPublicKey");
        }
        
        final ECPublicKey recipient = ECPublicKey.class.cast(recipientCredential.getPublicKey());
        final ECPrivateKey originator = ECPrivateKey.class.cast(originatorCredential.getPrivateKey());
        
        try {
            return ECSupport.performKeyAgreement(recipient, originator, null);
        } catch (final InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new KeyAgreementException("Error generating secret from recipient and originator EC keys", e);
        }
    }

}
