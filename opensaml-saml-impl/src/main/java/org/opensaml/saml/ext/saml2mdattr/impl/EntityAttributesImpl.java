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

package org.opensaml.saml.ext.saml2mdattr.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.ext.saml2mdattr.EntityAttributes;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.logic.PredicateSupport;

/** Concrete implementation of {@link EntityAttributes}. */
public class EntityAttributesImpl extends AbstractXMLObject implements EntityAttributes {

    /** Extension data. */
    @Nonnull private final IndexedXMLObjectChildrenList<SAMLObject> attributeInfo;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected EntityAttributesImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        attributeInfo = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nonnull @Live public List<Attribute> getAttributes() {
        return (List<Attribute>) attributeInfo.subList(Attribute.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nonnull @Live public List<Assertion> getAssertions() {
        return (List<Assertion>) attributeInfo.subList(Assertion.DEFAULT_ELEMENT_NAME);
    }
    
    /** {@inheritDoc} */
    @Nonnull @Live public List<SAMLObject> getEntityAttributesChildren() {
        return attributeInfo;
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {

        if (attributeInfo.size() == 0) {
            return null;
        }

        return attributeInfo
                .stream()
                .filter(PredicateSupport.or(Assertion.class::isInstance, Attribute.class::isInstance))
                .collect(Collectors.toUnmodifiableList());
    }

}