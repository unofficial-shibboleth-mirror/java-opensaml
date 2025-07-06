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

import java.io.IOException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.httpclient.HttpClientBuilder;
import net.shibboleth.shared.spring.httpclient.resource.HTTPResource;
import net.shibboleth.shared.testing.RepositorySupport;
import net.shibboleth.shared.testing.ResourceTestHelper;

import org.apache.hc.client5.http.classic.HttpClient;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test for HTTPResource with security support added.
 */
@SuppressWarnings("javadoc")
public class SecurityEnhancedHTTPResourceTest {

    private final String path = "data/org/opensaml/security/httpclient/document.xml";
    private final String pathPrefix = "opensaml-security-api/src/test/resources/";
    private final String existsHttps = RepositorySupport.buildHTTPSResourceURL("java-opensaml", pathPrefix+ path);
    private final String existsHttp = RepositorySupport.buildHTTPResourceURL("java-opensaml", pathPrefix+ path, false);

    private HttpClient client;
    private HttpClientSecurityParameters params;
    private HttpClientSecurityContextHandler handler;

    @BeforeClass public void setupClient() throws Exception {
        client = (new HttpClientBuilder()).buildClient();
    }
    
    @BeforeMethod public void setUp() throws ComponentInitializationException {
        params = new HttpClientSecurityParameters();
        handler = new HttpClientSecurityContextHandler();
        handler.setHttpClientSecurityParameters(params);
        handler.initialize();
    }

    @Test public void testNoSecurityAdded() throws IOException, ComponentInitializationException {
        final HTTPResource existsResource = new HTTPResource(client, existsHttp);
        existsResource.setHttpClientContextHandler(handler);
        
        Assert.assertTrue(ResourceTestHelper.compare(existsResource, new ClassPathResource(path)));
    }

    @Test public void testHostnameRejected() throws IOException, ComponentInitializationException {
        final HTTPResource existsResource = new HTTPResource(client, existsHttps);
        existsResource.setHttpClientContextHandler(handler);
        
        params.setHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String arg0, SSLSession arg1) {
                return false;
            }
        });
        
        Assert.assertFalse(existsResource.exists());
    }

    @Test public void testBadSSLProtocol() throws IOException, ComponentInitializationException {
        final HTTPResource existsResource = new HTTPResource(client, existsHttps);
        existsResource.setHttpClientContextHandler(handler);
        
        params.setTLSProtocols(CollectionSupport.singletonList("SSLv3"));
        
        Assert.assertFalse(existsResource.exists());
    }

}