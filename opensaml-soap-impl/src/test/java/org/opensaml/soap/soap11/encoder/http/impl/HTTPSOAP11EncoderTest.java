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

package org.opensaml.soap.soap11.encoder.http.impl;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.Nonnull;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.opensaml.soap.soap11.Fault;
import org.opensaml.soap.soap11.FaultCode;
import org.opensaml.soap.soap11.FaultString;
import org.opensaml.soap.soap11.Header;
import org.opensaml.soap.util.SOAPSupport;
import org.opensaml.soap.wsaddressing.Action;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import jakarta.servlet.http.HttpServletResponse;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.testing.ConstantSupplier;
import net.shibboleth.shared.xml.XMLParserException;

/**
 * Test basic SOAP 1.1 message encoding.
 */
@SuppressWarnings("javadoc")
public class HTTPSOAP11EncoderTest extends XMLObjectBaseTestCase {
    
    /**
     * Test basic encoding of a message in an envelope, using payload-oriented messaging.
     * 
     * @throws ComponentInitializationException ...
     * @throws XMLParserException ...
     * @throws UnmarshallingException ...
     * @throws MessageEncodingException ...
     * @throws UnsupportedEncodingException ...
     */
    @Test
    public void testBasicEncodingAsPayload() throws ComponentInitializationException, MessageEncodingException, UnsupportedEncodingException, XMLParserException, UnmarshallingException {
        XMLObjectBuilder<XSAny> xsAnyBuilder = getBuilder(XSAny.TYPE_NAME);
        XSAny payload =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "GetLastTradePriceResponse", "m");
        
