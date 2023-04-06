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

package org.opensaml.saml.saml2.core.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.BaseID;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDMappingRequest;
import org.opensaml.saml.saml2.core.NameIDPolicy;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * A concrete implementation of {@link NameIDMappingRequest}.
 */
public class NameIDMappingRequestImpl extends RequestAbstractTypeImpl implements NameIDMappingRequest {

    /** BaseID child element. */
    @Nullable private BaseID baseID;

    /** NameID child element. */
    @Nullable private NameID nameID;

    /** EncryptedID child element. */
    @Nullable private EncryptedID encryptedID;

    /** NameIDPolicy child element. */
    @Nullable private NameIDPolicy nameIDPolicy;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected NameIDMappingRequestImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public BaseID getBaseID() {
        return baseID;
    }

    /** {@inheritDoc} */
    public void setBaseID(@Nullable final BaseID newBaseID) {
        baseID = prepareForAssignment(baseID, newBaseID);
    }

    /** {@inheritDoc} */
    @Nullable public NameID getNameID() {
        return nameID;
    }

    /** {@inheritDoc} */
    public void setNameID(@Nullable final NameID newNameID) {
        nameID = prepareForAssignment(nameID, newNameID);
    }

    /** {@inheritDoc} */
    @Nullable public EncryptedID getEncryptedID() {
        return encryptedID;
    }

    /** {@inheritDoc} */
    public void setEncryptedID(@Nullable final EncryptedID newEncryptedID) {
        encryptedID = prepareForAssignment(encryptedID, newEncryptedID);
    }

    /** {@inheritDoc} */
    @Nullable public NameIDPolicy getNameIDPolicy() {
        return nameIDPolicy;
    }

    /** {@inheritDoc} */
    public void setNameIDPolicy(@Nullable final NameIDPolicy newNameIDPolicy) {
        nameIDPolicy = prepareForAssignment(nameIDPolicy, newNameIDPolicy);
    }

    /** {@inheritDoc} */
    @Override
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        final List<XMLObject> superKids = super.getOrderedChildren();
        if (superKids != null) {
            children.addAll(superKids);
        }

        if (baseID != null) {
            children.add(baseID);
        }

        if (nameID != null) {
            children.add(nameID);
        }
        
        if (encryptedID != null) {
            children.add(encryptedID);
        }

        if (nameIDPolicy != null) {
            children.add(nameIDPolicy);
        }

        return CollectionSupport.copyToList(children);
    }
    
}