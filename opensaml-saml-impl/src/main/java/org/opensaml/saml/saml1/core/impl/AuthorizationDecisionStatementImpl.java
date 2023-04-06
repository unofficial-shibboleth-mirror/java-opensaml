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
import org.opensaml.saml.saml1.core.Action;
import org.opensaml.saml.saml1.core.AuthorizationDecisionStatement;
import org.opensaml.saml.saml1.core.DecisionTypeEnumeration;
import org.opensaml.saml.saml1.core.Evidence;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * A concrete implementation of {@link org.opensaml.saml.saml1.core.AuthorizationDecisionStatement}.
 */
public class AuthorizationDecisionStatementImpl extends SubjectStatementImpl implements AuthorizationDecisionStatement {

    /** Contains the Resource attribute. */
    @Nullable private String resource;

    /** Contains the Decision attribute. */
    @Nullable private DecisionTypeEnumeration decision;

    /** Contains the list of Action elements. */
    @Nonnull private final XMLObjectChildrenList<Action> actions;

    /** Contains the (single) Evidence element. */
    @Nullable private Evidence evidence;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AuthorizationDecisionStatementImpl(@Nullable final String namespaceURI,
            @Nonnull final String elementLocalName, @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        actions = new XMLObjectChildrenList<>(this);
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
    @Nullable public DecisionTypeEnumeration getDecision() {
        return decision;
    }

    /** {@inheritDoc} */
    public void setDecision(@Nullable final DecisionTypeEnumeration dec) {
        decision = prepareForAssignment(decision, dec);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<Action> getActions() {
        return actions;
    }

    /** {@inheritDoc} */
    @Nullable public Evidence getEvidence() {
        return evidence;
    }

    /** {@inheritDoc} */
    public void setEvidence(@Nullable final Evidence ev) {
        evidence = prepareForAssignment(evidence, ev);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final List<XMLObject> list = new ArrayList<>(actions.size() + 2);

        final List<XMLObject> superKids = super.getOrderedChildren();
        if (superKids != null) {
            list.addAll(superKids);
        }
        
        list.addAll(actions);
        
        if (evidence != null) {
            list.add(evidence);
        }

        return CollectionSupport.copyToList(list);
    }
    
}