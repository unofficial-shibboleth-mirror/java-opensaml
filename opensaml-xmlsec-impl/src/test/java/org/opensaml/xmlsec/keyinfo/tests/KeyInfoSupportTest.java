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

package org.opensaml.xmlsec.keyinfo.tests;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import java.math.BigInteger;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.List;

import javax.crypto.interfaces.DHPublicKey;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.security.SecurityException;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.security.crypto.dh.DHSupport;
import org.opensaml.security.crypto.ec.ECSupport;
import org.opensaml.security.crypto.ec.EnhancedECParameterSpec;
import org.opensaml.security.x509.X509Support;
import org.opensaml.xmlsec.encryption.DHKeyValue;
import org.opensaml.xmlsec.encryption.Generator;
import org.opensaml.xmlsec.encryption.Public;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.signature.DEREncodedKeyValue;
import org.opensaml.xmlsec.signature.DSAKeyValue;
import org.opensaml.xmlsec.signature.ECKeyValue;
import org.opensaml.xmlsec.signature.Exponent;
import org.opensaml.xmlsec.signature.G;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyValue;
import org.opensaml.xmlsec.signature.Modulus;
import org.opensaml.xmlsec.signature.NamedCurve;
import org.opensaml.xmlsec.signature.P;
import org.opensaml.xmlsec.signature.Q;
import org.opensaml.xmlsec.signature.RSAKeyValue;
import org.opensaml.xmlsec.signature.X509CRL;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.X509Digest;
import org.opensaml.xmlsec.signature.X509IssuerName;
import org.opensaml.xmlsec.signature.X509IssuerSerial;
import org.opensaml.xmlsec.signature.X509SKI;
import org.opensaml.xmlsec.signature.X509SerialNumber;
import org.opensaml.xmlsec.signature.X509SubjectName;
import org.opensaml.xmlsec.signature.Y;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

import com.google.common.base.Strings;

import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.DecodingException;
import net.shibboleth.shared.codec.EncodingException;
import net.shibboleth.shared.logic.Constraint;

/**
 * Test to exercise the KeyInfoSupport methods to convert between XMLObject's contained within KeyInfo and Java security
 * native types.
 */
@SuppressWarnings({"javadoc", "null"})
public class KeyInfoSupportTest extends XMLObjectBaseTestCase {

    /** Cert which contains no X.509 v3 extensions. */
    private final String certNoExtensions = "MIIBwjCCASugAwIBAgIJAMrW6QSeKNBJMA0GCSqGSIb3DQEBBAUAMCMxITAfBgNV"
            + "BAMTGG5vZXh0ZW5zaW9ucy5leGFtcGxlLm9yZzAeFw0wNzA1MTkxNzU2NTVaFw0w"
            + "NzA2MTgxNzU2NTVaMCMxITAfBgNVBAMTGG5vZXh0ZW5zaW9ucy5leGFtcGxlLm9y"
            + "ZzCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAw8xxu6TLqEdmnyXVZjiUoRHN"
            + "6yHyobZaRK+tBEmWkD4nTlOVmTKWBCO/F4OnugaJbSTH+7Jk37l8/XYYBSIkW0+L"
            + "2BglzQ2JCux/uoRu146QDIk9f5PIFs+Fxy7VRVUUZiOsonB/PNVqA7OVbPxzr1SK"
            + "PSE0s9CHaDjCaEs2BnMCAwEAATANBgkqhkiG9w0BAQQFAAOBgQAuI/l80wb8K6RT"
            + "1EKrAcfr9JAlJR4jmVnCK7j3Ulx++U98ze2G6/cluLxrbnqwXmxJNC3nt6xkQVJU"
            + "X1UFg+zkmRrst2Nv8TTrR7S30az068BHfrZLRSUConG9jXXj+hJq+w/ojmrq8Mzv" + "JSczkA2BvsEUBARYo53na7RMgk+xWg==";

    /*
     * These test examples are from the NIST PKI path processing test suite:
     * http://csrc.nist.gov/pki/testing/x509paths.html Data file: http://csrc.nist.gov/pki/testing/PKITS_data.zip
     */

    /* certs/BasicSelfIssuedNewKeyCACert.crt */
    /** Test cert subject DN 1. */
    private final String cert1SubjectDN = "CN=Basic Self-Issued New Key CA,O=Test Certificates,C=US";

    /**
     * Test cert 1 SKI value. Base64 encoded version of cert's plain (non-DER encoded) subject key identifier, which is:
     * AF:B9:F9:1D:C2:45:18:CC:B8:21:E2:A7:47:BC:49:BD:19:B5:78:28
     */
    private final String cert1SKIPlainBase64 = "r7n5HcJFGMy4IeKnR7xJvRm1eCg=";
    
    /** Test cert 1 SHA-1 digest. */
    private final String cert1DigestBase64 = "EmkP8ttMw28A/JoA3KcO11eez7Q=";

    /** Test cert 1. */
    private final String cert1 = "MIICgjCCAeugAwIBAgIBEzANBgkqhkiG9w0BAQUFADBAMQswCQYDVQQGEwJVUzEa"
            + "MBgGA1UEChMRVGVzdCBDZXJ0aWZpY2F0ZXMxFTATBgNVBAMTDFRydXN0IEFuY2hv"
            + "cjAeFw0wMTA0MTkxNDU3MjBaFw0xMTA0MTkxNDU3MjBaMFAxCzAJBgNVBAYTAlVT"
            + "MRowGAYDVQQKExFUZXN0IENlcnRpZmljYXRlczElMCMGA1UEAxMcQmFzaWMgU2Vs"
            + "Zi1Jc3N1ZWQgTmV3IEtleSBDQTCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEA"
            + "tCkygqcMEOy3i8p6ZV3685us1lOugSU4pUMRJNRH/lV2ykesk+JRcQy1s7WS12j9"
            + "GCnSJ919/TgeKLmV3ps1fC1B8HziC0mzBAr+7f5LkJqSf0kS0kfpyLOoO8VSJCip"
            + "/8uENkSkpvX+Lak96OKzhtyvi4KpUdQKfwpg6xUqakECAwEAAaN8MHowHwYDVR0j"
            + "BBgwFoAU+2zULYGeyid6ng2wPOqavIf/SeowHQYDVR0OBBYEFK+5+R3CRRjMuCHi"
            + "p0e8Sb0ZtXgoMA4GA1UdDwEB/wQEAwIBBjAXBgNVHSAEEDAOMAwGCmCGSAFlAwIB"
            + "MAEwDwYDVR0TAQH/BAUwAwEB/zANBgkqhkiG9w0BAQUFAAOBgQCuRBfDy2gSPp2k"
            + "ZR7OAvt+xDx4toJ9ImImUvJ94AOLd6Uxsi2dvQT5HLrIBrTYsSfQj1pA50XY2F7k"
            + "3eM/+JhYCcyZD9XtAslpOkjwACPJnODFAY8PWC00CcOxGb6q+S/VkrCwvlBeMjev" + "IH4bHvAymWsZndBZhcG8gBmDrZMwhQ==";

    /* certs/GoodCACert.crt */
    /** Test cert subject DN 2. */
    private final String cert2SubjectDN = "CN=Good CA,O=Test Certificates,C=US";

