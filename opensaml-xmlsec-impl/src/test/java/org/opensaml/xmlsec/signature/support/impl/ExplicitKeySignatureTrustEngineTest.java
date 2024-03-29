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

package org.opensaml.xmlsec.signature.support.impl;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.impl.CollectionCredentialResolver;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.X509Support;
import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.crypto.XMLSigningUtil;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.X509KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.mock.SignableSimpleXMLObject;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.SignableXMLObject;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.DocumentInternalIDContentReference;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureValidationParametersCriterion;
import org.opensaml.xmlsec.signature.support.Signer;
import org.opensaml.xmlsec.testing.XMLSecurityTestingSupport;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.resolver.CriteriaSet;

/**
 * Test explicit key signature trust engine.
 */
@SuppressWarnings({"javadoc", "null"})
public class ExplicitKeySignatureTrustEngineTest extends XMLObjectBaseTestCase {
    
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
    
    private PrivateKey signingPrivateKey;
    private String signingPrivateKeyBase64 = 
        "MIIEogIBAAKCAQEAzVp5BZoctb2GuoDf8QUSpGcRct7FKtldC7GG+kN6XvUJW+vg" +
        "c2jOQ6zfLiKqq6ARN1qdC7a4CrkE6Q6TRQXUtqeWn4lLTmC1gQ7Ys0zs7N2d+jBj" +
        "IyD1GEOLNNyD98j4drnehCqQz4mKszW5EWoiMJmEorea/kTGL3en7ir0zp+oez2S" +
        "OQA+0XWu1VoeTlUqGV5Ucd6sRYaPpmYVtKuH1H04uZVsH+BIZHwZc4MP5OYH+HDo" +
        "uq6xqUUtc8Zm7V9UQIPiNtM+ndOINDdlrCubLbM4GCqCETiQol8I62mvP0qBXCC6" +
        "JVkKbbVRwSFGJcg5ZvJiBZXmX+EXhaX5vp1GMQIDAQABAoIBAC1P4lZvHBiqGll6" +
        "6G8pXGS0bXA4Ya9DyTk0UgFU9GKRlSAYWy18Gc9rDNAETD6Uklfxgae9CL0s+D1o" +
        "vuxDDh3DuwO26sv/oO06Vmyx87GMcThshuOQeSSCeuwOIHyDdvfTqZrmPY/d3KIQ" +
        "n6aNEcBBj7fL5cJncIe20nJGPkB9KuTAaGVnaKoOesxgWBr7SvjGq/SB7bRE1B3c" +
        "QxwUDWHkF0LljSIkXaV9ehKJcgBY2fV0rc8pI53WsUXEXk5HoqYZnQ5QjAZ4Hf2s" +
        "bRKevq+D2ENK+OuKNuCAS/oJbGSdS7q0/6jgHZ6cUGXi1r2qEEG7PIorCoSMkWQS" +
        "M1wMX0ECgYEA9c6/s9lKDrjzyjO9rlxzufGVRDjffiUZ1o8F3RD3JltdPLVcd429" +
        "CvGSNV730Yr/wSyRAum4vkGnmOR9tuQdi3PJHt3xGRsymTT5ym/5fnC4SvXVSR6v" +
        "LFPUY80yj+D6/0lwIaGE7x4JOclMXnHjqcpRl14onOjY844WORhxgjkCgYEA1d5N" +
        "Tqp938UbZYKX4Q9UvLf/pVR9xOFOCYnMywAFk0WnkUBPHmPoJuFgeNGeQ7gCmHi7" +
        "JFzwBjkj6DcGMdbXKWiUij1BoRxf9Mof+fZBWVSKw+/yVLbJkyK951+nywyiq3HC" +
        "NBti1eK/h/hXQd8t+dCBmDGj1ba1C2/3JZqLg7kCgYArxD1D85uJFYtq5F2Qryt3" +
        "3zj5pbq9hjOcjWi43O10qe3nAk/NhbI0QaEL2bX8XGh/Z8UGJMFdNul1grGTn/hW" +
        "vS4BTflAxCP1PYaAcgGVbtKRnkX0t/7uwJpfjsjC74chb10Ez/KQdOOlo17yrgqg" +
        "T8LJVd2bWqZOb20ri1uimQKBgFfJYSg6OWLh0IYRXfBmz5yLVmdx0BJBfTvTEXn+" +
        "L0utWsP3hsJttfxHpMbTHEilvoMBg6fAclHLoJ6P/33ztuvrXpWD4W2VbRnY4dlD" +
        "qL1XQ4J7+pelVAaOSy8vB3wEWr1O+61R1HcBFSdl28NRLdkOKjPjpGF0Fsp0Ehmg" +
        "X0YZAoGAXrM4+BUvcx2PLaeneTJoRdOi3GQbdAte03maDU6C474IdgR8IUygfspv" +
        "3fiGue9Wmk5ybUBlv/D6sIWVhnnedWsg2zAgZPfZ78HLLNhWeEx33wPFiK0wV5MJ" +
        "XQ224gQ5t9D3WXdZtmAxXIFoopj4zToCMBjXyep0u7zl3s7s00U=";
    
        
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
    
