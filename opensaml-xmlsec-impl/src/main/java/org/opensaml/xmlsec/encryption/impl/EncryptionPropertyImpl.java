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

package org.opensaml.xmlsec.encryption.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.xmlsec.encryption.EncryptionProperty;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link EncryptionProperty}.
 */
public class EncryptionPropertyImpl extends AbstractXMLObject implements
        EncryptionProperty {
    
    /** Target attribute value. */
    @Nullable private String target;
    
    /** Id attribute value. */
    @Nullable private String id;
    
    /** Child elements from the &lt;any&gt; content model. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> unknownChildren;
    
    /** "anyAttribute" attributes. */
    @Nonnull private final AttributeMap unknownAttributes;

    /**
     * Constructor.
     *
     * @param namespaceURI namespace URI
     * @param elementLocalName local name
     * @param namespacePrefix namespace prefix
     */
    protected EncryptionPropertyImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownChildren = new IndexedXMLObjectChildrenList<>(this);
        unknownAttributes =  new AttributeMap(this);
    }

    /** {@inheritDoc} */
    @Nullable public String getTarget() {
        return target;
    }

    /** {@inheritDoc} */
    public void setTarget(@Nullable final String newTarget) {
        target = prepareForAssignment(target, newTarget);
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
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<XMLObject> getUnknownXMLObjects() {
        return unknownChildren;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nonnull @Live public List<XMLObject> getUnknownXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) unknownChildren.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(unknownChildren);
    }

}