    /** Test cert 2. */
    private final String cert2 = "MIICbTCCAdagAwIBAgIBAjANBgkqhkiG9w0BAQUFADBAMQswCQYDVQQGEwJVUzEa"
            + "MBgGA1UEChMRVGVzdCBDZXJ0aWZpY2F0ZXMxFTATBgNVBAMTDFRydXN0IEFuY2hv"
            + "cjAeFw0wMTA0MTkxNDU3MjBaFw0xMTA0MTkxNDU3MjBaMDsxCzAJBgNVBAYTAlVT"
            + "MRowGAYDVQQKExFUZXN0IENlcnRpZmljYXRlczEQMA4GA1UEAxMHR29vZCBDQTCB"
            + "nzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEArsI1lQuXKwOxSkOVRaPwlhMQtgp0"
            + "p7HT4rKLGqojfY0twvMDc4rC9uj97wlh98kkraMx3r0wlllYSQ+Cp9mCCNu/C/Y2"
            + "IbZCyG+io4A3Um3q/QGvbHlclmrJb0j0MQi3o88GhE8Q6Vy6SGwFXGpKDJMpLSFp"
            + "Pxz8lh7M6J56Ex8CAwEAAaN8MHowHwYDVR0jBBgwFoAU+2zULYGeyid6ng2wPOqa"
            + "vIf/SeowHQYDVR0OBBYEFLcupoLLwsi8qHsnRNc1M9+aFZTHMA4GA1UdDwEB/wQE"
            + "AwIBBjAXBgNVHSAEEDAOMAwGCmCGSAFlAwIBMAEwDwYDVR0TAQH/BAUwAwEB/zAN"
            + "BgkqhkiG9w0BAQUFAAOBgQCOls9+0kEUS71w+KoQhfkVLdAKANXUmGCVZHL1zsya"
            + "cPP/Q8IsCNvwjefZpgc0cuhtnHt2uDd0/zYLRmgcvJwfx5vwOfmDN13mMB8Za+cg"
            + "3sZ/NI8MqQseKvS3fWqXaK6FJoKLzxId0iUGntbF4c5+rPFArzqM6IE7f9cMD5Fq" + "rA==";

    /* crls/BasicSelfIssuedCRLSigningKeyCACRL.crl */
    /** Test cert issuer DN 1. */
    private final String crl1IssuerDN = "CN=Basic Self-Issued CRL Signing Key CA,O=Test Certificates,C=US";

    /** Test CRL 1. */
    private final String crl1 = "MIIBdTCB3wIBATANBgkqhkiG9w0BAQUFADBYMQswCQYDVQQGEwJVUzEaMBgGA1UE"
            + "ChMRVGVzdCBDZXJ0aWZpY2F0ZXMxLTArBgNVBAMTJEJhc2ljIFNlbGYtSXNzdWVk"
            + "IENSTCBTaWduaW5nIEtleSBDQRcNMDEwNDE5MTQ1NzIwWhcNMTEwNDE5MTQ1NzIw"
            + "WjAiMCACAQMXDTAxMDQxOTE0NTcyMFowDDAKBgNVHRUEAwoBAaAvMC0wHwYDVR0j"
            + "BBgwFoAUD3LKM0OpxBFRq2PaRIcPYaT0vkcwCgYDVR0UBAMCAQEwDQYJKoZIhvcN"
            + "AQEFBQADgYEAXM2Poz2eZPdkc5wsOeLn1w64HD6bHRTcmMKOWh/lRzH9fqfVn1Ix"
            + "yBD30KKEP3fH8bp+JGKtBa4ce//w4s5V9SfTzCR/yB2muM5CBeEG7B+HTNVpjXhZ"
            + "0jOUHDsnaIA9bz2mx58rOZ/Xw4Prd73Mf5azrSRomdEavwUcjD4qAvg=";

    /* These are just randomly generated RSA and DSA public keys using OpenSSL. */

    /** Test RSA key 1. */
    private final String rsaPubKey1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw/WnsbA8frhQ+8EoPgMr"
            + "QjpINjt20U/MvsvmoAgQnAgEF4OYt9Vj9/2YvMO4NvX1fNDFzoYRyOMrypF7skAP"
            + "cITUhdcPSEpI4nsf5yFZLziK/tQ26RsccE7WhpGB8eHu9tfseelgyioorvmt+JCo"
            + "P15c5rYUuIfVC+eEsYolw344q6N61OACHETuySL0a1+GFu3WoISXte1pQIst7HKv"
            + "BbHH41HEWAxT6e0hlD5PyKL4lBJadGHXg8Zz4r2jV2n6+Ox7raEWmtVCGFxsAoCR"
            + "alu6nvs2++5Nnb4C1SE640esfYhfeMd5JYfsTNMaQ8sZLpsWdglAGpa/Q87K19LI" + "wwIDAQAB";

    /** Test DSA key 1. */
    private final String dsaPubKey1 = "MIIDOjCCAi0GByqGSM44BAEwggIgAoIBAQCWV7IK073aK2C3yggy69qXkxCw30j5"
            + "Ig0s1/GHgq5jEZf8FTGVpehX5qaYlRC3TBMSN4WAgkG+nFnsjHb6kIYkayV8ZVvI"
            + "IgEBCeaZg016f90G+Rre5C38G3OwsODKjPsVZCV5YQ9rm6lWMOfMRSUzJuFA0fdx"
            + "RLssAfKLI5JmzupliO2iH5FU3+dQr0UvcPwPjjRDA9JIi3ShKdmq9f/SzRM9AJPs"
            + "sjc0v4lRVMKWkTHLjbRH2XiOxsok/oL7NVTJ9hvd3xqi1/O3MM2pNhYaQoA0kLqq"
            + "sr006dNftgo8n/zrBFMC6iP7tmxhuRxgXXkNo5xiQCvAX7HsGno4y9ilAhUAjKlv"
            + "CQhbGeQo3fWbwVJMdokSK5ECggEAfERqa+S8UwjuvNxGlisuBGzR7IqqHSQ0cjFI"
            + "BD61CkYh0k0Y9am6ZL2jiAkRICdkW6f9lmGy0HidCwC56WeAYpLyfJslBAjC4r0t"
            + "6U8a822fECVcbsPNLDULoQG0KjVRtYfFH5GedNQ8LRkG8b+XIe4G74+vXOatVu8L"
            + "9QXQKYx9diOAHx8ghpt1pC0UAqPzAgVGNWIPQ+VO7WEYOYuVw+/uFoHiaU1OZOTF"
            + "C4VXk2+33AasT4i6It7DIESp+ye9lPnNU6nLEBNSnXdnBgaH27m8QnFRTfrjimiG"
            + "BwBTQvbjequRvM5dExfUqyCd2BUOK1lbaQmjZnCMH6k3ZFiAYgOCAQUAAoIBAGnD"
            + "wMuRoRGJHUhjjeePKwP9BgCc7dtKlB7QMnIHGPv03hdVPo9ezaQ5mFxdzQdXoLR2"
            + "BFucDtSj1je3e5L9KEnHZ5fHnislBnzSvYR5V8LwTa5mbNS4VHkAv8Eh3WG9tp1S"
            + "/f9ymefKHB7ISlskT7kODCIbr5HHU/n1zXtMRjoslY1A+nFlWiAaIvjnj/C8x0BW"
            + "BkhuSKX/2PbljnmIdGV7mJK9/XUHnyKgZBxXEul2mlvGkrgUvyv+qYsCFsKSSrkB"
            + "1Mj2Ql5xmTMaePMEmvOr6fDAP0OH8cvADEZjx0s/5vvoBFPGGmPrHJluEVS0Fu8I" + "9sROg9YjyuhRV0b8xHo=";
    
    /** Test DH key 1. */
    private final String dhPubKey1 = "MIIBJDCBmQYJKoZIhvcNAQMBMIGLAoGBAP//////////yQ/aoiFowjTExmKLgNwc0SkCTgiKZ8x0"
            + "Agu+pjsTmyJRSgh5jjQE3e+VGbPNOkMbMCsKbfJfFDdP4TVtbVHCReSFtXZiXn7G9ExC6aY37WsL"
            + "/1y29Aa37e44a/taiZ+lrp8kEXxLH+ZJKGZR7OZTgf//////////AgECAgICAAOBhQACgYEAwICZ"
            + "ws/L/QcxdYfg9AU/a0y3jEkgn6FaD0eaUTiWcXjpqEeVjPgqEeGnhffxI7z0B5n/ZSNB8bLVjrKe"
            + "srlS9Opop6HBKW9yuC9bMisN69n0eZn1SJoM3CpX5eBuVx3pOca2vf4T3J1naVpgvDTyhaaZ4rqH"
            + "3WC34FMOvm3rJio=";
    
