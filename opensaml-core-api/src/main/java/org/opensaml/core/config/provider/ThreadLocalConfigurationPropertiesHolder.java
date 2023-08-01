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

/**
 * A container class for holding a {link {@link ThreadLocal} copy of a {@link Properties} instance.
 */
public final class ThreadLocalConfigurationPropertiesHolder {
    
    /** ThreadLocal storage for the properties set. */
    @Nonnull private static final ThreadLocal<Properties> PROPERTIES = new ThreadLocal<>();
    
    /** Constructor. */
    private ThreadLocalConfigurationPropertiesHolder() {}
    
    /**
     * Get the thread-local configuration Properties instance.
     * 
     * @return the thread-local Properties
     */
    @Nullable public static Properties getProperties() {
        return PROPERTIES.get();
    }
    
    /**
     * Set the thread-local configuration Properties instance.
     * 
     * @param newProperties the new thread-local Properties instance
     */
    public static void setProperties(@Nullable final Properties newProperties) {
        PROPERTIES.set(newProperties);
    }
    
    /**
     *  Clear the thread-local configuration Properties instance.
     */
    public static void clear() {
        PROPERTIES.remove();
    }

}