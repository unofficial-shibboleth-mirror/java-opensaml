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
package org.opensaml.saml.saml2.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;


/**
 * SAML 2.0 Core ManageNameIDRequest.
 */
public interface ManageNameIDRequest extends RequestAbstractType {
    
    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "ManageNameIDRequest";
    
    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = 
        new QName(SAMLConstants.SAML20P_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "ManageNameIDRequestType"; 
        
    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = 
        new QName(SAMLConstants.SAML20P_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
    
    /**
     * Get the NameID of the request.
     * 
     * @return the NameID of the request
     */
    @Nullable NameID getNameID();

    /**
     * Set the NameID of the request.
     * 
     * @param newNameID the new NameID of the request
     */
    void setNameID(@Nullable final NameID newNameID);

    /**
     * Get the EncryptedID of the request.
     * 
     * @return the EncryptedID of the request
     */
    @Nullable EncryptedID getEncryptedID();

    /**
     * Set the EncryptedID of the request.
     * 
     * @param newEncryptedID the new EncryptedID of the request
     */
    void setEncryptedID(@Nullable final EncryptedID newEncryptedID);

    /**
     * Get the NewID of the request.
     * 
     * @return the NewID of the request
     */
    @Nullable NewID getNewID();

    /**
     * Set the NewID of the request.
     * 
     * @param newNewID the new NewID of the request
     */
    void setNewID(@Nullable final NewID newNewID);

    /**
     * Get the NewEncryptedID of the request.
     * 
     * @return the NewEncryptedID of the request
     */
    @Nullable NewEncryptedID getNewEncryptedID();

    /**
     * Set the NewEncryptedID of the request.
     * 
     * @param newNewEncryptedID the new NewEncryptedID of the request
     */
    void setNewEncryptedID(@Nullable final NewEncryptedID newNewEncryptedID);

    /**
     * Get the Terminate of the request.
     * 
     * @return the Terminate of the request
     */
    @Nullable Terminate getTerminate();

    /**
     * Set the Terminate of the request.
     * 
     * @param newTerminate the new NewID Terminate of the request
     */
    void setTerminate(@Nullable final Terminate newTerminate);

}