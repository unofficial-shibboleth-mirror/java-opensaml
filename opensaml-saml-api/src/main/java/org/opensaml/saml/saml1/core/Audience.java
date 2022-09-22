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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSURI;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.primitive.DeprecationSupport;
import net.shibboleth.utilities.java.support.primitive.DeprecationSupport.ObjectType;

/**
 * This interface describes how an object representing a SAML1 <code>Audience</code> element will behave.
 */
public interface Audience extends SAMLObject, XSURI {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "Audience";
    
    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML1_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML1_PREFIX);
    
    /**
     * Gets the URI of the audience for the assertion.
     * 
     * @return the URI of the audience for the assertion
     */
    @Deprecated(forRemoval=true, since="4.0.0")
    @Nullable default String getUri() {
        DeprecationSupport.warn(ObjectType.METHOD, "getUri", Audience.class.toString(), "getURI");
        return getURI();
    }

    /**
     * Sets the URI of the audience for the assertion.
     * 
     * @param uri the URI of the audience for the assertion
     */
    @Deprecated(forRemoval=true, since="4.0.0")
    default void setUri(@Nullable final String uri) {
        DeprecationSupport.warn(ObjectType.METHOD, "setUri", Audience.class.toString(), "setURI");
        setURI(uri);
    }

}