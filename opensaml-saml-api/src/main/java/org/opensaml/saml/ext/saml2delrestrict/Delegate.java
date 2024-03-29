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

package org.opensaml.saml.ext.saml2delrestrict;

import java.time.Instant;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.BaseID;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.NameID;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Condition for Delegation Restriction - Delegate element.
 */
public interface Delegate extends SAMLObject {
    
    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "Delegate";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
        new QName(SAMLConstants.SAML20DEL_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20DEL_PREFIX);
    
    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "DelegateType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
        new QName(SAMLConstants.SAML20DEL_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML20DEL_PREFIX);
    
    /** DelegationInstant attribute name. */
    @Nonnull @NotEmpty static final String DELEGATION_INSTANT_ATTRIB_NAME = "DelegationInstant";

    /** DelegationInstant attribute QName. */
    @Nonnull static final QName DELEGATION_INSTANT_ATTRIB_QNAME =
            new QName(null, DELEGATION_INSTANT_ATTRIB_NAME, XMLConstants.DEFAULT_NS_PREFIX);
    
    /** ConfirmationMethod attribute name. */
    @Nonnull @NotEmpty static final String CONFIRMATION_METHOD_ATTRIB_NAME = "ConfirmationMethod";    

    /**
     * Gets the BaseID child element of the delegate.
     * 
     * @return the base identifier of the delegate
     */
    @Nullable BaseID getBaseID();

    /**
     * Sets the BaseID child element of the delegate.
     * 
     * @param newBaseID the base identifier of the delegate
     */
    void setBaseID(@Nullable  final BaseID newBaseID);

    /**
     * Gets the NameID child element of the delegate.
     * 
     * @return the name identifier of the principal for this request
     */
    @Nullable NameID getNameID();

    /**
     * Sets the NameID child element of the delegate.
     * 
     * @param newNameID the name identifier of the delegate
     */
    void setNameID(@Nullable final NameID newNameID);

    /**
     * Gets the EncryptedID child element of the delegate.
     * 
     * @return the encrypted name identifier of the delegate
     */
    @Nullable EncryptedID getEncryptedID();

    /**
     * Sets the EncryptedID child element of the delegate.
     * 
     * @param newEncryptedID the new encrypted name identifier of the delegate
     */
    void setEncryptedID(@Nullable final EncryptedID newEncryptedID);
    
    /**
     * Get the delegation instant attribute value.
     * 
     * @return the delegation instant
     */
    @Nullable Instant getDelegationInstant();
    
    /**
     * Set the delegation instant attribute value.
     * 
     * @param newInstant the new delegation instant
     */
    void setDelegationInstant(@Nullable final Instant newInstant);
    
    /**
     * Get the confirmation method attribute value.
     * 
     * @return the confirmation method
     */
    @Nullable String getConfirmationMethod();
    
    /**
     * Set the confirmation method attribute value.
     * 
     * @param newMethod the new confirmation method
     */
    void setConfirmationMethod(@Nullable final String newMethod);

}
