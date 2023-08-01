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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.xmlsec.signature.KeyInfo;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interface to define how a SubjectConfirmation element behaves.
 */
public interface SubjectConfirmation extends SAMLObject {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "SubjectConfirmation";
    
    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML1_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "SubjectConfirmationType"; 
        
    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML1_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);

    /**
     * Get a mutable list of all the ConfirmationMethods.
     * 
     * @return mutable list of ConfirmationMethods.
     */
    @Nonnull @Live List<ConfirmationMethod> getConfirmationMethods();

    /**
     * Set the SubjectConfirmationData.
     * 
     * @param subjectConfirmationData data to set
     */
    void setSubjectConfirmationData(@Nullable final XMLObject subjectConfirmationData);

    /**
     * Get the SubjectConfirmationData.
     * 
     * @return the SubjectConfirmationData
     */
    @Nullable XMLObject getSubjectConfirmationData();
    
    /**
     * Gets the key information for the subject.
     * 
     * @return the key information for the subject
     */
    @Nullable KeyInfo getKeyInfo();

    /**
     * Sets the key information for the subject.
     * 
     * @param keyInfo the key information for the subject
     */
    void setKeyInfo(@Nullable final KeyInfo keyInfo);
}
