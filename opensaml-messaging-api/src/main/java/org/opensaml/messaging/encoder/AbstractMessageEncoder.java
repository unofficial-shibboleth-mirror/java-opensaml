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

package org.opensaml.messaging.encoder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.AbstractMessageDecoder;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.AbstractInitializableComponent;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.component.UnmodifiableComponent;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Abstract message encoder.
 */
public abstract class AbstractMessageEncoder extends AbstractInitializableComponent
        implements MessageEncoder, UnmodifiableComponent {
    
    @Nonnull public static final String BASE_PROTOCOL_MESSAGE_LOGGER_CATEGORY = "PROTOCOL_MESSAGE";
    
    /** Used to log protocol messages. */
    @Nonnull private Logger protocolMessageLog = LoggerFactory.getLogger(BASE_PROTOCOL_MESSAGE_LOGGER_CATEGORY);
    
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractMessageEncoder.class);

    /** The message context. */
    @NonnullAfterInit private MessageContext messageContext;
    
    /** The configured logging category for protocol messages. */
    @Nonnull private String protocolMessageLoggerCategory = BASE_PROTOCOL_MESSAGE_LOGGER_CATEGORY;

    /** {@inheritDoc} */
    public synchronized void setMessageContext(@Nullable final MessageContext context) {
        checkSetterPreconditions();

        messageContext = context;
    }
    
    /** {@inheritDoc}.
     * 
     * Default implementation is a no-op.
     */
    public void prepareContext() throws MessageEncodingException {
        
    }

    /**
     * Get the message context.
     * 
     * @return the message context.
     */
    @NonnullAfterInit protected MessageContext getMessageContext() {
        return messageContext;
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (messageContext == null) {
            throw new ComponentInitializationException("Message context cannot be null");
        }
    }
    
    /** {@inheritDoc} */
    public void encode() throws MessageEncodingException {
        checkComponentActive();
        doEncode();
        logEncodedMessage();
    }
    
    /**
     * Get the protocol message logger.
     * 
     * @return The protocol message logger
     */
    @Nonnull protected Logger getProtocolMessageLogger() {
        return protocolMessageLog;
    }

    /**
     * Get the configured logging category for protocol messages.
     * 
     * @return the logging category
     */
    @Nonnull protected String getProtocolMessageLoggerCategory() {
        return protocolMessageLoggerCategory;
    }
    
    /**
     * Set the configured logging category for protocol messages.
     * 
     * <p>
     * If null, {@link AbstractMessageDecoder#BASE_PROTOCOL_MESSAGE_LOGGER_CATEGORY} will be used.
     * </p>
     * 
     * @param category the logging category
     */
    protected void setProtocolMessageLoggerCategory(@Nullable final String category) {
       final String trimmed = StringSupport.trimOrNull(category);
       if (trimmed != null) {
           protocolMessageLoggerCategory = trimmed;
       } else {
           protocolMessageLoggerCategory = BASE_PROTOCOL_MESSAGE_LOGGER_CATEGORY;
       }
       protocolMessageLog = LoggerFactory.getLogger(protocolMessageLoggerCategory);
    }

    /**
     * Log the encoded message to the protocol message logger.
     */
    protected void logEncodedMessage() {
        if (protocolMessageLog.isDebugEnabled() ){
            final String serializedMessage = serializeMessageForLogging(getMessageToLog());
            if (serializedMessage == null) {
                log.debug("Serialized encoded protocol message was null, nothing to log");
                return;
            }
            
            protocolMessageLog.debug("\n" + serializedMessage);
        }
    }
    
    /**
     * Get the XMLObject which will be logged as the protocol message.
     * 
     * @return the XMLObject message considered to be the protocol message for logging purposes
     */
    @Nullable protected Object getMessageToLog() {
        final MessageContext mc = getMessageContext();
        return mc != null ? mc.getMessage() : null;
    }
    
    /**
     * Serialize the message for logging purposes.
     * 
     * <p>
     * Default implementation is to return the message object's {@link #toString()},
     * but subclasses should override if a better message-specific serialization mechanism exists.
     * </p>
     * 
     * @param message the message to serialize
     * 
     * @return the serialized message, or null if message can not be serialized
     */
    @Nullable protected String serializeMessageForLogging(@Nullable final Object message) {
        return message != null ? message.toString() : null;
    }

    /**
     * Performs the encoding logic. By the time this is called, this encoder has already been initialized and checked to
     * ensure that it has not been destroyed.
     * 
     * @throws MessageEncodingException thrown if there is a problem encoding the message
     */
    protected abstract void doEncode() throws MessageEncodingException;
}