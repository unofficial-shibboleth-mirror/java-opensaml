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

package org.opensaml.saml.metadata.resolver.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Set;

import org.apache.hc.client5.http.socket.LayeredConnectionSocketFactory;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.credential.impl.StaticCredentialResolver;
import org.opensaml.security.httpclient.HttpClientSecurityParameters;
import org.opensaml.security.httpclient.impl.SecurityEnhancedHttpClientSupport;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.trust.impl.ExplicitKeyTrustEngine;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.PKIXValidationInformation;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.X509Support;
import org.opensaml.security.x509.impl.BasicPKIXValidationInformation;
import org.opensaml.security.x509.impl.BasicX509CredentialNameEvaluator;
import org.opensaml.security.x509.impl.CertPathPKIXTrustEvaluator;
import org.opensaml.security.x509.impl.PKIXX509CredentialTrustEngine;
import org.opensaml.security.x509.impl.StaticPKIXValidationInformationResolver;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.io.ByteStreams;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.httpclient.HttpClientBuilder;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;
import net.shibboleth.shared.testing.RepositorySupport;

/**
 * Unit tests for {@link HTTPMetadataResolver}.
 */
@SuppressWarnings("javadoc")
public class HTTPMetadataResolverTest extends XMLObjectBaseTestCase {
    
    private HttpClientBuilder httpClientBuilder;

    private String metadataURLHttp;
    private String metadataURLHttps;
    private String badMDURL;
    private String entityID;
    private HTTPMetadataResolver metadataProvider;
    private CriteriaSet criteriaSet;

    static final String DATA_PATH = "/org/opensaml/saml/metadata/resolver/impl/";
    
    @BeforeClass
    protected void setUpClass() {
        metadataURLHttps = RepositorySupport.buildHTTPSResourceURL("java-opensaml", "opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/08ced64cddc9f1578598b2cf71ae747b11d11472.xml");
        metadataURLHttp = RepositorySupport.buildHTTPResourceURL("java-opensaml", "opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/08ced64cddc9f1578598b2cf71ae747b11d11472.xml", false);
    }

    @BeforeMethod
    protected void setUpMethod() throws Exception {
        httpClientBuilder = new HttpClientBuilder();
        
        badMDURL = "http://www.google.com/";
        entityID = "https://www.example.org/sp";
        
        criteriaSet = new CriteriaSet(new EntityIdCriterion(entityID));
    }
    
    /**
     * Tests failed condition.
     * 
     * @throws Exception 
     */
    @Test
    public void testInactive() throws Exception {
        try {
            metadataProvider = new HTTPMetadataResolver(httpClientBuilder.buildClient(), metadataURLHttp);
            metadataProvider.setParserPool(parserPool);
            metadataProvider.setId("test");
            metadataProvider.setActivationCondition(PredicateSupport.alwaysFalse());
            metadataProvider.initialize();
            
            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertTrue(flag != null && flag);
            Assert.assertNull(metadataProvider.getLastFailureCause());
        } catch (ComponentInitializationException e) {
            Assert.fail("Valid metadata failed init");
        }
        
        EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
        Assert.assertNull(descriptor, "Retrieved entity descriptor was not null");
    }
    
    /**
     * Tests the {@link HTTPMetadataResolver#lookupEntityID(String)} method.
     * @throws Exception 
     */
    @Test
    public void testGetEntityDescriptor() throws Exception {
        try {
            metadataProvider = new HTTPMetadataResolver(httpClientBuilder.buildClient(), metadataURLHttp);
            metadataProvider.setParserPool(parserPool);
            metadataProvider.setId("test");
            metadataProvider.initialize();
            
            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertTrue(flag != null && flag);
            Assert.assertNull(metadataProvider.getLastFailureCause());
        } catch (ComponentInitializationException e) {
            Assert.fail("Valid metadata failed init");
        }
        
        EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
        assert descriptor != null;
        Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");
    }
    
