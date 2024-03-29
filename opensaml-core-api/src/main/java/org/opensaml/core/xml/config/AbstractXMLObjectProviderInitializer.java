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

package org.opensaml.core.xml.config;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Reads in an XML configuration and configures the XMLTooling library accordingly.
 */
public abstract class AbstractXMLObjectProviderInitializer implements Initializer {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractXMLObjectProviderInitializer.class);
    
    /** {@inheritDoc} */
    public void init() throws InitializationException {
        try {
            final XMLConfigurator configurator = new XMLConfigurator();
            // Checkstyle: FinalLocalVariable OFF
            for (String resource : getConfigResources()) {
            // Checkstyle: FinalLocalVariable ON
                // When using ClassLoader.getResourceAsStream() (as below), resource names should *not*
                // begin with leading "/".  They are always absolute.
                // This differs from Class.getResourceAsStream(), where absolute names must begin with /, otherwise
                // are treated as relative.
                // Checkstyle: ModifiedControlVariable OFF
                if (resource.startsWith("/")) {
                    resource = resource.substring(1);
                }
                // Checkstyle: ModifiedControlVariable ON
                log.debug("Loading XMLObject provider configuration from resource '{}'", resource);
                try (final InputStream is =
                        Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
                    if (is != null) {
                        configurator.load(is);
                    } else {
                        throw new XMLConfigurationException("Resource not found: " + resource);
                    }
                } catch (final IOException e) {
                    throw new XMLConfigurationException("Error loading resource: " + resource, e);
                }
            }
        } catch (final XMLConfigurationException e) {
            log.error("Problem loading configuration resource: {}", e.getMessage());
            throw new InitializationException("Problem loading configuration resource", e);
        }
    }

    /**
     * Obtain the list of configuration file resources which should be loaded.
     * 
     * @return the list of configuration file resources
     */
    @Nonnull protected abstract String[] getConfigResources();

}