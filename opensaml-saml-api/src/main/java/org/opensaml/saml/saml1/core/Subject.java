/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interface to describe how the <code> Subject </code> elements work.
 */
public interface Subject extends SAMLObject {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "Subject";
    
    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML1_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "SubjectType"; 
        
    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML1_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    
    /**
     * Get the name identifier.
     * 
     * @return the name identifier
     */
    @Nullable NameIdentifier getNameIdentifier();
    
    /**
     * Set the name identifier.
     * 
     * @param nameIdentifier the name identifier
     */
    void setNameIdentifier(@Nullable final NameIdentifier nameIdentifier);
    
    /**
     * Get the subject confirmation.
     * 
     * @return the subject confirmation
     */
    @Nullable SubjectConfirmation getSubjectConfirmation();
    
    /**
     * Set the subject confirmation.
     * 
     * @param subjectConfirmation the subject confirmation
     */
    void setSubjectConfirmation(@Nullable final SubjectConfirmation subjectConfirmation);
    
}
