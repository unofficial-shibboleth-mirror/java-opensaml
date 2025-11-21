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

package org.opensaml.core.xml.config.impl;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.xml.config.AbstractXMLObjectProviderInitializer;
import org.opensaml.core.xml.config.XMLObjectProviderRegistry;

/**
 * XMLObject provider initializer for module "core".
 */
public class XMLObjectProviderInitializer extends AbstractXMLObjectProviderInitializer {
    
    /** Config resources. */
    @Nonnull private static String[] configs = {
        "/default-config.xml",
        "/schema-config.xml",
        };

    /** {@inheritDoc} */
    @Override
    @Nonnull protected String[] getConfigResources() {
        return configs;
    }

    /** {@inheritDoc} */
    @Override
    public void init() throws InitializationException {
        super.init();
        
        final XMLObjectProviderRegistry registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        if (registry == null) {
            throw new InitializationException("XMLObjectProviderRegistry was not available");
        }
        
        registry.registerIDAttribute(new QName(javax.xml.XMLConstants.XML_NS_URI, "id"));
    }

}