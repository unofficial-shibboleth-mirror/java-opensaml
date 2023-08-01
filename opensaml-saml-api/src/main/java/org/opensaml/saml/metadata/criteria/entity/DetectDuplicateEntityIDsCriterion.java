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

package org.opensaml.saml.metadata.criteria.entity;

import java.util.Objects;

import javax.annotation.Nonnull;

import org.opensaml.saml.metadata.resolver.DetectDuplicateEntityIDs;

import com.google.common.base.MoreObjects;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.Criterion;

/**
 * Criterion which holds an instance of {@link DetectDuplicateEntityIDs}.
 */
public class DetectDuplicateEntityIDsCriterion implements Criterion {
    
    /** The configured duplicate detection value. **/
    @Nonnull private DetectDuplicateEntityIDs value;

    /**
     * Constructor.
     *
     * @param detect the value used for duplicate detection
     */
    public DetectDuplicateEntityIDsCriterion(@Nonnull final DetectDuplicateEntityIDs detect) {
        value = Constraint.isNotNull(detect, "DetectDuplicateEntityIDs was null");
    }
    
    /**
     * Get the configured value.
     * 
     * @return the criterion value
     */
    @Nonnull public DetectDuplicateEntityIDs getValue() {
        return value;
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return value.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof DetectDuplicateEntityIDsCriterion other) {
            return Objects.equals(this.value, other.value);
        }
        
        return false;
    }

    /** {@inheritDoc} */
    public String toString() {
        return MoreObjects.toStringHelper(this).add("value", value).toString();
    }

}