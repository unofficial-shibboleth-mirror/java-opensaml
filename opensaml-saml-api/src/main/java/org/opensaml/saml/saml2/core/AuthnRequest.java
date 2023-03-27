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

package org.opensaml.saml.saml2.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Core AuthnRequest.
 */
public interface AuthnRequest extends RequestAbstractType {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "AuthnRequest";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20P_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20P_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "AuthnRequestType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20P_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20P_PREFIX);

    /** ForceAuthn attribute name. */
    @Nonnull @NotEmpty static final String FORCE_AUTHN_ATTRIB_NAME = "ForceAuthn";

    /** IsPassive attribute name. */
    @Nonnull @NotEmpty static final String IS_PASSIVE_ATTRIB_NAME = "IsPassive";

    /** ProtocolBinding attribute name. */
    @Nonnull @NotEmpty static final String PROTOCOL_BINDING_ATTRIB_NAME = "ProtocolBinding";

    /** AssertionConsumerServiceIndex attribute name. */
    @Nonnull @NotEmpty
    static final String ASSERTION_CONSUMER_SERVICE_INDEX_ATTRIB_NAME = "AssertionConsumerServiceIndex";

    /** AssertionConsumerServiceURL attribute name. */
    @Nonnull @NotEmpty static final String ASSERTION_CONSUMER_SERVICE_URL_ATTRIB_NAME = "AssertionConsumerServiceURL";

    /** AttributeConsumingServiceIndex attribute name. */
    @Nonnull @NotEmpty
    static final String ATTRIBUTE_CONSUMING_SERVICE_INDEX_ATTRIB_NAME = "AttributeConsumingServiceIndex";

    /** ProviderName attribute name. */
    @Nonnull @NotEmpty static final String PROVIDER_NAME_ATTRIB_NAME = "ProviderName";

    /**
     * Gets whether the IdP should force the user to reauthenticate.
     * 
     * @return whether the IdP should force the user to reauthenticate
     */
    @Nullable Boolean isForceAuthn();

    /**
     * Gets whether the IdP should force the user to reauthenticate.
     * 
     * @return whether the IdP should force the user to reauthenticate
     */
    @Nullable XSBooleanValue isForceAuthnXSBoolean();

    /**
     * Sets whether the IdP should force the user to reauthenticate. Boolean values will be marshalled to either "true"
     * or "false".
     * 
     * @param newForceAuthn whether the IdP should force the user to reauthenticate
     */
    void setForceAuthn(@Nullable final Boolean newForceAuthn);

    /**
     * Sets whether the IdP should force the user to reauthenticate.
     * 
     * @param newForceAuthn whether the IdP should force the user to reauthenticate
     */
    void setForceAuthn(@Nullable final XSBooleanValue newForceAuthn);

    /**
     * Gets whether the IdP should refrain from interacting with the user during the authentication process.
     * 
     * @return whether the IdP should refrain from interacting with the user during the authentication process
     */
    @Nullable Boolean isPassive();

    /**
     * Gets whether the IdP should refrain from interacting with the user during the authentication process.
     * 
     * @return whether the IdP should refrain from interacting with the user during the authentication process
     */
    @Nullable XSBooleanValue isPassiveXSBoolean();

    /**
     * Sets whether the IdP should refrain from interacting with the user during the authentication process. Boolean
     * values will be marshalled to either "true" or "false".
     * 
     * @param newIsPassive whether the IdP should refrain from interacting with the user during the authentication
     *            process
     */
    void setIsPassive(@Nullable final Boolean newIsPassive);

    /**
     * Sets whether the IdP should refrain from interacting with the user during the authentication process.
     * 
     * @param newIsPassive whether the IdP should refrain from interacting with the user during the authentication
     *            process
     */
    void setIsPassive(@Nullable final XSBooleanValue newIsPassive);

    /**
     * Gets the protocol binding URI for the request.
     * 
     * @return the value of the ProtocolBinding attribute
     */
    @Nullable String getProtocolBinding();

    /**
     * Sets the protocol binding URI for the request.
     * 
     * @param newProtocolBinding the new value of the ProtocolBinding attribute
     */
    void setProtocolBinding(@Nullable final String newProtocolBinding);

