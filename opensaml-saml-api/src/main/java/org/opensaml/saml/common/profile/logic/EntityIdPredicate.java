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

package org.opensaml.saml.common.profile.logic;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.saml2.metadata.EntityDescriptor;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.primitive.StringSupport;
import java.util.function.Predicate;

/**
 * Predicate that matches an {@link EntityDescriptor} against a set of entityIDs.
 */
public class EntityIdPredicate implements Predicate<EntityDescriptor> {
    
    /** Set of entityIDs to check for. */
    @Nonnull private final Set<String> entityIds;
    
    /**
     * Constructor.
     * 
     * @param ids the entityIDs to check for
     */
    public EntityIdPredicate(@Nullable @ParameterName(name="ids") final Collection<String> ids) {
        entityIds = CollectionSupport.copyToSet(StringSupport.normalizeStringCollection(ids));
    }
    
    /**
     * Get the entityID criteria.
     * 
     * @return  the entityID criteria
     */
    @Nonnull @Unmodifiable @NotLive public Set<String> getEntityIds() {
        return entityIds;
    }
    
    /** {@inheritDoc} */
    public boolean test(@Nullable final EntityDescriptor input) {
        
        if (input == null || input.getEntityID() == null) {
            return false;
        }
        
        return entityIds.contains(input.getEntityID());
    }

}