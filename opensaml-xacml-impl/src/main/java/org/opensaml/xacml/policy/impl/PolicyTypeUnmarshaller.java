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

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xacml.impl.AbstractXACMLObjectUnmarshaller;
import org.opensaml.xacml.policy.CombinerParametersType;
import org.opensaml.xacml.policy.DefaultsType;
import org.opensaml.xacml.policy.DescriptionType;
import org.opensaml.xacml.policy.ObligationsType;
import org.opensaml.xacml.policy.PolicyType;
import org.opensaml.xacml.policy.RuleCombinerParametersType;
import org.opensaml.xacml.policy.RuleType;
import org.opensaml.xacml.policy.TargetType;
import org.opensaml.xacml.policy.VariableDefinitionType;
import org.w3c.dom.Attr;

/** Unmarshaller for {@link PolicyType} objects. */
public class PolicyTypeUnmarshaller extends AbstractXACMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(final XMLObject xmlObject, final Attr attribute) throws UnmarshallingException {
        final PolicyType policy = (PolicyType) xmlObject;

        if (attribute.getLocalName().equals(PolicyType.POLICY_ID_ATTRIB_NAME)) {
            policy.setPolicyId(attribute.getValue());
        } else if (attribute.getLocalName().equals(PolicyType.VERSION_ATTRIB_NAME)) {
            policy.setVersion(attribute.getValue());
        } else if (attribute.getLocalName().equals(PolicyType.RULE_COMBINING_ALG_ID_ATTRIB_NAME)) {
            policy.setRuleCombiningAlgoId(attribute.getValue());
        } else {
            super.processAttribute(xmlObject, attribute);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(final XMLObject parentXMLObject, final XMLObject childXMLObject)
            throws UnmarshallingException {
        final PolicyType policy = (PolicyType) parentXMLObject;

        if (childXMLObject instanceof DescriptionType) {
            policy.setDescription((DescriptionType) childXMLObject);
        } else if (childXMLObject.getElementQName().equals(DefaultsType.POLICY_DEFAULTS_ELEMENT_NAME)) {
            policy.setPolicyDefaults((DefaultsType) childXMLObject);
        } else if (childXMLObject instanceof TargetType) {
            policy.setTarget((TargetType) childXMLObject);
        } else if (childXMLObject instanceof CombinerParametersType) {
            policy.getCombinerParameters().add((CombinerParametersType) childXMLObject);
        } else if (childXMLObject instanceof RuleCombinerParametersType) {
            policy.getRuleCombinerParameters().add((RuleCombinerParametersType) childXMLObject);
        } else if (childXMLObject instanceof VariableDefinitionType) {
            policy.getVariableDefinitions().add((VariableDefinitionType) childXMLObject);
        } else if (childXMLObject instanceof RuleType) {
            policy.getRules().add((RuleType)childXMLObject);
        } else if (childXMLObject instanceof ObligationsType) {
            policy.setObligations((ObligationsType) childXMLObject);
        } else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}