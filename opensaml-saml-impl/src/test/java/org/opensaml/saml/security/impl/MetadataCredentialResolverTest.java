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

package org.opensaml.saml.security.impl;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import net.shibboleth.shared.codec.EncodingException;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.saml.common.testing.SAMLTestSupport;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.PredicateRoleDescriptorResolver;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialContextSet;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.X509Support;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;

/**
 * Testing the metadata credential resolver.
 */
@SuppressWarnings({"null", "javadoc"})
public class MetadataCredentialResolverTest extends XMLObjectBaseTestCase {
    
    private String idpRSAPubKeyName = "IDP-SSO-RSA-Key";
    private RSAPublicKey idpRSAPubKey;
    private String idpRSAPubKeyBase64 = 
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDfCVgF2Lvhu0Q35FvmAVGMXc3i" +
        "1MojcqybcfVbfn0Tg/Aj5FvuAiDFg9KpGvMHDKdLOY+1xsKZqyIm58SFhW+5z51Y" +
        "pnblHGjuDtPtPbtspQ7pAOsknnvbKZrx7RGNOJyQZE3Qn88Y5ZBNzABusqNXjrWl" +
        "U9m4a+XNIFqM4YbJLwIDAQAB";
    
    private X509Certificate idpDSACert;
    private String idpDSACertBase64 = 
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
    
    private X509Certificate idpRSACert;
    private String idpRSACertBase64 = 
        "MIIC8TCCAdmgAwIBAgIBMjANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl" +
        "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDUyNTIwMDk1MVoX" +
        "DTE3MDUyMjIwMDk1MVowGjEYMBYGA1UEAxMPaWRwLmV4YW1wbGUub3JnMIGfMA0G" +
        "CSqGSIb3DQEBAQUAA4GNADCBiQKBgQDfCVgF2Lvhu0Q35FvmAVGMXc3i1Mojcqyb" +
        "cfVbfn0Tg/Aj5FvuAiDFg9KpGvMHDKdLOY+1xsKZqyIm58SFhW+5z51YpnblHGju" +
        "DtPtPbtspQ7pAOsknnvbKZrx7RGNOJyQZE3Qn88Y5ZBNzABusqNXjrWlU9m4a+XN" +
        "IFqM4YbJLwIDAQABo4GyMIGvMAkGA1UdEwQCMAAwLAYJYIZIAYb4QgENBB8WHU9w" +
        "ZW5TU0wgR2VuZXJhdGVkIENlcnRpZmljYXRlMB0GA1UdDgQWBBT2qDRFTzawttBG" +
        "jN6wxni/12tQQjBVBgNVHSMETjBMgBTV7mVT3lHah61C6kFu8q/W7kaUC6ExpC8w" +
        "LTESMBAGA1UEChMJSW50ZXJuZXQyMRcwFQYDVQQDEw5jYS5leGFtcGxlLm9yZ4IB" +
        "ATANBgkqhkiG9w0BAQUFAAOCAQEAlJYAou5ko3ujHVhOc4OB2AOOqdXAjThiXg6z" +
        "Tjezs7/F53b9IRt4in/k92y1tKZ87F/JcnH6MrzKfb8m5XtcYwtUSvmFTCp5rrFp" +
        "z1JhXlgnaWVJJ2G2vKLDGuPQvLV9zsWhnkbTPuzocvOotxl7w7LJvO3D/tzTAnnU" +
        "bgg1AfP+CTDs3F/ceHzWGVWTMUAmNGX8gMS2/xh66QoEzl7LBG8Xzpo0j+gSxe7h" +
        "Scb5iS4U/XUEbZylMUbbK57h9Bez8VVeO1jfwAniIBT0Ur9ksiYsAdyXYoXssGiF" +
        "bKW1K3QG1GA9wwGy5GvjyALuuXL4lEzFB0kMsGucNMfyyojX9A==";
    
