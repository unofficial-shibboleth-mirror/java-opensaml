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

package org.opensaml.core.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An interface for a property set.
 */
public interface ConfigurationProperties {
    
    /**
     * Return the property value with the specified key, or null.
     * 
     * @param key the property key
     * 
     * @return the property value, or null
     */
    @Nullable public String getProperty(@Nonnull final String key);
    
    /**
     * Return the property value with the specified key, or the specified default value if key does not exist.
     * 
     * @param key the property key
     * @param defaultValue the default value to return
     * 
     * @return the property value, or the specified default value
     */
    @Nonnull String getProperty(@Nonnull final String key, @Nonnull final String defaultValue);

}
