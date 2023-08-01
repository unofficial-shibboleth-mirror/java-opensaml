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

/**
 * 
 */

package org.opensaml.saml.saml2.metadata.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.OrganizationDisplayName;
import org.opensaml.saml.saml2.metadata.OrganizationName;
import org.opensaml.saml.saml2.metadata.OrganizationURL;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link Organization}.
 */
public class OrganizationImpl extends AbstractXMLObject implements Organization {

    /** element extensions. */
    @Nullable private Extensions extensions;

    /** OrganizationName children. */
    @Nonnull private final XMLObjectChildrenList<OrganizationName> names;

    /** OrganizationDisplayName children. */
    @Nonnull private final XMLObjectChildrenList<OrganizationDisplayName> displayNames;

    /** OrganizationURL children. */
    @Nonnull private final XMLObjectChildrenList<OrganizationURL> urls;
    
    /** "anyAttribute" attributes. */
    @Nonnull private final AttributeMap unknownAttributes;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected OrganizationImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        names = new XMLObjectChildrenList<>(this);
        displayNames = new XMLObjectChildrenList<>(this);
        urls = new XMLObjectChildrenList<>(this);
        unknownAttributes = new AttributeMap(this);
    }

    /** {@inheritDoc} */
    @Nullable public Extensions getExtensions() {
        return extensions;
    }

    /** {@inheritDoc} */
    public void setExtensions(@Nullable final Extensions newExtensions) {
        this.extensions = prepareForAssignment(this.extensions, newExtensions);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<OrganizationName> getOrganizationNames() {
        return names;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<OrganizationDisplayName> getDisplayNames() {
        return displayNames;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<OrganizationURL> getURLs() {
        return urls;
    }
    
    /** {@inheritDoc} */    @Override
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        if (extensions != null) {
            children.add(extensions);
        }
        
        children.addAll(names);
        children.addAll(displayNames);
        children.addAll(urls);

        return CollectionSupport.copyToList(children);
    }
    
}