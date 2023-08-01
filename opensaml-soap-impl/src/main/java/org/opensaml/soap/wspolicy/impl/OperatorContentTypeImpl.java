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

package org.opensaml.soap.wspolicy.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.soap.wspolicy.All;
import org.opensaml.soap.wspolicy.ExactlyOne;
import org.opensaml.soap.wspolicy.OperatorContentType;
import org.opensaml.soap.wspolicy.Policy;
import org.opensaml.soap.wspolicy.PolicyReference;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * OperatorContentTypeImpl.
 */
@SuppressWarnings("unchecked")
public class OperatorContentTypeImpl extends AbstractWSPolicyObject implements OperatorContentType {
    
    /** All child elements. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> xmlObjects;

    /**
     * Constructor.
     *
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public OperatorContentTypeImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        xmlObjects = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nonnull public List<All> getAlls() {
        return (List<All>) xmlObjects.subList(All.ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull public List<ExactlyOne> getExactlyOnes() {
        return (List<ExactlyOne>) xmlObjects.subList(ExactlyOne.ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull public List<Policy> getPolicies() {
        return (List<Policy>) xmlObjects.subList(Policy.ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull public List<PolicyReference> getPolicyReferences() {
        return (List<PolicyReference>) xmlObjects.subList(PolicyReference.ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull public List<XMLObject> getXMLObjects() {
        return xmlObjects;
    }

    /** {@inheritDoc} */
    @Nonnull public List<XMLObject> getXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) xmlObjects.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(xmlObjects);
    }

}
