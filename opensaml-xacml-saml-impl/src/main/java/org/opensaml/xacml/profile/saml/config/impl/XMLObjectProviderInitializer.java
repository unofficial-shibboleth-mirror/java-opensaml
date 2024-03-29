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

package org.opensaml.xacml.profile.saml.config.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.xml.config.AbstractXMLObjectProviderInitializer;

/**
 * XMLObject provider initializer for module "xacml-saml-impl".
 */
public class XMLObjectProviderInitializer extends AbstractXMLObjectProviderInitializer {
    
    /** Config resources. */
    @Nonnull private static String[] configs = {
        "/xacml10-saml2-profile-config.xml",
        "/xacml11-saml2-profile-config.xml",
        "/xacml2-saml2-profile-config.xml",
        "/xacml3-saml2-profile-config.xml",   
        };

    /** {@inheritDoc} */
    @Nonnull protected String[] getConfigResources() {
        return configs;
    }

}
