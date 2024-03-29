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
import javax.annotation.Nullable;

import org.opensaml.security.credential.UsageType;

import net.shibboleth.shared.resolver.Criterion;


/**
 * An implementation of {@link Criterion} which specifies criteria pertaining 
 * usage of the resolved credential. 
 */
public final class UsageCriterion implements Criterion {
   
    /** Key usage type of resolved credentials. */
    @Nonnull private UsageType credUsage;
    
    /**
    * Constructor.
     *
     * @param usage the usage for which a credential is intended
     */
    public UsageCriterion(@Nullable final UsageType usage) {
        if (usage != null) {
            credUsage = usage;
        } else {
            credUsage = UsageType.UNSPECIFIED;
        }
    }

    /**
     * Get the key usage criteria.
     * 
     * @return Returns the usage.
     */
    @Nonnull public UsageType getUsage() {
        return credUsage;
    }

    /**
     * Set the key usage criteria.
     * 
     * @param usage The usage to set.
     */
    public void setUsage(@Nullable final UsageType usage) {
        if (usage != null) {
            credUsage = usage;
        } else {
            credUsage = UsageType.UNSPECIFIED;
        }
    }
    
    /** {@inheritDoc} */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("UsageCriterion [credUsage=");
        builder.append(credUsage);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return credUsage.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof UsageCriterion usagecrit) {
            return credUsage.equals(usagecrit.credUsage);
        }

        return false;
    }

}