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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSURI;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.primitive.DeprecationSupport;
import net.shibboleth.utilities.java.support.primitive.DeprecationSupport.ObjectType;

/**
 * SAML 2.0 Metadata NameIDFormat.
 */
public interface NameIDFormat extends SAMLObject, XSURI {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "NameIDFormat";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /**
     * Gets the format of the NameID.
     * 
     * @return the format of the NameID
     */
    @Deprecated(forRemoval=true, since="4.0.0")
    @Nullable default String getFormat() {
        DeprecationSupport.warn(ObjectType.METHOD, "getFormat", NameIDFormat.class.toString(), "getURI");
        return getURI();
    }

    /**
     * Sets the format of the NameID.
     * 
     * @param uri the format of the NameID
     */
    @Deprecated(forRemoval=true, since="4.0.0")
    default void setFormat(@Nullable final String uri) {
        DeprecationSupport.warn(ObjectType.METHOD, "setFormat", NameIDFormat.class.toString(), "setURI");
        setURI(uri);
    }
    
}