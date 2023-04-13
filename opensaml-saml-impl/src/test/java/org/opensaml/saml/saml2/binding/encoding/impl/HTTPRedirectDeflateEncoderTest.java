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

package org.opensaml.saml.saml2.binding.encoding.impl;

import java.io.ByteArrayInputStream;
import java.security.KeyPair;
import java.time.Instant;
import java.util.Map;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.binding.impl.SAMLOutboundDestinationHandler;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.net.URISupport;
import net.shibboleth.shared.net.URLBuilder;
import net.shibboleth.shared.testing.ConstantSupplier;

/**
 * Unit test for redirect encoding.
 */
public class HTTPRedirectDeflateEncoderTest extends XMLObjectBaseTestCase {
    
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
        final Endpoint samlEndpoint = endpointBuilder.buildObject();
        samlEndpoint.setLocation("http://example.org");
        samlEndpoint.setResponseLocation("http://example.org/response");
        
        final MessageContext messageContext = new MessageContext();
        messageContext.setMessage(samlMessage);
        SAMLBindingSupport.setRelayState(messageContext, "relay");
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class)
            .ensureSubcontext(SAMLEndpointContext.class).setEndpoint(samlEndpoint);
        
        final SAMLOutboundDestinationHandler handler = new SAMLOutboundDestinationHandler();
        handler.initialize();
        handler.invoke(messageContext);
        
        final MockHttpServletResponse response = new MockHttpServletResponse();
        
        final HTTPRedirectDeflateEncoder encoder = new HTTPRedirectDeflateEncoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponseSupplier(new ConstantSupplier<>(response));
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();
        
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        
        final String redirectedUrl = response.getRedirectedUrl();
        assert redirectedUrl != null;
        final URLBuilder urlBuilder = new URLBuilder(redirectedUrl);
        Assert.assertEquals(urlBuilder.getScheme(), "http");
        Assert.assertEquals(urlBuilder.getHost(), "example.org");
        Assert.assertEquals(urlBuilder.getPath(), "/response");
        
        final Map<String,String> queryParams = URISupport.buildQueryMap(urlBuilder.getQueryParams());
        Assert.assertFalse(queryParams.containsKey("Signature"));
        Assert.assertFalse(queryParams.containsKey("SigAlg"));
        Assert.assertTrue(queryParams.containsKey("RelayState"));
        Assert.assertEquals(queryParams.get("RelayState"), "relay");
        Assert.assertTrue(queryParams.containsKey("SAMLResponse"));
        try (final InflaterInputStream inflater = 
                new InflaterInputStream(
                        new ByteArrayInputStream(
                                Base64Support.decode(queryParams.get("SAMLResponse"))), new Inflater(true))) {
           
            final Document outboundResponse = parserPool.parse(inflater);
            assertXMLEquals(outboundResponse, samlMessage);
        }
        
    }
    
    /**
     * Tests encoding a SAML message to an servlet response.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testResponseEncodingWithEndpointQueryParams() throws Exception {
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
        final Endpoint samlEndpoint = endpointBuilder.buildObject();
        samlEndpoint.setLocation("http://example.org");
        samlEndpoint.setResponseLocation("http://example.org/response?foo=bar&abc=123");
        
        final MessageContext messageContext = new MessageContext();
        messageContext.setMessage(samlMessage);
        SAMLBindingSupport.setRelayState(messageContext, "relay");
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class)
            .ensureSubcontext(SAMLEndpointContext.class).setEndpoint(samlEndpoint);
        
        final SAMLOutboundDestinationHandler handler = new SAMLOutboundDestinationHandler();
        handler.initialize();
        handler.invoke(messageContext);
        
        final MockHttpServletResponse response = new MockHttpServletResponse();
        
        final HTTPRedirectDeflateEncoder encoder = new HTTPRedirectDeflateEncoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponseSupplier(new ConstantSupplier<>(response));
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();
        
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        
        final String redirectedUrl = response.getRedirectedUrl();
        assert redirectedUrl != null;
        final URLBuilder urlBuilder = new URLBuilder(redirectedUrl);
        Assert.assertEquals(urlBuilder.getScheme(), "http");
        Assert.assertEquals(urlBuilder.getHost(), "example.org");
        Assert.assertEquals(urlBuilder.getPath(), "/response");
        
        Map<String,String> queryParams = URISupport.buildQueryMap(urlBuilder.getQueryParams());
        Assert.assertTrue(queryParams.containsKey("foo"));
        Assert.assertEquals(queryParams.get("foo"), "bar");
        Assert.assertTrue(queryParams.containsKey("abc"));
        Assert.assertEquals(queryParams.get("abc"), "123");
        
        Assert.assertFalse(queryParams.containsKey("Signature"));
        Assert.assertFalse(queryParams.containsKey("SigAlg"));
        Assert.assertTrue(queryParams.containsKey("RelayState"));
        Assert.assertEquals(queryParams.get("RelayState"), "relay");
        Assert.assertTrue(queryParams.containsKey("SAMLResponse"));
        try (final InflaterInputStream inflater = 
                new InflaterInputStream(
                        new ByteArrayInputStream(
                                Base64Support.decode(queryParams.get("SAMLResponse"))), new Inflater(true))) {
           
            final Document outboundResponse = parserPool.parse(inflater);
            assertXMLEquals(outboundResponse, samlMessage);
        }
    }
    
    /**
     * Tests encoding a SAML message to an servlet response.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testResponseEncodingWithDisallowedEndpointQueryParams() throws Exception {
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
        final Endpoint samlEndpoint = endpointBuilder.buildObject();
        samlEndpoint.setLocation("http://example.org");
        samlEndpoint.setResponseLocation("http://example.org/response?foo=bar&abc=123&SAMLEncoding=blah&SAMLRequest=blah&SAMLResponse=blah&RelayState=blah&SigAlg=blah&Signature=blah");
        
        final MessageContext messageContext = new MessageContext();
        messageContext.setMessage(samlMessage);
        SAMLBindingSupport.setRelayState(messageContext, "relay");
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class)
            .ensureSubcontext(SAMLEndpointContext.class).setEndpoint(samlEndpoint);
        
        final SAMLOutboundDestinationHandler handler = new SAMLOutboundDestinationHandler();
        handler.initialize();
        handler.invoke(messageContext);
        
        final MockHttpServletResponse response = new MockHttpServletResponse();
        
        final HTTPRedirectDeflateEncoder encoder = new HTTPRedirectDeflateEncoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponseSupplier(new ConstantSupplier<>(response));
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();
        
        Assert.assertEquals("UTF-8", response.getCharacterEncoding(), "Unexpected character encoding");
        Assert.assertEquals(response.getHeader("Cache-control"), "no-cache, no-store", "Unexpected cache controls");
        
        final String redirectedUrl = response.getRedirectedUrl();
        assert redirectedUrl != null;
        final URLBuilder urlBuilder = new URLBuilder(redirectedUrl);
        Assert.assertEquals(urlBuilder.getScheme(), "http");
        Assert.assertEquals(urlBuilder.getHost(), "example.org");
        Assert.assertEquals(urlBuilder.getPath(), "/response");
        
        Map<String,String> queryParams = URISupport.buildQueryMap(urlBuilder.getQueryParams());
        Assert.assertTrue(queryParams.containsKey("foo"));
        Assert.assertEquals(queryParams.get("foo"), "bar");
        Assert.assertTrue(queryParams.containsKey("abc"));
        Assert.assertEquals(queryParams.get("abc"), "123");
        
        Assert.assertFalse(queryParams.containsKey("SAMLEncoding"));
        Assert.assertFalse(queryParams.containsKey("SAMLRequest"));
        Assert.assertFalse(queryParams.containsKey("SigAlg"));
        Assert.assertFalse(queryParams.containsKey("Signature"));
        Assert.assertTrue(queryParams.containsKey("RelayState"));
        Assert.assertEquals(queryParams.get("RelayState"), "relay");
        
        Assert.assertTrue(queryParams.containsKey("SAMLResponse"));
        Assert.assertNotEquals(queryParams.get("SAMLResponse"), "blah");
        try (final InflaterInputStream inflater = 
                new InflaterInputStream(
                        new ByteArrayInputStream(
                                Base64Support.decode(queryParams.get("SAMLResponse"))), new Inflater(true))) {
           
            final Document outboundResponse = parserPool.parse(inflater);
            assertXMLEquals(outboundResponse, samlMessage);
        }
    }
    
    /**
     * Tests encoding a SAML message to an servlet response with simple sign.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testResponseEncodingWithSimpleSign() throws Exception {
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
        final Endpoint samlEndpoint = endpointBuilder.buildObject();
        samlEndpoint.setLocation("http://example.org");
        samlEndpoint.setResponseLocation("http://example.org/response");
        
        final MessageContext messageContext = new MessageContext();
        messageContext.setMessage(samlMessage);
        SAMLBindingSupport.setRelayState(messageContext, "relay");
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class)
            .ensureSubcontext(SAMLEndpointContext.class).setEndpoint(samlEndpoint);
        final KeyPair kp = KeySupport.generateKeyPair("RSA", 1024, null);
        
        final SignatureSigningParameters signingParameters = new SignatureSigningParameters();
        signingParameters.setSigningCredential(CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate()));
        signingParameters.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        messageContext.ensureSubcontext(SecurityParametersContext.class).setSignatureSigningParameters(signingParameters);
        
        final SAMLOutboundDestinationHandler handler = new SAMLOutboundDestinationHandler();
        handler.initialize();
        handler.invoke(messageContext);
        
        final MockHttpServletResponse response = new MockHttpServletResponse();
        
        final HTTPRedirectDeflateEncoder encoder = new HTTPRedirectDeflateEncoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponseSupplier(new ConstantSupplier<>(response));
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();
        
        final String redirectedUrl = response.getRedirectedUrl();
        assert redirectedUrl != null;
        final URLBuilder urlBuilder = new URLBuilder(redirectedUrl);
        Assert.assertEquals(urlBuilder.getScheme(), "http");
        Assert.assertEquals(urlBuilder.getHost(), "example.org");
        Assert.assertEquals(urlBuilder.getPath(), "/response");
        
        final Map<String,String> queryParams = URISupport.buildQueryMap(urlBuilder.getQueryParams());
        Assert.assertTrue(queryParams.containsKey("Signature"));
        Assert.assertNotNull(queryParams.get("Signature"));
        Assert.assertTrue(queryParams.containsKey("SigAlg"));
        Assert.assertEquals(queryParams.get("SigAlg"), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        Assert.assertTrue(queryParams.containsKey("RelayState"));
        Assert.assertEquals(queryParams.get("RelayState"), "relay");
        Assert.assertTrue(queryParams.containsKey("SAMLResponse"));
        try (final InflaterInputStream inflater = 
                new InflaterInputStream(
                        new ByteArrayInputStream(
                                Base64Support.decode(queryParams.get("SAMLResponse"))), new Inflater(true))) {
           
            final Document outboundResponse = parserPool.parse(inflater);
            assertXMLEquals(outboundResponse, samlMessage);
        }
        
        // Note: to test that actual signature is cryptographically correct, really need a known good test vector.
        // Need to verify that we're signing over the right data in the right byte[] encoded form.
    }
    
    /**
     * Tests encoding a SAML message to an servlet response with simple sign, 
     * where the destination URL had existing non-disallowed query parameters.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void OSJ271() throws Exception {
        // First we generate the signature with a redirect URL that does not have query params.
        
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
        final Endpoint samlEndpoint = endpointBuilder.buildObject();
        samlEndpoint.setLocation("http://example.org");
        samlEndpoint.setResponseLocation("http://example.org/response");
        
        MessageContext messageContext = new MessageContext();
        messageContext.setMessage(samlMessage);
        SAMLBindingSupport.setRelayState(messageContext, "relay");
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class)
            .ensureSubcontext(SAMLEndpointContext.class).setEndpoint(samlEndpoint);
        final KeyPair kp = KeySupport.generateKeyPair("RSA", 1024, null);
        
        final SignatureSigningParameters signingParameters = new SignatureSigningParameters();
        signingParameters.setSigningCredential(CredentialSupport.getSimpleCredential(kp.getPublic(), kp.getPrivate()));
        signingParameters.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        messageContext.ensureSubcontext(SecurityParametersContext.class).setSignatureSigningParameters(signingParameters);
        
        // NOTE: So that we can get an exact signature comparison without and with query params, we do not invoke
        // the SAMLOutboundDestinationHandler, which would change the data being signed. Not correct vis-a-vis actual 
        //SAML protocol usage, but for purposes of this test it doesn't matter.
        
        final MockHttpServletResponse response = new MockHttpServletResponse();
        
         HTTPRedirectDeflateEncoder encoder = new HTTPRedirectDeflateEncoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponseSupplier(new ConstantSupplier<>(response));
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();
        
        String redirectedUrl = response.getRedirectedUrl();
        assert redirectedUrl != null;
        URLBuilder urlBuilder = new URLBuilder(redirectedUrl);
        Assert.assertEquals(urlBuilder.getScheme(), "http");
        Assert.assertEquals(urlBuilder.getHost(), "example.org");
        Assert.assertEquals(urlBuilder.getPath(), "/response");
        
        Map<String,String> queryParams = URISupport.buildQueryMap(urlBuilder.getQueryParams());
        Assert.assertTrue(queryParams.containsKey("Signature"));
        Assert.assertNotNull(queryParams.get("Signature"));
        Assert.assertTrue(queryParams.containsKey("SigAlg"));
        Assert.assertEquals(queryParams.get("SigAlg"), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        Assert.assertTrue(queryParams.containsKey("RelayState"));
        Assert.assertEquals(queryParams.get("RelayState"), "relay");
        Assert.assertTrue(queryParams.containsKey("SAMLResponse"));
        try (InflaterInputStream inflater = 
                new InflaterInputStream(
                        new ByteArrayInputStream(
                                Base64Support.decode(queryParams.get("SAMLResponse"))), new Inflater(true))) {
           
            Document outboundResponse = parserPool.parse(inflater);
            assertXMLEquals(outboundResponse, samlMessage);
        }
        
        // Note: to test that actual signature is cryptographically correct, really need a known good test vector.
        // Need to verify that we're signing over the right data in the right byte[] encoded form.
        
        String signatureWithoutParams = queryParams.get("Signature");
        
        // Now repeat with a redirect location that does have query params.
        
        samlEndpoint.setResponseLocation("http://example.org/response?foo=bar&abc=123");
        
        messageContext = new MessageContext();
        messageContext.setMessage(samlMessage);
        SAMLBindingSupport.setRelayState(messageContext, "relay");
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class)
        .ensureSubcontext(SAMLEndpointContext.class).setEndpoint(samlEndpoint);
        
        messageContext.ensureSubcontext(SecurityParametersContext.class).setSignatureSigningParameters(signingParameters);
        
        final MockHttpServletResponse response2 = new MockHttpServletResponse();
        
        encoder = new HTTPRedirectDeflateEncoder();
        encoder.setMessageContext(messageContext);
        encoder.setHttpServletResponseSupplier(new ConstantSupplier<>(response2));
        
        encoder.initialize();
        encoder.prepareContext();
        encoder.encode();
        
        redirectedUrl = response2.getRedirectedUrl();
        assert redirectedUrl != null;
        urlBuilder = new URLBuilder(redirectedUrl);
        Assert.assertEquals(urlBuilder.getScheme(), "http");
        Assert.assertEquals(urlBuilder.getHost(), "example.org");
        Assert.assertEquals(urlBuilder.getPath(), "/response");
        
        queryParams = URISupport.buildQueryMap(urlBuilder.getQueryParams());
        Assert.assertTrue(queryParams.containsKey("foo"));
        Assert.assertEquals(queryParams.get("foo"), "bar");
        Assert.assertTrue(queryParams.containsKey("abc"));
        Assert.assertEquals(queryParams.get("abc"), "123");
        
        Assert.assertTrue(queryParams.containsKey("Signature"));
        Assert.assertNotNull(queryParams.get("Signature"));
        Assert.assertTrue(queryParams.containsKey("SigAlg"));
        Assert.assertEquals(queryParams.get("SigAlg"), SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        Assert.assertTrue(queryParams.containsKey("RelayState"));
        Assert.assertEquals(queryParams.get("RelayState"), "relay");
        Assert.assertTrue(queryParams.containsKey("SAMLResponse"));
        try (InflaterInputStream inflater = 
                new InflaterInputStream(
                        new ByteArrayInputStream(
                                Base64Support.decode(queryParams.get("SAMLResponse"))), new Inflater(true))) {
           
            Document outboundResponse = parserPool.parse(inflater);
            assertXMLEquals(outboundResponse, samlMessage);
        }
        
        String signatureWithParams = queryParams.get("Signature");
        
        // Since the new query params should not be signed, the signature should not change.
        Assert.assertEquals(signatureWithoutParams, signatureWithParams);
        
    }

}