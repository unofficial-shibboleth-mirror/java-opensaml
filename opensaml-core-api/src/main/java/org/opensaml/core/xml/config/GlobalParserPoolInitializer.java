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

import javax.annotation.Nonnull;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.slf4j.Logger;

/**
 * An initializer for the global parser pool held by the {@link XMLObjectProviderRegistry}.
 * 
 * <p>
 * The ParserPool configured by default here is an instance of
 * {@link BasicParserPool}, with a maxPoolSize property of 50 
 * and all other properties with default values.
 * </p>
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
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(GlobalParserPoolInitializer.class);

    /** {@inheritDoc} */
    public void init() throws InitializationException {
        final BasicParserPool pp = new BasicParserPool();
        pp.setMaxPoolSize(50);
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
