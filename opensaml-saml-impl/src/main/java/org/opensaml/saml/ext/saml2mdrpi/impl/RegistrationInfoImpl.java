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

package org.opensaml.saml.ext.saml2mdrpi.impl;

import java.time.Instant;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.ext.saml2mdrpi.RegistrationInfo;
import org.opensaml.saml.ext.saml2mdrpi.RegistrationPolicy;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete Implementation of {@link RegistrationInfo}.
 */
public class RegistrationInfoImpl extends AbstractXMLObject implements RegistrationInfo {

    /** The policies. */
    @Nonnull private final XMLObjectChildrenList<RegistrationPolicy> registrationPolicies;

    /** The authority. */
    @Nullable private String registrationAuthority;

    /** The registration instant. */
    @Nullable private Instant registrationInstant;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected RegistrationInfoImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        registrationPolicies = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public String getRegistrationAuthority() {
        return registrationAuthority;
    }

    /** {@inheritDoc} */
    public void setRegistrationAuthority(@Nullable final String authority) {
        registrationAuthority = prepareForAssignment(registrationAuthority, authority);
    }

    /** {@inheritDoc} */
    @Nullable public Instant getRegistrationInstant() {
        return registrationInstant;
    }

    /** {@inheritDoc} */
    public void setRegistrationInstant(@Nullable final Instant dateTime) {
        registrationInstant = prepareForAssignment(registrationInstant, dateTime);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<RegistrationPolicy> getRegistrationPolicies() {
        return registrationPolicies;
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(registrationPolicies);
    }
    
}