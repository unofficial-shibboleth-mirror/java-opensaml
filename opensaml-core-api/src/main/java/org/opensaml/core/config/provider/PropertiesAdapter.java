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

import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.config.ConfigurationProperties;

import net.shibboleth.shared.logic.Constraint;

/**
 * An implementation of {@link ConfigurationProperties} which wraps an instance of Java {@link Properties}.
 */
public class PropertiesAdapter implements ConfigurationProperties {
    
    /** The wrapped properties instance. */
    @Nonnull private Properties properties;

    /**
     * Constructor.
     *
     * @param wrappedProperties the wrapped properties instance
     */
    public PropertiesAdapter(@Nonnull final Properties wrappedProperties) {
        properties = Constraint.isNotNull(wrappedProperties, "Wrapped Properties was null");
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public String getProperty(@Nonnull final String key) {
        Constraint.isNotNull(key, "Key was null");
        return properties.getProperty(key);
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public String getProperty(@Nonnull final String key, @Nonnull final String defaultValue) {
        Constraint.isNotNull(key, "Key was null");
        Constraint.isNotNull(defaultValue, "Default value was null");
        final String value = properties.getProperty(key, defaultValue);
        assert value != null;
        return value;
    }

}
