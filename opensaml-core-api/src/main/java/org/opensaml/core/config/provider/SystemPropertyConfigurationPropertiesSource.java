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

import javax.annotation.Nullable;

import org.opensaml.core.config.ConfigurationProperties;
import org.opensaml.core.config.ConfigurationPropertiesSource;

/**
 * A configuration properties source implementation which simply returns the system properties set.
 */
public class SystemPropertyConfigurationPropertiesSource implements ConfigurationPropertiesSource {

    /** {@inheritDoc} */
    @Nullable public ConfigurationProperties getProperties() {
        final Properties props = System.getProperties();
        return props != null ? new PropertiesAdapter(props) : null;
    }

}