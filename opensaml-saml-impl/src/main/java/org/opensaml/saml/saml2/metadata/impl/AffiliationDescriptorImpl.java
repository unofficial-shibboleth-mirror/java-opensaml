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

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.common.AbstractSignableSAMLObject;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.AffiliateMember;
import org.opensaml.saml.saml2.metadata.AffiliationDescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.xmlsec.signature.Signature;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link AffiliationDescriptor}.
 */
public class AffiliationDescriptorImpl extends AbstractSignableSAMLObject implements AffiliationDescriptor {

    /** ID of the owner of this affiliation. */
    @Nullable private String ownerID;
    
    /** ID attribute. */
    @Nullable private String id;

    /** validUntil attribute. */
    @Nullable private Instant validUntil;

    /** cacheDurection attribute. */
    @Nullable private Duration cacheDuration;

    /** Extensions child. */
    @Nullable private Extensions extensions;
    
    /** "anyAttribute" attributes. */
    @Nonnull private final AttributeMap unknownAttributes;

    /** Members of this affiliation. */
    @Nonnull private final XMLObjectChildrenList<AffiliateMember> members;

    /** Key descriptors for this role. */
    @Nonnull private final XMLObjectChildrenList<KeyDescriptor> keyDescriptors;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace
     * @param elementLocalName localname
     * @param namespacePrefix prefix
     */
    protected AffiliationDescriptorImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
        members = new XMLObjectChildrenList<>(this);
        keyDescriptors = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public String getOwnerID() {
        return ownerID;
    }

    /** {@inheritDoc} */
    public void setOwnerID(@Nullable final String newOwnerID) {
        if (newOwnerID != null && newOwnerID.length() > 1024) {
            throw new IllegalArgumentException("Owner ID can not exceed 1024 characters in length");
        }
        ownerID = prepareForAssignment(ownerID, newOwnerID);
    }
    
    /** {@inheritDoc} */
    @Nullable public String getID() {
        return id;
    }
    
    /** {@inheritDoc} */
    public void setID(@Nullable final String newID) {
        final String oldID = id;
        this.id = prepareForAssignment(id, newID);
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
    public void setValidUntil(@Nullable final Instant theValidUntil) {
        validUntil = prepareForAssignment(validUntil, theValidUntil);
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
    @Nullable public Extensions getExtensions() {
        return extensions;
    }

    /** {@inheritDoc} */
    public void setExtensions(@Nullable final Extensions theExtensions) {
        extensions = prepareForAssignment(extensions, theExtensions);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AffiliateMember> getMembers() {
        return members;
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
    @Override
    @Nullable public String getSignatureReferenceID() {
        return id;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        final Signature sig = getSignature();
        if (sig != null) {
            children.add(sig);
        }
        
        if (extensions != null) {
            children.add(extensions);
        }

        children.addAll(getMembers());
        children.addAll(getKeyDescriptors());

        return CollectionSupport.copyToList(children);
    }

}