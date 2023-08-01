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
 * A specialization of {@link Credential} which supports mutation of its properties.
 */
public interface MutableCredential extends Credential {
    
    /**
     * Sets the ID of the entity this credential is for.
     * 
     * @param newEntityID ID of the entity this credential is for
     */
    void setEntityId(@Nullable final String newEntityID);

    /**
     * Sets the usage type for this credential.
     * 
     * @param newUsageType usage type for this credential
     */
    void setUsageType(@Nonnull final UsageType newUsageType);

    /**
     * Sets the public key for this credential.
     * 
     * @param newPublicKey public key for this credential
     */
    void setPublicKey(@Nonnull final PublicKey newPublicKey);
    
    /**
     * Sets the private key for this credential.
     * 
     * @param newPrivateKey private key for this credential
     */
    void setPrivateKey(@Nonnull final PrivateKey newPrivateKey);

    /**
     * Sets the secret key for this credential.
     * 
     * @param newSecretKey secret key for this credential
     */ 
    void setSecretKey(@Nonnull final SecretKey newSecretKey);

}
