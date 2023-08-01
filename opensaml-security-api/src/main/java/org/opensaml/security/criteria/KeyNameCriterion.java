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
 * An implementation of {@link Criterion} which specifies key name criteria.
 */
public final class KeyNameCriterion implements Criterion {

    /** Key name of resolved credentials.  */
    @Nonnull @NotEmpty private String keyName;
    
    /**
     * Constructor.
     *
     * @param name key name
     */
    public KeyNameCriterion(@Nonnull @NotEmpty final String name) {
        keyName = validateKeyName(name);
    }

    /**
     * Get the key name criteria.
     * 
     * @return Returns the keyName.
     */
    @Nonnull @NotEmpty public String getKeyName() {
        return keyName;
    }

    /**
     * Set the key name criteria.
     * 
     * @param name The keyName to set.
     */
    public void setKeyName(@Nonnull @NotEmpty final String name) {
        keyName = validateKeyName(name);
    }
    
    /** {@inheritDoc} */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("KeyNameCriterion [keyName=");
        builder.append(keyName);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return keyName.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof KeyNameCriterion namecrit) {
            return keyName.equals(namecrit.keyName);
        }

        return false;
    }

    /**
     * Validate key name criterion.
     * 
     * @param name name to check
     * 
     * @return the input if non-null/empty
     */
    @Nonnull @NotEmpty public String validateKeyName(@Nonnull @NotEmpty final String name) {
        final String trimmed = StringSupport.trimOrNull(name);

        return Constraint.isNotNull(trimmed, "Key name criterion value cannot be null or empty");
    }
    
}