    private String keyAuthorityCertBase64 = 
        "MIIDXTCCAkWgAwIBAgIBATANBgkqhkiG9w0BAQUFADAtMRIwEAYDVQQKEwlJbnRl" +
        "cm5ldDIxFzAVBgNVBAMTDmNhLmV4YW1wbGUub3JnMB4XDTA3MDQwOTA1NDcxMloX" +
        "DTE3MDQwNjA1NDcxMlowLTESMBAGA1UEChMJSW50ZXJuZXQyMRcwFQYDVQQDEw5j" +
        "YS5leGFtcGxlLm9yZzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANxM" +
        "5/6mBCcX+S7HApcKtfqdFRZzi6Ra91nkEzXOUcO+BPUdYqSxKGnCCso25ZOZP3gn" +
        "JVkY8Pi7VWrCM6wRgIMyQDvNYqCpNjkZGFkrMoa6fm8BSaDHJ1fz6l/eEl0CVU3U" +
        "uUAf0mXQLGm6Jannq8aMolRujlhE5iRaOJ2qp6wqsvyatK+vTgDngnwYVa4Cqu0j" +
        "UeNF28quST5D3gIuZ0OeFHSM2Z1WUKkwwsHqVkxBBcH1QE1JOGIoSnrxxl/o4VlL" +
        "WGEI8zq5qixE8VYtBBmijBwIL5ETy2fwiqcsvimQaQAtAfbtpO3kBSs8n7nnzMUH" +
        "fRlcebGkwwcNfYcD5hcCAwEAAaOBhzCBhDAdBgNVHQ4EFgQU1e5lU95R2oetQupB" +
        "bvKv1u5GlAswVQYDVR0jBE4wTIAU1e5lU95R2oetQupBbvKv1u5GlAuhMaQvMC0x" +
        "EjAQBgNVBAoTCUludGVybmV0MjEXMBUGA1UEAxMOY2EuZXhhbXBsZS5vcmeCAQEw" +
        "DAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOCAQEAbqrozetM/iBVIUir9k14" +
        "HbLEP0lZ6jOPWFIUFXMOn0t8+Ul7PMp9Rdn+7OGQIoJw97/mam3kpQ/HmjJMFYv6" +
        "MGsDQ4vAOiQiaTKzgMhrnCdVpVH9uz4ARHiysCujnjH9sehTWgybY8pXzzSG5BAj" +
        "EGowHq01nXxq2K4yAJSdAUBYLfuSKW1uRU6cmEa9uzl9EvoZfAF3BLnGlPqu4Zaj" +
        "H2NC9ZY0y19LX4yeJLHL1sY4fyxb3x8QhcCXiI16awBTr/VnUpJjSe9vh+OudWGe" +
        "yCL/KhjlrDkjJ3hIxBF5mP/Y27cFpRnC2gECkieURvh52OyuqkzpbOrTN5rD9fNi" +
        "nA==";
    
    
    // On IDPSSODescriptor, has RSAKeyValue (usage = encryption) and DSA cert (usage = signing)
    private String protocolFoo = "PROTOCOL_FOO";
    
    // On IDPSSODescriptor, has RSA cert (no usage)
    private String protocolBar = "PROTOCOL_BAR";
    
    private QName idpRole = IDPSSODescriptor.DEFAULT_ELEMENT_NAME;
    
    private String idpEntityID = "http://idp.example.org/shibboleth";
    
    private String mdFileName = "/org/opensaml/saml/security/test1-metadata.xml";
    
    private PredicateRoleDescriptorResolver roleResolver;
    
    private MetadataCredentialResolver mdCredResolver;
    
    private EntityIdCriterion entityIdCriteria;
    
    private EntityRoleCriterion roleCriteria;;
    
    private CriteriaSet criteriaSet;
    

    @BeforeMethod
    protected void setUp() throws Exception {
        idpRSAPubKey = KeySupport.buildJavaRSAPublicKey(idpRSAPubKeyBase64);
        idpDSACert = X509Support.decodeCertificate(idpDSACertBase64);
        idpRSACert = X509Support.decodeCertificate(idpRSACertBase64);
        X509Support.decodeCertificate(keyAuthorityCertBase64);
        
        final Document mdDoc = parserPool.parse(MetadataCredentialResolverTest.class.getResourceAsStream(mdFileName));
        
        final DOMMetadataResolver mdProvider = new DOMMetadataResolver(mdDoc.getDocumentElement());
        mdProvider.setId("test");
        mdProvider.initialize();
        
        roleResolver = new PredicateRoleDescriptorResolver(mdProvider);
        roleResolver.initialize();
        
        mdCredResolver = new MetadataCredentialResolver();
        mdCredResolver.setRoleDescriptorResolver(roleResolver);
        mdCredResolver.setKeyInfoCredentialResolver(SAMLTestSupport.buildBasicInlineKeyInfoResolver());
        mdCredResolver.initialize();
        
        entityIdCriteria = new EntityIdCriterion(idpEntityID);
        // by default set protocol to null
        roleCriteria = new EntityRoleCriterion(idpRole);
        
        criteriaSet = new CriteriaSet();
        criteriaSet.add(entityIdCriteria);
        criteriaSet.add(roleCriteria);
    }

