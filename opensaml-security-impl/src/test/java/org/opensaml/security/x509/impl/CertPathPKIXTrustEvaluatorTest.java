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

package org.opensaml.security.x509.impl;

import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;

import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import java.io.InputStream;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.security.SecurityException;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.PKIXTrustEvaluator;
import org.opensaml.security.x509.PKIXValidationInformation;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.X509Support;

/**
 * Tests the {@link CertPathPKIXTrustEvaluator} implementation.
 */
@SuppressWarnings("javadoc")
public class CertPathPKIXTrustEvaluatorTest extends XMLObjectBaseTestCase {
    
    private static final String DATA_PATH = "/org/opensaml/security/x509/impl/";
    
    private static final Set<X509CRL> EMPTY_CRLS = new HashSet<>();
    
    private static final Set<X509Certificate> EMPTY_ANCHORS = new HashSet<>();
    
    private static final Integer MAX_DEPTH  = 10;
    
    private PKIXTrustEvaluator pkixEvaluator;
    
    private PKIXValidationInformation info;
    
    private X509Credential cred;
    
    private CertPathPKIXValidationOptions opts;
       
    private static Set<String> testPolicy1 = CollectionSupport.singleton("1.3.6.1.4.1.32473.2011.6.20");
    private static Set<String> testPolicy2 = CollectionSupport.singleton("1.3.6.1.4.1.32473.2011.6.21");
    
    @BeforeMethod
    protected void setUp() throws Exception {
        pkixEvaluator = new CertPathPKIXTrustEvaluator();
        info = null;
        cred = null;
        opts = null;
    }
    
    @Test
    public void testGood() {
        cred = getCredential("foo-1A1-good.crt");
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt", "inter1A-ca.crt", "inter1A1-ca.crt"),
                EMPTY_CRLS,
                MAX_DEPTH );
        
