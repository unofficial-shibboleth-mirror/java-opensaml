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

package org.opensaml.saml.metadata.support;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.ext.saml2mdquery.AttributeQueryDescriptorType;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Metadata support class which selects an {@link AttributeConsumingService} based on input of a mandatory
 * {@link RoleDescriptor} and an optional index.
 * 
 * <p>
 * This implementation supports selecting an AttributeConsumingService from parent role descriptors of the following
 * types:
 * </p>
 * 
 * <ol>
 * <li>the standard SAML 2 metadata type {@link SPSSODescriptor}</li>
 * <li>the extension type {@link AttributeQueryDescriptorType}</li>
 * </ol>
 * 
 * <p>
 * Subclasses should override {@link #getCandidates()} if support for additional sources of attribute consuming services
 * is needed.
 * </p>
 * 
 * <p>
 * The selection algorithm is:
 * </p>
 * <ol>
 * <li>If an index is supplied, the service with that index is returned. If no such service exists in metadata: if
 * {@link #isOnBadIndexUseDefault()} is true, then the default service is returned as described below; otherwise null is
 * returned.</li>
 * <li>If an index is not supplied, then the default service is returned as follows: The service with an explicit
 * isDefault of true is returned. If no such service exists, then the first service without an explicit isDefault is
 * returned. If no service is yet selected, then the first service listed in metadata is returned.</li>
 * </ol>
 */
public class AttributeConsumingServiceSelector {

    /** Class logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(AttributeConsumingServiceSelector.class);

    /** The requested service index. */
    @Nullable private Integer index;

    /** The AttributeConsumingService's parent role descriptor. */
    @Nullable private RoleDescriptor roleDescriptor;

    /**
     * Flag which determines whether, in the case of an invalid index, to return the default AttributeConsumingService.
     */
    private boolean onBadIndexUseDefault;

    /**
     * Get the index of the desired service.
     * 
     * @return Returns the index.
     */
    @Nullable public Integer getIndex() {
        return index;
    }

    /**
     * Set the index of the desired service.
     * 
     * @param requestedIndex The index to set.
     */
    public void setIndex(@Nullable final Integer requestedIndex) {
        index = requestedIndex;
    }

    /**
     * Get the AttributeConsumingServie's parent RoleDescriptor.
     * 
     * @return Returns the spSSODescriptor.
     */
    @Nullable public RoleDescriptor getRoleDescriptor() {
        return roleDescriptor;
    }

    /**
     * Set the AttributeConsumingServie's parent RoleDescriptor.
     * 
     * @param descriptor The roleDescriptor to set.
     */
    public void setRoleDescriptor(@Nullable final RoleDescriptor descriptor) {
        roleDescriptor = descriptor;
    }

    /**
     * Set the flag which determines whether, in the case of an invalid index, to return the default
     * AttributeConsumingService. Defaults to false.
     * 
     * @param flag The onBadIndexUseDefault to set.
     */
    public void setOnBadIndexUseDefault(final boolean flag) {
        onBadIndexUseDefault = flag;
    }

    /**
     * Get the flag which determines whether, in the case of an invalid index, to return the default
     * AttributeConsumingService. Defaults to false.
     * 
     * @return Returns the onBadIndexUseDefault.
     */
    public boolean isOnBadIndexUseDefault() {
        return onBadIndexUseDefault;
    }

    /**
     * Select the AttributeConsumingService.
     * 
     * @return the selected AttributeConsumingService, or null
     */
    @Nullable public AttributeConsumingService selectService() {
        final List<AttributeConsumingService> candidates = getCandidates();

        if (candidates == null || candidates.isEmpty()) {
            log.debug("AttributeConsumingService candidate list was empty, can not select service");
            return null;
        }

        log.debug("AttributeConsumingService index was specified: {}", index != null);

        AttributeConsumingService acs = null;
        if (index != null) {
            acs = selectByIndex(candidates);
            if (acs == null && isOnBadIndexUseDefault()) {
                acs = selectDefault(candidates);
            }
        } else {
            return selectDefault(candidates);
        }

        return acs;
    }

    /**
     * Get the list of candidate attribute consuming services.
     * 
     * <p>
     * This implementation supports selecting an AttributeConsumingService from parent role descriptors of the following
     * types:
     * </p>
     * 
     * <ol>
     * <li>the standard SAML 2 metadata type {@link SPSSODescriptor}</li>
     * <li>the extension type {@link AttributeQueryDescriptorType}</li>
     * </ol>
     * 
     * <p>
     * Subclasses should override if support for additional sources of attribute consuming services is needed.
     * </p>
     * 
     * @return the list of candidate AttributeConsumingServices, or null if none could be resolved
     */
    @Nullable @Unmodifiable @NotLive protected List<AttributeConsumingService> getCandidates() {
        if (roleDescriptor == null) {
            log.debug("RoleDescriptor was not supplied, unable to select AttributeConsumingService");
            return null;
        }

        if (roleDescriptor instanceof SPSSODescriptor sprole) {
            log.debug("Resolving AttributeConsumingService candidates from SPSSODescriptor");
            return sprole.getAttributeConsumingServices();
        } else if (roleDescriptor instanceof AttributeQueryDescriptorType queryrole) {
            log.debug("Resolving AttributeConsumingService candidates from AttributeQueryDescriptorType");
            return queryrole.getAttributeConsumingServices();
        } else {
            assert roleDescriptor != null;
            log.debug("Unable to resolve service candidates, role descriptor was of an unsupported type: {}",
                    roleDescriptor.getClass().getName());
            return null;
        }
    }

    /**
     * Select the service based on the index value.
     * 
     * @param candidates the list of candiate services
     * @return the selected candidate or null
     */
    @Nullable private AttributeConsumingService selectByIndex(final List<AttributeConsumingService> candidates) {
        log.debug("Selecting AttributeConsumingService by index");
        for (final AttributeConsumingService attribCS : candidates) {
            // Check for null b/c don't ever want to fail with an NPE due to autoboxing.
            // Note: metadata index property is an int, not an Integer.
            if (index != null) {
                if (index.equals(attribCS.getIndex())) {
                    log.debug("Selected AttributeConsumingService with index: {}", index);
                    return attribCS;
                }
            }
        }
        log.debug("A service index of '{}' was specified, but was not found in metadata", index);
        return null;
    }

    /**
     * Select the default service.
     * 
     * @param candidates the list of candiate services
     * @return the selected candidate or null
     */
    @Nonnull private AttributeConsumingService selectDefault(final List<AttributeConsumingService> candidates) {
        log.debug("Selecting default AttributeConsumingService");
        AttributeConsumingService firstNoDefault = null;
        for (final AttributeConsumingService attribCS : candidates) {
            final Boolean isDefault = attribCS.isDefault();
            if (isDefault != null && isDefault) {
                log.debug("Selected AttributeConsumingService with explicit isDefault of true");
                return attribCS;
            }

            // This records the first element whose isDefault is not explicitly false
            if (firstNoDefault == null && attribCS.isDefaultXSBoolean() == null) {
                firstNoDefault = attribCS;
            }
        }

        if (firstNoDefault != null) {
            log.debug("Selected first AttributeConsumingService with no explicit isDefault");
            return firstNoDefault;
        }
        log.debug("Selected first AttributeConsumingService with explicit isDefault of false");
        return candidates.get(0);
    }
    
}