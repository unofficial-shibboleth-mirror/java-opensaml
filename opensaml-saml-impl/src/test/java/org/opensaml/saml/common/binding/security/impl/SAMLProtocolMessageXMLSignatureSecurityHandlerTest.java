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

package org.opensaml.saml.common.binding.security.impl;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.common.testing.SAMLTestSupport;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.impl.CollectionCredentialResolver;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.X509Support;
import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;


/**
 * Test SAML protocol message XML signature message handler.
 */
@SuppressWarnings({"null", "javadoc"})
public class SAMLProtocolMessageXMLSignatureSecurityHandlerTest extends XMLObjectBaseTestCase {
    
    private MessageContext messageContext;
    
    private SAMLProtocolMessageXMLSignatureSecurityHandler handler;
    
    
    private X509Certificate signingCert;
    private String signingCertBase64 = 
        "MIIDzjCCAragAwIBAgIBMTANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl" +
        "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDUyMTE4MjM0MFoX" +
        "DTE3MDUxODE4MjM0MFowMTESMBAGA1UEChMJSW50ZXJuZXQyMRswGQYDVQQDExJm" +
        "b29iYXIuZXhhbXBsZS5vcmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIB" +
        "AQDNWnkFmhy1vYa6gN/xBRKkZxFy3sUq2V0LsYb6Q3pe9Qlb6+BzaM5DrN8uIqqr" +
        "oBE3Wp0LtrgKuQTpDpNFBdS2p5afiUtOYLWBDtizTOzs3Z36MGMjIPUYQ4s03IP3" +
        "yPh2ud6EKpDPiYqzNbkRaiIwmYSit5r+RMYvd6fuKvTOn6h7PZI5AD7Rda7VWh5O" +
        "VSoZXlRx3qxFho+mZhW0q4fUfTi5lWwf4EhkfBlzgw/k5gf4cOi6rrGpRS1zxmbt" +
        "X1RAg+I20z6d04g0N2WsK5stszgYKoIROJCiXwjraa8/SoFcILolWQpttVHBIUYl" +
        "yDlm8mIFleZf4ReFpfm+nUYxAgMBAAGjgfQwgfEwCQYDVR0TBAIwADAsBglghkgB" +
        "hvhCAQ0EHxYdT3BlblNTTCBHZW5lcmF0ZWQgQ2VydGlmaWNhdGUwHQYDVR0OBBYE" +
        "FDgRgTkjaKoK6DoZfUZ4g9LDJUWuMFUGA1UdIwROMEyAFNXuZVPeUdqHrULqQW7y" +
        "r9buRpQLoTGkLzAtMRIwEAYDVQQKEwlJbnRlcm5ldDIxFzAVBgNVBAMTDmNhLmV4" +
        "YW1wbGUub3JnggEBMEAGA1UdEQQ5MDeCEmFzaW1vdi5leGFtcGxlLm9yZ4YbaHR0" +
        "cDovL2hlaW5sZWluLmV4YW1wbGUub3JnhwQKAQIDMA0GCSqGSIb3DQEBBQUAA4IB" +
        "AQBLiDMyQ60ldIytVO1GCpp1S1sKJyTF56GVxHh/82hiRFbyPu+2eSl7UcJfH4ZN" +
        "bAfHL1vDKTRJ9zoD8WRzpOCUtT0IPIA/Ex+8lFzZmujO10j3TMpp8Ii6+auYwi/T" +
        "osrfw1YCxF+GI5KO49CfDRr6yxUbMhbTN+ssK4UzFf36UbkeJ3EfDwB0WU70jnlk" +
        "yO8f97X6mLd5QvRcwlkDMftP4+MB+inTlxDZ/w8NLXQoDW6p/8r91bupXe0xwuyE" +
        "vow2xjxlzVcux2BZsUZYjBa07ZmNNBtF7WaQqH7l2OBCAdnBhvme5i/e0LK3Ivys" +
        "+hcVyvCXs5XtFTFWDAVYvzQ6";
    
