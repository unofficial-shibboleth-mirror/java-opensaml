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

package org.opensaml.xacml.policy.impl;

import java.util.ArrayList;
import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.impl.AbstractXACMLObject;
import org.opensaml.xacml.policy.ActionMatchType;
import org.opensaml.xacml.policy.AttributeDesignatorType;
import org.opensaml.xacml.policy.AttributeSelectorType;
import org.opensaml.xacml.policy.AttributeValueType;

/** Concrete implementation of {@link ActionMatchType}. */
public class ActionMatchTypeImpl extends AbstractXACMLObject implements ActionMatchType {

    /** Match's attribute value. */
    private AttributeValueType attributeValue;

    /** Match's choice of attribute elements. */
    private IndexedXMLObjectChildrenList<XACMLObject> attributeChoice;

    /** Gets the ID of this match. */
    private String matchId;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    public ActionMatchTypeImpl(final String namespaceURI, final String elementLocalName, final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        attributeChoice = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    public AttributeSelectorType getAttributeSelector() {
        final List<XACMLObject> selectors = (List<XACMLObject>) attributeChoice
                .subList(AttributeSelectorType.DEFAULT_ELEMENT_NAME);
        if (selectors != null && !selectors.isEmpty()) {
            return (AttributeSelectorType) selectors.get(0);
        }

        return null;
    }

    /** {@inheritDoc} */
    public AttributeValueType getAttributeValue() {
        return attributeValue;
    }

    /** {@inheritDoc} */
    public AttributeDesignatorType getActionAttributeDesignator() {
        final List<XACMLObject> selectors = (List<XACMLObject>) attributeChoice
                .subList(AttributeDesignatorType.ACTION_ATTRIBUTE_DESIGNATOR_ELEMENT_NAME);
        if (selectors != null && !selectors.isEmpty()) {
            return (AttributeDesignatorType) selectors.get(0);
        }

        return null;
    }

    /** {@inheritDoc} */
    public String getMatchId() {
        return matchId;
    }

    /** {@inheritDoc} */
    public void setAttributeSelector(final AttributeSelectorType selector) {
        final AttributeSelectorType currentSelector = getAttributeSelector();
        if (currentSelector != null) {
            attributeChoice.remove(currentSelector);
        }

        attributeChoice.add(selector);
    }

    /** {@inheritDoc} */
    public void setAttributeValue(final AttributeValueType value) {
        attributeValue = prepareForAssignment(attributeValue, value);
    }

    /** {@inheritDoc} */
    public void setActionAttributeDesignator(final AttributeDesignatorType attribute) {
        final AttributeDesignatorType currentDesignator = getActionAttributeDesignator();
        if (currentDesignator != null) {
            attributeChoice.remove(currentDesignator);
        }

        attributeChoice.add(attribute);
    }

    /** {@inheritDoc} */
    public void setMatchId(final String id) {
        matchId = prepareForAssignment(matchId, id);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        children.add(attributeValue);
        if (!attributeChoice.isEmpty()) {
            children.addAll(attributeChoice);
        }

        return children;
    }
}