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
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.metadata.support.SAML2MetadataSupport;
import org.opensaml.saml.saml2.metadata.ArtifactResolutionService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.ManageNameIDService;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.SSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link SSODescriptor}.
 */
public abstract class SSODescriptorImpl extends RoleDescriptorImpl implements SSODescriptor {

    /** Supported artifact resolutions services. */
    @Nonnull private final XMLObjectChildrenList<ArtifactResolutionService> artifactResolutionServices;

    /** Logout services for this SSO entity. */
    @Nonnull private final XMLObjectChildrenList<SingleLogoutService> singleLogoutServices;

    /** Manage NameID services for this entity. */
    @Nonnull private final XMLObjectChildrenList<ManageNameIDService> manageNameIDServices;

    /** NameID formats supported by this entity. */
    @Nonnull private final XMLObjectChildrenList<NameIDFormat> nameIDFormats;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected SSODescriptorImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        artifactResolutionServices = new XMLObjectChildrenList<>(this);
        singleLogoutServices = new XMLObjectChildrenList<>(this);
        manageNameIDServices = new XMLObjectChildrenList<>(this);
        nameIDFormats = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<ArtifactResolutionService> getArtifactResolutionServices() {
        return artifactResolutionServices;
    }
    
    /** {@inheritDoc} */
    @Nullable public ArtifactResolutionService getDefaultArtifactResolutionService() {
        return SAML2MetadataSupport.getDefaultIndexedEndpoint(artifactResolutionServices);
    }
    
    /** {@inheritDoc} */
    @Nonnull @Live public List<SingleLogoutService> getSingleLogoutServices() {
        return singleLogoutServices;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<ManageNameIDService> getManageNameIDServices() {
        return manageNameIDServices;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<NameIDFormat> getNameIDFormats() {
        return nameIDFormats;
    }
    
    /** {@inheritDoc} */
    @Nonnull @NotLive @Unmodifiable public List<Endpoint> getEndpoints() {
        final List<Endpoint> endpoints = new ArrayList<>();
        endpoints.addAll(artifactResolutionServices);
        endpoints.addAll(singleLogoutServices);
        endpoints.addAll(manageNameIDServices);
        return CollectionSupport.copyToList(endpoints);
    }
    
    /** {@inheritDoc} */
    @Nonnull @NotLive @Unmodifiable public List<Endpoint> getEndpoints(@Nonnull final QName type) {
        if(type.equals(ArtifactResolutionService.DEFAULT_ELEMENT_NAME)){
            return CollectionSupport.copyToList(artifactResolutionServices);
        }else if(type.equals(SingleLogoutService.DEFAULT_ELEMENT_NAME)){
            return CollectionSupport.copyToList(singleLogoutServices);
        }else if(type.equals(ManageNameIDService.DEFAULT_ELEMENT_NAME)){
            return CollectionSupport.copyToList(manageNameIDServices);
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
        children.addAll(artifactResolutionServices);
        children.addAll(singleLogoutServices);
        children.addAll(manageNameIDServices);
        children.addAll(nameIDFormats);
        
        return CollectionSupport.copyToList(children);
    }
    
}