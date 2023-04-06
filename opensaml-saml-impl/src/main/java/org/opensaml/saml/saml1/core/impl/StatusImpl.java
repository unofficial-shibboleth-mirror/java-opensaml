/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.saml1.core.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml1.core.Status;
import org.opensaml.saml.saml1.core.StatusCode;
import org.opensaml.saml.saml1.core.StatusDetail;
import org.opensaml.saml.saml1.core.StatusMessage;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete Implementation {@link org.opensaml.saml.saml1.core.Status}.
 */
public class StatusImpl extends AbstractXMLObject implements Status {

    /** Representation of the StatusMessage element. */
    @Nullable private StatusMessage statusMessage;

    /** Representation of the StatusCode element. */
    @Nullable private StatusCode statusCode;

    /** Representation of the StatusDetail element. */
    @Nullable private StatusDetail statusDetail;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected StatusImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    /** {@inheritDoc} */
    public void setStatusMessage(@Nullable final StatusMessage message) {
        statusMessage = prepareForAssignment(statusMessage, message);
    }

    /** {@inheritDoc} */
    @Nullable public StatusCode getStatusCode() {
        return statusCode;
    }

    /** {@inheritDoc} */
    public void setStatusCode(@Nullable final StatusCode code) {
        statusCode = prepareForAssignment(statusCode, code);
    }

    /** {@inheritDoc} */
    @Nullable public StatusDetail getStatusDetail() {
        return statusDetail;
    }

    /** {@inheritDoc} */
    public void setStatusDetail(@Nullable final StatusDetail detail) {
        statusDetail = prepareForAssignment(statusDetail, detail);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>(3);

        if (statusCode != null) {
            children.add(statusCode);
        }

        if (statusMessage != null) {
            children.add(statusMessage);
        }

        if (statusDetail != null) {
            children.add(statusDetail);
        }

        return CollectionSupport.copyToList(children);
    }
    
}