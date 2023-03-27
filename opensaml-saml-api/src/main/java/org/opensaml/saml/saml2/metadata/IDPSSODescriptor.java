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

package org.opensaml.saml.saml2.metadata;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Attribute;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Metadata IDPSSODescriptorType.
 */
public interface IDPSSODescriptor extends SSODescriptor {

    /** Local name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "IDPSSODescriptor";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "IDPSSODescriptorType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MD_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** "WantAuthnRequestSigned" attribute name. */
    @Nonnull @NotEmpty static final String WANT_AUTHN_REQ_SIGNED_ATTRIB_NAME = "WantAuthnRequestsSigned";

    /**
     * Checks if the IDP SSO service wants authentication requests signed.
     * 
     * @return true is signing is desired, false if not
     */
    @Nullable Boolean getWantAuthnRequestsSigned();

    /**
     * Checks if the IDP SSO service wants authentication requests signed.
     * 
     * @return true is signing is desired, false if not
     */
    @Nullable XSBooleanValue getWantAuthnRequestsSignedXSBoolean();

    /**
     * Sets whether the IDP SSO service wants authentication requests signed. Boolean values will be marshalled to
     * either "true" or "false".
     * 
     * @param newWantSigned true if request should be signed, false if not
     */
    void setWantAuthnRequestsSigned(@Nullable final Boolean newWantSigned);

    /**
     * Sets whether the IDP SSO service wants authentication requests signed.
     * 
     * @param newWantSigned true if request should be signed, false if not
     */
    void setWantAuthnRequestsSigned(@Nullable final XSBooleanValue newWantSigned);

    /**
     * Gets the list of single sign on services for this IDP.
     * 
     * @return list of single sign on services
     */
    @Nonnull @Live List<SingleSignOnService> getSingleSignOnServices();

    /**
     * Gets the list of NameID mapping services for this service.
     * 
     * @return the list of NameID mapping services for this service
     */
    @Nonnull @Live List<NameIDMappingService> getNameIDMappingServices();

    /**
     * Gets the list of assertion ID request services.
     * 
     * @return assertion ID request services
     */
    @Nonnull @Live List<AssertionIDRequestService> getAssertionIDRequestServices();

    /**
     * Gets the list of attribute profiles supported by this IdP.
     * 
     * @return attribute profiles supported by this IdP
     */
    @Nonnull @Live List<AttributeProfile> getAttributeProfiles();

    /**
     * Gets the list of attributes supported by this IdP.
     * 
     * @return attributes supported by this IdP
     */
    @Nonnull @Live List<Attribute> getAttributes();
}