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
import org.opensaml.xacml.impl.AbstractXACMLObject;
import org.opensaml.xacml.policy.ConditionType;
import org.opensaml.xacml.policy.DescriptionType;
import org.opensaml.xacml.policy.EffectType;
import org.opensaml.xacml.policy.RuleType;
import org.opensaml.xacml.policy.TargetType;

/**
 *Implementation for {@link RuleType}.
 */
public class RuleTypeImpl extends AbstractXACMLObject implements RuleType {

    /** Condition of the policy.*/
    private ConditionType condition;
    
    /** The rule target.*/
    private TargetType target;
    
    /**Dscription of the rule.*/
    private DescriptionType description;
    
    /**Effect type of the rule.*/
    private EffectType effectType;
    
    /**The id of the rule.*/
    private String ruleId;
        
    /**
     * Constructor.
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected RuleTypeImpl(final String namespaceURI, final String elementLocalName, final String namespacePrefix){
        super(namespaceURI,elementLocalName,namespacePrefix);
    }
    
    /** {@inheritDoc} */
    public ConditionType getCondition() {
        return condition;
    }

    /** {@inheritDoc} */
    public DescriptionType getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    public EffectType getEffect() {
        return effectType;
    }

    /** {@inheritDoc} */
    public String getRuleId() {
        return ruleId;
    }

    /** {@inheritDoc} */
    public TargetType getTarget() {
        return target;
    }

    /** {@inheritDoc} */
    public void setCondition(final ConditionType newCondition) {
       this.condition = prepareForAssignment(this.condition,newCondition);
    }

    /** {@inheritDoc} */
    public void setDescription(final DescriptionType newDescription) {
        this.description = prepareForAssignment(this.description,newDescription);
    }

    /** {@inheritDoc} */
    public void setEffect(final EffectType type) {
        this.effectType = prepareForAssignment(this.effectType,type);
    }

    /** {@inheritDoc} */
    public void setRuleId(final String id) {
        this.ruleId = prepareForAssignment(this.ruleId,id);
    }

    /** {@inheritDoc} */
    public void setTarget(final TargetType newTarget) {
       this.target = prepareForAssignment(this.target,newTarget);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        
        final ArrayList<XMLObject> children = new ArrayList<>();        
        
        if(description != null){
            children.add(description);
        }
        if(target != null){
            children.add(target);
        }
        if(condition != null){
            children.add(condition);
        }
                
        return Collections.unmodifiableList(children);
    }
}