    /**
     * Test protocol null, and no usage.
     * Should get 3 credentials, 2 from protocolFoo and 1 from protocolBar.
     * 
     * @throws SecurityException ...
     * @throws ResolverException ...
     */
    @Test
    public void testNoProtocolNoUsage() throws SecurityException, ResolverException {
        List<Credential> resolved = new ArrayList<>();
        for (Credential credential : mdCredResolver.resolve(criteriaSet)) {
           resolved.add(credential);
           checkContextAndID(credential, idpEntityID, idpRole, true);
        }
        
        Assert.assertEquals(resolved.size(), 3, "Incorrect number of credentials resolved");
        
        for (final Credential credential : resolved) {
            X509Credential x509Cred;
            final UsageType usage = credential.getUsageType();
            assert usage != null;
            switch(usage) {
                case SIGNING:
                    x509Cred = (X509Credential) credential;
                    Assert.assertEquals(x509Cred.getEntityCertificate(), idpDSACert, "Unexpected value for certificate");
                    break;
                case ENCRYPTION:
                    Assert.assertTrue(credential.getKeyNames().contains(idpRSAPubKeyName), 
                            "Expected value for key name not found");
                    Assert.assertEquals(credential.getPublicKey(), idpRSAPubKey, "Unexpected value for key");
                    break;
                case UNSPECIFIED:
                    x509Cred = (X509Credential) credential;
                    Assert.assertEquals(x509Cred.getEntityCertificate(), idpRSACert, "Unexpected value for certificate");
                    break;
                default:
            }
        }
    }
    
    /**
     * Test protocol null, and usage = encryption.
     * Should get 2 credentials, 1 from protocolFoo and 1 from protocolBar.
     * 
     * @throws SecurityException ...
     * @throws ResolverException ...
     */
    @Test
    public void testNoProtocolUsageEncryption() throws SecurityException, ResolverException {
        criteriaSet.add( new UsageCriterion(UsageType.ENCRYPTION) );
        
        List<Credential> resolved = new ArrayList<>();
        for (Credential credential : mdCredResolver.resolve(criteriaSet)) {
           resolved.add(credential);
           checkContextAndID(credential, idpEntityID, idpRole, true);
        }
        
        Assert.assertEquals(resolved.size(), 2, "Incorrect number of credentials resolved");
        
        for (final Credential credential : resolved) {
            X509Credential x509Cred;
            final UsageType usage = credential.getUsageType();
            assert usage != null;
            switch(usage) {
                case SIGNING:
                    Assert.fail("Credential with invalid usage was resolved");
                    break;
                case ENCRYPTION:
                    Assert.assertTrue(credential.getKeyNames().contains(idpRSAPubKeyName), 
                            "Expected value for key name not found");
                    Assert.assertEquals(credential.getPublicKey(), idpRSAPubKey, "Unexpected value for key");
                    break;
                case UNSPECIFIED:
                    x509Cred = (X509Credential) credential;
                    Assert.assertEquals(x509Cred.getEntityCertificate(), idpRSACert, "Unexpected value for certificate");
                    break;
                default:
            }
        }
    }
    
