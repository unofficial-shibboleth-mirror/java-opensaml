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

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.Namespace;
import org.opensaml.saml.saml2.metadata.AttributeAuthorityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

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
    @Nullable String getEntityID();

    /**
     * Whether to omit the namespace declarations on the root element.
     * 
     * @return true iff namespace declarations should be omitted
     */
    boolean isOmitNamespaceDeclarations();
    
    /**
     * Get a set of additional namespaces to declare on root element.
     * 
     * @return additional namespaces
     */
    @Nullable Set<Namespace> getAdditionalNamespaces();

    /**
     * Get the SP role to generate.
     * 
     * <p>Only the endpoints and any basic flags are extracted from the role.</p> 
     * 
     * @return SP role or null
     */
    @Nullable SPSSODescriptor getSPSSODescriptor();
    
    /**
     * Get the IdP role to generate.
     * 
     * <p>Only the endpoints and any basic flags are extracted from the role.</p>
     *  
     * @return IdP role or null
     */
    @Nullable IDPSSODescriptor getIDPSSODescriptor();

    /**
     * Get the AA role to generate.
     * 
     * <p>Only the endpoints and any basic flags are extracted from the role.</p>
     *  
     * @return AA role or null
     */
    @Nullable AttributeAuthorityDescriptor getAttributeAuthorityDescriptor();

    /**
     * Dual-use certificates.
     * 
     * <p>Keys will be applied to all roles.</p>
     * 
     * @return base64-encoded certificates
     */
    @Nonnull @Unmodifiable @NotLive List<String> getCertificates();

    /**
     * Signing-only certificate path(s).
     * 
     * <p>Keys will be applied to all roles.</p>
     * 
     * @return base64-encoded certificates
     */
    @Nonnull @Unmodifiable @NotLive List<String> getSigningCertificates();

    /**
     * Encryption-only certificate path(s).
     * 
     * <p>Keys will be applied to all roles.</p>
     * 
     * @return base64-encoded certificates
     */
    @Nonnull @Unmodifiable @NotLive List<String> getEncryptionCertificates();
        
}