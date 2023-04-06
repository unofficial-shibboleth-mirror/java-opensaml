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
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.metadata.support.AttributeConsumingServiceSelector;
import org.opensaml.saml.metadata.support.SAML2MetadataSupport;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link SPSSODescriptor}.
 */
public class SPSSODescriptorImpl extends SSODescriptorImpl implements SPSSODescriptor {

    /** value for isAuthnRequestSigned attribute. */
    @Nullable private XSBooleanValue authnRequestSigned;

    /** value for the want assertion signed attribute. */
    @Nullable private XSBooleanValue assertionSigned;

    /** AssertionConsumerService children. */
    @Nonnull private final XMLObjectChildrenList<AssertionConsumerService> assertionConsumerServices;

    /** AttributeConsumingService children. */
    @Nonnull private final XMLObjectChildrenList<AttributeConsumingService> attributeConsumingServices;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected SPSSODescriptorImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        assertionConsumerServices = new XMLObjectChildrenList<>(this);
        attributeConsumingServices = new XMLObjectChildrenList<>(this);
    }
    
    /** {@inheritDoc} */
    @Nullable public Boolean isAuthnRequestsSigned() {
        if (authnRequestSigned != null) {
            return authnRequestSigned.getValue();
        }
        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    @Nullable public XSBooleanValue isAuthnRequestsSignedXSBoolean() {
        return authnRequestSigned;
    }
    
    /** {@inheritDoc} */
    public void setAuthnRequestsSigned(@Nullable final Boolean newIsSigned) {
        if(newIsSigned != null){
            authnRequestSigned = prepareForAssignment(authnRequestSigned, new XSBooleanValue(newIsSigned, false));
        }else{
            authnRequestSigned = prepareForAssignment(authnRequestSigned, null);
        }
    }

    /** {@inheritDoc} */
    public void setAuthnRequestsSigned(@Nullable final XSBooleanValue isSigned) {
        authnRequestSigned = prepareForAssignment(authnRequestSigned, isSigned);
    }
    
    /** {@inheritDoc} */
    @Nullable public Boolean getWantAssertionsSigned() {
        if (assertionSigned != null) {
            return assertionSigned.getValue();
        }
        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    @Nullable public XSBooleanValue getWantAssertionsSignedXSBoolean() {
        return assertionSigned;
    }
    
    /** {@inheritDoc} */
    public void setWantAssertionsSigned(@Nullable final Boolean wantAssestionSigned) {
        if(wantAssestionSigned != null){
            assertionSigned = prepareForAssignment(assertionSigned, new XSBooleanValue(wantAssestionSigned, false));
        }else{
            assertionSigned = prepareForAssignment(assertionSigned, null);
        }
    }

    /** {@inheritDoc} */
    public void setWantAssertionsSigned(@Nullable final XSBooleanValue wantAssestionSigned) {
        this.assertionSigned = prepareForAssignment(this.assertionSigned, wantAssestionSigned);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AssertionConsumerService> getAssertionConsumerServices() {
        return assertionConsumerServices;
    }
    
    /** {@inheritDoc} */
    @Nullable public AssertionConsumerService getDefaultAssertionConsumerService() {
        return SAML2MetadataSupport.getDefaultIndexedEndpoint(assertionConsumerServices);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AttributeConsumingService> getAttributeConsumingServices() {
        return attributeConsumingServices;
    }
    
    /** {@inheritDoc} */
    @Nullable public AttributeConsumingService getDefaultAttributeConsumingService(){
        final AttributeConsumingServiceSelector selector = new AttributeConsumingServiceSelector();
        selector.setRoleDescriptor(this);
        return selector.selectService();
    }
    
    /** {@inheritDoc} */
    @Nonnull @NotLive @Unmodifiable public List<Endpoint> getEndpoints() {
        final List<Endpoint> endpoints = new ArrayList<>();
        endpoints.addAll(super.getEndpoints());
        endpoints.addAll(assertionConsumerServices);
        return CollectionSupport.copyToList(endpoints);
    }
    
    /** {@inheritDoc} */
    @Nonnull @NotLive @Unmodifiable public List<Endpoint> getEndpoints(@Nonnull final QName type) {
        if( type.equals(AssertionConsumerService.DEFAULT_ELEMENT_NAME)) {
            return CollectionSupport.copyToList(assertionConsumerServices);
        }else{
            return super.getEndpoints(type);
        }
    }

    /** {@inheritDoc} */
    @Override
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        final List<XMLObject> parentChildren = super.getOrderedChildren();
        if (parentChildren != null) {
            children.addAll(parentChildren);
        }

        children.addAll(assertionConsumerServices);
        children.addAll(attributeConsumingServices);

        return CollectionSupport.copyToList(children);
    }

}