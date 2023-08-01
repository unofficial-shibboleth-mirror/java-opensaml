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

package org.opensaml.saml.saml1.core.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.Response;
import org.opensaml.saml.saml1.core.Status;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Implementation of the {@link Response} Object.
 */
public class ResponseImpl extends ResponseAbstractTypeImpl implements Response {

    /** Status associated with this element. */
    @Nullable private Status status;

    /** List of all the Assertions. */
    @Nonnull private final XMLObjectChildrenList<Assertion> assertions;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected ResponseImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        assertions = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<Assertion> getAssertions() {
        return assertions;
    }

    /** {@inheritDoc} */
    @Nullable public Status getStatus() {
        return status;
    }

    /** {@inheritDoc} */
    public void setStatus(@Nullable final Status s) {
        status = prepareForAssignment(status, s);
    }

    /** {@inheritDoc} */
    @Override
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>(1 + assertions.size());

        final List<XMLObject> superKids = super.getOrderedChildren();
        if (superKids != null) {
            children.addAll(superKids);
        }

        if (status != null) {
            children.add(status);
        }

        children.addAll(assertions);

        return CollectionSupport.copyToList(children);
    }

}