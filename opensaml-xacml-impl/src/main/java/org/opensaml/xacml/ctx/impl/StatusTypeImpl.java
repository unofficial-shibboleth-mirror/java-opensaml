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
import org.opensaml.xacml.ctx.StatusCodeType;
import org.opensaml.xacml.ctx.StatusDetailType;
import org.opensaml.xacml.ctx.StatusMessageType;
import org.opensaml.xacml.ctx.StatusType;
import org.opensaml.xacml.impl.AbstractXACMLObject;

/** Concrete implementation of {@link StatusType}. */
public class StatusTypeImpl extends AbstractXACMLObject implements StatusType {

    /** Status code. */
    private StatusCodeType statusCode;

    /** The status message. */
    private StatusMessageType statusMessage;

    /** Status detail element. */
    private StatusDetailType statusDetail;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected StatusTypeImpl(final String namespaceURI, final String elementLocalName, final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    public StatusCodeType getStatusCode() {
        return statusCode;
    }

    /** {@inheritDoc} */
    public void setStatusCode(final StatusCodeType newStatusCode) {
        statusCode = prepareForAssignment(this.statusCode, newStatusCode);
    }

    /** {@inheritDoc} */
    public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        if (statusCode != null) {
            children.add(statusCode);
        }

        if (statusMessage != null) {
            children.add(statusMessage);
        }

        if (statusDetail != null) {
            children.add(statusDetail);
        }

        return Collections.unmodifiableList(children);
    }

    /** {@inheritDoc} */
    public StatusMessageType getStatusMessage() {
        return statusMessage;
    }

    /** {@inheritDoc} */
    public void setStatusMessage(final StatusMessageType newStatusMessage) {
        this.statusMessage = prepareForAssignment(this.statusMessage, newStatusMessage);
    }

    /** {@inheritDoc} */
    public StatusDetailType getStatusDetail() {
        return statusDetail;
    }

    /** {@inheritDoc} */
    public void setStatusDetail(final StatusDetailType newStatusDetail) {
        this.statusDetail = prepareForAssignment(this.statusDetail, newStatusDetail);
    }
}