    private X509Certificate otherCert1;
    private String otherCert1Base64 = 
        "MIIECTCCAvGgAwIBAgIBMzANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl" +
        "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDUyNTIwMTYxMVoX" +
        "DTE3MDUyMjIwMTYxMVowGjEYMBYGA1UEAxMPaWRwLmV4YW1wbGUub3JnMIIBtjCC" +
        "ASsGByqGSM44BAEwggEeAoGBAI+ktw7R9m7TxjaCrT2MHwWNQUAyXPrqbFCcu+DC" +
        "irr861U6R6W/GyqWdcy8/D1Hh/I1U94POQn5yfqVPpVH2ZRS4OMFndHWaoo9V5LJ" +
        "oXTXHiDYB3W4t9tn0fm7It0n7VoUI5C4y9LG32Hq+UIGF/ktNTmo//mEqLS6aJNd" +
        "bMFpAhUArmKGh0hcpmjukYArWcMRvipB4CMCgYBuCiCrUaHBRRtqrk0P/Luq0l2M" +
        "2718GwSGeLPZip06gACDG7IctMrgH1J+ZIjsx6vffi977wnMDiktqacmaobV+SCR" +
        "W9ijJRdkYpUHmlLvuJGnDPjkvewpbGWJsCabpWEvWdYw3ma8RuHOPj4Jkrdd4VcR" +
        "aFwox/fPJ7cG6kBydgOBhAACgYBxQIPv9DCsmiMHG1FAxSARX0GcRiELJPJ+MtaS" +
        "tdTrVobNa2jebwc3npLiTvUR4U/CDo1mSZb+Sp/wian8kNZHmGcR6KbtJs9UDsa3" +
        "V0pbbgpUar4HcxV+NQJBbhn9RGu85g3PDILUrINiUAf26mhPN5Y0paM+HbM68nUf" +
        "1OLv16OBsjCBrzAJBgNVHRMEAjAAMCwGCWCGSAGG+EIBDQQfFh1PcGVuU1NMIEdl" +
        "bmVyYXRlZCBDZXJ0aWZpY2F0ZTAdBgNVHQ4EFgQUIHFAEB/3jIIZzJEJ/qdsuI8v" +
        "N3kwVQYDVR0jBE4wTIAU1e5lU95R2oetQupBbvKv1u5GlAuhMaQvMC0xEjAQBgNV" +
        "BAoTCUludGVybmV0MjEXMBUGA1UEAxMOY2EuZXhhbXBsZS5vcmeCAQEwDQYJKoZI" +
        "hvcNAQEFBQADggEBAJt4Q34+pqjW5tHHhkdzTITSBjOOf8EvYMgxTMRzhagLSHTt" +
        "9RgO5i/G7ELvnwe1j6187m1XD9iEAWKeKbB//ljeOpgnwzkLR9Er5tr1RI3cbil0" +
        "AX+oX0c1jfRaQnR50Rfb5YoNX6G963iphlxp9C8VLB6eOk/S270XoWoQIkO1ioQ8" +
        "JY4HE6AyDsOpJaOmHpBaxjgsiko52ZWZeZyaCyL98BXwVxeml7pYnHlXWWidB0N/" +
        "Zy+LbvWg3urUkiDjMcB6nGImmEfDSxRdybitcMwbwL26z2WOpwL3llm3mcCydKXg" +
        "Xt8IQhfDhOZOHWckeD2tStnJRP/cqBgO62/qirw=";
    
    private CollectionCredentialResolver credResolver;
    private List<Credential> trustedCredentials;
    private BasicX509Credential signingX509Cred;
    
    private String issuer;
    
    private SignatureTrustEngine signatureTrustEngine;
    
    private SignatureValidationParameters sigValParams;
    
