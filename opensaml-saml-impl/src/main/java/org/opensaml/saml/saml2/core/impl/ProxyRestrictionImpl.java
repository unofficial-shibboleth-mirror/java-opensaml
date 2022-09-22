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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.ProxyRestriction;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Concrete implementation of {@link org.opensaml.saml.saml2.core.ProxyRestriction}.
 */
public class ProxyRestrictionImpl extends AbstractXMLObject implements ProxyRestriction {

    /** Audiences of the Restriction. */
    @Nonnull private final XMLObjectChildrenList<Audience> audiences;

    /** Count of the Restriction. */
    @Nullable private Integer proxyCount;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected ProxyRestrictionImpl(@Nullable @NotEmpty final String namespaceURI,
            @Nonnull @NotEmpty final String elementLocalName, @Nullable @NotEmpty final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        audiences = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public List<Audience> getAudiences() {
        return audiences;
    }

    /** {@inheritDoc} */
    @Nullable public Integer getProxyCount() {
        return proxyCount;
    }

    /** {@inheritDoc} */
    public void setProxyCount(@Nullable final Integer newProxyCount) {
        if (newProxyCount == null || newProxyCount >= 0) {
            proxyCount = prepareForAssignment(proxyCount, newProxyCount);
        } else {
            throw new IllegalArgumentException("Count must be a non-negative integer.");
        }
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        children.addAll(audiences);
        return Collections.unmodifiableList(children);
    }
    
}