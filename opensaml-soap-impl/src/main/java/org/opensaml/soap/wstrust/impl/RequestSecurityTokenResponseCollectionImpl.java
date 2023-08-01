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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.soap.wstrust.RequestSecurityTokenResponse;
import org.opensaml.soap.wstrust.RequestSecurityTokenResponseCollection;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * RequestSecurityTokenResponseCollectionImpl.
 * 
 */
public class RequestSecurityTokenResponseCollectionImpl extends AbstractWSTrustObject implements
        RequestSecurityTokenResponseCollection {

    /** Wildcard attributes. */
    @Nonnull private final AttributeMap unknownAttributes;

    /** The list of wst:RequestSecurityTokenResponse child elements. */
    @Nonnull private final List<RequestSecurityTokenResponse> requestSecurityTokenResponses;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     * 
     */
    public RequestSecurityTokenResponseCollectionImpl(@Nullable final String namespaceURI,
            @Nonnull final String elementLocalName, @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
        requestSecurityTokenResponses = new ArrayList<>();
    }
    
    /** {@inheritDoc} */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

    /** {@inheritDoc} */
    @Nonnull public List<RequestSecurityTokenResponse> getRequestSecurityTokenResponses() {
        return requestSecurityTokenResponses;
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(requestSecurityTokenResponses);
    }

}
