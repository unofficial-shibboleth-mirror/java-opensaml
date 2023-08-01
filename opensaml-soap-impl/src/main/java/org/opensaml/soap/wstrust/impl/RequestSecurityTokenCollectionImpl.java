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
import org.opensaml.soap.wstrust.RequestSecurityToken;
import org.opensaml.soap.wstrust.RequestSecurityTokenCollection;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * RequestSecurityTokenCollectionImpl.
 * 
 */
public class RequestSecurityTokenCollectionImpl extends AbstractWSTrustObject 
        implements RequestSecurityTokenCollection {

    /** The list of wst:RequestSecurityToken child elements. */
    @Nonnull private final List<RequestSecurityToken> requestSecurityTokens;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public RequestSecurityTokenCollectionImpl(@Nullable final String namespaceURI,
            @Nonnull final String elementLocalName, @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        requestSecurityTokens = new ArrayList<>();
    }

    /** {@inheritDoc} */
    @Nonnull public List<RequestSecurityToken> getRequestSecurityTokens() {
        return requestSecurityTokens;
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(requestSecurityTokens);
    }

}
