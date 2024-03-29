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

import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.common.CacheableSAMLObject;
import org.opensaml.saml.saml2.common.TimeBoundSAMLObject;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Metadata EntitiesDescriptor.
 * 
 * @author Chad La Joie
 */
public interface EntitiesDescriptor extends SignableSAMLObject, TimeBoundSAMLObject, CacheableSAMLObject {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "EntitiesDescriptor";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "EntitiesDescriptorType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MD_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** Element QName, no prefix. */
    @Nonnull static final QName ELEMENT_QNAME = new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME);

    /** "ID" attribute name. */
    @Nonnull @NotEmpty static final String ID_ATTRIB_NAME = "ID";

    /** "Name" attribute name. */
    @Nonnull @NotEmpty static final String NAME_ATTRIB_NAME = "Name";

    /**
     * Gets the name of this entity group.
     * 
     * @return the name of this entity group
     */
    @Nullable String getName();

    /**
     * Sets the name of this entity group.
     * 
     * @param name the name of this entity group
     */
    void setName(@Nullable final String name);

    /**
     * Gets the ID of this entity group.
     * 
     * @return the id of this entity group
     */
    @Nullable String getID();

    /**
     * Sets the ID of this entity group.
     * 
     * @param newID the ID of this entity group
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
     * Gets a list of child {@link EntitiesDescriptor}s.
     * 
     * @return list of descriptors
     */
    @Nonnull @Live List<EntitiesDescriptor> getEntitiesDescriptors();

    /**
     * Gets a list of child {@link EntityDescriptor}s.
     * 
     * @return list of child descriptors
     */
    @Nonnull @Live List<EntityDescriptor> getEntityDescriptors();
}