    /**
     * Test protocol null, and usage = signing.
     * Should get 2 credentials, 1 from protocolFoo and 1 from protocolBar.
     * 
     * @throws SecurityException ...
     * @throws ResolverException ...
     */
    @Test
    public void testNoProtocolUsageSigning() throws SecurityException, ResolverException {
        criteriaSet.add( new UsageCriterion(UsageType.SIGNING) );
        
        List<Credential> resolved = new ArrayList<>();
        for (Credential credential : mdCredResolver.resolve(criteriaSet)) {
           resolved.add(credential);
           checkContextAndID(credential, idpEntityID, idpRole, true);
        }
        
        Assert.assertEquals(resolved.size(), 2, "Incorrect number of credentials resolved");
        
        for (final Credential credential : resolved) {
            X509Credential x509Cred;
            final UsageType usage = credential.getUsageType();
            assert usage != null;
            switch(usage) {
                case SIGNING:
                    x509Cred = (X509Credential) credential;
                    Assert.assertEquals(x509Cred.getEntityCertificate(), idpDSACert, "Unexpected value for certificate");
                    break;
                case ENCRYPTION:
                    Assert.fail("Credential with invalid usage was resolved");
                    break;
                case UNSPECIFIED:
                    x509Cred = (X509Credential) credential;
                    Assert.assertEquals(x509Cred.getEntityCertificate(), idpRSACert, "Unexpected value for certificate");
                    break;
                default:
            }
        }
    }
    
    /**
     * Test 1 protocol (FOO), and no usage .
     * Should get 2 credentials.
     * 
     * @throws SecurityException ...
     * @throws ResolverException ...
     */
    @Test
    public void testProtocolFOONoUsage() throws SecurityException, ResolverException {
        criteriaSet.add(new ProtocolCriterion(protocolFoo));
        
        List<Credential> resolved = new ArrayList<>();
        for (Credential credential : mdCredResolver.resolve(criteriaSet)) {
           resolved.add(credential);
           checkContextAndID(credential, idpEntityID, idpRole, true);
        }
        
        Assert.assertEquals(resolved.size(), 2, "Incorrect number of credentials resolved");
        
        for (final Credential credential : resolved) {
            X509Credential x509Cred;
            final UsageType usage = credential.getUsageType();
            assert usage != null;
            switch(usage) {
                case SIGNING:
                    x509Cred = (X509Credential) credential;
                    Assert.assertEquals(x509Cred.getEntityCertificate(), idpDSACert, "Unexpected value for certificate");
                    break;
                case ENCRYPTION:
                    Assert.assertTrue(credential.getKeyNames().contains(idpRSAPubKeyName), 
                            "Expected value for key name not found");
                    Assert.assertEquals(credential.getPublicKey(), idpRSAPubKey, "Unexpected value for key");
                    break;
                case UNSPECIFIED:
                    Assert.fail("Credential was resolved from invalid protocol");
                    break;
                default:
            }
        }
    }
    
    /**
     * Test 1 protocol (FOO), and usage = signing.
     * Should get 1 credentials.
     * 
     * @throws SecurityException ...
     * @throws ResolverException ...
     */
    @Test
    public void testProtocolFOOUsageSigning() throws SecurityException, ResolverException {
        criteriaSet.add(new ProtocolCriterion(protocolFoo));
        criteriaSet.add( new UsageCriterion(UsageType.SIGNING) );
        
        List<Credential> resolved = new ArrayList<>();
        for (Credential credential : mdCredResolver.resolve(criteriaSet)) {
           resolved.add(credential);
           checkContextAndID(credential, idpEntityID, idpRole, true);
        }
        
        Assert.assertEquals(resolved.size(), 1, "Incorrect number of credentials resolved");
        
        for (final Credential credential : resolved) {
            X509Credential x509Cred;
            final UsageType usage = credential.getUsageType();
            assert usage != null;
            switch(usage) {
                case SIGNING:
                    x509Cred = (X509Credential) credential;
                    Assert.assertEquals(x509Cred.getEntityCertificate(), idpDSACert, "Unexpected value for certificate");
                    break;
                case ENCRYPTION:
                    Assert.fail("Credential was resolved from invalid protocol or usage");
                    break;
                case UNSPECIFIED:
                    Assert.fail("Credential was resolved from invalid protocol or usage");
                    break;
                default:
            }
        }
    }
    
