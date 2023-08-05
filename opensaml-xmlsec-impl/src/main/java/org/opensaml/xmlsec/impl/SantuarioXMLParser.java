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

package org.opensaml.xmlsec.impl;

import java.io.InputStream;

import javax.annotation.Nonnull;

import org.apache.xml.security.parser.XMLParser;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.utils.XMLUtils;
import org.opensaml.core.config.InitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.xml.impl.BasicParserPool;

/**
 * Implementation of Santuario's {@link XMLParser} which simply wraps an instance of {@link BasicParserPool}.
 * 
 * <p>
 * Note:
 * </p>
 * <ul>
 * <li>This class is required to have a no-arg constructor.</li>
 * <li>It will fail on any calls where <code>disallowDocTypeDeclarations=false</code>.</li>
 * <li>It is configured into Santuario by setting the class name using system
 *     property <code>org.apache.xml.security.XMLParser</code>. For details see: {@link XMLUtils}.</li>
 * <li>The internal parser pool's max pool size may be configured via system property
 *     <code>org.opensaml.xmlsec.impl.SantuarioXMLParser.maxPoolSize</code>.</li>
 * </ul>
 */
public class SantuarioXMLParser implements XMLParser {
    
    /** Logger. */
    @Nonnull final private Logger log = LoggerFactory.getLogger(SantuarioXMLParser.class);
    
    /** Wrapped instance of {@link BasicParserPool}. */
    @Nonnull final private BasicParserPool parserPool;

    /**
     * Constructor. 
     * 
     * @throws InitializationException if the internal {@link BasicParserPool} can not be initialized successfully
     * 
     * */
    public SantuarioXMLParser() throws InitializationException {
        try {
            parserPool = new BasicParserPool();
            parserPool.setMaxPoolSize(Integer.getInteger("org.opensaml.xmlsec.impl.SantuarioXMLParser.maxPoolSize", 50));
            parserPool.initialize();
        } catch (final ComponentInitializationException e) {
            throw new InitializationException("Error initializing parser pool", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Document parse(InputStream inputStream, boolean disallowDocTypeDeclarations) throws XMLParserException {
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
