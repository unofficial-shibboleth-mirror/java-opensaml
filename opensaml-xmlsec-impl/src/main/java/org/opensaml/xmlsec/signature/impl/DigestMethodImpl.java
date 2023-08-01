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

package org.opensaml.xmlsec.signature.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.xmlsec.signature.DigestMethod;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link DigestMethod}.
 */
public class DigestMethodImpl extends AbstractXMLObject implements DigestMethod {
    
    /** Algorithm attribute value. */
    @Nullable private String algorithm;
    
    /** "any" children. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> unknownChildren;
    
    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected DigestMethodImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        
        unknownChildren = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public String getAlgorithm() {
        return this.algorithm;
    }

    /** {@inheritDoc} */
    public void setAlgorithm(@Nullable final String newAlgorithm) {
        this.algorithm = prepareForAssignment(this.algorithm, newAlgorithm);
    }

    /** {@inheritDoc} */
    @Nonnull @Live  public List<XMLObject> getUnknownXMLObjects() {
        return this.unknownChildren;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nonnull @Live public List<XMLObject> getUnknownXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) unknownChildren.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(unknownChildren);
    }

}