    /**
     * Test fail-fast = true with known bad metadata URL.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testFailFastBadURL() throws Exception {
        metadataProvider = new HTTPMetadataResolver(httpClientBuilder.buildClient(), badMDURL);
        
        metadataProvider.setFailFastInitialization(true);
        metadataProvider.setId("test");
        metadataProvider.setParserPool(parserPool);
        
        try {
            metadataProvider.initialize();
            Assert.fail("metadata provider claims to have parsed known invalid data");
        } catch (final ComponentInitializationException e) {
            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertTrue(flag == null || !flag);
            Assert.assertNotNull(metadataProvider.getLastFailureCause());
            Assert.assertTrue(ResolverException.class.isInstance(metadataProvider.getLastFailureCause()));
        }
    }
    
    /**
     * Test fail-fast = false with known bad metadata URL.
     * 
     * @throws Exception if something goes wrong
     */
    @Test
    public void testNoFailFastBadURL() throws Exception {
        metadataProvider = new HTTPMetadataResolver(httpClientBuilder.buildClient(), badMDURL);
        
        metadataProvider.setFailFastInitialization(false);
        metadataProvider.setId("test");
        metadataProvider.setParserPool(parserPool);
        
        try {
            metadataProvider.initialize();

            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertFalse(flag != null && flag);
            Assert.assertNotNull(metadataProvider.getLastFailureCause());
            Assert.assertTrue(ResolverException.class.isInstance(metadataProvider.getLastFailureCause()));
        } catch (ComponentInitializationException e) {
            Assert.fail("Provider failed init with fail-fast=false");
        }
        
        final EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
        Assert.assertNull(descriptor);
    }
    
    @Test
    public void testTrustEngineSocketFactoryNoHTTPSNoTrustEngine() throws Exception  {
        // Make sure resolver works when TrustEngine socket factory is configured but just using an HTTP URL.
        httpClientBuilder.setTLSSocketFactory(buildSocketFactory(true));
        
        metadataProvider = new HTTPMetadataResolver(httpClientBuilder.buildClient(), metadataURLHttp);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("test");
        metadataProvider.initialize();
        
        final Boolean flag = metadataProvider.wasLastRefreshSuccess();
        Assert.assertTrue(flag != null && flag);
        Assert.assertNull(metadataProvider.getLastFailureCause());
        
        final EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
        assert descriptor != null;
        Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");
    }
    
    @Test
    public void testTrustEngineSocketFactoryNoHTTPSWithTrustEngine() throws Exception  {
        // Make sure resolver works when TrustEngine socket factory is configured but just using an HTTP URL.
        httpClientBuilder.setTLSSocketFactory(buildSocketFactory());
        
        metadataProvider = new HTTPMetadataResolver(httpClientBuilder.buildClient(), metadataURLHttps);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("test");

        final HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setTLSTrustEngine(HTTPMetadataResolverTest.buildExplicitKeyTrustEngine("repo-entity.crt"));
        metadataProvider.setHttpClientSecurityParameters(params);
        metadataProvider.initialize();
        
        final Boolean flag = metadataProvider.wasLastRefreshSuccess();
        Assert.assertTrue(flag != null && flag);
        Assert.assertNull(metadataProvider.getLastFailureCause());
        
        final EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
        assert descriptor != null;
        Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");
    }
    
    @Test
    public void testHTTPSNoTrustEngine() throws Exception  {
        try {
            System.setProperty("javax.net.ssl.trustStore", getClass().getResource("repo.truststore.jks").getFile());
            System.setProperty("javax.net.ssl.trustStorePassword", "shibboleth");

            httpClientBuilder.setTLSSocketFactory(buildSocketFactory(false));

            metadataProvider = new HTTPMetadataResolver(httpClientBuilder.buildClient(), metadataURLHttps);
            metadataProvider.setParserPool(parserPool);
            metadataProvider.setId("test");
            metadataProvider.initialize();
            
            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertTrue(flag != null && flag);
            Assert.assertNull(metadataProvider.getLastFailureCause());

            final EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
            assert descriptor != null;
            Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");
        } finally {
            System.setProperty("javax.net.ssl.trustStore", "");
            System.setProperty("javax.net.ssl.trustStorePassword", "");        
        }
    }
    
