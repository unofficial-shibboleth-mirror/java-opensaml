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

package org.opensaml.security.httpclient;

import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_CLIENT_TLS_CREDENTIAL;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_CRITERIA_SET;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_HOSTNAME_VERIFIER;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_SERVER_TLS_CREDENTIAL_TRUSTED;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_TLS_CIPHER_SUITES;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_TLS_PROTOCOLS;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_TRUST_ENGINE;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_SERVER_TLS_FAILURE_IS_FATAL;

import java.io.File;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.resolver.CriteriaSet;

import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.TrustedNamesCriterion;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.X509Support;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Unit test for {@link HttpClientSecuritySupport}.
 */
@SuppressWarnings("javadoc")
public class HttpClientSecuritySupportTest {
    
    private X509Certificate cert;
    
    private String certDERPath = "/data/certificate.der";
    
    
    @BeforeClass
    public void generatedTestData() throws NoSuchAlgorithmException, NoSuchProviderException, CertificateException, URISyntaxException {
        cert = X509Support.decodeCertificate(new File(HttpClientSecuritySupportTest.class.getResource(certDERPath).toURI()));
    }
    
    @Test
    public void testMarshalNullSecurityParameters() {
        HttpClientContext context = HttpClientContext.create();
        
        HttpClientSecuritySupport.marshalSecurityParameters(context, null, false);
        
        Assert.assertNull(context.getCredentialsProvider());
        Assert.assertNull(context.getAttribute(CONTEXT_KEY_TRUST_ENGINE));
        Assert.assertNull(context.getAttribute(CONTEXT_KEY_CRITERIA_SET));
        Assert.assertNull(context.getAttribute(CONTEXT_KEY_TLS_PROTOCOLS));
        Assert.assertNull(context.getAttribute(CONTEXT_KEY_TLS_CIPHER_SUITES));
        Assert.assertNull(context.getAttribute(CONTEXT_KEY_CLIENT_TLS_CREDENTIAL));
        Assert.assertNull(context.getAttribute(CONTEXT_KEY_HOSTNAME_VERIFIER));
        Assert.assertNull(context.getAttribute(CONTEXT_KEY_SERVER_TLS_FAILURE_IS_FATAL));
    }
    
