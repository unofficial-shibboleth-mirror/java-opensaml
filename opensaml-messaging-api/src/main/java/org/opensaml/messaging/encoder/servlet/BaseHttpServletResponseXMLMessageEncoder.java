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

package org.opensaml.messaging.encoder.servlet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.slf4j.Logger;

import org.w3c.dom.Element;

import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.xml.SerializeSupport;

/**
 * Base class for message encoders which encode XML messages to HttpServletResponse.
 */
public abstract class BaseHttpServletResponseXMLMessageEncoder  extends AbstractHttpServletResponseMessageEncoder {
    
    /** Used to log protocol messages. */
    @Nonnull private Logger protocolMessageLog = LoggerFactory.getLogger("PROTOCOL_MESSAGE");

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(BaseHttpServletResponseXMLMessageEncoder.class);

    /** {@inheritDoc} */
    public void encode() throws MessageEncodingException {
        if (log.isDebugEnabled()) {
            final MessageContext mc = getMessageContext();
            if (mc != null) {
                final Object msg = mc.getMessage();
                if (msg != null) {
                    log.debug("Beginning encode of message of type: {}", msg.getClass().getName());
                }
            }
        }

        super.encode();
        
        log.debug("Successfully encoded message.");
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
     * Helper method that marshalls the given message.
     * 
     * @param message message the marshall and serialize
     * 
     * @return marshalled message
     * 
     * @throws MessageEncodingException thrown if the give message can not be marshalled into its DOM representation
     */
    @Nonnull protected Element marshallMessage(@Nonnull final XMLObject message) throws MessageEncodingException {
        log.debug("Marshalling message");
        
        try {
            return XMLObjectSupport.marshall(message);
        } catch (final MarshallingException e) {
            log.error("Error marshalling message: {}", e.getMessage());
            throw new MessageEncodingException("Error marshalling message", e);
        }
    }

}
