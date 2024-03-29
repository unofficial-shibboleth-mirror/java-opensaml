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

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.saml2.core.Action;
import org.opensaml.saml.saml2.core.AuthzDecisionQuery;
import org.opensaml.saml.saml2.core.Evidence;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link AuthzDecisionQuery}.
 */
public class AuthzDecisionQueryImpl extends SubjectQueryImpl implements AuthzDecisionQuery {

    /** Resource attribute value. */
    @Nullable private String resource;

    /** Evidence child element. */
    @Nullable private Evidence evidence;

    /** Action child elements. */
    @Nonnull private final XMLObjectChildrenList<Action> actions;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AuthzDecisionQueryImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        actions = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public String getResource() {
        return this.resource;
    }

    /** {@inheritDoc} */
    public void setResource(@Nullable final String newResource) {
        this.resource = prepareForAssignment(this.resource, newResource);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<Action> getActions() {
        return actions;
    }

    /** {@inheritDoc} */
    @Nullable public Evidence getEvidence() {
        return this.evidence;
    }

    /** {@inheritDoc} */
    public void setEvidence(@Nullable final Evidence newEvidence) {
        this.evidence = prepareForAssignment(this.evidence, newEvidence);
    }

    /** {@inheritDoc} */
    @Override
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        final List<XMLObject> superKids = super.getOrderedChildren();
        if (superKids != null) {
            children.addAll(superKids);
        }

        children.addAll(actions);
        
        if (evidence != null) {
            children.add(evidence);
        }

        return CollectionSupport.copyToList(children);
    }
    
}