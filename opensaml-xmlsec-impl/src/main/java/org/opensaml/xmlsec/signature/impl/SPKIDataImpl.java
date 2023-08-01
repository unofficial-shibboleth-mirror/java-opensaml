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
import org.opensaml.xmlsec.signature.SPKIData;
import org.opensaml.xmlsec.signature.SPKISexp;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link SPKIData}.
 */
@SuppressWarnings("unchecked")
public class SPKIDataImpl extends AbstractXMLObject implements SPKIData {
    
    /** The list of XMLObject child elements. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> indexedChildren;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected SPKIDataImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        indexedChildren = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<XMLObject> getXMLObjects() {
        return this.indexedChildren;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<XMLObject> getXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) this.indexedChildren.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<SPKISexp> getSPKISexps() {
        return (List<SPKISexp>) this.indexedChildren.subList(SPKISexp.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(indexedChildren);
    }

}