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

package org.opensaml.soap.wspolicy.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.soap.wspolicy.Policy;
import org.opensaml.soap.wssecurity.IdBearing;

/**
 * PolicyImpl.
 * 
 */
public class PolicyImpl extends OperatorContentTypeImpl implements Policy {

    /** The wsu:Id attribute value. */
    @Nullable private String id;

    /** The Name attribute value. */
    @Nullable private String name;
    
    /** Wildcard attributes. */
    @Nonnull private final AttributeMap unknownAttributes;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    protected PolicyImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
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
    @Nullable public String getWSUId() {
        return id;
    }

    /** {@inheritDoc} */
    public void setWSUId(@Nullable final String newId) {
        final String oldId = id;
        id = prepareForAssignment(id, newId);
        registerOwnID(oldId, id);
        manageQualifiedAttributeNamespace(IdBearing.WSU_ID_ATTR_NAME, id != null);
    }
    
    /** {@inheritDoc} */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

}