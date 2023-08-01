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

package org.opensaml.saml.saml2.metadata;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.common.CacheableSAMLObject;
import org.opensaml.saml.saml2.common.TimeBoundSAMLObject;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * SAML 2.0 Metadata EntityDescriptor.
 */
public interface EntityDescriptor extends SignableSAMLObject, TimeBoundSAMLObject, CacheableSAMLObject,
        AttributeExtensibleXMLObject {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "EntityDescriptor";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "EntityDescriptorType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MD_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** Element QName, no prefix. */
    @Nonnull static final QName ELEMENT_QNAME = new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME);

    /** "ID" attribute name. */
    @Nonnull @NotEmpty static final String ID_ATTRIB_NAME = "ID";

    /** "Name" attribute name. */
    @Nonnull @NotEmpty static final String ENTITY_ID_ATTRIB_NAME = "entityID";

    /**
     * Gets the entity ID for this entity descriptor.
     * 
     * @return the entity ID for this entity descriptor
     */
    @Nullable String getEntityID();

    /**
     * Sets the entity ID for this entity descriptor.
     * 
     * @param id the entity ID for this entity descriptor
     */
    void setEntityID(@Nullable final String id);

    /**
     * Gets the ID for this entity descriptor.
     * 
     * @return the ID for this entity descriptor
     */
    @Nullable String getID();

    /**
     * Sets the ID for this entity descriptor.
     * 
     * @param newID the ID for this entity descriptor
     */
    void setID(@Nullable final String newID);

    /**
     * Gets the Extensions child of this object.
     * 
     * @return the Extensions child of this object
     */
    @Nullable Extensions getExtensions();

    /**
     * Sets the Extensions child of this object.
     * 
     * @param extensions the Extensions child of this object
     */
    void setExtensions(@Nullable final Extensions extensions);

    /**
     * Gets all the role descriptors for this entity descriptor.
     * 
     * @return the role descriptors for this entity descriptor
     */
    @Nonnull @Live List<RoleDescriptor> getRoleDescriptors();

    /**
     * Gets all the role descriptors for this entity descriptor that match the supplied QName parameter.
     * 
     * @param typeOrName the name of the role
     * 
     * @return the role descriptors for this entity descriptor
     */
    @Nonnull @Live List<RoleDescriptor> getRoleDescriptors(@Nonnull final QName typeOrName);

    /**
     * Gets all the role descriptors for this entity that support the given protocol.
     * 
     * @param typeOrName the name of the role
     * @param supportedProtocol the supported protocol
     * 
     * @return the list of role descriptors that support the given protocol
     */
    @Nonnull @Unmodifiable @NotLive List<RoleDescriptor> getRoleDescriptors(@Nonnull final QName typeOrName,
            @Nonnull @NotEmpty String supportedProtocol);

    /**
     * Gets the first {@link IDPSSODescriptor} role descriptor for this entity that supports the given protocol.
     * 
     * @param supportedProtocol protocol that must be supported
     * 
     * @return the {@link IDPSSODescriptor} role descriptor
     */
    @Nullable IDPSSODescriptor getIDPSSODescriptor(@Nonnull @NotEmpty final String supportedProtocol);

    /**
     * Gets the first {@link SPSSODescriptor} role descriptor for this entity that supports the given protocol.
     * 
     * @param supportedProtocol protocol that must be supported

     * @return the {@link SPSSODescriptor} role descriptor
     */
    @Nullable SPSSODescriptor getSPSSODescriptor(@Nonnull @NotEmpty final String supportedProtocol);

    /**
     * Gets the first {@link AuthnAuthorityDescriptor} role descriptor for this entity that supports the given protocol.
     * 
     * @param supportedProtocol protocol that must be supported
     * 
     * @return the {@link AuthnAuthorityDescriptor} role descriptor
     */
    @Nullable AuthnAuthorityDescriptor getAuthnAuthorityDescriptor(
            @Nonnull @NotEmpty final String supportedProtocol);

    /**
     * Gets the first {@link AttributeAuthorityDescriptor} role descriptor for this entity that supports the given
     * protocol.
     * 
     * @param supportedProtocol protocol that must be supported
     * 
     * @return the {@link AttributeAuthorityDescriptor} role descriptor
     */
    @Nullable AttributeAuthorityDescriptor getAttributeAuthorityDescriptor(
            @Nonnull @NotEmpty final String supportedProtocol);

    /**
     * Gets the first {@link PDPDescriptor} role descriptor for this entity that supports the given protocol.
     * 
     * @param supportedProtocol protocol that must be supported
     * 
     * @return the {@link PDPDescriptor} role descriptor
     */
    @Nullable PDPDescriptor getPDPDescriptor(@Nonnull @NotEmpty final String supportedProtocol);

    /**
     * Gets the affiliation descriptor for this entity.
     * 
     * @return the affiliation descriptor for this entity
     */
    @Nullable AffiliationDescriptor getAffiliationDescriptor();

    /**
     * Sets the affiliation descriptor for this entity.
     * 
     * @param descriptor the affiliation descriptor for this entity
     */
    void setAffiliationDescriptor(@Nullable final AffiliationDescriptor descriptor);

    /**
     * Gets the organization for this entity.
     * 
     * @return the organization for this entity
     */
    @Nullable Organization getOrganization();

    /**
     * Sets the organization for this entity.
     * 
     * @param organization the organization for this entity
     */
    void setOrganization(@Nullable final Organization organization);

    /**
     * Get the contact people for this entity.
     * 
     * @return the contact people for this entity
     */
    @Nonnull @Live List<ContactPerson> getContactPersons();

    /**
     * Gets the additional metadata locations for this entity.
     * 
     * @return the additional metadata locations for this entity
     */
    @Nonnull @Live List<AdditionalMetadataLocation> getAdditionalMetadataLocations();
}
