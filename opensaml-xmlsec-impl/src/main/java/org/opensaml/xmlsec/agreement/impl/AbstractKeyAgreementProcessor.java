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

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.agreement.KeyAgreementCredential;
import org.opensaml.xmlsec.agreement.KeyAgreementException;
import org.opensaml.xmlsec.agreement.KeyAgreementParameters;
import org.opensaml.xmlsec.agreement.KeyAgreementProcessor;

/**
 * Abstract base class for {@link KeyAgreementProcessor} implementations.
 */
public abstract class AbstractKeyAgreementProcessor implements KeyAgreementProcessor {

    /** {@inheritDoc} */
    @Nonnull public KeyAgreementCredential execute(@Nonnull final Credential recipientCredential,
            @Nonnull final String keyAlgorithm, @Nonnull final Integer keyLength,
            @Nonnull final KeyAgreementParameters parameters) throws KeyAgreementException {
        
        final Credential originatorCredential = obtainOriginatorCredential(recipientCredential, parameters);
        
        final byte[] secret = generateAgreementSecret(recipientCredential, originatorCredential, parameters);
        
        final SecretKey derivedKey = deriveSecretKey(secret, keyAlgorithm, keyLength, parameters);
        
        return buildKeyAgreementCredential(derivedKey, recipientCredential, originatorCredential, parameters);
    }
    
    /**
     * Obtain an originator credential which is compatible with the given recipient credential.
     * 
     * @param recipientCredential the recipient credential
     * @param parameters the key agreement parameters
     * 
     * @return the obtained originator credential
     * 
     * @throws KeyAgreementException
     */
    @Nonnull protected abstract Credential obtainOriginatorCredential(@Nonnull final Credential recipientCredential,
            @Nonnull final KeyAgreementParameters parameters) throws KeyAgreementException;
    
    /**
     * Generate the agreement secret according to the key algorithm and using the supplied
     * originator and recipient credentials.
     * 
     * @param recipientCredential the recipient credential
     * @param originatorCredential the originator credential
     * @param parameters the key agreement parameters
     * 
     * @return the obtained originator credential
     * 
     * @throws KeyAgreementException
     */
    @Nonnull protected abstract byte[] generateAgreementSecret(@Nonnull final Credential recipientCredential,
            @Nonnull final Credential originatorCredential, @Nonnull final KeyAgreementParameters parameters)
                throws KeyAgreementException;
    
    /**
     * Derive a {@link SecretKey} from a given secret.
     * 
     * @param secret the input secret
     * @param keyAlgorithm the JCA key algorithm for the derived key
     * @param keyLength the key length for the derived key
     * @param parameters the key agreement parameters
     * 
     * @return the derived secret key
     * 
     * @throws KeyAgreementException
     */
    @Nonnull protected abstract SecretKey deriveSecretKey(@Nonnull final byte[] secret,
            @Nonnull final String keyAlgorithm, @Nonnull final Integer keyLength,
            @Nonnull final KeyAgreementParameters parameters) throws KeyAgreementException;
    
    /**
     * Build the final {@link KeyAgreementCredential} from the given inputs.
     * 
     * @param derivedKey the derived secret key
     * @param recipientCredential the recipient credential
     * @param originatorCredential the originator credential
     * @param parameters the key agreement parameters
     * 
     * @return the new key agreement credential
     * 
     * @throws KeyAgreementException
     */
    @Nonnull protected KeyAgreementCredential buildKeyAgreementCredential(@Nonnull final SecretKey derivedKey,
            @Nonnull final Credential recipientCredential, @Nonnull final Credential originatorCredential,
            @Nonnull final KeyAgreementParameters parameters) throws KeyAgreementException {
        
        final KeyAgreementCredential cred = new BasicKeyAgreementCredential(derivedKey, getAlgorithm(),
                originatorCredential, recipientCredential);
        cred.getParameters().addAll(parameters);
        
        return cred;
    }

}
