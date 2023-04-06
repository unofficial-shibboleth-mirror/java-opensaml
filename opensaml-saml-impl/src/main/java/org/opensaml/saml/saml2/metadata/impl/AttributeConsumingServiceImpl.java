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

package org.opensaml.saml.saml2.metadata.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.RequestedAttribute;
import org.opensaml.saml.saml2.metadata.ServiceDescription;
import org.opensaml.saml.saml2.metadata.ServiceName;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link AttributeConsumingService}.
 */
public class AttributeConsumingServiceImpl extends AbstractXMLObject implements AttributeConsumingService {

    /** Index of this service. */
    @Nullable private Integer index;

    /** isDefault attribute of this service. */
    @Nullable private XSBooleanValue isDefault;

    /** ServiceName children. */
    @Nonnull private final XMLObjectChildrenList<ServiceName> serviceNames;

    /** ServiceDescription children. */
    @Nonnull private final XMLObjectChildrenList<ServiceDescription> serviceDescriptions;

    /** RequestedAttribute children. */
    @Nonnull private final XMLObjectChildrenList<RequestedAttribute> requestedAttributes;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI name space
     * @param elementLocalName local name
     * @param namespacePrefix prefix
     */
    protected AttributeConsumingServiceImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        serviceNames = new XMLObjectChildrenList<>(this);
        serviceDescriptions = new XMLObjectChildrenList<>(this);
        requestedAttributes = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public Integer getIndex() {
        return index;
    }

    /** {@inheritDoc} */
    public void setIndex(@Nullable final Integer theIndex) {
        index = prepareForAssignment(index, theIndex);
    }
    
    /** {@inheritDoc} */
    @Nullable public Boolean isDefault(){
        if(isDefault != null){
            return isDefault.getValue();
        }
        
        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    @Nullable public XSBooleanValue isDefaultXSBoolean() {
        return isDefault;
    }
    
    /** {@inheritDoc} */
    public void setIsDefault(@Nullable final Boolean newIsDefault){
        if(newIsDefault != null){
            isDefault = prepareForAssignment(isDefault, new XSBooleanValue(newIsDefault, false));
        }else{
            isDefault = prepareForAssignment(isDefault, null);
        }
    }

    /** {@inheritDoc} */
    public void setIsDefault(@Nullable final XSBooleanValue newIsDefault) {
        isDefault = prepareForAssignment(isDefault, newIsDefault);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<ServiceName> getNames() {
        return serviceNames;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<ServiceDescription> getDescriptions() {
        return serviceDescriptions;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<RequestedAttribute> getRequestedAttributes() {
        return requestedAttributes;
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        children.addAll(serviceNames);
        children.addAll(serviceDescriptions);
        children.addAll(requestedAttributes);

        return CollectionSupport.copyToList(children);
    }

}