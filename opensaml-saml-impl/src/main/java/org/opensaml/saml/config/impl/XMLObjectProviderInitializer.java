/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml.config.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.config.AbstractXMLObjectProviderInitializer;

/**
 * XMLObject provider initializer for module "saml-impl".
 */
public class XMLObjectProviderInitializer extends AbstractXMLObjectProviderInitializer {
    
    /** Config resources. */
    @Nonnull private static String[] configs = {
        "/saml1-assertion-config.xml", 
        "/saml1-metadata-config.xml", 
        "/saml1-protocol-config.xml",
        "/saml2-assertion-config.xml", 
        "/saml2-assertion-delegation-restriction-config.xml",    
        "/saml2-ecp-config.xml",
        "/saml2-metadata-algorithm-config.xml",
        "/saml2-metadata-attr-config.xml",
        "/saml2-metadata-config.xml",
        "/saml2-metadata-idp-discovery-config.xml",
        "/saml2-metadata-query-config.xml", 
        "/saml2-metadata-reqinit-config.xml", 
        "/saml2-metadata-ui-config.xml",
        "/saml2-metadata-rpi-config.xml",
        "/saml2-protocol-config.xml",
        "/saml2-protocol-thirdparty-config.xml",
        "/saml2-req-attr-config.xml",
        "/saml2-protocol-aslo-config.xml",
        "/saml2-channel-binding-config.xml",
        "/saml-ec-gss-config.xml",
        };

    /** {@inheritDoc} */
    @Override
    @Nonnull protected String[] getConfigResources() {
        return configs;
    }

}