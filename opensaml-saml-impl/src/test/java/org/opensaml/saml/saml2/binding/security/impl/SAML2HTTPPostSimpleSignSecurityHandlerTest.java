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

package org.opensaml.saml.saml2.binding.security.impl;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.XMLConstants;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.common.testing.SAMLTestSupport;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.binding.decoding.impl.HTTPPostSimpleSignDecoder;
import org.opensaml.saml.saml2.binding.decoding.impl.SimpleSignatureContext;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPPostSimpleSignEncoder;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.impl.CollectionCredentialResolver;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.X509Support;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.codec.DecodingException;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.testing.ConstantSupplier;
import net.shibboleth.shared.xml.ElementSupport;
import net.shibboleth.shared.xml.XMLParserException;
import net.shibboleth.shared.xml.impl.BasicParserPool;

/**
 * Test SAML simple signature via HTTP POST-SimpleSign binding
 */
public class SAML2HTTPPostSimpleSignSecurityHandlerTest extends XMLObjectBaseTestCase {
    
    private MessageContext messageContext;
    
    private SAML2HTTPPostSimpleSignSecurityHandler handler;
    
    private X509Certificate signingCert;

    private String signingCertBase64 = "MIIDzjCCAragAwIBAgIBMTANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl"
            + "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDUyMTE4MjM0MFoX"
            + "DTE3MDUxODE4MjM0MFowMTESMBAGA1UEChMJSW50ZXJuZXQyMRswGQYDVQQDExJm"
            + "b29iYXIuZXhhbXBsZS5vcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB"
            + "AQDNWnkFmhy1vYa6gN/xBRKkZxFy3sUq2V0LsYb6Q3pe9Qlb6+BzaM5DrN8uIqqr"
            + "oBE3Wp0LtrgKuQTpDpNFBdS2p5afiUtOYLWBDtizTOzs3Z36MGMjIPUYQ4s03IP3"
            + "yPh2ud6EKpDPiYqzNbkRaiIwmYSit5r+RMYvd6fuKvTOn6h7PZI5AD7Rda7VWh5O"
            + "VSoZXlRx3qxFho+mZhW0q4fUfTi5lWwf4EhkfBlzgw/k5gf4cOi6rrGpRS1zxmbt"
            + "X1RAg+I20z6d04g0N2WsK5stszgYKoIROJCiXwjraa8/SoFcILolWQpttVHBIUYl"
            + "yDlm8mIFleZf4ReFpfm+nUYxAgMBAAGjgfQwgfEwCQYDVR0TBAIwADAsBglghkgB"
            + "hvhCAQ0EHxYdT3BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYE"
            + "FDgRgTkjaKoK6DoZfUZ4g9LDJUWuMFUGA1UdIwROMEyAFNXuZVPeUdqHrULqQW7y"
            + "r9buRpQLoTGkLzAtMRIwEAYDVQQKEwlJbnRlcm5ldDIxFzAVBgNVBAMTDmNhLmV4"
            + "YW1wbGUub3JnggEBMEAGA1UdEQQ5MDeCEmFzaW1vdi5leGFtcGxlLm9yZ4YbaHR0"
            + "cDovL2hlaW5sZWluLmV4YW1wbGUub3JnhwQKAQIDMA0GCSqGSIb3DQEBBQUAA4IB"
            + "AQBLiDMyQ60ldIytVO1GCpp1S1sKJyTF56GVxHh/82hiRFbyPu+2eSl7UcJfH4ZN"
            + "bAfHL1vDKTRJ9zoD8WRzpOCUtT0IPIA/Ex+8lFzZmujO10j3TMpp8Ii6+auYwi/T"
            + "osrfw1YCxF+GI5KO49CfDRr6yxUbMhbTN+ssK4UzFf36UbkeJ3EfDwB0WU70jnlk"
            + "yO8f97X6mLd5QvRcwlkDMftP4+MB+inTlxDZ/w8NLXQoDW6p/8r91bupXe0xwuyE"
            + "vow2xjxlzVcux2BZsUZYjBa07ZmNNBtF7WaQqH7l2OBCAdnBhvme5i/e0LK3Ivys" + "+hcVyvCXs5XtFTFWDAVYvzQ6";

