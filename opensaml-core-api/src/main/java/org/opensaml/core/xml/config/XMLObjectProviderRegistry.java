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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.slf4j.Logger;

import org.w3c.dom.Element;

import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.xml.ParserPool;

/** Configuration registry component for registering and retrieving implementation instances 
 * and related configuration relevant to working with XMLObjects, 
 * including builders, marshallers and unmarshallers.
 * 
 * <p>
 * The registry instance to use would typically be retrieved from the
 * {@link org.opensaml.core.config.ConfigurationService}.
 * </p>
 * 
 */
public class XMLObjectProviderRegistry {
    
    /** Default object provider. */
    @Nonnull private static QName defaultProvider = new QName(XMLConfigurator.XMLTOOLING_CONFIG_NS,
            XMLConfigurator.XMLTOOLING_DEFAULT_OBJECT_PROVIDER);
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(XMLObjectProviderRegistry.class);

    /** Object provider configuration elements indexed by QName. */
    @Nonnull private final Map<QName, Element> configuredObjectProviders;

    /** Configured XMLObject builder factory. */
    @Nonnull private XMLObjectBuilderFactory builderFactory;

    /** Configured XMLObject marshaller factory. */
    @Nonnull private MarshallerFactory marshallerFactory;

    /** Configured XMLObject unmarshaller factory. */
    @Nonnull private UnmarshallerFactory unmarshallerFactory;

    /** Configured set of attribute QNames which have been globally registered as having an ID type. */
    @Nonnull private final Set<QName> idAttributeNames;

    /** Configured parser pool. */
    @Nullable private ParserPool parserPool;

    /** Constructor. */
    public XMLObjectProviderRegistry() {
        configuredObjectProviders = new ConcurrentHashMap<>(0);
        builderFactory = new XMLObjectBuilderFactory();
        marshallerFactory = new MarshallerFactory();
        unmarshallerFactory = new UnmarshallerFactory();
        idAttributeNames = new CopyOnWriteArraySet<>();
        
        registerIDAttribute(new QName(javax.xml.XMLConstants.XML_NS_URI, "id"));
    }
    
    /**
     * Get the currently configured ParserPool instance.
     * 
     * @return the currently ParserPool
     */
    @Nullable public ParserPool getParserPool() {
        return parserPool;
    }

    /**
     * Set the currently configured ParserPool instance.
     * 
     * @param newParserPool the new ParserPool instance to configure
     */
    public void setParserPool(@Nullable final ParserPool newParserPool) {
        parserPool = newParserPool;
    }
    
    /**
     * Gets the QName for the object provider that will be used for XMLObjects that do not have a registered object
     * provider.
     * 
     * @return the QName for the default object provider
     */
    @Nonnull public QName getDefaultProviderQName() {
        return defaultProvider;
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
    public void registerObjectProvider(@Nonnull final QName providerName, @Nonnull final XMLObjectBuilder<?> builder,
            @Nonnull final Marshaller marshaller, @Nonnull final Unmarshaller unmarshaller) {
        log.debug("Registering new builder, marshaller, and unmarshaller for {}", providerName);
        builderFactory.registerBuilder(providerName, builder);
        marshallerFactory.registerMarshaller(providerName, marshaller);
        unmarshallerFactory.registerUnmarshaller(providerName, unmarshaller);
    }

    /**
     * Removes the builder, marshaller, and unmarshaller registered to the given key.
     * 
     * @param key the key of the builder, marshaller, and unmarshaller to be removed
     */
    public void deregisterObjectProvider(@Nonnull final QName key) {
        log.debug("Unregistering builder, marshaller, and unmarshaller for {}", key);
        configuredObjectProviders.remove(key);
        builderFactory.deregisterBuilder(key);
        marshallerFactory.deregisterMarshaller(key);
        unmarshallerFactory.deregisterUnmarshaller(key);
    }

    /**
     * Gets the XMLObject builder factory that has been configured with information from loaded configuration files.
     * 
     * @return the XMLObject builder factory
     */
    @Nonnull public XMLObjectBuilderFactory getBuilderFactory() {
        return builderFactory;
    }

    /**
     * Gets the XMLObject marshaller factory that has been configured with information from loaded configuration files.
     * 
     * @return the XMLObject marshaller factory
     */
    @Nonnull public MarshallerFactory getMarshallerFactory() {
        return marshallerFactory;
    }

    /**
     * Gets the XMLObject unmarshaller factory that has been configured with information from loaded configuration
     * files.
     * 
     * @return the XMLObject unmarshaller factory
     */
    @Nonnull public UnmarshallerFactory getUnmarshallerFactory() {
        return unmarshallerFactory;
    }

    /**
     * Register an attribute as having a type of ID.
     * 
     * @param attributeName the QName of the ID attribute to be registered
     */
    public void registerIDAttribute(@Nonnull final QName attributeName) {
        if (!idAttributeNames.contains(attributeName)) {
            idAttributeNames.add(attributeName);
        }
    }

    /**
     * Deregister an attribute as having a type of ID.
     * 
     * @param attributeName the QName of the ID attribute to be de-registered
     */
    public void deregisterIDAttribute(@Nonnull final QName attributeName) {
        if (idAttributeNames.contains(attributeName)) {
            idAttributeNames.remove(attributeName);
        }
    }

    /**
     * Determine whether a given attribute is registered as having an ID type.
     * 
     * @param attributeName the QName of the attribute to be checked for ID type.
     * @return true if attribute is registered as having an ID type.
     */
    public boolean isIDAttribute(@Nonnull final QName attributeName) {
        return idAttributeNames.contains(attributeName);
    }
    
}