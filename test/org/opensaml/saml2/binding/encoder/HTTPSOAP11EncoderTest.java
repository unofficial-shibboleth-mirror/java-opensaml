/*
 * Copyright [2007] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.saml2.binding.encoder;

import org.joda.time.DateTime;
import org.opensaml.common.BaseTestCase;
import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.common.SAMLVersion;
import org.opensaml.common.binding.encoding.HTTPMessageEncoder;
import org.opensaml.saml2.binding.encoding.HTTPPostEncoder;
import org.opensaml.saml2.binding.encoding.HTTPSOAP11EncoderBuilder;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml2.metadata.Endpoint;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Test for SAML 2 SOAP 1.1 message encoder.
 */
public class HTTPSOAP11EncoderTest extends BaseTestCase {

    /**
     * Tests encoding a SAML message to an servlet response.
     * 
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testResponseEncoding() throws Exception {
        SAMLObjectBuilder<StatusCode> statusCodeBuilder = (SAMLObjectBuilder<StatusCode>) builderFactory
                .getBuilder(StatusCode.DEFAULT_ELEMENT_NAME);
        StatusCode statusCode = statusCodeBuilder.buildObject();
        statusCode.setValue(StatusCode.SUCCESS_URI);

        SAMLObjectBuilder<Status> statusBuilder = (SAMLObjectBuilder<Status>) builderFactory
                .getBuilder(Status.DEFAULT_ELEMENT_NAME);
        Status responseStatus = statusBuilder.buildObject();
        responseStatus.setStatusCode(statusCode);

        SAMLObjectBuilder<Response> responseBuilder = (SAMLObjectBuilder<Response>) builderFactory
                .getBuilder(Response.DEFAULT_ELEMENT_NAME);
        Response samlMessage = responseBuilder.buildObject();
        samlMessage.setID("foo");
        samlMessage.setVersion(SAMLVersion.VERSION_20);
        samlMessage.setIssueInstant(new DateTime(0));
        samlMessage.setStatus(responseStatus);

        SAMLObjectBuilder<Endpoint> endpointBuilder = (SAMLObjectBuilder<Endpoint>) builderFactory
                .getBuilder(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        Endpoint samlEndpoint = endpointBuilder.buildObject();
        samlEndpoint.setBinding(HTTPPostEncoder.BINDING_URI);
        samlEndpoint.setLocation("http://example.org");
        samlEndpoint.setResponseLocation("http://example.org/response");

        HTTPSOAP11EncoderBuilder encoderBuilder = new HTTPSOAP11EncoderBuilder();
        HTTPMessageEncoder encoder = encoderBuilder.buildEncoder();

        MockHttpServletResponse response = new MockHttpServletResponse();
        encoder.setRelyingPartyEndpoint(samlEndpoint);
        encoder.setSamlMessage(samlMessage);
        encoder.setRelayState("relay");
        encoder.setResponse(response);
        encoder.encode();

        assertEquals("Unexpected content type", "text/xml", response.getContentType());
        assertEquals("Unexpected character encoding", response.getCharacterEncoding(), "UTF-8");
        assertEquals("Unexpected cache controls", "no-cache, no-store", response.getHeader("Cache-control"));
        assertEquals("http://www.oasis-open.org/committees/security", response.getHeader("SOAPAction"));
        assertEquals(-1586018121, response.getContentAsString().hashCode());
    }
}