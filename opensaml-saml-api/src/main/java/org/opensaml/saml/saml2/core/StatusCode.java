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

package org.opensaml.saml.saml2.core;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Core StatusCode.
 */
public interface StatusCode extends SAMLObject {

    /** Local Name of StatusCode. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "StatusCode";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20P_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20P_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "StatusCodeType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20P_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20P_PREFIX);

    /** Local Name of the Value attribute. */
    @Nonnull @NotEmpty static final String VALUE_ATTRIB_NAME = "Value";

    /** URI for Success status code. */
    @Nonnull @NotEmpty static final String SUCCESS = "urn:oasis:names:tc:SAML:2.0:status:Success";

    /** URI for Requester status code. */
    @Nonnull @NotEmpty static final String REQUESTER = "urn:oasis:names:tc:SAML:2.0:status:Requester";

    /** URI for Responder status code. */
    @Nonnull @NotEmpty static final String RESPONDER = "urn:oasis:names:tc:SAML:2.0:status:Responder";

    /** URI for VersionMismatch status code. */
    @Nonnull @NotEmpty static final String VERSION_MISMATCH = "urn:oasis:names:tc:SAML:2.0:status:VersionMismatch";

    /** URI for AuthnFailed status code. */
    @Nonnull @NotEmpty static final String AUTHN_FAILED = "urn:oasis:names:tc:SAML:2.0:status:AuthnFailed";

    /** URI for InvalidAttrNameOrValue status code. */
    @Nonnull @NotEmpty
    static final String INVALID_ATTR_NAME_OR_VALUE = "urn:oasis:names:tc:SAML:2.0:status:InvalidAttrNameOrValue";

    /** URI for InvalidNameIDPolicy status code. */
    @Nonnull @NotEmpty
    static final String INVALID_NAMEID_POLICY = "urn:oasis:names:tc:SAML:2.0:status:InvalidNameIDPolicy";

    /** URI for NoAuthnContext status code. */
    @Nonnull @NotEmpty static final String NO_AUTHN_CONTEXT = "urn:oasis:names:tc:SAML:2.0:status:NoAuthnContext";

    /** URI for NoAvailableIDP status code. */
    @Nonnull @NotEmpty static final String NO_AVAILABLE_IDP = "urn:oasis:names:tc:SAML:2.0:status:NoAvailableIDP";

    /** URI for NoPassive status code. */
    @Nonnull @NotEmpty static final String NO_PASSIVE = "urn:oasis:names:tc:SAML:2.0:status:NoPassive";

    /** URI for NoSupportedIDP status code. */
    @Nonnull @NotEmpty static final String NO_SUPPORTED_IDP = "urn:oasis:names:tc:SAML:2.0:status:NoSupportedIDP";

    /** URI for PartialLogout status code. */
    @Nonnull @NotEmpty static final String PARTIAL_LOGOUT = "urn:oasis:names:tc:SAML:2.0:status:PartialLogout";

    /** URI for ProxyCountExceeded status code. */
    @Nonnull @NotEmpty
    static final String PROXY_COUNT_EXCEEDED = "urn:oasis:names:tc:SAML:2.0:status:ProxyCountExceeded";

    /** URI for RequestDenied status code. */
    @Nonnull @NotEmpty static final String REQUEST_DENIED = "urn:oasis:names:tc:SAML:2.0:status:RequestDenied";

    /** URI for RequestUnsupported status code. */
    @Nonnull @NotEmpty
    static final String REQUEST_UNSUPPORTED = "urn:oasis:names:tc:SAML:2.0:status:RequestUnsupported";

    /** URI for RequestVersionDeprecated status code. */
    @Nonnull @NotEmpty
    static final String REQUEST_VERSION_DEPRECATED = "urn:oasis:names:tc:SAML:2.0:status:RequestVersionDeprecated";

    /** URI for RequestVersionTooHigh status code. */
    @Nonnull @NotEmpty
    static final String REQUEST_VERSION_TOO_HIGH = "urn:oasis:names:tc:SAML:2.0:status:RequestVersionTooHigh";
    
    /** URI for RequestVersionTooLow status code. */
    @Nonnull @NotEmpty
    static final String REQUEST_VERSION_TOO_LOW = "urn:oasis:names:tc:SAML:2.0:status:RequestVersionTooLow";

    /** URI for ResourceNotRecognized status code. */
    @Nonnull @NotEmpty
    static final String RESOURCE_NOT_RECOGNIZED = "urn:oasis:names:tc:SAML:2.0:status:ResourceNotRecognized";

    /** URI for TooManyResponses status code. */
    @Nonnull @NotEmpty static final String TOO_MANY_RESPONSES = "urn:oasis:names:tc:SAML:2.0:status:TooManyResponses";

    /** URI for UnknownAttrProfile status code. */
    @Nonnull @NotEmpty
    static final String UNKNOWN_ATTR_PROFILE = "urn:oasis:names:tc:SAML:2.0:status:UnknownAttrProfile";

    /** URI for UnknownPrincipal status code. */
    @Nonnull @NotEmpty static final String UNKNOWN_PRINCIPAL = "urn:oasis:names:tc:SAML:2.0:status:UnknownPrincipal";

    /** URI for UnsupportedBinding status code. */
    @Nonnull @NotEmpty
    static final String UNSUPPORTED_BINDING = "urn:oasis:names:tc:SAML:2.0:status:UnsupportedBinding";

    /**
     * Gets the Status Code of this Status Code.
     * 
     * @return StatusCode StatusCode
     */
    @Nullable StatusCode getStatusCode();

    /**
     * Sets the Status Code of this Status Code.
     * 
     * @param newStatusCode the Status Code of this Status Code.
     */
    void setStatusCode(@Nullable final StatusCode newStatusCode);

    /**
     * Gets the Value of this Status Code.
     * 
     * @return StatusCode Value
     */
    @Nullable String getValue();

    /**
     * Sets the Value of this Status Code.
     * 
     * @param newValue the Value of this Status Code
     */
    void setValue(@Nullable final String newValue);

}