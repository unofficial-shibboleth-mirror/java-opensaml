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

import java.time.Instant;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AbstractXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.AttributeMap;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link SubjectConfirmationData}.
 */
public class SubjectConfirmationDataImpl extends AbstractXMLObject implements SubjectConfirmationData {

    /** NotBefore of the Confirmation Data. */
    @Nullable private Instant notBefore;

    /** NotOnOrAfter of the Confirmation Data. */
    @Nullable private Instant notOnOrAfter;

    /** Recipient of the Confirmation Data. */
    @Nullable private String recipient;

    /** InResponseTo of the Confirmation Data. */
    @Nullable private String inResponseTo;

    /** Address of the Confirmation Data. */
    @Nullable private String address;
    
    /** "anyAttribute" attributes. */
    @Nonnull private final AttributeMap unknownAttributes;
    
    /** "any" children. */
    @Nonnull private final IndexedXMLObjectChildrenList<XMLObject> unknownChildren;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected SubjectConfirmationDataImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        unknownAttributes = new AttributeMap(this);
        unknownChildren = new IndexedXMLObjectChildrenList<>(this);
    }

    /** {@inheritDoc} */
    @Nullable public Instant getNotBefore() {
        return notBefore;
    }

    /** {@inheritDoc} */
    public void setNotBefore(@Nullable final Instant newNotBefore) {
        this.notBefore = prepareForAssignment(this.notBefore, newNotBefore);
    }

    /** {@inheritDoc} */
    @Nullable public Instant getNotOnOrAfter() {
        return notOnOrAfter;
    }

    /** {@inheritDoc} */
    public void setNotOnOrAfter(@Nullable final Instant newNotOnOrAfter) {
        this.notOnOrAfter = prepareForAssignment(this.notOnOrAfter, newNotOnOrAfter);
    }

    /** {@inheritDoc} */
    @Nullable public String getRecipient() {
        return recipient;
    }

    /** {@inheritDoc} */
    public void setRecipient(@Nullable final String newRecipient) {
        this.recipient = prepareForAssignment(this.recipient, newRecipient);
    }

    /** {@inheritDoc} */
    @Nullable public String getInResponseTo() {
        return inResponseTo;
    }

    /** {@inheritDoc} */
    public void setInResponseTo(@Nullable final String newInResponseTo) {
        this.inResponseTo = prepareForAssignment(this.inResponseTo, newInResponseTo);
    }

    /** {@inheritDoc} */
    @Nullable public String getAddress() {
        return address;
    }

    /** {@inheritDoc} */
    public void setAddress(@Nullable final String newAddress) {
        this.address = prepareForAssignment(this.address, newAddress);
    }
    
    /**
     * {@inheritDoc}
     */
    @Nonnull public AttributeMap getUnknownAttributes() {
        return unknownAttributes;
    }
    
    /**
     * {@inheritDoc}
     */
    @Nonnull @Live public List<XMLObject> getUnknownXMLObjects() {
        return unknownChildren;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nonnull @Live public List<XMLObject> getUnknownXMLObjects(@Nonnull final QName typeOrName) {
        return (List<XMLObject>) unknownChildren.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        return CollectionSupport.copyToList(unknownChildren);
    }
    
}