    /** Test EC key with named curve variant 1, curve: secp256r1, OID: 1.2.840.10045.3.1.7 */
    private final String ecPubKey_NamedCurve1 = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEBM0jGYrvVMpbVTT728+RfDLL0tPg"
            + "swfUSUXfrXKwAGOmrSbF1KHsErZdXhnEC1VSmm9kTd8VzIi4OihEVMoU+w==";
    
    /** OID of the curve for ecPubKey_NamedCurve1, curve: secp256r1. */
    private final String ecPubKey_NamedCurve1_OID = "1.2.840.10045.3.1.7";
    
    /** Test EC key with explicit params variant 1, curve: secp256r1, OID: 1.2.840.10045.3.1.7 */
    private final String ecPubKey_ExplicitParams1 = "MIIBSzCCAQMGByqGSM49AgEwgfcCAQEwLAYHKoZIzj0BAQIhAP////8AAAABAAAA\n"
            + "AAAAAAAAAAAA////////////////MFsEIP////8AAAABAAAAAAAAAAAAAAAA////"
            + "///////////8BCBaxjXYqjqT57PrvVV2mIa8ZR0GsMxTsPY7zjw+J9JgSwMVAMSd"
            + "NgiG5wSTamZ44ROdJreBn36QBEEEaxfR8uEsQkf4vOblY6RA8ncDfYEt6zOg9KE5"
            + "RdiYwpZP40Li/hp/m47n60p8D54WK84zV2sxXs7LtkBoN79R9QIhAP////8AAAAA"
            + "//////////+85vqtpxeehPO5ysL8YyVRAgEBA0IABBRJ1RlY9GqHxNRRPDh+rciw"
            + "7HI/QgGWVf32j91hIwbQ8yNx0Hveirx0B5YGhF0cXrihKH0wC0zcYhtXUKHL7uQ=";

    /** OID of the curve for ecPubKey_ExplicitParams1, curve: secp256r1. */
    private final String ecPubKey_ExplicitParams1_OID = "1.2.840.10045.3.1.7";
    
    private X509Certificate xmlCert1, xmlCert2;

    private X509CRL xmlCRL1;

    private X509Data xmlX509Data;

    private KeyInfo keyInfo;

    private KeyValue keyValue;

    private DSAKeyValue xmlDSAKeyValue1, xmlDSAKeyValue1NoParams;

    private DHKeyValue xmlDHKeyValue1;

    private RSAKeyValue xmlRSAKeyValue1;

    private ECKeyValue xmlECKeyValue_NamedCurve1, xmlECKeyValue_ExplicitParams1;
    
    private int numExpectedCerts;

    private int numExpectedCRLs;

    private java.security.cert.X509Certificate javaCert1;

    private java.security.cert.X509CRL javaCRL1;

    private RSAPublicKey javaRSAPubKey1;

    private DSAPublicKey javaDSAPubKey1;
    
    private DHPublicKey javaDHPubKey1;
    
    private ECPublicKey javaECPubKey_NamedCurve1, javaECPubKey_ExplicitParams1;

    private DSAParams javaDSAParams1;

    /**
     * Constructor.
     * 
     */
    public KeyInfoSupportTest() {
        super();
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        xmlCert1 = (X509Certificate) buildXMLObject(X509Certificate.DEFAULT_ELEMENT_NAME);
        xmlCert1.setValue(cert1);

        xmlCert2 = (X509Certificate) buildXMLObject(X509Certificate.DEFAULT_ELEMENT_NAME);
        xmlCert2.setValue(cert2);

        xmlCRL1 = (X509CRL) buildXMLObject(X509CRL.DEFAULT_ELEMENT_NAME);
        xmlCRL1.setValue(crl1);

        xmlX509Data = (X509Data) buildXMLObject(X509Data.DEFAULT_ELEMENT_NAME);
        xmlX509Data.getX509Certificates().add(xmlCert1);
        xmlX509Data.getX509Certificates().add(xmlCert2);
        xmlX509Data.getX509CRLs().add(xmlCRL1);

        keyValue = (KeyValue) buildXMLObject(KeyValue.DEFAULT_ELEMENT_NAME);

        keyInfo = (KeyInfo) buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        keyInfo.getX509Datas().add(xmlX509Data);

        numExpectedCerts = 2;
        numExpectedCRLs = 1;

        javaCert1 = X509Support.decodeCertificate(cert1);
        X509Support.decodeCertificate(cert2);
        javaCRL1 = X509Support.decodeCRL(crl1);

        javaDSAPubKey1 = KeySupport.buildJavaDSAPublicKey(dsaPubKey1);
        javaDHPubKey1 = KeySupport.buildJavaDHPublicKey(dhPubKey1);
        javaRSAPubKey1 = KeySupport.buildJavaRSAPublicKey(rsaPubKey1);
        javaECPubKey_NamedCurve1 = KeySupport.buildJavaECPublicKey(ecPubKey_NamedCurve1);
        // SunEC provider doesn't support explicit params, so use a custom method. 
        javaECPubKey_ExplicitParams1 = buildECPublicKeyWithExplicitParams(ecPubKey_ExplicitParams1);

        xmlRSAKeyValue1 = (RSAKeyValue) buildXMLObject(RSAKeyValue.DEFAULT_ELEMENT_NAME);
        Modulus modulus = (Modulus) buildXMLObject(Modulus.DEFAULT_ELEMENT_NAME);
        Exponent exponent = (Exponent) buildXMLObject(Exponent.DEFAULT_ELEMENT_NAME);
        modulus.setValueBigInt(javaRSAPubKey1.getModulus());
        exponent.setValueBigInt(javaRSAPubKey1.getPublicExponent());
        xmlRSAKeyValue1.setModulus(modulus);
        xmlRSAKeyValue1.setExponent(exponent);

        xmlDHKeyValue1 = (DHKeyValue) buildXMLObject(DHKeyValue.DEFAULT_ELEMENT_NAME);
        org.opensaml.xmlsec.encryption.P dhP =
                (org.opensaml.xmlsec.encryption.P) buildXMLObject(org.opensaml.xmlsec.encryption.P.DEFAULT_ELEMENT_NAME);
        org.opensaml.xmlsec.encryption.Q dhQ =
                (org.opensaml.xmlsec.encryption.Q) buildXMLObject(org.opensaml.xmlsec.encryption.Q.DEFAULT_ELEMENT_NAME);
        Generator gen = (Generator) buildXMLObject(Generator.DEFAULT_ELEMENT_NAME);
        Public pub = (Public) buildXMLObject(Public.DEFAULT_ELEMENT_NAME);
        dhP.setValueBigInt(javaDHPubKey1.getParams().getP());
        dhQ.setValueBigInt(DHSupport.getPrimeQDomainParameter(javaDHPubKey1));
        gen.setValueBigInt(javaDHPubKey1.getParams().getG());
        pub.setValueBigInt(javaDHPubKey1.getY());
        xmlDHKeyValue1.setP(dhP);
        xmlDHKeyValue1.setQ(dhQ);
        xmlDHKeyValue1.setGenerator(gen);
        xmlDHKeyValue1.setPublic(pub);

        xmlDSAKeyValue1 = (DSAKeyValue) buildXMLObject(DSAKeyValue.DEFAULT_ELEMENT_NAME);
        P p = (P) buildXMLObject(P.DEFAULT_ELEMENT_NAME);
        Q q = (Q) buildXMLObject(Q.DEFAULT_ELEMENT_NAME);
        G g = (G) buildXMLObject(G.DEFAULT_ELEMENT_NAME);
        Y y1 = (Y) buildXMLObject(Y.DEFAULT_ELEMENT_NAME);
        p.setValueBigInt(javaDSAPubKey1.getParams().getP());
        q.setValueBigInt(javaDSAPubKey1.getParams().getQ());
        g.setValueBigInt(javaDSAPubKey1.getParams().getG());
        y1.setValueBigInt(javaDSAPubKey1.getY());
        xmlDSAKeyValue1.setP(p);
        xmlDSAKeyValue1.setQ(q);
        xmlDSAKeyValue1.setG(g);
        xmlDSAKeyValue1.setY(y1);

        xmlDSAKeyValue1NoParams = (DSAKeyValue) buildXMLObject(DSAKeyValue.DEFAULT_ELEMENT_NAME);
        Y y2 = (Y) buildXMLObject(Y.DEFAULT_ELEMENT_NAME);
        y2.setValueBigInt(javaDSAPubKey1.getY());
        xmlDSAKeyValue1NoParams.setY(y2);
        javaDSAParams1 = javaDSAPubKey1.getParams();
        
        xmlECKeyValue_NamedCurve1 = buildXMLObject(ECKeyValue.DEFAULT_ELEMENT_NAME);
        NamedCurve namedCurve1 = buildXMLObject(NamedCurve.DEFAULT_ELEMENT_NAME);
        org.opensaml.xmlsec.signature.PublicKey xmlECPublicKey_NamedCurve1 =
                buildXMLObject(org.opensaml.xmlsec.signature.PublicKey.DEFAULT_ELEMENT_NAME);
        namedCurve1.setURI("urn:oid:" + ecPubKey_NamedCurve1_OID);
        xmlECPublicKey_NamedCurve1.setValue(Base64Support.encode(ECSupport.encodeECPointUncompressed(
                javaECPubKey_NamedCurve1.getW(), javaECPubKey_NamedCurve1.getParams().getCurve()),
                Base64Support.UNCHUNKED));
        xmlECKeyValue_NamedCurve1.setNamedCurve(namedCurve1);
        xmlECKeyValue_NamedCurve1.setPublicKey(xmlECPublicKey_NamedCurve1);
        
        // Note: this is expressing the explicit params EC pub key as a named curve in KeyInfo.
        // Update or add test cases + control data if we ever support the ECKeyValue with ECParameters variant.
        xmlECKeyValue_ExplicitParams1 = buildXMLObject(ECKeyValue.DEFAULT_ELEMENT_NAME);
        NamedCurve namedCurve_Explicit1 = buildXMLObject(NamedCurve.DEFAULT_ELEMENT_NAME);
        org.opensaml.xmlsec.signature.PublicKey xmlECPublicKey_ExplicitParams1 =
                buildXMLObject(org.opensaml.xmlsec.signature.PublicKey.DEFAULT_ELEMENT_NAME);
        namedCurve_Explicit1.setURI("urn:oid:" + ecPubKey_ExplicitParams1_OID);
        xmlECPublicKey_ExplicitParams1.setValue(Base64Support.encode(ECSupport.encodeECPointUncompressed(
                javaECPubKey_ExplicitParams1.getW(), javaECPubKey_ExplicitParams1.getParams().getCurve()),
                Base64Support.UNCHUNKED));
        xmlECKeyValue_ExplicitParams1.setNamedCurve(namedCurve_Explicit1);
        xmlECKeyValue_ExplicitParams1.setPublicKey(xmlECPublicKey_ExplicitParams1);
        
    }