    @Test
    public void testMarshalSecurityParametersEmptyContext() {
        HttpClientContext context = HttpClientContext.create();
        
        HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setCredentialsProvider(new BasicCredentialsProvider());
        params.setTLSTrustEngine(new MockTrustEngine());
        params.setTLSCriteriaSet(new CriteriaSet());
        params.setTLSProtocols(CollectionSupport.singletonList("foo"));
        params.setTLSCipherSuites(CollectionSupport.singletonList("foo"));
        params.setClientTLSCredential(new BasicX509Credential(cert));
        params.setHostnameVerifier(new DefaultHostnameVerifier());
        params.setServerTLSFailureFatal(Boolean.TRUE);
        
        HttpClientSecuritySupport.marshalSecurityParameters(context, params, false);
        
        Assert.assertSame(context.getCredentialsProvider(), params.getCredentialsProvider());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TRUST_ENGINE), params.getTLSTrustEngine());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_CRITERIA_SET), params.getTLSCriteriaSet());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TLS_PROTOCOLS), params.getTLSProtocols());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TLS_CIPHER_SUITES), params.getTLSCipherSuites());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_CLIENT_TLS_CREDENTIAL), params.getClientTLSCredential());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_HOSTNAME_VERIFIER), params.getHostnameVerifier());
        Assert.assertTrue(((Boolean)context.getAttribute(CONTEXT_KEY_SERVER_TLS_FAILURE_IS_FATAL)));
    }

    
    @Test
    public void testMarshalSecurityParametersWithReplacement() {
        HttpClientContext context = HttpClientContext.create();
        
        context.setCredentialsProvider(new BasicCredentialsProvider());
        context.setAttribute(CONTEXT_KEY_TRUST_ENGINE, new MockTrustEngine());
        context.setAttribute(CONTEXT_KEY_CRITERIA_SET, new CriteriaSet());
        context.setAttribute(CONTEXT_KEY_TLS_PROTOCOLS, CollectionSupport.singletonList("foo"));
        context.setAttribute(CONTEXT_KEY_TLS_CIPHER_SUITES, CollectionSupport.singletonList("foo"));
        context.setAttribute(CONTEXT_KEY_CLIENT_TLS_CREDENTIAL, new BasicX509Credential(cert));
        context.setAttribute(CONTEXT_KEY_HOSTNAME_VERIFIER, new DefaultHostnameVerifier());
        context.setAttribute(CONTEXT_KEY_SERVER_TLS_FAILURE_IS_FATAL, Boolean.FALSE);
        
        HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setCredentialsProvider(new BasicCredentialsProvider());
        params.setTLSTrustEngine(new MockTrustEngine());
        params.setTLSCriteriaSet(new CriteriaSet());
        params.setTLSProtocols(CollectionSupport.singletonList("foo"));
        params.setTLSCipherSuites(CollectionSupport.singletonList("foo"));
        params.setClientTLSCredential(new BasicX509Credential(cert));
        params.setHostnameVerifier(new DefaultHostnameVerifier());
        params.setServerTLSFailureFatal(Boolean.TRUE);
        
        HttpClientSecuritySupport.marshalSecurityParameters(context, params, true);
        
        Assert.assertSame(context.getCredentialsProvider(), params.getCredentialsProvider());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TRUST_ENGINE), params.getTLSTrustEngine());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_CRITERIA_SET), params.getTLSCriteriaSet());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TLS_PROTOCOLS), params.getTLSProtocols());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TLS_CIPHER_SUITES), params.getTLSCipherSuites());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_CLIENT_TLS_CREDENTIAL), params.getClientTLSCredential());
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_HOSTNAME_VERIFIER), params.getHostnameVerifier());
        Assert.assertTrue(((Boolean)context.getAttribute(CONTEXT_KEY_SERVER_TLS_FAILURE_IS_FATAL)));
    }

    @Test
    public void testMarshalSecurityParametersWithoutReplacement() {
        HttpClientContext context = HttpClientContext.create();
        
        CredentialsProvider credProvider = new BasicCredentialsProvider();
        TrustEngine<X509Credential> trustEngine = new MockTrustEngine();
        CriteriaSet criteriaSet = new CriteriaSet();
        List<String> protocols = CollectionSupport.singletonList("foo");
        List<String> cipherSuites = CollectionSupport.singletonList("foo");
        X509Credential clientTLSCred = new BasicX509Credential(cert);
        HostnameVerifier verifier = new DefaultHostnameVerifier();
        
        context.setCredentialsProvider(credProvider);
        context.setAttribute(CONTEXT_KEY_TRUST_ENGINE, trustEngine);
        context.setAttribute(CONTEXT_KEY_CRITERIA_SET, criteriaSet);
        context.setAttribute(CONTEXT_KEY_TLS_PROTOCOLS, protocols);
        context.setAttribute(CONTEXT_KEY_TLS_CIPHER_SUITES, cipherSuites);
        context.setAttribute(CONTEXT_KEY_CLIENT_TLS_CREDENTIAL, clientTLSCred);
        context.setAttribute(CONTEXT_KEY_HOSTNAME_VERIFIER, verifier);
        context.setAttribute(CONTEXT_KEY_SERVER_TLS_FAILURE_IS_FATAL, Boolean.FALSE);
        
        HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setCredentialsProvider(new BasicCredentialsProvider());
        params.setTLSTrustEngine(new MockTrustEngine());
        params.setTLSCriteriaSet(new CriteriaSet());
        params.setTLSProtocols(CollectionSupport.singletonList("foo"));
        params.setTLSCipherSuites(CollectionSupport.singletonList("foo"));
        params.setClientTLSCredential(new BasicX509Credential(cert));
        params.setHostnameVerifier(new DefaultHostnameVerifier());
        params.setServerTLSFailureFatal(Boolean.TRUE);
        
        HttpClientSecuritySupport.marshalSecurityParameters(context, params, false);
        
        Assert.assertSame(context.getCredentialsProvider(), credProvider);
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TRUST_ENGINE), trustEngine);
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_CRITERIA_SET), criteriaSet);
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TLS_PROTOCOLS), protocols);
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_TLS_CIPHER_SUITES), cipherSuites);
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_CLIENT_TLS_CREDENTIAL), clientTLSCred);
        Assert.assertSame(context.getAttribute(CONTEXT_KEY_HOSTNAME_VERIFIER), verifier);
        Assert.assertFalse(((Boolean)context.getAttribute(CONTEXT_KEY_SERVER_TLS_FAILURE_IS_FATAL)));
    }
    
    @Test
    public void testSetContextValue() {
        String attribName = "MyAttrib";
        HttpClientContext context = null;
        
        // Empty context
        context = HttpClientContext.create();
        HttpClientSecuritySupport.setContextValue(context, attribName, "foo", false);
        Assert.assertEquals(context.getAttribute(attribName), "foo");
        
        // Empty context, null value
        context = HttpClientContext.create();
        HttpClientSecuritySupport.setContextValue(context, attribName, null, false);
        Assert.assertNull(context.getAttribute(attribName));
        
        // Don't replace existing
        context = HttpClientContext.create();
        context.setAttribute(attribName, "bar");
        HttpClientSecuritySupport.setContextValue(context, attribName, "foo", false);
        Assert.assertEquals(context.getAttribute(attribName), "bar");
        
        // Replace existing
        context = HttpClientContext.create();
        context.setAttribute(attribName, "bar");
        HttpClientSecuritySupport.setContextValue(context, attribName, "foo", true);
        Assert.assertEquals(context.getAttribute(attribName), "foo");
        
        // Don't replace because null value
        context = HttpClientContext.create();
        context.setAttribute(attribName, "bar");
        HttpClientSecuritySupport.setContextValue(context, attribName, null, true);
        Assert.assertEquals(context.getAttribute(attribName), "bar");
    }
    
    @DataProvider
    private Object[][] tlsCredentialEvaluatedData() {
        MockTrustEngine trustEngine = new MockTrustEngine();
        return new Object[][] {
                // "Standard" HTTPS pass cases
                new Object[] { trustEngine, "https", Boolean.TRUE, true },
                new Object[] { trustEngine, "https", Boolean.FALSE, true },
                
                // HTTPS, no trust engine: pass
                new Object[] { null, "https", null, true },
                new Object[] { null, "https", Boolean.TRUE, true },
                new Object[] { null, "https", Boolean.FALSE, true },
                
                // Non-HTTPS: pass
                new Object[] { trustEngine, "http", null, true },
                new Object[] { trustEngine, "http", Boolean.TRUE, true },
                new Object[] { trustEngine, "http", Boolean.FALSE, true },
                
                // No pass
                new Object[] { trustEngine, "https", null, false},
        };
    }
    
    @Test(dataProvider="tlsCredentialEvaluatedData")
    public void testCheckTLSCredentialEvaluated(MockTrustEngine trustEngine, String scheme, Boolean trusted, boolean shouldPass) {
        HttpClientContext context = new HttpClientContext();
        context.setAttribute(CONTEXT_KEY_TRUST_ENGINE, trustEngine);
        context.setAttribute(CONTEXT_KEY_SERVER_TLS_CREDENTIAL_TRUSTED, trusted);
        
        if (shouldPass) {
            try {
                HttpClientSecuritySupport.checkTLSCredentialEvaluated(context, scheme);
            } catch (SSLPeerUnverifiedException e) {
                Assert.fail("Exception thrown unexpectedly");
            }
        } else {
            try {
                HttpClientSecuritySupport.checkTLSCredentialEvaluated(context, scheme);
                Assert.fail("Exception was expected but not seen");
            } catch (SSLPeerUnverifiedException e) {
                // expected
            }
        }
    }
    
    @Test
    public void testAddDefaultTLSTrustEngineCriteria() {
        HttpGet request = new HttpGet("https://www.example.com/foobar");
        HttpClientContext context = new HttpClientContext();
        context.setAttribute(CONTEXT_KEY_TRUST_ENGINE, new MockTrustEngine());
        
        HttpClientSecuritySupport.addDefaultTLSTrustEngineCriteria(context, request);
        
        CriteriaSet criteria = (CriteriaSet) context.getAttribute(CONTEXT_KEY_CRITERIA_SET);
        Assert.assertNotNull(criteria);
        
        final UsageCriterion usage = criteria.get(UsageCriterion.class);
        assert usage != null;
        Assert.assertEquals(usage.getUsage(), UsageType.SIGNING);
        
        final TrustedNamesCriterion trustedNames = criteria.get(TrustedNamesCriterion.class);
        assert trustedNames != null;
        Assert.assertEquals(trustedNames.getTrustedNames().size(), 1);
        Assert.assertTrue(trustedNames.getTrustedNames().contains("www.example.com"));
        
        // Not https
        request = new HttpGet("http://www.example.com/foobar");
        context = new HttpClientContext();
        context.setAttribute(CONTEXT_KEY_TRUST_ENGINE, new MockTrustEngine());
        HttpClientSecuritySupport.addDefaultTLSTrustEngineCriteria(context, request);
        Assert.assertNull(context.getAttribute(CONTEXT_KEY_CRITERIA_SET));
        
        // No trust engine
        request = new HttpGet("https://www.example.com/foobar");
        context = new HttpClientContext();
        HttpClientSecuritySupport.addDefaultTLSTrustEngineCriteria(context, request);
        Assert.assertNull(context.getAttribute(CONTEXT_KEY_CRITERIA_SET));
    }
    
    
    // Helpers
    
    public static class MockTrustEngine implements TrustEngine<X509Credential>  {
        public boolean validate(@Nonnull final X509Credential token, @Nullable final CriteriaSet trustBasisCriteria)
                throws SecurityException {
            return false;
        }
    }

}
