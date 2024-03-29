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

package org.opensaml.xacml.ctx.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.xacml.ctx.ResourceContentType;
import org.opensaml.xacml.impl.AbstractXACMLObject;

/** Concrete implementation of {@link ResourceContentType}. */
public class ResourceContentTypeImpl extends AbstractXACMLObject implements ResourceContentType {

    /** "any" elements. */
    private IndexedXMLObjectChildrenList<XMLObject> unknownElements;

    /** "any" attributes. */
    private AttributeMap unknownAttributes;
    
    /** String value of the mixed content element. */
    private String value;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected ResourceContentTypeImpl(final String namespaceURI, final String elementLocalName,
            final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
        unknownElements = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        children.addAll(unknownElements);
        
        return Collections.unmodifiableList(children);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getUnknownXMLObjects() {
        return unknownElements;
    }

    /** {@inheritDoc} */
    public List<XMLObject> getUnknownXMLObjects(final QName typeOrName) {
        return (List<XMLObject>) unknownElements.subList(typeOrName);
    }

    /** {@inheritDoc} */
    public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }
    
    /** {@inheritDoc} */
    public String getValue() {
        return value;
    }
    
    /** {@inheritDoc} */
    public void setValue(final String newValue) {
        value = prepareForAssignment(value, newValue);
    }
}