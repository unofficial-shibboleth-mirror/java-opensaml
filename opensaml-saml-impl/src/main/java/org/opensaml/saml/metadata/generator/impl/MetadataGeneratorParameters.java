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
package org.opensaml.saml.metadata.generator.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.Namespace;
import org.opensaml.saml.ext.saml2mdattr.EntityAttributes;
import org.opensaml.saml.ext.saml2mdui.Logo;
import org.opensaml.saml.saml2.metadata.AttributeAuthorityDescriptor;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Inputs to metadata generation.
 * 
 * <p>TODO: This will eventually migrate into the API.</p>
 * 
 * @since 5.0.0
 */
public interface MetadataGeneratorParameters {

    /**
     * Get the unique ID.
     * 
     * @return the unique ID
     */
    @Nullable default String getEntityID() {
        return null;
    }
    
    /**
     * Get the {Scope} extension(s).
     * 
     * @return scope list
     */
    @Nonnull @Unmodifiable @NotLive default List<String> getScopes() {
        return CollectionSupport.emptyList();
    }

    /**
     * Whether to omit the namespace declarations on the root element.
     * 
     * @return true iff namespace declarations should be omitted
     */
    default boolean isOmitNamespaceDeclarations() {
        return false;
    }
    
    /**
     * Whether to include an {@link Extensions} element in output.
     * 
     * @return whether to include extensions element
     */
    default boolean isRequiresExtensions() {
        return !getScopes().isEmpty() || getDisplayName() != null || getDescription() != null || getLogo() != null
                || !getTagAssignments().isEmpty();
    }
    
    /**
     * Get a set of additional namespaces to declare on root element.
     * 
     * @return additional namespaces
     */
    @Nullable default Set<Namespace> getAdditionalNamespaces() {
        return null;
    }

    /**
     * Get the SP role to generate.
     * 
     * <p>Only the endpoints and any basic flags are extracted from the role.</p> 
     * 
     * @return SP role or null
     */
    @Nullable default SPSSODescriptor getSPSSODescriptor() {
        return null;
    }
    
    /**
     * Get the IdP role to generate.
     * 
     * <p>Only the endpoints and any basic flags are extracted from the role.</p>
     *  
     * @return IdP role or null
     */
    @Nullable default IDPSSODescriptor getIDPSSODescriptor() {
        return null;
    }

    /**
     * Get the AA role to generate.
     * 
     * <p>Only the endpoints and any basic flags are extracted from the role.</p>
     *  
     * @return AA role or null
     */
    @Nullable default AttributeAuthorityDescriptor getAttributeAuthorityDescriptor() {
        return null;
    }

    /**
     * Dual-use certificates.
     * 
     * <p>Keys will be applied to all roles.</p>
     * 
     * @return base64-encoded certificates
     */
    @Nonnull @Unmodifiable @NotLive default List<String> getCertificates() {
        return CollectionSupport.emptyList();
    }

    /**
     * Signing-only certificate path(s).
     * 
     * <p>Keys will be applied to all roles.</p>
     * 
     * @return base64-encoded certificates
     */
    @Nonnull @Unmodifiable @NotLive default List<String> getSigningCertificates() {
        return CollectionSupport.emptyList();
    }

    /**
     * Encryption-only certificate path(s).
     * 
     * <p>Keys will be applied to all roles.</p>
     * 
     * @return base64-encoded certificates
     */
    @Nonnull @Unmodifiable @NotLive default List<String> getEncryptionCertificates() {
        return CollectionSupport.emptyList();
    }
    
    /**
     * Get the language tag for language-specific content.
     *
     * @return language tag
     */
    @Nullable default String getLang() {
        return null;
    }
    
    /**
     * Get the display name.
     * 
     * @return display name
     */
    @Nullable default String getDisplayName() {
        return null;
    }

    /**
     * Get the description.
     * 
     * @return description
     */
    @Nullable default String getDescription() {
        return null;
    }

    /**
     * Get the logo.
     * 
     * @return logo
     */
    @Nullable default Logo getLogo() {
        return null;
    }
    
    /**
     * Get the organization name.
     *
     * @return organization name
     */
    @Nullable default String getOrganizationName() {
        return null;
    }

    /**
     * Get the organization URL.
     *
     * @return organization URL
     */
    @Nullable default String getOrganizationURL() {
        return null;
    }
    
    /**
     * Get the contacts.
     * 
     * @return list of contacts
     */
    @Nonnull @Unmodifiable @NotLive default List<ContactPerson> getContactPersons() {
        return CollectionSupport.emptyList();
    }
    
    /**
     * Get the {@link EntityAttributes} tag names and values in the form of a Java-based multi-map.
     * 
     * @return map of tag names to zero or more values
     */
    @Nonnull @Unmodifiable @NotLive default Map<String,Collection<String>> getTagAssignments() {
        return CollectionSupport.emptyMap();
    }
    
}