    private ExplicitKeySignatureTrustEngine engine;
    private CriteriaSet criteriaSet;
    private String signingEntityID;
    
    private String rawAlgorithmURI;
    private String rawData;
    private byte[] rawControlSignature;
    
    
    /** Constructor. */
    public ExplicitKeySignatureTrustEngineTest() {
        
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        signingEntityID = "signing-entity-ID";
        signingCert = X509Support.decodeCertificate(signingCertBase64);
        signingPrivateKey = KeySupport.buildJavaRSAPrivateKey(signingPrivateKeyBase64);
        
        signingX509Cred = new BasicX509Credential(signingCert, signingPrivateKey);
        signingX509Cred.setEntityId(signingEntityID);
        
        otherCert1 = X509Support.decodeCertificate(otherCert1Base64);
        
        BasicX509Credential otherCred1 = new BasicX509Credential(otherCert1);
        otherCred1.setEntityId("other-1");
        
        trustedCredentials = new ArrayList<>();
        trustedCredentials.add(otherCred1);
        
        credResolver = new CollectionCredentialResolver(trustedCredentials);
        
        //KeyInfoCredentialResolver kiResolver = new StaticKeyInfoCredentialResolver(new ArrayList<Credential>());
        //Testing with inline cert
        KeyInfoCredentialResolver kiResolver = XMLSecurityTestingSupport.buildBasicInlineKeyInfoResolver();
        engine = new ExplicitKeySignatureTrustEngine(credResolver, kiResolver);
        
        criteriaSet = new CriteriaSet();
        criteriaSet.add( new EntityIdCriterion(signingEntityID) );
        
        rawData = "Hello, here is some secret data that is to be signed";
        rawAlgorithmURI = SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1;
        rawControlSignature = XMLSigningUtil.signWithURI(signingX509Cred, rawAlgorithmURI, rawData.getBytes());
    }
    
    /**
     * Test valid signature.
     * 
     * @throws SecurityException ...
     */
    @Test
    public void testSuccess() throws SecurityException {
        trustedCredentials.add(signingX509Cred);
        
        final SignableXMLObject signableXO = getValidSignedObject();
        final Signature signature = signableXO.getSignature();
        assert signature != null;
        Assert.assertTrue(engine.validate(signature, criteriaSet), "Signature was valid and signing cred was trusted");
    }
    
    /**
     * Test valid signature, untrusted signing credential.
     * 
     * @throws SecurityException ...
     */
    @Test
    public void testUntrustedCredential() throws SecurityException {
        final SignableXMLObject signableXO = getValidSignedObject();
        final Signature signature = signableXO.getSignature();
        assert signature != null;
        Assert.assertFalse(engine.validate(signature, criteriaSet), "Signature was valid, but signing cred was untrusted");
    }
    
    /**
     * Test invalid signature, trusted signing credential.
     * 
     * @throws SecurityException ...
     */
    @Test
    public void testInvalidSignature() throws SecurityException {
        trustedCredentials.add(signingX509Cred);
        
        final SignableXMLObject signableXO = getInvalidSignedObject();
        final Signature signature = signableXO.getSignature();
        assert signature != null;
        Assert.assertFalse(engine.validate(signature, criteriaSet), "Signature was invalid due to document modification");
        
    }
    
