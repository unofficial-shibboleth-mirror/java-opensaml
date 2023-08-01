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
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.metadata.AssertionIDRequestService;
import org.opensaml.saml.saml2.metadata.AttributeProfile;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.NameIDMappingService;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link IDPSSODescriptor}.
 */
public class IDPSSODescriptorImpl extends SSODescriptorImpl implements IDPSSODescriptor {

    /** wantAuthnRequestSigned attribute. */
    @Nullable private XSBooleanValue wantAuthnRequestsSigned;

    /** SingleSignOn services for this entity. */
    @Nonnull private final XMLObjectChildrenList<SingleSignOnService> singleSignOnServices;

    /** NameID mapping services for this entity. */
    @Nonnull private final XMLObjectChildrenList<NameIDMappingService> nameIDMappingServices;

    /** AssertionID request services for this entity. */
    @Nonnull private final XMLObjectChildrenList<AssertionIDRequestService> assertionIDRequestServices;

    /** Attribute profiles supported by this entity. */
    @Nonnull private final XMLObjectChildrenList<AttributeProfile> attributeProfiles;

    /** Attributes accepted by this entity. */
    @Nonnull private final XMLObjectChildrenList<Attribute> attributes;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected IDPSSODescriptorImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        singleSignOnServices = new XMLObjectChildrenList<>(this);
        nameIDMappingServices = new XMLObjectChildrenList<>(this);
        assertionIDRequestServices = new XMLObjectChildrenList<>(this);
        attributeProfiles = new XMLObjectChildrenList<>(this);
        attributes = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public Boolean getWantAuthnRequestsSigned() {
        if (wantAuthnRequestsSigned != null) {
            return wantAuthnRequestsSigned.getValue();
        }

        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    @Nullable public XSBooleanValue getWantAuthnRequestsSignedXSBoolean() {
        return wantAuthnRequestsSigned;
    }

    /** {@inheritDoc} */
    public void setWantAuthnRequestsSigned(@Nullable final Boolean newWantSigned) {
        if (newWantSigned != null) {
            wantAuthnRequestsSigned =
                    prepareForAssignment(wantAuthnRequestsSigned, new XSBooleanValue(newWantSigned, false));
        } else {
            wantAuthnRequestsSigned = prepareForAssignment(wantAuthnRequestsSigned, null);
        }
    }

    /** {@inheritDoc} */
    public void setWantAuthnRequestsSigned(@Nullable final XSBooleanValue wantSigned) {
        wantAuthnRequestsSigned = prepareForAssignment(wantAuthnRequestsSigned, wantSigned);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<SingleSignOnService> getSingleSignOnServices() {
        return singleSignOnServices;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<NameIDMappingService> getNameIDMappingServices() {
        return nameIDMappingServices;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AssertionIDRequestService> getAssertionIDRequestServices() {
        return assertionIDRequestServices;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AttributeProfile> getAttributeProfiles() {
        return attributeProfiles;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<Attribute> getAttributes() {
        return attributes;
    }

    /** {@inheritDoc} */
    @Nonnull @NotLive @Unmodifiable public List<Endpoint> getEndpoints() {
        final List<Endpoint> endpoints = new ArrayList<>();
        endpoints.addAll(super.getEndpoints());
        endpoints.addAll(singleSignOnServices);
        endpoints.addAll(nameIDMappingServices);
        endpoints.addAll(assertionIDRequestServices);
        return CollectionSupport.copyToList(endpoints);
    }

    /** {@inheritDoc} */
    @Nonnull @NotLive @Unmodifiable public List<Endpoint> getEndpoints(@Nonnull final QName type) {
        if (type.equals(SingleSignOnService.DEFAULT_ELEMENT_NAME)) {
            return CollectionSupport.copyToList(singleSignOnServices);
        } else if (type.equals(NameIDMappingService.DEFAULT_ELEMENT_NAME)) {
            return CollectionSupport.copyToList(nameIDMappingServices);
        } else if (type.equals(AssertionIDRequestService.DEFAULT_ELEMENT_NAME)) {
            return CollectionSupport.copyToList(assertionIDRequestServices);
        } else {
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
        
        children.addAll(singleSignOnServices);
        children.addAll(nameIDMappingServices);
        children.addAll(assertionIDRequestServices);
        children.addAll(attributeProfiles);
        children.addAll(attributes);

        return CollectionSupport.copyToList(children);
    }
}