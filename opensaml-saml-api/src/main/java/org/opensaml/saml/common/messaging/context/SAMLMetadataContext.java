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

package org.opensaml.saml.common.messaging.context;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;

/**
 * Subcontext that carries information SAML metadata about an associated entity. This context usually appears as a
 * subcontext of the {@link org.opensaml.messaging.context.MessageContext} that carries the actual SAML message, in such
 * cases the metadata carried herein applies to the issuer of that message.
 */
public final class SAMLMetadataContext extends BaseContext {

    /** The descriptor of the SAML entity. */
    @Nullable private EntityDescriptor entityDescriptor;

    /** The role descriptor of the SAML entity. */
    @Nullable private transient RoleDescriptor roleDescriptor;

    /**
     * Gets the descriptor of the SAML entity.
     * 
     * @return descriptor of the SAML entity, may be null
     */
    @Nullable public EntityDescriptor getEntityDescriptor() {
        return entityDescriptor;
    }

    /**
     * Sets the descriptor of the SAML entity.
     * 
     * @param descriptor of the SAML entity
     */
    public void setEntityDescriptor(@Nullable final EntityDescriptor descriptor) {
        entityDescriptor = descriptor;
    }

    /**
     * Gets the role descriptor of the SAML entity.
     * 
     * @return role descriptor of the SAML entity
     */
    @Nullable public RoleDescriptor getRoleDescriptor() {
        return roleDescriptor;
    }

    /**
     * Sets the role descriptor of the SAML entity.
     * 
     * @param descriptor role descriptor of the SAML entity
     */
    public void setRoleDescriptor(@Nullable final RoleDescriptor descriptor) {
        roleDescriptor = descriptor;
    }
    
}