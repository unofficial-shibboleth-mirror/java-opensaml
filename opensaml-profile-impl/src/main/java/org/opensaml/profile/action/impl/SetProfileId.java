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

package org.opensaml.profile.action.impl;

import javax.annotation.Nonnull;

import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.context.ProfileRequestContext;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;


/**
 * A profile action that sets the ID of the profile in use.
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 * @post <pre>ProfileRequestContext.profileId != null</pre>
 */
public class SetProfileId extends AbstractProfileAction {

    /** ID of the profile in use. */
    @Nonnull @NotEmpty private final String profileId;

    /**
     * Constructor.
     * 
     * @param id ID of the profile in use
     */
    public SetProfileId(@Nonnull @NotEmpty final String id) {
        profileId = Constraint.isNotNull(StringSupport.trimOrNull(id), "Profile ID cannot be null or empty");
    }

    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        profileRequestContext.setProfileId(profileId);
    }
}