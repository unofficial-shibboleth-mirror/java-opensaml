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

package org.opensaml.saml.saml2.metadata.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.metadata.AssertionIDRequestService;
import org.opensaml.saml.saml2.metadata.AttributeAuthorityDescriptor;
import org.opensaml.saml.saml2.metadata.AttributeProfile;
import org.opensaml.saml.saml2.metadata.AttributeService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.NameIDFormat;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * A concrete implementation of {@link AttributeAuthorityDescriptor}.
 */
public class AttributeAuthorityDescriptorImpl extends RoleDescriptorImpl implements AttributeAuthorityDescriptor {

    /** Attribte query endpoints. */
    @Nonnull private final XMLObjectChildrenList<AttributeService> attributeServices;

    /** Assertion request endpoints. */
    @Nonnull private final XMLObjectChildrenList<AssertionIDRequestService> assertionIDRequestServices;

    /** Supported NameID formats. */
    @Nonnull private final XMLObjectChildrenList<NameIDFormat> nameFormats;

    /** Supported attribute profiles. */
    @Nonnull private final XMLObjectChildrenList<AttributeProfile> attributeProfiles;

    /** Supported attribute. */
    @Nonnull private final XMLObjectChildrenList<Attribute> attributes;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AttributeAuthorityDescriptorImpl(@Nullable final String namespaceURI,
            @Nonnull final String elementLocalName, @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        attributeServices = new XMLObjectChildrenList<>(this);
        assertionIDRequestServices = new XMLObjectChildrenList<>(this);
        attributeProfiles = new XMLObjectChildrenList<>(this);
        nameFormats = new XMLObjectChildrenList<>(this);
        attributes = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AttributeService> getAttributeServices() {
        return attributeServices;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AssertionIDRequestService> getAssertionIDRequestServices() {
        return assertionIDRequestServices;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<NameIDFormat> getNameIDFormats() {
        return nameFormats;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AttributeProfile> getAttributeProfiles() {
        return attributeProfiles;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<Attribute> getAttributes() {
        return attributes;
    }
    
    /** {@inheritDoc} */
    @Nonnull @NotLive @Unmodifiable public List<Endpoint> getEndpoints() {
        final List<Endpoint> endpoints = new ArrayList<>();
        endpoints.addAll(attributeServices);
        endpoints.addAll(assertionIDRequestServices);
        return CollectionSupport.copyToList(endpoints);
    }
    
    /** {@inheritDoc} */
    @Nonnull @NotLive @Unmodifiable public List<Endpoint> getEndpoints(@Nonnull final QName type) {
        if (type.equals(AttributeService.DEFAULT_ELEMENT_NAME)) {
            return CollectionSupport.copyToList(attributeServices);
        } else if (type.equals(AssertionIDRequestService.DEFAULT_ELEMENT_NAME)) {
            return CollectionSupport.copyToList(assertionIDRequestServices);
        }
        
        return CollectionSupport.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        final List<XMLObject> parentChildren = super.getOrderedChildren();
        if (parentChildren != null) {
            children.addAll(parentChildren);
        }
        
        children.addAll(attributeServices);
        children.addAll(assertionIDRequestServices);
        children.addAll(nameFormats);
        children.addAll(attributeProfiles);
        children.addAll(attributes);

        return CollectionSupport.copyToList(children);
    }
    
}