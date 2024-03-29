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

package org.opensaml.security.trust;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.SecurityException;

import net.shibboleth.shared.resolver.CriteriaSet;

/**
 * Evaluates the trustworthiness and validity of a token against 
 * implementation-specific requirements.
 *
 * @param <TokenType> the token type this trust engine evaluates
 */
public interface TrustEngine<TokenType> {
    
    /**
     * Validates the token against trusted information obtained in an
     * implementation-specific manner.
     *
     * @param token security token to validate
     * @param trustBasisCriteria criteria used to describe and/or resolve the information
     *          which serves as the basis for trust evaluation
     *
     * @return true iff the token is trusted and valid
     *
     * @throws SecurityException thrown if there is a problem validating the security token
     */
    boolean validate(@Nonnull final TokenType token, @Nullable final CriteriaSet trustBasisCriteria)
            throws SecurityException;
}