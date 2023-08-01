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

package org.opensaml.saml.metadata.criteria.role.impl;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.metadata.criteria.role.EvaluableRoleDescriptorCriterion;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;

import com.google.common.base.MoreObjects;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Implementation of {@link EvaluableRoleDescriptorCriterion} which evaluates that a role descriptor
 * supports a certain protocol.
 */
public class EvaluableProtocolRoleDescriptorCriterion implements EvaluableRoleDescriptorCriterion {
    
    /** The SAML URI protocol being evaluated. */
    @Nonnull @NotEmpty private String protocol;
    
    /**
     * Constructor.
     *
     * @param criterion the protocol criterion
     */
    public EvaluableProtocolRoleDescriptorCriterion(@Nonnull final ProtocolCriterion criterion) {
        Constraint.isNotNull(criterion, "ProtocolCriterion was null");
        protocol = Constraint.isNotNull(criterion.getProtocol(), "Criterion protocol was null");
    }
    
    /**
     * Constructor.
     *
     * @param roleProtocol the protocol
     */
    public EvaluableProtocolRoleDescriptorCriterion(@Nonnull final String roleProtocol) {
        protocol = Constraint.isNotNull(StringSupport.trimOrNull(roleProtocol), 
                "Entity Role protocol was null or empty");
    }
    
    /** {@inheritDoc} */
    public boolean test(@Nullable final RoleDescriptor input) {
        if (input == null) {
            return false;
        }
        
        return input.isSupportedProtocol(protocol);
    }
    
    /** {@inheritDoc} */
    public int hashCode() {
        return protocol.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj instanceof EvaluableProtocolRoleDescriptorCriterion other) {
            return Objects.equals(protocol, other.protocol);
        }
        
        return false;
    }

    /** {@inheritDoc} */
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("protocol", protocol)
                .toString();
    }
    
}