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

package org.opensaml.xmlsec.signature.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.xmlsec.signature.PGPData;
import org.opensaml.xmlsec.signature.PGPKeyID;
import org.opensaml.xmlsec.signature.PGPKeyPacket;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link PGPData}.
 */
public class PGPDataImpl extends AbstractXMLObject implements PGPData {
    
    /** PGPKeyID child element value. */
    @Nullable private PGPKeyID pgpKeyID;
    
    /** PGPKeyPacket child element value. */
    @Nullable private PGPKeyPacket pgpKeyPacket;
    
    /** List of &lt;any&gt; wildcard XMLObject children. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> xmlChildren;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected PGPDataImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        xmlChildren = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public PGPKeyID getPGPKeyID() {
        return pgpKeyID;
    }

    /** {@inheritDoc} */
    public void setPGPKeyID(@Nullable final PGPKeyID newPGPKeyID) {
        pgpKeyID = prepareForAssignment(pgpKeyID, newPGPKeyID);
    }

    /** {@inheritDoc} */
    @Nullable public PGPKeyPacket getPGPKeyPacket() {
        return pgpKeyPacket;
    }

    /** {@inheritDoc} */
    public void setPGPKeyPacket(@Nullable final PGPKeyPacket newPGPKeyPacket) {
        pgpKeyPacket = prepareForAssignment(pgpKeyPacket, newPGPKeyPacket);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<XMLObject> getUnknownXMLObjects() {
        return xmlChildren;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nonnull @Live public List<XMLObject> getUnknownXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) xmlChildren.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        if (pgpKeyID != null) {
            children.add(pgpKeyID);
        }
        if (pgpKeyPacket != null) {
            children.add(pgpKeyPacket);
        }
        
        children.addAll(xmlChildren);
        
        return CollectionSupport.copyToList(children);
    }

}