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
import org.opensaml.xacml.policy.ConditionType;
import org.opensaml.xacml.policy.DescriptionType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.RuleType;
import org.opensaml.xacml.policy.TargetType;
import org.w3c.dom.Attr;

import net.shibboleth.shared.primitive.StringSupport;

/**
 * Unmarshaller for {@link RuleType}.
 */
public class RuleTypeUnmarshaller extends AbstractXACMLObjectUnmarshaller {

    /** {@inheritDoc} */
    @Override
    protected void processAttribute(final XMLObject xmlObject, final Attr attribute) throws UnmarshallingException {
        final RuleType ruleType = (RuleType) xmlObject;
      
        if(attribute.getLocalName().equals(RuleType.EFFECT_ATTRIB_NAME)){
            ruleType.setEffect(EffectType.valueOf(
                    StringSupport.trimOrNull(attribute.getValue())));                       
        } else if(attribute.getLocalName().equals(RuleType.RULE_ID_ATTRIB_NAME)){
            ruleType.setRuleId(StringSupport.trimOrNull(attribute.getValue()));
        } else {
            super.processAttribute(xmlObject, attribute);
        }

    }

    /** {@inheritDoc} */
    @Override
    protected void processChildElement(final XMLObject parentXMLObject, final XMLObject childXMLObject)
            throws UnmarshallingException {
        final RuleType ruleType = (RuleType) parentXMLObject;
        
        if(childXMLObject instanceof TargetType){
            ruleType.setTarget((TargetType)childXMLObject);
        } else if(childXMLObject instanceof DescriptionType){
            ruleType.setDescription((DescriptionType)childXMLObject);
        }else if(childXMLObject instanceof ConditionType){
            ruleType.setCondition((ConditionType)childXMLObject);
        } else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}