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
import org.opensaml.soap.wssecurity.SecurityTokenReference;
import org.opensaml.soap.wstrust.RequestedReferenceType;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * RequestedReferenceTypeImpl.
 * 
 */
public class RequestedReferenceTypeImpl extends AbstractWSTrustObject implements RequestedReferenceType {
    
    /** SecurityTokenReference child element. */
    @Nullable private SecurityTokenReference securityTokenReference;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public RequestedReferenceTypeImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public SecurityTokenReference getSecurityTokenReference() {
        return securityTokenReference;
    }

    /** {@inheritDoc} */
    public void setSecurityTokenReference(@Nullable final SecurityTokenReference newSecurityTokenReference) {
        securityTokenReference = prepareForAssignment(securityTokenReference, newSecurityTokenReference);
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        if (securityTokenReference != null) {
            return CollectionSupport.singletonList(securityTokenReference);
        }
        
        return null;
    }

}
