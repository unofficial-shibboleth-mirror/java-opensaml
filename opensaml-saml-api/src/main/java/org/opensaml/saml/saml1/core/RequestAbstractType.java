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

package org.opensaml.saml.saml1.core;

import java.time.Instant;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.SignableSAMLObject;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * This interface describes the base class for types derived from <code> RequestAbstractType </code>.
 */
public interface RequestAbstractType extends SignableSAMLObject {

    /** Name for the attribute which defines the MajorVersion (which must be "1". */
    @Nonnull @NotEmpty static final String MAJORVERSION_ATTRIB_NAME = "MajorVersion";

    /** Name for the attribute which defines the MinorVersion. */
    @Nonnull @NotEmpty static final String MINORVERSION_ATTRIB_NAME = "MinorVersion";

    /** Name for the attribute which defines the IssueInstant. */
    @Nonnull @NotEmpty static final String ISSUEINSTANT_ATTRIB_NAME = "IssueInstant";
    
    /** QName for the attribute which defines the IssueInstant. */
    @Nonnull static final QName ISSUEINSTANT_ATTRIB_QNAME =
            new QName(null, "IssueInstant", XMLConstants.DEFAULT_NS_PREFIX);
    
    /** Name for the attribute which defines the Issue Instant. */
    @Nonnull @NotEmpty static final String ID_ATTRIB_NAME = "RequestID";

    /**
     * Gets the SAML version of this message.
     * 
     * @return the SAML version of this message
     */
    @Nullable SAMLVersion getVersion();

    /**
     * Sets the SAML version of this message.
     * 
     * @param version SAML version of this message
     */
    void setVersion(@Nullable final SAMLVersion version);

    /**
     * Get the issue instant.
     * 
     * @return the issue instant
     */
    @Nullable Instant getIssueInstant();

    /**
     * Set the issue instant.
     * 
     * @param date what to set
     */
    void setIssueInstant(@Nullable final Instant date);

    /**
     * Get the ID.
     * 
     * @return the ID
     */
    @Nullable String getID();

    /**
     * Set the ID.
     * 
     * @param id what to set
     */
    void setID(@Nullable final String id);

    /**
     * Return the list of RespondWith elements.
     * 
     * @return the list of RespondWith elements
     */
    @Nonnull @Live List<RespondWith> getRespondWiths();
}