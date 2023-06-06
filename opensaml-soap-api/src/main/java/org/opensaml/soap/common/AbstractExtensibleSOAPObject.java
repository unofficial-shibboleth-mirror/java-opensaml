/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.soap.common;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.ElementExtensibleXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Abstract class implementing element and attribute extensibility.
 */
public abstract class AbstractExtensibleSOAPObject extends AbstractXMLObject implements SOAPObject,
        AttributeExtensibleXMLObject, ElementExtensibleXMLObject {

    /** "Any" type children. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> unknownXMLObjects;

    /** Attributes of the proxied Element. */
    @Nonnull private final AttributeMap attributes;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    protected AbstractExtensibleSOAPObject(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        attributes = new AttributeMap(this);
        unknownXMLObjects = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        children.addAll(unknownXMLObjects);

        return CollectionSupport.copyToList(children);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<XMLObject> getUnknownXMLObjects() {
        return unknownXMLObjects;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nonnull public List<XMLObject> getUnknownXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) unknownXMLObjects.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return attributes;
    }

}