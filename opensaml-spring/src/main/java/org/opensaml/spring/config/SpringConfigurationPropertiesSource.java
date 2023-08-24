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

package org.opensaml.spring.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.config.ConfigurationProperties;
import org.opensaml.core.config.ConfigurationPropertiesSource;
import org.springframework.core.env.PropertyResolver;

import net.shibboleth.shared.logic.Constraint;

/**
 * An implementation of {@link ConfigurationPropertiesSource} that delegates to a supplied
 * Spring {@link PropertyResolver}.
 */
public class SpringConfigurationPropertiesSource implements ConfigurationPropertiesSource {
    
    /** The supplied property resolver. */
    @Nonnull private final PropertyResolver propertyResolver;

    /**
     * Constructor.
     *
     * @param resolver the Spring property resolver
     */
    public SpringConfigurationPropertiesSource(@Nonnull final PropertyResolver resolver) {
        propertyResolver = Constraint.isNotNull(resolver, "PropertyResolver was null");
    }

    /** {@inheritDoc} */
    @Override
    @Nullable
    public ConfigurationProperties getProperties() {
        return new SpringPropertiesAdapter(propertyResolver);
    }

}
