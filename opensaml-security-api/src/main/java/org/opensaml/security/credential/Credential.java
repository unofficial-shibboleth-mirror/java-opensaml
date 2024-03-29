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
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;

/**
 * A credential for an entity. A particular credential may contain either asymmetric key information (a public key 
 * and optionally the corresponding private key), or a symmetric (secret) key, but never both.
 * With asymmetric key-based credentials, local entity credentials will usually contain both a public 
 * and private key while peer credentials will normally contain only a public key. 
 */
public interface Credential {
    
    /**
     * The unique ID of the entity this credential is for.
     * 
     * @return unique ID of the entity this credential is for
     */
    @Nullable String getEntityId();
    
    /**
     * Gets usage type of this credential.
     * 
     * @return usage type of this credential
     */
    @Nullable UsageType getUsageType();
    
    /**
     * Gets key names for this credential. These names may be used to reference a key(s) exchanged 
     * through an out-of-band agreement. Implementations may or may not implement means to resolve 
     * these names into keys retrievable through the {@link #getPublicKey()}, {@link #getPrivateKey()} 
     * or {@link #getSecretKey()} methods.
     * 
     * @return key names for this credential
     */
    @Nonnull Collection<String> getKeyNames();

    /**
     * Gets the public key for the entity.
     * 
     * @return public key for the entity
     */
    @Nullable PublicKey getPublicKey();

    /**
     * Gets the private key for the entity if there is one.
     * 
     * @return the private key for the entity
     */
    @Nullable PrivateKey getPrivateKey();
    
    /**
     * Gets the secret key for this entity.
     * 
     * @return secret key for this entity
     */
    @Nullable SecretKey getSecretKey();
    
    /**
     * Get the set of credential context information, which provides additional information
     * specific to the contexts in which the credential was resolved.
     * 
     * @return set of resolution contexts of the credential
     */
    @Nullable CredentialContextSet getCredentialContextSet();
    
    /**
     * Get the primary type of the credential instance. This will usually be the primary sub-interface
     * of {@link Credential} implemented by an implementation.
     * 
     * @return the credential type
     */
    @Nonnull Class<? extends Credential> getCredentialType();
}