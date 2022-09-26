/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.ext.saml2mdattr.EntityAttributes;
import org.opensaml.saml.metadata.resolver.filter.FilterException;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilterContext;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.component.AbstractInitializableComponent;
import net.shibboleth.shared.logic.Constraint;

/**
 * A filter that adds {@link EntityAttributes} extension content to entities in order to drive software
 * behavior based on them.
 * 
 * <p>The entities to annotate are identified with a {@link Predicate}, and multiple attributes can be
 * associated with each.</p>
 * 
 * <p>As of 3.4.0, another predicate can be set to validate pre-existing extension content to better
 * protect use cases of this component.</p>
 */
public class EntityAttributesFilter extends AbstractInitializableComponent implements MetadataFilter {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EntityAttributesFilter.class);

    /** Rules for adding attributes. */
    @Nonnull @NonnullElements private Multimap<Predicate<EntityDescriptor>,Attribute> applyMap;

    /** A condition to apply to pre-existing tags to determine their legitimacy. */
    @Nullable private Predicate<Attribute> attributeFilter;
    
    /** Builder for {@link Extensions}. */
    @Nonnull private final SAMLObjectBuilder<Extensions> extBuilder;

    /** Builder for {@link EntityAttributes}. */
    @Nonnull private final SAMLObjectBuilder<EntityAttributes> entityAttributesBuilder;

    /** Constructor. */
    public EntityAttributesFilter() {
        extBuilder = (SAMLObjectBuilder<Extensions>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<Extensions>getBuilderOrThrow(
                        Extensions.DEFAULT_ELEMENT_NAME);
        entityAttributesBuilder = (SAMLObjectBuilder<EntityAttributes>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<EntityAttributes>getBuilderOrThrow(
                        EntityAttributes.DEFAULT_ELEMENT_NAME);
        applyMap = ArrayListMultimap.create();
    }
    
    /**
     * Set the mappings from {@link Predicate} to {@link Attribute} collection to apply.
     * 
     * @param rules rules to apply
     */
    public void setRules(@Nonnull @NonnullElements final Map<Predicate<EntityDescriptor>,Collection<Attribute>> rules) {
        checkSetterPreconditions();
        Constraint.isNotNull(rules, "Rules map cannot be null");
        
        applyMap = ArrayListMultimap.create(rules.size(), 1);
        for (final Map.Entry<Predicate<EntityDescriptor>,Collection<Attribute>> entry : rules.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                applyMap.putAll(entry.getKey(), List.copyOf(entry.getValue()));
            }
        }
    }

    /**
     * Set a condition to apply to any pre-existing extension attributes, such that failure
     * causes their removal.
     * 
     * <p>If not set, then anything is allowed.</p>
     * 
     * @param condition condition to apply
     * 
     * @since 3.4.0
     */
    public void setAttributeFilter(@Nullable final Predicate<Attribute> condition) {
        checkSetterPreconditions();
        attributeFilter = condition;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable public XMLObject filter(@Nullable final XMLObject metadata, @Nonnull final MetadataFilterContext context)
            throws FilterException {
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
        if (attributeFilter != null) {
            applyFilter(descriptor);
        }
        
        for (final Map.Entry<Predicate<EntityDescriptor>,Collection<Attribute>> entry : applyMap.asMap().entrySet()) {
            if (!entry.getValue().isEmpty() && entry.getKey().test(descriptor)) {
                
                // Put extension objects in place.
                Extensions extensions = descriptor.getExtensions();
                if (extensions == null) {
                    extensions = extBuilder.buildObject();
                    descriptor.setExtensions(extensions);
                }
                final Collection<XMLObject> entityAttributesCollection =
                        extensions.getUnknownXMLObjects(EntityAttributes.DEFAULT_ELEMENT_NAME);
                if (entityAttributesCollection.isEmpty()) {
                    entityAttributesCollection.add(entityAttributesBuilder.buildObject());
                }
                
                final EntityAttributes entityAttributes =
                        (EntityAttributes) entityAttributesCollection.iterator().next();
                entry.getValue().forEach(a -> addEntityAttribute(descriptor, entityAttributes, a));
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
            filterEntitiesDescriptor(group);
        }
        
        // Next, check contained EntityDescriptors.
        for (final EntityDescriptor entity : descriptor.getEntityDescriptors()) {
            filterEntityDescriptor(entity);
        }
    }
    
    /**
     * Get or create {@link Attribute} based on the input/template object.
     * 
     * @param descriptor parent entity
     * @param container extension container
     * @param input input object
     */
    @Nonnull private void addEntityAttribute(@Nonnull final EntityDescriptor descriptor,
            @Nonnull final EntityAttributes container, @Nonnull final Attribute input) {
        
        Attribute toMutate = null;
        
        for (final Attribute attribute : container.getAttributes()) {
            if (Objects.equals(input.getName(), attribute.getName())) {

                String format1 = input.getNameFormat();
                String format2 = attribute.getNameFormat();
                
                if (Attribute.UNSPECIFIED.equals(format1)) {
                    format1 = null;
                }
                if (Attribute.UNSPECIFIED.equals(format2)) {
                    format2 = null;
                }
                if (Objects.equals(format1, format2)) {
                    toMutate = attribute;
                    break;
                }
            }
        }
        
        if (toMutate != null) {
            for (final XMLObject newValue : input.getAttributeValues()) {
                try {
                    log.info("Adding value to existing EntityAttribute ({}) on EntityDescriptor ({})", input.getName(),
                            descriptor.getEntityID());
                    toMutate.getAttributeValues().add(XMLObjectSupport.cloneXMLObject(newValue));
                } catch (final MarshallingException | UnmarshallingException e) {
                    log.error("Error cloning AttributeValue", e);
                }
            }
        } else {
            try {
                log.info("Adding new EntityAttribute ({}) to EntityDescriptor ({})", input.getName(),
                        descriptor.getEntityID());
                container.getAttributes().add(XMLObjectSupport.cloneXMLObject(input));
            } catch (final MarshallingException | UnmarshallingException e) {
                log.error("Error cloning Attribute", e);
            }
        }
    }

    /**
     * Apply include policy to metadata on input.
     * 
     * @param descriptor input to evaluate
     */
    @Nullable private void applyFilter(@Nonnull final EntityDescriptor descriptor) {
        final Extensions ext = descriptor.getExtensions();
        if (ext != null) {
            final Collection<XMLObject> entityAttributesCollection =
                    ext.getUnknownXMLObjects(EntityAttributes.DEFAULT_ELEMENT_NAME);
            if (!entityAttributesCollection.isEmpty()) {
                final EntityAttributes entityAttributes =
                        (EntityAttributes) entityAttributesCollection.iterator().next();
                final List<SAMLObject> attributes = entityAttributes.getEntityAttributesChildren();
                final Iterator<SAMLObject> iter = attributes.iterator();
                while (iter.hasNext()) {
                    final SAMLObject attribute = iter.next();
                    if (attribute instanceof Attribute) {
                        if (!attributeFilter.test((Attribute) attribute)) {
                            log.warn("Filtering pre-existing attribute '{}' from entity '{}'",
                                    ((Attribute) attribute).getName(), descriptor.getEntityID());
                            iter.remove();
                        }
                    }
                }
            }
        }
    }
    
}