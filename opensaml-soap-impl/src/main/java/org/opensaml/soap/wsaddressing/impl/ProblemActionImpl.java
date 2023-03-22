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

package org.opensaml.soap.wsaddressing.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.soap.wsaddressing.Action;
import org.opensaml.soap.wsaddressing.ProblemAction;
import org.opensaml.soap.wsaddressing.SoapAction;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Implementation of {@link ProblemAction}.
 */
public class ProblemActionImpl extends AbstractWSAddressingObject implements ProblemAction {
    
    /** Action child element. */
    @Nullable private Action action;
    
    /** SoapAction child element. */
    @Nullable private SoapAction soapAction;
    
    /** Wildcard attributes. */
    @Nonnull private final AttributeMap unknownAttributes;

    /**
     * Constructor.
     * 
     * @param namespaceURI The namespace of the element
     * @param elementLocalName The local name of the element
     * @param namespacePrefix The namespace prefix of the element
     */
    public ProblemActionImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
    }

    /** {@inheritDoc} */
    @Nullable public Action getAction() {
        return action;
    }

    /** {@inheritDoc} */
    @Nullable public SoapAction getSoapAction() {
        return soapAction;
    }

    /** {@inheritDoc} */
    public void setAction(@Nullable final Action newAction) {
        action = prepareForAssignment(action, newAction);
    }

    /** {@inheritDoc} */
    public void setSoapAction(@Nullable final SoapAction newSoapAction) {
        soapAction = prepareForAssignment(soapAction, newSoapAction);
    }

    /** {@inheritDoc} */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }

    /** {@inheritDoc} */
    @Nullable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();
        if (action != null) {
            children.add(action);
        }
        if (soapAction != null) {
            children.add(soapAction);
        }

        return CollectionSupport.copyToList(children);
    }

}
