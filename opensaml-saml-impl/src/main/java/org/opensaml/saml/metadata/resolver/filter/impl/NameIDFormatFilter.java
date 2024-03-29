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

package org.opensaml.saml.metadata.resolver.filter.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.metadata.resolver.filter.AbstractMetadataFilter;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilterContext;
import org.opensaml.saml.saml2.metadata.AttributeAuthorityDescriptor;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.PDPDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.slf4j.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * A filter that adds {@link NameIDFormat} content to entities in order to drive software
 * behavior based on them.
 * 
 * The entities to annotate are identified with a {@link Predicate}, and multiple formats can be
 * associated with each.
 */
public class NameIDFormatFilter extends AbstractMetadataFilter {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(NameIDFormatFilter.class);

    /** Whether to strip any existing Formats when adding new ones. */
    private boolean removeExistingFormats;
    
    /** Rules for adding formats. */
    @Nonnull private Multimap<Predicate<EntityDescriptor>,String> applyMap;

    /** Builder for {@link NameIDFormat}. */
    @Nonnull private final SAMLObjectBuilder<NameIDFormat> formatBuilder;

    /** Constructor. */
    public NameIDFormatFilter() {
        applyMap = ArrayListMultimap.create();
        formatBuilder = (SAMLObjectBuilder<NameIDFormat>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<NameIDFormat>ensureBuilder(
                        NameIDFormat.DEFAULT_ELEMENT_NAME);
    }
    
    /**
     * Set whether the filter should remove any existing formats from an entity to which it adds
     * new ones.
     * 
     * <p>Defaults to false (for compatibility).</p>
     * 
     * @param flag flag to set
     */
    public void setRemoveExistingFormats(final boolean flag) {
        checkSetterPreconditions();
        removeExistingFormats = flag;
    }
    
    /**
     * Set the mappings from {@link Predicate} to format collection to apply.
     * 
     * @param rules rules to apply
     */
    public void setRules(@Nonnull final Map<Predicate<EntityDescriptor>,Collection<String>> rules) {
        checkSetterPreconditions();
        Constraint.isNotNull(rules, "Rules map cannot be null");
        
        applyMap = ArrayListMultimap.create(rules.size(), 1);
        for (final Map.Entry<Predicate<EntityDescriptor>,Collection<String>> entry : rules.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                applyMap.putAll(entry.getKey(), StringSupport.normalizeStringCollection(entry.getValue()));
            }
        }
    }

    /** {@inheritDoc} */
    @Nullable public XMLObject filter(@Nullable final XMLObject metadata, @Nonnull final MetadataFilterContext context)
            throws FilterException {
        checkComponentActive();
        if (metadata == null) {
            return null;
        }

        if (metadata instanceof EntitiesDescriptor) {
            filterEntitiesDescriptor((EntitiesDescriptor) metadata);
        } else {
            filterEntityDescriptor((EntityDescriptor) metadata);
        }
        
        return metadata;
    }
    
    /**
     * Filters entity descriptor.
     * 
     * @param descriptor entity descriptor to filter
     */
    protected void filterEntityDescriptor(@Nonnull final EntityDescriptor descriptor) {
        for (final Map.Entry<Predicate<EntityDescriptor>,Collection<String>> entry : applyMap.asMap().entrySet()) {
            if (!entry.getValue().isEmpty() && entry.getKey().test(descriptor)) {
                for (final RoleDescriptor role : descriptor.getRoleDescriptors()) {
                    assert role != null;
                    filterRoleDescriptor(role, entry.getValue());
                }
            }
        }
    }
    
    /**
     * 
     * Filters role descriptor.
     * 
     * @param role role to modify
     * @param formats formats to attach
     */
    protected void filterRoleDescriptor(@Nonnull final RoleDescriptor role, @Nonnull final Collection<String> formats) {
        
        final Collection<NameIDFormat> roleFormats;
        
        if (role instanceof SPSSODescriptor sp) {
            roleFormats = sp.getNameIDFormats();
        } else if (role instanceof AttributeAuthorityDescriptor aa) {
            roleFormats = aa.getNameIDFormats();
        } else if (role instanceof PDPDescriptor pdp) {
            roleFormats = pdp.getNameIDFormats();
        } else {
            return;
        }
        
        final String entityID;
        if (role.getParent() instanceof EntityDescriptor entity) {
            entityID = entity.getEntityID();
        } else {
            entityID = null;
        }
        
        if (removeExistingFormats && !roleFormats.isEmpty()) {
            log.debug("Removing existing NameIDFormats from {} role in EntityDescriptor '{}'", role.getElementQName(),
                    entityID);
            roleFormats.clear();
        }
        
        final Set<String> existingFormats =
                roleFormats.stream().map(NameIDFormat::getURI).collect(Collectors.toUnmodifiableSet());
        
        for (final String format : formats) {
            if (existingFormats.contains(format)) {
                log.debug("Ignoring add of existing NameIDFormat '{}' on EntityDescriptor '{}'", format, entityID);
            } else {
                final NameIDFormat nif = formatBuilder.buildObject();
                nif.setURI(format);
                log.info("Adding NameIDFormat '{}' to EntityDescriptor '{}'", format, entityID);
                roleFormats.add(nif);
            }
        }
        
    }
    
    /**
     * Filters entities descriptor.
     * 
     * @param descriptor entities descriptor to filter
     */
    protected void filterEntitiesDescriptor(@Nonnull final EntitiesDescriptor descriptor) {
        
        // First we check any contained EntitiesDescriptors.
        for (final EntitiesDescriptor group : descriptor.getEntitiesDescriptors()) {
            assert group != null;
            filterEntitiesDescriptor(group);
        }
        
        // Next, check contained EntityDescriptors.
        for (final EntityDescriptor entity : descriptor.getEntityDescriptors()) {
            assert entity != null;
            filterEntityDescriptor(entity);
        }
    }

}