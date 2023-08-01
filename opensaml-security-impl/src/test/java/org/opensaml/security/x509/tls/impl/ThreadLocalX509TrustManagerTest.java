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

package org.opensaml.security.x509.tls.impl;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;

import org.opensaml.security.SecurityException;
import org.opensaml.security.testing.MockTrustEngine;
import org.opensaml.security.x509.X509Support;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.resolver.CriteriaSet;

@SuppressWarnings("javadoc")
public class ThreadLocalX509TrustManagerTest {
    
    private ThreadLocalX509TrustManager trustManager = new ThreadLocalX509TrustManager();
    
    private X509Certificate[] chain;
    
    private CriteriaSet criteria;
    
    @BeforeClass
    public void beforeClass() throws CertificateException, IOException {
        try (final InputStream is = this.getClass().getResourceAsStream("/data/certificate.pem")) {
            final Collection<X509Certificate> certs = X509Support.decodeCertificates(is);
            chain = certs.stream().toArray(X509Certificate[]::new);
        }
    }
    
    @BeforeMethod
    public void beforeMethod() {
       ThreadLocalX509TrustEngineContext.clearCurrent(); 
       criteria = new CriteriaSet();
    }
     
    @AfterMethod
    public void afterMethod() {
       ThreadLocalX509TrustEngineContext.clearCurrent(); 
    }
    
    @Test
    public void trusted() throws CertificateException {
        ThreadLocalX509TrustEngineContext.loadCurrent(new MockTrustEngine<>(true), criteria, true);
        
        trustManager.checkServerTrusted(chain, "RSA");
        
        final Boolean trusted = ThreadLocalX509TrustEngineContext.getTrusted();
        assert trusted != null;
        Assert.assertTrue(trusted);
    }

    @Test
    public void notTrusted() throws CertificateException {
        ThreadLocalX509TrustEngineContext.loadCurrent(new MockTrustEngine<>(false), criteria, true);
        
        try {
            trustManager.checkServerTrusted(chain, "RSA");
            Assert.fail("Trust manager should have thrown");
        } catch (final CertificateException e) {
            final Boolean trusted = ThreadLocalX509TrustEngineContext.getTrusted();
            assert trusted != null;
            Assert.assertFalse(trusted);
        }
    }

    @Test
    public void notTrustedNotFatal() throws CertificateException {
        ThreadLocalX509TrustEngineContext.loadCurrent(new MockTrustEngine<>(false), criteria, false);
        
        trustManager.checkServerTrusted(chain, "RSA");
        final Boolean trusted = ThreadLocalX509TrustEngineContext.getTrusted();
        assert trusted != null;
        Assert.assertFalse(trusted);
    }

    @Test
    public void trustEngineThrowsSecurityException() throws CertificateException {
        ThreadLocalX509TrustEngineContext.loadCurrent(new MockTrustEngine<>(new SecurityException()), criteria, true);
        
        try {
            trustManager.checkServerTrusted(chain, "RSA");
            Assert.fail("Trust manager should have thrown");
        } catch (final CertificateException e) {
            final Boolean trusted = ThreadLocalX509TrustEngineContext.getTrusted();
            assert trusted != null;
            Assert.assertFalse(trusted);
        }
    }

    @Test
    public void trustEngineThrowsSecurityExceptionNotFatal() throws CertificateException {
        ThreadLocalX509TrustEngineContext.loadCurrent(new MockTrustEngine<>(new SecurityException()), criteria, false);
        
        trustManager.checkServerTrusted(chain, "RSA");
        final Boolean trusted = ThreadLocalX509TrustEngineContext.getTrusted();
        assert trusted != null;
        Assert.assertFalse(trusted);
    }

    @Test
    public void trustEngineThrowsRuntimeException() throws CertificateException {
        ThreadLocalX509TrustEngineContext.loadCurrent(new MockTrustEngine<>(new RuntimeException()), criteria, true);
        
        try {
            trustManager.checkServerTrusted(chain, "RSA");
            Assert.fail("Trust manager should have thrown");
        } catch (final CertificateException e) {
            final Boolean trusted = ThreadLocalX509TrustEngineContext.getTrusted();
            assert trusted != null;
            Assert.assertFalse(trusted);
        }
        
    }

    @Test
    public void trustEngineThrowsRuntimeExceptionNotFatal() throws CertificateException {
        ThreadLocalX509TrustEngineContext.loadCurrent(new MockTrustEngine<>(new RuntimeException()), criteria, false);
        
        trustManager.checkServerTrusted(chain, "RSA");
        final Boolean trusted = ThreadLocalX509TrustEngineContext.getTrusted();
        assert trusted != null;
        Assert.assertFalse(trusted);
        
    }

    @Test
    public void threadLocalNotLoaded() throws CertificateException {
        
        try {
            trustManager.checkServerTrusted(chain, "RSA");
            Assert.fail("Trust manager should have thrown");
        } catch (CertificateException e) {
            Assert.assertNull(ThreadLocalX509TrustEngineContext.getTrusted());
        }
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class) 
    public void nullChain() throws CertificateException {
        ThreadLocalX509TrustEngineContext.loadCurrent(new MockTrustEngine<>(true), criteria, true);
        trustManager.checkServerTrusted(null, "RSA");
    }

    @Test(expectedExceptions=IllegalArgumentException.class) 
    public void emptyChain() throws CertificateException {
        ThreadLocalX509TrustEngineContext.loadCurrent(new MockTrustEngine<>(true), criteria, true);
        trustManager.checkServerTrusted(new X509Certificate[] {}, "RSA");
    }

    @Test(expectedExceptions=IllegalArgumentException.class) 
    public void nullAuthType() throws CertificateException {
        ThreadLocalX509TrustEngineContext.loadCurrent(new MockTrustEngine<>(true), criteria, true);
        trustManager.checkServerTrusted(chain, null);
    }

    @Test(expectedExceptions=IllegalArgumentException.class) 
    public void emptyAuthType() throws CertificateException {
        ThreadLocalX509TrustEngineContext.loadCurrent(new MockTrustEngine<>(true), criteria, true);
        trustManager.checkServerTrusted(chain, "");
    }

}
