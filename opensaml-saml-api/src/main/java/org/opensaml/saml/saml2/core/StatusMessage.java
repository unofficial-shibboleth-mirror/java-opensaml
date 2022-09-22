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
import net.shibboleth.utilities.java.support.primitive.DeprecationSupport;
import net.shibboleth.utilities.java.support.primitive.DeprecationSupport.ObjectType;

/**
 * SAML 2.0 Core StatusMessage.
 */
public interface StatusMessage extends SAMLObject, XSString {

    /** Local Name of StatusMessage. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "StatusMessage";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20P_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20P_PREFIX);

    /**
     * Gets the Message of this Status Message.
     * 
     * @return StatusMessage message
     */
    @Deprecated(forRemoval=true, since="4.0.0")
    @Nullable default String getMessage() {
        DeprecationSupport.warn(ObjectType.METHOD, "getMessage", StatusMessage.class.toString(), "getValue");
        return getValue();
    }

    /**
     * Sets the Message of this Status Message.
     * 
     * @param value the Message of this Status Message
     */
    @Deprecated(forRemoval=true, since="4.0.0")
    default void setMessage(@Nullable final String value) {
        DeprecationSupport.warn(ObjectType.METHOD, "setMessage", StatusMessage.class.toString(), "setValue");
        setValue(value);
    }

}