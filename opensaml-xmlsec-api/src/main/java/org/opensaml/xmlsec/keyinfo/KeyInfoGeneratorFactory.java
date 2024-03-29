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

package org.opensaml.xmlsec.keyinfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.encryption.OriginatorKeyInfo;
import org.opensaml.xmlsec.encryption.RecipientKeyInfo;
import org.opensaml.xmlsec.signature.KeyInfo;

/**
 * Interface for factories which produce {@link KeyInfoGenerator} instances.
 */
public interface KeyInfoGeneratorFactory {
    
    /**
     * Get a new instance of the generator type produced by the factory.
     * 
     * @return a new KeyInfoGenerator instance
     */
    @Nonnull KeyInfoGenerator newInstance();
    
    /**
     * Get a new instance of the generator type produced by the factory, and which generates
     * {@link KeyInfo} instances of the specified type, for example {@link OriginatorKeyInfo}
     * or {@link RecipientKeyInfo}.
     * 
     * @param type the type of element to produce. Null is interpreted as a standard {@link KeyInfo}.
     * 
     * @return a new KeyInfoGenerator instance
     */
    @Nonnull default KeyInfoGenerator newInstance(@Nullable final Class<? extends KeyInfo> type) {
        throw new UnsupportedOperationException("Method not implemented");
    }
    
    /**
     * Check whether the generators produced by this factory can handle the specified credential.
     * 
     * @param credential the credential to evaluate
     * @return true if the generators produced by this factory can handle the type of the specified credential,
     *          false otherwise
     */
    boolean handles(@Nonnull final Credential credential);
    
    /**
     * Get the type (interface) of the specific type of credential handled by generators produced by
     * this factory.  Primarily used as an index by manager implementions such as {@link KeyInfoGeneratorManager}.
     * 
     * @return the specifc type of credential handled by the generators produced by this factory
     */
    @Nonnull Class<? extends Credential> getCredentialType();
    
}