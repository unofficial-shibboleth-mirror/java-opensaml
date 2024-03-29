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

package org.opensaml.saml.metadata.resolver.impl;

import java.util.function.Function;

import javax.annotation.Nullable;

import org.opensaml.core.criterion.EntityIdCriterion;

import net.shibboleth.shared.resolver.CriteriaSet;

/**
 * Function which just reflects back the entityID from the passed {@link EntityIdCriterion}.
 */
public class IdentityEntityIDGenerator implements Function<CriteriaSet, String> {
    
    /** {@inheritDoc} */
    @Nullable public String apply(@Nullable final CriteriaSet input) {
        if (input == null) {
            return null;
        }
        
        final EntityIdCriterion entityIDCrit = input.get(EntityIdCriterion.class);
        if (entityIDCrit == null) {
            return null;
        }
        
        return entityIDCrit.getEntityId();
    }

}