    @BeforeMethod
    protected void setUp() throws Exception {
        issuer = "SomeCoolIssuer";
        
        signingCert = X509Support.decodeCertificate(signingCertBase64);
        //signingPrivateKey = SecurityTestHelper.buildJavaRSAPrivateKey(signingPrivateKeyBase64);
        
        signingX509Cred = new BasicX509Credential(signingCert);
        signingX509Cred.setEntityId(issuer);
        
        otherCert1 = X509Support.decodeCertificate(otherCert1Base64);
        
        final BasicX509Credential otherCred1 = new BasicX509Credential(otherCert1);
        otherCred1.setEntityId("other-1");
        
        trustedCredentials = new ArrayList<>();
        trustedCredentials.add(otherCred1);
        
        credResolver = new CollectionCredentialResolver(trustedCredentials);
        
        //KeyInfoCredentialResolver kiResolver = new StaticKeyInfoCredentialResolver(new ArrayList<Credential>());
        //Testing with inline cert
        final KeyInfoCredentialResolver kiResolver = SAMLTestSupport.buildBasicInlineKeyInfoResolver();
        signatureTrustEngine = new ExplicitKeySignatureTrustEngine(credResolver, kiResolver);
        
        sigValParams = new SignatureValidationParameters();
        sigValParams.setSignatureTrustEngine(signatureTrustEngine);
        
        handler = new SAMLProtocolMessageXMLSignatureSecurityHandler();
        handler.initialize();
        
        messageContext = new MessageContext();
        messageContext.setMessage(buildInboundSAMLMessage());
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class).setEntityId(issuer);
        messageContext.ensureSubcontext(SAMLPeerEntityContext.class).setRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        messageContext.ensureSubcontext(SAMLProtocolContext.class).setProtocol(SAMLConstants.SAML20P_NS);
        messageContext.ensureSubcontext(SecurityParametersContext.class).setSignatureValidationParameters(sigValParams);
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
    public void testBlacklistedSigntureAlgorithm() throws MessageHandlerException {
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
     */
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testInvalidSignature() throws MessageHandlerException {
        trustedCredentials.add(signingX509Cred);
        
        AuthnRequest request  = 
            (AuthnRequest) unmarshallElement("/org/opensaml/saml/common/binding/security/Signed-AuthnRequest-InvalidSignature.xml"); 
        messageContext.setMessage(request);
        
        handler.invoke(messageContext);
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
        AuthnRequest request = 
            (AuthnRequest) unmarshallElement("/org/opensaml/saml/common/binding/security/Signed-AuthnRequest.xml");
        
        return request;
        
        /*
        AuthnRequest request = (AuthnRequest) buildXMLObject(AuthnRequest.DEFAULT_ELEMENT_NAME);
        request.setIssuer(buildIssuer());
        request.setID("abc123");
        request.setIssueInstant(new DateTime());
        
        Signature signature = (Signature) buildXMLObject(Signature.DEFAULT_ELEMENT_NAME);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        signature.setSigningCredential(signingX509Cred);
        
        X509KeyInfoGeneratorFactory kiFactory = new X509KeyInfoGeneratorFactory();
        kiFactory.setEmitEntityCertificate(true);
        KeyInfo keyInfo = null;
        try {
            keyInfo = kiFactory.newInstance().generate(signingX509Cred);
        } catch (SecurityException e1) {
            fail("Error generating KeyInfo from signing credential");
        }
        
        signature.setKeyInfo(keyInfo);
        
        request.setSignature(signature);
        
        try {
            SAMLConfigurationSupport.getMarshallerFactory().getMarshaller(request).marshall(request);
        } catch (MarshallingException e) {
            fail("Error marshalling message for signing");
        }
        
        Signer.signObject(signature);
        
        try {
            XMLHelper.writeNode(request.getDOM(), new FileWriter("signed-authn-request-test.xml"));
        } catch (IOException e) {
            fail("Error writing node to file: " + e);
        }
        
        return request;
        */
    }

}