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

package org.opensaml.soap.soap11.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.soap.soap11.Detail;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.soap11.FaultActor;
import org.opensaml.soap.soap11.FaultCode;
import org.opensaml.soap.soap11.FaultString;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implemenation of {@link org.opensaml.soap.soap11.Fault}.
 */
public class FaultImpl extends AbstractXMLObject implements Fault {

    /** Fault code. */
    @Nullable private FaultCode faultCode;

    /** Fault message. */
    @Nullable private FaultString message;

    /** Actor that faulted. */
    @Nullable private FaultActor actor;

    /** Details of the fault. */
    @Nullable private Detail detail;

    /**
     * Constructor.
     * 
     * @param namespaceURI namespace of the element
     * @param elementLocalName name of the element
     * @param namespacePrefix namespace prefix of the element
     */
    protected FaultImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
    }

    /** {@inheritDoc} */
    @Nullable public FaultCode getCode() {
        return faultCode;
    }

    /** {@inheritDoc} */
    public void setCode(@Nullable final FaultCode newFaultCode) {
        faultCode = prepareForAssignment(faultCode, newFaultCode);
    }

    /** {@inheritDoc} */
    @Nullable public FaultString getMessage() {
        return message;
    }

    /** {@inheritDoc} */
    public void setMessage(@Nullable final FaultString newMessage) {
        message = prepareForAssignment(message, newMessage);
    }

    /** {@inheritDoc} */
    @Nullable public FaultActor getActor() {
        return actor;
    }

    /** {@inheritDoc} */
    public void setActor(@Nullable final FaultActor newActor) {
        actor = prepareForAssignment(actor, newActor);
    }

    /** {@inheritDoc} */
    @Nullable public Detail getDetail() {
        return detail;
    }

    /** {@inheritDoc} */
    public void setDetail(@Nullable final Detail newDetail) {
        detail = prepareForAssignment(detail, newDetail);
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        if (faultCode != null) {
            children.add(faultCode);
        }
        
        if (message != null) {
            children.add(message);
        }
        
        if (actor != null) {
            children.add(actor);
        }
        
        if (detail != null) {
            children.add(detail);
        }

        if (children.isEmpty()) {
            return null;
        }
        
        return CollectionSupport.copyToList(children);
    }
}