    /**
     * Test converting XML X509Certificate to java.security.cert.X509Certificate.
     * 
     * @throws CertificateException ...
     */
    @Test
    public void testCertConversionXMLtoJava() throws CertificateException {
        java.security.cert.X509Certificate javaCert = KeyInfoSupport.getCertificate(xmlCert1);
        assert javaCert != null;
        Assert.assertEquals(javaCert.getSubjectX500Principal().getName(X500Principal.RFC2253), cert1SubjectDN,
                "Cert1 SubjectDN");
        final String xmlCert1Value = xmlCert1.getValue();
        assert xmlCert1Value != null;
        Assert.assertEquals(javaCert, X509Support.decodeCertificate(xmlCert1Value), "Java cert was not the expected value");

        List<java.security.cert.X509Certificate> javaCertList = KeyInfoSupport.getCertificates(xmlX509Data);
        Assert.assertEquals(javaCertList.size(), numExpectedCerts, "# of certs returned");
        Assert.assertEquals(javaCertList.get(0).getSubjectX500Principal().getName(X500Principal.RFC2253), cert1SubjectDN,
                "Cert1 SubjectDN");
        Assert.assertEquals(javaCertList.get(1).getSubjectX500Principal().getName(X500Principal.RFC2253), cert2SubjectDN,
                "Cert2 SubjectDN");

        javaCertList = KeyInfoSupport.getCertificates(keyInfo);
        Assert.assertEquals(javaCertList.size(), numExpectedCerts, "# of certs returned");
        Assert.assertEquals(javaCertList.get(0).getSubjectX500Principal().getName(X500Principal.RFC2253), cert1SubjectDN,
                "Cert1 SubjectDN");
        Assert.assertEquals(javaCertList.get(1).getSubjectX500Principal().getName(X500Principal.RFC2253), cert2SubjectDN,
                "Cert2 SubjectDN");
    }

    /**
     * Test converting XML X509CRL to java.security.cert.X509CRL.
     * 
     * @throws CRLException ...
     * @throws CertificateException ...
     */
    @Test
    public void testCRLConversionXMLtoJava() throws CertificateException, CRLException {
        final java.security.cert.X509CRL javaCRL = KeyInfoSupport.getCRL(xmlCRL1);
        assert javaCRL != null;
        Assert.assertEquals(javaCRL.getIssuerX500Principal().getName(X500Principal.RFC2253), crl1IssuerDN, "CRL IssuerDN");
        final String crl1 = xmlCRL1.getValue();
        assert crl1 != null;
        Assert.assertEquals(javaCRL, X509Support.decodeCRL(crl1), "Java CRL was not the expected value");

        List<java.security.cert.X509CRL> javaCRLList = KeyInfoSupport.getCRLs(xmlX509Data);

        Assert.assertEquals(javaCRLList.size(), numExpectedCRLs, "# of CRLs returned");
        Assert.assertEquals(javaCRLList.get(0).getIssuerX500Principal().getName(X500Principal.RFC2253), crl1IssuerDN,
                "CRL IssuerDN");

        javaCRLList = KeyInfoSupport.getCRLs(keyInfo);
        Assert.assertEquals(javaCRLList.size(), numExpectedCRLs, "# of CRLs returned");
        Assert.assertEquals(javaCRLList.get(0).getIssuerX500Principal().getName(X500Principal.RFC2253), crl1IssuerDN,
                "CRL IssuerDN");
    }

    /**
     * Test converting java.security.cert.X509Certificate to XML X509Certificate.
     * 
     * @throws CertificateException ...
     */
    @Test
    public void testCertConversionJavaToXML() throws CertificateException {
        final X509Certificate xmlCert = KeyInfoSupport.buildX509Certificate(javaCert1);
        final String val = xmlCert.getValue();
        assert val != null;
        Assert.assertEquals(X509Support.decodeCertificate(val), javaCert1,
                "Java X509Certificate encoding to XMLObject failed");
    }

    /**
     * Test converting java.security.cert.X509CRL to XML X509CRL.
     * 
     * @throws CRLException ...
     * @throws CertificateException ...
     */
    @Test
    public void testCRLConversionJavaToXML() throws CertificateException, CRLException {
        final X509CRL xmlCRL = KeyInfoSupport.buildX509CRL(javaCRL1);
        final String val = xmlCRL.getValue();
        assert val != null;
        Assert.assertEquals(X509Support.decodeCRL(val), javaCRL1,
                "Java X509CRL encoding to XMLObject failed");
    }
    
