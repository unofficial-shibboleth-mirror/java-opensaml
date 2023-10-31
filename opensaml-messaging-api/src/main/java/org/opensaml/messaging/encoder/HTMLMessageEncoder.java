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

package org.opensaml.messaging.encoder;

import javax.annotation.Nullable;

import net.shibboleth.shared.codec.StringDigester;
import net.shibboleth.shared.security.IdentifierGenerationStrategy;

/**
 * Interface for a {@link MessageEncoder} that relies on HTML output, and thus requires CSP considerations.
 * 
 * @since 5.1.0
 */
public interface HTMLMessageEncoder extends MessageEncoder {
    
    /**
     * Set a {@link StringDigester} to use to generate CSP hashes.
     * 
     * @param digester string digester
     * 
     * @since 5.1.0
     */
    void setCSPDigester(@Nullable final StringDigester digester);
    
    /**
     * Set a {@link IdentifierGenerationStrategy} to use to generate CSP nonces.
     * 
     * @param strategy nonce strategy
     * 
     * @since 5.1.0
     */
    void setCSPNonceGenerator(@Nullable final IdentifierGenerationStrategy strategy);

}