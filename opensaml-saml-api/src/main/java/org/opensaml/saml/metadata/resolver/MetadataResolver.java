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

package org.opensaml.saml.metadata.resolver;

import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.component.IdentifiedComponent;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.Resolver;

import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

/**
 * A resolver that is capable of resolving {@link EntityDescriptor} instances
 * which meet certain supplied criteria.
 * 
 * At a minimum, a {@link MetadataResolver} implementation MUST support the following criteria:
 * <ul>
 * <li>{@link org.opensaml.core.criterion.EntityIdCriterion}</li>
 * </ul>
 * 
 * Implementations SHOULD also support the following criteria when possible:
 * <ul>
 * <li>{@link org.opensaml.saml.criterion.EntityRoleCriterion}</li>
 * <li>{@link org.opensaml.saml.criterion.ProtocolCriterion}</li>
 * <li>{@link org.opensaml.saml.criterion.BindingCriterion}</li>
 * </ul>
 */
public interface MetadataResolver extends Resolver<EntityDescriptor, CriteriaSet>, IdentifiedComponent {
    
    /**
     * Gets the type of resolver for reporting or logging purposes.
     * 
     * @return resolver type
     * 
     * @since 5.0.0
     */
    @Nullable @NotEmpty String getType();
    
    /**
     * Gets whether the metadata returned by queries must be valid. 
     * 
     * <p>
     * At a minimum, metadata is valid only if the date expressed in the <code>validUntil</code> attribute 
     * of the element and all its ancestral elements has not passed. Specific implementations may add additional 
     * constraints.
     * </p>
     * 
     * @return whether the metadata returned by queries must be valid
     */
    boolean isRequireValidMetadata();

    /**
     * Sets whether the metadata returned by queries must be valid.
     * 
     * @param requireValidMetadata whether the metadata returned by queries must be valid
     */
    void setRequireValidMetadata(final boolean requireValidMetadata);

    /**
     * Gets the metadata filter applied to the metadata.
     * 
     * @return the metadata filter applied to the metadata
     */
    @Nullable MetadataFilter getMetadataFilter();

    /**
     * Sets the metadata filter applied to the metadata.
     * 
     * @param newFilter the metadata filter applied to the metadata
     */
    void setMetadataFilter(@Nullable final MetadataFilter newFilter);

}