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
import org.opensaml.saml.saml2.core.GetComplete;
import org.opensaml.saml.saml2.core.IDPEntry;
import org.opensaml.saml.saml2.core.IDPList;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link IDPList}.
 */
public class IDPListImpl extends AbstractXMLObject implements IDPList {

    /** List of IDPEntry's. */
    @Nonnull private final XMLObjectChildrenList<IDPEntry> idpEntries;

    /** GetComplete child element. */
    @Nullable private GetComplete getComplete;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected IDPListImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        idpEntries = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<IDPEntry> getIDPEntrys() {
        return idpEntries;
    }

    /** {@inheritDoc} */
    @Nullable public GetComplete getGetComplete() {
        return getComplete;
    }

    /** {@inheritDoc} */
    public void setGetComplete(@Nullable final GetComplete newGetComplete) {
        getComplete = prepareForAssignment(getComplete, newGetComplete);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        children.addAll(idpEntries);
        
        if (getComplete != null) {
            children.add(getComplete);
        }

        return CollectionSupport.copyToList(children);
    }

}