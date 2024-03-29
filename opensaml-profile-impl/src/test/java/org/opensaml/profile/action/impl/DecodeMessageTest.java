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

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.AbstractMessageDecoder;
import org.opensaml.messaging.decoder.MessageDecoder;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

/** Unit test for {@link DecodeMessage}. */
public class DecodeMessageTest {
    
    private MockMessage message; 
    
    private MockMessageDecoder decoder;
    
    private ProfileRequestContext profileCtx;
    
    /**
     * Test setup. 
     * @throws ComponentInitializationException
     */
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        message = new MockMessage();
        
        decoder = new MockMessageDecoder(message);
        decoder.initialize();
        
        profileCtx = new ProfileRequestContext();
    }

    /**
     * Test that the action proceeds properly if the message can be decoded.
     *  
     * @throws ComponentInitializationException if something goes wrong
     */
    @Test public void testDecodeMessage() throws ComponentInitializationException {
        final DecodeMessage action = new DecodeMessage(decoder);
        action.initialize();

        action.execute(profileCtx);

        ActionTestingSupport.assertProceedEvent(profileCtx);

        final MessageContext mc = profileCtx.getInboundMessageContext(); 
        assert mc != null;
        Assert.assertEquals(mc.getMessage(), message);
    }

    /**
     * Test that the action errors out properly if the message can not be decoded.
     * 
     * @throws ComponentInitializationException if something goes wrong
     */
    @Test public void testFailure() throws ComponentInitializationException {
        decoder.setThrowException(true);

        final DecodeMessage action = new DecodeMessage(decoder);
        action.initialize();

        action.execute(profileCtx);

        ActionTestingSupport.assertEvent(profileCtx, EventIds.UNABLE_TO_DECODE);
    }

    /**
     * Mock implementation of {@link MessageDecoder} which either returns a  
     * {@link MessageContext} with a mock message or throws a {@link MessageDecodingException}.
     */
    class MockMessageDecoder extends AbstractMessageDecoder {

        /** Whether a {@link MessageDecodingException} should be thrown by {@link #doDecode()}. */
        private boolean throwException = false;
        
        /** The mock message to produce (optional). */
        private MockMessage message;

        /**
         * Constructor.
         *
         * @param mockMessage the mock message to produce
         */
        public MockMessageDecoder(MockMessage mockMessage) {
            message = mockMessage;
        }

        /**
         * Sets whether a {@link MessageDecodingException} should be thrown by {@link #doDecode()}.
         * 
         * @param shouldThrowDecodeException true if an exception should be thrown, false if not
         */
        public void setThrowException(final boolean shouldThrowDecodeException) {
            throwException = shouldThrowDecodeException;
        }

        /** {@inheritDoc} */
        @Override
        protected void doDecode() throws MessageDecodingException {
            if (throwException) {
                throw new MessageDecodingException();
            }
            final MessageContext messageContext = new MessageContext();
            if (message != null) {
                messageContext.setMessage(message);
            } else {
                messageContext.setMessage(new MockMessage());
            }
            setMessageContext(messageContext);
        }

    }
    
}