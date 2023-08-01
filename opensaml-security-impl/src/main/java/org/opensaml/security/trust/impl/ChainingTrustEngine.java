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

package org.opensaml.security.trust.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.SecurityException;
import org.opensaml.security.trust.TrustEngine;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;

/**
 * Evaluate a token in sequence using a chain of subordinate trust engines. If the token may be established as trusted
 * by any of the subordinate engines, the token is considered trusted. Otherwise it is considered untrusted.
 * 
 * @param <TokenType> the token type this trust engine evaluates
 */
public class ChainingTrustEngine<TokenType> implements TrustEngine<TokenType> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ChainingTrustEngine.class);

    /** The chain of subordinate trust engines. */
    @Nonnull private List<TrustEngine<? super TokenType>> engines;

    /** 
     * Constructor.
     * 
     * @param chain the list of trust engines in the chain
     */
    public ChainingTrustEngine(@Nonnull @ParameterName(name="chain") final List<TrustEngine<? super TokenType>> chain) {
        engines = CollectionSupport.copyToList(Constraint.isNotNull(chain, "TrustEngine list cannot be null"));
    }

    /**
     * Get the list of configured trust engines which constitute the trust evaluation chain.
     * 
     * @return the modifiable list of trust engines in the chain
     */
    @Nonnull @Unmodifiable @NotLive public List<TrustEngine<? super TokenType>> getChain() {
        return engines;
    }

    /** {@inheritDoc} */
    @Override
    public boolean validate(@Nonnull final TokenType token, @Nullable final CriteriaSet trustBasisCriteria)
            throws SecurityException {
        for (final TrustEngine<? super TokenType> engine : engines) {
            if (engine.validate(token, trustBasisCriteria)) {
                log.debug("Token was trusted by chain member: {}", engine.getClass().getName());
                return true;
            }
        }
        return false;
    }

}