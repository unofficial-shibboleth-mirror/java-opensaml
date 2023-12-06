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

import java.time.Instant;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Core LogoutRequest.
 */
public interface LogoutRequest extends RequestAbstractType, LogoutMessage {
    
    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "LogoutRequest";
    
    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = 
        new QName(SAMLConstants.SAML20P_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty  static final String TYPE_LOCAL_NAME = "LogoutRequestType"; 
        
    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = 
        new QName(SAMLConstants.SAML20P_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML20P_PREFIX);
    
    /** Reason attribute name. */
    @Nonnull @NotEmpty static final String REASON_ATTRIB_NAME = "Reason";
    
    /** NotOnOrAfter attribute name. */
    @Nonnull @NotEmpty static final String NOT_ON_OR_AFTER_ATTRIB_NAME = "NotOnOrAfter";

    /** QName for the NotOnOrAfter attribute. */
    @Nonnull static final QName NOT_ON_OR_AFTER_ATTRIB_QNAME =
            new QName(null, "NotOnOrAfter", XMLConstants.DEFAULT_NS_PREFIX);
    
    /** User-initiated logout reason. */
    @Nonnull @NotEmpty static final String USER_REASON = "urn:oasis:names:tc:SAML:2.0:logout:user";

    /** Admin-initiated logout reason. */
    @Nonnull @NotEmpty static final String ADMIN_REASON = "urn:oasis:names:tc:SAML:2.0:logout:admin";
    
    /** Global timeout logout reason. */
    @Nonnull @NotEmpty static final String GLOBAL_TIMEOUT_REASON = "urn:oasis:names:tc:SAML:2.0:logout:global-timeout";
    
    /** SP timeout logout reason. */
    @Nonnull @NotEmpty static final String SP_TIMEOUT_REASON = "urn:oasis:names:tc:SAML:2.0:logout:sp-timeout";
    
    /**
     * Get the Reason attrib value of the request.
     * 
     * @return the Reason value of the request
     */
    @Nullable String getReason();

    /**
     * Set the Reason attrib value of the request.
     * 
     * @param newReason the new Reason value of the request
     */
    void setReason(@Nullable final String newReason);
    
    /**
     * Get the NotOnOrAfter attrib value of the request.
     * 
     * @return the NotOnOrAfter value of the request
     */
    @Nullable Instant getNotOnOrAfter();

    /**
     * Set the NotOnOrAfter attrib value of the request.
     * 
     * @param newNotOnOrAfter the new NotOnOrAfter value of the request
     */
    void setNotOnOrAfter(@Nullable final Instant newNotOnOrAfter);
    
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
     * Gets the encrytped name identifier of the principal for this request.
     * 
     * @return the encrytped name identifier of the principal for this request
     */
    @Nullable EncryptedID getEncryptedID();
    
    /**
     * Sets the encrypted name identifier of the principal for this request.
     * 
     * @param newEncryptedID the new encrypted name identifier of the principal for this request
     */
    void setEncryptedID(@Nullable final EncryptedID newEncryptedID);
       
    /**
     *  Get the list of SessionIndexes for the request.
     * 
     * 
     * @return the list of SessionIndexes
     */
    @Nonnull @Live List<SessionIndex> getSessionIndexes();

}