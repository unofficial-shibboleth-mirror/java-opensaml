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

package org.opensaml.security.messaging.impl;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;

import org.opensaml.core.testing.OpenSAMLInitBaseTestCase;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialContextSet;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.httpclient.HttpClientSecurityConfigurationCriterion;
import org.opensaml.security.httpclient.HttpClientSecurityParameters;
import org.opensaml.security.httpclient.HttpClientSecurityParametersResolver;
import org.opensaml.security.messaging.HttpClientSecurityContext;
import org.opensaml.security.x509.X509Credential;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/** Unit test for {@link PopulateHttpClientSecurityParametersHandler}. */
@SuppressWarnings("javadoc")
public class PopulateHttpClientSecurityParametersHandlerTest extends OpenSAMLInitBaseTestCase {

    private MessageContext messageContext;
    
    private PopulateHttpClientSecurityParametersHandler handler;
    
    @BeforeMethod public void setUp() {
        messageContext = new MessageContext();
        handler = new PopulateHttpClientSecurityParametersHandler();
    }
    
    @Test(expectedExceptions=ComponentInitializationException.class)
    public void testConfig() throws ComponentInitializationException {
        handler.initialize();
    }
    
    @Test(expectedExceptions=MessageHandlerException.class)
    public void testResolverError() throws Exception {
        handler.setHttpClientSecurityParametersResolver(new MockResolver(true));
        handler.initialize();
        
        assert messageContext != null;
        handler.invoke(messageContext);
    }    

    @Test public void testSuccess() throws Exception {
        handler.setHttpClientSecurityParametersResolver(new MockResolver(false));
        handler.initialize();
        
        assert messageContext != null;
        handler.invoke(messageContext);
        
        final HttpClientSecurityParameters params = messageContext.ensureSubcontext(HttpClientSecurityContext.class).getSecurityParameters(); 
        assert params != null;
        Assert.assertNotNull(params.getClientTLSCredential());
    }    
    
    @Test public void testSuccessIncludeClientTLS() throws Exception {
        handler.setHttpClientSecurityParametersResolver(new MockResolver(false));
        handler.setClientTLSPredicate(PredicateSupport.alwaysTrue());
        handler.initialize();
        
        assert messageContext != null;
        handler.invoke(messageContext);
        
        final HttpClientSecurityParameters params = messageContext.ensureSubcontext(HttpClientSecurityContext.class).getSecurityParameters(); 
        assert params != null;
        Assert.assertNotNull(params.getClientTLSCredential());
    }    
    
    @Test public void testSuccessExcludeClientTLS() throws Exception {
        handler.setHttpClientSecurityParametersResolver(new MockResolver(false));
        handler.setClientTLSPredicate(PredicateSupport.alwaysFalse());
        handler.initialize();
        
        assert messageContext != null;
        handler.invoke(messageContext);
        
        final HttpClientSecurityParameters params = messageContext.ensureSubcontext(HttpClientSecurityContext.class).getSecurityParameters(); 
        assert params != null;
        Assert.assertNull(params.getClientTLSCredential());
    }    
    
    private class MockResolver implements HttpClientSecurityParametersResolver {

        private boolean throwException;
        
        public MockResolver(final boolean shouldThrow) {
            throwException = shouldThrow;
        }
        
        /** {@inheritDoc} */
        @Nonnull public Iterable<HttpClientSecurityParameters> resolve(@Nullable final CriteriaSet criteria) throws ResolverException {
            final HttpClientSecurityParameters params = resolveSingle(criteria);
            assert params != null;
            return CollectionSupport.singletonList(params);
        }

        /** {@inheritDoc} */
        @Nullable public HttpClientSecurityParameters resolveSingle(@Nullable final CriteriaSet criteria) throws ResolverException {
            if (throwException) {
                throw new ResolverException();
            }
            
            assert criteria != null;
            Constraint.isNotNull(criteria.get(HttpClientSecurityConfigurationCriterion.class), "Criterion was null");
            HttpClientSecurityParameters params = new HttpClientSecurityParameters();
            params.setClientTLSCredential(new MockX509Credential());
            return params;
        }
        
    }
    
    private class MockX509Credential implements X509Credential {

        /** {@inheritDoc} */
        public String getEntityId() {
            return null;
        }

        /** {@inheritDoc} */
        public UsageType getUsageType() {
            return null;
        }

        /** {@inheritDoc} */
        @Nonnull public Collection<String> getKeyNames() {
            return CollectionSupport.emptyList();
        }

        /** {@inheritDoc} */
        public PublicKey getPublicKey() {
            return null;
        }

        /** {@inheritDoc} */
        public PrivateKey getPrivateKey() {
            return null;
        }

        /** {@inheritDoc} */
        public SecretKey getSecretKey() {
            return null;
        }

        /** {@inheritDoc} */
        public CredentialContextSet getCredentialContextSet() {
            return null;
        }

        /** {@inheritDoc} */
        @Nonnull public Class<? extends Credential> getCredentialType() {
            return getClass();
        }

        /** {@inheritDoc} */
        @Nonnull public X509Certificate getEntityCertificate() {
            throw new IllegalStateException();
        }

        /** {@inheritDoc} */
        @Nonnull public Collection<X509Certificate> getEntityCertificateChain() {
            return CollectionSupport.emptyList();
        }

        /** {@inheritDoc} */
        public Collection<X509CRL> getCRLs() {
            return null;
        }
        
    }
    
}