    /**
     * Test whitelisted signature and digest method algorithm URIs.
     * 
     * @throws SecurityException ...
     */
    @Test
    public void testWhitelistedAlgorithms() throws SecurityException {
        trustedCredentials.add(signingX509Cred);
        
        final HashSet<String> algos = new HashSet<>();
        algos.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        algos.add(SignatureConstants.ALGO_ID_DIGEST_SHA1);
        final SignatureValidationParameters validationParams = new SignatureValidationParameters();
        validationParams.setIncludedAlgorithms(algos);
        criteriaSet.add(new SignatureValidationParametersCriterion(validationParams));
        
        final SignableXMLObject signableXO = getValidSignedObject();
        final Signature signature = signableXO.getSignature();
        assert signature != null;
        Assert.assertTrue(engine.validate(signature, criteriaSet), "Signature was valid with whitelisted algorithms");
    }
    
    /**
     * Test blacklisted signature method algorithm URI.
     * 
     * @throws SecurityException ...
     */
    public void testBlacklistedSignatureAlgorithm() throws SecurityException {
        trustedCredentials.add(signingX509Cred);
        
        final HashSet<String> algos = new HashSet<>();
        algos.add(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        final SignatureValidationParameters validationParams = new SignatureValidationParameters();
        validationParams.setExcludedAlgorithms(algos);
        criteriaSet.add(new SignatureValidationParametersCriterion(validationParams));
        
        final SignableXMLObject signableXO = getValidSignedObject();
        final Signature signature = signableXO.getSignature();
        assert signature != null;
        Assert.assertFalse(engine.validate(signature, criteriaSet), "Signature algorithm was blacklisted");
    }
    
    /**
     * Test blacklisted digest method algorithm URI.
     * 
     * @throws SecurityException ...
     */
    public void testBlacklistedDigestAlgorithm() throws SecurityException {
        trustedCredentials.add(signingX509Cred);
        
        final HashSet<String> algos = new HashSet<>();
        algos.add(SignatureConstants.ALGO_ID_DIGEST_SHA1);
        final SignatureValidationParameters validationParams = new SignatureValidationParameters();
        validationParams.setExcludedAlgorithms(algos);
        criteriaSet.add(new SignatureValidationParametersCriterion(validationParams));
        
        final SignableXMLObject signableXO = getValidSignedObject();
        final Signature signature = signableXO.getSignature();
        assert signature != null;
        Assert.assertFalse(engine.validate(signature, criteriaSet), "Digest algorithm was blacklisted");
    } 
    
    /**
     * Test valid raw signature, trusted signing credential.
     * 
     * @throws SecurityException ...
     */
    @Test
    public void testRawSuccess() throws SecurityException {
        trustedCredentials.add(signingX509Cred);
        
        Assert.assertTrue(engine.validate(rawControlSignature, rawData.getBytes(), rawAlgorithmURI, 
                criteriaSet, signingX509Cred), 
                "Raw Signature was valid and supplied candidate signing cred was trusted");
        
        Assert.assertTrue(engine.validate(rawControlSignature, rawData.getBytes(), rawAlgorithmURI, 
                criteriaSet, null), 
                "Raw Signature was valid and non-supplied candidate signing cred was in trusted set");
    }
    
    /**
     * Test valid raw signature, untrusted signing credential.
     * 
     * @throws SecurityException ...
     */
    @Test
    public void testRawUntrustedCredential() throws SecurityException {
        
        Assert.assertFalse(engine.validate(rawControlSignature, rawData.getBytes(), rawAlgorithmURI, 
                criteriaSet, signingX509Cred), 
                "Raw Signature was valid, but supplied candidate signing cred was untrusted");
        
        Assert.assertFalse(engine.validate(rawControlSignature, rawData.getBytes(), rawAlgorithmURI, 
                criteriaSet, null), 
                "Raw Signature was valid and the signing cred was not present in trusted set");
    }
    
    /**
     * Test invalid raw signature, trusted signing credential.
     * 
     * @throws SecurityException ...
     */
    @Test
    public void testRawInvalidSignature() throws SecurityException {
        trustedCredentials.add(signingX509Cred);
        
        String tamperedData = rawData + "HAHA All your base are belong to us";
        
        Assert.assertFalse(engine.validate(rawControlSignature, tamperedData.getBytes(), rawAlgorithmURI, 
                criteriaSet, signingX509Cred), 
                "Raw Signature was invalid due to data tampering, supplied candidate signing cred was trusted");
    }
    
    /**
     * Test valid raw signature with whitelisted signature algorithm.
     * 
     * @throws SecurityException ...
     */
    @Test
    public void testRawWhitelistedAlgorithm() throws SecurityException {
        trustedCredentials.add(signingX509Cred);
        
        HashSet<String> algos = new HashSet<>();
        algos.add(rawAlgorithmURI);
        SignatureValidationParameters validationParams = new SignatureValidationParameters();
        validationParams.setIncludedAlgorithms(algos);
        criteriaSet.add(new SignatureValidationParametersCriterion(validationParams));
        
        Assert.assertTrue(engine.validate(rawControlSignature, rawData.getBytes(), rawAlgorithmURI, 
                criteriaSet, signingX509Cred), 
                "Raw Signature was valid with whitelisted algorithms");
    }
    
    /**
     * Test valid raw signature with whitelisted signature algorithm.
     * 
     * @throws SecurityException ...
     */
    @Test
    public void testRawBlacklistedAlgorithm() throws SecurityException {
        trustedCredentials.add(signingX509Cred);
        
        HashSet<String> algos = new HashSet<>();
        algos.add(rawAlgorithmURI);
        SignatureValidationParameters validationParams = new SignatureValidationParameters();
        validationParams.setExcludedAlgorithms(algos);
        criteriaSet.add(new SignatureValidationParametersCriterion(validationParams));
        
        Assert.assertFalse(engine.validate(rawControlSignature, rawData.getBytes(), rawAlgorithmURI, 
                criteriaSet, signingX509Cred), 
                "Raw Signature was invalid with blacklisted algorithms");
    }
    
    /**
     * Get a signed object containing the signature to be validated.
     * 
     * @return a signed object
     */
    protected SignableXMLObject getValidSignedObject() {
        //return buildSignedObject();
        return (SignableSimpleXMLObject) unmarshallElement("/org/opensaml/xmlsec/signature/support/Signed-SimpleObject.xml");
    }

    /**
     * Get a signed object containing the signature to be validated.  Signature should be invalid 
     * when valid (i.e. signed content modified, etc ).
     * 
     * @return a signed object, with invalid signature
     */
    protected SignableXMLObject getInvalidSignedObject() {
        //return buildSignedObject();
        return (SignableSimpleXMLObject) unmarshallElement("/org/opensaml/xmlsec/signature/support/Signed-SimpleObject-InvalidSignature.xml");
    }
    
    /**
     * Build a signed object.
     * 
     * @return a signed object
     * 
     * @throws SignatureException ...
     */
    protected SignableXMLObject buildSignedObject() throws SignatureException {
        SignableSimpleXMLObject sxo = (SignableSimpleXMLObject) buildXMLObject(SignableSimpleXMLObject.ELEMENT_NAME);
        sxo.setId("abc123");
        
        SignableSimpleXMLObject child = (SignableSimpleXMLObject) buildXMLObject(SignableSimpleXMLObject.ELEMENT_NAME);
        child.setValue("SomeSimpleValueAsTextContent");
        sxo.getSimpleXMLObjects().add(child);
        
        Signature signature = (Signature) buildXMLObject(Signature.DEFAULT_ELEMENT_NAME);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
        signature.setSigningCredential(signingX509Cred);
        final String id = sxo.getId();
        assert id != null;
        DocumentInternalIDContentReference idContentRef = new DocumentInternalIDContentReference(id);
        idContentRef.setDigestAlgorithm(SignatureConstants.ALGO_ID_DIGEST_SHA1);
        idContentRef.getTransforms().add(SignatureConstants.TRANSFORM_ENVELOPED_SIGNATURE);
        idContentRef.getTransforms().add(SignatureConstants.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
        signature.getContentReferences().add(idContentRef);
        
        X509KeyInfoGeneratorFactory kiFactory = new X509KeyInfoGeneratorFactory();
        kiFactory.setEmitEntityCertificate(true);
        KeyInfo keyInfo = null;
        try {
            keyInfo = kiFactory.newInstance().generate(signingX509Cred);
        } catch (SecurityException e) {
            Assert.fail("Error generating KeyInfo from signing credential: " + e);
        }
        
        signature.setKeyInfo(keyInfo);
        
        sxo.setSignature(signature);
        
        try {
            XMLObjectProviderRegistrySupport.getMarshallerFactory().ensureMarshaller(sxo).marshall(sxo);
        } catch (MarshallingException e) {
            Assert.fail("Error marshalling object for signing: " + e);
        }
        
        Signer.signObject(signature);
        
        /*
        try {
            XMLHelper.writeNode(sxo.getDOM(), new FileWriter("signed-simple-object-test4.xml"));
        } catch (IOException e) {
            fail("Error writing node to file: " + e);
        }
        */
        
        return sxo;
    }
    
}
