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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.config.ConfigurationProperties;
import org.opensaml.core.config.ConfigurationPropertiesSource;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * A configuration properties source implementation which obtains the properties set
 * from a resource on the filesystem.
 * 
 * <p>
 * This is an abstract implementation.  A concrete implementation must supply the 
 * filesystem path to use via the {@link #getFilename()} method.
 * </p>
 */
public abstract class AbstractFilesystemConfigurationPropertiesSource implements ConfigurationPropertiesSource {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(AbstractFilesystemConfigurationPropertiesSource.class);
    
    /** Cache of properties. */
    @Nullable private Properties cachedProperties;

    /** {@inheritDoc} */
    @Nullable public ConfigurationProperties getProperties() {
        final String fileName = StringSupport.trimOrNull(getFilename());
        if (fileName == null) {
            log.warn("No filename was supplied, unable to load properties");
            return null;
        }
        synchronized (this) {
            if (cachedProperties == null) {
                // NOTE: in this invocation style via class loader, resource should NOT have a leading slash
                // because all names are absolute. This is unlike Class.getResourceAsStream 
                // where a leading slash is required for absolute names.
                final File file = new File(fileName);
                if (file.exists()) {
                    try (InputStream is = new FileInputStream(fileName)) {
                        final Properties props = new Properties();
                        props.load(is);
                        cachedProperties = props;
                    } catch (final FileNotFoundException e) {
                        log.warn("File not found attempting to load configuration properties '" 
                                + fileName + "' from filesystem");
                    } catch (final IOException e) {
                        log.warn("I/O problem attempting to load configuration properties '" 
                                + fileName + "' from filesystem", e);
                    }
                }
            }
            return cachedProperties != null ? new PropertiesAdapter(cachedProperties) : null;
        }
    }

    /**
     * Get the configuration properties filename.
     * 
     * @return the absolute filename
     */
    @Nullable protected abstract String getFilename();
    
}