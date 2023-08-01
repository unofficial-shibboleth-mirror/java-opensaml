/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.saml1.core.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.saml1.core.AttributeDesignator;
import org.opensaml.saml.saml1.core.AttributeQuery;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of the {@link AttributeQuery} interface.
 */
public class AttributeQueryImpl extends SubjectQueryImpl implements AttributeQuery {

    /** Contains the resource attribute. */
    @Nullable private String resource;

    /** Contains all the child AttributeDesignators. */
    @Nonnull private final XMLObjectChildrenList<AttributeDesignator> attributeDesignators;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AttributeQueryImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        attributeDesignators = new XMLObjectChildrenList<>(this);
    }
    
    /** {@inheritDoc} */
    @Nullable public String getResource() {
        return resource;
    }

    /** {@inheritDoc} */
    public void setResource(@Nullable final String res) {
        resource = prepareForAssignment(resource, res);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AttributeDesignator> getAttributeDesignators() {
        return attributeDesignators;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final List<XMLObject> list = new ArrayList<>(attributeDesignators.size() + 1);
        
        final List<XMLObject> superKids = super.getOrderedChildren();
        if (superKids != null) {
            list.addAll(superKids);
        }
        
        list.addAll(attributeDesignators);
        
        return CollectionSupport.copyToList(list);
    }

}