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

package org.opensaml.profile.criterion;

import javax.annotation.Nonnull;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.resolver.Criterion;

/** {@link Criterion} representing a profile ID. */
public final class ProfileIDCriterion implements Criterion {

    /** The profile ID. */
    @Nonnull @NotEmpty private final String id;

    /**
     * Constructor.
     * 
     * @param profileId the profile ID, can not be null or empty
     */
    public ProfileIDCriterion(@Nonnull @NotEmpty final String profileId) {
        id = Constraint.isNotNull(StringSupport.trimOrNull(profileId), "Profile ID cannot be null or empty");
    }

    /**
     * Gets the profile ID.
     * 
     * @return the profile ID, never null or empty
     */
    @Nonnull @NotEmpty public String getProfileId() {
        return id;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ProfileIdCriterion [id=");
        builder.append(id);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof ProfileIDCriterion) {
            return id.equals(((ProfileIDCriterion) obj).id);
        }

        return false;
    }
}