    /** Test conversion of DSA public keys from XML to Java security native type. */
    @Test
    public void testDSAConversionXMLToJava() {
        PublicKey key = null;
        DSAPublicKey dsaKey = null;

        try {
            key = KeyInfoSupport.getDSAKey(xmlDSAKeyValue1);
        } catch (KeyException e) {
            Assert.fail("DSA key conversion XML to Java failed: " + e);
        }
        dsaKey = (DSAPublicKey) key;
        Assert.assertNotNull(dsaKey, "Generated key was not an instance of DSAPublicKey");
        Assert.assertEquals(dsaKey, javaDSAPubKey1, "Generated key was not the expected value");

        try {
            key = KeyInfoSupport.getDSAKey(xmlDSAKeyValue1NoParams, javaDSAParams1);
        } catch (KeyException e) {
            Assert.fail("DSA key conversion XML to Java failed: " + e);
        }
        dsaKey = (DSAPublicKey) key;
        Assert.assertNotNull(dsaKey, "Generated key was not an instance of DSAPublicKey");
        Assert.assertEquals(dsaKey, javaDSAPubKey1, "Generated key was not the expected value");

        try {
            key = KeyInfoSupport.getDSAKey(xmlDSAKeyValue1NoParams);
            Assert.fail("DSA key conversion XML to Java failed should have thrown an exception but didn't");
        } catch (KeyException e) {
            // do nothing, we expect to fail b/c not complete set of DSAParams
        }
    }

    /** Test conversion of DH public keys from XML to Java security native type. */
    @Test
    public void testDHConversionXMLToJava() {
        PublicKey key = null;
        DHPublicKey dhKey = null;

        try {
            key = KeyInfoSupport.getDHKey(xmlDHKeyValue1);
        } catch (KeyException e) {
            Assert.fail("DH key conversion XML to Java failed: " + e);
        }
        dhKey = (DHPublicKey) key;
        Assert.assertNotNull(dhKey, "Generated key was not an instance of DHPublicKey");
        Assert.assertEquals(dhKey, javaDHPubKey1, "Generated key was not the expected value");
    }

    /** Test conversion of RSA public keys from XML to Java security native type. */
    @Test
    public void testRSAConversionXMLToJava() {
        PublicKey key = null;
        RSAPublicKey rsaKey = null;

        try {
            key = KeyInfoSupport.getRSAKey(xmlRSAKeyValue1);
        } catch (KeyException e) {
            Assert.fail("RSA key conversion XML to Java failed: " + e);
        }
        rsaKey = (RSAPublicKey) key;
        Assert.assertNotNull(rsaKey, "Generated key was not an instance of RSAPublicKey");
        Assert.assertEquals(rsaKey, javaRSAPubKey1, "Generated key was not the expected value");
    }

    /** Test conversion of EC public keys from XML to Java security native type. */
    @Test
    public void testECConversionXMLToJavaWithNamedCurve() {
        PublicKey key = null;
        ECPublicKey ecKey = null;

        try {
            key = KeyInfoSupport.getECKey(xmlECKeyValue_NamedCurve1);
        } catch (KeyException e) {
            Assert.fail("RSA key conversion XML to Java failed: " + e);
        }
        ecKey = (ECPublicKey) key;
        Assert.assertNotNull(ecKey, "Generated key was not an instance of ECPublicKey");
        Assert.assertEquals(ecKey, javaECPubKey_NamedCurve1, "Generated key was not the expected value");
    }
    
    /** Test conversion of EC public keys from XML to Java security native type. */
    @Test
    public void testECConversionXMLToJavaWithExplicitParameters() {
        PublicKey key = null;
        ECPublicKey ecKey = null;

        try {
            key = KeyInfoSupport.getECKey(xmlECKeyValue_ExplicitParams1);
        } catch (KeyException e) {
            Assert.fail("RSA key conversion XML to Java failed: " + e);
        }
        ecKey = (ECPublicKey) key;
        assert ecKey != null;
        // The standard equals() test below doesn't work b/c the standard ECParameterSpec doesn't really implement equals()beyond
        // the standard reference compare, and the control key is from BC, so the instance object isn't the same (constant one) as from SunEC.
        // So use our Enhanced- equality wrapper helper instead.
        //Assert.assertEquals(ecKey, javaECPubKey_ExplicitParams1);
        Assert.assertEquals(ecKey.getW(), javaECPubKey_ExplicitParams1.getW());
        Assert.assertEquals(new EnhancedECParameterSpec(ecKey.getParams()),
                new EnhancedECParameterSpec(javaECPubKey_ExplicitParams1.getParams()));

    }

    /** Test conversion of DH public keys from Java security native type to XML. 
     * @throws EncodingException on base64 encoding error*/
    @Test
    public void testDHConversionJavaToXML() throws EncodingException {
        DHKeyValue dhKeyValue = KeyInfoSupport.buildDHKeyValue(javaDHPubKey1);
        assert dhKeyValue != null;
        Assert.assertEquals(
                Constraint.isNotNull(dhKeyValue.getPublic(), "Public was null").getValueBigInt(),
                javaDHPubKey1.getY(),
                "Generated DHKeyValue Public component was not the expected value");
        Assert.assertEquals(
                Constraint.isNotNull(dhKeyValue.getP(), "P was null").getValueBigInt(),
                javaDHPubKey1.getParams().getP(),
                "Generated DHKeyValue P component was not the expected value");
        Assert.assertEquals(
                Constraint.isNotNull(dhKeyValue.getGenerator(), "Generator was null").getValueBigInt(),
                javaDHPubKey1.getParams().getG(),
                "Generated DHKeyValue Generator component was not the expected value");
        Assert.assertEquals(
                Constraint.isNotNull(dhKeyValue.getQ(), "Q was null").getValueBigInt(),
                DHSupport.getPrimeQDomainParameter(javaDHPubKey1),
                "Generated DHKeyValue Q component was not the expected value");
    }

    /** Test conversion of DSA public keys from Java security native type to XML. 
     * @throws EncodingException on base64 encoding error*/
    @Test
    public void testDSAConversionJavaToXML() throws EncodingException {
        DSAKeyValue dsaKeyValue = KeyInfoSupport.buildDSAKeyValue(javaDSAPubKey1);
        assert dsaKeyValue != null;
        Assert.assertEquals(
                Constraint.isNotNull(dsaKeyValue.getY(), "Y was null").getValueBigInt(),
                javaDSAPubKey1.getY(),
                "Generated DSAKeyValue Y component was not the expected value");
        Assert.assertEquals(
                Constraint.isNotNull(dsaKeyValue.getP(), "P was null").getValueBigInt(),
                javaDSAPubKey1.getParams().getP(),
                "Generated DSAKeyValue P component was not the expected value");
        Assert.assertEquals(
                Constraint.isNotNull(dsaKeyValue.getQ(), "Q was null").getValueBigInt(),
                javaDSAPubKey1.getParams().getQ(),
                "Generated DSAKeyValue Q component was not the expected value");
        Assert.assertEquals(
                Constraint.isNotNull(dsaKeyValue.getG(), "G was null").getValueBigInt(),
                javaDSAPubKey1.getParams().getG(),
                "Generated DSAKeyValue G component was not the expected value");
    }

    /** Test conversion of RSA public keys from Java security native type to XML. 
     * @throws EncodingException on base64 encoding error*/
    @Test
    public void testRSAConversionJavaToXML() throws EncodingException {
        RSAKeyValue rsaKeyValue = KeyInfoSupport.buildRSAKeyValue(javaRSAPubKey1);
        assert rsaKeyValue != null;
        Assert.assertEquals(
                Constraint.isNotNull(rsaKeyValue.getModulus(), "Modulus was null").getValueBigInt(),
                javaRSAPubKey1.getModulus(),
                "Generated RSAKeyValue modulus component was not the expected value");
        Assert.assertEquals(
                Constraint.isNotNull(rsaKeyValue.getExponent(), "Exponent was null").getValueBigInt(),
                javaRSAPubKey1.getPublicExponent(),
                "Generated RSAKeyValue exponent component was not the expected value");
    }

