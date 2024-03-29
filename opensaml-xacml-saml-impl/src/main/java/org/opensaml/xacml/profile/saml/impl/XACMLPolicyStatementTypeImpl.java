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

package org.opensaml.xacml.profile.saml.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.xacml.XACMLObject;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.profile.saml.ReferencedPoliciesType;
import org.opensaml.xacml.profile.saml.XACMLPolicyStatementType;

/** Concrete implementation of {@link XACMLPolicyStatementType}. */
public class XACMLPolicyStatementTypeImpl extends AbstractXMLObject implements XACMLPolicyStatementType {

    /** Choice group in element. */
    private IndexedXMLObjectChildrenList<XACMLObject> choiceGroup;

    /** ReferencedPolicie child. */
    private ReferencedPoliciesType referencedPolicies;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected XACMLPolicyStatementTypeImpl(final String namespaceURI, final String elementLocalName,
            final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        choiceGroup = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        children.addAll(choiceGroup);

        if (referencedPolicies != null) {
            children.add(referencedPolicies);
        }

        return Collections.unmodifiableList(children);
    }

    /** {@inheritDoc} */
    public List<PolicyType> getPolicies() {
        return (List<PolicyType>) choiceGroup.subList(PolicyType.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    public List<PolicySetType> getPolicySets() {
        return (List<PolicySetType>) choiceGroup.subList(PolicySetType.DEFAULT_ELEMENT_NAME);
    }

    /** {@inheritDoc} */
    public ReferencedPoliciesType getReferencedPolicies() {
        return referencedPolicies;
    }

    /** {@inheritDoc} */
    public void setReferencedPolicies(final ReferencedPoliciesType policies) {
        referencedPolicies = prepareForAssignment(referencedPolicies, policies);
    }
}
