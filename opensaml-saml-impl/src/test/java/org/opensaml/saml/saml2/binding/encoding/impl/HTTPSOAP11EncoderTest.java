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

package org.opensaml.saml.saml2.binding.encoding.impl;

import java.io.ByteArrayInputStream;
import java.time.Instant;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.testing.ConstantSupplier;

/**
 * Test for SAML 2 SOAP 1.1 message encoder.
 */
public class HTTPSOAP11EncoderTest extends XMLObjectBaseTestCase {

    /**
     * Tests encoding a SAML message to an servlet response.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testResponseEncoding() throws Exception {
        final SAMLObjectBuilder<StatusCode> statusCodeBuilder = (SAMLObjectBuilder<StatusCode>) builderFactory
                .<StatusCode>ensureBuilder(StatusCode.DEFAULT_ELEMENT_NAME);
        final StatusCode statusCode = statusCodeBuilder.buildObject();
        statusCode.setValue(StatusCode.SUCCESS);

        final SAMLObjectBuilder<Status> statusBuilder = (SAMLObjectBuilder<Status>) builderFactory
                .<Status>ensureBuilder(Status.DEFAULT_ELEMENT_NAME);
        final Status responseStatus = statusBuilder.buildObject();
        responseStatus.setStatusCode(statusCode);

        final SAMLObjectBuilder<Response> responseBuilder = (SAMLObjectBuilder<Response>) builderFactory
                .<Response>ensureBuilder(Response.DEFAULT_ELEMENT_NAME);
        final Response samlMessage = responseBuilder.buildObject();
        samlMessage.setID("foo");
        samlMessage.setVersion(SAMLVersion.VERSION_20);
        samlMessage.setIssueInstant(Instant.ofEpochMilli(0));
        samlMessage.setStatus(responseStatus);

        final SAMLObjectBuilder<Endpoint> endpointBuilder = (SAMLObjectBuilder<Endpoint>) builderFactory
                .<Endpoint>ensureBuilder(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        Endpoint samlEndpoint = endpointBuilder.buildObject();
        samlEndpoint.setLocation("http://example.org");
        samlEndpoint.setResponseLocation("http://example.org/response");
        
        final MessageContext messageContext = new MessageContext();
        messageContext.setMessage(samlMessage);
        SAMLBindingSupport.setRelayState(messageContext, "relay");
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class)
            .ensureSubcontext(SAMLEndpointContext.class).setEndpoint(samlEndpoint);
        
        final MockHttpServletResponse response = new MockHttpServletResponse();
        
        final HTTPSOAP11Encoder encoder = new HTTPSOAP11Encoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponseSupplier(new ConstantSupplier<>(response));
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();

        Assert.assertEquals(response.getContentType(), "text/xml;charset=UTF-8", "Unexpected content type");
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        Assert.assertEquals(response.getHeader("SOAPAction"), "http://www.oasis-open.org/committees/security");
        
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(response.getContentAsByteArray())) {
            XMLObject xmlObject = XMLObjectSupport.unmarshallFromInputStream(parserPool, inputStream);
            Assert.assertNotNull(xmlObject);
            Assert.assertTrue(xmlObject instanceof Envelope);
            Envelope envelope = (Envelope) xmlObject;
            Assert.assertNull(envelope.getHeader());
            final Body body = envelope.getBody();
            assert body != null;
            Assert.assertEquals(body.getUnknownXMLObjects().size(), 1);
            Response outboundResponse = (Response) body.getUnknownXMLObjects().get(0);
            outboundResponse.releaseDOM();
            outboundResponse.releaseChildrenDOM(true);
            outboundResponse.setParent(null);
            assertXMLEquals(XMLObjectSupport.marshall(outboundResponse).getOwnerDocument(), samlMessage);
        }
        
    }
}
