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

import java.time.Instant;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Core Conditions.
 */
public interface Conditions extends SAMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "Conditions";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "ConditionsType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** NotBefore attribute name. */
    @Nonnull @NotEmpty static final String NOT_BEFORE_ATTRIB_NAME = "NotBefore";

    /** QName for the NotBefore attribute. */
    @Nonnull static final QName NOT_BEFORE_ATTRIB_QNAME = new QName(null, "NotBefore", XMLConstants.DEFAULT_NS_PREFIX);

    /** Name for the NotOnOrAfter attribute. */
    @Nonnull @NotEmpty static final String NOT_ON_OR_AFTER_ATTRIB_NAME = "NotOnOrAfter";

    /** QName for the NotOnOrAfter attribute. */
    @Nonnull static final QName NOT_ON_OR_AFTER_ATTRIB_QNAME =
            new QName(null, "NotOnOrAfter", XMLConstants.DEFAULT_NS_PREFIX);

    /**
     * Get the date/time before which the assertion is invalid.
     * 
     * @return the date/time before which the assertion is invalid
     */
    @Nullable Instant getNotBefore();

    /**
     * Sets the date/time before which the assertion is invalid.
     * 
     * @param newNotBefore the date/time before which the assertion is invalid
     */
    void setNotBefore(@Nullable final Instant newNotBefore);

    /**
     * Gets the date/time on, or after, which the assertion is invalid.
     * 
     * @return the date/time on, or after, which the assertion is invalid
     */
    @Nullable Instant getNotOnOrAfter();

    /**
     * Sets the date/time on, or after, which the assertion is invalid.
     * 
     * @param newNotOnOrAfter the date/time on, or after, which the assertion is invalid
     */
    void setNotOnOrAfter(@Nullable final Instant newNotOnOrAfter);

    /**
     * Gets all the conditions on the assertion.
     * 
     * @return all the conditions on the assertion
     */
    @Nonnull @Live List<Condition> getConditions();

    /**
     * Gets the list of conditions that match a particular QName.
     * 
     * @param typeOrName the QName of the conditions to return
     * 
     * @return the list of conditions that match the specified QName
     */
    @Nonnull @Live List<Condition> getConditions(@Nonnull final QName typeOrName);

    /**
     * Gets the audience restriction conditions for the assertion.
     * 
     * @return the audience restriction conditions for the assertion
     */
    @Nonnull @Live List<AudienceRestriction> getAudienceRestrictions();

    /**
     * Gets the OneTimeUse condition for the assertion.
     * 
     * @return the OneTimeUse condition for the assertion
     */
    @Nullable OneTimeUse getOneTimeUse();

    /**
     * Gets the ProxyRestriction condition for the assertion.
     * 
     * @return the ProxyRestriction condition for the assertion
     */
    @Nullable ProxyRestriction getProxyRestriction();
}