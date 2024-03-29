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

package org.opensaml.security.httpclient.impl;

import java.io.File;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.X509Support;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.resolver.CriteriaSet;

@SuppressWarnings("javadoc")
public class BasicHttpClientSecurityConfigurationTest {
    
    private X509Credential x509Credential;
    
    @BeforeMethod
    protected void setUp() throws CertificateException, URISyntaxException {
        x509Credential = CredentialSupport.getSimpleCredential(
                X509Support.decodeCertificate(new File(this.getClass().getResource("/data/certificate.pem").toURI())), null);
    }
    
    @Test
    public void testBasic() {
        BasicHttpClientSecurityConfiguration config = new BasicHttpClientSecurityConfiguration();
        config.setClientTLSCredential(x509Credential);
        config.setCredentialsProvider(new BasicCredentialsProvider());
        config.setHostnameVerifier(new DefaultHostnameVerifier());
        config.setTLSCipherSuites(CollectionSupport.singletonList("test"));
        config.setTLSProtocols(CollectionSupport.singletonList("test"));
        config.setTLSTrustEngine(new MockTrustEngine());
        
        Assert.assertNotNull(config.getClientTLSCredential());
        Assert.assertNotNull(config.getCredentialsProvider());
        Assert.assertNotNull(config.getHostnameVerifier());
        Assert.assertNotNull(config.getTLSCipherSuites());
        Assert.assertNotNull(config.getTLSProtocols());
        Assert.assertNotNull(config.getTLSTrustEngine());
    }
    
    @Test
    public void testEmptyLists() {
        BasicHttpClientSecurityConfiguration config = new BasicHttpClientSecurityConfiguration();
        config.setTLSCipherSuites(CollectionSupport.emptyList());
        config.setTLSProtocols(CollectionSupport.emptyList());
        
        Assert.assertNull(config.getTLSCipherSuites());
        Assert.assertNull(config.getTLSProtocols());
    }
    
    @Test
    public void testCredentialsProvider() {
        BasicHttpClientSecurityConfiguration config = new BasicHttpClientSecurityConfiguration();
        config.setBasicCredentials(new UsernamePasswordCredentials("test", "test".toCharArray()));
        
        Assert.assertNotNull(config.getCredentialsProvider());
    }
    
    
    // Helpers
    
    public static class MockTrustEngine implements TrustEngine<X509Credential>  {
        public boolean validate(@Nonnull final X509Credential token, @Nullable final CriteriaSet trustBasisCriteria)
                throws SecurityException {
            return false;
        }
    }

}
