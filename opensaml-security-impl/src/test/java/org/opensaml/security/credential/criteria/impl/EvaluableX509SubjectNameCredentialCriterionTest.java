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

package org.opensaml.security.credential.criteria.impl;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.X509Certificate;

import javax.annotation.Nonnull;
import javax.security.auth.x500.X500Principal;

import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.X509SubjectNameCriterion;
import org.opensaml.security.x509.X509Support;

@SuppressWarnings("javadoc")
public class EvaluableX509SubjectNameCredentialCriterionTest {
    
    private BasicX509Credential credential;
    private X500Principal subjectName;
    private X509SubjectNameCriterion criteria;
    
    private X509Certificate entityCert;
    @Nonnull private String entityCertBase64 = 
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
    
    
    public EvaluableX509SubjectNameCredentialCriterionTest() {
        
    }

    @BeforeMethod
    protected void setUp() throws Exception {
        entityCert = X509Support.decodeCertificate(entityCertBase64);
        subjectName = new X500Principal("cn=foobar.example.org, O=Internet2");
        
        credential = new BasicX509Credential(entityCert);
        
        criteria = new X509SubjectNameCriterion(subjectName);
    }
    
    @Test
    public void testSatisfy() {
        final EvaluableX509SubjectNameCredentialCriterion evalCrit = new EvaluableX509SubjectNameCredentialCriterion(criteria);
        Assert.assertTrue(evalCrit.test(credential), "Credential should have matched the evaluable criteria");
    }

    @Test
    public void testNotSatisfy() {
        criteria.setSubjectName( new X500Principal("cn=SomeOtherName, o=SomeOtherOrg"));
        final EvaluableX509SubjectNameCredentialCriterion evalCrit = new EvaluableX509SubjectNameCredentialCriterion(criteria);
        Assert.assertFalse(evalCrit.test(credential), "Credential should NOT have matched the evaluable criteria");
    }
    
    @Test
    public void testNotSatisfyWrongCredType() throws NoSuchAlgorithmException, NoSuchProviderException {
        final BasicCredential basicCred = new BasicCredential(KeySupport.generateKey("AES", 128, null));
        final EvaluableX509SubjectNameCredentialCriterion evalCrit = new EvaluableX509SubjectNameCredentialCriterion(criteria);
        Assert.assertFalse(evalCrit.test(basicCred), "Credential should NOT have matched the evaluable criteria");
    }
    
    @Test
    public void testRegistry() throws Exception {
        final EvaluableCredentialCriterion evalCrit = EvaluableCredentialCriteriaRegistry.getEvaluator(criteria);
        assert evalCrit != null;
        Assert.assertTrue(evalCrit.test(credential), "Credential should have matched the evaluable criteria");
    }
}
