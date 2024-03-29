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

package org.opensaml.saml.saml2.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Core SubjectConfirmation.
 */
public interface SubjectConfirmation extends SAMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "SubjectConfirmation";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "SubjectConfirmationType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** Method attribute name. */
    @Nonnull @NotEmpty static final String METHOD_ATTRIB_NAME = "Method";
    
    /** URI for the Holder of Key subject confirmation method, {@value}. */
    @Nonnull @NotEmpty static final String METHOD_HOLDER_OF_KEY = "urn:oasis:names:tc:SAML:2.0:cm:holder-of-key";
    
    /** URI for the Sender Vouches subject confirmation method, {@value}. */
    @Nonnull @NotEmpty static final String METHOD_SENDER_VOUCHES = "urn:oasis:names:tc:SAML:2.0:cm:sender-vouches";
    
    /** URI for the Bearer subject confirmation method, {@value}. */
    @Nonnull @NotEmpty static final String METHOD_BEARER = "urn:oasis:names:tc:SAML:2.0:cm:bearer";

    /**
     * Get the method used to confirm this subject.
     * 
     * @return the method used to confirm this subject
     */
    @Nullable String getMethod();

    /**
     * Sets the method used to confirm this subject.
     * 
     * @param newMethod the method used to confirm this subject
     */
    void setMethod(@Nullable final String newMethod);

    /**
     * Gets the base identifier of the principal for this request.
     * 
     * @return the base identifier of the principal for this request
     */
    @Nullable BaseID getBaseID();

    /**
     * Sets the base identifier of the principal for this request.
     * 
     * @param newBaseID the base identifier of the principal for this request
     */
    void setBaseID(@Nullable final BaseID newBaseID);

    /**
     * Gets the name identifier of the principal for this request.
     * 
     * @return the name identifier of the principal for this request
     */
    @Nullable NameID getNameID();

    /**
     * Sets the name identifier of the principal for this request.
     * 
     * @param newNameID the name identifier of the principal for this request
     */
    void setNameID(@Nullable final NameID newNameID);

    /**
     * Gets the encrypted name identifier of the principal for this request.
     * 
     * @return the encrypted name identifier of the principal for this request
     */
    @Nullable EncryptedID getEncryptedID();

    /**
     * Sets the encrypted name identifier of the principal for this request.
     * 
     * @param newEncryptedID the new encrypted name identifier of the principal for this request
     */
    void setEncryptedID(@Nullable final EncryptedID newEncryptedID);

    /**
     * Gets the data about how this subject was confirmed or constraints on the confirmation.
     * 
     * @return the data about how this subject was confirmed or constraints on the confirmation
     */
    @Nullable SubjectConfirmationData getSubjectConfirmationData();

    /**
     * Sets the data about how this subject was confirmed or constraints on the confirmation.
     * 
     * @param newSubjectConfirmationData the data about how this subject was confirmed or constraints on the
     *            confirmation
     */
    void setSubjectConfirmationData(@Nullable final SubjectConfirmationData newSubjectConfirmationData);

}