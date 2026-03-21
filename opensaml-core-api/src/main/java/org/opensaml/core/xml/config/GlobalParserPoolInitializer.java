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

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.xml.impl.BasicParserPool;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.opensaml.core.config.ConfigurationProperties;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.slf4j.Logger;

/**
 * An initializer for the global parser pool held by the {@link XMLObjectProviderRegistry}.
 * 
 * <p>
 * The ParserPool configured by default here is an instance of
 * {@link BasicParserPool}, with the following non-default property values:
 * </p>
 * <ul>
 * <li>maxPoolSize = 50</li>
 * <li>builder attribute jdk.xml.elementAttributeLimit = 30</li>
 * <li>builder attribute jdk.xml.maxElementDepth = 25</li>
 * </ul>
 * 
 * <p>
 * If a deployment wishes to use a different parser pool implementation,
 * or one configured with different characteristics, they may 
 * simply configure a different ParserPool after initialization by
 * retrieving the {@link XMLObjectProviderRegistry} from the {@link ConfigurationService}.
 * </p>
 * 
 * 
 */
public class GlobalParserPoolInitializer implements Initializer {
            
    /** Config property prefix for XML processing. */
    public static final String CONFIG_PROP_PREFIX_XML = "opensaml.config.xml";
    
    /** Config property name: XML element attribute limit. */
    public static final String CONFIG_PROPERTY_XML_ELEMENT_ATTRIBUTE_LIMIT =
            CONFIG_PROP_PREFIX_XML + ".elementAttributeLimit";

    /** Config property default: XML element attribute limit: 30. */
    public static final String ELEMENT_ATTRIBUTE_LIMIT_DEFAULT = "30";
    
    /** Config property name: XML max element depth. */
    public static final String CONFIG_PROPERTY_XML_MAX_ELEMENT_DEPTH =
            CONFIG_PROP_PREFIX_XML + ".maxElementDepth";

    /** Config property default: XML max element depth: 25. */
    public static final String MAX_ELEMENT_DEPTH_DEFAULT = "25";
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(GlobalParserPoolInitializer.class);

    /** {@inheritDoc} */
    public void init() throws InitializationException {
        final ConfigurationProperties props = ConfigurationService.getConfigurationProperties(); 

        final Map<String,Object> builderAttributes = new HashMap<>();
        builderAttributes.put("jdk.xml.elementAttributeLimit",
                Integer.valueOf(props.getProperty(CONFIG_PROPERTY_XML_ELEMENT_ATTRIBUTE_LIMIT,
                        ELEMENT_ATTRIBUTE_LIMIT_DEFAULT)));
        builderAttributes.put("jdk.xml.maxElementDepth",
                Integer.valueOf(props.getProperty(CONFIG_PROPERTY_XML_MAX_ELEMENT_DEPTH,
                        MAX_ELEMENT_DEPTH_DEFAULT)));

        final BasicParserPool pp = new BasicParserPool();
        pp.setMaxPoolSize(50);
        pp.setBuilderAttributes(builderAttributes);
        try {
            pp.initialize();
        } catch (final ComponentInitializationException e) {
            throw new InitializationException("Error initializing parser pool", e);
        }
        
        XMLObjectProviderRegistry registry = null;
        synchronized(ConfigurationService.class) {
            registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
            if (registry == null) {
                log.debug("XMLObjectProviderRegistry did not exist in ConfigurationService, will be created");
                registry = new XMLObjectProviderRegistry();
                ConfigurationService.register(XMLObjectProviderRegistry.class, registry);
            }
        }
        
        registry.setParserPool(pp);
    }

}
