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

package org.opensaml.soap.soap11.decoder.http.impl;

import javax.annotation.Nonnull;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Envelope;

/**
 * A body handler for use with {@link HTTPSOAP11Decoder} that populates the 
 * context message with the SOAP Envelope.
 */
public class EnvelopeBodyHandler extends AbstractMessageHandler {

    /** {@inheritDoc} */
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        final Envelope env = messageContext.ensureSubcontext(SOAP11Context.class).getEnvelope();
        if (env == null) {
            throw new MessageHandlerException("MessageContext did not contain a SOAP Envelope");
        }
        messageContext.setMessage(env);
    }

}