/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.security.httpclient.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.List;

import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.socket.LayeredConnectionSocketFactory;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.opensaml.security.credential.impl.StaticCredentialResolver;
import org.opensaml.security.httpclient.HttpClientSecurityConstants;
import org.opensaml.security.trust.impl.ExplicitKeyTrustEngine;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.X509Support;
import org.opensaml.security.x509.tls.impl.ThreadLocalX509CredentialContext;
import org.opensaml.security.x509.tls.impl.ThreadLocalX509TrustEngineContext;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.httpclient.HttpClientSupport;
import net.shibboleth.shared.resolver.CriteriaSet;

@SuppressWarnings("javadoc")
public class SecurityEnhancedTLSSocketFactoryTest {
    
    private static final String DATA_PATH = "/org/opensaml/security/x509/impl/";
    
    private SecurityEnhancedTLSSocketFactory securityEnhancedSocketFactory;
    
    private HttpContext httpContext;
    
    private String hostname = "foo.example.org";
    
    @BeforeMethod
    public void buildHttpContext() {
        httpContext = new HttpClientContext();
    }
    
    @AfterMethod
    public void clearThreadLocals() {
        ThreadLocalX509TrustEngineContext.clearCurrent();
        ThreadLocalX509CredentialContext.clearCurrent();
    }
    
    @Test
    public void testNoContextParametersHTTP() throws IOException {
        securityEnhancedSocketFactory = new SecurityEnhancedTLSSocketFactory(buildInnerSSLFactory(null, hostname));
        Socket socket = securityEnhancedSocketFactory.createSocket(httpContext);

        securityEnhancedSocketFactory.connectSocket(TimeValue.ofMilliseconds(0), socket, new HttpHost("http", hostname, 80), null, null, httpContext);

        Assert.assertFalse(ThreadLocalX509TrustEngineContext.haveCurrent());
        Assert.assertFalse(HttpClientSupport.getDynamicContextHandlerList(
                HttpClientContext.adapt(httpContext)).stream().anyMatch(h -> ThreadLocalServerTLSHandler.class.isInstance(h)));
        
        Assert.assertFalse(ThreadLocalX509CredentialContext.haveCurrent());
        Assert.assertFalse(HttpClientSupport.getDynamicContextHandlerList(
                HttpClientContext.adapt(httpContext)).stream().anyMatch(h -> ThreadLocalClientTLSCredentialHandler.class.isInstance(h)));
    }
    
    @Test
    public void testNoContextParametersHTTPS() throws IOException {
        X509Credential cred = getCredential("foo-1A1-good.crt");

        securityEnhancedSocketFactory = new SecurityEnhancedTLSSocketFactory(buildInnerSSLFactory(
                CollectionSupport.singletonList((Certificate)cred.getEntityCertificate()), hostname));
        Socket socket = securityEnhancedSocketFactory.createSocket(httpContext);

        securityEnhancedSocketFactory.connectSocket(TimeValue.ofMilliseconds(0), socket, new HttpHost("https", hostname, 443), null, null, httpContext);

        Assert.assertFalse(ThreadLocalX509TrustEngineContext.haveCurrent());
        Assert.assertFalse(HttpClientSupport.getDynamicContextHandlerList(
                HttpClientContext.adapt(httpContext)).stream().anyMatch(h -> ThreadLocalServerTLSHandler.class.isInstance(h)));
        
        Assert.assertFalse(ThreadLocalX509CredentialContext.haveCurrent());
        Assert.assertFalse(HttpClientSupport.getDynamicContextHandlerList(
                HttpClientContext.adapt(httpContext)).stream().anyMatch(h -> ThreadLocalClientTLSCredentialHandler.class.isInstance(h)));
    }
    
