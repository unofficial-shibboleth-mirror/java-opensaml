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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.saml2.core.Attribute;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link Attribute}.
 */
public class AttributeImpl extends AbstractXMLObject implements Attribute {

    /** Name of the attribute. */
    @Nullable private String name;

    /** Format of the name of the attribute. */
    @Nullable private String nameFormat;

    /** Human readable name of the attribute. */
    @Nullable private String friendlyName;

    /** "anyAttribute" attributes. */
    @Nonnull private final AttributeMap unknownAttributes;

    /** List of attribute values for this attribute. */
    @Nonnull private final XMLObjectChildrenList<XMLObject> attributeValues;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AttributeImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
        attributeValues = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    public void setName(@Nullable final String n) {
        name = prepareForAssignment(name, n);
    }

    /** {@inheritDoc} */
    @Nullable public String getNameFormat() {
        return nameFormat;
    }

    /** {@inheritDoc} */
    public void setNameFormat(@Nullable final String format) {
        nameFormat = prepareForAssignment(nameFormat, format);
    }

    /** {@inheritDoc} */
    @Nullable public String getFriendlyName() {
        return friendlyName;
    }

    /** {@inheritDoc} */
    public void setFriendlyName(@Nullable final String fname) {
        friendlyName = prepareForAssignment(friendlyName, fname);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<XMLObject> getAttributeValues() {
        return attributeValues;
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(attributeValues);
    }

}