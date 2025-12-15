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

import java.io.UnsupportedEncodingException;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.core.Response;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.DecodingException;
import net.shibboleth.shared.testing.ConstantSupplier;

/**
 * Test case for HTTP POST decoders.
 */
public class HTTPPostSimpleSignDecoderTest extends XMLObjectBaseTestCase {
    
    private String expectedRelayValue = "relay";
    
    private HTTPPostSimpleSignDecoder decoder;
    
    private MockHttpServletRequest httpRequest;
    
    /** Invalid base64 string as it has invalid trailing digits. */
    private final static String INVALID_BASE64_TRAILING = "AB==";

    @BeforeMethod
    protected void setUp() throws Exception {
        httpRequest = new MockHttpServletRequest();
        httpRequest.setMethod("POST");
        assert expectedRelayValue != null;
        httpRequest.setParameter("RelayState", expectedRelayValue);
        
        decoder = new HTTPPostSimpleSignDecoder();
        assert parserPool != null;
        decoder.setParserPool(parserPool);
        decoder.setHttpServletRequestSupplier(new ConstantSupplier<>(httpRequest));
        decoder.initialize();
    }
    /**
     * Test decoding a SAML httpRequest.
     * 
     * @throws MessageDecodingException ...
     */
    @Test
    public void testRequestDecoding() throws MessageDecodingException {
        httpRequest.setParameter("SAMLRequest", "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHNhbWxwOkF1dGhuUm"
                + "VxdWVzdCBJRD0iZm9vIiBJc3N1ZUluc3RhbnQ9IjE5NzAtMDEtMDFUMDA6MDA6MDAuMDAwWiIgVmVyc2lvbj0iMi4wIiB4bW"
                + "xuczpzYW1scD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOnByb3RvY29sIi8+");
        
        decoder.decode();
        final MessageContext messageContext = decoder.getMessageContext();
        assert messageContext != null;

        Assert.assertTrue(messageContext.getMessage() instanceof RequestAbstractType);
        Assert.assertEquals(SAMLBindingSupport.getRelayState(messageContext), expectedRelayValue);
        Assert.assertNull(messageContext.ensureSubcontext(SimpleSignatureContext.class).getSignedContent());
    }
    
    /**
     * Test decoding a SAML Response message incorrectly supplied in a "SAMLRequest" param.
     * 
     * @throws MessageDecodingException ...
     */
    @Test(expectedExceptions = MessageDecodingException.class)
    public void testSAMLRequestParamHoldsResponse() throws MessageDecodingException {
        httpRequest.setParameter("SAMLRequest", "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHNhbWxwOlJlc3Bvbn"
                + "NlIElEPSJmb28iIElzc3VlSW5zdGFudD0iMTk3MC0wMS0wMVQwMDowMDowMC4wMDBaIiBWZXJzaW9uPSIyLjAiIHhtbG5zOnN"
                + "hbWxwPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6cHJvdG9jb2wiPjxzYW1scDpTdGF0dXM+PHNhbWxwOlN0YXR1c0Nv"
                + "ZGUgVmFsdWU9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDpzdGF0dXM6U3VjY2VzcyIvPjwvc2FtbHA6U3RhdHVzPjwvc"
                + "2FtbHA6UmVzcG9uc2U+");
        
        decoder.decode();
    }
 