    private PrivateKey signingPrivateKey;

    private String signingPrivateKeyBase64 = "MIIEogIBAAKCAQEAzVp5BZoctb2GuoDf8QUSpGcRct7FKtldC7GG+kN6XvUJW+vg"
            + "c2jOQ6zfLiKqq6ARN1qdC7a4CrkE6Q6TRQXUtqeWn4lLTmC1gQ7Ys0zs7N2d+jBj"
            + "IyD1GEOLNNyD98j4drnehCqQz4mKszW5EWoiMJmEorea/kTGL3en7ir0zp+oez2S"
            + "OQA+0XWu1VoeTlUqGV5Ucd6sRYaPpmYVtKuH1H04uZVsH+BIZHwZc4MP5OYH+HDo"
            + "uq6xqUUtc8Zm7V9UQIPiNtM+ndOINDdlrCubLbM4GCqCETiQol8I62mvP0qBXCC6"
            + "JVkKbbVRwSFGJcg5ZvJiBZXmX+EXhaX5vp1GMQIDAQABAoIBAC1P4lZvHBiqGll6"
            + "6G8pXGS0bXA4Ya9DyTk0UgFU9GKRlSAYWy18Gc9rDNAETD6Uklfxgae9CL0s+D1o"
            + "vuxDDh3DuwO26sv/oO06Vmyx87GMcThshuOQeSSCeuwOIHyDdvfTqZrmPY/d3KIQ"
            + "n6aNEcBBj7fL5cJncIe20nJGPkB9KuTAaGVnaKoOesxgWBr7SvjGq/SB7bRE1B3c"
            + "QxwUDWHkF0LljSIkXaV9ehKJcgBY2fV0rc8pI53WsUXEXk5HoqYZnQ5QjAZ4Hf2s"
            + "bRKevq+D2ENK+OuKNuCAS/oJbGSdS7q0/6jgHZ6cUGXi1r2qEEG7PIorCoSMkWQS"
            + "M1wMX0ECgYEA9c6/s9lKDrjzyjO9rlxzufGVRDjffiUZ1o8F3RD3JltdPLVcd429"
            + "CvGSNV730Yr/wSyRAum4vkGnmOR9tuQdi3PJHt3xGRsymTT5ym/5fnC4SvXVSR6v"
            + "LFPUY80yj+D6/0lwIaGE7x4JOclMXnHjqcpRl14onOjY844WORhxgjkCgYEA1d5N"
            + "Tqp938UbZYKX4Q9UvLf/pVR9xOFOCYnMywAFk0WnkUBPHmPoJuFgeNGeQ7gCmHi7"
            + "JFzwBjkj6DcGMdbXKWiUij1BoRxf9Mof+fZBWVSKw+/yVLbJkyK951+nywyiq3HC"
            + "NBti1eK/h/hXQd8t+dCBmDGj1ba1C2/3JZqLg7kCgYArxD1D85uJFYtq5F2Qryt3"
            + "3zj5pbq9hjOcjWi43O10qe3nAk/NhbI0QaEL2bX8XGh/Z8UGJMFdNul1grGTn/hW"
            + "vS4BTflAxCP1PYaAcgGVbtKRnkX0t/7uwJpfjsjC74chb10Ez/KQdOOlo17yrgqg"
            + "T8LJVd2bWqZOb20ri1uimQKBgFfJYSg6OWLh0IYRXfBmz5yLVmdx0BJBfTvTEXn+"
            + "L0utWsP3hsJttfxHpMbTHEilvoMBg6fAclHLoJ6P/33ztuvrXpWD4W2VbRnY4dlD"
            + "qL1XQ4J7+pelVAaOSy8vB3wEWr1O+61R1HcBFSdl28NRLdkOKjPjpGF0Fsp0Ehmg"
            + "X0YZAoGAXrM4+BUvcx2PLaeneTJoRdOi3GQbdAte03maDU6C474IdgR8IUygfspv"
            + "3fiGue9Wmk5ybUBlv/D6sIWVhnnedWsg2zAgZPfZ78HLLNhWeEx33wPFiK0wV5MJ"
            + "XQ224gQ5t9D3WXdZtmAxXIFoopj4zToCMBjXyep0u7zl3s7s00U=";

