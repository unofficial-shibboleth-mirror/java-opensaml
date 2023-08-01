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
import org.opensaml.saml.saml2.core.IDPList;
import org.opensaml.saml.saml2.core.RequesterID;
import org.opensaml.saml.saml2.core.Scoping;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link Scoping}.
 */
public class ScopingImpl extends AbstractXMLObject implements Scoping {

    /** IDPList child element. */
    @Nullable private IDPList idpList;

    /** List of RequesterID child elements. */
    @Nonnull private final XMLObjectChildrenList<RequesterID> requesterIDs;

    /** ProxyCount attribute. */
    @Nullable private Integer proxyCount;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected ScopingImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        requesterIDs = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public Integer getProxyCount() {
        return this.proxyCount;
    }

    /** {@inheritDoc} */
    public void setProxyCount(@Nullable final Integer newProxyCount) {
        this.proxyCount = prepareForAssignment(this.proxyCount, newProxyCount);
    }

    /** {@inheritDoc} */
    @Nullable public IDPList getIDPList() {
        return idpList;
    }

    /** {@inheritDoc} */
    public void setIDPList(@Nullable final IDPList newIDPList) {
        this.idpList = prepareForAssignment(this.idpList, newIDPList);

    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<RequesterID> getRequesterIDs() {
        return requesterIDs;
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        if (idpList != null) {
            children.add(idpList);
        }

        children.addAll(requesterIDs);

        return CollectionSupport.copyToList(children);
    }
    
}