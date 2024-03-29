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

package org.opensaml.security.credential;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;

/**
 * A basic implementation of {@link Credential}.
 */
public class BasicCredential extends AbstractCredential implements MutableCredential {

    /**
     * Default constructor.
     */
    public BasicCredential() {
    }

    /**
     * Constructor.
     *
     * @param publicKey the credential public key
     */
    public BasicCredential(@Nonnull final PublicKey publicKey) {
        setPublicKey(publicKey);
    }
    
    /**
     * Constructor.
     *
     * @param publicKey the credential public key
     * @param privateKey the credential private key
     */
    public BasicCredential(@Nonnull final PublicKey publicKey, @Nullable final PrivateKey privateKey) {
        setPublicKey(publicKey);
        if (privateKey != null) {
            setPrivateKey(privateKey);
        }
    }
    
    /**
     * Constructor.
     *
     * @param secretKey the credential secret key
     */
    public BasicCredential(@Nonnull final SecretKey secretKey) {
        setSecretKey(secretKey);
    }
    
    /** {@inheritDoc} */
    @Nonnull public Class<? extends Credential> getCredentialType() {
        return Credential.class;
    }

    /** {@inheritDoc} */
    public void setEntityId(@Nullable final String newEntityId) {
        super.setEntityId(newEntityId);
    }

    /** {@inheritDoc} */
    public void setUsageType(@Nonnull final UsageType newUsageType) {
        super.setUsageType(newUsageType);
    }

    /** {@inheritDoc} */
    public void setPublicKey(@Nonnull final PublicKey newPublicKey) {
        super.setPublicKey(newPublicKey);
    }
    
    /** {@inheritDoc} */
    public void setPrivateKey(@Nonnull final PrivateKey newPrivateKey) {
        super.setPrivateKey(newPrivateKey);
    }

    /** {@inheritDoc} */
    public void setSecretKey(@Nonnull final SecretKey newSecretKey) {
        super.setSecretKey(newSecretKey);
    }

}