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

/**
 * SAML 2.0 Metadata AffiliationDescriptorType.
 */
public interface AffiliationDescriptor extends SignableSAMLObject, TimeBoundSAMLObject, CacheableSAMLObject,
        AttributeExtensibleXMLObject {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "AffiliationDescriptor";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "AffiliationDescriptorType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MD_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** "affiliationOwnerID" attribute's local name. */
    @Nonnull @NotEmpty static final String OWNER_ID_ATTRIB_NAME = "affiliationOwnerID";

    /** ID attribute's local name. */
    @Nonnull @NotEmpty static final String ID_ATTRIB_NAME = "ID";

    /**
     * Gets the ID of the owner of this affiliation. The owner may, or may not, be a member of the affiliation.
     * 
     * @return the ID of the owner of this affiliation
     */
    @Nullable String getOwnerID();

    /**
     * Gets the ID of this Descriptor.
     * 
     * @return the ID of this Descriptor
     */
    @Nullable String getID();

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
     * Sets the ID of the owner of this affiliation.
     * 
     * @param ownerID the ID of the owner of this affiliation
     */
    void setOwnerID(@Nullable final String ownerID);

    /**
     * Sets the ID of this descriptor.
     * 
     * @param newID the ID of this descriptor
     */
    void setID(@Nullable final String newID);

    /**
     * Gets a list of the members of this affiliation.
     * 
     * @return a list of affiliate members
     */
    @Nonnull @Live List<AffiliateMember> getMembers();

    /**
     * Gets a list of KeyDescriptors for this affiliation.
     * 
     * @return list of {@link KeyDescriptor}s for this affiliation
     */
    @Nonnull @Live List<KeyDescriptor> getKeyDescriptors();
}
