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

package org.opensaml.messaging.pipeline;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.decoder.MessageDecoder;
import org.opensaml.messaging.encoder.MessageEncoder;
import org.opensaml.messaging.handler.MessageHandler;

import net.shibboleth.shared.logic.Constraint;

/**
 * Basic implementation of {@link MessagePipeline}.
 */
public class BasicMessagePipeline implements MessagePipeline {
    
    /** Message encoder. */
    @Nonnull private MessageEncoder encoder;
    
    /** Message decoder. */
    @Nonnull private MessageDecoder decoder;
    
    /** Outbound payload message handler. */
    @Nullable private MessageHandler outboundPayloadHandler;
    
    /** Outbound transport message handler. */
    @Nullable private MessageHandler outboundTransportHandler;
    
    /** Inbound message handler. */
    @Nullable private MessageHandler inboundHandler;
    
    /**
     * Constructor.
     *
     * @param newEncoder the message encoder instance
     * @param newDecoder the message decoder instance
     */
    public BasicMessagePipeline(@Nonnull final MessageEncoder newEncoder, 
            @Nonnull final MessageDecoder newDecoder) {
        encoder = Constraint.isNotNull(newEncoder, "MessageEncoder cannot be null");
        decoder = Constraint.isNotNull(newDecoder, "MessageDecoder cannot be null");
    }

    /** {@inheritDoc} */
    @Nonnull public MessageEncoder getEncoder() {
        return encoder;
    }
    
    /**
     * Set the message encoder instance.
     * 
     * @param newEncoder the new message encoder
     */
    protected void setEncoder(@Nonnull final MessageEncoder newEncoder) {
       encoder = Constraint.isNotNull(newEncoder, "MessageEncoder cannot be null");
    }

    /** {@inheritDoc} */
    @Nonnull public MessageDecoder getDecoder() {
        return decoder;
    }
    
    /**
     * Set the message decoder instance.
     * 
     * @param newDecoder the new message decoder
     */
    protected void setDecoder(@Nonnull final MessageDecoder newDecoder) {
       decoder = Constraint.isNotNull(newDecoder, "MessageDecoder cannot be null");
    }


    /** {@inheritDoc} */
    @Nullable public MessageHandler getOutboundPayloadMessageHandler() {
        return outboundPayloadHandler;
    }

    /**
     * Set the outbound payload message handler.
     * 
     * @param handler the new handler
     */
    public void setOutboundPayloadHandler(@Nullable final MessageHandler handler) {
        outboundPayloadHandler = handler;
    }

    /** {@inheritDoc} */
    @Nullable public MessageHandler getOutboundTransportMessageHandler() {
        return outboundTransportHandler;
    }

    /**
     * Set the outbound transport message handler.
     * 
     * @param handler the new handler
     */
    public void setOutboundTransportHandler(@Nullable final MessageHandler handler) {
        outboundTransportHandler = handler;
    }

    /** {@inheritDoc} */
    @Nullable public MessageHandler getInboundMessageHandler() {
        return inboundHandler;
    }
    
    /**
     * Set the inbound message handler.
     * 
     * @param handler the new handler
     */
    public void setInboundHandler(@Nullable final MessageHandler handler) {
        inboundHandler = handler;
    }

}