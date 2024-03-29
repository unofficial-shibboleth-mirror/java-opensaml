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
import org.opensaml.xacml.ctx.RequestType;
import org.opensaml.xacml.ctx.ResponseType;
import org.opensaml.xacml.profile.saml.XACMLAuthzDecisionStatementType;

/** A concrete implementation of {@link org.opensaml.xacml.profile.saml.XACMLAuthzDecisionStatementType}. */
public class XACMLAuthzDecisionStatementTypeImpl extends AbstractXMLObject implements XACMLAuthzDecisionStatementType {

    /** The request of the authorization request. */
    private RequestType request;

    /** The response of the authorization request. */
    private ResponseType response;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected XACMLAuthzDecisionStatementTypeImpl(final String namespaceURI, final String elementLocalName,
            final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public RequestType getRequest() {
        return request;
    }

    /** {@inheritDoc} */
    public ResponseType getResponse() {
        return response;
    }

    /** {@inheritDoc} */
    public void setRequest(final RequestType req) {
        request = prepareForAssignment(request, req);
    }

    /** {@inheritDoc} */
    public void setResponse(final ResponseType resp) {
        response = prepareForAssignment(response, resp);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        if (request != null) {
            children.add(request);
        }
        if (response != null) {
            children.add(response);
        }

        return Collections.unmodifiableList(children);
    }
}
