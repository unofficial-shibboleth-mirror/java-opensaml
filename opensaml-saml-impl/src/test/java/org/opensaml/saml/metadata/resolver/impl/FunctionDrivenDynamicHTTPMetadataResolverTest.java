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

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.testing.XMLObjectBaseTestCase;
import org.opensaml.saml.common.binding.artifact.SAMLSourceIDArtifact;
import org.opensaml.saml.criterion.ArtifactCriterion;
import org.opensaml.saml.metadata.resolver.impl.TemplateRequestURLBuilder.EncodingStyle;
import org.opensaml.saml.saml2.binding.artifact.SAML2ArtifactType0004;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.security.httpclient.HttpClientSecurityParameters;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;

import net.shibboleth.shared.codec.StringDigester;
import net.shibboleth.shared.codec.StringDigester.OutputFormat;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.httpclient.HttpClientBuilder;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.testing.RepositorySupport;
import net.shibboleth.shared.testing.VelocityEngine;

@SuppressWarnings("javadoc")
public class FunctionDrivenDynamicHTTPMetadataResolverTest extends XMLObjectBaseTestCase {
    
    private FunctionDrivenDynamicHTTPMetadataResolver resolver;
    
    private HttpClientBuilder httpClientBuilder;
    
    @BeforeMethod
    public void setUp() {
        httpClientBuilder = new HttpClientBuilder();
    }
    
    @AfterMethod
    public void tearDown() {
        if (resolver != null) {
            resolver.destroy();
        }
    }
    
    
    @Test
    public void testInactive() throws Exception {
        // Repo should return 'text/xml', which is supported by default.
        String template = RepositorySupport.buildHTTPResourceURL("java-opensaml", "opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml", false);
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                EncodingStyle.path, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.setActivationCondition(Predicates.alwaysFalse());
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(ed);
    }
    
    @Test
    public void testTemplateFromRepoDefaultContentTypes() throws Exception {
        // Repo should return 'text/xml', which is supported by default.
        String template = RepositorySupport.buildHTTPResourceURL("java-opensaml", "opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml", false);
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                EncodingStyle.path, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        final EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        assert ed != null;
        Assert.assertEquals(ed.getEntityID(), entityID);
        Assert.assertNull(ed.getDOM());
    }
   
    //TODO disabled b/c gitweb currently doesn't seem to have a way to request a different MIME type
    @Test(enabled=false)
    public void testTemplateFromRepoWithExplicitContentType() throws Exception {
        // Explicitly request 'text/plain', and then configure it below to be supported.  Also test case-insensitivity.
        String template = RepositorySupport.buildHTTPResourceURL("java-opensaml", "opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml", false);
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                EncodingStyle.path, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.setSupportedContentTypes(Arrays.asList("application/samlmetadata+xml", "application/xml", "text/xml", "TEXT/PLAIN"));
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        final EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        assert ed != null;
        Assert.assertEquals(ed.getEntityID(), entityID);
        Assert.assertNull(ed.getDOM());
    }
    
    //TODO disabled b/c gitweb currently doesn't seem to have a way to request a different MIME type
    @Test(enabled=false)
    public void testTemplateFromRepoUnsupportedContentType() throws Exception {
        // Repo should return 'text/plain', which is not supported by default.
        String template = RepositorySupport.buildHTTPSResourceURL("java-opensaml", "opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml");
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                EncodingStyle.path, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        final EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(ed);
    }
    
