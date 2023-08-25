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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.config.ConfigurationProperties;

/**
 * An implementation of {@link ConfigurationProperties} that is empty.
 */
public class EmptyConfigurationProperties implements ConfigurationProperties {

    /** {@inheritDoc} */
    @Override
    @Nullable
    public String getProperty(@Nonnull final String key) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull
    public String getProperty(@Nonnull final String key, @Nonnull final String defaultValue) {
        return defaultValue;
    }

}
