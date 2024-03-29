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

package org.opensaml.saml.criterion;

import java.util.Objects;

import com.google.common.base.MoreObjects;

import net.shibboleth.shared.resolver.Criterion;

/**
 * A criterion which allows to specify at runtime whether location paths being evaluated
 * may be evaluated on the basis of a "starts with" match.
 */
public class StartsWithLocationCriterion implements Criterion {
    
    /** The matchStartsWith criterion value. */
    private boolean matchStartsWith;
    
    /**
     * Constructor.
     */
    public StartsWithLocationCriterion() {
        matchStartsWith = true;
    }
    
    /**
     * Constructor.
     *
     * @param value the matchStartsWith flag value
     */
    public StartsWithLocationCriterion(final boolean value) {
        matchStartsWith = value;
    }

    /**
     * Get the startswith value.
     * 
     * @return true or false
     */
    public boolean isMatchStartsWith() {
        return matchStartsWith;
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return Boolean.valueOf(matchStartsWith).hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof StartsWithLocationCriterion other) {
            return Objects.equals(matchStartsWith, other.matchStartsWith);
        }
        
        return false;
    }

    /** {@inheritDoc} */
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .addValue(matchStartsWith).toString();
    }

}