    /** Test conversion of EC public keys from Java security native type to XML. 
     * @throws EncodingException on base64 encoding error
     * @throws DecodingException 
     * @throws KeyException */
    @Test
    public void testECConversionJavaToXMLWithNamedCurve() throws EncodingException, KeyException, DecodingException {
        ECKeyValue ecKeyValue = KeyInfoSupport.buildECKeyValue(javaECPubKey_NamedCurve1);
        assert ecKeyValue != null;
        Assert.assertEquals(
                Constraint.isNotNull(ecKeyValue.getNamedCurve(), "NamedCurve").getURI(),
                "urn:oid:" + ecPubKey_NamedCurve1_OID);
        final org.opensaml.xmlsec.signature.PublicKey pubKey = ecKeyValue.getPublicKey();
        assert pubKey != null;
        
        final var curve = ECSupport.getNamedCurve("urn:oid:" + ecPubKey_NamedCurve1_OID);
        assert curve != null;
        Assert.assertEquals(
                ECSupport.decodeECPoint(
                        Base64Support.decode(Constraint.isNotNull(pubKey.getValue(), "Public key value was null")),
                        curve.getParameterSpec().getCurve()), javaECPubKey_NamedCurve1.getW());
    }

    /** Test conversion of EC public keys from Java security native type to XML. 
     * @throws EncodingException on base64 encoding error
     * @throws DecodingException 
     * @throws KeyException */
    @Test
    public void testECConversionJavaToXMLWithExplicitParameters() throws EncodingException, KeyException, DecodingException {
        ECKeyValue ecKeyValue = KeyInfoSupport.buildECKeyValue(javaECPubKey_ExplicitParams1);
        assert ecKeyValue != null;
        Assert.assertEquals(
                Constraint.isNotNull(ecKeyValue.getNamedCurve(), "NamedCurve").getURI(),
                "urn:oid:" + ecPubKey_ExplicitParams1_OID);
        final org.opensaml.xmlsec.signature.PublicKey pubKey = ecKeyValue.getPublicKey();
        assert pubKey != null;

        final var curve = ECSupport.getNamedCurve("urn:oid:" + ecPubKey_ExplicitParams1_OID);
        assert curve != null;
        Assert.assertEquals(
                ECSupport.decodeECPoint(
                        Base64Support.decode(Constraint.isNotNull(pubKey.getValue(), "Public key value was null")),
                        curve.getParameterSpec().getCurve()), javaECPubKey_ExplicitParams1.getW());
    }

    /** Tests extracting a DH public key from a KeyValue. */
    @Test
    public void testGetDHKey() {
        keyValue.setRSAKeyValue(null);
        keyValue.setDHKeyValue(xmlDHKeyValue1);

        PublicKey pk = null;
        DHPublicKey dhKey = null;
        try {
            pk = KeyInfoSupport.getKey(keyValue);
        } catch (KeyException e) {
            Assert.fail("Extraction of key from KeyValue failed: " + e);
        }
        Assert.assertTrue(pk instanceof DHPublicKey, "Generated key was not an instance of DHPublicKey");
        dhKey = (DHPublicKey) pk;
        Assert.assertEquals(dhKey, javaDHPubKey1, "Generated key was not the expected value");

        keyValue.setDSAKeyValue(null);
    }

    /** Tests extracting a DSA public key from a KeyValue. */
    @Test
    public void testGetDSAKey() {
        keyValue.setRSAKeyValue(null);
        keyValue.setDSAKeyValue(xmlDSAKeyValue1);

        PublicKey pk = null;
        DSAPublicKey dsaKey = null;
        try {
            pk = KeyInfoSupport.getKey(keyValue);
        } catch (KeyException e) {
            Assert.fail("Extraction of key from KeyValue failed: " + e);
        }
        Assert.assertTrue(pk instanceof DSAPublicKey, "Generated key was not an instance of DSAPublicKey");
        dsaKey = (DSAPublicKey) pk;
        Assert.assertEquals(dsaKey, javaDSAPubKey1, "Generated key was not the expected value");

        keyValue.setDSAKeyValue(null);
    }

    /** Tests extracting a RSA public key from a KeyValue. */
    @Test
    public void testGetRSAKey() {
        keyValue.setDSAKeyValue(null);
        keyValue.setRSAKeyValue(xmlRSAKeyValue1);

        PublicKey pk = null;
        RSAPublicKey rsaKey = null;
        try {
            pk = KeyInfoSupport.getKey(keyValue);
        } catch (KeyException e) {
            Assert.fail("Extraction of key from KeyValue failed: " + e);
        }
        Assert.assertTrue(pk instanceof RSAPublicKey, "Generated key was not an instance of RSAPublicKey");
        rsaKey = (RSAPublicKey) pk;
        Assert.assertEquals(rsaKey, javaRSAPubKey1, "Generated key was not the expected value");

        keyValue.setRSAKeyValue(null);
    }

    /** Tests adding a public key as a KeyValue to KeyInfo. 
     * @throws EncodingException on base64 encoding error*/
    @Test
    public void testAddDSAPublicKey() throws EncodingException {
        keyInfo.getKeyValues().clear();

        KeyInfoSupport.addPublicKey(keyInfo, javaDSAPubKey1);
        KeyValue kv = keyInfo.getKeyValues().get(0);
        Assert.assertNotNull(kv, "KeyValue was null");
        DSAKeyValue dsaKeyValue = kv.getDSAKeyValue();
        assert dsaKeyValue != null;

        DSAPublicKey javaKey = null;
        try {
            javaKey = (DSAPublicKey) KeyInfoSupport.getDSAKey(dsaKeyValue);
        } catch (KeyException e) {
            Assert.fail("Extraction of Java key failed: " + e);
        }

        Assert.assertEquals(javaKey, javaDSAPubKey1, "Inserted DSA public key was not the expected value");

        keyInfo.getKeyValues().clear();
    }    

    /** Tests adding a public key as a KeyValue to KeyInfo. 
     * @throws EncodingException on base64 encoding error*/
    @Test
    public void testAddDHPublicKey() throws EncodingException {
        keyInfo.getKeyValues().clear();

        KeyInfoSupport.addPublicKey(keyInfo, javaDHPubKey1);
        KeyValue kv = keyInfo.getKeyValues().get(0);
        Assert.assertNotNull(kv, "KeyValue was null");
        DHKeyValue dhKeyValue = kv.getDHKeyValue();
        assert dhKeyValue != null;

        DHPublicKey javaKey = null;
        try {
            javaKey = (DHPublicKey) KeyInfoSupport.getDHKey(dhKeyValue);
        } catch (KeyException e) {
            Assert.fail("Extraction of Java key failed: " + e);
        }

        Assert.assertEquals(javaKey, javaDHPubKey1, "Inserted DH public key was not the expected value");

        keyInfo.getKeyValues().clear();
    }    

    /** Tests adding a public key as a KeyValue to KeyInfo. 
     * @throws EncodingException on base64 encoding error*/
    @Test
    public void testAddRSAPublicKey() throws EncodingException {
        keyInfo.getKeyValues().clear();

        KeyInfoSupport.addPublicKey(keyInfo, javaRSAPubKey1);
        KeyValue kv = keyInfo.getKeyValues().get(0);
        Assert.assertNotNull(kv, "KeyValue was null");
        RSAKeyValue rsaKeyValue = kv.getRSAKeyValue();
        assert rsaKeyValue != null;

        RSAPublicKey javaKey = null;
        try {
            javaKey = (RSAPublicKey) KeyInfoSupport.getRSAKey(rsaKeyValue);
        } catch (KeyException e) {
            Assert.fail("Extraction of Java key failed: " + e);
        }

        Assert.assertEquals(javaKey, javaRSAPubKey1, "Inserted RSA public key was not the expected value");

        keyInfo.getKeyValues().clear();
    }

