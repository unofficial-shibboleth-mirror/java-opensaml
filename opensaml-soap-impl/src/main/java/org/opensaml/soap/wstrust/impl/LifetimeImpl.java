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

package org.opensaml.soap.wstrust.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.soap.wssecurity.Created;
import org.opensaml.soap.wssecurity.Expires;
import org.opensaml.soap.wstrust.Lifetime;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * LifetimeImpl.
 * 
 */
public class LifetimeImpl extends AbstractWSTrustObject implements Lifetime {

    /** The wsu:Created child element. */
    @Nullable private Created created;

    /** The wsu:Expires child element. */
    @Nullable private Expires expires;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public LifetimeImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public Created getCreated() {
        return created;
    }

    /** {@inheritDoc} */
    @Nullable public Expires getExpires() {
        return expires;
    }

    /** {@inheritDoc} */
    public void setCreated(@Nullable final Created newCreated) {
        created = prepareForAssignment(created, newCreated);
    }

    /** {@inheritDoc} */
    public void setExpires(@Nullable final Expires newExpires) {
        expires = prepareForAssignment(expires, newExpires);
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        if (created != null) {
            children.add(created);
        }
        if (expires != null) {
            children.add(expires);
        }
        return CollectionSupport.copyToList(children);
    }

}
