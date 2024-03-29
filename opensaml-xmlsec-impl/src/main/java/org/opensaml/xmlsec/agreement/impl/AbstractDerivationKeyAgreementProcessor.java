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
import javax.crypto.SecretKey;

import org.opensaml.xmlsec.agreement.KeyAgreementException;
import org.opensaml.xmlsec.agreement.KeyAgreementParameters;
import org.opensaml.xmlsec.agreement.KeyAgreementProcessor;
import org.opensaml.xmlsec.agreement.KeyAgreementSupport;
import org.opensaml.xmlsec.derivation.KeyDerivation;
import org.opensaml.xmlsec.derivation.KeyDerivationException;

/**
 * Abstract base class for {@link KeyAgreementProcessor} implementations which do key derivation by means of
 * a required {@link KeyDerivation} parameter.
 */
public abstract class AbstractDerivationKeyAgreementProcessor extends AbstractKeyAgreementProcessor {

    /** {@inheritDoc} */
    @Nonnull protected SecretKey deriveSecretKey(@Nonnull final byte[] secret, @Nonnull final String keyAlgorithm,
            @Nonnull final KeyAgreementParameters parameters) throws KeyAgreementException {
        
        final KeyDerivation keyDerivation = parameters.stream()
                .filter(KeyDerivation.class::isInstance)
                .map(KeyDerivation.class::cast)
                .findFirst()
                .orElse(null);
        if (keyDerivation == null) {
            throw new KeyAgreementException("Required KeyDerivation parameter was not supplied");
        }
        
        final KeySize keySizeParam = parameters.get(KeySize.class);
        final Integer keySize = keySizeParam != null ? keySizeParam.getSize() : null;
        
        KeyAgreementSupport.validateKeyAlgorithmAndSize(keyAlgorithm, keySize);
        
        try {
            return keyDerivation.derive(secret, keyAlgorithm, keySize);
        } catch (final KeyDerivationException e) {
            throw new KeyAgreementException("Key derivation failed using supplied KeyDerivation parameter", e);
        }
    }

}