    /**
     * Gets the index of the particular Assertion Consumer Service to which the response to this request should be
     * delivered.
     * 
     * @return the value of the AssertionConsumerServiceIndex attribute
     */
    @Nullable Integer getAssertionConsumerServiceIndex();

    /**
     * Sets the index of the particular Assertion Consumer Service to which the response to this request should be
     * delivered.
     * 
     * @param newAssertionConsumerServiceIndex the new value of the AssertionConsumerServiceIndex attribute
     */
    void setAssertionConsumerServiceIndex(@Nullable final Integer newAssertionConsumerServiceIndex);

    /**
     * Gets the URL of the particular Assertion Consumer Service to which the response to this request should be
     * delivered.
     * 
     * @return the value of the AssertionConsumerServiceURL attribute
     */
    @Nullable String getAssertionConsumerServiceURL();

    /**
     * Sets the URL of the particular Assertion Consumer Service to which the response to this request should be
     * delivered.
     * 
     * @param newAssertionConsumerServiceURL the new value of the AssertionConsumerServiceURL attribute
     */
    void setAssertionConsumerServiceURL(@Nullable final String newAssertionConsumerServiceURL);

    /**
     * Gets the index of the Attribute Consuming Service which describes the SAML attributes the requester desires or
     * requires to be supplied in the <code>Response</code> message.
     * 
     * 
     * @return the value of the AssertionConsumerServiceIndex attribute
     */
    @Nullable Integer getAttributeConsumingServiceIndex();

    /**
     * 
     * Sets the index of the Attribute Consuming Service which describes the SAML attributes the requester desires or
     * requires to be supplied in the <code>Response</code> message.
     * 
     * @param newAttributeConsumingServiceIndex the new value of the AttributeConsumingServiceIndex attribute
     */
    void setAttributeConsumingServiceIndex(@Nullable final Integer newAttributeConsumingServiceIndex);

    /**
     * Gets the human-readable name of the requester for use by the presenter's user agent or the identity provider.
     * 
     * @return the value of the ProviderName attribute
     */
    @Nullable String getProviderName();

    /**
     * Sets the human-readable name of the requester for use by the presenter's user agent or the identity provider.
     * 
     * @param newProviderName the new value of the ProviderName attribute
     */
    void setProviderName(@Nullable final String newProviderName);

    /**
     * Gets the {@link Subject} of the request.
     * 
     * @return the Subject of the request
     */
    @Nullable Subject getSubject();

    /**
     * Sets the {@link Subject} of the request.
     * 
     * @param newSubject the new value of the Subject of the request
     */
    void setSubject(@Nullable final Subject newSubject);

    /**
     * Gets the {@link NameIDPolicy} of the request.
     * 
     * @return the NameIDPolicy of the request
     */
    @Nullable NameIDPolicy getNameIDPolicy();

    /**
     * Sets the {@link NameIDPolicy} of the request.
     * 
     * @param newNameIDPolicy the new value of the NameIDPolicy of the request
     */
    void setNameIDPolicy(@Nullable final NameIDPolicy newNameIDPolicy);

    /**
     * Gets the {@link Conditions} of the request.
     * 
     * @return the Conditions of the request
     */
    @Nullable Conditions getConditions();

    /**
     * Sets the {@link Conditions} of the request.
     * 
     * @param newConditions the new value of the Conditions of the request
     */
    void setConditions(@Nullable final Conditions newConditions);

    /**
     * Gets the {@link RequestedAuthnContext} of the request.
     * 
     * @return the RequestedAuthnContext of the request
     */
    @Nullable RequestedAuthnContext getRequestedAuthnContext();

    /**
     * Sets the {@link RequestedAuthnContext} of the request.
     * 
     * @param newRequestedAuthnContext the new value of the RequestedAuthnContext of the request
     */
    void setRequestedAuthnContext(@Nullable final RequestedAuthnContext newRequestedAuthnContext);

    /**
     * Gets the {@link Scoping} of the request.
     * 
     * @return the Scoping of the request
     */
    @Nullable Scoping getScoping();

    /**
     * Sets the {@link Scoping} of the request.
     * 
     * @param newScoping the new value of the Scoping of the request
     */
    void setScoping(@Nullable final Scoping newScoping);

}