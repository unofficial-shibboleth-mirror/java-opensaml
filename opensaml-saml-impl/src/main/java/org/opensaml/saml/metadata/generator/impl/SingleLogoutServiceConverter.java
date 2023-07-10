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

package org.opensaml.saml.metadata.generator.impl;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;

import net.shibboleth.shared.annotation.constraint.Live;

/**
 * Support for parsing a binding/endpoint pair into a {@link SingleLogoutService}.
 * 
 * @since 5.0.0
 */
public class SingleLogoutServiceConverter extends AbstractEndpointConverter<SingleLogoutService> {
    
    /**
     * Constructor.
     */
    public SingleLogoutServiceConverter() {
        super((SAMLObjectBuilder<SingleLogoutService>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<SingleLogoutService>ensureBuilder(
                        SingleLogoutService.DEFAULT_ELEMENT_NAME));
    }
    
    /** {@inheritDoc} */
    @Nonnull public SingleLogoutService apply(@Nullable final String value,
            @Nullable @Live final List<String> protocols) {
        return getProcessedEndpoint(protocols, value);
    }
    
}