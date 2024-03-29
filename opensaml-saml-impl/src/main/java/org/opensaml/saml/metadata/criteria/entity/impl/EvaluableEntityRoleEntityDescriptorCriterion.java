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

package org.opensaml.saml.metadata.criteria.entity.impl;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.metadata.criteria.entity.EvaluableEntityDescriptorCriterion;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

import com.google.common.base.MoreObjects;

import net.shibboleth.shared.logic.Constraint;

/**
 * Implementation of {@link EvaluableEntityDescriptorCriterion} which evaluates that an entity descriptor
 * contains a certain role.
 */
public class EvaluableEntityRoleEntityDescriptorCriterion implements EvaluableEntityDescriptorCriterion {
    
    /** Entity role. */
    @Nonnull private QName role;
    
    /**
     * Constructor.
     *
     * @param criterion the entity role criterion
     */
    public EvaluableEntityRoleEntityDescriptorCriterion(@Nonnull final EntityRoleCriterion criterion) {
        Constraint.isNotNull(criterion, "EntityRoleCriterion was null");
        role = Constraint.isNotNull(criterion.getRole(), "Criterion role QName was null");
    }
    
    /**
     * Constructor.
     *
     * @param entityRole the entity
     */
    public EvaluableEntityRoleEntityDescriptorCriterion(@Nonnull final QName entityRole) {
        role = Constraint.isNotNull(entityRole, "Entity Role QName was null");
    }

    /** {@inheritDoc} */
    public boolean test(@Nullable final EntityDescriptor entityDescriptor) {
        if (entityDescriptor == null) {
            return false;
        }
        
        return ! entityDescriptor.getRoleDescriptors(role).isEmpty();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return role.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof EvaluableEntityRoleEntityDescriptorCriterion other) {
            return Objects.equals(role, other.role);
        }
        
        return false;
    }

    /** {@inheritDoc} */
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("role", role)
                .toString();
    }
    
}