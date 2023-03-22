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

package org.opensaml.soap.wstrust.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.soap.wstrust.Code;
import org.opensaml.soap.wstrust.Reason;
import org.opensaml.soap.wstrust.Status;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * StatusImpl.
 * 
 */
public class StatusImpl extends AbstractWSTrustObject implements Status {

    /** The Code child element. */
    @Nullable private Code code;

    /** The Reason child element. */
    @Nullable private Reason reason;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public StatusImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public Code getCode() {
        return code;
    }

    /** {@inheritDoc} */
    @Nullable public Reason getReason() {
        return reason;
    }

    /** {@inheritDoc} */
    public void setCode(@Nullable final Code newCode) {
        code = prepareForAssignment(code, newCode);
    }

    /** {@inheritDoc} */
    public void setReason(@Nullable final Reason newReason) {
        reason = prepareForAssignment(reason, newReason);
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        if (code != null) {
            children.add(code);
        }
        if (reason != null) {
            children.add(reason);
        }
        return CollectionSupport.copyToList(children);
    }

}