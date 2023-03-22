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

package org.opensaml.soap.wsfed.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.soap.wsfed.AppliesTo;
import org.opensaml.soap.wsfed.RequestSecurityTokenResponse;
import org.opensaml.soap.wsfed.RequestedSecurityToken;

import net.shibboleth.shared.collection.CollectionSupport;

/** Implementation of the {@link RequestSecurityTokenResponse} object. */
public class RequestSecurityTokenResponseImpl extends AbstractXMLObject implements RequestSecurityTokenResponse {

    /** List of all the request security tokens. */
    @Nonnull private final XMLObjectChildrenList<RequestedSecurityToken> requestedSecurityTokens;

    /** Entity to whom the tokens apply. */
    @Nullable private AppliesTo appliesTo;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    RequestSecurityTokenResponseImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        requestedSecurityTokens = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nonnull public List<RequestedSecurityToken> getRequestedSecurityToken() {
        return requestedSecurityTokens;
    }

    /** {@inheritDoc} */
    @Nullable public AppliesTo getAppliesTo() {
        return appliesTo;
    }

    /** {@inheritDoc} */
    public void setAppliesTo(@Nullable final AppliesTo newappliesTo) {
        this.appliesTo = prepareForAssignment(this.appliesTo, newappliesTo);
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>(1 + requestedSecurityTokens.size());

        children.addAll(requestedSecurityTokens);
        if (appliesTo != null) {
            children.add(appliesTo);
        }

        return CollectionSupport.copyToList(children);
    }
}