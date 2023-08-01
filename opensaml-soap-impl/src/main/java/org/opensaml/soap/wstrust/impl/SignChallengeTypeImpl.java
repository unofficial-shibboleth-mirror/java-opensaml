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
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.soap.wstrust.Challenge;
import org.opensaml.soap.wstrust.SignChallengeType;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * SignChallengeTypeImpl.
 * 
 */
public class SignChallengeTypeImpl extends AbstractWSTrustObject implements SignChallengeType {
    
    /** Wilcard child elements. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> unknownChildren;
    
    /** Wildcard attributes. */
    @Nonnull private final AttributeMap unknownAttributes;

    /** {@link Challenge} child element. */
    @Nullable private Challenge challenge;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public SignChallengeTypeImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownChildren = new IndexedXMLObjectChildrenList<>(this);
        unknownAttributes = new AttributeMap(this);
    }

    /** {@inheritDoc} */
    @Nullable public Challenge getChallenge() {
        return challenge;
    }

    /** {@inheritDoc} */
    public void setChallenge(@Nullable final Challenge newChallenge) {
        challenge = prepareForAssignment(challenge, newChallenge);
    }

    /** {@inheritDoc} */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

    /** {@inheritDoc} */
    @Nonnull public List<XMLObject> getUnknownXMLObjects() {
        return unknownChildren;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nonnull public List<XMLObject> getUnknownXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) unknownChildren.subList(typeOrName);
    }
    
    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        final List<XMLObject> children = new ArrayList<>();
        if (challenge != null) {
            children.add(challenge);
        }
        children.addAll(unknownChildren);
        return CollectionSupport.copyToList(children);
    }

}
