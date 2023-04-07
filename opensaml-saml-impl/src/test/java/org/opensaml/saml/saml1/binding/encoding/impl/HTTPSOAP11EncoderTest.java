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

package org.opensaml.saml.saml1.binding.encoding.impl;

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
import org.opensaml.saml.saml1.core.Request;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.soap.soap11.Body;
import org.opensaml.soap.soap11.Envelope;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import net.shibboleth.shared.testing.ConstantSupplier;

/**
 * Test case for SAML 1.X HTTP SOAP 1.1 binding encoding.
 */
public class HTTPSOAP11EncoderTest extends XMLObjectBaseTestCase {

    /**
     * Tests encoding a simple SAML message.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testEncoding() throws Exception {
        final SAMLObjectBuilder<Request> requestBuilder =
                (SAMLObjectBuilder<Request>) builderFactory.<Request>ensureBuilder(Request.DEFAULT_ELEMENT_NAME);
        final Request request = requestBuilder.buildObject();
        request.setID("foo");
        request.setIssueInstant(Instant.ofEpochMilli(0));
        request.setVersion(SAMLVersion.VERSION_11);

        final SAMLObjectBuilder<Endpoint> endpointBuilder =
                (SAMLObjectBuilder<Endpoint>) builderFactory.<Endpoint>ensureBuilder(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        final Endpoint samlEndpoint = endpointBuilder.buildObject();
        samlEndpoint.setLocation("http://example.org");
        samlEndpoint.setResponseLocation("http://example.org/response");

        final MessageContext messageContext = new MessageContext();
        messageContext.setMessage(request);
        SAMLBindingSupport.setRelayState(messageContext, "relay");
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class).ensureSubcontext(SAMLEndpointContext.class).setEndpoint(samlEndpoint);
        
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
            final XMLObject xmlObject = XMLObjectSupport.unmarshallFromInputStream(parserPool, inputStream);
            Assert.assertNotNull(xmlObject);
            Assert.assertTrue(xmlObject instanceof Envelope);
            final Envelope envelope = (Envelope) xmlObject;
            Assert.assertNull(envelope.getHeader());
            final Body body = envelope.getBody();
            assert body != null;
            Assert.assertEquals(body.getUnknownXMLObjects().size(), 1);
            Request outboundRequest = (Request) body.getUnknownXMLObjects().get(0);
            outboundRequest.releaseDOM();
            outboundRequest.releaseChildrenDOM(true);
            outboundRequest.setParent(null);
            assertXMLEquals(XMLObjectSupport.marshall(outboundRequest).getOwnerDocument(), request);
        }
    }

}