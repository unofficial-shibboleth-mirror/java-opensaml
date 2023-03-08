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

package org.opensaml.messaging.encoder;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;

import net.shibboleth.shared.component.AbstractInitializableComponent;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.component.UnmodifiableComponent;

/**
 * Abstract message encoder.
 */
public abstract class AbstractMessageEncoder extends AbstractInitializableComponent
        implements MessageEncoder, UnmodifiableComponent {

    /** The message context. */
    @Nullable private MessageContext messageContext;

    /** {@inheritDoc} */
    public synchronized void setMessageContext(@Nullable final MessageContext context) {
        checkSetterPreconditions();

        messageContext = context;
    }

    /** {@inheritDoc} */
    public void encode() throws MessageEncodingException {
        checkComponentActive();
        doEncode();
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
    @Nullable protected MessageContext getMessageContext() {
        return messageContext;
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (messageContext == null) {
            throw new ComponentInitializationException("Message context cannot be null");
        }
    }

    /**
     * Performs the encoding logic. By the time this is called, this encoder has already been initialized and checked to
     * ensure that it has not been destroyed.
     * 
     * @throws MessageEncodingException thrown if there is a problem encoding the message
     */
    protected abstract void doEncode() throws MessageEncodingException;
}