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

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.xacml.ctx.ActionType;
import org.opensaml.xacml.ctx.EnvironmentType;
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.ctx.ResourceType;
import org.opensaml.xacml.ctx.SubjectType;
import org.opensaml.xacml.impl.AbstractXACMLObjectUnmarshaller;

/** Unmarshaller for {@link EnvironmentType} objects. */
public class RequestTypeUnmarshaller extends AbstractXACMLObjectUnmarshaller {

    /** Constructor. */
    public RequestTypeUnmarshaller() {
        super();
    }

    /** {@inheritDoc} */
    protected void processChildElement(final XMLObject parentXMLObject, final XMLObject childXMLObject)
            throws UnmarshallingException {
        final RequestType request = (RequestType) parentXMLObject;

        if (childXMLObject instanceof ActionType) {
            request.setAction((ActionType) childXMLObject);
        } else if (childXMLObject instanceof EnvironmentType) {
            request.setEnvironment((EnvironmentType) childXMLObject);
        } else if (childXMLObject instanceof SubjectType) {
            request.getSubjects().add((SubjectType) childXMLObject);
        } else if (childXMLObject instanceof ResourceType) {
            request.getResources().add((ResourceType) childXMLObject);
        } else {
            super.processChildElement(parentXMLObject, childXMLObject);
        }
    }

}