    /**
     * Test decoding a SAML httpRequest.
     * 
     * @throws MessageDecodingException ...
     * @throws UnsupportedEncodingException ...
     * @throws DecodingException  ...
     */
    @Test
    public void testRequestDecodingWithSignature() throws MessageDecodingException, UnsupportedEncodingException, DecodingException {
        httpRequest.setParameter("SAMLRequest", "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHNhbWxwOkF1dGhuUm"
                + "VxdWVzdCBJRD0iZm9vIiBJc3N1ZUluc3RhbnQ9IjE5NzAtMDEtMDFUMDA6MDA6MDAuMDAwWiIgVmVyc2lvbj0iMi4wIiB4bW"
                + "xuczpzYW1scD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOnByb3RvY29sIi8+");
        httpRequest.setParameter("SigAlg", "TheAlgorithm");
        httpRequest.setParameter("Signature", "TheSignature");
        // Note RelayState is already set to 'relay'
        
        decoder.decode();
        final MessageContext messageContext = decoder.getMessageContext();
        assert messageContext != null;

        Assert.assertTrue(messageContext.getMessage() instanceof RequestAbstractType);
        Assert.assertEquals(SAMLBindingSupport.getRelayState(messageContext), expectedRelayValue);
        Assert.assertNotNull(messageContext.ensureSubcontext(SimpleSignatureContext.class).getSignedContent());
        
        final String requestValue = httpRequest.getParameter("SAMLRequest");
        assert requestValue != null;
        final byte[] expectedSignedContent = new StringBuilder()
                .append("SAMLRequest=" + new String(Base64Support.decode(requestValue), "UTF-8"))
                .append("&")
                .append("RelayState=" + httpRequest.getParameter("RelayState"))
                .append("&")
                .append("SigAlg=" + httpRequest.getParameter("SigAlg"))
                .toString().getBytes("UTF-8");
        //System.err.println("Actual:   " + new String(messageContext.ensureSubcontext(SimpleSignatureContext.class).getSignedContent(), "UTF-8"));
        //System.err.println("Expected: " + new String(expectedSignedContent, "UTF-8"));
        Assert.assertEquals(messageContext.ensureSubcontext(SimpleSignatureContext.class).getSignedContent(), expectedSignedContent);
    }
    
    /**
     * Test decoding a Base64 invalid SAML Request. Should throw a {@link MessageDecodingException} wrapping
     * a {@link DecodingException}.
     */
    @Test
    public void testInvalidRequestDecoding()  {
        httpRequest.setParameter("SAMLRequest", INVALID_BASE64_TRAILING);        
        try {
            decoder.decode();
        } catch (MessageDecodingException e) {
            if(e.getCause() instanceof DecodingException){
                //pass
            } else {
                Assert.fail("Expected DecodingException type");
            }
        }        
    }

    /**
     * Test decoding a SAML response.
     * 
     * @throws MessageDecodingException ...
     */
    @Test
    public void testResponseDecoding() throws MessageDecodingException {
        httpRequest.setParameter("SAMLResponse", "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHNhbWxwOlJlc3Bvbn"
                + "NlIElEPSJmb28iIElzc3VlSW5zdGFudD0iMTk3MC0wMS0wMVQwMDowMDowMC4wMDBaIiBWZXJzaW9uPSIyLjAiIHhtbG5zOnN"
                + "hbWxwPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6cHJvdG9jb2wiPjxzYW1scDpTdGF0dXM+PHNhbWxwOlN0YXR1c0Nv"
                + "ZGUgVmFsdWU9InVybjpvYXNpczpuYW1lczp0YzpTQU1MOjIuMDpzdGF0dXM6U3VjY2VzcyIvPjwvc2FtbHA6U3RhdHVzPjwvc"
                + "2FtbHA6UmVzcG9uc2U+");

        decoder.decode();
        final MessageContext messageContext = decoder.getMessageContext();
        assert messageContext != null;

        Assert.assertTrue(messageContext.getMessage() instanceof Response);
        Assert.assertEquals(SAMLBindingSupport.getRelayState(messageContext), expectedRelayValue);
        Assert.assertNull(messageContext.ensureSubcontext(SimpleSignatureContext.class).getSignedContent());
    }
    
    /**
     * Test decoding a SAML request message incorrectly supplied in a "SAMLResponse" param.
     * 
     * @throws MessageDecodingException ...
     */
    @Test(expectedExceptions = MessageDecodingException.class)
    public void testSAMLResponseParamHoldsRequest() throws MessageDecodingException {
        httpRequest.setParameter("SAMLResponse", "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4KPHNhbWxwOkF1dGhuUm"
                + "VxdWVzdCBJRD0iZm9vIiBJc3N1ZUluc3RhbnQ9IjE5NzAtMDEtMDFUMDA6MDA6MDAuMDAwWiIgVmVyc2lvbj0iMi4wIiB4bW"
                + "xuczpzYW1scD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOnByb3RvY29sIi8+");

        decoder.decode();
    }
    
}
