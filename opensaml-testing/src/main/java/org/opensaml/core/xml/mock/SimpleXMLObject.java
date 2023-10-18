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

/**
 * 
 */
package org.opensaml.core.xml.mock;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.ElementExtensibleXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.core.xml.util.XMLObjectChildrenList;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Simple XMLObject that can be used for testing.
 */
public class SimpleXMLObject extends AbstractXMLObject
        implements ElementExtensibleXMLObject, AttributeExtensibleXMLObject {
    
    /** Default namespace. */
    @Nonnull @NotEmpty public static final String NAMESPACE = "http://www.example.org/testObjects";
    
    /** Default namespace prefix. */
    @Nonnull @NotEmpty public static final String NAMESPACE_PREFIX = "test";
    
    /** Element local name. */
    @Nonnull @NotEmpty public static final String LOCAL_NAME = "SimpleElement";
    
    /** Default element name. */
    @Nonnull public static final QName ELEMENT_NAME = new QName(NAMESPACE, LOCAL_NAME, NAMESPACE_PREFIX);
    
    /** Name attribute name. */
    @Nonnull @NotEmpty public static final String ID_ATTRIB_NAME = "Id";
    
    /** Name attribute. */
    @Nullable private String id;
    
    /** Value of the object stored as text content in the element. */
    @Nullable private String value;
    
    /** Child SimpleXMLObjects. */
    @Nonnull private XMLObjectChildrenList<SimpleXMLObject> simpleXMLObjects;
    
    /** Other children. */
    @Nonnull private IndexedXMLObjectChildrenList<XMLObject> unknownXMLObjects;
    
    /** anyAttribute wildcard attributes. */
    @Nonnull private AttributeMap unknownAttributes;
    
    /**
     * Constructor.
     * 
     * @param namspaceURI namespace for the node
     * @param localName local name for the node
     * @param namespacePrefix namespace prefix for the node 
     */
    public SimpleXMLObject(@Nullable final String namspaceURI, @Nonnull final String localName,
            @Nullable final String namespacePrefix) {
        super(namspaceURI, localName, namespacePrefix);
        
        simpleXMLObjects = new XMLObjectChildrenList<>(this);
        unknownXMLObjects = new IndexedXMLObjectChildrenList<>(this);
        unknownAttributes = new AttributeMap(this);
    }
    
    /**
     * Gets the name attribute.
     * 
     * @return the name attribute
     */
    @Nullable public String getId() {
        return id;
    }
    
    /**
     * Sets the name attribute.
     * 
     * @param newId the name attribute
     */
    public void setId(@Nullable final String newId) {
        registerOwnID(id, newId);
        id = newId;
    }
    
    /**
     * Gets the value of this object.
     * 
     * @return the value of this object
     */
    @Nullable public String getValue() {
        return value;
    }
    
    /**
     * Sets the value of this object.
     * 
     * @param newValue the value of this object
     */
    public void setValue(@Nullable final String newValue) {
        value = prepareForAssignment(value, newValue);
    }
    
    /**
     * Gets the list of child SimpleXMLObjects.
     * 
     * @return the list of child SimpleXMLObjects
     */
    @Nonnull public List<SimpleXMLObject> getSimpleXMLObjects(){
        return simpleXMLObjects;
    }
    
    /** {@inheritDoc} */
    @Nonnull public List<XMLObject> getUnknownXMLObjects() {
        return unknownXMLObjects;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nonnull public List<XMLObject> getUnknownXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) unknownXMLObjects.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        final List<XMLObject> children = new LinkedList<>();
        
        children.addAll(simpleXMLObjects);
        children.addAll(unknownXMLObjects);
        
        return CollectionSupport.copyToList(children);
    }

    /** {@inheritDoc} */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }
}