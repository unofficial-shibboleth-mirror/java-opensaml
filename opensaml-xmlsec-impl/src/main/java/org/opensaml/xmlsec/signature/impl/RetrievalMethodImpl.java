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

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.xmlsec.signature.RetrievalMethod;
import org.opensaml.xmlsec.signature.Transforms;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link RetrievalMethod}.
 */
public class RetrievalMethodImpl extends AbstractXMLObject implements RetrievalMethod {
    
    /** URI attribute value. */
    @Nullable private String uri;
    
    /** Type attribute value. */
    @Nullable private String type;
    
    /** Transforms attribute value. */
    @Nullable private Transforms transforms;

    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected RetrievalMethodImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public String getURI() {
        return uri;
    }

    /** {@inheritDoc} */
    public void setURI(@Nullable final String newURI) {
        uri = prepareForAssignment(this.uri, newURI);
    }

    /** {@inheritDoc} */
    @Nullable public String getType() {
        return type;
    }

    /** {@inheritDoc} */
    public void setType(@Nullable final String newType) {
        type = prepareForAssignment(type, newType);
    }

    /** {@inheritDoc} */
    @Nullable public Transforms getTransforms() {
        return transforms;
    }

    /** {@inheritDoc} */
    public void setTransforms(@Nullable final Transforms newTransforms) {
        transforms = prepareForAssignment(transforms, newTransforms);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        if (transforms != null) {
            return CollectionSupport.singletonList(transforms);
        }
        
        return null;
    }

}