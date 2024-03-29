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

import java.io.IOException;

import javax.annotation.Nonnull;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.io.Resources;

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.testing.ConstantSupplier;

/**
 * Test basic SOAP 1.1 message decoding.
 */
public class HTTPSOAP11DecoderTest extends XMLObjectBaseTestCase {
    
    private HTTPSOAP11Decoder decoder;
    
    private MockHttpServletRequest httpRequest;
    
    @BeforeMethod
    protected void setUp() throws Exception {
        httpRequest = new MockHttpServletRequest();
        httpRequest.setMethod("POST");
        httpRequest.setContentType("text/xml; charset=utf-8");
        
        decoder = new HTTPSOAP11Decoder();
        decoder.setParserPool(parserPool);
        decoder.setHttpServletRequestSupplier(new ConstantSupplier<>(httpRequest));
        // Let actual test method do the initialize(), so can set own body handler.
    }
    
    /**
     * Test basic no header case. Message will be an Envelope.
     * 
     * @throws ComponentInitializationException ...
     * @throws MessageDecodingException ...
     * @throws IOException ...
     */
    @Test
    public void testDecodeToEnvelope() throws ComponentInitializationException, MessageDecodingException, IOException {
        httpRequest.setContent(getServletRequestContent("/org/opensaml/soap/soap11/SOAPNoHeaders.xml"));
        
        final MessageHandler handler = new TestEnvelopeBodyHandler();
        handler.initialize();
        decoder.setBodyHandler(handler);
        decoder.initialize();
        
        decoder.decode();
        final MessageContext msgContext = decoder.getMessageContext();
        assert msgContext != null;
        
        final Object msg = msgContext.getMessage();
        Assert.assertTrue(msg instanceof Envelope);
    }
    
    /**
     * Test basic no header case. Message will be an non-Envelope payload.
     * 
     * @throws ComponentInitializationException ...
     * @throws MessageDecodingException ...
     * @throws IOException ...
     */
    @Test
    public void testDecodeToPayload() throws ComponentInitializationException, MessageDecodingException, IOException {
        httpRequest.setContent(getServletRequestContent("/org/opensaml/soap/soap11/SOAPNoHeaders.xml"));
        
        final MessageHandler handler = new TestPayloadBodyHandler();
        handler.initialize();
        decoder.setBodyHandler(handler);
        decoder.initialize();
        
        decoder.decode();
        final MessageContext msgContext = decoder.getMessageContext();
        assert msgContext != null;
        
        final Object msg = msgContext.getMessage();
        Assert.assertTrue(msg instanceof XSAny);
    }
    
    /**
     * Test missing content type.
     * 
     * @throws ComponentInitializationException ...
     * @throws MessageDecodingException ...
     * @throws IOException ...
     */
    @Test(expectedExceptions=MessageDecodingException.class)
    public void testmissingContentType() throws ComponentInitializationException, MessageDecodingException, IOException {
        httpRequest.setContent(getServletRequestContent("/org/opensaml/soap/soap11/SOAPNoHeaders.xml"));
        
        httpRequest.setContentType(null);
        
        final MessageHandler handler = new TestEnvelopeBodyHandler();
        handler.initialize();
        decoder.setBodyHandler(handler);
        decoder.initialize();
        
        decoder.decode();
    }
    
    /**
     * Test invalid content type.
     * 
     * @throws ComponentInitializationException ...
     * @throws MessageDecodingException ...
     * @throws IOException ...
     */
    @Test(expectedExceptions=MessageDecodingException.class)
    public void testInvalidContentType() throws ComponentInitializationException, MessageDecodingException, IOException {
        httpRequest.setContent(getServletRequestContent("/org/opensaml/soap/soap11/SOAPNoHeaders.xml"));
        
        httpRequest.setContentType("application/x-www-form-urlencoded");
        
        final MessageHandler handler = new TestEnvelopeBodyHandler();
        handler.initialize();
        decoder.setBodyHandler(handler);
        decoder.initialize();
        
        decoder.decode();
    }
    
    //
    // Helper stuff
    //
    
    /**
     * Get a resource relative to a class.
     * 
     * @param resourceName ...
     * 
     * @return resource content
     * 
     * @throws IOException ...
     */
    private byte[] getServletRequestContent(String resourceName) throws IOException {
        return Resources.toByteArray(getClass().getResource(resourceName));
    }

    public class TestEnvelopeBodyHandler extends AbstractMessageHandler {
        /** {@inheritDoc} */
        protected void doInvoke(@Nonnull final MessageContext msgContext) throws MessageHandlerException {
            final Envelope env = (Envelope) msgContext.ensureSubcontext(SOAP11Context.class).getEnvelope();
            msgContext.setMessage(env);
        }
    }
    
    public class TestPayloadBodyHandler extends AbstractMessageHandler {
        /** {@inheritDoc} */
        protected void doInvoke(@Nonnull final MessageContext msgContext) throws MessageHandlerException {
            final Envelope env = (Envelope) msgContext.ensureSubcontext(SOAP11Context.class).getEnvelope();
            assert env != null;
            final Body body = env.getBody();
            assert body != null;
            msgContext.setMessage(body.getUnknownXMLObjects().get(0));
        }
    }
    
}
