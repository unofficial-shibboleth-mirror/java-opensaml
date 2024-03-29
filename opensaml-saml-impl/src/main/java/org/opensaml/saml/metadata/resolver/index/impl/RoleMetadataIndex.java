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

package org.opensaml.saml.metadata.resolver.index.impl;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.CriteriaSet;

import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.metadata.resolver.index.MetadataIndex;
import org.opensaml.saml.metadata.resolver.index.MetadataIndexKey;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;

import com.google.common.base.MoreObjects;

/**
 * An implementation of {@link MetadataIndex} which indexes entities by their roles.
 */
public class RoleMetadataIndex implements MetadataIndex {

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public Set<MetadataIndexKey> generateKeys(
            @Nullable final CriteriaSet criteriaSet) {
        final EntityRoleCriterion roleCrit = criteriaSet != null ? criteriaSet.get(EntityRoleCriterion.class) : null;
        if (roleCrit != null) {
            final HashSet<MetadataIndexKey> result = new HashSet<>();
            result.add(new RoleMetadataIndexKey(roleCrit.getRole()));
            return result;
        }
        return null;
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public Set<MetadataIndexKey> generateKeys(
            @Nonnull final EntityDescriptor descriptor) {
        Constraint.isNotNull(descriptor, "EntityDescriptor was null");
        final HashSet<MetadataIndexKey> result = new HashSet<>();
        for (final RoleDescriptor role : descriptor.getRoleDescriptors()) {
            final QName type = role.getSchemaType();
            if (type != null) {
                result.add(new RoleMetadataIndexKey(type));
            } else {
                result.add(new RoleMetadataIndexKey(role.getElementQName()));
            }
        }
        return result;
    }
    
    /**
     * An implementation of {@link MetadataIndexKey} representing a single SAML metadata role.
     */
    protected static class RoleMetadataIndexKey implements MetadataIndexKey {
        /** The entity role. */
        @Nonnull private final QName role;

        /**
         * Constructor.
         * 
         * @param samlRole the entity role
         */
        public RoleMetadataIndexKey(@Nonnull final QName samlRole) {
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
            return MoreObjects.toStringHelper(this).add("role", role).toString();
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

            if (obj instanceof RoleMetadataIndexKey other) {
                return role.equals(other.role);
            }

            return false;
        }
    }

}