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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.soap.wsaddressing.Metadata;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * MetadataImpl.
 * 
 */
public class MetadataImpl extends AbstractWSAddressingObject implements Metadata {
    
    /** Wildcard attributes. */
    @Nonnull private final AttributeMap unknownAttributes;
    
    /** Wildcard child elements. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> unknownChildren;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public MetadataImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
        unknownChildren = new IndexedXMLObjectChildrenList<>(this);
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
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(unknownChildren);
    }

}