    /**
     * Test 1 protocol (FOO), and usage encryption.
     * Should get 1 credentials.
     * 
     * @throws SecurityException ...
     * @throws ResolverException ...
     */
    @Test
    public void testProtocolFOOUsageEncryption() throws SecurityException, ResolverException {
        criteriaSet.add(new ProtocolCriterion(protocolFoo));
        criteriaSet.add( new UsageCriterion(UsageType.ENCRYPTION) );
        
        List<Credential> resolved = new ArrayList<>();
        for (Credential credential : mdCredResolver.resolve(criteriaSet)) {
           resolved.add(credential);
           checkContextAndID(credential, idpEntityID, idpRole, true);
        }
        
        Assert.assertEquals(resolved.size(), 1, "Incorrect number of credentials resolved");
        
        for (final Credential credential : resolved) {
            final UsageType usage = credential.getUsageType();
            assert usage != null;
            switch(usage) {
                case SIGNING:
                    Assert.fail("Credential was resolved from invalid protocol or usage");
                    break;
                case ENCRYPTION:
                    Assert.assertTrue(credential.getKeyNames().contains(idpRSAPubKeyName), 
                            "Expected value for key name not found");
                    Assert.assertEquals(credential.getPublicKey(), idpRSAPubKey, "Unexpected value for key");
                    break;
                case UNSPECIFIED:
                    Assert.fail("Credential was resolved from invalid protocol or usage");
                    break;
                default:
            }
        }
    }
        
    /**
     * Test 1 protocol (BAR), and no usage.
     * Should get 1 credentials.
     * 
     * @throws SecurityException ...
     * @throws ResolverException ...
     */
    @Test
    public void testProtocolBARNoUsage() throws SecurityException, ResolverException {
        criteriaSet.add(new ProtocolCriterion(protocolBar));
        
        List<Credential> resolved = new ArrayList<>();
        for (Credential credential : mdCredResolver.resolve(criteriaSet)) {
           resolved.add(credential);
           checkContextAndID(credential, idpEntityID, idpRole, true);
        }
        
        Assert.assertEquals(resolved.size(), 1, "Incorrect number of credentials resolved");
        
        for (final Credential credential : resolved) {
            X509Credential x509Cred;
            final UsageType usage = credential.getUsageType();
            assert usage != null;
            switch(usage) {
                case SIGNING:
                    Assert.fail("Credential was resolved from invalid protocol");
                    break;
                case ENCRYPTION:
                    Assert.fail("Credential was resolved from invalid protocol");
                    break;
                case UNSPECIFIED:
                    x509Cred = (X509Credential) credential;
                    Assert.assertEquals(x509Cred.getEntityCertificate(), idpRSACert, "Unexpected value for certificate");
                    break;
                default:
            }
        }
    }
    
    /**
     * Test 1 protocol (BAR), and usage = signing.
     * Should get 1 credentials.
     * 
     * @throws SecurityException ...
     * @throws ResolverException ...
     */
    @Test
    public void testProtocolBARUsageSigning() throws SecurityException, ResolverException {
        criteriaSet.add(new ProtocolCriterion(protocolBar));
        criteriaSet.add( new UsageCriterion(UsageType.SIGNING) );
        
        List<Credential> resolved = new ArrayList<>();
        for (Credential credential : mdCredResolver.resolve(criteriaSet)) {
           resolved.add(credential);
           checkContextAndID(credential, idpEntityID, idpRole, true);
        }
        
        Assert.assertEquals(resolved.size(), 1, "Incorrect number of credentials resolved");
        
        for (final Credential credential : resolved) {
            X509Credential x509Cred;
            final UsageType usage = credential.getUsageType();
            assert usage != null;
            switch(usage) {
                case SIGNING:
                    Assert.fail("Credential was resolved from invalid protocol or usage");
                    break;
                case ENCRYPTION:
                    Assert.fail("Credential was resolved from invalid protocol or usage");
                    break;
                case UNSPECIFIED:
                    x509Cred = (X509Credential) credential;
                    Assert.assertEquals(x509Cred.getEntityCertificate(), idpRSACert, "Unexpected value for certificate");
                    break;
                default:
            }
        }
    }
    
