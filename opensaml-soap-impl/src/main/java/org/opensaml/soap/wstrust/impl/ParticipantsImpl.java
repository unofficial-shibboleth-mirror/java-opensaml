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
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.soap.wstrust.Participant;
import org.opensaml.soap.wstrust.Participants;
import org.opensaml.soap.wstrust.Primary;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * ParticipantsImpl.
 * 
 */
public class ParticipantsImpl extends AbstractWSTrustObject implements Participants {

    /** The {@link Primary} child element. */
    @Nullable private Primary primary;

    /** The list of {@link Participant} child elements. */
    @Nonnull private final List<Participant> participants;
    
    /** Wildcard child elements. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> unknownChildren;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public ParticipantsImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        participants = new ArrayList<>();
        unknownChildren = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public Primary getPrimary() {
        return primary;
    }

    /** {@inheritDoc} */
    public void setPrimary(@Nullable final Primary newPrimary) {
        primary = prepareForAssignment(primary, newPrimary);
    }

    /** {@inheritDoc} */
    @Nonnull public List<Participant> getParticipants() {
        return participants;
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
        if (primary != null) {
            children.add(primary);
        }
        
        children.addAll(participants);
        
        children.addAll(unknownChildren);
        
        return CollectionSupport.copyToList(children);
    }

}
