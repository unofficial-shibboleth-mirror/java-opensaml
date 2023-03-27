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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.AttributeExtensibleXMLObject;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Core Attribute.
 */
public interface Attribute extends SAMLObject, AttributeExtensibleXMLObject {

    /** Local name of the Attribute element. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "Attribute";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "AttributeType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** Name of the Name attribute. */
    @Nonnull @NotEmpty static final String NAME_ATTTRIB_NAME = "Name";

    /** Name for the NameFormat attribute. */
    @Nonnull @NotEmpty static final String NAME_FORMAT_ATTRIB_NAME = "NameFormat";

    /** Name of the FriendlyName attribute. */
    @Nonnull @NotEmpty static final String FRIENDLY_NAME_ATTRIB_NAME = "FriendlyName";

    /** Unspecified attribute format ID. */
    @Nonnull @NotEmpty static final String UNSPECIFIED = "urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified";

    /** URI reference attribute format ID. */
    @Nonnull @NotEmpty static final String URI_REFERENCE = "urn:oasis:names:tc:SAML:2.0:attrname-format:uri";

    /** Basic attribute format ID. */
    @Nonnull @NotEmpty static final String BASIC = "urn:oasis:names:tc:SAML:2.0:attrname-format:basic";

    /**
     * Get the name of this attribute.
     * 
     * @return the name of this attribute
     */
    @Nullable String getName();

    /**
     * Sets the name of this attribute.
     * 
     * @param name the name of this attribute
     */
    void setName(@Nullable final String name);

    /**
     * Get the name format of this attribute.
     * 
     * @return the name format of this attribute
     */
    @Nullable String getNameFormat();

    /**
     * Sets the name format of this attribute.
     * 
     * @param nameFormat the name format of this attribute
     */
    void setNameFormat(@Nullable final String nameFormat);

    /**
     * Get the friendly name of this attribute.
     * 
     * @return the friendly name of this attribute
     */
    @Nullable String getFriendlyName();

    /**
     * Sets the friendly name of this attribute.
     * 
     * @param friendlyName the friendly name of this attribute
     */
    void setFriendlyName(@Nullable final String friendlyName);

    /**
     * Gets the list of attribute values for this attribute.
     * 
     * @return the list of attribute values for this attribute
     */
    @Nonnull @Live List<XMLObject> getAttributeValues();
}