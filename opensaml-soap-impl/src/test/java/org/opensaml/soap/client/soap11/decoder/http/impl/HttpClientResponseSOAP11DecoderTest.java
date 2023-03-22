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

package org.opensaml.soap.client.soap11.decoder.http.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.xml.SerializeSupport;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.common.SOAP11FaultDecodingException;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.util.SOAPSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

@SuppressWarnings("javadoc")
public class HttpClientResponseSOAP11DecoderTest extends XMLObjectBaseTestCase {
    
    private HttpClientResponseSOAP11Decoder decoder;
    
    @BeforeMethod
    public void setUp() {
        decoder = new HttpClientResponseSOAP11Decoder();
        decoder.setParserPool(parserPool);
    }
    
    @Test
    public void testDecodeToPayload() throws ComponentInitializationException, MessageDecodingException, MarshallingException, IOException {
        final Envelope envelope = buildMessageSkeleton();
        final Body body = envelope.getBody();
        assert body != null;
        body.getUnknownXMLObjects().add(buildXMLObject(simpleXMLObjectQName));
        final ClassicHttpResponse httpResponse = buildResponse(HttpStatus.SC_OK, envelope);
        
        decoder.setBodyHandler(new TestPayloadBodyHandler());
        decoder.setHttpResponse(httpResponse);
        decoder.initialize();
        
        decoder.decode();
        
        final MessageContext messageContext = decoder.getMessageContext();
        assert messageContext != null;
        Assert.assertNotNull(messageContext.getMessage());
        Assert.assertTrue(messageContext.getMessage() instanceof SimpleXMLObject);
        
        final SOAP11Context soapContext = messageContext.getSubcontext(SOAP11Context.class);
        assert soapContext != null;
        Assert.assertNotNull(soapContext.getEnvelope());
        Assert.assertEquals(soapContext.getHTTPResponseStatus(), Integer.valueOf(HttpStatus.SC_OK));
    }
    
    @Test
    public void testDecodeToEnvelope() throws ComponentInitializationException, MessageDecodingException, MarshallingException, IOException {
        final Envelope envelope = buildMessageSkeleton();
        final Body body = envelope.getBody();
        assert body != null;
        body.getUnknownXMLObjects().add(buildXMLObject(simpleXMLObjectQName));
        final ClassicHttpResponse httpResponse = buildResponse(HttpStatus.SC_OK, envelope);
        
        decoder.setBodyHandler(new TestEnvelopeBodyHandler());
        decoder.setHttpResponse(httpResponse);
        decoder.initialize();
        
        decoder.decode();
        
        final MessageContext messageContext = decoder.getMessageContext();
        assert messageContext != null;
        Assert.assertNotNull(messageContext.getMessage());
        Assert.assertTrue(messageContext.getMessage() instanceof Envelope);
        
        final SOAP11Context soapContext = messageContext.getSubcontext(SOAP11Context.class);
        assert soapContext != null;
        Assert.assertNotNull(soapContext.getEnvelope());
        Assert.assertEquals(soapContext.getHTTPResponseStatus(), Integer.valueOf(HttpStatus.SC_OK));
    }
    
    @Test(expectedExceptions=SOAP11FaultDecodingException.class)
    public void testFault() throws ComponentInitializationException, MessageDecodingException, MarshallingException, IOException {
        final Fault fault = SOAPSupport.buildSOAP11Fault(new QName("urn:test:soap:fault:foo", "TestFault", "foo"), "Test fault", null, null, null);
        
        final Envelope envelope = buildMessageSkeleton();
        final Body body = envelope.getBody();
        assert body != null;
        body.getUnknownXMLObjects().add(fault);
        final ClassicHttpResponse httpResponse = buildResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, envelope);
        
        decoder.setBodyHandler(new TestPayloadBodyHandler());
        decoder.setHttpResponse(httpResponse);
        decoder.initialize();
        
        decoder.decode();
    }
    
    @Nonnull private ClassicHttpResponse buildResponse(int statusResponseCode, @Nonnull final Envelope envelope)
            throws MarshallingException, IOException {
        final BasicClassicHttpResponse response = new BasicClassicHttpResponse(statusResponseCode, null);
        final Element envelopeElement = XMLObjectSupport.marshall(envelope);
        
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SerializeSupport.writeNode(envelopeElement, baos);
        baos.flush();
        
        final ByteArrayEntity entity = new ByteArrayEntity(baos.toByteArray(), ContentType.TEXT_XML);
        response.setEntity(entity);
        return response;
    }

    @Nonnull private Envelope buildMessageSkeleton() {
        final Envelope envelope = buildXMLObject(Envelope.DEFAULT_ELEMENT_NAME);
        final Body body = buildXMLObject(Body.DEFAULT_ELEMENT_NAME);
        envelope.setBody(body);
        return envelope;
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
