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

package org.opensaml.saml.ext.saml2cb;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.schema.XSBase64Binary;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.soap.soap11.ActorBearing;
import org.opensaml.soap.soap11.MustUnderstandBearing;

import net.shibboleth.shared.annotation.constraint.NotEmpty;


/**
 * SAML 2.0 Channel Bindings Extensions ChannelBinding element.
 */
public interface ChannelBindings extends XSBase64Binary, MustUnderstandBearing, ActorBearing, SAMLObject {

    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "ChannelBindings";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
            new QName(SAMLConstants.SAML20CB_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20CB_PREFIX);

    /** Local name of the XSI type. */
    @Nonnull @NotEmpty static final String TYPE_LOCAL_NAME = "ChannelBindingsType";

    /** QName of the XSI type. */
    @Nonnull static final QName TYPE_NAME =
            new QName(SAMLConstants.SAML20CB_NS, TYPE_LOCAL_NAME, SAMLConstants.SAML20CB_PREFIX);

    /** Type attribute name. */
    @Nonnull @NotEmpty static final String TYPE_ATTRIB_NAME = "Type";

    /**
     * Get the Type attribute value.
     * 
     * @return the Type attribute value
     */
    @Nullable String getType();
    
    /**
     * Set the Type attribute value.
     * 
     * @param newType the new Type attribute value
     */
    void setType(@Nullable final String newType);
}
