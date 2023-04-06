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

package org.opensaml.saml.saml1.core.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.saml1.core.AuthenticationStatement;
import org.opensaml.saml.saml1.core.AuthorityBinding;
import org.opensaml.saml.saml1.core.SubjectLocality;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * A Concrete implementation of the {@link AuthenticationStatement} Interface.
 */
public class AuthenticationStatementImpl extends SubjectStatementImpl implements AuthenticationStatement {

    /** Contains the AuthenticationMethod attribute contents. */
    @Nullable private String authenticationMethod;

    /** Contains the AuthenticationMethod attribute contents. */
    @Nullable private Instant authenticationInstant;

    /** Contains the SubjectLocality subelement. */
    @Nullable private SubjectLocality subjectLocality;

    /** Contains the AuthorityBinding subelements. */
    @Nonnull private final XMLObjectChildrenList<AuthorityBinding> authorityBindings;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AuthenticationStatementImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        authorityBindings = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    /** {@inheritDoc} */
    public void setAuthenticationMethod(@Nullable final String method) {
        authenticationMethod = prepareForAssignment(authenticationMethod, method);
    }

    /** {@inheritDoc} */
    @Nullable public Instant getAuthenticationInstant() {
        return authenticationInstant;
    }

    /** {@inheritDoc} */
    public void setAuthenticationInstant(@Nullable final Instant instant) {
        authenticationInstant = prepareForAssignment(authenticationInstant, instant);
    }

    /** {@inheritDoc} */
    @Nullable public SubjectLocality getSubjectLocality() {
        return subjectLocality;
    }

    /** {@inheritDoc} */
    public void setSubjectLocality(@Nullable final SubjectLocality locality) {
        subjectLocality = prepareForAssignment(subjectLocality, locality);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AuthorityBinding> getAuthorityBindings() {
        return authorityBindings;
    }

    /** {@inheritDoc} */
    @Override
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final List<XMLObject> list = new ArrayList<>(authorityBindings.size() + 2);

        final List<XMLObject> superKids = super.getOrderedChildren();
        if (superKids != null) {
            list.addAll(superKids);
        }

        if (subjectLocality != null) {
            list.add(subjectLocality);
        }

        list.addAll(authorityBindings);

        return CollectionSupport.copyToList(list);
    }
    
}