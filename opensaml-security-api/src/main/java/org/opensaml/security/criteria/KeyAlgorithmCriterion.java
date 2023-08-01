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

import javax.annotation.Nonnull;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.resolver.Criterion;


/**
 * An implementation of {@link Criterion} which specifies key algorithm criteria.
 */
public final class KeyAlgorithmCriterion implements Criterion {
    
    /** Key algorithm type of resolved credentials. */
    @Nonnull private String keyAlgorithm;
    
    /**
     * Constructor.
     *
     * @param algorithm key algorithm
     */
    public KeyAlgorithmCriterion(@Nonnull @NotEmpty final String algorithm) {
        keyAlgorithm = validateAlgorithm(algorithm);
    }
 
    /**
     * Get the key algorithm criteria.
     * 
     * @return returns the keyAlgorithm.
     */
    @Nonnull @NotEmpty public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    /**
     * Set the key algorithm criteria.
     * 
     * @param algorithm The keyAlgorithm to set.
     */
    public void setKeyAlgorithm(@Nonnull final String algorithm) {

        keyAlgorithm = validateAlgorithm(algorithm);
    }
    
    /** {@inheritDoc} */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("KeyAlgorithmCriterion [keyAlgorithm=");
        builder.append(keyAlgorithm);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return keyAlgorithm.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof KeyAlgorithmCriterion algcrit) {
            return keyAlgorithm.equals(algcrit.keyAlgorithm);
        }

        return false;
    }

    /**
     * Static method to validate and return null algorithm.
     * 
     * @param algorithm candidate
     * 
     * @return the input parameter if not null/empty after trimming
     */
    @Nonnull @NotEmpty private static String validateAlgorithm(@Nonnull final String algorithm) {
        final String trimmed = StringSupport.trimOrNull(algorithm);

        return Constraint.isNotNull(trimmed, "Key algorithm criterion cannot be null or empty");
    }
    
}