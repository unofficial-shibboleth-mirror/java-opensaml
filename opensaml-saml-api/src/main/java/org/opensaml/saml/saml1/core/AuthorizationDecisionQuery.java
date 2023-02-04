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
package org.opensaml.saml.saml1.core;

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.xml.SAMLConstants;

/**
 * Description of the behaviour of the <code> AuthorizationDecisionQuery </code> element.
 */
public interface AuthorizationDecisionQuery extends SubjectQuery {

    /** Element name, no namespace. */
    @Nonnull public static final String DEFAULT_ELEMENT_LOCAL_NAME = "AuthorizationDecisionQuery";
    
    /** Default element name. */
    @Nonnull public static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML10P_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1P_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull public static final String TYPE_LOCAL_NAME = "AuthorizationDecisionQueryType"; 
        
    /** QName of the XSI type. */
    @Nonnull public static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML10P_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML1P_PREFIX);
    
    /** AuthenticationMethod attribute name. */
    @Nonnull public static final String RESOURCE_ATTRIB_NAME = "Resource"; 

    /** 
     * Get Resource attribute.
     * 
     * @return Resource attribute
     */
    public String getResource();
    
    /**
     * Set Resource attribute.
     * 
     * @param resource Resource attribute to set
     */
    public void setResource(String resource);

    /**
     * Get list of Action child elements.
     * 
     * @return Action list
     */
    public List<Action> getActions();
    
    /**
     * Get the Evidence child element.
     * 
     * @return Evidence child element
     */
    public Evidence getEvidence();

    /**
     * Set the Evidence child element.
     * 
     * @param evidence child element to set
     */
    public void setEvidence(Evidence evidence);
}