    /** Tests adding a public key as a KeyValue to KeyInfo. 
     * @throws EncodingException on base64 encoding error*/
    @Test
    public void testAddECPublicKeyWithNamedCurve() throws EncodingException {
        keyInfo.getKeyValues().clear();

        KeyInfoSupport.addPublicKey(keyInfo, javaECPubKey_NamedCurve1);
        KeyValue kv = keyInfo.getKeyValues().get(0);
        Assert.assertNotNull(kv, "KeyValue was null");
        ECKeyValue ecKeyValue = kv.getECKeyValue();
        assert ecKeyValue != null;

        ECPublicKey javaKey = null;
        try {
            javaKey = (ECPublicKey) KeyInfoSupport.getECKey(ecKeyValue);
        } catch (KeyException e) {
            Assert.fail("Extraction of Java key failed: " + e);
        }

        Assert.assertEquals(javaKey, javaECPubKey_NamedCurve1, "Inserted EC public key with named curve was not the expected value");

        keyInfo.getKeyValues().clear();
    }

    /** Tests adding a public key as a KeyValue to KeyInfo. 
     * @throws EncodingException on base64 encoding error*/
    @Test
    public void testAddECPublicKeyWithExplicitParams() throws EncodingException {
        keyInfo.getKeyValues().clear();

        KeyInfoSupport.addPublicKey(keyInfo, javaECPubKey_ExplicitParams1);
        KeyValue kv = keyInfo.getKeyValues().get(0);
        Assert.assertNotNull(kv, "KeyValue was null");
        ECKeyValue ecKeyValue = kv.getECKeyValue();
        assert ecKeyValue != null;

        ECPublicKey javaKey = null;
        try {
            javaKey = (ECPublicKey) KeyInfoSupport.getECKey(ecKeyValue);
        } catch (KeyException e) {
            Assert.fail("Extraction of Java key failed: " + e);
        }
        
        assert javaKey != null;

        // The standard equals() test below doesn't work b/c the standard ECParameterSpec doesn't really implement equals()beyond
        // the standard reference compare, and the control key is from BC, so the instance object isn't the same (constant one) as from SunEC.
        // So use our Enhanced- equality wrapper helper instead.
        //Assert.assertEquals(javaKey, javaECPubKey_ExplicitParams1, "Inserted EC public key with explicit params was not the expected value");
        Assert.assertEquals(javaKey.getW(), javaECPubKey_ExplicitParams1.getW());
        Assert.assertEquals(new EnhancedECParameterSpec(javaKey.getParams()),
                new EnhancedECParameterSpec(javaECPubKey_ExplicitParams1.getParams()));

        keyInfo.getKeyValues().clear();
    }

    /** Tests adding a public key as a DEREncodedKeyValue to KeyInfo. */
    @Test
    public void testAddDEREncodedDSAPublicKey() {
       keyInfo.getXMLObjects(DEREncodedKeyValue.DEFAULT_ELEMENT_NAME).clear();
        
        try {
            KeyInfoSupport.addDEREncodedPublicKey(keyInfo, javaDSAPubKey1);
        } catch (NoSuchAlgorithmException e) {
            Assert.fail("Unsupported key algorithm: " + e);
        } catch (InvalidKeySpecException e) {
            Assert.fail("Unsupported key specification: " + e);
        }
        DEREncodedKeyValue kv = keyInfo.getDEREncodedKeyValues().get(0);
        Assert.assertNotNull(kv, "DEREncodedKeyValue was null");
        
        DSAPublicKey javaKey = null;
        try {
            javaKey = (DSAPublicKey) KeyInfoSupport.getKey(kv);
        } catch (KeyException e) {
            Assert.fail("Extraction of Java key failed: " + e);
        }
        
        Assert.assertEquals(javaDSAPubKey1, javaKey, "Inserted DSA public key was not the expected value");
        
        keyInfo.getDEREncodedKeyValues().clear();
    }
    
    /** Tests adding a public key as a DEREncodedKeyValue to KeyInfo. */
    @Test
    public void testAddDEREncodedRSAPublicKey() {
       keyInfo.getXMLObjects(DEREncodedKeyValue.DEFAULT_ELEMENT_NAME).clear();
        
        try {
            KeyInfoSupport.addDEREncodedPublicKey(keyInfo, javaRSAPubKey1);
        } catch (NoSuchAlgorithmException e) {
            Assert.fail("Unsupported key algorithm: " + e);
        } catch (InvalidKeySpecException e) {
            Assert.fail("Unsupported key specification: " + e);
        }
        DEREncodedKeyValue kv = keyInfo.getDEREncodedKeyValues().get(0);
        Assert.assertNotNull(kv, "DEREncodedKeyValue was null");
        
        RSAPublicKey javaKey = null;
        try {
            javaKey = (RSAPublicKey) KeyInfoSupport.getKey(kv);
        } catch (KeyException e) {
            Assert.fail("Extraction of Java key failed: " + e);
        }
        
        Assert.assertEquals(javaRSAPubKey1, javaKey, "Inserted RSA public key was not the expected value");
        
        keyInfo.getDEREncodedKeyValues().clear();
    }
    
    /** Tests adding a public key as a DEREncodedKeyValue to KeyInfo. */
    @Test
    public void testAddDEREncodedECPublicKeyWithNamedCurve() {
       keyInfo.getXMLObjects(DEREncodedKeyValue.DEFAULT_ELEMENT_NAME).clear();
        
        try {
            KeyInfoSupport.addDEREncodedPublicKey(keyInfo, javaECPubKey_NamedCurve1);
        } catch (NoSuchAlgorithmException e) {
            Assert.fail("Unsupported key algorithm: " + e);
        } catch (InvalidKeySpecException e) {
            Assert.fail("Unsupported key specification: " + e);
        }
        DEREncodedKeyValue kv = keyInfo.getDEREncodedKeyValues().get(0);
        Assert.assertNotNull(kv, "DEREncodedKeyValue was null");
        
        ECPublicKey javaKey = null;
        try {
            javaKey = (ECPublicKey) KeyInfoSupport.getKey(kv);
        } catch (KeyException e) {
            Assert.fail("Extraction of Java key failed: " + e);
        }
        
        Assert.assertEquals(javaECPubKey_NamedCurve1, javaKey, "Inserted EC public key was not the expected value");
        
        keyInfo.getDEREncodedKeyValues().clear();
    }
    
    /** Tests adding a public key as a DEREncodedKeyValue to KeyInfo. */
    @Test
    public void testAddDEREncodedECPublicKeyWithExplictParams() {
       keyInfo.getXMLObjects(DEREncodedKeyValue.DEFAULT_ELEMENT_NAME).clear();
        
       try {
           // As of this writing SunEC provider doesn't support explicit params,
           // and so the Java ECPublicKeys wind up not being equal below.  So for this test only,
           // register BC as the preferred provider, and unregister at the end.
           // (Note: Provider positions are 1-based, not 0-based).
           Security.insertProviderAt(new BouncyCastleProvider(), 1);
           
           try {
               KeyInfoSupport.addDEREncodedPublicKey(keyInfo, javaECPubKey_ExplicitParams1);
           } catch (NoSuchAlgorithmException e) {
               Assert.fail("Unsupported key algorithm: " + e);
           } catch (InvalidKeySpecException e) {
               Assert.fail("Unsupported key specification: " + e);
           }
           DEREncodedKeyValue kv = keyInfo.getDEREncodedKeyValues().get(0);
           Assert.assertNotNull(kv, "DEREncodedKeyValue was null");

           ECPublicKey javaKey = null;
           try {
               javaKey = (ECPublicKey) KeyInfoSupport.getKey(kv);
           } catch (KeyException e) {
               Assert.fail("Extraction of Java key failed: " + e);
           }

           Assert.assertEquals(javaECPubKey_ExplicitParams1, javaKey, "Inserted EC public key was not the expected value");
       } finally {
           Security.removeProvider("BC");
       }
        
       keyInfo.getDEREncodedKeyValues().clear();
    }
    