    @Test
    public void testEngineParamWithDefaultCriteria() throws IOException {
        X509Credential cred = getCredential("foo-1A1-good.crt");
        ExplicitKeyTrustEngine trustEngine = new ExplicitKeyTrustEngine(new StaticCredentialResolver(cred));
        httpContext.setAttribute(HttpClientSecurityConstants.CONTEXT_KEY_TRUST_ENGINE, trustEngine);

        securityEnhancedSocketFactory = new SecurityEnhancedTLSSocketFactory(buildInnerSSLFactory(
                CollectionSupport.singletonList((Certificate)cred.getEntityCertificate()), hostname));
        Socket socket = securityEnhancedSocketFactory.createSocket(httpContext);

        securityEnhancedSocketFactory.connectSocket(TimeValue.ofMilliseconds(0), socket, new HttpHost("https", hostname, 443), null, null, httpContext);
       
        Assert.assertTrue(ThreadLocalX509TrustEngineContext.haveCurrent());
        Assert.assertSame(ThreadLocalX509TrustEngineContext.getTrustEngine(), trustEngine);
        Assert.assertNotNull(ThreadLocalX509TrustEngineContext.getCriteria());
        Assert.assertTrue(ThreadLocalX509TrustEngineContext.isFailureFatal());
        Assert.assertTrue(HttpClientSupport.getDynamicContextHandlerList(
                HttpClientContext.adapt(httpContext)).stream().anyMatch(h -> ThreadLocalServerTLSHandler.class.isInstance(h)));
        
        Assert.assertFalse(ThreadLocalX509CredentialContext.haveCurrent());
        Assert.assertFalse(HttpClientSupport.getDynamicContextHandlerList(
                HttpClientContext.adapt(httpContext)).stream().anyMatch(h -> ThreadLocalClientTLSCredentialHandler.class.isInstance(h)));
    }
    
    @Test
    public void testEngineParamWithExplicitCriteria() throws IOException {
        X509Credential cred = getCredential("foo-1A1-good.crt");
        ExplicitKeyTrustEngine trustEngine = new ExplicitKeyTrustEngine(new StaticCredentialResolver(cred));
        httpContext.setAttribute(HttpClientSecurityConstants.CONTEXT_KEY_TRUST_ENGINE, trustEngine);

        CriteriaSet criteria = new CriteriaSet();
        httpContext.setAttribute(HttpClientSecurityConstants.CONTEXT_KEY_CRITERIA_SET, criteria);

        securityEnhancedSocketFactory = new SecurityEnhancedTLSSocketFactory(buildInnerSSLFactory(
                CollectionSupport.singletonList((Certificate)cred.getEntityCertificate()), hostname));
        Socket socket = securityEnhancedSocketFactory.createSocket(httpContext);

        securityEnhancedSocketFactory.connectSocket(TimeValue.ofMilliseconds(0), socket, new HttpHost("https", hostname, 443), null, null, httpContext);
       
        Assert.assertTrue(ThreadLocalX509TrustEngineContext.haveCurrent());
        Assert.assertSame(ThreadLocalX509TrustEngineContext.getTrustEngine(), trustEngine);
        Assert.assertSame(ThreadLocalX509TrustEngineContext.getCriteria(), criteria);
        Assert.assertTrue(ThreadLocalX509TrustEngineContext.isFailureFatal());
        Assert.assertTrue(HttpClientSupport.getDynamicContextHandlerList(
                HttpClientContext.adapt(httpContext)).stream().anyMatch(h -> ThreadLocalServerTLSHandler.class.isInstance(h)));
        
        Assert.assertFalse(ThreadLocalX509CredentialContext.haveCurrent());
        Assert.assertFalse(HttpClientSupport.getDynamicContextHandlerList(
                HttpClientContext.adapt(httpContext)).stream().anyMatch(h -> ThreadLocalClientTLSCredentialHandler.class.isInstance(h)));
    }
    
