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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.soap.wspolicy.AppliesTo;
import org.opensaml.soap.wspolicy.Policy;
import org.opensaml.soap.wspolicy.PolicyAttachment;
import org.opensaml.soap.wspolicy.PolicyReference;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * PolicyAttachmentImpl.
 */
@SuppressWarnings("unchecked")
public class PolicyAttachmentImpl extends AbstractWSPolicyObject implements PolicyAttachment {
    
    /** AppliesTo Child element. */
    @Nullable private AppliesTo appliesTo;
    
    /** Policy and PolicyReference children. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> policiesAndReferences;
    
    /** Wildcard child elements. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> unknownChildren;
    
    /** Wildcard attributes. */
    @Nonnull private final AttributeMap unknownAttributes;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public PolicyAttachmentImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        policiesAndReferences = new IndexedXMLObjectChildrenList<>(this);
        unknownChildren = new IndexedXMLObjectChildrenList<>(this);
        unknownAttributes = new AttributeMap(this);
    }

    /** {@inheritDoc} */
    @Nullable public AppliesTo getAppliesTo() {
        return appliesTo;
    }

    /** {@inheritDoc} */
    public void setAppliesTo(@Nullable final AppliesTo newAppliesTo) {
        appliesTo = prepareForAssignment(appliesTo, newAppliesTo);
    }

    /** {@inheritDoc} */
    @Nonnull public List<Policy> getPolicies() {
        return (List<Policy>) policiesAndReferences.subList(Policy.ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull public List<PolicyReference> getPolicyReferences() {
        return (List<PolicyReference>) policiesAndReferences.subList(PolicyReference.ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    @Nonnull public List<XMLObject> getUnknownXMLObjects() {
        return unknownChildren;
    }

    /** {@inheritDoc} */
    @Nonnull public List<XMLObject> getUnknownXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) unknownChildren.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        if (appliesTo != null) {
            children.add(appliesTo);
        }
        children.addAll(policiesAndReferences);
        children.addAll(unknownChildren);
        
        return CollectionSupport.copyToList(children);
    }

}
