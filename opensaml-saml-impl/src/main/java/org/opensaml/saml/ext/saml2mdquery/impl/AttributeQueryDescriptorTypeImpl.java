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

package org.opensaml.saml.ext.saml2mdquery.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.ext.saml2mdquery.AttributeQueryDescriptorType;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.Endpoint;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link AttributeQueryDescriptorType}.
 */
public class AttributeQueryDescriptorTypeImpl extends QueryDescriptorTypeImpl implements AttributeQueryDescriptorType {

    /** Attribute consuming endpoints. */
    @Nonnull private final XMLObjectChildrenList<AttributeConsumingService> attributeConsumingServices;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AttributeQueryDescriptorTypeImpl(@Nullable final String namespaceURI,
            @Nonnull final String elementLocalName, @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);

        attributeConsumingServices = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AttributeConsumingService> getAttributeConsumingServices() {
        return attributeConsumingServices;
    }
    
    /** {@inheritDoc} */
    @Nonnull @NotLive @Unmodifiable public List<Endpoint> getEndpoints() {
        return CollectionSupport.emptyList();
    }
    
    /** {@inheritDoc} */
    @Nonnull @NotLive @Unmodifiable public List<Endpoint> getEndpoints(@Nonnull final QName type) {
        return CollectionSupport.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        final List<XMLObject> superChildren = super.getOrderedChildren();
        if (superChildren != null) {
            children.addAll(superChildren);
        }
        children.addAll(attributeConsumingServices);

        return CollectionSupport.copyToList(children);
    }

}