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

package org.opensaml.saml.ext.saml2mdreqinit;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.Endpoint;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interfaces for SAML 2 Metadata Extension for SSO Service Provider Request Initiation - RequestInitiator element.
 */
public interface RequestInitiator extends Endpoint {
    
    /** Element local name. */
    @Nonnull @NotEmpty static final String DEFAULT_ELEMENT_LOCAL_NAME = "RequestInitiator";

    /** Default element name. */
    @Nonnull static final QName DEFAULT_ELEMENT_NAME =
        new QName(SAMLConstants.SAML20MDRI_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.SAML20MDRI_PREFIX);
    
    /** Per the extension specification, the value of the 'Binding' attribute MUST be set to this fixed value. */
    @Nonnull @NotEmpty
    static final String REQUIRED_BINDING_VALUE = "urn:oasis:names:tc:SAML:profiles:SSO:request-init";

}
