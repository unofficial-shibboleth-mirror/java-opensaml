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
import org.opensaml.saml.saml1.core.Advice;
import org.opensaml.saml.saml1.core.Assertion;
import org.opensaml.saml.saml1.core.AttributeStatement;
import org.opensaml.saml.saml1.core.AuthenticationStatement;
import org.opensaml.saml.saml1.core.AuthorizationDecisionStatement;
import org.opensaml.saml.saml1.core.Conditions;
import org.opensaml.saml.saml1.core.Statement;
import org.opensaml.saml.saml1.core.SubjectStatement;
import org.opensaml.xmlsec.signature.Signature;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * This class implements the SAML 1 <code> Assertion </code> statement.
 */
@SuppressWarnings("unchecked")
public class AssertionImpl extends AbstractSignableSAMLObject implements Assertion {

    /** The <code> AssertionID </code> attrribute. */
    @Nullable private String id;
    
    /** SAML version of this assertion. */
    @Nullable private SAMLVersion version;
    
    /** Object version of the <code> Issuer </code> attribute. */
    @Nullable private String issuer;

    /** Object version of the <code> IssueInstant </code> attribute. */
    @Nullable private Instant issueInstant;

    /** (Possibly null) Singleton object version of the <code> Conditions </code> element. */
    @Nullable private Conditions conditions;

    /** (Possibly null) Singleton object version of the <code> Advice </code> element. */
    @Nullable private Advice advice;

    /** Object representation of all the <code>Statement</code> elements. */
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
        statements = new IndexedXMLObjectChildrenList<>(this);
        version = SAMLVersion.VERSION_11;
    }
    
    /** {@inheritDoc} */
    @Nullable public Integer getMajorVersion(){
        return version != null ? version.getMajorVersion() : null;
    }
    
    /** {@inheritDoc} */
    @Nullable public Integer getMinorVersion() {
        return version != null ? version.getMinorVersion() : null;
    }
    
    /** {@inheritDoc} */
    public void setVersion(@Nullable final SAMLVersion newVersion){
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
    @Nullable public String getIssuer() {
        return this.issuer;
    }

    /** {@inheritDoc} */
    public void setIssuer(@Nullable final String iss) {
        issuer = prepareForAssignment(issuer, iss);
    }

    /** {@inheritDoc} */
    @Nullable public Instant getIssueInstant() {
        return this.issueInstant;
    }

    /** {@inheritDoc} */
    public void setIssueInstant(@Nullable final Instant instant) {
        issueInstant = prepareForAssignment(issueInstant, instant);
    }

    /** {@inheritDoc} */
    @Nullable public Conditions getConditions() {
        return conditions;
    }

    /** {@inheritDoc} */
    public void setConditions(@Nullable final Conditions c) {
        conditions = prepareForAssignment(conditions, c);
    }

    /** {@inheritDoc} */
    @Nullable public Advice getAdvice() {
        return advice;
    }

    /** {@inheritDoc} */
    public void setAdvice(@Nullable final Advice adv) {
        advice = prepareForAssignment(advice, adv);
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
    @Nonnull @Live public List<SubjectStatement> getSubjectStatements() {
        final QName statementQName = new QName(SAMLConstants.SAML1_NS, SubjectStatement.DEFAULT_ELEMENT_LOCAL_NAME);
        return (List<SubjectStatement>) statements.subList(statementQName);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AuthenticationStatement> getAuthenticationStatements() {
        final QName statementQName =
                new QName(SAMLConstants.SAML1_NS, AuthenticationStatement.DEFAULT_ELEMENT_LOCAL_NAME);
        return (List<AuthenticationStatement>) statements.subList(statementQName);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AttributeStatement> getAttributeStatements() {
        final QName statementQName = new QName(SAMLConstants.SAML1_NS, AttributeStatement.DEFAULT_ELEMENT_LOCAL_NAME);
        return (List<AttributeStatement>) statements.subList(statementQName);
    }

    /** {@inheritDoc} */
    @Nonnull @Live public List<AuthorizationDecisionStatement> getAuthorizationDecisionStatements() {
        final QName statementQName =
                new QName(SAMLConstants.SAML1_NS, AuthorizationDecisionStatement.DEFAULT_ELEMENT_LOCAL_NAME);
        return (List<AuthorizationDecisionStatement>) statements.subList(statementQName);
    }

    /** {@inheritDoc} */
    @Nullable public String getSignatureReferenceID() {
        return id;
    }

    /** {@inheritDoc} */
    @Nullable @NotLive @Unmodifiable public List<XMLObject> getOrderedChildren() {

        final ArrayList<XMLObject> children = new ArrayList<>();

        if (conditions != null) {
            children.add(conditions);
        }

        if (advice != null) {
            children.add(advice);
        }

        children.addAll(statements);
        
        final Signature sig = getSignature();
        if (sig != null) {
            children.add(sig);
        }

        return CollectionSupport.copyToList(children);
    }
    
}