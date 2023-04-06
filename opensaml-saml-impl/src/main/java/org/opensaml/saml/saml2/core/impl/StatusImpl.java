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

/**
 * 
 */

package org.opensaml.saml.saml2.core.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.StatusDetail;
import org.opensaml.saml.saml2.core.StatusMessage;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link Status}.
 */
public class StatusImpl extends AbstractXMLObject implements Status {

    /** StatusCode element. */
    @Nullable private StatusCode statusCode;

    /** StatusMessage element. */
    @Nullable private StatusMessage statusMessage;

    /** StatusDetail element. */
    @Nullable private StatusDetail statusDetail;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace uri
     * @param elementLocalName element name
     * @param namespacePrefix namespace prefix
     */
    protected StatusImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public StatusCode getStatusCode() {
        return this.statusCode;
    }

    /** {@inheritDoc} */
    public void setStatusCode(@Nullable final StatusCode newStatusCode) {
        this.statusCode = prepareForAssignment(this.statusCode, newStatusCode);

    }

    /** {@inheritDoc} */
    @Nullable public StatusMessage getStatusMessage() {
        return this.statusMessage;
    }

    /** {@inheritDoc} */
    public void setStatusMessage(@Nullable final StatusMessage newStatusMessage) {
        this.statusMessage = prepareForAssignment(this.getStatusMessage(), newStatusMessage);
    }

    /** {@inheritDoc} */
    @Nullable public StatusDetail getStatusDetail() {
        return this.statusDetail;
    }

    /** {@inheritDoc} */
    public void setStatusDetail(@Nullable final StatusDetail newStatusDetail) {
        this.statusDetail = prepareForAssignment(this.statusDetail, newStatusDetail);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
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
        
        return CollectionSupport.copyToList(children);
    }

}