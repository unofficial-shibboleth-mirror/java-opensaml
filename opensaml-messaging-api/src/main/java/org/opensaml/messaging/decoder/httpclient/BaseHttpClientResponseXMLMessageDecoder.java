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

package org.opensaml.messaging.decoder.httpclient;

import java.io.InputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.xml.ParserPool;
import net.shibboleth.shared.xml.SerializeSupport;
import net.shibboleth.shared.xml.XMLParserException;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**
 * Base class for message decoders which decode XML messages from a
 * {@link org.apache.hc.core5.http.ClassicHttpResponse}.
 */
public abstract class BaseHttpClientResponseXMLMessageDecoder extends AbstractHttpClientResponseMessageDecoder {
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(BaseHttpClientResponseXMLMessageDecoder.class);

    /** Parser pool used to deserialize the message. */
    @NonnullAfterInit private ParserPool parserPool;

    /** Constructor. */
    public BaseHttpClientResponseXMLMessageDecoder() {
        parserPool = XMLObjectProviderRegistrySupport.getParserPool();
    }

    /** {@inheritDoc} */
    public void decode() throws MessageDecodingException {
        log.debug("Beginning to decode message from HttpResponse");
        
        super.decode();

        log.debug("Successfully decoded message from HttpResponse");
    }
    
    /**
     * Gets the parser pool used to deserialize incoming messages.
     * 
     * @return parser pool used to deserialize incoming messages
     */
    @NonnullAfterInit public ParserPool getParserPool() {
        return parserPool;
    }

    /**
     * Sets the parser pool used to deserialize incoming messages.
     * 
     * @param pool parser pool used to deserialize incoming messages
     */
    public void setParserPool(@Nonnull final ParserPool pool) {
        checkSetterPreconditions();
        
        parserPool = Constraint.isNotNull(pool, "ParserPool cannot be null");
    }
    
    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (parserPool == null) {
            throw new ComponentInitializationException("Parser pool cannot be null");
        }
    }
    
    /** {@inheritDoc} */
    @Override
    @Nullable protected String serializeMessageForLogging(@Nullable final Object message) {
        if (message == null || !XMLObject.class.isInstance(message)) {
            log.debug("Message was null or unsupported, can not serialize");
            return null;
        }
        try {
            final Element dom = XMLObjectSupport.marshall(XMLObject.class.cast(message));
            return SerializeSupport.prettyPrintXML(dom);     
        } catch (MarshallingException e) {
            log.error("Unable to marshall message for logging purposes", e);
            return null;
        }
    }

    /**
     * Helper method that deserializes and unmarshalls the message from the given stream.
     * 
     * @param messageStream input stream containing the message
     * 
     * @return the inbound message
     * 
     * @throws MessageDecodingException thrown if there is a problem deserializing and unmarshalling the message
     */
    protected XMLObject unmarshallMessage(@Nonnull final InputStream messageStream) throws MessageDecodingException {
        try {
            final XMLObject message = XMLObjectSupport.unmarshallFromInputStream(getParserPool(), messageStream);
            return message;
        } catch (final XMLParserException e) {
            log.error("Error unmarshalling message from input stream: {}", e.getMessage());
            throw new MessageDecodingException("Error unmarshalling message from input stream", e);
        } catch (final UnmarshallingException e) {
            log.error("Error unmarshalling message from input stream: {}", e.getMessage());
            throw new MessageDecodingException("Error unmarshalling message from input stream", e);
        }
    }

}