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

package org.opensaml.spring.credential;

import java.security.KeyException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.annotation.Nullable;
import javax.crypto.SecretKey;

import org.cryptacular.util.KeyPairUtil;
import org.opensaml.security.crypto.KeySupport;
import org.springframework.beans.factory.BeanCreationException;

/**
 * Factory bean for BasicInline Credentials. 
 */
public class BasicInlineCredentialFactoryBean extends AbstractBasicCredentialFactoryBean {

    /** Configured public key Info. */
    @Nullable private byte[] publicKeyInfo;

    /** Configured private key Info. */
    @Nullable private byte[] privateKeyInfo;

    /** Configured secret key Info. */
    @Nullable private byte[] secretKeyInfo;

    /**
     * Get the information used to generate the public key.
     * 
     * @return Returns the info.
     */
    @Nullable public byte[] getPublicKeyInfo() {
        return publicKeyInfo;
    }

    /**
     * Set the information used to generate the public key.
     * 
     * @param info The info to set.
     */
    public void setPublicKeyInfo(@Nullable final byte[] info) {
        publicKeyInfo = info;
    }

    /**
     * Get the information used to generate the private key.
     * 
     * @return Returns the info.
     */
    @Nullable public byte[] getPrivateKeyInfo() {
        return privateKeyInfo;
    }

    /**
     * Set the information used to generate the private key.
     * 
     * @param info The info to set.
     */
    public void setPrivateKeyInfo(@Nullable final byte[] info) {
        privateKeyInfo = info;
    }

    /**
     * Get the information used to generate the secret key.
     * 
     * @return Returns the info.
     */
    @Nullable public byte[] getSecretKeyInfo() {
        return secretKeyInfo;
    }

    /**
     * Set the information used to generate the secret key.
     * 
     * @param info The info to set.
     */
    public void setSecretKeyInfo(@Nullable final byte[] info) {
        secretKeyInfo = info;
    }

    /** {@inheritDoc} */
    @Override @Nullable protected PublicKey getPublicKey() {
        if (null == getPublicKeyInfo()) {
            return null;
        }
        return KeyPairUtil.decodePublicKey(getPublicKeyInfo());
    }

    /** {@inheritDoc} */
    @Override @Nullable protected PrivateKey getPrivateKey() {
        if (null == getPrivateKeyInfo()) {
            return null;
        }
        return KeyPairUtil.decodePrivateKey(getPrivateKeyInfo(), getPrivateKeyPassword());
    }

    /** {@inheritDoc} */
    @Override @Nullable protected SecretKey getSecretKey() {
        if (null ==  getSecretKeyInfo()) {
            return null;
        }
        try {
            final String alg = getSecretKeyAlgorithm();
            if (alg == null) {
                throw new KeyException("Key algorithm was null");
            }
            return KeySupport.decodeSecretKey(decodeSecretKey(getSecretKeyInfo()), alg);
        } catch (final KeyException e) {
            throw new BeanCreationException("Could not decode secret key", e);
        }
    }
}
