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

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.signature.ECKeyValue;
import org.opensaml.xmlsec.signature.NamedCurve;
import org.opensaml.xmlsec.signature.PublicKey;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link ECKeyValue}.
 */
public class ECKeyValueImpl extends AbstractXMLObject implements ECKeyValue {
    
    /** Id attribute value. */
    @Nullable private String id;
    
    /** ECParameters child element value. */
    @Nullable private XMLObject ecParams;
    
    /** NamedCurve child element value. */
    @Nullable private NamedCurve namedCurve;

    /** PublicKey child element value. */
    @Nullable private PublicKey publicKey;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected ECKeyValueImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
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
    @Nullable public XMLObject getECParameters() {
        return ecParams;
    }

    /** {@inheritDoc} */
    public void setECParameters(@Nullable final XMLObject newParams) {
        ecParams = prepareForAssignment(ecParams, newParams);
    }
    
    /** {@inheritDoc} */
    @Nullable public NamedCurve getNamedCurve() {
        return namedCurve;
    }

    /** {@inheritDoc} */
    public void setNamedCurve(@Nullable final NamedCurve newCurve) {
        namedCurve = prepareForAssignment(namedCurve, newCurve);
    }

    /** {@inheritDoc} */
    @Nullable public PublicKey getPublicKey() {
        return publicKey;
    }

    /** {@inheritDoc} */
    public void setPublicKey(@Nullable final PublicKey newKey) {
        publicKey = prepareForAssignment(publicKey, newKey);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        if (ecParams != null) {
            children.add(ecParams);
        }
        if (namedCurve != null) {
            children.add(namedCurve);
        }
        if (publicKey != null) {
            children.add(publicKey);
        }
        
        return CollectionSupport.copyToList(children);
    }

}