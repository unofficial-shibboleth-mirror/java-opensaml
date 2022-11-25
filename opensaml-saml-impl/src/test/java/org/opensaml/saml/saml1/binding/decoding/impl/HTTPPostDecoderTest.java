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

package org.opensaml.saml.saml1.binding.decoding.impl;

import java.net.MalformedURLException;
import java.net.URL;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.saml1.core.Response;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.DecodingException;
import net.shibboleth.shared.testing.ConstantSupplier;
import net.shibboleth.shared.xml.SerializeSupport;

/**
 * Test case for SAML 1 HTTP POST decoding.
 */
public class HTTPPostDecoderTest extends XMLObjectBaseTestCase {

    private String expectedRelayValue = "relay";

    private HTTPPostDecoder decoder;

    private MockHttpServletRequest httpRequest;
    
    /** Invalid base64 string as it has invalid trailing digits. */
    private final static String INVALID_BASE64_TRAILING = "AB==";

    @BeforeMethod
    protected void setUp() throws Exception {
        httpRequest = new MockHttpServletRequest();
        httpRequest.setMethod("POST");
        httpRequest.setParameter("TARGET", expectedRelayValue);

        decoder = new HTTPPostDecoder();
        decoder.setParserPool(parserPool);
        decoder.setHttpServletRequestSupplier(new ConstantSupplier<>(httpRequest));
        decoder.initialize();
    }

    /**
     * Test decoding message.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testDecode() throws Exception {
        Response samlResponse = (Response) unmarshallElement("/org/opensaml/saml/saml1/binding/Response.xml");

        String deliveredEndpointURL = samlResponse.getRecipient();

        httpRequest.setParameter("SAMLResponse", encodeMessage(samlResponse));

        populateRequestURL(httpRequest, deliveredEndpointURL);

        decoder.decode();
        MessageContext messageContext = decoder.getMessageContext();

        Assert.assertTrue(messageContext.getMessage() instanceof Response);
        Assert.assertEquals(SAMLBindingSupport.getRelayState(messageContext), expectedRelayValue);
    }
    
    /**
     * Test decoding a base64 invalid message. Should throw a {@link MessageDecodingException}
     * wrapping a DecodingException.
     * 
     */
    @Test
    public void testDecodeInvalidResponse() {       
        httpRequest.setParameter("SAMLResponse", INVALID_BASE64_TRAILING);
        try {
            decoder.decode();
        } catch (MessageDecodingException e) {
            if(e.getCause() instanceof DecodingException){
                // pass
            } else {
                Assert.fail("Expected DecodingException type");
            }
        }
    }

    private void populateRequestURL(MockHttpServletRequest request, String requestURL) {
        URL url = null;
        try {
            url = new URL(requestURL);
        } catch (MalformedURLException e) {
            Assert.fail("Malformed URL: " + e.getMessage());
        }
        request.setScheme(url.getProtocol());
        request.setServerName(url.getHost());
        if (url.getPort() != -1) {
            request.setServerPort(url.getPort());
        } else {
            if ("https".equalsIgnoreCase(url.getProtocol())) {
                request.setServerPort(443);
            } else if ("http".equalsIgnoreCase(url.getProtocol())) {
                request.setServerPort(80);
            }
        }
        request.setRequestURI(url.getPath());
        request.setQueryString(url.getQuery());
    }

    protected String encodeMessage(SAMLObject message) throws Exception {
        marshallerFactory.getMarshaller(message).marshall(message);
        String messageStr = SerializeSupport.nodeToString(message.getDOM());

        return Base64Support.encode(messageStr.getBytes("UTF-8"), Base64Support.UNCHUNKED);
    }
}