    /**
     * Test 1 protocol (BAR), and usage = encryption.
     * Should get 1 credentials.
     * 
     * @throws SecurityException ...
     * @throws ResolverException ...
     */
    @Test
    public void testProtocolBARUsageEncryption() throws SecurityException, ResolverException {
        criteriaSet.add(new ProtocolCriterion(protocolBar));
        criteriaSet.add( new UsageCriterion(UsageType.ENCRYPTION) );
        
        List<Credential> resolved = new ArrayList<>();
        for (Credential credential : mdCredResolver.resolve(criteriaSet)) {
           resolved.add(credential);
           checkContextAndID(credential, idpEntityID, idpRole, true);
        }
        
        Assert.assertEquals(resolved.size(), 1, "Incorrect number of credentials resolved");
        
        for (final Credential credential : resolved) {
            X509Credential x509Cred;
            final UsageType usage = credential.getUsageType();
            assert usage != null;
            switch(usage) {
                case SIGNING:
                    Assert.fail("Credential was resolved from invalid protocol or usage");
                    break;
                case ENCRYPTION:
                    Assert.fail("Credential was resolved from invalid protocol or usage");
                    break;
                case UNSPECIFIED:
                    x509Cred = (X509Credential) credential;
                    Assert.assertEquals(x509Cred.getEntityCertificate(), idpRSACert, "Unexpected value for certificate");
                    break;
                default:
            }
        }
    }
    
    /**
     * Test caching behavior across 2 resolutions.
     * 
     * @throws SecurityException ...
     * @throws ResolverException ...
     */
    @Test
    public void testCaching() throws SecurityException, ResolverException {
        HashSet<Credential> resolved1 = new HashSet<>();
        for (Credential credential : mdCredResolver.resolve(criteriaSet)) {
           resolved1.add(credential);
        }
        
        HashSet<Credential> resolved2 = new HashSet<>();
        for (Credential credential : mdCredResolver.resolve(criteriaSet)) {
           resolved2.add(credential);
        }
        
        Assert.assertEquals(resolved1.size(), 3, "Incorrect number of credentials resolved");
        Assert.assertEquals(resolved2.size(), 3, "Incorrect number of credentials resolved");
        
        // On credentials we don't override equals(), so the only way the sets will be identical
        // is if all the members are the same instances.  This will not be true if caching
        // didn't happen and new Credential instances were created on the second run.
        Assert.assertTrue(resolved1.equals(resolved2), 
                "Resolved credential sets were non-equal, caching must have failed");
    }
    