    @Test
    public void testTemplateNonexistentDomain() throws Exception {
        // Unresolveable domain.  Should silently fail.
        String template = "http://bogus.example.org/metadata?entityID=${entityID}";
        String entityID = "https://www.example.org/sp";
        
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                EncodingStyle.form);
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        final EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(ed);
    }
    
    @Test
    public void testTemplateNonexistentPath() throws Exception {
        // Bad path, resulting in 404.  Should silently fail.
        String template = "http://test.shibboleth.net/unittests/metadata?entityID=${entityID}";
        String entityID = "https://www.example.org/sp";
        
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                EncodingStyle.form);
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        final EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(ed);
    }
    
    @Test
    public void testWellKnownLocation() throws Exception {
        String entityID = "https://test.shibboleth.net/shibboleth";
        
        HTTPEntityIDRequestURLBuilder requestURLBuilder = new HTTPEntityIDRequestURLBuilder();
        
        httpClientBuilder.setTLSSocketFactory(HTTPMetadataResolverTest.buildSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        
        final HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setTLSTrustEngine(HTTPMetadataResolverTest.buildExplicitKeyTrustEngine("test.shibboleth.net.crt"));
        resolver.setHttpClientSecurityParameters(params);
        
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        final EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        assert ed != null;
        Assert.assertEquals(ed.getEntityID(), entityID);
        Assert.assertNull(ed.getDOM());
    }
    
    @Test
    public void testMDQ() throws Exception {
        String baseURL = "http://mdq.incommon.org";
        String entityID = "urn:mace:incommon:osu.edu";
        
        MetadataQueryProtocolRequestURLBuilder requestURLBuilder = new MetadataQueryProtocolRequestURLBuilder(baseURL);
        
        httpClientBuilder.setConnectionDisregardTLSCertificate(true);
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        final EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        assert ed != null;
        Assert.assertEquals(ed.getEntityID(), entityID);
        Assert.assertNull(ed.getDOM());
    }
    
    @Test
    public void testMDQViaArtifact() throws Exception {
        String baseURL = "http://mdq.incommon.org";
        String entityID = "urn:mace:incommon:osu.edu";
        
        MetadataQueryProtocolRequestURLBuilder requestURLBuilder = new MetadataQueryProtocolRequestURLBuilder(baseURL,
                CollectionSupport.singletonList(new SAMLArtifactURLBuilder()));
        
        httpClientBuilder.setConnectionDisregardTLSCertificate(true);
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.initialize();
        
        MessageDigest sha1Digester = MessageDigest.getInstance(JCAConstants.DIGEST_SHA1);
        byte[] entityIDSourceID = sha1Digester.digest(entityID.getBytes("UTF-8"));
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] messageHandle = new byte[20];
        secureRandom.nextBytes(messageHandle);
        SAMLSourceIDArtifact sourceIDArtifact = new SAML2ArtifactType0004(new byte[] {0, 0} , entityIDSourceID, messageHandle);
        
        final CriteriaSet criteriaSet = new CriteriaSet( new ArtifactCriterion(sourceIDArtifact));
        
        final EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        assert ed != null;
        Assert.assertEquals(ed.getEntityID(), entityID);
        Assert.assertNull(ed.getDOM());
    }
    
    @Test
    public void testTrustEngineSocketFactoryNoHTTPSNoTrustEngine() throws Exception {
        String template = RepositorySupport.buildHTTPResourceURL("java-opensaml", "opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml", false);
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                EncodingStyle.path, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        httpClientBuilder.setTLSSocketFactory(HTTPMetadataResolverTest.buildSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);
        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        final EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        assert ed != null;
        Assert.assertEquals(ed.getEntityID(), entityID);
        Assert.assertNull(ed.getDOM());
    }
    
    @Test
    public void testTrustEngineSocketFactoryNoHTTPSWithTrustEngine() throws Exception  {
        String template = RepositorySupport.buildHTTPResourceURL("java-opensaml", "opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml", false);
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                EncodingStyle.path, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        httpClientBuilder.setTLSSocketFactory(HTTPMetadataResolverTest.buildSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);

        final HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setTLSTrustEngine(HTTPMetadataResolverTest.buildExplicitKeyTrustEngine("repo-entity.crt"));
        resolver.setHttpClientSecurityParameters(params);

        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        final EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        assert ed != null;
        Assert.assertEquals(ed.getEntityID(), entityID);
        Assert.assertNull(ed.getDOM());
    }
    
    @Test
    public void testHTTPSNoTrustEngine() throws Exception  {
        try {
            System.setProperty("javax.net.ssl.trustStore", getClass().getResource("repo.truststore.jks").getFile());
            System.setProperty("javax.net.ssl.trustStorePassword", "shibboleth");
            
            String template = RepositorySupport.buildHTTPSResourceURL("java-opensaml", "opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml");
            String entityID = "https://www.example.org/sp";

            // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
            TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                    VelocityEngine.newVelocityEngine(), 
                    template, 
                    EncodingStyle.path, 
                    new StringDigester("SHA-1", OutputFormat.HEX_LOWER));

            httpClientBuilder.setTLSSocketFactory(HTTPMetadataResolverTest.buildSocketFactory(false));

            resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
            resolver.setId("myDynamicResolver");
            resolver.setParserPool(parserPool);
            resolver.setRequestURLBuilder(requestURLBuilder);
            resolver.initialize();

            CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));

            final EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
            assert ed != null;
            Assert.assertEquals(ed.getEntityID(), entityID);
            Assert.assertNull(ed.getDOM());
        } finally {
            System.setProperty("javax.net.ssl.trustStore", "");
            System.setProperty("javax.net.ssl.trustStorePassword", "");
        }
    }
    
    @Test
    public void testHTTPSTrustEngineExplicitKey() throws Exception  {
        String template = RepositorySupport.buildHTTPSResourceURL("java-opensaml", "opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml");
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                EncodingStyle.path, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        httpClientBuilder.setTLSSocketFactory(HTTPMetadataResolverTest.buildSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);

        final HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setTLSTrustEngine(HTTPMetadataResolverTest.buildExplicitKeyTrustEngine("repo-entity.crt"));
        resolver.setHttpClientSecurityParameters(params);

        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        final EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        assert ed != null;
        Assert.assertEquals(ed.getEntityID(), entityID);
        Assert.assertNull(ed.getDOM());
    }
    
    @Test
    public void testHTTPSTrustEngineInvalidKey()  throws Exception {
        String template = RepositorySupport.buildHTTPSResourceURL("java-opensaml", "opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml");
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                EncodingStyle.path, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        httpClientBuilder.setTLSSocketFactory(HTTPMetadataResolverTest.buildSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);

        final HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setTLSTrustEngine(HTTPMetadataResolverTest.buildExplicitKeyTrustEngine("badKey.crt"));
        resolver.setHttpClientSecurityParameters(params);

        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        final EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(ed);
    }
    
    @Test
    public void testHTTPSTrustEngineValidPKIX() throws Exception  {
        String template = RepositorySupport.buildHTTPSResourceURL("java-opensaml", "opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml");
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                EncodingStyle.path, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        httpClientBuilder.setTLSSocketFactory(HTTPMetadataResolverTest.buildSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);

        final HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setTLSTrustEngine(HTTPMetadataResolverTest.buildPKIXTrustEngine("repo-rootCA.crt", null, false));
        resolver.setHttpClientSecurityParameters(params);

        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        final EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        assert ed != null;
        Assert.assertEquals(ed.getEntityID(), entityID);
        Assert.assertNull(ed.getDOM());
    }
    
    @Test
    public void testHTTPSTrustEngineValidPKIXExplicitName() throws Exception  {
        String template = RepositorySupport.buildHTTPSResourceURL("java-opensaml", "opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml");
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                EncodingStyle.path, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        httpClientBuilder.setTLSSocketFactory(HTTPMetadataResolverTest.buildSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);

        final HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setTLSTrustEngine(HTTPMetadataResolverTest.buildPKIXTrustEngine("repo-rootCA.crt", "test.shibboleth.net", true));
        resolver.setHttpClientSecurityParameters(params);

        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        final EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        assert ed != null;
        Assert.assertEquals(ed.getEntityID(), entityID);
        Assert.assertNull(ed.getDOM());
    }
    
    @Test
    public void testHTTPSTrustEngineInvalidPKIX() throws Exception  {
        String template = RepositorySupport.buildHTTPSResourceURL("java-opensaml", "opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml");
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                EncodingStyle.path, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        httpClientBuilder.setTLSSocketFactory(HTTPMetadataResolverTest.buildSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);

        final HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setTLSTrustEngine(HTTPMetadataResolverTest.buildPKIXTrustEngine("badCA.crt", null, false));
        resolver.setHttpClientSecurityParameters(params);

        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(ed);
    }
    
    @Test
    public void testHTTPSTrustEngineValidPKIXInvalidName() throws Exception  {
        String template = RepositorySupport.buildHTTPSResourceURL("java-opensaml", "opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml");
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                EncodingStyle.path, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        httpClientBuilder.setTLSSocketFactory(HTTPMetadataResolverTest.buildSocketFactory());
        
        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);

        final HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setTLSTrustEngine(HTTPMetadataResolverTest.buildPKIXTrustEngine("repo-rootCA.crt", "foobar.shibboleth.net", true));
        resolver.setHttpClientSecurityParameters(params);

        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(ed);
    }
    
    @Test
    public void testHTTPSTrustEngineWrongSocketFactory() throws Exception  {
        String template = RepositorySupport.buildHTTPSResourceURL("java-opensaml", "opensaml-saml-impl/src/test/resources/org/opensaml/saml/metadata/resolver/impl/${entityID}.xml");
        String entityID = "https://www.example.org/sp";
        
        // Digesting the entityID is a little artificial for the test, but means we can test more easily against a path in the repo.
        TemplateRequestURLBuilder requestURLBuilder = new TemplateRequestURLBuilder(
                VelocityEngine.newVelocityEngine(), 
                template, 
                EncodingStyle.path, 
                new StringDigester("SHA-1", OutputFormat.HEX_LOWER));
        
        // Trust engine set, but appropriate socket factory not set

        resolver = new FunctionDrivenDynamicHTTPMetadataResolver(httpClientBuilder.buildClient());
        resolver.setId("myDynamicResolver");
        resolver.setParserPool(parserPool);
        resolver.setRequestURLBuilder(requestURLBuilder);

        final HttpClientSecurityParameters params = new HttpClientSecurityParameters();
        params.setTLSTrustEngine(HTTPMetadataResolverTest.buildExplicitKeyTrustEngine("repo-entity.crt"));
        resolver.setHttpClientSecurityParameters(params);

        resolver.initialize();
        
        CriteriaSet criteriaSet = new CriteriaSet( new EntityIdCriterion(entityID));
        
        EntityDescriptor ed = resolver.resolveSingle(criteriaSet);
        Assert.assertNull(ed);
    }
}
