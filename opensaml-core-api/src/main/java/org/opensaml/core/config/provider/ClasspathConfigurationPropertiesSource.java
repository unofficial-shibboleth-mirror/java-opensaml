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

package org.opensaml.core.config.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.config.ConfigurationProperties;
import org.opensaml.core.config.ConfigurationPropertiesSource;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * A configuration properties source implementation which obtains the properties set
 * from a resource on the class path.
 */
public class ClasspathConfigurationPropertiesSource implements ConfigurationPropertiesSource {
    
    /** Configuration properties resource name. */
    @Nonnull @NotEmpty private static final String RESOURCE_NAME = "opensaml-config.properties";
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(ClasspathConfigurationPropertiesSource.class);

    /** Cache of properties. */
    @Nullable private Properties cachedProperties;
    
    /** {@inheritDoc} */
    @Nullable public ConfigurationProperties getProperties() {
        synchronized (this) {
            if (cachedProperties == null) {
                try (final InputStream is =
                        Thread.currentThread().getContextClassLoader().getResourceAsStream(RESOURCE_NAME)) {
                    // NOTE: in this invocation style via class loader, resource should NOT have a leading slash
                    // because all names are absolute. This is unlike Class.getResourceAsStream 
                    // where a leading slash is required for absolute names.
                    if (is != null) {
                        final Properties props = new Properties();
                        props.load(is);
                        cachedProperties = props;
                    }
                } catch (final IOException e) {
                    log.warn("Problem attempting to load configuration properties '" 
                            + RESOURCE_NAME + "' from classpath", e);
                }
            }
            return cachedProperties != null ? new PropertiesAdapter(cachedProperties) : null;
        }
    }

}