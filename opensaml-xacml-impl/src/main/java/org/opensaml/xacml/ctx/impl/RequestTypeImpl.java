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

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectChildrenList;
import org.opensaml.xacml.ctx.ActionType;
import org.opensaml.xacml.ctx.EnvironmentType;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.ctx.ResourceType;
import org.opensaml.xacml.ctx.SubjectType;
import org.opensaml.xacml.impl.AbstractXACMLObject;

/** Concrete implementation of {@link RequestType}. */
public class RequestTypeImpl extends AbstractXACMLObject implements RequestType {

    /** The subjects of the request. */
    private XMLObjectChildrenList<SubjectType> subjects;

    /** The resources of the request. */
    private XMLObjectChildrenList<ResourceType> resources;

    /** The environment of the request. */
    private EnvironmentType environment;

    /** The action of the request. */
    private ActionType action;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected RequestTypeImpl(final String namespaceURI, final String elementLocalName, final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        subjects = new XMLObjectChildrenList<>(this);
        resources = new XMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    public List<SubjectType> getSubjects() {
        return subjects;
    }

    /** {@inheritDoc} */
    public List<ResourceType> getResources() {
        return resources;
    }

    /** {@inheritDoc} */
    public EnvironmentType getEnvironment() {
        return environment;
    }

    /** {@inheritDoc} */
    public void setEnvironment(final EnvironmentType env) {
        environment = prepareForAssignment(environment, env);
    }

    /** {@inheritDoc} */
    public ActionType getAction() {
        return action;
    }

    /** {@inheritDoc} */
    public void setAction(final ActionType act) {
        action = prepareForAssignment(action, act);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        children.addAll(subjects);
        children.addAll(resources);

        if (action != null) {
            children.add(action);
        }

        if (environment != null) {
            children.add(environment);
        }

        return Collections.unmodifiableList(children);
    }
}