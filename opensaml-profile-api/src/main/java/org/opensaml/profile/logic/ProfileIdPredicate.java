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

package org.opensaml.profile.logic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;

import java.util.function.Predicate;

import org.opensaml.profile.context.ProfileRequestContext;

/**
 * A predicate implementation that tests whether a profile request's profile ID matches an expected value.
 */
public class ProfileIdPredicate implements Predicate<ProfileRequestContext> {

    /** Profile ID to test for. */
    @Nonnull @NotEmpty private final String profileId;
    
    /**
     * Constructor.
     *
     * @param id profile ID to test for
     */
    public ProfileIdPredicate(@Nonnull @NotEmpty @ParameterName(name = "id") final String id) {
        profileId = Constraint.isNotNull(StringSupport.trimOrNull(id), "Profile ID cannot be null or empty");
    }
    
    /** {@inheritDoc} */
    public boolean test(@Nullable final ProfileRequestContext input) {
        return input != null && profileId.equals(input.getProfileId());
    }
    
}