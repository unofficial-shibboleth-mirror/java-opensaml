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

package org.opensaml.xmlsec.config.impl;

import javax.annotation.Nonnull;

import org.apache.xml.security.Init;
import org.opensaml.core.config.ConfigurationProperties;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.opensaml.xmlsec.impl.provider.ApacheSantuarioXMLParser;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Initializer which initializes the Apache XML Security library (Santuario).
 */
public class ApacheXMLSecurityInitializer implements Initializer {
    
    /** Config property for enabling the use of {@link ApacheSantuarioXMLParser}. */
    @Nonnull @NotEmpty public static final String CONFIG_PROPERTY_XML_PARSER_ENABLE =
            "opensaml.config.xmlsec.ApacheSantuarioXMLParser.enable";
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(ApacheXMLSecurityInitializer.class);

    /** {@inheritDoc} */
    public void init() throws InitializationException {
        final String lineBreakPropName = "org.apache.xml.security.ignoreLineBreaks";
        // Don't override if it was set explicitly
        if (System.getProperty(lineBreakPropName) == null) {
            System.setProperty(lineBreakPropName, "true");
        }
        
        final ConfigurationProperties props = ConfigurationService.getConfigurationProperties(); 
        final boolean enableXMLParser = Boolean.parseBoolean(
                props.getProperty(CONFIG_PROPERTY_XML_PARSER_ENABLE, "true"));

        if (enableXMLParser) {
            final String xmlParserPropName = "org.apache.xml.security.XMLParser";
            // Don't override if it was set explicitly
            if (System.getProperty(xmlParserPropName) == null) {
                log.trace("Enabling use of ApacheSantuarioXMLParser");
                System.setProperty(xmlParserPropName, ApacheSantuarioXMLParser.class.getName());
            }
        }

        if (!Init.isInitialized()) {
            log.debug("Initializing Apache XMLSecurity library");
            Init.init();
        } else {
            log.debug("Apache XMLSecurity library was already initialized, skipping...");
        }
    }

}