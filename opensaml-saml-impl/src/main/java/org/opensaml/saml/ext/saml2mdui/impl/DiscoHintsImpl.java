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

package org.opensaml.saml.ext.saml2mdui.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.saml.ext.saml2mdui.DiscoHints;
import org.opensaml.saml.ext.saml2mdui.DomainHint;
import org.opensaml.saml.ext.saml2mdui.GeolocationHint;
import org.opensaml.saml.ext.saml2mdui.IPHint;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/** Concrete implementation of {@link DiscoHints}. */
@SuppressWarnings("unchecked")
public class DiscoHintsImpl extends AbstractXMLObject implements DiscoHints {
    
    /** Children of the UIInfo. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> discoHintsChildren;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespaceURI
     * @param elementLocalName elementLocalName
     * @param namespacePrefix namespacePrefix
     */
    protected DiscoHintsImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        discoHintsChildren = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<XMLObject> getXMLObjects() {
        return discoHintsChildren;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<XMLObject> getXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) discoHintsChildren.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<DomainHint> getDomainHints() {
        return (List<DomainHint>) discoHintsChildren.subList(DomainHint.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<GeolocationHint> getGeolocationHints() {
        return (List<GeolocationHint>) discoHintsChildren.subList(GeolocationHint.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<IPHint> getIPHints() {
        return (List<IPHint>) discoHintsChildren.subList(IPHint.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(discoHintsChildren);
    }

}