    @Test
    public void testHTTPSTrustEngineExplicitKey() throws Exception  {
        httpClientBuilder.setTLSSocketFactory(buildSocketFactory());
        
        metadataProvider = new HTTPMetadataResolver(httpClientBuilder.buildClient(), metadataURLHttps);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("test");

        final HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setTLSTrustEngine(HTTPMetadataResolverTest.buildExplicitKeyTrustEngine("repo-entity.crt"));
        metadataProvider.setHttpClientSecurityParameters(params);

        metadataProvider.initialize();
        
        final Boolean flag = metadataProvider.wasLastRefreshSuccess();
        Assert.assertTrue(flag != null && flag);
        Assert.assertNull(metadataProvider.getLastFailureCause());
        
        final EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
        assert descriptor != null;
        Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");
    }
    

    @Test
    public void testHTTPSTrustEngineInvalidKey() throws Exception  {
        httpClientBuilder.setTLSSocketFactory(buildSocketFactory());
        
        metadataProvider = new HTTPMetadataResolver(httpClientBuilder.buildClient(), metadataURLHttps);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("test");

        final HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setTLSTrustEngine(HTTPMetadataResolverTest.buildExplicitKeyTrustEngine("badKey.crt"));
        metadataProvider.setHttpClientSecurityParameters(params);

        try {
            metadataProvider.initialize();
            Assert.fail("Invalid metadata TLS should have failed init");
        } catch (final ComponentInitializationException e) {
            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertFalse(flag != null && flag);
            Assert.assertNotNull(metadataProvider.getLastFailureCause());
            Assert.assertTrue(ResolverException.class.isInstance(metadataProvider.getLastFailureCause()));
        }
    }
    
    @Test
    public void testHTTPSTrustEngineValidPKIX() throws Exception  {
        httpClientBuilder.setTLSSocketFactory(buildSocketFactory());
        
        metadataProvider = new HTTPMetadataResolver(httpClientBuilder.buildClient(), metadataURLHttps);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("test");

        final HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setTLSTrustEngine(HTTPMetadataResolverTest.buildPKIXTrustEngine("repo-rootCA.crt", null, false));
        metadataProvider.setHttpClientSecurityParameters(params);

        metadataProvider.initialize();
        
        final Boolean flag = metadataProvider.wasLastRefreshSuccess();
        Assert.assertTrue(flag != null && flag);
        Assert.assertNull(metadataProvider.getLastFailureCause());
        
        final EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
        assert descriptor != null;
        Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");
    }
    
    @Test
    public void testHTTPSTrustEngineValidPKIXExplicitName() throws Exception  {
        httpClientBuilder.setTLSSocketFactory(buildSocketFactory());
        
        metadataProvider = new HTTPMetadataResolver(httpClientBuilder.buildClient(), metadataURLHttps);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("test");

        final HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setTLSTrustEngine(HTTPMetadataResolverTest.buildPKIXTrustEngine("repo-rootCA.crt", "test.shibboleth.net", true));
        metadataProvider.setHttpClientSecurityParameters(params);

        metadataProvider.initialize();
        
        final Boolean flag = metadataProvider.wasLastRefreshSuccess();
        Assert.assertTrue(flag != null && flag);
        Assert.assertNull(metadataProvider.getLastFailureCause());
        
        final EntityDescriptor descriptor = metadataProvider.resolveSingle(criteriaSet);
        assert descriptor != null;
        Assert.assertEquals(descriptor.getEntityID(), entityID, "Entity's ID does not match requested ID");
    }
    
    @Test
    public void testHTTPSTrustEngineInvalidPKIX() throws Exception  {
        httpClientBuilder.setTLSSocketFactory(buildSocketFactory());
        
        metadataProvider = new HTTPMetadataResolver(httpClientBuilder.buildClient(), metadataURLHttps);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("test");

        final HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setTLSTrustEngine(HTTPMetadataResolverTest.buildPKIXTrustEngine("badCA.crt", null, false));
        metadataProvider.setHttpClientSecurityParameters(params);

        try {
            metadataProvider.initialize();
            Assert.fail("Invalid metadata TLS should have failed init");
        } catch (ComponentInitializationException e) {
            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertFalse(flag != null && flag);
            Assert.assertNotNull(metadataProvider.getLastFailureCause());
            Assert.assertTrue(ResolverException.class.isInstance(metadataProvider.getLastFailureCause()));
        }
    }
    
