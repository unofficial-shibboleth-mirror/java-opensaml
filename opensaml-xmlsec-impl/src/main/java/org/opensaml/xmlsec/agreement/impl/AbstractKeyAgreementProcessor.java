/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import javax.annotation.Nullable;
import javax.crypto.SecretKey;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.agreement.KeyAgreementCredential;
import org.opensaml.xmlsec.agreement.KeyAgreementException;
import org.opensaml.xmlsec.agreement.KeyAgreementParameters;
import org.opensaml.xmlsec.agreement.KeyAgreementProcessor;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Abstract base class for {@link KeyAgreementProcessor} implementations.
 */
public abstract class AbstractKeyAgreementProcessor implements KeyAgreementProcessor {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractKeyAgreementProcessor.class);

    /** {@inheritDoc} */
    @Nonnull public KeyAgreementCredential execute(@Nonnull final Credential publicCredential,
            @Nonnull final String keyAlgorithm, @Nonnull final KeyAgreementParameters inputParameters)
                    throws KeyAgreementException {
        
        // Make a copy so methods can store items without mutating the input instance
        final KeyAgreementParameters parameters = new KeyAgreementParameters(inputParameters);
        
        final Credential privateCredential = obtainPrivateCredential(publicCredential, parameters);
        if (privateCredential == null) {
            throw new KeyAgreementException("Unable to obtain or derive private key");
        }
        
        final byte[] secret = generateAgreementSecret(publicCredential, privateCredential, parameters);
        
        final SecretKey derivedKey = deriveSecretKey(secret, keyAlgorithm, parameters);
        
        return buildKeyAgreementCredential(derivedKey, publicCredential, privateCredential, parameters);
    }
    
    /**
     * Obtain the private credential which is compatible with the given public credential.
     * 
     * @param publicCredential the public credential
     * @param parameters the key agreement parameters
     * 
     * @return the obtained private credential
     * 
     * @throws KeyAgreementException if private credential can not be obtained
     */
    @Nullable protected Credential obtainPrivateCredential(@Nonnull final Credential publicCredential,
            @Nonnull final KeyAgreementParameters parameters) throws KeyAgreementException {
        
        final PrivateCredential priv = parameters.get(PrivateCredential.class);
        if (priv != null) {
            log.debug("Found supplied PrivateCredential in KeyAgreementParameters");
            return priv.getCredential();
        }
        return null;
        
    }
    
    /**
     * Generate the agreement secret according to the key algorithm and using the supplied
     * public and private credentials.
     * 
     * @param publicCredential the public credential
     * @param privateCredential the private credential
     * @param parameters the key agreement parameters
     * 
     * @return the secret produced by the key agreement operation
     * 
     * @throws KeyAgreementException if secret generation fails
     */
    @Nonnull protected abstract byte[] generateAgreementSecret(@Nonnull final Credential publicCredential,
            @Nonnull final Credential privateCredential, @Nonnull final KeyAgreementParameters parameters)
                throws KeyAgreementException;
    
    /**
     * Derive a {@link SecretKey} from a given secret.
     * 
     * @param secret the input secret
     * @param keyAlgorithm the JCA key algorithm for the derived key
     * @param parameters the key agreement parameters
     * 
     * @return the derived secret key
     * 
     * @throws KeyAgreementException if key derivation operation does not complete successfully
     */
    @Nonnull protected abstract SecretKey deriveSecretKey(@Nonnull final byte[] secret,
            @Nonnull final String keyAlgorithm, @Nonnull final KeyAgreementParameters parameters)
                    throws KeyAgreementException;
    
    /**
     * Build the final {@link KeyAgreementCredential} from the given inputs.
     * 
     * @param derivedKey the derived secret key
     * @param publicCredential the public credential
     * @param privateCredential the private credential
     * @param parameters the key agreement parameters
     * 
     * @return the new key agreement credential
     * 
     * @throws KeyAgreementException if credential can not be successfully constructed
     */
    @Nonnull protected KeyAgreementCredential buildKeyAgreementCredential(@Nonnull final SecretKey derivedKey,
            @Nonnull final Credential publicCredential, @Nonnull final Credential privateCredential,
            @Nonnull final KeyAgreementParameters parameters) throws KeyAgreementException {
        
        Credential recipient = null;
        Credential originator = null;
        
        if (parameters.contains(PrivateCredential.class) && ! parameters.contains(StaticStaticMode.class)) {
            // Decrypting party case
            recipient = privateCredential;
            originator = publicCredential;
        } else {
            // Encrypting party case
            recipient = publicCredential;
            originator = privateCredential;
        }
        
        final KeyAgreementCredential cred = new BasicKeyAgreementCredential(derivedKey, getAlgorithm(),
                originator, recipient);
        cred.getParameters().addAll(parameters);
        
        return cred;
    }

}