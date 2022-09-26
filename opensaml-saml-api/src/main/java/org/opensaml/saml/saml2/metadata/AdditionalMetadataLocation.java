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

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.DeprecationSupport;
import net.shibboleth.shared.primitive.DeprecationSupport.ObjectType;

/**
 * SAML 2.0 Metadata AdditionalMetadataLocation.
 */
public interface AdditionalMetadataLocation extends SAMLObject, XSURI {

    /** Element name, no namespace. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "AdditionalMetadataLocation";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.SAML20MD_NS, DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "AdditionalMetadataLocationType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME = new QName(SAMLConstants.SAML20MD_NS, TYPE_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);

    /** "affiliationOwnerID" attribute's local name. */
    @Nonnull @NotEmpty static final String NAMESPACE_ATTRIB_NAME = "namespace";

    /**
     * Gets the location URI.
     * 
     * @return the location URI
     */
    @Deprecated(forRemoval=true, since="4.0.0")
    @Nullable default String getLocationURI() {
        DeprecationSupport.warn(ObjectType.METHOD, "getLocationURI", AdditionalMetadataLocation.class.toString(),
                "getURI");
        return getURI();
    }

    /**
     * Sets the location URI.
     * 
     * @param uri the location URI
     */
    @Deprecated(forRemoval=true, since="4.0.0")
    default void setLocationURI(@Nullable final String uri) {
        DeprecationSupport.warn(ObjectType.METHOD, "setLocationURI", AdditionalMetadataLocation.class.toString(),
                "setURI");
        setURI(uri);
    }

    /**
     * Gets the namespace URI.
     * 
     * @return the namespace URI
     */
    @Nullable String getNamespaceURI();

    /**
     * Sets the namespace URI.
     * 
     * @param namespaceURI the namespace URI
     */
    void setNamespaceURI(@Nullable final String namespaceURI);

}