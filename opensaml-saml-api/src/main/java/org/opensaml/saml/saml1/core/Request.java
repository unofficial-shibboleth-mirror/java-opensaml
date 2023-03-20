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

package org.opensaml.saml.saml1.core;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * This interface defines how the SAML1 <code> Request </code> objects behave.
 */
public interface Request extends RequestAbstractType {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty public static final String DEFAULT_ELEMENT_LOCAL_NAME = "Request";
    
    /** Default element name. */
    @Nonnull public static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML10P_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1P_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty public static final String TYPE_LOCAL_NAME = "RequestType"; 
        
    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML10P_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML1P_PREFIX);

    /* 
     * A bit odd this, it s a choice so only one of these will return any value
     */

    /**
     * Get the request Query, SubjectQuery, AuthenticationQuery, AttributeQuery, or AuthorizationDecisionQuery.
     * 
     * @return request Query, SubjectQuery, AuthenticationQuery, AttributeQuery, or AuthorizationDecisionQuery
     */
    @Nullable public Query getQuery();
    
    /**
     * Get the request SubjectQuery, AuthenticationQuery, AttributeQuery, or AuthorizationDecisionQuery.
     *
     * @return request SubjectQuery, AuthenticationQuery, AttributeQuery, or AuthorizationDecisionQuery
     */
    @Nullable public SubjectQuery getSubjectQuery();
    
    /**
     * Get the request AuthenticationQuery.
     * 
     * @return request AuthenticationQuery
     */ 
    @Nullable public AuthenticationQuery getAuthenticationQuery();
    
    /**
     * Get the request AttributeQuery.
     * 
     * @return request AttributeQuery
     */
    @Nullable public AttributeQuery getAttributeQuery();
    
    /**
     * Get the request AuthorizationDecisionQuery.
     * 
     * @return request AuthorizationDecisionQuery
     */ 
    @Nullable public AuthorizationDecisionQuery getAuthorizationDecisionQuery();
    
    /**
     * Set the request query (Query, SubjectQuery, AuthenticationQuery, AttributeQuery, AuthorizationDecisioonQuery).
     * 
     * @param query Query, SubjectQuery, AuthenticationQuery, AttributeQuery, AuthorizationDecisioonQuery
     */ 
    public void setQuery(@Nullable Query query);
    
    /**
     * Get the list of AssertionIDReferences.
     * 
     * @return list of AssertionIDReferences
     */
    @Nonnull @NonnullElements public List <AssertionIDReference> getAssertionIDReferences();
    
    /**
     * Get the list of artifacts.
     * 
     * @return list of artifacts
     */
    @Nonnull @NonnullElements public List <AssertionArtifact> getAssertionArtifacts();
}