        XSAny price =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "Price", "m");
        price.setTextContent("34.5");
        
        payload.getUnknownXMLObjects().add(price);
        
        MessageContext messageContext = new MessageContext();
        messageContext.setMessage(payload);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HTTPSOAP11Encoder encoder = new HTTPSOAP11Encoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponseSupplier(new ConstantSupplier<>(response));
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();

        Assert.assertEquals(response.getContentType(), "text/xml;charset=UTF-8", "Unexpected content type");
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        Assert.assertEquals(response.getHeader("SOAPAction"), "");
        Assert.assertEquals(response.getStatus(), 200);
        
        Envelope encodedEnv = (Envelope) parseUnmarshallResourceByteArray(response.getContentAsByteArray(), false);
        
        String soapMessage = "/org/opensaml/soap/soap11/SOAPNoHeaders.xml";
        Envelope controlEnv = (Envelope) parseUnmarshallResource(soapMessage, false);
        
        final Diff diff = DiffBuilder.compare(controlEnv.getDOM()).withTest(encodedEnv.getDOM())
                .checkForIdentical()
                .ignoreWhitespace()
                .build();
        Assert.assertFalse(diff.hasDifferences(), diff.toString());
    }
    
    /**
     * Test basic encoding of a message in an envelope, using SOAP-message oriented messaging.
     * 
     * @throws ComponentInitializationException ...
     * @throws XMLParserException ...
     * @throws UnmarshallingException ...
     * @throws MessageEncodingException ...
     * @throws UnsupportedEncodingException ...
     */
    @Test
    public void testBasicEncodingAsSOAPEnvelope() throws ComponentInitializationException, MessageEncodingException, UnsupportedEncodingException, XMLParserException, UnmarshallingException {
        Envelope envelope = buildXMLObject(Envelope.DEFAULT_ELEMENT_NAME);
        Body body = buildXMLObject(Body.DEFAULT_ELEMENT_NAME);
        envelope.setBody(body);
        
        XMLObjectBuilder<XSAny> xsAnyBuilder = getBuilder(XSAny.TYPE_NAME);
        XSAny payload =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "GetLastTradePriceResponse", "m");
        
        XSAny price =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "Price", "m");
        price.setTextContent("34.5");
        
        payload.getUnknownXMLObjects().add(price);
        
        body.getUnknownXMLObjects().add(payload);
        
        MessageContext messageContext = new MessageContext();
        messageContext.setMessage(envelope);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HTTPSOAP11Encoder encoder = new HTTPSOAP11Encoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponseSupplier(new ConstantSupplier<>(response));
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();

        Assert.assertEquals(response.getContentType(), "text/xml;charset=UTF-8", "Unexpected content type");
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        Assert.assertEquals(response.getHeader("SOAPAction"), "");
        Assert.assertEquals(response.getStatus(), 200);
        
        
        Envelope encodedEnv = (Envelope) parseUnmarshallResourceByteArray(response.getContentAsByteArray(), false);
        
        String soapMessage = "/org/opensaml/soap/soap11/SOAPNoHeaders.xml";
        Envelope controlEnv = (Envelope) parseUnmarshallResource(soapMessage, false);
        
        final Diff diff = DiffBuilder.compare(controlEnv.getDOM()).withTest(encodedEnv.getDOM())
                .checkForIdentical()
                .ignoreWhitespace()
                .build();
        Assert.assertFalse(diff.hasDifferences(), diff.toString());
    }
    
    /**
     * Test basic encoding of a message in an envelope, using payload-oriented messaging. 
     * Supply an Envelope and header via SOAP subcontext.
     * 
     * @throws ComponentInitializationException ...
     * @throws XMLParserException ...
     * @throws UnmarshallingException ...
     * @throws MessageEncodingException ...
     * @throws UnsupportedEncodingException ...
     */
    @Test
    public void testEncodingAsPayloadWithHeader() throws ComponentInitializationException, MessageEncodingException, UnsupportedEncodingException, XMLParserException, UnmarshallingException {
        Envelope envelope = buildXMLObject(Envelope.DEFAULT_ELEMENT_NAME);
        Header header = buildXMLObject(Header.DEFAULT_ELEMENT_NAME);
        envelope.setHeader(header);
        
        XMLObjectBuilder<XSAny> xsAnyBuilder = getBuilder(XSAny.TYPE_NAME);
        
        XSAny transactionHeader = xsAnyBuilder.buildObject("http://example.org/soap/ns/transaction", "Transaction", "t");
        SOAPSupport.addSOAP11MustUnderstandAttribute(transactionHeader, true);
        transactionHeader.setTextContent("5");
        header.getUnknownXMLObjects().add(transactionHeader);
        
        XSAny payload =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "GetLastTradePriceResponse", "m");
        
        XSAny price =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "Price", "m");
        price.setTextContent("34.5");
        
        payload.getUnknownXMLObjects().add(price);
        
        MessageContext messageContext = new MessageContext();
        messageContext.setMessage(payload);
        messageContext.ensureSubcontext(SOAP11Context.class).setEnvelope(envelope);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HTTPSOAP11Encoder encoder = new HTTPSOAP11Encoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponseSupplier(new ConstantSupplier<>(response) );
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();

        Assert.assertEquals(response.getContentType(), "text/xml;charset=UTF-8", "Unexpected content type");
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        Assert.assertEquals(response.getHeader("SOAPAction"), "");
        Assert.assertEquals(response.getStatus(), 200);
        
        Envelope encodedEnv = (Envelope) parseUnmarshallResourceByteArray(response.getContentAsByteArray(), false);
        
        String soapMessage = "/org/opensaml/soap/soap11/SOAPHeaderMustUnderstand.xml";
        Envelope controlEnv = (Envelope) parseUnmarshallResource(soapMessage, false);
        
        final Diff diff = DiffBuilder.compare(controlEnv.getDOM()).withTest(encodedEnv.getDOM())
                .checkForIdentical()
                .ignoreWhitespace()
                .build();
        Assert.assertFalse(diff.hasDifferences(), diff.toString());
    }
    
    /**
     * Test basic encoding of a message in an envelope, using payload-oriented messaging.
     * 
     * @throws ComponentInitializationException ...
     * @throws XMLParserException ...
     * @throws UnmarshallingException ...
     * @throws MessageEncodingException ...
     * @throws UnsupportedEncodingException ...
     */
    @Test
    public void testEncodingWithAction() throws ComponentInitializationException, MessageEncodingException, UnsupportedEncodingException, XMLParserException, UnmarshallingException {
        Envelope envelope = buildXMLObject(Envelope.DEFAULT_ELEMENT_NAME);
        
        Body body = buildXMLObject(Body.DEFAULT_ELEMENT_NAME);
        envelope.setBody(body);
        
        Header header = buildXMLObject(Header.DEFAULT_ELEMENT_NAME);
        envelope.setHeader(header);
        
        Action action = buildXMLObject(Action.ELEMENT_NAME);
        action.setURI("urn:test:soap:action");
        header.getUnknownXMLObjects().add(action);
        
        XMLObjectBuilder<XSAny> xsAnyBuilder = getBuilder(XSAny.TYPE_NAME);
        XSAny payload =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "GetLastTradePriceResponse", "m");
        
        XSAny price =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "Price", "m");
        price.setTextContent("34.5");
        
        payload.getUnknownXMLObjects().add(price);
        
        body.getUnknownXMLObjects().add(payload);
        
        MessageContext messageContext = new MessageContext();
        messageContext.setMessage(envelope);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HTTPSOAP11Encoder encoder = new HTTPSOAP11Encoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponseSupplier(new ConstantSupplier<>(response));
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();

        Assert.assertEquals(response.getContentType(), "text/xml;charset=UTF-8", "Unexpected content type");
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        Assert.assertEquals(response.getStatus(), 200);
        
        Assert.assertEquals(response.getHeader("SOAPAction"), "urn:test:soap:action");
    }
    
    @Test
    public void testFaultAsMessage() throws ComponentInitializationException, MessageEncodingException, XMLParserException, UnmarshallingException {
        Fault fault = buildXMLObject(Fault.DEFAULT_ELEMENT_NAME);
        
        FaultCode faultCode = buildXMLObject(FaultCode.DEFAULT_ELEMENT_NAME);
        faultCode.setValue(FaultCode.SERVER);
        fault.setCode(faultCode);
        
        FaultString faultString = buildXMLObject(FaultString.DEFAULT_ELEMENT_NAME);
        faultString.setValue("Something bad happened");
        fault.setMessage(faultString);
       
        MessageContext messageContext = new MessageContext();
        messageContext.setMessage(fault);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HTTPSOAP11Encoder encoder = new HTTPSOAP11Encoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponseSupplier(new ConstantSupplier<>(response));
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();
        
        Assert.assertEquals(response.getContentType(), "text/xml;charset=UTF-8", "Unexpected content type");
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        Assert.assertEquals(response.getStatus(), 500);
        
        final Envelope encodedEnv = (Envelope) parseUnmarshallResourceByteArray(response.getContentAsByteArray(), false);
        
        Assert.assertNotNull(encodedEnv);
        Assert.assertNotNull(encodedEnv.getBody());
        final Body encodedBody = encodedEnv.getBody();
        assert encodedBody != null;
        final List<XMLObject> faults = encodedBody.getUnknownXMLObjects(Fault.DEFAULT_ELEMENT_NAME);
        Assert.assertEquals(faults.size(), 1);
        final Fault encodedFault = (Fault) faults.get(0);
        final FaultCode fcode = encodedFault.getCode();
        assert fcode != null;
        Assert.assertEquals(fcode.getValue(), FaultCode.SERVER);
        final FaultString fstring = encodedFault.getMessage();
        assert fstring != null;
        Assert.assertEquals(fstring.getValue(), "Something bad happened");
    }
    
    @Test
    public void testFaultFromContextSignal() throws ComponentInitializationException, MessageEncodingException, XMLParserException, UnmarshallingException {
        Fault fault = buildXMLObject(Fault.DEFAULT_ELEMENT_NAME);
        
        FaultCode faultCode = buildXMLObject(FaultCode.DEFAULT_ELEMENT_NAME);
        faultCode.setValue(FaultCode.SERVER);
        fault.setCode(faultCode);
        
        FaultString faultString = buildXMLObject(FaultString.DEFAULT_ELEMENT_NAME);
        faultString.setValue("Something bad happened");
        fault.setMessage(faultString);
        
        XMLObjectBuilder<XSAny> xsAnyBuilder = getBuilder(XSAny.TYPE_NAME);
        XSAny payload =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "GetLastTradePriceResponse", "m");
        
        XSAny price =  xsAnyBuilder.buildObject("http://example.org/soap/ns/message", "Price", "m");
        price.setTextContent("34.5");
        
        payload.getUnknownXMLObjects().add(price);
       
        MessageContext messageContext = new MessageContext();
        messageContext.setMessage(payload);
        SOAPMessagingSupport.registerSOAP11Fault(messageContext, fault);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HTTPSOAP11Encoder encoder = new HTTPSOAP11Encoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponseSupplier(new ConstantSupplier<>(response));
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();
        
        Assert.assertEquals(response.getContentType(), "text/xml;charset=UTF-8", "Unexpected content type");
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        Assert.assertEquals(response.getStatus(), 500);
        
        final Envelope encodedEnv = (Envelope) parseUnmarshallResourceByteArray(response.getContentAsByteArray(), false);
        
        Assert.assertNotNull(encodedEnv);
        Assert.assertNotNull(encodedEnv.getBody());
        final Body encodedBody = encodedEnv.getBody();
        assert encodedBody != null;
        List<XMLObject> faults = encodedBody.getUnknownXMLObjects(Fault.DEFAULT_ELEMENT_NAME);
        Assert.assertEquals(faults.size(), 1);
        final Fault encodedFault = (Fault) faults.get(0);
        assert encodedFault != null;
        final FaultCode fcode = encodedFault.getCode();
        assert fcode != null;
        Assert.assertEquals(fcode.getValue(), FaultCode.SERVER);
        final FaultString fstring = encodedFault.getMessage();
        assert fstring != null;
        Assert.assertEquals(fstring.getValue(), "Something bad happened");
    }
    
    @Test
    public void testContextReturnStatus() throws ComponentInitializationException, MessageEncodingException, XMLParserException, UnmarshallingException {
        XMLObject payload = buildXMLObject(simpleXMLObjectQName);
        
        MessageContext messageContext = new MessageContext();
        messageContext.setMessage(payload);
        
        messageContext.ensureSubcontext(SOAP11Context.class).setHTTPResponseStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HTTPSOAP11Encoder encoder = new HTTPSOAP11Encoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponseSupplier(new ConstantSupplier<>(response));
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();
        
        Assert.assertEquals(response.getStatus(), HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    }
    
    //
    // Helper stuff
    //

    @Nonnull protected XMLObject parseUnmarshallResource(@Nonnull final String resource, boolean dropDOM) throws XMLParserException, UnmarshallingException {
        Document soapDoc = parserPool.parse(this.getClass().getResourceAsStream(resource));
        return unmarshallXMLObject(soapDoc, dropDOM);
    }
    
    @Nonnull protected XMLObject parseUnmarshallResourceByteArray(byte [] input, boolean dropDOM) throws XMLParserException, UnmarshallingException {
        ByteArrayInputStream bais = new ByteArrayInputStream(input);
        Document soapDoc = parserPool.parse(bais);
        return unmarshallXMLObject(soapDoc, dropDOM);
    }

    @Nonnull protected XMLObject unmarshallXMLObject(@Nonnull final Document soapDoc, boolean dropDOM) throws UnmarshallingException {
        Element envelopeElem = soapDoc.getDocumentElement();
        Unmarshaller unmarshaller = unmarshallerFactory.ensureUnmarshaller(envelopeElem);
        
        Envelope envelope = (Envelope) unmarshaller.unmarshall(envelopeElem);
        if (dropDOM) {
            envelope.releaseDOM();
            envelope.releaseChildrenDOM(true);
        }
        return envelope;
    }

}