        testValidateSuccess("Valid path was specified", info, cred);
    }
    
    @Test
    public void testIncompletePath() {
        cred = getCredential("foo-1A1-good.crt");
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt", "inter1A-ca.crt"),
                EMPTY_CRLS,
                MAX_DEPTH );
        
        testValidateFailure("Incomplete path was specified, missing issuing CA certificate", info, cred);
    }
    
    @Test
    public void testNoAnchors() {
        cred = getCredential("foo-1A1-good.crt");
        info = getPKIXInfoSet(
                EMPTY_ANCHORS,
                EMPTY_CRLS,
                MAX_DEPTH );
        
        // Must have at least one trust anchor, otherwise it's a fatal processing error due to invalid inputs.
        testValidateProcessingError("No trust anchors specified", info, cred);
    }
    
    @Test
    public void testNonRootIssuerAsTrustAnchor() {
        cred = getCredential("foo-1A1-good.crt");
        info = getPKIXInfoSet(
                getCertificates("inter1A1-ca.crt"),
                EMPTY_CRLS,
                MAX_DEPTH );
        
        // Note this validates, b/c the issuing CA cert is present and is treated as
        // a Java TrustAnchor (i.e. a "most trusted cert"). Doesn't matter that it's not a root CA cert.
        testValidateSuccess("Incomplete path was specified, missing (non-issuing) CA certificate in path", info, cred);
    }
    
    @Test
    public void testRevokedV1() {
        cred = getCredential("foo-1A1-revoked.crt");
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt", "inter1A-ca.crt", "inter1A1-ca.crt"),
                EMPTY_CRLS,
                MAX_DEPTH );
        
        testValidateSuccess("Sanity check that revoked cert is otherwise good, sans CRLs", info, cred);
        
        cred = getCredential("foo-1A1-revoked.crt");
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt", "inter1A-ca.crt", "inter1A1-ca.crt"),
                getCRLS("inter1A1-v1.crl"),
                MAX_DEPTH );
        
        testValidateFailure("Specified certificate was revoked, V1 CRL was processed", info, cred);
    }
    
    @Test
    public void testRevokedV1CRLinCred() {
        cred = getCredential("foo-1A1-revoked.crt");
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt", "inter1A-ca.crt", "inter1A1-ca.crt"),
                EMPTY_CRLS,
                MAX_DEPTH );
        
        testValidateSuccess("Sanity check that revoked cert is otherwise good, sans CRLs", info, cred);
        
        cred = getCredential("foo-1A1-revoked.crt");
        ((BasicX509Credential)cred).setCRLs(getCRLS("inter1A1-v1.crl"));
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt", "inter1A-ca.crt", "inter1A1-ca.crt"),
                EMPTY_CRLS,
                MAX_DEPTH );
        
        testValidateFailure("Specified certificate was revoked, V1 CRL from credential was processed", info, cred);
    }
    
    @Test
    public void testRevokedV2() {
        cred = getCredential("foo-1A1-revoked.crt");
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt", "inter1A-ca.crt", "inter1A1-ca.crt"),
                EMPTY_CRLS,
                MAX_DEPTH );
        
        testValidateSuccess("Sanity check that revoked cert is otherwise good, sans CRLs", info, cred);
        
        cred = getCredential("foo-1A1-revoked.crt");
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt", "inter1A-ca.crt", "inter1A1-ca.crt"),
                getCRLS("inter1A1-v2.crl"),
                MAX_DEPTH );
        
        testValidateFailure("Specified certificate was revoked, V2 CRL was processed", info, cred);
    }
    
    @Test
    public void testRevokedV2CRLinCred() {
        cred = getCredential("foo-1A1-revoked.crt");
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt", "inter1A-ca.crt", "inter1A1-ca.crt"),
                EMPTY_CRLS,
                MAX_DEPTH );
        
        testValidateSuccess("Sanity check that revoked cert is otherwise good, sans CRLs", info, cred);
        
        cred = getCredential("foo-1A1-revoked.crt");
        ((BasicX509Credential)cred).setCRLs(getCRLS("inter1A1-v2.crl"));
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt", "inter1A-ca.crt", "inter1A1-ca.crt"),
                EMPTY_CRLS,
                MAX_DEPTH );
        
        testValidateFailure("Specified certificate was revoked, V2 CRL from credential was processed", info, cred);
    }
    
    @Test
    public void testEmptyCRL() {
        cred = getCredential("foo-1A1-good.crt");
        info = getPKIXInfoSet(
                getCertificates("inter1A1-ca.crt"),
                getCRLS("inter1A1-v1-empty.crl"),
                MAX_DEPTH );
        
        // Only supply 1 CRL, make the issuing intermediate CA the trust anchor, rest of chain irrelevant, 
        // so doesn't matter that CRL's are missing
        testValidateSuccess("Certificate was valid, empty V1 CRL was processed", info, cred);
    }
    
    @Test
    public void testIncompleteCRLsForChain() {
        cred = getCredential("foo-1A1-good.crt", "inter1A1-ca.crt", "inter1A-ca.crt");
        ((BasicX509Credential)cred).setCRLs(getCRLS("inter1A1-v2.crl"));
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt"),
                EMPTY_CRLS,
                MAX_DEPTH );
        
        // The only valid chain will be the full chain to the root.  Missing CRL's for the inter1A-ca and root1-ca will
        // cause validation to fail.
        testValidateFailure("Certificate was valid (non-revoked), V2 CRL for intermediate CA was processed, missing complete CRL info for chain", info, cred);
    }
    
    @Test
    public void testExpiredCRL() {
        cred = getCredential("foo-1A1-good.crt");
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt", "inter1A-ca.crt", "inter1A1-ca.crt"),
                getCRLS("inter1A1-v1-expired.crl"),
                MAX_DEPTH );
        
        // This is the expected behavior, apparently.
        testValidateFailure("Certificate was valid, expired V1 CRL was processed", info, cred);
    }
    
    @Test
    public void testNonRevokedCertWithNonEmptyCRL() {
        cred = getCredential("foo-1A1-good.crt");
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt", "inter1A-ca.crt", "inter1A1-ca.crt"),
                getCRLS("inter1A1-v1.crl"),
                MAX_DEPTH );
        
        testValidateSuccess("Certificate was valid, V1 CRL containing other revolcations was processed", info, cred);
    }
    
    @Test
    public void testEntityCertExpired() {
        cred = getCredential("foo-1A1-expired.crt");
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt", "inter1A-ca.crt", "inter1A1-ca.crt"),
                EMPTY_CRLS,
                MAX_DEPTH );
        
        testValidateFailure("Specified certificate was expired", info, cred);
    }
    
    @Test
    public void testGoodPathInCred() {
        cred = getCredential("foo-1A1-good.crt", "inter1A-ca.crt", "inter1A1-ca.crt");
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt"),
                EMPTY_CRLS,
                MAX_DEPTH );
        
        testValidateSuccess("Valid path was specified, intermediate path in credential chain", info, cred);
        
        cred = getCredential("foo-1A1-good.crt", "inter1A1-ca.crt");
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt", "inter1A-ca.crt"),
                EMPTY_CRLS,
                MAX_DEPTH );
        
        testValidateSuccess("Valid path was specified, intermediate path in credential chain", info, cred);
    }
    
    @Test
    public void testGoodPathInCredNoAnchors() {
        cred = getCredential("foo-1A1-good.crt", "inter1A1-ca.crt", "inter1A-ca.crt", "root1-ca.crt");
        info = getPKIXInfoSet(
                getCertificates("root2-ca.crt", "inter2A-ca.crt", "inter2B-ca.crt"),
                EMPTY_CRLS,
                MAX_DEPTH );
        
        testValidateFailure("Complete good path was specified in cred, but no relevant trust anchors", info, cred);
    }
      
    @Test
    public void testIncompletePathInCred() {
        cred = getCredential("foo-1A1-good.crt", "inter1A1-ca.crt");
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt"),
                EMPTY_CRLS,
                MAX_DEPTH );
        
        testValidateFailure("Incomplete path was specified, neither contains required intermediate cert", info, cred);
    }
      
    @Test
    public void testPathTooDeep() {
        cred = getCredential("foo-1A1-good.crt", "inter1A-ca.crt", "inter1A1-ca.crt");
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt"),
                EMPTY_CRLS,
                2 );
        
        testValidateSuccess("Valid path was specified, depth was equal to max path depth", info, cred);
        
        cred = getCredential("foo-1A1-good.crt", "inter1A-ca.crt", "inter1A1-ca.crt");
        info = getPKIXInfoSet(
                getCertificates("root1-ca.crt"),
                EMPTY_CRLS,
                1 );
        
        testValidateFailure("Valid path was specified, but depth exceeded max path depth", info, cred);
    }

    @Test
    public void testAnyPolicy() {
        cred = getCredential("mdt-signer.1.crt", "mdt-ica.1.crt");
        info = getPKIXInfoSet(
                getCertificates("mdt-root.crt"),
                EMPTY_CRLS,
                2 );
        opts = getPKIXOptions(testPolicy1, false, false);

        testValidateSuccess("Intermediate CA with anyPolicy (2.5.29.32.0) entry permitted", info, cred, opts);
    }

    @Test
    public void testExplicitPolicy() {
        cred = getCredential("mdt-signer.1.crt", "mdt-ica.1.crt");
        info = getPKIXInfoSet(
                getCertificates("mdt-root.crt"),
                EMPTY_CRLS,
                2 );
        opts = getPKIXOptions(testPolicy1, false, true);

        testValidateFailure("Intermediate CA with anyPolicy (2.5.29.32.0), but anyPolicy is inhibited", info, cred, opts);

        cred = getCredential("mdt-signer.2.crt", "mdt-ica.2.crt");

        testValidateSuccess("Intermediate CA with explicit policy " + testPolicy1, info, cred, opts);

        cred = getCredential("mdt-signer.3.crt", "mdt-ica.3.crt");

        testValidateSuccess("Intermediate CA with explicit policies " + testPolicy1 + ", " + testPolicy2, info, cred, opts);
    }

    @Test
    public void testExplicitPolicyMap() {
        cred = getCredential("mdt-signer.3.crt", "mdt-ica.3.crt");
        info = getPKIXInfoSet(
                getCertificates("mdt-root.crt"),
                EMPTY_CRLS,
                2 );
        opts = getPKIXOptions(testPolicy2, false, true);

        testValidateSuccess("Intermediate CA with policy mapping, and mapping is permitted", info, cred, opts);
    }
    
    @Test
    public void testExplicitPolicyNoMap() {
        cred = getCredential("mdt-signer.3.crt", "mdt-ica.3.crt");
        info = getPKIXInfoSet(
                getCertificates("mdt-root.crt"),
                EMPTY_CRLS,
                2 );
        opts = getPKIXOptions(testPolicy2, true, true);

        testValidateFailure("Intermediate CA with policy mapping, but mapping is inhibited", info, cred, opts);
    }    
    
    //********************
    //* Helper methods.  *
    //********************
    
    @Test(enabled = false)
    private void testValidateSuccess(String message, PKIXValidationInformation info, X509Credential cred) {
        try {
            if ( !pkixEvaluator.validate(info, cred) ) {
                Assert.fail("Evaluation of X509Credential failed, success was expected: " + message);
            }
        } catch (SecurityException e) {
            Assert.fail("Evaluation failed due to processing exception: " + e.getMessage());
        }
    }
    
    @Test(enabled = false)
    private void testValidateFailure(String message, PKIXValidationInformation info, X509Credential cred) {
        try {
            if ( pkixEvaluator.validate(info, cred) ) {
                Assert.fail("Evaluation of X509Credential succeeded, failure was expected: " + message);
            }
        } catch (SecurityException e) {
            Assert.fail("Evaluation failed due to processing exception: " + e.getMessage());
        }
    }
    
    @Test(enabled = false)
    private void testValidateProcessingError(String message, PKIXValidationInformation info, X509Credential cred) {
        try {
            if ( pkixEvaluator.validate(info, cred) ) {
                Assert.fail("Evaluation of X509Credential succeeded, processing failure was expected: " + message);
            } else {
                Assert.fail("Evaluation of X509Credential failed, but processing failure was expected: " + message);
            }
        } catch (SecurityException e) {
            // do nothing, failure expected
        }
    }

    private void testValidateSuccess(String message, PKIXValidationInformation info, X509Credential cred,
                CertPathPKIXValidationOptions opts) {
        try {
            PKIXTrustEvaluator pkixEvaluator = new CertPathPKIXTrustEvaluator(opts);
            if ( !pkixEvaluator.validate(info, cred) ) {
                Assert.fail("Evaluation of X509Credential failed, success was expected: " + message);
            }
        } catch (SecurityException e) {
            Assert.fail("Evaluation failed due to processing exception: " + e.getMessage());
        }
    }
    
    private void testValidateFailure(String message, PKIXValidationInformation info, X509Credential cred,
                CertPathPKIXValidationOptions opts) {
        try {
            PKIXTrustEvaluator pkixEvaluator = new CertPathPKIXTrustEvaluator(opts);
            if ( pkixEvaluator.validate(info, cred) ) {
                Assert.fail("Evaluation of X509Credential succeeded, failure was expected: " + message);
            }
        } catch (SecurityException e) {
            Assert.fail("Evaluation failed due to processing exception: " + e.getMessage());
        }
    }
            
    private BasicX509Credential getCredential(String entityCertFileName, String ... chainMembers) {
        
        X509Certificate entityCert = getCertificate(entityCertFileName);
        
        BasicX509Credential cred = new BasicX509Credential(entityCert);
        
        HashSet<X509Certificate> certChain = new HashSet<>();
        certChain.add(entityCert);
        
        for (String member: chainMembers) {
            certChain.add( getCertificate(member) );
        }
        
        cred.setEntityCertificateChain(certChain);
        
        return cred;
    }
    
    private PKIXValidationInformation getPKIXInfoSet(Collection<X509Certificate> certs,
                Collection<X509CRL> crls, Integer depth) {
        return new BasicPKIXValidationInformation(certs, crls, depth);
    }
    

    private CertPathPKIXValidationOptions getPKIXOptions(Set<String> initialPolicies,
                boolean policyMappingInhibit, boolean anyPolicyInhibit) {
        CertPathPKIXValidationOptions opts = new CertPathPKIXValidationOptions();

        opts.setInitialPolicies(initialPolicies);
        opts.setPolicyMappingInhibit(policyMappingInhibit);
        opts.setAnyPolicyInhibit(anyPolicyInhibit);

        return opts;
    }
    
    private Collection<X509Certificate> getCertificates(String ... certNames) {
        Set<X509Certificate> certs = new HashSet<>();
        for (String certName : certNames) {
           certs.add( getCertificate(certName) );
        }
        return certs;
    }
    
    private X509Certificate getCertificate(String fileName) {
        try {
            InputStream ins = getInputStream(fileName);
            byte[] encoded = new byte[ins.available()];
            ins.read(encoded);
            return X509Support.decodeCertificates(encoded).iterator().next();
        } catch (Exception e) {
            Assert.fail("Could not create certificate from file: " + fileName + ": " + e.getMessage());
        }
        return null;
    }
    
    private Collection<X509CRL> getCRLS(String ... crlNames) {
        Set<X509CRL> crls = new HashSet<>();
        for (String crlName : crlNames) {
           crls.add( getCRL(crlName) );
        }
        return crls;
    }
    
    private X509CRL getCRL(String fileName) {
        try {
            InputStream ins = getInputStream(fileName);
            byte[] encoded = new byte[ins.available()];
            ins.read(encoded);
            return X509Support.decodeCRLs(encoded).iterator().next();
        } catch (Exception e) {
            Assert.fail("Could not create CRL from file: " + fileName + ": " + e.getMessage());
        }
        return null;
    }
    
    private InputStream getInputStream(String fileName) {
        return  CertPathPKIXTrustEvaluatorTest.class.getResourceAsStream(DATA_PATH + fileName);
    }

}
