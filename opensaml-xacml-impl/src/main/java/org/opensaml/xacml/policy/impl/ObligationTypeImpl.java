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
import java.util.Collections;
import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.xacml.impl.AbstractXACMLObject;
import org.opensaml.xacml.policy.AttributeAssignmentType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.ObligationType;

/** Implementation for {@link ObligationType}. */
public class ObligationTypeImpl extends AbstractXACMLObject implements ObligationType {

    /** List of the atrributeAssignments in the obligation. */
    private XMLObjectChildrenList<AttributeAssignmentType> attributeAssignments;

    /** The attribute fulfillOn. */
    private EffectType fulFillOn;

    /** Obligation Id. */
    private String obligationId;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected ObligationTypeImpl(final String namespaceURI, final String elementLocalName,
            final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        attributeAssignments = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    public List<AttributeAssignmentType> getAttributeAssignments() {
        return attributeAssignments;
    }

    /** {@inheritDoc} */
    public EffectType getFulfillOn() {
        return fulFillOn;
    }

    /** {@inheritDoc} */
    public String getObligationId() {
        return obligationId;
    }

    /** {@inheritDoc} */
    public void setFulfillOn(final EffectType newFulfillOn) {
        fulFillOn = prepareForAssignment(this.fulFillOn, newFulfillOn);
    }

    /** {@inheritDoc} */
    public void setObligationId(final String newObligationId) {
        obligationId = prepareForAssignment(this.obligationId, newObligationId);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        if (!attributeAssignments.isEmpty()) {
            children.addAll(attributeAssignments);
        }
        return Collections.unmodifiableList(children);
    }
}
