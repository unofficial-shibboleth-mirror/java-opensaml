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

package org.opensaml.security.x509;

import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.credential.Credential;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * An entity credential based on key material and other information (e.g. certificates and certificate
 * revocation lists) associated with X.509 Public Key Infrastructure.
 * 
 * Note that this type of credential may not contain a symmetric (secret) key, and hence 
 * {@link Credential#getSecretKey()} should always return null.  
 */
public interface X509Credential extends Credential {
    
    /**
     * Gets the public key certificate for the entity. The public key of this certificate will be 
     * the same key obtained from {@link Credential#getPublicKey()}.
     * 
     * @return the public key certificate for the entity
     */
    @Nonnull X509Certificate getEntityCertificate();

    /**
     * Gets an immutable collection of certificates in the entity's trust chain. The entity certificate is contained
     * within this list. No specific ordering of the certificates is guaranteed.
     * 
     * @return entities certificate chain
     */
    @Nonnull @Unmodifiable @NotLive Collection<X509Certificate> getEntityCertificateChain();

    /**
     * Gets a collection of CRLs associated with the credential.
     * 
     * @return CRLs associated with the credential
     */
    @Nullable @Unmodifiable @NotLive Collection<X509CRL> getCRLs();

}