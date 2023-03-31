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

package org.opensaml.xmlsec.encryption.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.KeySize;
import org.opensaml.xmlsec.encryption.OAEPparams;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link EncryptionMethod}.
 */
public class EncryptionMethodImpl extends AbstractXMLObject implements EncryptionMethod {
    
    /** Algorithm attribute value. */
    @Nullable private String algorithm;
    
    /** KeySize child element value. */
    @Nullable private KeySize keySize;
    
    /** OAEPparams child element value. */
    @Nullable private OAEPparams oaepParams;
    
    /** "any" children. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> unknownChildren;
    
    /**
     * Constructor.
     *
     * @param namespaceURI namespace URI
     * @param elementLocalName local name
     * @param namespacePrefix namespace prefix
     */
    protected EncryptionMethodImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        
        unknownChildren = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public String getAlgorithm() {
        return algorithm;
    }

    /** {@inheritDoc} */
    public void setAlgorithm(@Nullable final String newAlgorithm) {
        algorithm = prepareForAssignment(algorithm, newAlgorithm);
    }

    /** {@inheritDoc} */
    @Nullable public KeySize getKeySize() {
        return keySize;
    }

    /** {@inheritDoc} */
    public void setKeySize(@Nullable final KeySize newKeySize) {
        keySize = prepareForAssignment(keySize, newKeySize);
    }

    /** {@inheritDoc} */
    @Nullable public OAEPparams getOAEPparams() {
        return oaepParams;
    }

    /** {@inheritDoc} */
    public void setOAEPparams(@Nullable final OAEPparams newOAEPparams) {
        oaepParams = prepareForAssignment(oaepParams, newOAEPparams);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<XMLObject> getUnknownXMLObjects() {
        return unknownChildren;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nonnull @Live public List<XMLObject> getUnknownXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) unknownChildren.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        if (keySize != null) {
            children.add(keySize);
        }
        if (oaepParams != null) {
            children.add(oaepParams);
        }
        
        children.addAll(unknownChildren);
        
        return CollectionSupport.copyToList(children);
    }

}