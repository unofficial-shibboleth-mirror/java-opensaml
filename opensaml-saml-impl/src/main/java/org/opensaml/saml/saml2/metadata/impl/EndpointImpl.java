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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.saml.saml2.metadata.Endpoint;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * A concrete implementation of {@link Endpoint}.
 */
public abstract class EndpointImpl extends AbstractXMLObject implements Endpoint {

    /** Binding URI. */
    @Nullable private String bindingId;

    /** Endpoint location URI. */
    @Nullable private String location;

    /** Response location URI. */
    @Nullable private String responseLocation;

    /** "anyAttribute" attributes. */
    @Nonnull private final AttributeMap unknownAttributes;

    /** child "any" elements. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> unknownChildren;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected EndpointImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
        unknownChildren = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public String getBinding() {
        return bindingId;
    }

    /** {@inheritDoc} */
    public void setBinding(@Nullable final String binding) {
        bindingId = prepareForAssignment(bindingId, binding);
    }

    /** {@inheritDoc} */
    @Nullable public String getLocation() {
        return location;
    }

    /** {@inheritDoc} */
    public void setLocation(@Nullable final String theLocation) {
        this.location = prepareForAssignment(this.location, theLocation);
    }

    /** {@inheritDoc} */
    @Nullable public String getResponseLocation() {
        return responseLocation;
    }

    /** {@inheritDoc} */
    public void setResponseLocation(@Nullable final String theLocation) {
        responseLocation = prepareForAssignment(responseLocation, theLocation);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull @Live public List<XMLObject> getUnknownXMLObjects() {
        return unknownChildren;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nonnull @Live public List<XMLObject> getUnknownXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) unknownChildren.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(unknownChildren);
    }
    
}