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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.Scoping;
import org.opensaml.saml.saml2.core.Subject;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * A concrete implementation of {@link AuthnRequest}.
 */
public class AuthnRequestImpl extends RequestAbstractTypeImpl implements AuthnRequest {

    /** Subject child element. */
    @Nullable private Subject subject;

    /** NameIDPolicy child element. */
    @Nullable private NameIDPolicy nameIDPolicy;

    /** Conditions child element. */
    @Nullable private Conditions conditions;

    /** RequestedAuthnContext child element. */
    @Nullable private RequestedAuthnContext requestedAuthnContext;

    /** Scoping child element. */
    @Nullable private Scoping scoping;

    /** ForeceAuthn attribute. */
    @Nullable private XSBooleanValue forceAuthn;

    /** IsPassive attribute. */
    @Nullable private XSBooleanValue isPassive;

    /** ProtocolBinding attribute. */
    @Nullable private String protocolBinding;

    /** AssertionConsumerServiceIndex attribute. */
    @Nullable private Integer assertionConsumerServiceIndex;

    /** AssertionConsumerServiceURL attribute. */
    @Nullable private String assertionConsumerServiceURL;

    /** AttributeConsumingServiceIndex attribute. */
    @Nullable private Integer attributeConsumingServiceIndex;

    /** ProviderName attribute. */
    @Nullable private String providerName;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AuthnRequestImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public Boolean isForceAuthn() {
        if (forceAuthn != null) {
            return forceAuthn.getValue();
        }

        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    @Nullable public XSBooleanValue isForceAuthnXSBoolean() {
        return forceAuthn;
    }

    /** {@inheritDoc} */
    public void setForceAuthn(@Nullable final Boolean newForceAuth) {
        if (newForceAuth != null) {
            forceAuthn = prepareForAssignment(forceAuthn, new XSBooleanValue(newForceAuth, false));
        } else {
            forceAuthn = prepareForAssignment(forceAuthn, null);
        }
    }

    /** {@inheritDoc} */
    public void setForceAuthn(@Nullable final XSBooleanValue newForceAuthn) {
        forceAuthn = prepareForAssignment(forceAuthn, newForceAuthn);
    }

    /** {@inheritDoc} */
    @Nullable public Boolean isPassive() {
        if (isPassive != null) {
            return isPassive.getValue();
        }

        return Boolean.FALSE;
    }

    /** {@inheritDoc} */
    @Nullable public XSBooleanValue isPassiveXSBoolean() {
        return isPassive;
    }

    /** {@inheritDoc} */
    public void setIsPassive(@Nullable final Boolean newIsPassive) {
        if (newIsPassive != null) {
            isPassive = prepareForAssignment(isPassive, new XSBooleanValue(newIsPassive, false));
        } else {
            isPassive = prepareForAssignment(isPassive, null);
        }
    }

    /** {@inheritDoc} */
    public void setIsPassive(@Nullable final XSBooleanValue newIsPassive) {
        isPassive = prepareForAssignment(isPassive, newIsPassive);
    }

    /** {@inheritDoc} */
    @Nullable public String getProtocolBinding() {
        return protocolBinding;
    }

    /** {@inheritDoc} */
    public void setProtocolBinding(@Nullable final String newProtocolBinding) {
        protocolBinding = prepareForAssignment(protocolBinding, newProtocolBinding);
    }

    /** {@inheritDoc} */
    @Nullable public Integer getAssertionConsumerServiceIndex() {
        return assertionConsumerServiceIndex;
    }

    /** {@inheritDoc} */
    public void setAssertionConsumerServiceIndex(@Nullable final Integer newAssertionConsumerServiceIndex) {
        assertionConsumerServiceIndex = prepareForAssignment(assertionConsumerServiceIndex,
                newAssertionConsumerServiceIndex);
    }

    /** {@inheritDoc} */
    @Nullable public String getAssertionConsumerServiceURL() {
        return assertionConsumerServiceURL;
    }

    /** {@inheritDoc} */
    public void setAssertionConsumerServiceURL(@Nullable final String newAssertionConsumerServiceURL) {
        assertionConsumerServiceURL = prepareForAssignment(assertionConsumerServiceURL,
                newAssertionConsumerServiceURL);
    }

    /** {@inheritDoc} */
    @Nullable public Integer getAttributeConsumingServiceIndex() {
        return attributeConsumingServiceIndex;
    }

    /** {@inheritDoc} */
    public void setAttributeConsumingServiceIndex(@Nullable final Integer newAttributeConsumingServiceIndex) {
        attributeConsumingServiceIndex = prepareForAssignment(attributeConsumingServiceIndex,
                newAttributeConsumingServiceIndex);
    }

    /** {@inheritDoc} */
    @Nullable public String getProviderName() {
        return providerName;
    }

    /** {@inheritDoc} */
    public void setProviderName(@Nullable final String newProviderName) {
        providerName = prepareForAssignment(providerName, newProviderName);
    }

    /** {@inheritDoc} */
    @Nullable public Subject getSubject() {
        return subject;
    }

    /** {@inheritDoc} */
    public void setSubject(@Nullable final Subject newSubject) {
        subject = prepareForAssignment(subject, newSubject);
    }

    /** {@inheritDoc} */
    @Nullable public NameIDPolicy getNameIDPolicy() {
        return nameIDPolicy;
    }

    /** {@inheritDoc} */
    public void setNameIDPolicy(@Nullable final NameIDPolicy newNameIDPolicy) {
        nameIDPolicy = prepareForAssignment(nameIDPolicy, newNameIDPolicy);
    }

    /** {@inheritDoc} */
    @Nullable  public Conditions getConditions() {
        return conditions;
    }

    /** {@inheritDoc} */
    public void setConditions(@Nullable final Conditions newConditions) {
        conditions = prepareForAssignment(conditions, newConditions);
    }

    /** {@inheritDoc} */
    @Nullable public RequestedAuthnContext getRequestedAuthnContext() {
        return requestedAuthnContext;
    }

    /** {@inheritDoc} */
    public void setRequestedAuthnContext(@Nullable final RequestedAuthnContext newRequestedAuthnContext) {
        requestedAuthnContext = prepareForAssignment(requestedAuthnContext, newRequestedAuthnContext);
    }

    /** {@inheritDoc} */
    @Nullable public Scoping getScoping() {
        return scoping;
    }

    /** {@inheritDoc} */
    public void setScoping(@Nullable final Scoping newScoping) {
        scoping = prepareForAssignment(scoping, newScoping);
    }

    /** {@inheritDoc} */
    @Override
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        final List<XMLObject> superKids = super.getOrderedChildren();
        if (superKids != null) {
            children.addAll(superKids);
        }

        if (subject != null) {
            children.add(subject);
        }

        if (nameIDPolicy != null) {
            children.add(nameIDPolicy);
        }

        if (conditions != null) {
            children.add(conditions);
        }

        if (requestedAuthnContext != null) {
            children.add(requestedAuthnContext);
        }

        if (scoping != null) {
            children.add(scoping);
        }

        return CollectionSupport.copyToList(children);
    }

}