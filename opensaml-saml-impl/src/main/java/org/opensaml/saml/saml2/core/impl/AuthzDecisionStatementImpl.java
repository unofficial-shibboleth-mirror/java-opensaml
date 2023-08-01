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

package org.opensaml.saml.saml2.core.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.saml2.core.Action;
import org.opensaml.saml.saml2.core.AuthzDecisionStatement;
import org.opensaml.saml.saml2.core.DecisionTypeEnumeration;
import org.opensaml.saml.saml2.core.Evidence;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * A concrete implementation of {@link AuthzDecisionStatement}.
 */
public class AuthzDecisionStatementImpl extends AbstractXMLObject implements AuthzDecisionStatement {

    /** URI of the resource to which authorization is sought. */
    @Nullable private String resource;

    /** Decision of the authorization request. */
    @Nullable private DecisionTypeEnumeration decision;

    /** Actions authorized to be performed. */
    @Nonnull private final XMLObjectChildrenList<Action> actions;

    /** SAML assertion the authority relied on when making the authorization decision. */
    @Nullable private Evidence evidence;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AuthzDecisionStatementImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        actions = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public String getResource() {
        return resource;
    }

    /** {@inheritDoc} */
    public void setResource(@Nullable final String newResourceURI) {
        resource = prepareForAssignment(resource, newResourceURI, false);
    }

    /** {@inheritDoc} */
    @Nullable public DecisionTypeEnumeration getDecision() {
        return decision;
    }

    /** {@inheritDoc} */
    public void setDecision(@Nullable final DecisionTypeEnumeration newDecision) {
        decision = prepareForAssignment(decision, newDecision);
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
    public void setEvidence(@Nullable final Evidence newEvidence) {
        evidence = prepareForAssignment(evidence, newEvidence);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        children.addAll(actions);
        
        if (evidence != null) {
            children.add(evidence);
        }
        
        return CollectionSupport.copyToList(children);
    }
    
}