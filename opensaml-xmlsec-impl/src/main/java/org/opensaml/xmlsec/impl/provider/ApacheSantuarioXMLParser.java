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

package org.opensaml.xmlsec.impl.provider;

import java.io.InputStream;
import java.util.Properties;

import javax.annotation.Nonnull;

import org.apache.xml.security.parser.XMLParser;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.utils.XMLUtils;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.xml.config.XMLObjectProviderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.xml.ParserPool;
import net.shibboleth.shared.xml.impl.BasicParserPool;

/**
 * Implementation of Santuario's {@link XMLParser} which simply wraps an instance of {@link ParserPool}.
 * 
 * <p>
 * Note:
 * </p>
 * <ul>
 * <li>This class is required to have a no-arg constructor.</li>
 * <li>It will fail on any calls where <code>disallowDocTypeDeclarations=false</code>.</li>
 * <li>It is configured into Santuario by setting the class name using system
 *      property <code>org.apache.xml.security.XMLParser</code>. For details see: {@link XMLUtils}.</li>
 * <li>By default it internally uses the registered global {@link ParserPool} from the {@link ConfigurationService},
 *      if available. If not, then it constructs an internal instance of {@link BasicParserPool}.</li>
 * <li>The internal parser pool's max pool size may be configured via OpenSAML {@link ConfigurationService} property
 *     <code>{@link #CONFIG_PROPERTY_MAX_POOL_SIZE}</code>. The default is: 50</li>
 * </ul>
 */
public class ApacheSantuarioXMLParser implements XMLParser {
    
    /** Config property for internal pool's maxPoolSize. */
    @Nonnull @NotEmpty public static final String CONFIG_PROPERTY_MAX_POOL_SIZE =
            "opensaml.config.xmlsec.ApacheSantuarioXMLParser.maxPoolSize";
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ApacheSantuarioXMLParser.class);
    
    /** Wrapped instance of {@link ParserPool}. */
    @Nonnull private final ParserPool parserPool;

    /**
     * Constructor. 
     * 
     * @throws InitializationException if the internal {@link BasicParserPool} can not be initialized successfully
     * 
     * */
    public ApacheSantuarioXMLParser() throws InitializationException {
        // Note: We have to do it this way rather than XMLObjectProviderRegistrySupport b/c this class will get
        // instantiated by Santuario in a static block, and we don't have any control over when that might run vs
        // OpenSAML init. We shouldn't throw from #ensure(...) in that case.
        final XMLObjectProviderRegistry registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
        ParserPool globalPool = null;
        if (registry != null) {
            globalPool = registry.getParserPool();
        }
        if (globalPool != null) {
            parserPool = globalPool;
            log.trace("Configured parser pool as global ParserPool");
        } else {
            try {
                final Properties props = ConfigurationService.getConfigurationProperties(); 
                final int maxPoolSize =
                        (props != null) ? Integer.parseUnsignedInt(
                                props.getProperty(CONFIG_PROPERTY_MAX_POOL_SIZE, "50"))
                                : 50;

                final BasicParserPool basicPool = new BasicParserPool();
                basicPool.setMaxPoolSize(maxPoolSize);
                basicPool.initialize();
                parserPool = basicPool;
                log.trace("Configured parser pool as internally-constructed BasicParserPool with maxPoolSize: {}",
                        maxPoolSize);
            } catch (final ComponentInitializationException e) {
                throw new InitializationException("Error initializing internal BasicParserPool", e);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Document parse(final InputStream inputStream, final boolean disallowDocTypeDeclarations)
            throws XMLParserException {
        if (!disallowDocTypeDeclarations) {
           throw new XMLParserException("This implementation does not support disallowDocTypeDeclarations=false");
        }
        
        try {
            return parserPool.parse(inputStream);
        } catch (final Exception e) {
            log.warn("Fatal error parsing XML InputStream", e);
            throw new XMLParserException(e, "Fatal error parsing XML InputStream");
        }
    }

}
