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

package org.opensaml.saml.saml2.core.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSignableSAMLObject;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Extensions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.xmlsec.signature.Signature;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Concrete implementation of {@link StatusResponseType}.
 */
public abstract class StatusResponseTypeImpl extends AbstractSignableSAMLObject implements StatusResponseType {

    /** SAML Version attribute. */
    @Nullable private SAMLVersion version;
    
    /** ID attribute. */
    @Nullable private String id;

    /** InResponseTo attribute. */
    @Nullable private String inResponseTo;

    /** IssueInstant attribute. */
    @Nullable private Instant issueInstant;

    /** Destination attribute. */
    @Nullable private String destination;

    /** Consent attribute. */
    @Nullable private String consent;

    /** Issuer child element. */
    @Nullable private Issuer issuer;

    /** Extensions child element. */
    @Nullable private Extensions extensions;

    /** Status child element. */
    @Nullable private Status status;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected StatusResponseTypeImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        version = SAMLVersion.VERSION_20;
    }

    /** {@inheritDoc} */
    @Nullable public SAMLVersion getVersion() {
        return version;
    }

    /** {@inheritDoc} */
    public void setVersion(@Nullable final SAMLVersion newVersion) {
        version = prepareForAssignment(version, newVersion);
    }
    
    /** {@inheritDoc} */
    @Nullable public String getID() {
        return id;
    }

    /** {@inheritDoc} */
    public void setID(@Nullable final String newID) {
        final String oldID = id;
        id = prepareForAssignment(id, newID);
        registerOwnID(oldID, id);
    }

    /** {@inheritDoc} */
    @Nullable public String getInResponseTo() {
        return inResponseTo;
    }

    /** {@inheritDoc} */
    public void setInResponseTo(@Nullable final String newInResponseTo) {
        inResponseTo = prepareForAssignment(inResponseTo, newInResponseTo);
    }

    /** {@inheritDoc} */
    @Nullable public Instant getIssueInstant() {
        return issueInstant;
    }

    /** {@inheritDoc} */
    public void setIssueInstant(@Nullable final Instant newIssueInstant) {
        issueInstant = prepareForAssignment(issueInstant, newIssueInstant);
    }

    /** {@inheritDoc} */
    @Nullable public String getDestination() {
        return destination;
    }

    /** {@inheritDoc} */
    public void setDestination(@Nullable final String newDestination) {
        destination = prepareForAssignment(destination, newDestination);
    }

    /** {@inheritDoc} */
    @Nullable public String getConsent() {
        return consent;
    }

    /** {@inheritDoc} */
    public void setConsent(@Nullable final String newConsent) {
        consent = prepareForAssignment(consent, newConsent);
    }

    /** {@inheritDoc} */
    @Nullable public Issuer getIssuer() {
        return issuer;
    }

    /** {@inheritDoc} */
    public void setIssuer(@Nullable final Issuer newIssuer) {
        issuer = prepareForAssignment(issuer, newIssuer);
    }

    /** {@inheritDoc} */
    @Nullable public Extensions getExtensions() {
        return extensions;
    }

    /** {@inheritDoc} */
    public void setExtensions(@Nullable final Extensions newExtensions) {
        extensions = prepareForAssignment(extensions, newExtensions);
    }

    /** {@inheritDoc} */
    @Nullable public Status getStatus() {
        return status;
    }

    /** {@inheritDoc} */
    public void setStatus(@Nullable final Status newStatus) {
        status = prepareForAssignment(status, newStatus);
    }
    
    /** {@inheritDoc} */
    @Nullable public String getSignatureReferenceID(){
        return id;
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {
        final ArrayList<XMLObject> children = new ArrayList<>();

        if (issuer != null) {
            children.add(issuer);
        }
        
        final Signature sig = getSignature();
        if (sig != null) {
            children.add(sig);
        }
        
        if (extensions != null) {
            children.add(extensions);
        }
        
        if (status != null) {
            children.add(status);
        }

        return CollectionSupport.copyToList(children);
    }
    
}