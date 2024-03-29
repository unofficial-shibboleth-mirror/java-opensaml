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

package org.opensaml.security.criteria;

import java.security.PublicKey;

import javax.annotation.Nonnull;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.Criterion;

/**
 * An implementation of {@link Criterion} which specifies public key criteria.
 */
public final class PublicKeyCriterion implements Criterion {

    /** Specifier of public key associated with resolved credentials. */
    @Nonnull private PublicKey publicKey;
    
    /**
     * Constructor.
     *
     * @param pubKey public key
     */
    public PublicKeyCriterion(@Nonnull final PublicKey pubKey) {
        publicKey = Constraint.isNotNull(pubKey, "Public key criterion value cannot be null");
    }
    
    /**
     * Get the public key criteria.
     * 
     * @return Returns the publicKey.
     */
    @Nonnull public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Set the public key criteria. 
     * 
     * @param key The publicKey to set.
     */
    public void setPublicKey(@Nonnull final PublicKey key) {
        publicKey = Constraint.isNotNull(key, "Public key criterion value cannot be null");
    }
    
    /** {@inheritDoc} */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("PublicKeyCriterion [publicKey=");
        builder.append(publicKey);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return publicKey.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof PublicKeyCriterion keycrit) {
            return publicKey.equals(keycrit.publicKey);
        }

        return false;
    }

}