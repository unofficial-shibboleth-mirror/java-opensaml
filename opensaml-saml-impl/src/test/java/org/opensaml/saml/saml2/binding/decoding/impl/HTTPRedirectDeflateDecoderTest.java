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

package org.opensaml.saml.saml2.binding.decoding.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.annotation.Nonnull;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.core.Response;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.EncodingException;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.net.URISupport;
import net.shibboleth.shared.testing.ConstantSupplier;
import net.shibboleth.shared.xml.SerializeSupport;

@SuppressWarnings("javadoc")
public class HTTPRedirectDeflateDecoderTest extends XMLObjectBaseTestCase {

    private String expectedRelayValue = "relay";

    private HTTPRedirectDeflateDecoder decoder;

    private MockHttpServletRequest httpRequest;
    
    /** Invalid base64 string as it has invalid trailing digits. */
    private final static String INVALID_BASE64_TRAILING = "AB==";

    @BeforeMethod
    protected void setUp() throws Exception {
        httpRequest = new MockHttpServletRequest();
        httpRequest.setMethod("GET");
        httpRequest.setParameter("RelayState", expectedRelayValue);

        decoder = new HTTPRedirectDeflateDecoder();
        decoder.setParserPool(parserPool);
        decoder.setHttpServletRequestSupplier(new ConstantSupplier<>(httpRequest));
        decoder.initialize();
    }      

    @Test
    public void testResponseDecoding() throws MessageDecodingException {
        // Note, Spring's Mock objects don't do URL encoding/decoding, so this is the URL decoded form
        httpRequest
                .setParameter(
                        "SAMLResponse",
                        "fZAxa8NADIX3/opDe3yXLG2F7VASCoF2qdMM3Y6LkhrOp8PSlfz8uqYdvBTeIMHT08ert7chmi8apefUwLpyYCgFPvfp2sD78Xn1ANv2rhY/xIxvJJmTkNmTaJ+8zkefqhmtpZsfcqSKxyuYw76BC/M0iBQ6JFGfdMp/vHcrt550dA5nVc65DzCnP4TND8IElQTnpw2UMSF76QWTH0hQA3ZPry84OTGPrBw4QvuL2KnXIsttx2cyJx8L/R8msxu7EgKJgG1ruwy1yxrabw==");

        populateRequestURL(httpRequest, "http://example.org");

        decoder.decode();
        final MessageContext messageContext = decoder.getMessageContext();
        assert messageContext != null;

        Assert.assertTrue(messageContext.getMessage() instanceof Response);
        Assert.assertEquals(SAMLBindingSupport.getRelayState(messageContext), expectedRelayValue);
        Assert.assertNull(messageContext.ensureSubcontext(SimpleSignatureContext.class).getSignedContent());
    }    
   
    @Test
    public void testRequestDecoding() throws MessageDecodingException, MessageEncodingException, 
                                                            MarshallingException, EncodingException {
        final AuthnRequest samlRequest =
                (AuthnRequest) unmarshallElement("/org/opensaml/saml/saml2/binding/AuthnRequest.xml");
        assert samlRequest != null;
        samlRequest.setDestination(null);

        httpRequest.setParameter("SAMLRequest", encodeMessage(samlRequest));

        decoder.decode();
        final MessageContext messageContext = decoder.getMessageContext();
        assert messageContext != null;

        Assert.assertTrue(messageContext.getMessage() instanceof RequestAbstractType);
        Assert.assertEquals(SAMLBindingSupport.getRelayState(messageContext), expectedRelayValue);
        Assert.assertNull(messageContext.ensureSubcontext(SimpleSignatureContext.class).getSignedContent());
    }
    
