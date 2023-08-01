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

import java.util.Collection;
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
 * SAML 2.0 Metadata RoleDescriptor.
 */
public interface RoleDescriptor extends SignableSAMLObject, TimeBoundSAMLObject, CacheableSAMLObject,
        AttributeExtensibleXMLObject {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "RoleDescriptor";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "RoleDescriptorType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MD_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** "ID" attribute's local name. */
    @Nonnull @NotEmpty static final String ID_ATTRIB_NAME = "ID";

    /** "protocolEnumeration" attribute's local name. */
    @Nonnull @NotEmpty static final String PROTOCOL_ENUMERATION_ATTRIB_NAME = "protocolSupportEnumeration";

    /** "errorURL" attribute's local name. */
    @Nonnull @NotEmpty static final String ERROR_URL_ATTRIB_NAME = "errorURL";

    /**
     * Gets the ID of this role descriptor.
     * 
     * @return the ID of this role descriptor
     */
    @Nullable String getID();

    /**
     * Sets the ID of this role descriptor.
     * 
     * @param newID the ID of this role descriptor
     */
    void setID(@Nullable final String newID);

    /**
     * Gets an immutable list of protocol URIs supported by this role.
     * 
     * @return list of protocol URIs supported by this role
     */
    @Nonnull @NotLive @Unmodifiable List<String> getSupportedProtocols();

    /**
     * Chckes to see if the given protocol is supported by this role.
     * 
     * @param protocol the protocol
     * 
     * @return true if the protocol is supported, false if not
     */
    boolean isSupportedProtocol(@Nonnull @NotEmpty final String protocol);

    /**
     * Adds a protocol to the list of supported protocols for this role.
     * 
     * @param protocol the protocol
     */
    void addSupportedProtocol(@Nonnull @NotEmpty final String protocol);

    /**
     * Removes a protocol to the list of supported protocols for this role.
     * 
     * @param protocol the protocol
     */
    void removeSupportedProtocol(@Nonnull @NotEmpty final String protocol);

    /**
     * Removes a list of protocols to the list of supported protocols for this role.
     * 
     * @param protocols the protocol
     */
    void removeSupportedProtocols(@Nonnull Collection<String> protocols);

    /**
     * Removes all the supported protocols from this role.
     * 
     */
    void removeAllSupportedProtocols();

    /**
     * Gets the URI users should be sent to in the event of an error.
     * 
     * @return the URI users should be sent to in the event of an error
     */
    @Nullable String getErrorURL();

    /**
     * Sets the URI users should be sent to in the event of an error.
     * 
     * @param errorURL the URI users should be sent to in the event of an error
     */
    void setErrorURL(@Nullable final String errorURL);

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
     * Gets the key descriptors for this role.
     * 
     * @return the key descriptors for this role
     */
    @Nonnull @Live List<KeyDescriptor> getKeyDescriptors();

    /**
     * Gets the organization responsible for this role.
     * 
     * @return the organization responsible for this role
     */
    @Nullable Organization getOrganization();

    /**
     * Sets the organization responsible for this role.
     * 
     * @param organization the organization responsible for this role
     */
    void setOrganization(@Nullable final Organization organization);

    /**
     * Gets list of {@link ContactPerson}s for this role.
     * 
     * @return list of {@link ContactPerson}s for this role
     */
    @Nonnull @Live List<ContactPerson> getContactPersons();

    /**
     * Gets immutable list of endpoints for this role.
     * 
     * @return immutable list of endpoints for this role
     */
    @Nonnull @NotLive @Unmodifiable List<Endpoint> getEndpoints();

    /**
     * Gets a read-only list of endpoints for this role for the given type.
     * 
     * @param type the type of endpoints to retrieve
     * 
     * @return immutable list of endpoints for this role
     */
    @Nonnull @NotLive @Unmodifiable List<Endpoint> getEndpoints(@Nonnull final QName type);
}
