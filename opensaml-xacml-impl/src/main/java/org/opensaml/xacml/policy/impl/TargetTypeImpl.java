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
import org.opensaml.xacml.policy.ActionsType;
import org.opensaml.xacml.policy.EnvironmentsType;
import org.opensaml.xacml.policy.ResourcesType;
import org.opensaml.xacml.policy.SubjectsType;
import org.opensaml.xacml.policy.TargetType;

/**
 * Implementing {@link org.opensaml.xacml.policy.TargetType}.
 */
public class TargetTypeImpl extends AbstractXACMLObject implements TargetType {

    /** The actions in the policy. */
    private ActionsType actions;

    /** The environments in the policy. */
    private EnvironmentsType environments;

    /** The subjects in the policy. */
    private SubjectsType subjects;

    /** The resourcese in the policy. */
    private ResourcesType resources;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected TargetTypeImpl(final String namespaceURI, final String elementLocalName, final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);       
    }


    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        if(subjects != null){
            children.add(subjects);  
        }
        if(resources != null){
            children.add(resources);  
        }
        if(actions != null){
            children.add(actions);  
        }        
        if(environments != null){
            children.add(environments);  
        }
        return Collections.unmodifiableList(children);
    }

    /** {@inheritDoc}*/
    public SubjectsType getSubjects() {
        return subjects;
    }
        
    /** {@inheritDoc}*/
    public ResourcesType getResources() {
        return resources;
    }

    /** {@inheritDoc}*/
    public ActionsType getActions() {
        return actions;
    }

    /**{@inheritDoc}*/
    public EnvironmentsType getEnvironments() {
        return environments;
    }

    /**{@inheritDoc}*/
    public void setActions(final ActionsType newActions) {
        this.actions = prepareForAssignment(this.actions,newActions);
    }

    /**{@inheritDoc}*/
    public void setEnvironments(final EnvironmentsType newEnvironments) {
        this.environments = prepareForAssignment(this.environments,newEnvironments);
    }

    /**{@inheritDoc}*/
    public void setResources(final ResourcesType newResources) {
        this.resources = prepareForAssignment(this.resources,newResources);
    }

    /**{@inheritDoc}*/
    public void setSubjects(final SubjectsType newSubjects) {
        this.subjects = prepareForAssignment(this.subjects,newSubjects);
    }
}