    private X509Certificate otherCert1;

    private String otherCert1Base64 = "MIIECTCCAvGgAwIBAgIBMzANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl"
            + "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDUyNTIwMTYxMVoX"
            + "DTE3MDUyMjIwMTYxMVowGjEYMBYGA1UEAxMPaWRwLmV4YW1wbGUub3JnMIIBtjCC"
            + "ASsGByqGSM44BAEwggEeAoGBAI+ktw7R9m7TxjaCrT2MHwWNQUAyXPrqbFCcu+DC"
            + "irr861U6R6W/GyqWdcy8/D1Hh/I1U94POQn5yfqVPpVH2ZRS4OMFndHWaoo9V5LJ"
            + "oXTXHiDYB3W4t9tn0fm7It0n7VoUI5C4y9LG32Hq+UIGF/ktNTmo//mEqLS6aJNd"
            + "bMFpAhUArmKGh0hcpmjukYArWcMRvipB4CMCgYBuCiCrUaHBRRtqrk0P/Luq0l2M"
            + "2718GwSGeLPZip06gACDG7IctMrgH1J+ZIjsx6vffi977wnMDiktqacmaobV+SCR"
            + "W9ijJRdkYpUHmlLvuJGnDPjkvewpbGWJsCabpWEvWdYw3ma8RuHOPj4Jkrdd4VcR"
            + "aFwox/fPJ7cG6kBydgOBhAACgYBxQIPv9DCsmiMHG1FAxSARX0GcRiELJPJ+MtaS"
            + "tdTrVobNa2jebwc3npLiTvUR4U/CDo1mSZb+Sp/wian8kNZHmGcR6KbtJs9UDsa3"
            + "V0pbbgpUar4HcxV+NQJBbhn9RGu85g3PDILUrINiUAf26mhPN5Y0paM+HbM68nUf"
            + "1OLv16OBsjCBrzAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdl"
            + "bmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQUIHFAEB/3jIIZzJEJ/qdsuI8v"
            + "N3kwVQYDVR0jBE4wTIAU1e5lU95R2oetQupBbvKv1u5GlAuhMaQvMC0xEjAQBgNV"
            + "BAoTCUludGVybmV0MjEXMBUGA1UEAxMOY2EuZXhhbXBsZS5vcmeCAQEwDQYJKoZI"
            + "hvcNAQEFBQADggEBAJt4Q34+pqjW5tHHhkdzTITSBjOOf8EvYMgxTMRzhagLSHTt"
            + "9RgO5i/G7ELvnwe1j6187m1XD9iEAWKeKbB//ljeOpgnwzkLR9Er5tr1RI3cbil0"
            + "AX+oX0c1jfRaQnR50Rfb5YoNX6G963iphlxp9C8VLB6eOk/S270XoWoQIkO1ioQ8"
            + "JY4HE6AyDsOpJaOmHpBaxjgsiko52ZWZeZyaCyL98BXwVxeml7pYnHlXWWidB0N/"
            + "Zy+LbvWg3urUkiDjMcB6nGImmEfDSxRdybitcMwbwL26z2WOpwL3llm3mcCydKXg"
            + "Xt8IQhfDhOZOHWckeD2tStnJRP/cqBgO62/qirw=";

    private CollectionCredentialResolver credResolver;

    private List<Credential> trustedCredentials;

    private BasicX509Credential signingX509Cred;

    private BasicX509Credential otherCred1;

    private String issuer;

    private String expectedRelayValue = "cookieMonster";
    
    private SignatureTrustEngine signatureTrustEngine;
    
    private SignatureValidationParameters sigValParams;

    /** Velocity template engine. */
    private VelocityEngine velocityEngine;
    
    /** Invalid base64 string as it has invalid trailing digits. */
    private final static String INVALID_BASE64_TRAILING = "AB==";

