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

package org.opensaml.soap.wstrust.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.soap.wstrust.UseKey;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * UseKeyImpl.
 * 
 */
public class UseKeyImpl extends AbstractWSTrustObject implements UseKey {
    
    /** Wildcard child element. */
    @Nullable private XMLObject unknownChild;

    /** wst:UseKey/@Sig attribute value. */
    @Nullable private String sig;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public UseKeyImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public String getSig() {
        return sig;
    }

    /** {@inheritDoc} */
    public void setSig(@Nullable final String newSig) {
        sig = prepareForAssignment(sig, newSig);
    }
    
    /** {@inheritDoc} */
    @Nullable public XMLObject getUnknownXMLObject() {
        return unknownChild;
    }

    /** {@inheritDoc} */
    public void setUnknownXMLObject(@Nullable final XMLObject unknownObject) {
        unknownChild = prepareForAssignment(unknownChild, unknownObject);
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        if (unknownChild != null) {
            return CollectionSupport.singletonList(unknownChild);
        }
        
        return null;
    }

}
