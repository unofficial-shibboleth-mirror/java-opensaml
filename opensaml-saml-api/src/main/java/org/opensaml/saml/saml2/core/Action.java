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

import org.opensaml.core.xml.schema.XSString;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * SAML 2.0 Core Action.
 */
public interface Action extends SAMLObject, XSString {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "Action";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "ActionType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20_PREFIX);

    /** Name of the Namespace attribute. */
    @Nonnull @NotEmpty static final String NAMEPSACE_ATTRIB_NAME = "Namespace";

    /** Read/Write/Execute/Delete/Control action namespace. */
    @Nonnull @NotEmpty static final String RWEDC_NS_URI = "urn:oasis:names:tc:SAML:1.0:action:rwedc";

    /** Read/Write/Execute/Delete/Control negation action namespace. */
    @Nonnull @NotEmpty static final String RWEDC_NEGATION_NS_URI = "urn:oasis:names:tc:SAML:1.0:action:rwedc-negation";

    /** Get/Head/Put/Post action namespace. */
    @Nonnull @NotEmpty static final String GHPP_NS_URI = "urn:oasis:names:tc:SAML:1.0:action:ghpp";

    /** UNIX file permission action namespace. */
    @Nonnull @NotEmpty static final String UNIX_NS_URI = "urn:oasis:names:tc:SAML:1.0:action:unix";

    /** Read action. */
    @Nonnull @NotEmpty static final String READ_ACTION = "Read";

    /** Write action. */
    @Nonnull @NotEmpty static final String WRITE_ACTION = "Write";

    /** Execute action. */
    @Nonnull @NotEmpty static final String EXECUTE_ACTION = "Execute";

    /** Delete action. */
    @Nonnull @NotEmpty static final String DELETE_ACTION = "Delete";

    /** Control action. */
    @Nonnull @NotEmpty static final String CONTROL_ACTION = "Control";

    /** Negated Read action. */
    @Nonnull @NotEmpty static final String NEG_READ_ACTION = "~Read";

    /** Negated Write action. */
    @Nonnull @NotEmpty static final String NEG_WRITE_ACTION = "~Write";

    /** Negated Execute action. */
    @Nonnull @NotEmpty static final String NEG_EXECUTE_ACTION = "~Execute";

    /** Negated Delete action. */
    @Nonnull @NotEmpty static final String NEG_DELETE_ACTION = "~Delete";

    /** Negated Control action. */
    @Nonnull @NotEmpty static final String NEG_CONTROL_ACTION = "~Control";

    /** HTTP GET action. */
    @Nonnull @NotEmpty static final String HTTP_GET_ACTION = "GET";

    /** HTTP HEAD action. */
    @Nonnull @NotEmpty static final String HTTP_HEAD_ACTION = "HEAD";

    /** HTTP PUT action. */
    @Nonnull @NotEmpty static final String HTTP_PUT_ACTION = "PUT";

    /** HTTP POST action. */
    @Nonnull @NotEmpty static final String HTTP_POST_ACTION = "POST";

    /**
     * Gets the namespace scope of the specified action.
     * 
     * @return the namespace scope of the specified action
     */
    @Nullable String getNamespace();

    /**
     * Sets the namespace scope of the specified action.
     * 
     * @param newNamespace the namespace scope of the specified action
     */
    void setNamespace(@Nullable final String newNamespace);
    
}