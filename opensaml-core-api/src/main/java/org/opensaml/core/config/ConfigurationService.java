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

import java.util.Iterator;
import java.util.ServiceLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.config.provider.MapBasedConfiguration;
import org.opensaml.core.config.provider.SystemPropertyConfigurationPropertiesSource;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * A service which provides for the registration, retrieval and deregistration of objects
 * related to library module configuration. 
 * 
 * <p>
 * The service uses an internally-managed instance of {@link Configuration} to handle
 * the registration, retrieval and deregistration of the configuration objects under its
 * management.
 * </p>
 * 
 * <p>
 * The service first attempts to use the Java Services API to resolve the instance 
 * of Configuration to use. If multiple implementations of Configuration are registered
 * via the Services API mechanism, the first one returned by the {@link ServiceLoader} iterator
 * is used.  If no Configuration implementation is declared or resolvable using 
 * the Services API, then it uses the default implementation {@link MapBasedConfiguration}.
 * </p>
 * 
 * <p>
 * The Configuration instance to use may also be set externally via {@link #setConfiguration(Configuration)}.
 * This may be useful where an application-specific means such as Spring is used to configure the environment.
 * This overrides the resolution process described above.
 * </p>
 */
public class ConfigurationService {
    
    /** The default storage partition name, if none is specified using configuration properties. */
    @Nonnull @NotEmpty public static final String DEFAULT_PARTITION_NAME = "default";
    
    /** The configuration property name for the storage partition name to use. */
    @Nonnull @NotEmpty public static final String PROPERTY_PARTITION_NAME = "opensaml.config.partitionName";
    
    /** Logger. */
    @Nonnull private static final Logger LOG = LoggerFactory.getLogger(ConfigurationService.class);
    
    /** The service loader used to locate registered implementations of ConfigurationPropertiesSource. */
    @SuppressWarnings("null")
    @Nonnull private static ServiceLoader<ConfigurationPropertiesSource> configPropertiesLoader = 
        ServiceLoader.load(ConfigurationPropertiesSource.class) ;
    
    /** The configuration instance to use. */
    @Nullable private static Configuration configuration;
    
    /** A default configuration properties source to use. */
    @Nullable private static ConfigurationPropertiesSource defaultConfigurationPropertiesSource;
    
    /** Constructor. */
    protected ConfigurationService() { }
    
    /**
     * Obtain the registered configuration instance. 
     * 
     * @param <T> the type of configuration being retrieved
     * 
     * @param configClass the configuration class identifier
     * 
     * @return the instance of the registered configuration object, or null
     */
    @Nullable public static <T extends Object> T get(@Nonnull final Class<T> configClass) {
        final String partitionName = getPartitionName();
        return getConfiguration().get(configClass, partitionName);
    }

    /**
     * Obtain the registered configuration instance, raising an exception if absent.
     * 
     * @param <T> the type of configuration being retrieved
     * 
     * @param configClass the configuration class identifier
     * 
     * @return the instance of the registered configuration object
     * 
     * @since 5.0.0
     */
    @Nonnull public static <T extends Object> T ensure(@Nonnull final Class<T> configClass) {
        final String partitionName = getPartitionName();
        final T config = getConfiguration().get(configClass, partitionName);
        if (config == null) {
            throw new IllegalStateException("Configuration instance of type "
                    + configClass.getName() + " was unavailable");
        }
        
        return config;
    }

    /**
     * Register a configuration instance.
     * 
     * @param <T> the type of configuration being registered
     * @param <I> the configuration object instance type being registered, which must be an instance of {@code T}
     * 
     * @param configClass the type of configuration being registered
     * @param configInstance the configuration object instance being registered
     */
    public static <T extends Object, I extends T> void register(@Nonnull final Class<T> configClass,
            @Nonnull final I configInstance) {
        final String partitionName = getPartitionName();
        getConfiguration().register(configClass, configInstance, partitionName);
    }

    /**
     * Deregister a configuration instance.
     * 
     * @param <T> the type of configuration being deregistered
     * 
     * @param configClass the type of configuration class being deregistered
     * 
     * @return the configuration object instance which was deregistered, or null
     */
    @Nullable public static <T extends Object> T deregister(@Nonnull final Class<T> configClass) {
        final String partitionName = getPartitionName();
        return getConfiguration().deregister(configClass, partitionName);
    }
    
    /**
     * Get the set of configuration meta-properties, which determines the configuration of the configuration
     * service itself.
     * 
     * <p>
     * The properties set is obtained from the first configured instance of 
     * {@link ConfigurationPropertiesSource} which returns a non-null properties set.
     * </p>
     * 
     * <p>
     * The first properties source to evaluate is the instance configured by
     * {@link #setDefaultConfigurationPropertiesSource(ConfigurationPropertiesSource)}.
     * </p>
     * 
     * <p>
     * If that is null or produces a null properties set, and there are no properties sources 
     * configured via the Java Services API, then a default implementation which exposes the
     * standard Java system properties from {@link System#getProperties()} set is used.
     * </p>
     * 
     * <p>
     * If properties sources are configured via the Java Services API, then those are 
     * evaluated in order, and the first non-null properties set returned is used.
     * If no configured sources return a properties set, then null is returned.
     * </p>
     * 
     * <p>
     * Properties made available in this meta-properties set may also be used by {@link Initializer} 
     * implementations.
     * </p>
     * 
     * @return the set of configuration meta-properties
     */
    @Nullable public static ConfigurationProperties getConfigurationProperties() {
        final ConfigurationPropertiesSource defaultSource = defaultConfigurationPropertiesSource;
        if (defaultSource != null) {
            final ConfigurationProperties props = defaultSource.getProperties();
            if (props != null) {
                LOG.trace("Resolved configuration properties from configured default properties source: {}",
                        defaultSource.getClass().getName());
                return props;
            } else {
                LOG.trace("A default properties source was configured, but produced a null properties set");
            }
        } else {
            LOG.trace("No default configuration properties source was configured");
        }

        LOG.trace("Attempting to resolve configuration properties source candidates via Java Services API");
        final Iterator<ConfigurationPropertiesSource> iter = configPropertiesLoader.iterator();
        
        if (!iter.hasNext()) {
            LOG.trace("No ConfigurationPropertiesSources are configured, defaulting to system properties");
            return new SystemPropertyConfigurationPropertiesSource().getProperties();
        }
        
        while (iter.hasNext()) {
            final ConfigurationPropertiesSource source = iter.next();
            LOG.trace("Evaluating configuration properties implementation: {}", source.getClass().getName());
            final ConfigurationProperties props = source.getProperties();
            if (props != null) {
                LOG.trace("Resolved non-null configuration properties using implementation: {}", 
                        source.getClass().getName());
                return props;
            }
        }
        LOG.trace("Unable to resolve non-null configuration properties from any ConfigurationPropertiesSource");
        return null;
    }
    
    /**
     * Get the default {@link ConfigurationPropertiesSource} instance to use.
     * 
     * <p>
     * The configuration properties source to use is normally resolved via the Java Services API,
     * or is defaulted if none are configured. However, this method is provided to allow the default
     * properties source instance to be supplied externally, perhaps using an application-specific
     * means such as Spring dependency injection.
     * </p>
     * 
     * @return the default ConfigurationPropertiesSource, possibly null
     */
    @Nullable public static ConfigurationPropertiesSource getDefaultConfigurationPropertiesSource() {
        return defaultConfigurationPropertiesSource;
    }
    
    /**
     * Set the default {@link ConfigurationPropertiesSource} instance to use.
     * 
     * <p>
     * The configuration properties source to use is normally resolved via the Java Services API,
     * or is defaulted if none are configured. However, this method is provided to allow the default
     * properties source instance to be supplied externally, perhaps using an application-specific
     * means such as Spring dependency injection.
     * </p>
     * 
     * @param source the default ConfigurationPropertiesSource instance to use
     */
    public static void setDefaultConfigurationPropertiesSource(@Nullable final ConfigurationPropertiesSource source) {
        defaultConfigurationPropertiesSource = source;
    }
    
    /**
     * Set the {@link Configuration} instance to use.
     * 
     * <p>
     * The configuration instance to use is normally resolved via the Java Services API,
     * or is defaulted.  However, this method is provided to allow the configuration
     * instance to be supplied externally, perhaps using an application-specific
     * means such as Spring dependency injection.
     * </p>
     * 
     * @param newConfiguration the Configuration instance to use
     */
    public static void setConfiguration(@Nonnull final Configuration newConfiguration) {
        configuration = Constraint.isNotNull(newConfiguration, "Configuration cannot be null");
    }
    
    /**
     * Return the partition name which will be used for storage of configuration objects.
     * 
     * <p>
     * This partition name is obtained from the configuration meta-properties.  If a value is not supplied
     * via that mechanism, then an internal default value is used.
     * </p>
     * 
     * @return the partition name
     */
    @Nonnull @NotEmpty protected static String getPartitionName() {
        final ConfigurationProperties configProperties = getConfigurationProperties();
        String partitionName = null;
        if (configProperties != null) {
            partitionName = configProperties.getProperty(PROPERTY_PARTITION_NAME, DEFAULT_PARTITION_NAME);
        } else {
            partitionName = DEFAULT_PARTITION_NAME;
        }
        LOG.trace("Resolved effective configuration partition name '{}'", partitionName);
        return partitionName;
    }

    /**
     * Get the {@link Configuration} instance to use.
     * 
     * <p>
     * The implementation to return is first resolved using the Java Services API.
     * If this produces no implementation, then an instance of the default implementation
     * of {@link MapBasedConfiguration} is used.
     * </p>
     * 
     * @return the Configuration implementation instance 
     */
    @Nonnull protected static Configuration getConfiguration() {
        if (configuration == null) {
            synchronized (ConfigurationService.class) {
                final ServiceLoader<Configuration> loader = ServiceLoader.load(Configuration.class);
                final Iterator<Configuration> iter = loader.iterator();
                if (iter.hasNext()) {
                    configuration = iter.next();
                } else {
                    // Default impl
                    configuration = new MapBasedConfiguration();
                }
            }
        }
        
        assert configuration != null;
        return configuration;
    }
    
}