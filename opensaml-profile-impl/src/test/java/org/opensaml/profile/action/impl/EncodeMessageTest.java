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

import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.encoder.AbstractMessageEncoder;
import org.opensaml.messaging.encoder.MessageEncoder;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.testing.ActionTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

/** Unit test for {@link EncodeMessage}. */
@SuppressWarnings("javadoc")
public class EncodeMessageTest {
    
    private MockMessage message; 
    
    private MockMessageEncoder encoder;
    
    private MessageContext messageContext;
    
    private ProfileRequestContext profileCtx;
    
    private String expectedMessage;
    
    @BeforeMethod
    public void setUp() throws ComponentInitializationException {
        message = new MockMessage();
        message.getProperties().put("foo", "3");
        message.getProperties().put("bar", "1");
        message.getProperties().put("baz", "2");
        
        // Encoded mock message, keys sorted alphabetically, per MockMessage#toString
        expectedMessage = "bar=1&baz=2&foo=3";
        
        messageContext = new MessageContext();
        messageContext.setMessage(message);
        
        profileCtx = new ProfileRequestContext();
        profileCtx.setOutboundMessageContext(messageContext);
        
        encoder = new MockMessageEncoder();
        // Note: we don't init the encoder, b/c that is done by the action after setting the message context
    }

    @Test(expectedExceptions = ComponentInitializationException.class)
    public void testNoFactory() throws ComponentInitializationException {
        final EncodeMessage action = new EncodeMessage();
        action.initialize();
    }
    
    /**
     * Test that the action proceeds properly if the message can be decoded.
     * 
     * @throws Exception if something goes wrong
     */
    @Test public void testDecodeMessage() throws Exception {
        EncodeMessage action = new EncodeMessage();
        action.setMessageEncoderFactory(new MockEncoderFactory());
        action.initialize();

        action.execute(profileCtx);
        ActionTestingSupport.assertProceedEvent(profileCtx);

        Assert.assertEquals(encoder.getEncodedMessage(), expectedMessage);
    }

    /**
     * Test that the action errors out properly if the message can not be decoded.
     * 
     * @throws Exception if something goes wrong
     */
    @Test public void testThrowException() throws Exception {
        encoder.setThrowException(true);

        EncodeMessage action = new EncodeMessage();
        action.setMessageEncoderFactory(new MockEncoderFactory());
        action.initialize();

        action.execute(profileCtx);
        ActionTestingSupport.assertEvent(profileCtx, EventIds.UNABLE_TO_ENCODE);
    }

    /**
     * Mock implementation of {@link MessageEncoder} which either returns a  
     * {@link MessageContext} with a mock message or throws a {@link MessageDecodingException}.
     */
    private class MockMessageEncoder extends AbstractMessageEncoder {

        /** Whether a {@link MessageEncodingException} should be thrown by {@link #doEncode()}. */
        private boolean throwException = false;
        
        /** Mock encoded message. */
        private String message;
        
        /**
         * Get the encoded message
         * 
         * @return the string buffer
         */
        public String getEncodedMessage() {
            return message;
        }

        /**
         * Sets whether a {@link MessageEncodingException} should be thrown by {@link #doEncode()}.
         * 
         * @param shouldThrowDecodeException true if an exception should be thrown, false if not
         */
        public void setThrowException(final boolean shouldThrowDecodeException) {
            throwException = shouldThrowDecodeException;
        }

        /** {@inheritDoc} */
        @Override
        protected void doEncode() throws MessageEncodingException {
            if (throwException) {
                throw new MessageEncodingException();
            }
            final MessageContext mc = getMessageContext();
            assert mc != null;
            
            final MockMessage msg = (MockMessage) mc.getMessage();
            assert msg != null;
            message = msg.getEncoded();
        }
    }
 
    private class MockEncoderFactory implements Function<ProfileRequestContext,MessageEncoder> {

        /** {@inheritDoc} */
        @Nullable public MessageEncoder apply(@Nullable final ProfileRequestContext profileRequestContext) {
            return encoder;
        }
        
    }
    
}