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

package org.opensaml.soap.wsaddressing.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.soap.wsaddressing.Address;
import org.opensaml.soap.wsaddressing.EndpointReferenceType;
import org.opensaml.soap.wsaddressing.Metadata;
import org.opensaml.soap.wsaddressing.ReferenceParameters;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Abstract implementation of the element of type {@link EndpointReferenceType }.
 * 
 */
public class EndpointReferenceTypeImpl extends AbstractWSAddressingObject implements EndpointReferenceType {

    /** {@link Address} child element. */
    @Nullable private Address address;

    /** Optional {@link Metadata} child element. */
    @Nullable private Metadata metadata;

    /** Optional {@link ReferenceParameters} child element. */
    @Nullable private ReferenceParameters referenceParameters;
    
    /** Wildcard child elements. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject>  unknownChildren;
    
    /** Wildcard attributes. */
    @Nonnull private final AttributeMap unknownAttributes;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public EndpointReferenceTypeImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownChildren = new IndexedXMLObjectChildrenList<>(this);
        unknownAttributes = new AttributeMap(this);
    }

    /** {@inheritDoc} */
    @Nullable public Address getAddress() {
        return address;
    }

    /** {@inheritDoc} */
    public void setAddress(@Nullable final Address newAddress) {
        address = prepareForAssignment(address, newAddress);
    }

    /** {@inheritDoc} */
    @Nullable public Metadata getMetadata() {
        return metadata;
    }

    /** {@inheritDoc} */
    public void setMetadata(@Nullable final Metadata newMetadata) {
        metadata = prepareForAssignment(metadata, newMetadata);
    }

    /** {@inheritDoc} */
    @Nullable public ReferenceParameters getReferenceParameters() {
        return referenceParameters;
    }

    /** {@inheritDoc} */
    public void setReferenceParameters(@Nullable final ReferenceParameters newReferenceParameters) {
        referenceParameters = prepareForAssignment(referenceParameters, newReferenceParameters);
    }

    /** {@inheritDoc} */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

    /** {@inheritDoc} */
    @Nonnull public List<XMLObject> getUnknownXMLObjects() {
        return unknownChildren;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nonnull public List<XMLObject> getUnknownXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) unknownChildren.subList(typeOrName);
    }
    
    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        if (address != null) {
            children.add(address);
        }
        if (referenceParameters != null) {
            children.add(referenceParameters);
        }
        if (metadata != null) {
            children.add(metadata);
        }

        // xs:any element
        children.addAll(getUnknownXMLObjects());

        return CollectionSupport.copyToList(children);
    }
 
}
