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

package org.opensaml.profile.action.impl;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncoder;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.messaging.handler.MessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

/**
 * Action that encodes an outbound response from the outbound {@link MessageContext}. 
 * 
 * <p>The input to {@link #setMessageEncoderFactory(Function)} is used to obtain a new
 * {@link MessageEncoder} to use, and the encoder is destroyed upon completion.</p>
 *
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 * @event {@link EventIds#UNABLE_TO_ENCODE}
 * 
 * @post If ProfileRequestContext.getOutboundMessageContext() != null, it will be injected and encoded.
 */
public class EncodeMessage extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(EncodeMessage.class);

    /** The function to use to obtain an encoder. */
    @NonnullAfterInit private Function<ProfileRequestContext,MessageEncoder> encoderFactory;
    
    /**
     * An optional {@link MessageHandler} instance to be invoked after 
     * {@link MessageEncoder#prepareContext()} and prior to {@link MessageEncoder#encode()}.
     */
    @Nullable private MessageHandler messageHandler;
    
    /** The outbound MessageContext to encode. */
    @Nullable private MessageContext msgContext;
    
    /**
     * Set the encoder factory to use.
     * 
     * @param factory   factory to use
     */
    public void setMessageEncoderFactory(@Nonnull final Function<ProfileRequestContext,MessageEncoder> factory) {
        encoderFactory = Constraint.isNotNull(factory, "MessageEncoderFactory cannot be null");
    }
    
    /**
     * <p>The supplied {@link MessageHandler} will be invoked on the {@link MessageContext} after 
     * {@link MessageEncoder#prepareContext()}, and prior to invoking {@link MessageEncoder#encode()}.
     * Its use is optional and primarily used for transport/binding-specific message handling, 
     * as opposed to more generalized message handling operations which would typically be invoked 
     * earlier than this action. For more details see {@link MessageEncoder}.</p>
     * 
     * @param handler a message handler
     */
    public void setMessageHandler(@Nullable final MessageHandler handler) {
        messageHandler = handler;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (encoderFactory == null) {
            throw new ComponentInitializationException("MessageEncoderFactory cannot be null");
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        msgContext = profileRequestContext.getOutboundMessageContext();
        if (msgContext == null) {
            log.debug("{} Outbound message context was null", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        }
        
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        final MessageEncoder encoder = encoderFactory.apply(profileRequestContext);
        if (encoder == null) {
            log.error("{} Unable to locate an outbound message encoder", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.UNABLE_TO_ENCODE);
            return;
        }

        try {
            log.debug("{} Encoding outbound response using message encoder of type {} for this response",
                    getLogPrefix(), encoder.getClass().getName());

            if (!encoder.isInitialized()) {
                log.debug("{} Encoder was not initialized, injecting MessageContext and initializing", getLogPrefix());
                encoder.setMessageContext(msgContext);
                encoder.initialize();
            } else {
                log.debug("{} Encoder was already initialized, skipping MessageContext injection and init",
                        getLogPrefix());
            }
            
            encoder.prepareContext();
            
            final MessageHandler handlerCopy = messageHandler;
            if (handlerCopy != null) {
                log.debug("{} Invoking message handler of type {} for this response", getLogPrefix(), 
                        handlerCopy.getClass().getName());
                assert msgContext != null;
                handlerCopy.invoke(msgContext);
            }
            
            encoder.encode();
            
            assert msgContext != null;
            final Object msg = msgContext.getMessage();
            
            if (msg != null) {
                log.debug("{} Outbound message encoded to a message of type {}", getLogPrefix(),
                        msg.getClass().getName());
            } else {
                log.debug("{} Outbound message was encoded via protocol-specific data " 
                        + "rather than MessageContext#getMessage()", getLogPrefix());
            }
            
        } catch (final MessageEncodingException | ComponentInitializationException | MessageHandlerException e) {
            log.error("{} Unable to encode outbound response", getLogPrefix(), e);
            ActionSupport.buildEvent(profileRequestContext, EventIds.UNABLE_TO_ENCODE);
        } finally {
            // TODO: do we want to destroy the encoder here?
            encoder.destroy();
        }
    }
    
}