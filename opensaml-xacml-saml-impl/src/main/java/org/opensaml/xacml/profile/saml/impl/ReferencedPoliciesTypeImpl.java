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
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.xacml.policy.PolicySetType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.profile.saml.ReferencedPoliciesType;

/**
 * Implementation of {@link ReferencedPoliciesType}.
 */
public class ReferencedPoliciesTypeImpl extends AbstractXMLObject implements ReferencedPoliciesType {

    /**List of policies.*/
    private XMLObjectChildrenList<PolicyType> policies;
    
    /**List of policieSets.*/
    private XMLObjectChildrenList<PolicySetType> policieSets;
    
    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected ReferencedPoliciesTypeImpl(final String namespaceURI, final String elementLocalName,
            final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        policies = new XMLObjectChildrenList<>(this);
        policieSets = new XMLObjectChildrenList<>(this); 
    }
    
    /** {@inheritDoc} */
    public List<PolicySetType> getPolicySets() {
        return policieSets;
    }

    /** {@inheritDoc} */
    public List<PolicyType> getPolicies() {
       return policies;
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        
        if(!policies.isEmpty()) {
            children.addAll(policies);
        }
        if(!policieSets.isEmpty()) {
            children.addAll(policieSets);
        }
        return Collections.unmodifiableList(children);
    }
}