    /** Tests adding a public key as a DEREncodedKeyValue to KeyInfo. */
    @Test
    public void testAddDEREncodedDHPublicKey() {
       keyInfo.getXMLObjects(DEREncodedKeyValue.DEFAULT_ELEMENT_NAME).clear();
        
        try {
            KeyInfoSupport.addDEREncodedPublicKey(keyInfo, javaDHPubKey1);
        } catch (NoSuchAlgorithmException e) {
            Assert.fail("Unsupported key algorithm: " + e);
        } catch (InvalidKeySpecException e) {
            Assert.fail("Unsupported key specification: " + e);
        }
        DEREncodedKeyValue kv = keyInfo.getDEREncodedKeyValues().get(0);
        Assert.assertNotNull(kv, "DEREncodedKeyValue was null");
        
        DHPublicKey javaKey = null;
        try {
            javaKey = (DHPublicKey) KeyInfoSupport.getKey(kv);
        } catch (KeyException e) {
            Assert.fail("Extraction of Java key failed: " + e);
        }
        
        Assert.assertEquals(javaDHPubKey1, javaKey, "Inserted DH public key was not the expected value");
        
        keyInfo.getDEREncodedKeyValues().clear();
    }
    
    /**
     * Tests adding a certificate as a X509Data/X509Certificate to KeyInfo.
     * 
     * @throws CertificateException ...
     */
    @Test
    public void testAddX509Certificate() throws CertificateException {
        keyInfo.getX509Datas().clear();

        KeyInfoSupport.addCertificate(keyInfo, javaCert1);
        X509Data x509Data = keyInfo.getX509Datas().get(0);
        Assert.assertNotNull(x509Data, "X509Data was null");
        X509Certificate x509Cert = x509Data.getX509Certificates().get(0);
        Assert.assertNotNull(x509Cert, "X509Certificate was null");

        java.security.cert.X509Certificate javaCert = null;
        javaCert = KeyInfoSupport.getCertificate(x509Cert);

        Assert.assertEquals(javaCert, javaCert1, "Inserted X509Certificate was not the expected value");

        keyInfo.getX509Datas().clear();
    }

    /**
     * Tests adding a CRL as a X509Data/X509CRL to KeyInfo.
     * 
     * @throws CRLException ...
     */
    @Test
    public void testAddX509CRL() throws CRLException {
        keyInfo.getX509Datas().clear();

        KeyInfoSupport.addCRL(keyInfo, javaCRL1);
        X509Data x509Data = keyInfo.getX509Datas().get(0);
        Assert.assertNotNull(x509Data, "X509Data was null");
        X509CRL x509CRL = x509Data.getX509CRLs().get(0);
        Assert.assertNotNull(x509CRL, "X509CRL was null");

        java.security.cert.X509CRL javaCRL = null;
        javaCRL = KeyInfoSupport.getCRL(x509CRL);

        Assert.assertEquals(javaCRL, javaCRL1, "Inserted X509CRL was not the expected value");

        keyInfo.getX509Datas().clear();
    }

    /** Tests building a new X509SubjectName. */
    @Test
    public void testBuildSubjectName() {
        String name = "cn=foobar.example.org, o=Internet2";
        X509SubjectName xmlSubjectName = KeyInfoSupport.buildX509SubjectName(name);
        Assert.assertNotNull(xmlSubjectName, "Constructed X509SubjectName was null");
        Assert.assertEquals(xmlSubjectName.getValue(), name, "Unexpected subject name value");
    }

    /** Tests building a new X509IssuerSerial. */
    @Test
    public void testBuildIssuerSerial() {
        String name = "cn=CA.example.org, o=Internet2";
        BigInteger serialNumber = new BigInteger("42");
        X509IssuerSerial xmlIssuerSerial = KeyInfoSupport.buildX509IssuerSerial(name, serialNumber);
        Assert.assertNotNull(xmlIssuerSerial, "Constructed X509IssuerSerial was null");

        final X509IssuerName issuerName = xmlIssuerSerial.getX509IssuerName();
        assert issuerName != null;
        Assert.assertEquals(issuerName.getValue(), name, "Unexpected issuer name value");

        final X509SerialNumber issuerSerial = xmlIssuerSerial.getX509SerialNumber();
        assert issuerSerial != null;
        Assert.assertEquals(issuerSerial.getValue(), serialNumber, "Unexpected serial number");
    }

    /**
     * Tests building a new X509SKI from a certificate containing an SKI value.
     * 
     * @throws CertificateException ...
     * @throws DecodingException if an issue base64-decoding SKI values
     * @throws SecurityException if an issue building X509SKI.
     */
    @Test
    public void testBuildSubjectKeyIdentifier() throws CertificateException, DecodingException, SecurityException {
        final byte[] skiValue = Base64Support.decode(cert1SKIPlainBase64);
        final X509SKI xmlSKI = KeyInfoSupport.buildX509SKI(javaCert1);
        assert xmlSKI != null;
        final String SKI = xmlSKI.getValue();
        Assert.assertFalse(Strings.isNullOrEmpty(SKI), "SKI value was empty");
        assert SKI != null;
        byte[] xmlValue = Base64Support.decode(SKI);
        Assert.assertNotNull(xmlValue, "Decoded XML SKI value was null");
        Assert.assertTrue(Arrays.equals(skiValue, xmlValue), "Incorrect SKI value");

        // Test that a cert with no SKI produces null
        java.security.cert.X509Certificate noExtCert = X509Support.decodeCertificate(certNoExtensions);
        Assert.assertNotNull(noExtCert);
        final X509SKI noExtXMLSKI = KeyInfoSupport.buildX509SKI(noExtCert);
        Assert.assertNull(noExtXMLSKI, "Building X509SKI from cert without SKI should have generated null");
    }

    /**
     * Tests building a new X509Digest from a certificate.
     * 
     * @throws CertificateException ...
     * @throws DecodingException if an issue base64-decoding digests.
     */
    @Test
    public void testBuildDigest() throws CertificateException, DecodingException {
        byte[] digestValue = Base64Support.decode(cert1DigestBase64);
        X509Digest xmlDigest = null;
        try {
            xmlDigest = KeyInfoSupport.buildX509Digest(javaCert1, SignatureConstants.ALGO_ID_DIGEST_SHA1);
        } catch (NoSuchAlgorithmException e) {
            Assert.fail("Digest algorithm missing: " + e);
        }
        
        assert xmlDigest != null;
        final String digest = xmlDigest.getValue();
        
        Assert.assertNotNull(xmlDigest, "Constructed X509Digest was null");
        Assert.assertFalse(Strings.isNullOrEmpty(digest), "Digest value was empty");
        assert digest != null;
        final byte[] xmlValue = Base64Support.decode(digest);
        Assert.assertNotNull(xmlValue, "Decoded X509Digest value was null");
        Assert.assertTrue(Arrays.equals(digestValue, xmlValue), "Incorrect digest value");
    }
    
    
    
    //
    // Helpers
    //
    
    private ECPublicKey buildECPublicKeyWithExplicitParams(String encodedKey) throws KeyException {
        // Use BC for this for now, since standard Java's SunEC provider does not support explicit params
        try {
            Security.addProvider(new BouncyCastleProvider());
            final KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
            return (ECPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(Base64Support.decode(encodedKey)));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | DecodingException | NoSuchProviderException e) {
            throw new KeyException("Failed creating ECPublicKey containing explict params", e);
        } finally {
            Security.removeProvider("BC");
        }
    }

}