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

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.collection.LazyList;
import net.shibboleth.shared.primitive.StringSupport;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.common.AbstractSignableSAMLObject;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.xmlsec.signature.Signature;

/** Concrete implementation of {@link RoleDescriptor}. */
public abstract class RoleDescriptorImpl extends AbstractSignableSAMLObject implements RoleDescriptor {

    /** ID attribute. */
    @Nullable private String id;

    /** validUntil attribute. */
    @Nullable private Instant validUntil;

    /** cacheDurection attribute. */
    @Nullable private Duration cacheDuration;

    /** Set of supported protocols. */
    @Nonnull private final List<String> supportedProtocols;

    /** Error URL. */
    @Nullable private String errorURL;

    /** Extensions child. */
    @Nullable private Extensions extensions;

    /** Organization administering this role. */
    @Nullable private Organization organization;

    /** "anyAttribute" attributes. */
    @Nonnull private final AttributeMap unknownAttributes;

    /** Contact persons for this role. */
    @Nonnull private final XMLObjectChildrenList<ContactPerson> contactPersons;

    /** Key descriptors for this role. */
    @Nonnull private final XMLObjectChildrenList<KeyDescriptor> keyDescriptors;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected RoleDescriptorImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
        supportedProtocols = new LazyList<>();
        contactPersons = new XMLObjectChildrenList<>(this);
        keyDescriptors = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public String getID() {
        return id;
    }

    /** {@inheritDoc} */
    public void setID(@Nullable final String newID) {
        final String oldID = id;
        id = prepareForAssignment(id, newID);
        registerOwnID(oldID, id);
    }

    /** {@inheritDoc} */
    public boolean isValid() {
        if (null == validUntil) {
            return true;
        }
        
        return Instant.now().isBefore(validUntil);
    }

    /** {@inheritDoc} */
    @Nullable public Instant getValidUntil() {
        return validUntil;
    }

    /** {@inheritDoc} */
    public void setValidUntil(@Nullable final Instant dt) {
        validUntil = prepareForAssignment(validUntil, dt);
    }

    /** {@inheritDoc} */
    @Nullable public Duration getCacheDuration() {
        return cacheDuration;
    }

    /** {@inheritDoc} */
    public void setCacheDuration(@Nullable final Duration duration) {
        cacheDuration = prepareForAssignment(cacheDuration, duration);
    }

    /** {@inheritDoc} */
    @Nonnull @NotLive @Unmodifiable public List<String> getSupportedProtocols() {
        return CollectionSupport.copyToList(supportedProtocols);
    }

    /** {@inheritDoc} */
    public boolean isSupportedProtocol(@Nonnull @NotEmpty final String protocol) {
        return supportedProtocols.contains(protocol);
    }

    /** {@inheritDoc} */
    public void addSupportedProtocol(@Nonnull @NotEmpty final String protocol) {
        final String trimmed = StringSupport.trimOrNull(protocol);
        if (trimmed != null && !supportedProtocols.contains(trimmed)) {
            releaseThisandParentDOM();
            supportedProtocols.add(trimmed);
        }
    }

    /** {@inheritDoc} */
    public void removeSupportedProtocol(@Nonnull @NotEmpty final String protocol) {
        final String trimmed = StringSupport.trimOrNull(protocol);
        if (trimmed != null && supportedProtocols.contains(trimmed)) {
            releaseThisandParentDOM();
            supportedProtocols.remove(trimmed);
        }
    }

    /** {@inheritDoc} */
    public void removeSupportedProtocols(@Nonnull final Collection<String> protocols) {
        for (final String protocol : protocols) {
            assert protocol != null;
            removeSupportedProtocol(protocol);
        }
    }

    /** {@inheritDoc} */
    public void removeAllSupportedProtocols() {
        supportedProtocols.clear();
    }

    /** {@inheritDoc} */
    @Nullable public String getErrorURL() {
        return errorURL;
    }

    /** {@inheritDoc} */
    public void setErrorURL(@Nullable final String url) {
        errorURL = prepareForAssignment(errorURL, url);
    }

    /** {@inheritDoc} */
    @Nullable public Extensions getExtensions() {
        return extensions;
    }

    /** {@inheritDoc} */
    public void setExtensions(@Nullable final Extensions ext) {
        extensions = prepareForAssignment(extensions, ext);
    }

    /** {@inheritDoc} */
    @Nullable public Organization getOrganization() {
        return organization;
    }

    /** {@inheritDoc} */
    public void setOrganization(@Nullable final Organization org) {
        organization = prepareForAssignment(organization, org);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<ContactPerson> getContactPersons() {
        return contactPersons;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<KeyDescriptor> getKeyDescriptors() {
        return keyDescriptors;
    }

    /** {@inheritDoc} */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

    /** {@inheritDoc} */
    @Nullable public String getSignatureReferenceID() {
        return id;
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        final Signature sig = getSignature();
        if (sig != null) {
            children.add(sig);
        }

        if (extensions != null) {
            children.add(getExtensions());
        }
        
        children.addAll(getKeyDescriptors());
        
        if (organization != null) {
            children.add(getOrganization());
        }
        
        children.addAll(getContactPersons());

        return CollectionSupport.copyToList(children);
    }
    
}