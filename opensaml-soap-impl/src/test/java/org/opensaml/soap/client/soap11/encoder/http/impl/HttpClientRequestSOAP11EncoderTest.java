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

package org.opensaml.soap.client.soap11.encoder.http.impl;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.mock.SimpleXMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Header;
import org.opensaml.soap.wsaddressing.Action;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.component.ComponentInitializationException;

@SuppressWarnings("javadoc")
public class HttpClientRequestSOAP11EncoderTest extends XMLObjectBaseTestCase {
    
    private HttpClientRequestSOAP11Encoder encoder;
    
    private MessageContext messageContext;
    
    private HttpPost request;
    
    @BeforeMethod
    public void setUp() {
        request = new HttpPost("http://example.org/soap/receiver");
        
        messageContext = new MessageContext();
        
        encoder = new HttpClientRequestSOAP11Encoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpRequest(request);
    }
    
    @Test
    public void testBasic() throws ComponentInitializationException, MessageEncodingException {
        final SimpleXMLObject sxo = buildXMLObject(SimpleXMLObject.ELEMENT_NAME);
        messageContext.setMessage(sxo);
        
        encoder.initialize();
        encoder.prepareContext();
        
        final SOAP11Context soapContext = messageContext.getSubcontext(SOAP11Context.class);
        assert soapContext != null;
        final Envelope envelope = soapContext.getEnvelope();
        Assert.assertNotNull(envelope);
        
        encoder.encode();
        
        Assert.assertNotNull(request.getEntity());
        
        Assert.assertTrue(request.getEntity().getContentType().startsWith("text/xml;"), "Unexpected content type");
        Assert.assertEquals(request.getEntity().getContentEncoding(), "UTF-8", "Unexpected character encoding");
        Assert.assertEquals(request.getFirstHeader("SOAPAction").getValue(), "");
    }

    @Test
    public void testAction() throws ComponentInitializationException, MessageEncodingException {
        final SimpleXMLObject sxo = buildXMLObject(SimpleXMLObject.ELEMENT_NAME);
        messageContext.setMessage(sxo);
        
        encoder.initialize();
        encoder.prepareContext();
        
        final SOAP11Context soapContext = messageContext.getSubcontext(SOAP11Context.class);
        assert soapContext != null;
        final Envelope envelope = soapContext.getEnvelope();
        assert envelope != null;
        
        // "For real" this would be added by a MessageHandler.  Here just add manually.
        final Action action = buildXMLObject(Action.ELEMENT_NAME);
        action.setURI("urn:test:action:foo");
        Header header = envelope.getHeader();
        if (header == null) {
            header = (Header) buildXMLObject(Header.DEFAULT_ELEMENT_NAME);
            envelope.setHeader(header);
        }
        header.getUnknownXMLObjects().add(action);
        
        encoder.encode();
        
        Assert.assertNotNull(request.getEntity());
        
        Assert.assertTrue(request.getEntity().getContentType().startsWith("text/xml;"), "Unexpected content type");
        Assert.assertEquals(request.getEntity().getContentEncoding(), "UTF-8", "Unexpected character encoding");
        Assert.assertEquals(request.getFirstHeader("SOAPAction").getValue(), "urn:test:action:foo");
    }

}
