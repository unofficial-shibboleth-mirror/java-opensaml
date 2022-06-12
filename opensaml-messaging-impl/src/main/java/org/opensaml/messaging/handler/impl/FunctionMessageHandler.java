/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.messaging.handler.impl;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;

/**
 * Message handler that runs an injected function (expected to have side effects).
 * 
 * <p>The function may return an exception to signal failure, allowing for checked exceptions.</p>
 * 
 * @since 4.1.0
 */
public final class FunctionMessageHandler extends AbstractMessageHandler {
    
    /** The {@link Function} to run. */
    @Nullable private Function<MessageContext,Exception> messageContextConsumer;
    
    /**
     * Set the {@link Function} to use.
     * 
     * @param function the function to use
     */
    public void setFunction(@Nullable final Function<MessageContext,Exception> function) {
        throwSetterPreconditionExceptions();

        messageContextConsumer = function;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        if (messageContextConsumer != null) {
            try {
                final Exception e = messageContextConsumer.apply(messageContext);
                if (e != null) {
                    throw e;
                }
            } catch (final Exception e) {
                if (e instanceof MessageHandlerException) {
                    throw (MessageHandlerException) e;
                }
                throw new MessageHandlerException(e);
            }
        }
    }
    
}