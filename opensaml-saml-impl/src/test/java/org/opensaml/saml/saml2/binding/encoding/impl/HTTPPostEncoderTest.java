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
import java.util.List;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.binding.impl.SAMLOutboundDestinationHandler;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.StringDigester;
import net.shibboleth.shared.codec.StringDigester.OutputFormat;
import net.shibboleth.shared.testing.ConstantSupplier;

/**
 * Test case for {@link HTTPPostEncoder}.
 */
@SuppressWarnings({"null", "javadoc"})
public class HTTPPostEncoderTest extends XMLObjectBaseTestCase {

    /** Velocity template engine. */
    private VelocityEngine velocityEngine;

    @BeforeMethod
    public void setUp() throws Exception {
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init();
    }

    /**
     * Tests encoding a SAML message to an servlet response.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testResponseEncoding() throws Exception {
        SAMLObjectBuilder<StatusCode> statusCodeBuilder =
                (SAMLObjectBuilder<StatusCode>) builderFactory.<StatusCode>ensureBuilder(
                        StatusCode.DEFAULT_ELEMENT_NAME);
        StatusCode statusCode = statusCodeBuilder.buildObject();
        statusCode.setValue(StatusCode.SUCCESS);

        SAMLObjectBuilder<Status> statusBuilder =
                (SAMLObjectBuilder<Status>) builderFactory.<Status>ensureBuilder(Status.DEFAULT_ELEMENT_NAME);
        Status responseStatus = statusBuilder.buildObject();
        responseStatus.setStatusCode(statusCode);

        SAMLObjectBuilder<Response> responseBuilder =
                (SAMLObjectBuilder<Response>) builderFactory.<Response>ensureBuilder(Response.DEFAULT_ELEMENT_NAME);
        Response samlMessage = responseBuilder.buildObject();
        samlMessage.setID("foo");
        samlMessage.setVersion(SAMLVersion.VERSION_20);
        samlMessage.setIssueInstant(Instant.ofEpochMilli(0));
        samlMessage.setStatus(responseStatus);

        SAMLObjectBuilder<AssertionConsumerService> endpointBuilder =
                (SAMLObjectBuilder<AssertionConsumerService>) builderFactory.<AssertionConsumerService>ensureBuilder(
                        AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        AssertionConsumerService samlEndpoint = endpointBuilder.buildObject();
        samlEndpoint.setLocation("http://example.org");
        samlEndpoint.setResponseLocation("http://example.org/response");

        MessageContext messageContext = new MessageContext();
        messageContext.setMessage(samlMessage);
        SAMLBindingSupport.setRelayState(messageContext, "relay");
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class)
            .ensureSubcontext(SAMLEndpointContext.class).setEndpoint(samlEndpoint);
        
        final SAMLOutboundDestinationHandler handler = new SAMLOutboundDestinationHandler();
        handler.initialize();
        handler.invoke(messageContext);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HTTPPostEncoder encoder = new HTTPPostEncoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponseSupplier(new ConstantSupplier<>(response));
        
        encoder.setVelocityEngine(velocityEngine);
        
        encoder.setCSPDigester(new StringDigester("SHA-256", OutputFormat.HEX_LOWER));
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();

        final String csp = response.getHeader("Content-Security-Policy");
        Assert.assertTrue(csp != null && csp.contains("script-src-attr 'unsafe-hashes' 'sha256-78f9e25449128af5ff73b5d604669faa1f2d4a9891aca8aa61ea9b1bb3754ce1"));

        Assert.assertEquals(response.getContentType(), "text/html;charset=UTF-8", "Unexpected content type");
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        
        Document webDoc = Jsoup.parse(response.getContentAsString());
        
        boolean sawDocType = false;
        List<Node>nods = webDoc.childNodes();
        for (Node node : nods) {
           if (node instanceof DocumentType) {
               sawDocType = true;
               DocumentType documentType = (DocumentType)node;
               Assert.assertEquals(documentType.attr("name"), "html");
               Assert.assertEquals(documentType.attr("publicId"), "");
               Assert.assertEquals(documentType.attr("systemId"), "");
           }
        }
        Assert.assertTrue(sawDocType);
        
        Element head = webDoc.selectFirst("html > head");
        assert head != null;
        Element metaCharSet = head.selectFirst("meta[charset]");
        assert metaCharSet != null;
        Assert.assertEquals(metaCharSet.attr("charset").toLowerCase(), "utf-8");
        
        Element body = webDoc.selectFirst("html > body");
        assert body != null;
        Assert.assertEquals(body.attr("onload"), "document.forms[0].submit()");
        
        Element form = body.selectFirst("form");
        assert form != null;
        Assert.assertEquals(form.attr("method").toLowerCase(), "post");
        Assert.assertEquals(form.attr("action"), "http://example.org/response");
        
        Element relayState = form.selectFirst("input[name=RelayState]");
        assert relayState != null;
        Assert.assertEquals(relayState.val(), "relay");
        
        Element noscriptMsg = body.selectFirst("noscript > p");
        assert noscriptMsg != null;
        Assert.assertTrue(noscriptMsg.text().contains("Since your browser does not support JavaScript"));
        
        Element samlResponse = form.selectFirst("input[name=SAMLResponse]");
        assert samlResponse != null;
        Assert.assertNotNull(samlResponse.val());
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Support.decode(samlResponse.val()))) {
            XMLObject xmlObject = XMLObjectSupport.unmarshallFromInputStream(parserPool, inputStream);
            Assert.assertTrue(xmlObject instanceof Response);
            assertXMLEquals(xmlObject.ensureDOM().getOwnerDocument(), samlMessage);
        }
        
        Element submit = body.selectFirst("noscript > div > input[type=submit]");
        assert submit != null;
        Assert.assertEquals(submit.val(), "Continue");
        
    }

    @Test
    public void testRequestEncoding() throws Exception {
        SAMLObjectBuilder<AuthnRequest> requestBuilder =
                (SAMLObjectBuilder<AuthnRequest>) builderFactory.<AuthnRequest>ensureBuilder(AuthnRequest.DEFAULT_ELEMENT_NAME);
        AuthnRequest samlMessage = requestBuilder.buildObject();
        samlMessage.setID("foo");
        samlMessage.setVersion(SAMLVersion.VERSION_20);
        samlMessage.setIssueInstant(Instant.ofEpochMilli(0));

        SAMLObjectBuilder<Endpoint> endpointBuilder =
                (SAMLObjectBuilder<Endpoint>) builderFactory.<Endpoint>ensureBuilder(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        Endpoint samlEndpoint = endpointBuilder.buildObject();
        samlEndpoint.setLocation("http://example.org");
        samlEndpoint.setResponseLocation("http://example.org/response");
        
        MessageContext messageContext = new MessageContext();
        messageContext.setMessage(samlMessage);
        SAMLBindingSupport.setRelayState(messageContext, "relay");
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class)
            .ensureSubcontext(SAMLEndpointContext.class).setEndpoint(samlEndpoint);
        
        final SAMLOutboundDestinationHandler handler = new SAMLOutboundDestinationHandler();
        handler.initialize();
        handler.invoke(messageContext);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        
        HTTPPostEncoder encoder = new HTTPPostEncoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponseSupplier(new ConstantSupplier<>(response));
        
        encoder.setVelocityEngine(velocityEngine);
        
        encoder.setCSPDigester(new StringDigester("SHA-256", OutputFormat.HEX_LOWER));

        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();

        final String csp = response.getHeader("Content-Security-Policy");
        Assert.assertTrue(csp != null && csp.contains("script-src-attr 'unsafe-hashes' 'sha256-78f9e25449128af5ff73b5d604669faa1f2d4a9891aca8aa61ea9b1bb3754ce1"));
        
        Assert.assertEquals(response.getContentType(), "text/html;charset=UTF-8", "Unexpected content type");
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        
        Document webDoc = Jsoup.parse(response.getContentAsString());
        
        boolean sawDocType = false;
        List<Node>nods = webDoc.childNodes();
        for (Node node : nods) {
           if (node instanceof DocumentType) {
               sawDocType = true;
               DocumentType documentType = (DocumentType)node;
               Assert.assertEquals(documentType.attr("name"), "html");
               Assert.assertEquals(documentType.attr("publicId"), "");
               Assert.assertEquals(documentType.attr("systemId"), "");
           }
        }
        Assert.assertTrue(sawDocType);
        
        Element head = webDoc.selectFirst("html > head");
        assert head != null;
        Element metaCharSet = head.selectFirst("meta[charset]");
        assert metaCharSet != null;
        Assert.assertEquals(metaCharSet.attr("charset").toLowerCase(), "utf-8");
        
        Element body = webDoc.selectFirst("html > body");
        assert body != null;
        Assert.assertEquals(body.attr("onload"), "document.forms[0].submit()");
        
        Element form = body.selectFirst("form");
        assert form != null;
        Assert.assertEquals(form.attr("method").toLowerCase(), "post");
        Assert.assertEquals(form.attr("action"), "http://example.org");
        
        Element relayState = form.selectFirst("input[name=RelayState]");
        assert relayState != null;
        Assert.assertEquals(relayState.val(), "relay");
        
        Element noscriptMsg = body.selectFirst("noscript > p");
        assert noscriptMsg != null;
        Assert.assertTrue(noscriptMsg.text().contains("Since your browser does not support JavaScript"));
        
        Element samlResponse = form.selectFirst("input[name=SAMLRequest]");
        assert samlResponse != null;
        Assert.assertNotNull(samlResponse.val());
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Support.decode(samlResponse.val()))) {
            XMLObject xmlObject = XMLObjectSupport.unmarshallFromInputStream(parserPool, inputStream);
            Assert.assertTrue(xmlObject instanceof AuthnRequest);
            assertXMLEquals(xmlObject.ensureDOM().getOwnerDocument(), samlMessage);
        }
        
        Element submit = body.selectFirst("noscript > div > input[type=submit]");
        assert submit != null;
        Assert.assertEquals(submit.val(), "Continue");
        
    }
}
