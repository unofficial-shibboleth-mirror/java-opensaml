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
 * A component which provides for the registration, retrieval and deregistration of objects
 * related to library module configuration.
 * 
 * <p>
 * An implementation may manage the registration, retrieval and deregistration of objects
 * using a variety of mechanisms, such as internal in-memory storage, JNDI or a database.
 * </p>
 */
public interface Configuration {
    
    /**
     * Obtain the registered configuration instance. 
     * 
     * @param <T> the type of configuration being retrieved, typically an interface
     * 
     * @param configClass the configuration class identifier, typically an interface
     * @param partitionName the partition name to use
     * 
     * @return the instance of the registered configuration interface, or null
     */
    @Nullable <T extends Object> T get(@Nonnull final Class<T> configClass, @Nonnull final String partitionName);
    
    /**
     * Register a configuration instance.
     * 
     * @param <T> the type of configuration being registered, typically an interface
     * @param <I> the configuration implementation being registered, which will be an instance of {@code T}
     * 
     * @param configClass the type of configuration class being registered, typically an interface
     * @param configInstance the configuration implementation instance being registered
     * @param partitionName the partition name to use
     */
    <T extends Object, I extends T> void register(@Nonnull final Class<T> configClass,
            @Nonnull final I configInstance, @Nonnull final String partitionName);
    
    /**
     * Deregister a configuration instance.
     * 
     * @param <T> the type of configuration being deregistered, typically an interface
     * 
     * @param configClass the type of configuration class being deregistered , typically an interface
     * @param partitionName the partition name to use
     * 
     * @return the configuration implementation instance which was deregistered, or null
     */
    @Nullable <T extends Object> T deregister(@Nonnull final Class<T> configClass, @Nonnull final String partitionName);

}