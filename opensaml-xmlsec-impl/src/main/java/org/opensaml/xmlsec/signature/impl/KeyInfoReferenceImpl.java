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
import org.opensaml.xmlsec.signature.KeyInfoReference;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * Concrete implementation of {@link KeyInfoReference}.
 */
public class KeyInfoReferenceImpl extends AbstractXMLObject implements KeyInfoReference {
    
    /** Id attribute value. */
    @Nullable private String id;
    
    /** URI attribute value. */
    @Nullable private String uri;
    
    /**
     * Constructor.
     *
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected KeyInfoReferenceImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public String getID() {
        return id;
    }

    /** {@inheritDoc} */
    public void setID(@Nullable final String newID) {
        final String oldID = id;
        id = prepareForAssignment(id, newID);
        registerOwnID(oldID, id);
    }
    
    /** {@inheritDoc} */
    @Nullable public String getURI() {
        return uri;
    }

    /** {@inheritDoc} */
    public void setURI(@Nullable final String newURI) {
        uri = prepareForAssignment(uri, newURI);
    }
    
    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        // no children
        return null;
    }
    
}