    @Test
    public void testEngineParamWithFailureNonFatal() throws IOException {
        X509Credential cred = getCredential("foo-1A1-good.crt");
        ExplicitKeyTrustEngine trustEngine = new ExplicitKeyTrustEngine(new StaticCredentialResolver(cred));
        httpContext.setAttribute(HttpClientSecurityConstants.CONTEXT_KEY_TRUST_ENGINE, trustEngine);
        httpContext.setAttribute(HttpClientSecurityConstants.CONTEXT_KEY_SERVER_TLS_FAILURE_IS_FATAL, Boolean.FALSE);

        CriteriaSet criteria = new CriteriaSet();
        httpContext.setAttribute(HttpClientSecurityConstants.CONTEXT_KEY_CRITERIA_SET, criteria);

        securityEnhancedSocketFactory = new SecurityEnhancedTLSSocketFactory(buildInnerSSLFactory(
                CollectionSupport.singletonList((Certificate)cred.getEntityCertificate()), hostname));
        Socket socket = securityEnhancedSocketFactory.createSocket(httpContext);

        securityEnhancedSocketFactory.connectSocket(TimeValue.ofMilliseconds(0), socket, new HttpHost("https", hostname, 443), null, null, httpContext);
       
        Assert.assertTrue(ThreadLocalX509TrustEngineContext.haveCurrent());
        Assert.assertSame(ThreadLocalX509TrustEngineContext.getTrustEngine(), trustEngine);
        Assert.assertNotNull(ThreadLocalX509TrustEngineContext.getCriteria());
        Assert.assertFalse(ThreadLocalX509TrustEngineContext.isFailureFatal());
        Assert.assertTrue(HttpClientSupport.getDynamicContextHandlerList(
                HttpClientContext.adapt(httpContext)).stream().anyMatch(h -> ThreadLocalServerTLSHandler.class.isInstance(h)));
        
        Assert.assertFalse(ThreadLocalX509CredentialContext.haveCurrent());
        Assert.assertFalse(HttpClientSupport.getDynamicContextHandlerList(
                HttpClientContext.adapt(httpContext)).stream().anyMatch(h -> ThreadLocalClientTLSCredentialHandler.class.isInstance(h)));
    }
    
    @Test
    public void testClientTLSParam() throws IOException {
        X509Credential cred = getCredential("foo-1A1-good.crt");
        httpContext.setAttribute(HttpClientSecurityConstants.CONTEXT_KEY_CLIENT_TLS_CREDENTIAL, cred);
        
        securityEnhancedSocketFactory = new SecurityEnhancedTLSSocketFactory(buildInnerSSLFactory(null, hostname));
        Socket socket = securityEnhancedSocketFactory.createSocket(httpContext);

        securityEnhancedSocketFactory.connectSocket(TimeValue.ofMilliseconds(0), socket, new HttpHost("https", hostname, 443), null, null, httpContext);
        
        Assert.assertFalse(ThreadLocalX509TrustEngineContext.haveCurrent());
        Assert.assertFalse(HttpClientSupport.getDynamicContextHandlerList(
                HttpClientContext.adapt(httpContext)).stream().anyMatch(h -> ThreadLocalServerTLSHandler.class.isInstance(h)));
        
        Assert.assertTrue(ThreadLocalX509CredentialContext.haveCurrent());
        Assert.assertSame(ThreadLocalX509CredentialContext.getCredential(), cred);
        Assert.assertTrue(HttpClientSupport.getDynamicContextHandlerList(
                HttpClientContext.adapt(httpContext)).stream().anyMatch(h -> ThreadLocalClientTLSCredentialHandler.class.isInstance(h)));
    }
    
    // Helper methods
    
    private LayeredConnectionSocketFactory buildInnerSSLFactory(List<Certificate> certs, String host) {
        if (certs == null) {
            return new MockTLSSocketFactory();
        }
        return new MockTLSSocketFactory(certs, host);
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
    
    private InputStream getInputStream(String fileName) {
        return  this.getClass().getResourceAsStream(DATA_PATH + fileName);
    }

}