    /**
     * Constructor.
     * 
     * @throws Exception if something goes wrong
     */
    public SAML2HTTPPostSimpleSignSecurityHandlerTest() throws Exception {
        issuer = "SomeCoolIssuer";
        signingCert = X509Support.decodeCertificate(signingCertBase64);
        signingPrivateKey = KeySupport.buildJavaRSAPrivateKey(signingPrivateKeyBase64);

        signingX509Cred = new BasicX509Credential(signingCert, signingPrivateKey);
        signingX509Cred.setEntityId(issuer);

        otherCert1 = X509Support.decodeCertificate(otherCert1Base64);

        otherCred1 = new BasicX509Credential(otherCert1);
        otherCred1.setEntityCertificate(otherCert1);
        otherCred1.setEntityId("other-1");

        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init();
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        // Trust engine setup

        trustedCredentials = new ArrayList<>();
        trustedCredentials.add(otherCred1);

        credResolver = new CollectionCredentialResolver(trustedCredentials);

        final KeyInfoCredentialResolver kiResolver = SAMLTestSupport.buildBasicInlineKeyInfoResolver();
        signatureTrustEngine = new ExplicitKeySignatureTrustEngine(credResolver, kiResolver);
        
        sigValParams = new SignatureValidationParameters();
        sigValParams.setSignatureTrustEngine(signatureTrustEngine);
        
        final HttpServletRequest request = buildServletRequest();
        
        final HTTPPostSimpleSignDecoder decoder = new HTTPPostSimpleSignDecoder();
        decoder.setHttpServletRequestSupplier(new ConstantSupplier<>(request));
        decoder.setParserPool(parserPool);
        decoder.initialize();
        
        decoder.decode();

        messageContext = decoder.getMessageContext();
        assert messageContext != null;
        messageContext.setMessage(buildInboundSAMLMessage());
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class).setEntityId(issuer);
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class).setRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        messageContext.ensureSubcontext(SAMLProtocolContext.class).setProtocol(SAMLConstants.SAML20P_NS);
        messageContext.ensureSubcontext(SecurityParametersContext.class).setSignatureValidationParameters(sigValParams);

        handler = new SAML2HTTPPostSimpleSignSecurityHandler();
        handler.setHttpServletRequestSupplier(new ConstantSupplier<>(request));
        handler.setParser(parserPool);
        handler.setKeyInfoResolver(kiResolver);
        handler.initialize();
        
    }

    /**
     * Test context issuer set, valid signature with trusted credential.
     * 
     * @throws MessageHandlerException ...
     */
    @Test
    public void testSuccess() throws MessageHandlerException {
        trustedCredentials.add(signingX509Cred);

        handler.invoke(messageContext);
        
        Assert.assertEquals(messageContext.ensureSubcontext(SAMLPeerEntityContext.class).getEntityId(), issuer, 
                "Unexpected value for Issuer found");
        //TODO before this was evaling isInboundSAMLMessageAuthenticated
        Assert.assertTrue(messageContext.ensureSubcontext(SAMLPeerEntityContext.class).isAuthenticated(), 
                "Unexpected value for context authentication state");
    }

    /**
     * Test blacklisted signature algorithm.
     * 
     * @throws MessageHandlerException ...
     */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testBlacklistedSignatureAlgorithm() throws MessageHandlerException {
        sigValParams.setExcludedAlgorithms(CollectionSupport.singleton(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1));
        
        trustedCredentials.add(signingX509Cred);

        handler.invoke(messageContext);
    }
    
    /**
     * Test context issuer set, valid signature with untrusted credential.
     * 
     * @throws MessageHandlerException ...
     */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testUntrustedCredential() throws MessageHandlerException {
        handler.invoke(messageContext);
    }

    /**
     * Test context issuer set, invalid signature with trusted credential.
     * 
     * @throws MessageHandlerException ...
     * @throws UnsupportedEncodingException ...
     */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testInvalidSignature() throws MessageHandlerException, UnsupportedEncodingException {
        trustedCredentials.add(signingX509Cred);

        // Note: this is just for posterity and clarity as to what's going on. Can't manipulate the request anymore to cause signature failure,
        // since the signed content is now obtained from the message context, as populated by the decoder.
        final MockHttpServletRequest request = (MockHttpServletRequest) handler.getHttpServletRequest();
        request.setParameter("RelayState", "AlteredData" + request.getParameter("RelayState"));
        
        // This actually causes the expected signature failure
        final String origSignedContent = new String(messageContext.ensureSubcontext(SimpleSignatureContext.class).getSignedContent(), "UTF-8");
        final String badSignedContent = origSignedContent.replaceFirst("RelayState=", "RelayState=AlteredData");
        //System.err.println("Actual:   " + origSignedContent);
        //System.err.println("Expected: " + badSignedContent);
        messageContext.ensureSubcontext(SimpleSignatureContext.class).setSignedContent(badSignedContent.getBytes());
        
        handler.invoke(messageContext);
    }
    
    /**
     * Test invalid base64 encoded signature. Should throw a MessageHandlerException with wrapped
     * {@link DecodingException}.
     */
    @Test
    public void testInvalidBase64RequestSignature()  {  
        try {
            final MockHttpServletRequest request = (MockHttpServletRequest) handler.getHttpServletRequest();
            request.setParameter("Signature", INVALID_BASE64_TRAILING);        
            handler.invoke(messageContext);
        } catch (MessageHandlerException e) {
            if(e.getCause() instanceof DecodingException){
                // pass
            } else {
                Assert.fail("Expected DecodingException type");
            }
        }
    }
    
    /**
     * Test invalid base64 encoded KeyInfo. Should throw a MessageHandlerException with wrapped
     * {@link DecodingException}.
     */
    @Test
    public void testInvalidBase64KeyInfo()  {  
        try {
            final MockHttpServletRequest request = (MockHttpServletRequest) handler.getHttpServletRequest();
            request.setParameter("KeyInfo", INVALID_BASE64_TRAILING);        
            handler.invoke(messageContext);
        } catch (MessageHandlerException e) {
            if(e.getCause() instanceof DecodingException){
                // pass
            } else {
                Assert.fail("Expected DecodingException type");
            }
        }
    }

    /**
     * Test context issuer set, valid signature with untrusted credential.
     * 
     * @throws MessageHandlerException ...
     */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testNoContextIssuer() throws MessageHandlerException {
        messageContext.removeSubcontext(SAMLPeerEntityContext.class);
        handler.invoke(messageContext);
    }

    /**
     * Test context issuer set, valid signature with trusted credential.
     * 
     * @throws MessageHandlerException ...
     */
    @Test
    public void testSuccessNoKeyInfo() throws MessageHandlerException {
        trustedCredentials.add(signingX509Cred);

        final MockHttpServletRequest request = (MockHttpServletRequest) handler.getHttpServletRequest();
        request.removeParameter("KeyInfo");

        handler.invoke(messageContext);
        
        Assert.assertEquals(messageContext.ensureSubcontext(SAMLPeerEntityContext.class).getEntityId(), issuer, 
                "Unexpected value for Issuer found");
        //TODO before this was evaling isInboundSAMLMessageAuthenticated
        Assert.assertTrue(messageContext.ensureSubcontext(SAMLPeerEntityContext.class).isAuthenticated(), 
                "Unexpected value for context authentication state");
    }

    /**
     * Test context issuer set, valid signature with trusted credential.
     * 
     * @throws MessageHandlerException ...
     */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testFailureNoKeyInfo() throws MessageHandlerException {
        final MockHttpServletRequest request = (MockHttpServletRequest) handler.getHttpServletRequest();
        request.removeParameter("KeyInfo");

        handler.invoke(messageContext);
    }
    
    /**
     * Test no trust engine supplied.
     * 
     * @throws MessageHandlerException ...
     */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testNoTrustEngine() throws MessageHandlerException {
        messageContext.removeSubcontext(SecurityParametersContext.class);
        
        handler.invoke(messageContext);
    }

    protected AuthnRequest buildInboundSAMLMessage() {
        return unmarshallElement("/org/opensaml/saml/saml2/binding/AuthnRequest.xml");
    }

    protected MockHttpServletRequest buildServletRequest() {
        //
        // Encode the "outbound" message context, with simple signature
        //
        SAMLObjectBuilder<AssertionConsumerService> endpointBuilder =
                (SAMLObjectBuilder<AssertionConsumerService>) builderFactory.<AssertionConsumerService>ensureBuilder(
                        AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        AssertionConsumerService samlEndpoint = endpointBuilder.buildObject();
        samlEndpoint.setLocation("http://example.org");
        samlEndpoint.setResponseLocation("http://example.org/response");
        
        final MessageContext mc = new MessageContext();
        mc.setMessage(buildInboundSAMLMessage());
        SAMLBindingSupport.setRelayState(mc, expectedRelayValue);
        mc.ensureSubcontext(SAMLPeerEntityContext.class)
            .ensureSubcontext(SAMLEndpointContext.class).setEndpoint(samlEndpoint);
        
        final SignatureSigningParameters signingParameters = new SignatureSigningParameters();
        signingParameters.setSigningCredential(signingX509Cred);
        signingParameters.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        mc.ensureSubcontext(SecurityParametersContext.class).setSignatureSigningParameters(signingParameters);
        
        final MockHttpServletResponse response = new MockHttpServletResponse();
        
        final HTTPPostSimpleSignEncoder encoder = new HTTPPostSimpleSignEncoder();
        encoder.setMessageContext(mc);
        encoder.setHttpServletResponseSupplier(new ConstantSupplier<>(response));
        
        encoder.setVelocityEngine(velocityEngine);
        encoder.setVelocityTemplateId("/templates/saml2-post-simplesign-binding.vm");
        
        try {
            encoder.initialize();
        } catch (ComponentInitializationException e1) {
            Assert.fail("Failed to initialize encoder");
        }

        try {
            encoder.prepareContext();
            encoder.encode();
        } catch (MessageEncodingException e) {
            Assert.fail("Could not encode outbound message context");
        }

        // Now populate the new "inbound" message context with the "outbound" encoded info
        final MockHttpServletRequest request = new MockHttpServletRequest();

        request.setMethod("POST");

        String contentString = null;
        try {
            contentString = response.getContentAsString();
        } catch (UnsupportedEncodingException e) {
            Assert.fail("Could not get content string from response object");
        }
        // System.out.println("Content String: " + contentString);

        try {
            populateRequest(request, contentString);
        } catch (Exception e) {
            Assert.fail("Could not populate mock request with form data");
        }

        return request;
    }

    /**
     * Populate the HTTP servlet request object with the parameter info. Would be nice if Spring mock object actually
     * did this for you from populating the content string.... Oh well.
     * 
     * @param request ...
     * @param htmlContentString ...
     * 
     * @throws XMLParserException ...
     * @throws ComponentInitializationException ...
     */
    private void populateRequest(MockHttpServletRequest request, String htmlContentString) throws XMLParserException, ComponentInitializationException {
        request.setContent(htmlContentString.getBytes());
        
        // Encoder template now carries HTML 5 DOCTYPE, so have to use a parser pool that allows this.
        HashMap<String, Boolean> features = new HashMap<>();
        features.put(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        features.put("http://apache.org/xml/features/disallow-doctype-decl", false);
        BasicParserPool pp = new BasicParserPool();
        pp.setBuilderFeatures(features);
        pp.initialize();

        Document doc = pp.parse(new ByteArrayInputStream(htmlContentString.getBytes()));
        // html
        Element current = doc.getDocumentElement();
        // head
        current = ElementSupport.getFirstChildElement(current);
        assert current != null;
        // body
        current = ElementSupport.getNextSiblingElement(current);
        assert current != null;
        // form
        current = ElementSupport.getFirstChildElement(current);
        assert current != null;
        current = ElementSupport.getNextSiblingElement(current);
        assert current != null;
        // div
        current = ElementSupport.getFirstChildElement(current);
        assert current != null;
        // list of form input fields
        List<Element> inputs =
                ElementSupport.getChildElementsByTagNameNS(current, null, "input");
        for (Element element : inputs) {
            String name = element.getAttributeNS(null, "name");
            String value = element.getAttributeNS(null, "value");
            // System.out.println("Processing input field: " + name);
            request.setParameter(name, value);
        }
    }

}