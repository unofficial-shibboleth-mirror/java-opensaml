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

package org.opensaml.core.xml.config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallerFactory;

import net.shibboleth.shared.xml.ParserPool;

/** Class for loading library configuration files and retrieving the configured components. */
public final class XMLObjectProviderRegistrySupport {

    /** Constructor. */
    private XMLObjectProviderRegistrySupport() {

    }
    
    /**
     * Get the currently configured ParserPool instance.
     * 
     * @return the currently ParserPool
     */
    @Nullable public static ParserPool getParserPool() {
        return ConfigurationService.ensure(XMLObjectProviderRegistry.class).getParserPool();
    }

    /**
     * Set the currently configured ParserPool instance.
     * 
     * @param newParserPool the new ParserPool instance to configure
     */
    public static void setParserPool(@Nullable final ParserPool newParserPool) {
        ConfigurationService.ensure(XMLObjectProviderRegistry.class).setParserPool(newParserPool);
    }
    
    /**
     * Gets the QName for the object provider that will be used for XMLObjects that do not have a registered object
     * provider.
     * 
     * @return the QName for the default object provider
     */
    @Nonnull public static QName getDefaultProviderQName() {
        return ConfigurationService.ensure(XMLObjectProviderRegistry.class).getDefaultProviderQName();
    }

    /**
     * Adds an object provider to this configuration.
     * 
     * @param providerName the name of the object provider, corresponding to the element name or type name that the
     *            builder, marshaller, and unmarshaller operate on
     * @param builder the builder for that given provider
     * @param marshaller the marshaller for the provider
     * @param unmarshaller the unmarshaller for the provider
     */
    public static void registerObjectProvider(@Nonnull final QName providerName,
            @Nonnull final XMLObjectBuilder<?> builder, @Nonnull final Marshaller marshaller,
            @Nonnull final Unmarshaller unmarshaller) {
        final XMLObjectProviderRegistry registry = ConfigurationService.ensure(XMLObjectProviderRegistry.class);
        
        registry.getBuilderFactory().registerBuilder(providerName, builder);
        registry.getMarshallerFactory().registerMarshaller(providerName, marshaller);
        registry.getUnmarshallerFactory().registerUnmarshaller(providerName, unmarshaller);
    }

    /**
     * Removes the builder, marshaller, and unmarshaller registered to the given key.
     * 
     * @param key the key of the builder, marshaller, and unmarshaller to be removed
     */
    public static void deregisterObjectProvider(@Nonnull final QName key) {
        final XMLObjectProviderRegistry registry = ConfigurationService.ensure(XMLObjectProviderRegistry.class);
        registry.getBuilderFactory().deregisterBuilder(key);
        registry.getMarshallerFactory().deregisterMarshaller(key);
        registry.getUnmarshallerFactory().deregisterUnmarshaller(key);
    }

    /**
     * Gets the XMLObject builder factory that has been configured with information from loaded configuration files.
     * 
     * @return the XMLObject builder factory
     */
    @Nonnull public static XMLObjectBuilderFactory getBuilderFactory() {
        return ConfigurationService.ensure(XMLObjectProviderRegistry.class).getBuilderFactory();
    }

    /**
     * Gets the XMLObject marshaller factory that has been configured with information from loaded configuration files.
     * 
     * @return the XMLObject marshaller factory
     */
    @Nonnull public static MarshallerFactory getMarshallerFactory() {
        return ConfigurationService.ensure(XMLObjectProviderRegistry.class).getMarshallerFactory();
    }

    /**
     * Gets the XMLObject unmarshaller factory that has been configured with information from loaded configuration
     * files.
     * 
     * @return the XMLObject unmarshaller factory
     */
    @Nonnull public static UnmarshallerFactory getUnmarshallerFactory() {
        return ConfigurationService.ensure(XMLObjectProviderRegistry.class).getUnmarshallerFactory();
    }

    /**
     * Register an attribute as having a type of ID.
     * 
     * @param attributeName the QName of the ID attribute to be registered
     */
    public static void registerIDAttribute(@Nonnull final QName attributeName) {
        ConfigurationService.ensure(XMLObjectProviderRegistry.class).registerIDAttribute(attributeName);
    }

    /**
     * Deregister an attribute as having a type of ID.
     * 
     * @param attributeName the QName of the ID attribute to be de-registered
     */
    public static void deregisterIDAttribute(@Nonnull final QName attributeName) {
        ConfigurationService.ensure(XMLObjectProviderRegistry.class).deregisterIDAttribute(attributeName);
    }

    /**
     * Determine whether a given attribute is registered as having an ID type.
     * 
     * @param attributeName the QName of the attribute to be checked for ID type.
     * @return true if attribute is registered as having an ID type.
     */
    public static boolean isIDAttribute(@Nonnull final QName attributeName) {
        return ConfigurationService.ensure(XMLObjectProviderRegistry.class).isIDAttribute(attributeName);
    }

}