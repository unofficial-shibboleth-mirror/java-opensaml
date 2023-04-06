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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.saml1.core.AssertionArtifact;
import org.opensaml.saml.saml1.core.AssertionIDReference;
import org.opensaml.saml.saml1.core.AttributeQuery;
import org.opensaml.saml.saml1.core.AuthenticationQuery;
import org.opensaml.saml.saml1.core.AuthorizationDecisionQuery;
import org.opensaml.saml.saml1.core.Query;
import org.opensaml.saml.saml1.core.Request;
import org.opensaml.saml.saml1.core.SubjectQuery;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link Request}.
 */
public class RequestImpl extends RequestAbstractTypeImpl implements Request {

    /** Saves the query (one of Query, SubjectQuery, AuthenticationQuery, AttributeQuery, AuthorizationDecisionQuery. */
    @Nullable private Query query;

    /** The List of AssertionIDReferences. */
    @Nonnull private final XMLObjectChildrenList<AssertionIDReference> assertionIDReferences;

    /** The List of AssertionArtifacts. */
    @Nonnull private final XMLObjectChildrenList<AssertionArtifact> assertionArtifacts;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected RequestImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        assertionIDReferences = new XMLObjectChildrenList<>(this);
        assertionArtifacts = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public Query getQuery() {
        return query;
    }

    /** {@inheritDoc} */
    @Nullable public SubjectQuery getSubjectQuery() {
        return query instanceof SubjectQuery ? (SubjectQuery) query : null;
    }

    /** {@inheritDoc} */
    @Nullable public AttributeQuery getAttributeQuery() {
        return query instanceof AttributeQuery ? (AttributeQuery) query : null;
    }

    /** {@inheritDoc} */
    @Nullable public AuthenticationQuery getAuthenticationQuery() {
        return query instanceof AuthenticationQuery ? (AuthenticationQuery) query : null;
    }

    /** {@inheritDoc} */
    @Nullable public AuthorizationDecisionQuery getAuthorizationDecisionQuery() {
        return query instanceof AuthorizationDecisionQuery ? (AuthorizationDecisionQuery) query : null;
    }

    /** {@inheritDoc} */
    public void setQuery(@Nullable final Query q) {
        query = prepareForAssignment(query, q);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AssertionIDReference> getAssertionIDReferences() {
        return assertionIDReferences;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AssertionArtifact> getAssertionArtifacts() {
        return assertionArtifacts;
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {

        final List<XMLObject> list = new ArrayList<>();

        final List<XMLObject> superKids = super.getOrderedChildren();
        if (superKids != null) {
            list.addAll(superKids);
        }
        if (query != null) {
            list.add(query);
        }
        list.addAll(assertionIDReferences);
        list.addAll(assertionArtifacts);

        return CollectionSupport.copyToList(list);
    }
    
}