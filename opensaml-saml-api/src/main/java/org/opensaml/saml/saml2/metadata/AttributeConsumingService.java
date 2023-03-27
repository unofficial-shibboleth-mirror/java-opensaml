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
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Metadata AttributeAuthorityDescriptor.
 */
public interface AttributeConsumingService extends SAMLObject {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "AttributeConsumingService";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MD_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "AttributeConsumingServiceType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MD_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** "index" attribute's local name. */
    @Nonnull @NotEmpty static final String INDEX_ATTRIB_NAME = "index";

    /** "isDefault" attribute's local name. */
    @Nonnull @NotEmpty static final String IS_DEFAULT_ATTRIB_NAME = "isDefault";

    /**
     * Gets the index for this service.
     * 
     * @return the index for this service
     */
    int getIndex();

    /**
     * Sets the index for this service.
     * 
     * @param index the index for this service
     */
    void setIndex(int index);

    /**
     * Checks if this is the default service for the service provider.
     * 
     * @return true if this is the default service, false if not
     */
    @Nullable Boolean isDefault();

    /**
     * Checks if this is the default service for the service provider.
     * 
     * @return true if this is the default service, false if not
     */
    @Nullable XSBooleanValue isDefaultXSBoolean();

    /**
     * Sets if this is the default service for the service provider. Boolean values will be marshalled to either "true"
     * or "false".
     * 
     * @param newIsDefault true if this is the default service, false if not
     */
    void setIsDefault(@Nullable final Boolean newIsDefault);

    /**
     * Sets if this is the default service for the service provider.
     * 
     * @param newIsDefault true if this is the default service, false if not
     */
    void setIsDefault(@Nullable final XSBooleanValue newIsDefault);

    /**
     * Gets the list of names this service has.
     * 
     * @return list of names this service has
     */
    @Nonnull @Live List<ServiceName> getNames();

    /**
     * Gets the descriptions for this service.
     * 
     * @return descriptions for this service
     */
    @Nonnull @Live List<ServiceDescription> getDescriptions();

    /**
     * Gets the attributes this service requests.
     * 
     * @return attributes this service requests
     */
    @Nonnull @Live List<RequestedAttribute> getRequestedAttributes();
}