    @Test
    public void testRequestDecodingWithSignature() throws MessageDecodingException, MessageEncodingException, 
                                                            MarshallingException, EncodingException, UnsupportedEncodingException {
        final AuthnRequest samlRequest =
                (AuthnRequest) unmarshallElement("/org/opensaml/saml/saml2/binding/AuthnRequest.xml");
        assert samlRequest != null;
        samlRequest.setDestination(null);

        httpRequest.setParameter("SAMLRequest", encodeMessage(samlRequest));
        httpRequest.setParameter("SigAlg", "TheAlgorithm");
        httpRequest.setParameter("Signature", "TheSignature");
        // Note RelayState is already set to 'relay'
        
        String query = URISupport.buildQuery(CollectionSupport.listOf(
                new Pair<>("SAMLRequest", httpRequest.getParameter("SAMLRequest")),
                new Pair<>("SigAlg", httpRequest.getParameter("SigAlg")),
                new Pair<>("Signature", httpRequest.getParameter("Signature")),
                new Pair<>("RelayState", httpRequest.getParameter("RelayState"))
                ));
        httpRequest.setQueryString(query);

        decoder.decode();
        final MessageContext messageContext = decoder.getMessageContext();
        assert messageContext != null;

        Assert.assertTrue(messageContext.getMessage() instanceof RequestAbstractType);
        Assert.assertEquals(SAMLBindingSupport.getRelayState(messageContext), expectedRelayValue);
        Assert.assertNotNull(messageContext.ensureSubcontext(SimpleSignatureContext.class).getSignedContent());
        
        final byte[] expectedSignedContent = new StringBuilder()
                .append(URISupport.getRawQueryStringParameter(httpRequest.getQueryString(), "SAMLRequest"))
                .append("&")
                .append(URISupport.getRawQueryStringParameter(httpRequest.getQueryString(), "RelayState"))
                .append("&")
                .append(URISupport.getRawQueryStringParameter(httpRequest.getQueryString(), "SigAlg"))
                .toString().getBytes("UTF-8");
        //System.err.println("Actual:   " + new String(messageContext.ensureSubcontext(SimpleSignatureContext.class).getSignedContent(), "UTF-8"));
        //System.err.println("Expected: " + new String(expectedSignedContent, "UTF-8"));
        Assert.assertEquals(messageContext.ensureSubcontext(SimpleSignatureContext.class).getSignedContent(), expectedSignedContent);
    }
    
    /**
     * Test decoding a Base64 invalid SAML Request.
     * 
     * @throws MessageDecodingException decoding exception, which is expected.
     */
    @Test(expectedExceptions = MessageDecodingException.class)
    public void testInvalidRequestDecoding() throws MessageDecodingException {
        httpRequest.setParameter("SAMLRequest", INVALID_BASE64_TRAILING);        
        decoder.decode();    
    } 

    @Test
    public void testExplicitDefaultSAMLEncoding() 
            throws MessageDecodingException, MessageEncodingException, MarshallingException, EncodingException {
        final AuthnRequest samlRequest =
                (AuthnRequest) unmarshallElement("/org/opensaml/saml/saml2/binding/AuthnRequest.xml");
        assert samlRequest != null;
        samlRequest.setDestination(null);

        httpRequest.setParameter("SAMLRequest", encodeMessage(samlRequest));
        httpRequest.setParameter("SAMLEncoding", "urn:oasis:names:tc:SAML:2.0:bindings:URL-Encoding:DEFLATE");

        decoder.decode();
        final MessageContext messageContext = decoder.getMessageContext();
        assert messageContext != null;

        Assert.assertTrue(messageContext.getMessage() instanceof RequestAbstractType);
        Assert.assertEquals(SAMLBindingSupport.getRelayState(messageContext), expectedRelayValue);
    }

    @Test(expectedExceptions=MessageDecodingException.class)
    public void testUnsupportedSAMLEncoding() 
            throws MessageDecodingException, MessageEncodingException, MarshallingException, EncodingException {
        final AuthnRequest samlRequest =
                (AuthnRequest) unmarshallElement("/org/opensaml/saml/saml2/binding/AuthnRequest.xml");
        assert samlRequest != null;
        samlRequest.setDestination(null);

        httpRequest.setParameter("SAMLRequest", encodeMessage(samlRequest));
        httpRequest.setParameter("SAMLEncoding", "urn:test:encoding:bogus");

        decoder.decode();
    }

    private void populateRequestURL(MockHttpServletRequest request, String requestURL) {
        URL url = null;
        try {
            url = new URL(requestURL);
        } catch (MalformedURLException e) {
            Assert.fail("Malformed URL: " + e.getMessage());
        }
        assert url != null;
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

    protected String encodeMessage(@Nonnull final SAMLObject message) throws MessageEncodingException, MarshallingException, EncodingException {
        try {
            final Element dom = marshallerFactory.ensureMarshaller(message).marshall(message);
            String messageStr = SerializeSupport.nodeToString(dom);

            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            Deflater deflater = new Deflater(Deflater.DEFLATED, true);
            DeflaterOutputStream deflaterStream = new DeflaterOutputStream(bytesOut, deflater);
            deflaterStream.write(messageStr.getBytes("UTF-8"));
            deflaterStream.finish();

            return Base64Support.encode(bytesOut.toByteArray(), Base64Support.UNCHUNKED);
        } catch (IOException e) {
            throw new MessageEncodingException("Unable to DEFLATE and Base64 encode SAML message", e);
        }
    }
    
}