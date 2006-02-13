/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
package org.opensaml.saml2.core;

import java.util.List;

import org.opensaml.common.SAMLObject;

/**
 * SAML 2.0 Core RequestedAuthnContext 
 */
public interface RequestedAuthnContext extends SAMLObject {
    
    /** Element local name */
    public final static String LOCAL_NAME = "RequestedAuthnContext";
    
    /** SessionIndex attribute name */
    public final static String COMPARISON_ATTRIB_NAME = "Comparison";
    
    /**
     * Gets the Comparison attribute value of the requested authn context
     * 
     * @return the Comparison attribute value of the requested authn context
     */
    public AuthnContextComparisonType getComparison();
    
    /**
     * Sets the Comparison attribute value of the requested authn context
     * 
     * @param newComparison the SessionIndex of this request
     */
    public void setComparison(AuthnContextComparisonType newComparison);
    
    /**
     * Gets the AuthnContextClassRefs of this request.
     * 
     * @return the AuthnContextClassRefs of this request
     */
    public List<AuthnContextClassRef> getAuthnContextClassRefs();

    /**
     * Gets the AuthnContextDeclRefs of this request.
     * 
     * @return the AuthnContextDeclRef of this request
     */
    public List<AuthnContextDeclRef> getAuthnContextDeclRefs();
    
}