    @Test
    public void testHTTPSTrustEngineValidPKIXInvalidName() throws Exception  {
        httpClientBuilder.setTLSSocketFactory(buildSocketFactory());
        
        metadataProvider = new HTTPMetadataResolver(httpClientBuilder.buildClient(), metadataURLHttps);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("test");

        final HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setTLSTrustEngine(HTTPMetadataResolverTest.buildPKIXTrustEngine("repo-rootCA.crt", "foobar.shibboleth.net", true));
        metadataProvider.setHttpClientSecurityParameters(params);

        try {
            metadataProvider.initialize();
            Assert.fail("Invalid metadata TLS should have failed init");
        } catch (final ComponentInitializationException e) {
            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertFalse(flag != null && flag);
            Assert.assertNotNull(metadataProvider.getLastFailureCause());
            Assert.assertTrue(ResolverException.class.isInstance(metadataProvider.getLastFailureCause()));
        }
    }
    
    @Test
    public void testHTTPSTrustEngineWrongSocketFactory() throws Exception  {
        // Trust engine set, but appropriate socket factory not set
        metadataProvider = new HTTPMetadataResolver(httpClientBuilder.buildClient(), metadataURLHttps);
        metadataProvider.setParserPool(parserPool);
        metadataProvider.setId("test");

        final HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setTLSTrustEngine(HTTPMetadataResolverTest.buildExplicitKeyTrustEngine("repo-entity.crt"));
        metadataProvider.setHttpClientSecurityParameters(params);

        try {
            metadataProvider.initialize();
            Assert.fail("Invalid metadata TLS should have failed init");
        } catch (final ComponentInitializationException e) {
            final Boolean flag = metadataProvider.wasLastRefreshSuccess();
            Assert.assertFalse(flag != null && flag);
            Assert.assertNotNull(metadataProvider.getLastFailureCause());
            Assert.assertTrue(ResolverException.class.isInstance(metadataProvider.getLastFailureCause()));
        }
    }
    
    // Helpers
    
    public static TrustEngine<? super X509Credential> buildPKIXTrustEngine(String cert, String name, boolean nameCheckEnabled) throws URISyntaxException, CertificateException, IOException {
        final InputStream certStream = FileBackedHTTPMetadataResolver.class.getResourceAsStream((HTTPMetadataResolverTest.DATA_PATH + cert));
        final X509Certificate rootCert = X509Support.decodeCertificate(ByteStreams.toByteArray(certStream));
        final PKIXValidationInformation info = new BasicPKIXValidationInformation(CollectionSupport.singletonList(rootCert), null, 5);
        final Set<String> trustedNames = name != null ? CollectionSupport.singleton(name) : CollectionSupport.emptySet();
        final StaticPKIXValidationInformationResolver resolver = new StaticPKIXValidationInformationResolver(CollectionSupport.singletonList(info), trustedNames);
        return new PKIXX509CredentialTrustEngine(resolver,
                new CertPathPKIXTrustEvaluator(),
                (nameCheckEnabled ? new BasicX509CredentialNameEvaluator() : null));
    }

    public static TrustEngine<? super X509Credential> buildExplicitKeyTrustEngine(String cert) throws URISyntaxException, CertificateException, IOException {
        
        final InputStream certStream = FileBackedHTTPMetadataResolver.class.getResourceAsStream(HTTPMetadataResolverTest.DATA_PATH + cert);
        final X509Certificate entityCert = X509Support.decodeCertificate(ByteStreams.toByteArray(certStream));
        final X509Credential entityCredential = new BasicX509Credential(entityCert);
        return new ExplicitKeyTrustEngine(new StaticCredentialResolver(entityCredential));
        
    }

    public static LayeredConnectionSocketFactory buildSocketFactory() {
        return buildSocketFactory(true);
    }
    
    public static LayeredConnectionSocketFactory buildSocketFactory(boolean supportTrustEngine) {
        return SecurityEnhancedHttpClientSupport.buildTLSSocketFactory(supportTrustEngine, false);
    }

}
