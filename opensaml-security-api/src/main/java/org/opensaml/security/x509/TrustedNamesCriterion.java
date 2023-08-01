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

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.resolver.Criterion;

/**
 * A criterion implementation for conveying a dynamically-generated set of trusted
 * names for PKIX validation purposes.  This criterion would typically be evaluated
 * by a {@link PKIXValidationInformationResolver} that supports trusted name resolution.
 */
public class TrustedNamesCriterion implements Criterion {
    
    /** The set of trusted names. */
    @Nonnull private Set<String> trustedNames;
    
    /**
     * Constructor.
     *
     * @param names the set of trusted names
     */
    public TrustedNamesCriterion(@Nullable final Set<String> names)  {
        trustedNames = processNames(names);
    }
    
    /**
     * Get the set of trusted names.
     * 
     * @return the set of trusted names
     */
    @Nonnull @NotLive @Unmodifiable public Set<String> getTrustedNames() {
        return trustedNames;
    }
    
    /**
     * Set the set of trusted names.
     * 
     * @param names the new trusted names
     */
    public void setTrustedNames(@Nullable final Set<String> names) {
        trustedNames = processNames(names);
    }
    
    /** {@inheritDoc} */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("TrustedNamesCriterion [names=");
        builder.append(trustedNames);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        int result = 17;  
        result = 37*result + trustedNames.hashCode();
        return result;
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof TrustedNamesCriterion) {
            final TrustedNamesCriterion other = (TrustedNamesCriterion) obj;
            return trustedNames.equals(other.trustedNames);
        }

        return false;
    }
    
    /**
     * Sanitize input names.
     * 
     * @param names input names
     * 
     * @return sanitized set
     */
    @Nonnull @NotLive @Unmodifiable private Set<String> processNames(@Nullable final Set<String> names) {
        if (names != null) {
            return CollectionSupport.copyToSet(StringSupport.normalizeStringCollection(names));
        } else {
            return CollectionSupport.emptySet();
        }
    }

}