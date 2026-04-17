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

import org.opensaml.core.config.ConfigurationProperties;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.opensaml.saml.config.SAMLConfiguration;
import org.opensaml.saml.saml1.binding.artifact.SAML1ArtifactBuilderFactory;
import org.opensaml.saml.saml2.binding.artifact.SAML2ArtifactBuilderFactory;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * An initializer which initializes the {@link SAMLConfiguration} instance held
 * by the {@link ConfigurationService}.
 * 
 * <p>
 * This includes the artifact factories for SAML 1 and SAML 2 artifacts.
 * </p>
 */
public class SAMLConfigurationInitializer implements Initializer {

    /** Config property prefix for decoders. */
    public static final String CONFIG_PROP_PREFIX_DECODE = "opensaml.config.decode.saml";

    /** Config property name: Enforce SAMLRequest size limit. */
    public static final String CONFIG_PROPERTY_ENFORCE_REQUEST_SIZE_LIMIT =
            CONFIG_PROP_PREFIX_DECODE + ".request.enforceSizeLimit";

    /** Config property default: Enforce SAMLRequest size limit: false. */
    public static final String ENFORCE_REQUEST_SIZE_LIMIT_DEFAULT = "false";

    /** Config property name: SAMLRequest size limit. */
    public static final String CONFIG_PROPERTY_REQUEST_SIZE_LIMIT =
            CONFIG_PROP_PREFIX_DECODE + ".request.sizeLimit";

    /** Config property default: SAMLRequest size limit: 1 MB. */
    public static final String REQUEST_SIZE_LIMIT_DEFAULT = "1048576";
    
    /** Config property name: Enforce SAMLResponse size limit. */
    public static final String CONFIG_PROPERTY_ENFORCE_RESPONSE_SIZE_LIMIT =
            CONFIG_PROP_PREFIX_DECODE + ".response.enforceSizeLimit";

    /** Config property default: Enforce SAMLResponse size limit: false. */
    public static final String ENFORCE_RESPONSE_SIZE_LIMIT_DEFAULT = "false";

    /** Config property name: SAMLResponse size limit. */
    public static final String CONFIG_PROPERTY_RESPONSE_SIZE_LIMIT =
            CONFIG_PROP_PREFIX_DECODE + ".response.sizeLimit";

    /** Config property default: SAMLResponse size limit: 2 MB. */
    public static final String RESPONSE_SIZE_LIMIT_DEFAULT = "2097152";

    /** Config property name: Enforce KeyInfo size limit. */
    public static final String CONFIG_PROPERTY_ENFORCE_KEYINFO_SIZE_LIMIT =
            CONFIG_PROP_PREFIX_DECODE + ".keyinfo.enforceSizeLimit";

    /** Config property default: Enforce KeyInfo size limit: false. */
    public static final String ENFORCE_KEYINFO_SIZE_LIMIT_DEFAULT = "false";

    /** Config property name: KeyInfo size limit. */
    public static final String CONFIG_PROPERTY_KEYINFO_SIZE_LIMIT =
            CONFIG_PROP_PREFIX_DECODE + ".keyinfo.sizeLimit";

    /** Config property default: KeyInfo size limit: 2 MB. */
    public static final String KEYINFO_SIZE_LIMIT_DEFAULT = "2097152";

    /** Config property name: Estimate size of deflated data. */
    public static final String CONFIG_PROPERTY_DEFLATE_ESTIMATE =
            CONFIG_PROP_PREFIX_DECODE + ".deflate.estimateInflatedSize";

    /** Config property default: Estimate size of deflated data: true. */
    public static final String DEFLATE_ESTIMATE_DEFAULT = "true";

    /** Config property name: Inflation factor for deflated data. */
    public static final String CONFIG_PROPERTY_DEFLATE_INFLATION_FACTOR =
            CONFIG_PROP_PREFIX_DECODE + ".deflate.inflationFactor";

    /** Config property default: Inflation factor for deflated data: 1.75. */
    public static final String DEFLATE_INFLATION_FACTOR_DEFAULT = "1.75";

    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(SAMLConfigurationInitializer.class);

    /** {@inheritDoc} */
    public void init() throws InitializationException {
        log.debug("Initializing SAMLConfiguration");
        SAMLConfiguration config = null;
        
        synchronized (ConfigurationService.class) {
            config = ConfigurationService.get(SAMLConfiguration.class);
            if (config == null) {
                config = new SAMLConfiguration();
                ConfigurationService.register(SAMLConfiguration.class, config);
            }
        }
        
        config.setSAML1ArtifactBuilderFactory(new SAML1ArtifactBuilderFactory());
        config.setSAML2ArtifactBuilderFactory(new SAML2ArtifactBuilderFactory());
        
        final ConfigurationProperties props = ConfigurationService.getConfigurationProperties(); 

        config.setEnforceDecoderRequestSizeLimit(
                Boolean.parseBoolean(props.getProperty(CONFIG_PROPERTY_ENFORCE_REQUEST_SIZE_LIMIT,
                        ENFORCE_REQUEST_SIZE_LIMIT_DEFAULT)));
        config.setDecoderRequestSizeLimit(
                Integer.valueOf(props.getProperty(CONFIG_PROPERTY_REQUEST_SIZE_LIMIT,
                        REQUEST_SIZE_LIMIT_DEFAULT)));

        config.setEnforceDecoderResponseSizeLimit(
                Boolean.parseBoolean(props.getProperty(CONFIG_PROPERTY_ENFORCE_RESPONSE_SIZE_LIMIT,
                        ENFORCE_RESPONSE_SIZE_LIMIT_DEFAULT)));
        config.setDecoderResponseSizeLimit(
                Integer.valueOf(props.getProperty(CONFIG_PROPERTY_RESPONSE_SIZE_LIMIT,
                        RESPONSE_SIZE_LIMIT_DEFAULT)));

        config.setEnforceDecoderKeyInfoSizeLimit(
                Boolean.parseBoolean(props.getProperty(CONFIG_PROPERTY_ENFORCE_KEYINFO_SIZE_LIMIT,
                        ENFORCE_KEYINFO_SIZE_LIMIT_DEFAULT)));
        config.setDecoderKeyInfoSizeLimit(
                Integer.valueOf(props.getProperty(CONFIG_PROPERTY_KEYINFO_SIZE_LIMIT,
                        KEYINFO_SIZE_LIMIT_DEFAULT)));

        config.setDecoderEstimateInflatedSize(
                Boolean.parseBoolean(props.getProperty(CONFIG_PROPERTY_DEFLATE_ESTIMATE,
                        DEFLATE_ESTIMATE_DEFAULT)));
        config.setDecoderInflationFactor(
                Float.valueOf(props.getProperty(CONFIG_PROPERTY_DEFLATE_INFLATION_FACTOR,
                        DEFLATE_INFLATION_FACTOR_DEFAULT)));
    }

}