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

package org.opensaml.saml.saml1.core.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml1.core.AttributeDesignator;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * Concrete Implementation of the {@link AttributeDesignator} interface.
 */
public class AttributeDesignatorImpl extends AbstractXMLObject implements AttributeDesignator {

    /** Contains the AttributeName. */
    @Nullable private String attributeName;

    /** Contains the AttributeNamespace. */
    @Nullable private String attributeNamespace;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AttributeDesignatorImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public String getAttributeName() {
        return attributeName;
    }

    /** {@inheritDoc} */
    public void setAttributeName(@Nullable final String name) {
        attributeName = prepareForAssignment(attributeName, name);
    }

    /** {@inheritDoc} */
    @Nullable public String getAttributeNamespace() {
        return attributeNamespace;
    }

    /** {@inheritDoc} */
    public void setAttributeNamespace(@Nullable final String ns) {
        attributeNamespace = prepareForAssignment(attributeNamespace, ns);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return null;
    }
    
}