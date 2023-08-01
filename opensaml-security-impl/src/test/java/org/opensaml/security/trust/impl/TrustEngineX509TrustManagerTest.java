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

package org.opensaml.security.trust.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import javax.annotation.Nonnull;

import net.shibboleth.shared.testing.InMemoryDirectory;

import org.cryptacular.util.KeyPairUtil;
import org.ldaptive.ConnectException;
import org.ldaptive.Connection;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.LdapException;
import org.ldaptive.ResultCode;
import org.ldaptive.SearchOperation;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResponse;
import org.ldaptive.ssl.SslConfig;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.impl.StaticCredentialResolver;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test of {@link TrustEngineX509TrustManager} implementation.
 */
public class TrustEngineX509TrustManagerTest {

    private final static String DATA_PATH = "src/test/resources/org/opensaml/security/ldap/impl/";

    private final static String DATA_CLASSPATH = "/org/opensaml/security/ldap/impl/";

    private InMemoryDirectory directoryServer;

    /** LDAP DN to test. */
    private final String context = "ou=people,dc=example,dc=org";

    /**
     * Creates an in-memory directory server. Leverages LDIF found in test resources.
     */
    @BeforeClass public void setupDirectoryServer() {
        directoryServer =
            new InMemoryDirectory(
                new String[] {"dc=example,dc=org"},
                new ClassPathResource(DATA_CLASSPATH + "test-ldap.ldif"),
                10389,
                new ClassPathResource(DATA_CLASSPATH + "test-ldap.keystore"),
                Optional.empty());
        directoryServer.start();
    }

    /**
     * Shutdown the in-memory directory server.
     */
    @AfterClass public void teardownDirectoryServer() throws Exception {
        if (directoryServer.openConnectionCount() > 0) {
            Thread.sleep(100);
        }
        assertEquals(directoryServer.openConnectionCount(), 0);
        directoryServer.stop(true);
    }

    /**
     * Make sure default trust fails.
     * 
     * @throws LdapException ...
     */
    @Test(expectedExceptions=ConnectException.class)
    public void testDefaultTrust() throws LdapException {
        doOpen(DefaultConnectionFactory.builder()
            .config(ConnectionConfig.builder()
                .url("ldap://localhost:10389")
                .useStartTLS(true)
                .build())
            .build());
    }
    
    /**
     * No trust engine.
     * 
     * @throws LdapException ...
     */
    @Test(expectedExceptions=ConnectException.class)
    public void testNullTrust() throws LdapException {
        final TrustEngineX509TrustManager trustManager = new TrustEngineX509TrustManager();
        doOpen(DefaultConnectionFactory.builder()
            .config(ConnectionConfig.builder()
                .url("ldap://localhost:10389")
                .useStartTLS(true)
                .sslConfig(SslConfig.builder()
                    .trustManagers(trustManager)
                    .build())
                .build())
            .build());
    }
    
    /**
     * Static trust engine.
     * 
     * @throws LdapException ...
     * @throws FileNotFoundException ...
     * @throws IOException ...
     */
    @Test
    public void testStaticTrust() throws LdapException, FileNotFoundException, IOException {
        final StaticCredentialResolver resolver;
        try (final FileInputStream is = new FileInputStream(DATA_PATH + "test-ldap.key")) {
            resolver = new StaticCredentialResolver(new BasicCredential(KeyPairUtil.readPublicKey(is)));
        }
        final TrustEngineX509TrustManager trustManager = new TrustEngineX509TrustManager();
        trustManager.setTLSTrustEngine(new ExplicitKeyTrustEngine(resolver));
        doSearch(DefaultConnectionFactory.builder()
            .config(ConnectionConfig.builder()
                .url("ldap://localhost:10389")
                .useStartTLS(true)
                .sslConfig(SslConfig.builder()
                    .trustManagers(trustManager)
                    .build())
                .build())
            .build());
    }

    protected void doOpen(@Nonnull final ConnectionFactory factory) throws LdapException {
        final Connection conn = factory.getConnection();
        try {
            conn.open();
        } finally {
            conn.close();
        }
    }

    protected void doSearch(@Nonnull final ConnectionFactory factory) throws LdapException {
        final SearchOperation search = new SearchOperation(factory);
        final SearchResponse response =
                search.execute(SearchRequest.objectScopeSearchRequest(context, new String[] {"description"}));
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getResultCode(), ResultCode.SUCCESS);
    }
    
}
