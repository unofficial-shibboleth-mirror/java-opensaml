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
import javax.xml.namespace.QName;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.Criterion;

/** {@link Criterion} representing an entity role. */
public final class EntityRoleCriterion implements Criterion {

    /** The entity role. */
    @Nonnull private final QName role;

    /**
     * Constructor.
     * 
     * @param samlRole the entity role
     */
    public EntityRoleCriterion(@Nonnull final QName samlRole) {
        role = Constraint.isNotNull(samlRole, "SAML role cannot be null");
    }

    /**
     * Gets the entity role.
     * 
     * @return the entity role
     */
    @Nonnull public QName getRole() {
        return role;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("EntityRoleCriterion [role=");
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

        if (obj instanceof EntityRoleCriterion other) {
            return role.equals(other.role);
        }

        return false;
    }
    
}