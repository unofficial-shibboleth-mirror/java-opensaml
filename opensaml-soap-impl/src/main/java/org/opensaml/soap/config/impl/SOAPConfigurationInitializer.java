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

package org.opensaml.soap.config.impl;

import javax.annotation.Nonnull;

import org.opensaml.core.config.ConfigurationProperties;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.opensaml.soap.config.SOAPConfiguration;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * An initializer which initializes the {@link SOAPConfiguration} instance held
 * by the {@link ConfigurationService}.
 * 
 * <p>
 * This includes the artifact factories for SAML 1 and SAML 2 artifacts.
 * </p>
 */
public class SOAPConfigurationInitializer implements Initializer {
    
    /** Config property prefix for decoders. */
    public static final String CONFIG_PROP_PREFIX_DECODE = "opensaml.config.decode.soap";

    /** Config property name: Enforce SOAP size limit. */
    public static final String CONFIG_PROPERTY_ENFORCE_SIZE_LIMIT =
            CONFIG_PROP_PREFIX_DECODE  + ".enforceSizeLimit";

    /** Config property default: Enforce SOAP size limit: false. */
    public static final String ENFORCE_SIZE_LIMIT_DEFAULT = "false";

    /** Config property name: SAMLRequest size limit. */
    public static final String CONFIG_PROPERTY_SIZE_LIMIT =
            CONFIG_PROP_PREFIX_DECODE  + ".sizeLimit";

    /** Config property default: SAMLRequest size limit: 2 MB. */
    public static final String SIZE_LIMIT_DEFAULT = "2097152";

    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(SOAPConfigurationInitializer.class);

    /** {@inheritDoc} */
    public void init() throws InitializationException {
        log.debug("Initializing SOAPConfiguration");
        SOAPConfiguration config = null;
        
        synchronized (ConfigurationService.class) {
            config = ConfigurationService.get(SOAPConfiguration.class);
            if (config == null) {
                config = new SOAPConfiguration();
                ConfigurationService.register(SOAPConfiguration.class, config);
            }
        }
        
        final ConfigurationProperties props = ConfigurationService.getConfigurationProperties(); 

        config.setEnforceDecoderSizeLimit(
                Boolean.parseBoolean(props.getProperty(CONFIG_PROPERTY_ENFORCE_SIZE_LIMIT,
                        ENFORCE_SIZE_LIMIT_DEFAULT)));
        config.setDecoderSizeLimit(
                Integer.valueOf(props.getProperty(CONFIG_PROPERTY_SIZE_LIMIT,
                        SIZE_LIMIT_DEFAULT)));
    }

}