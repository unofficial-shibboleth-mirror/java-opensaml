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
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.saml.common.AbstractSignableSAMLObject;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.xmlsec.signature.Signature;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

/**
 * Concrete implementation of {@link EntitiesDescriptor}.
 */
public class EntitiesDescriptorImpl extends AbstractSignableSAMLObject implements EntitiesDescriptor {

    /** Name of this descriptor group. */
    @Nullable private String name;

    /** ID attribute. */
    @Nullable private String id;

    /** validUntil attribute. */
    @Nullable private Instant validUntil;

    /** cacheDurection attribute. */
    @Nullable private Duration cacheDuration;

    /** Extensions child. */
    @Nullable private Extensions extensions;

    /** Ordered set of child Entity/Entities Descriptors. */
    @Nonnull private final IndexedXMLObjectChildrenList<SAMLObject> orderedDescriptors;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected EntitiesDescriptorImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        orderedDescriptors = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public void setName(@Nullable final String newName) {
        name = prepareForAssignment(name, newName);
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
    public void setValidUntil(@Nullable final Instant newValidUntil) {
        validUntil = prepareForAssignment(validUntil, newValidUntil);
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
    public void setExtensions(@Nullable final Extensions newExtensions) {
        extensions = prepareForAssignment(extensions, newExtensions);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nonnull @Live public List<EntitiesDescriptor> getEntitiesDescriptors() {
        return (List<EntitiesDescriptor>) orderedDescriptors.subList(EntitiesDescriptor.ELEMENT_QNAME);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nonnull @Live public List<EntityDescriptor> getEntityDescriptors() {
        return (List<EntityDescriptor>) orderedDescriptors.subList(EntityDescriptor.ELEMENT_QNAME);
    }
    
    /** {@inheritDoc} */
    public String getSignatureReferenceID(){
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
            children.add(extensions);
        }
        
        children.addAll(orderedDescriptors);

        return CollectionSupport.copyToList(children);
    }
    
}