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
import org.opensaml.saml.saml2.core.StatusCode;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link StatusCode}.
 */
public class StatusCodeImpl extends AbstractXMLObject implements StatusCode {

    /** Value attribute URI. */
    @Nullable private String value;

    /** Nested secondary StatusCode child element. */
    @Nullable private StatusCode childStatusCode;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected StatusCodeImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public StatusCode getStatusCode() {
        return childStatusCode;
    }

    /** {@inheritDoc} */
    public void setStatusCode(@Nullable final StatusCode newStatusCode) {
        childStatusCode = prepareForAssignment(childStatusCode, newStatusCode);
    }

    /** {@inheritDoc} */
    @Nullable public String getValue() {
        return value;
    }

    /** {@inheritDoc} */
    public void setValue(@Nullable final String newValue) {
        value = prepareForAssignment(value, newValue);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        
        if (childStatusCode != null) {
            return CollectionSupport.singletonList(childStatusCode);
        }
        
        return null;
    }

}