    /**
     * Test resolving directly from a RoleDescriptor input.
     * 
     * @throws ComponentInitializationException ...
     * @throws ResolverException ...
     * @throws CertificateEncodingException ...
     * @throws EncodingException on base64 encoding error
     */
    @Test 
    public void testDirectResolutionFromRoleDescriptor() throws ComponentInitializationException, ResolverException, CertificateEncodingException, EncodingException {
        // Use a new instance that does not have a role descriptor resolver, just for good measure.
        mdCredResolver = new MetadataCredentialResolver();
        mdCredResolver.setKeyInfoCredentialResolver(SAMLTestSupport.buildBasicInlineKeyInfoResolver());
        mdCredResolver.initialize();
        
        EntityDescriptor entityDesc = buildXMLObject(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        entityDesc.setEntityID(idpEntityID);
        
        IDPSSODescriptor idpSSODesc = buildXMLObject(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        idpSSODesc.setParent(entityDesc);
        
        KeyDescriptor keyDesc1 = buildXMLObject(KeyDescriptor.DEFAULT_ELEMENT_NAME);
        KeyInfo keyInfo1 = buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        KeyInfoSupport.addPublicKey(keyInfo1, idpRSAPubKey);
        KeyInfoSupport.addKeyName(keyInfo1, idpRSAPubKeyName);
        keyDesc1.setKeyInfo(keyInfo1);
        keyDesc1.setUse(UsageType.ENCRYPTION);
        idpSSODesc.getKeyDescriptors().add(keyDesc1);
        
        KeyDescriptor keyDesc2 = buildXMLObject(KeyDescriptor.DEFAULT_ELEMENT_NAME);
        KeyInfo keyInfo2 = buildXMLObject(KeyInfo.DEFAULT_ELEMENT_NAME);
        KeyInfoSupport.addCertificate(keyInfo2, idpRSACert);
        keyDesc2.setKeyInfo(keyInfo2);
        keyDesc2.setUse(UsageType.SIGNING);
        idpSSODesc.getKeyDescriptors().add(keyDesc2);
        
        RoleDescriptorCriterion roleDescCriterion = new RoleDescriptorCriterion(idpSSODesc);
        CriteriaSet criteria =  new CriteriaSet(roleDescCriterion);
        
        List<Credential> resolved = new ArrayList<>();
        for (Credential credential : mdCredResolver.resolve(criteria)) {
            resolved.add(credential);
            checkContextAndID(credential, idpEntityID, idpRole, false);
        }
        
        Assert.assertEquals(resolved.size(), 2);
        
        for (final Credential credential : resolved) {
            X509Credential x509Cred;
            final UsageType usage = credential.getUsageType();
            assert usage != null;
            switch(usage) {
                case SIGNING:
                    x509Cred = (X509Credential) credential;
                    Assert.assertEquals(x509Cred.getEntityCertificate(), idpRSACert, "Unexpected value for certificate");
                    break;
                case ENCRYPTION:
                    Assert.assertTrue(credential.getKeyNames().contains(idpRSAPubKeyName));
                    Assert.assertEquals(credential.getPublicKey(), idpRSAPubKey, "Unexpected value for public key");
                    break;
                case UNSPECIFIED:
                    Assert.fail("Credential was resolved with an invalid usage");
                    break;
                default:
            }
        }
        
    }
    
    /**
     * Test fails correctly when required inputs are missing.
     * 
     * @throws ResolverException ...
     */
    @Test(expectedExceptions=ResolverException.class)
    public void testMissingRequiredInputs() throws ResolverException {
        mdCredResolver.resolve(new CriteriaSet());
    }
    
    /**
     * Test fails correctly when required KeyInfo credential resolver is not configured
     * 
     * @throws ComponentInitializationException ...
     */
    @Test(expectedExceptions=ComponentInitializationException.class)
    public void testMissingKeyInfoCredentialResolver() throws ComponentInitializationException {
        mdCredResolver = new MetadataCredentialResolver();
        mdCredResolver.initialize();
    }
    
    /**
     * Test that fails when entityID+role name are supplied, but no role descriptor resolver was configured.
     * 
     * @throws ComponentInitializationException ...
     * @throws ResolverException ...
     */
    @Test(expectedExceptions=ResolverException.class)
    public void testMissingRequiredRoleDescriptorResolver() throws ComponentInitializationException, ResolverException {
        mdCredResolver = new MetadataCredentialResolver();
        mdCredResolver.setKeyInfoCredentialResolver(SAMLTestSupport.buildBasicInlineKeyInfoResolver());
        mdCredResolver.initialize();
        
        mdCredResolver.resolve(criteriaSet);
    }
    
    /**
     * Check expected entity ID and also that expected data is available from the metadata context.
     * 
     * @param credential the credential to evaluate
     * @param entityID the expected entity ID value
     * @param role the expected type of role from the context role descriptor data
     * @param checkEntitiesDesc whether to check for and validate an EntitiesDescriptor grandparent
     */
    private void checkContextAndID(@Nonnull final Credential credential, @Nonnull String entityID, @Nonnull QName role,
            boolean checkEntitiesDesc) {
        Assert.assertEquals(credential.getEntityId(), entityID, "Unexpected value found for credential entityID");
        
        final CredentialContextSet contextSet = credential.getCredentialContextSet();
        assert contextSet != null;
        SAMLMDCredentialContext mdContext = contextSet.get(SAMLMDCredentialContext.class);
        assert mdContext != null;
        
        Assert.assertNotNull(mdContext.getRoleDescriptor());
        final RoleDescriptor contextRole = mdContext.getRoleDescriptor();
        assert contextRole != null;
        Assert.assertEquals(contextRole.getElementQName(), role, "Unexpected value for context role descriptor");
        
        Assert.assertTrue(contextRole.getParent() instanceof EntityDescriptor);
        final EntityDescriptor entityDescriptor = (EntityDescriptor) contextRole.getParent();
        assert entityDescriptor != null;
        Assert.assertEquals(entityDescriptor.getEntityID(), entityID, "Unexpected value for entity descriptor entity ID");
        
        if (checkEntitiesDesc) {
            Assert.assertTrue(entityDescriptor.getParent() instanceof EntitiesDescriptor);
            final EntitiesDescriptor entitiesDescriptor = (EntitiesDescriptor) entityDescriptor.getParent();
            assert entitiesDescriptor != null;
            
            final Extensions exts = entitiesDescriptor.getExtensions();
            assert exts != null;
            Assert.assertNotNull(exts.getUnknownXMLObjects().get(0));
        }
    }

}