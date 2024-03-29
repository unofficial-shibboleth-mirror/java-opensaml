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

package org.opensaml.saml.ext.saml2alg.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.saml.ext.saml2alg.DigestMethod;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Implementation of {@link DigestMethod}.
 */
public class DigestMethodImpl extends AbstractXMLObject implements DigestMethod {
    
    /** Wildcard child elements. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> unknownChildren;
    
    /** Algorithm attribute value. */
    @Nullable private String algorithm;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace URI
     * @param elementLocalName the element local name
     * @param namespacePrefix the namespace prefix
     */
    public DigestMethodImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownChildren = new IndexedXMLObjectChildrenList<>(this);
    }
    
    /** {@inheritDoc} */
    @Nullable public String getAlgorithm() {
        return algorithm;
    }

    /** {@inheritDoc} */
    public void setAlgorithm(@Nullable final String newValue) {
        algorithm = prepareForAssignment(algorithm, newValue);
    }
    
    /**
     * {@inheritDoc}
     */
    @Nonnull @Live public List<XMLObject> getUnknownXMLObjects() {
        return unknownChildren;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nonnull @Live public List<XMLObject> getUnknownXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) unknownChildren.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(unknownChildren);
    }

}