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

/**
 * 
 */

package org.opensaml.saml.saml2.core.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.IDPEntry;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * Concrete implementation of {@link IDPEntry}.
 */
public class IDPEntryImpl extends AbstractXMLObject implements IDPEntry {

    /** The unique identifier of the IdP. */
    @Nullable private String providerID;

    /** Human-readable name for the IdP. */
    @Nullable private String name;

    /**
     * URI reference representing the location of a profile-specific endpoint supporting the authentication request
     * protocol.
     */
    @Nullable private String loc;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected IDPEntryImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public String getProviderID() {
        return providerID;
    }

    /** {@inheritDoc} */
    public void setProviderID(@Nullable final String newProviderID) {
        providerID = prepareForAssignment(providerID, newProviderID);

    }

    /** {@inheritDoc} */
    @Nullable public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public void setName(@Nullable final String newName) {
        name = prepareForAssignment(name, newName);

    }

    /** {@inheritDoc} */
    @Nullable public String getLoc() {
        return loc;
    }

    /** {@inheritDoc} */
    public void setLoc(@Nullable final String newLoc) {
        loc = prepareForAssignment(loc, newLoc);

    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        // no children
        return null;
    }

}