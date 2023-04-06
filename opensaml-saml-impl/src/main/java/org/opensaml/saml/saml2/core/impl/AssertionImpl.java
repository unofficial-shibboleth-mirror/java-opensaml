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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.IndexedXMLObjectChildrenList;
import org.opensaml.saml.common.AbstractSignableSAMLObject;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Advice;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.AuthzDecisionStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Statement;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.xmlsec.signature.Signature;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * A concrete implementation of {@link Assertion}.
 */
@SuppressWarnings("unchecked")
public class AssertionImpl extends AbstractSignableSAMLObject implements Assertion {

    /** SAML Version of the assertion. */
    @Nullable private SAMLVersion version;

    /** Issue Instant of the assertion. */
    @Nullable private Instant issueInstant;

    /** ID of the assertion. */
    @Nullable private String id;

    /** Issuer of the assertion. */
    @Nullable private Issuer issuer;

    /** Subject of the assertion. */
    @Nullable private Subject subject;

    /** Conditions of the assertion. */
    @Nullable private Conditions conditions;

    /** Advice of the assertion. */
    @Nullable private Advice advice;

    /** Statements of the assertion. */
    @Nonnull private final IndexedXMLObjectChildrenList<Statement> statements;

    /**
     * Constructor.
     * 
     * @param namespaceURI the namespace the element is in
     * @param elementLocalName the local name of the XML element this Object represents
     * @param namespacePrefix the prefix for the given namespace
     */
    protected AssertionImpl(@Nullable final String namespaceURI, @Nonnull final String elementLocalName,
            @Nullable final String namespacePrefix) {
        super(namespaceURI, elementLocalName, namespacePrefix);
        version = SAMLVersion.VERSION_20;
        statements = new IndexedXMLObjectChildrenList<>(this);
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
    @Nullable public Instant getIssueInstant() {
        return issueInstant;
    }

    /** {@inheritDoc} */
    public void setIssueInstant(@Nullable final Instant newIssueInstance) {
        issueInstant = prepareForAssignment(issueInstant, newIssueInstance);
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
    @Nullable public Issuer getIssuer() {
        return issuer;
    }

    /** {@inheritDoc} */
    public void setIssuer(@Nullable final Issuer newIssuer) {
        issuer = prepareForAssignment(issuer, newIssuer);
    }

    /** {@inheritDoc} */
    @Nullable public Subject getSubject() {
        return subject;
    }

    /** {@inheritDoc} */
    public void setSubject(@Nullable final Subject newSubject) {
        subject = prepareForAssignment(subject, newSubject);
    }

    /** {@inheritDoc} */
    @Nullable public Conditions getConditions() {
        return conditions;
    }

    /** {@inheritDoc} */
    public void setConditions(@Nullable final Conditions newConditions) {
        conditions = prepareForAssignment(conditions, newConditions);
    }

    /** {@inheritDoc} */
    @Nullable public Advice getAdvice() {
        return advice;
    }

    /** {@inheritDoc} */
    public void setAdvice(@Nullable final Advice newAdvice) {
        advice = prepareForAssignment(advice, newAdvice);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<Statement> getStatements() {
        return statements;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<Statement> getStatements(@Nonnull final QName typeOrName) {
        return (List<Statement>) statements.subList(typeOrName);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AuthnStatement> getAuthnStatements() {
        final QName statementQName = new QName(SAMLConstants.SAML20_NS, AuthnStatement.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20_PREFIX);
        return (List<AuthnStatement>) statements.subList(statementQName);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AuthzDecisionStatement> getAuthzDecisionStatements() {
        final QName statementQName = new QName(SAMLConstants.SAML20_NS,
                AuthzDecisionStatement.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20_PREFIX);
        return (List<AuthzDecisionStatement>) statements.subList(statementQName);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AttributeStatement> getAttributeStatements() {
        final QName statementQName = new QName(SAMLConstants.SAML20_NS, AttributeStatement.DEFAULT_ELEMENT_LOCAL_NAME,
                SAMLConstants.SAML20_PREFIX);
        return (List<AttributeStatement>) statements.subList(statementQName);
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
        if (sig != null){
            children.add(sig);
        }
        
        if (subject != null) {
            children.add(subject);
        }
        
        if (conditions != null) {
            children.add(conditions);
        }
        
        if (advice != null) {
            children.add(advice);
        }
        
        children.addAll(statements);

        return CollectionSupport.copyToList(children);
    }
    
}