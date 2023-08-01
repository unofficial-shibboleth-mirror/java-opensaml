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

package org.opensaml.soap.wssecurity.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.soap.wssecurity.Created;
import org.opensaml.soap.wssecurity.Expires;
import org.opensaml.soap.wssecurity.IdBearing;
import org.opensaml.soap.wssecurity.Timestamp;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link org.opensaml.soap.wssecurity.Timestamp}.
 * 
 */
public class TimestampImpl extends AbstractWSSecurityObject implements Timestamp {

    /** wsu:Timestamp/@wsu:Id attribute. */
    @Nullable private String id;

    /** wsu:Timestamp/wsu:Created element. */
    @Nullable private Created created;

    /** wsu:Timestamp/wsu:Expires element. */
    @Nullable private Expires expires;
    
    /** Wildcard attributes. */
    @Nonnull private final AttributeMap unknownAttributes;
    
    /** Wildcard child elements. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> unknownChildren;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    public TimestampImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
        unknownChildren = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public Created getCreated() {
        return created;
    }

    /** {@inheritDoc} */
    @Nullable public Expires getExpires() {
        return expires;
    }

    /** {@inheritDoc} */
    public void setCreated(@Nullable final Created newCreated) {
        created = prepareForAssignment(created, newCreated);
    }

    /** {@inheritDoc} */
    public void setExpires(@Nullable final Expires newExpires) {
        expires = prepareForAssignment(expires, newExpires);
    }
 
    /** {@inheritDoc} */
    @Nullable public String getWSUId() {
        return id;
    }

    /** {@inheritDoc} */
    public void setWSUId(@Nullable final String newId) {
        final String oldId = id;
        id = prepareForAssignment(id, newId);
        registerOwnID(oldId, id);
        manageQualifiedAttributeNamespace(IdBearing.WSU_ID_ATTR_NAME, id != null);
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
        final ArrayList<XMLObject> children = new ArrayList<>();
        if (created != null) {
            children.add(created);
        }
        if (expires != null) {
            children.add(expires);
        }
        
        children.addAll(getUnknownXMLObjects());

        return CollectionSupport.copyToList(children);
    }

}
