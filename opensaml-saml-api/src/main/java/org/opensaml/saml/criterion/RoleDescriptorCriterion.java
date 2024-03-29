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

import javax.annotation.Nonnull;

import org.opensaml.saml.saml2.metadata.RoleDescriptor;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.Criterion;

/** {@link Criterion} representing an entity role. */
public final class RoleDescriptorCriterion implements Criterion {

    /** The entity role. */
    @Nonnull private final RoleDescriptor role;

    /**
     * Constructor.
     * 
     * @param samlRole the entity role
     */
    public RoleDescriptorCriterion(@Nonnull final RoleDescriptor samlRole) {
        role = Constraint.isNotNull(samlRole, "SAML role cannot be null");
    }

    /**
     * Gets the entity role.
     * 
     * @return the entity role
     */
    @Nonnull public RoleDescriptor getRole() {
        return role;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("RoleDescriptorCriterion [role=");
        builder.append(role);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return role.hashCode();
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

        if (obj instanceof RoleDescriptorCriterion other) {
            return role.equals(other.role);
        }

        return false;
    }

}