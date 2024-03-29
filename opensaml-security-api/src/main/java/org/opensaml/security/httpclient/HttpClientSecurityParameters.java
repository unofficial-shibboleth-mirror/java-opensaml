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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.primitive.DeprecationSupport;
import net.shibboleth.shared.primitive.DeprecationSupport.ObjectType;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.resolver.CriteriaSet;

import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpHost;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.X509Credential;

/**
 * Parameters related to HttpClient request security features.
 */
public class HttpClientSecurityParameters {
    
    /** HttpClient credentials provider. */
    @Nullable private CredentialsProvider credentialsProvider;
    
    /** Map of host specifications to basic-auth credentials to be applied preemptively. */
    @Nullable private Map<HttpHost,UsernamePasswordCredentials> preemptiveBasicAuthMap;
    
    /** HttpClient {@link AuthCache} to allow pre-emptive authentication. */
    @Nullable private AuthCache authCache;
    
    /** Optional trust engine used in evaluating server TLS credentials. */
    @Nullable private TrustEngine<? super X509Credential> tlsTrustEngine;
    
    /** Optional criteria set used in evaluating server TLS credentials. */
    @Nullable private CriteriaSet tlsCriteriaSet;
    
    /** TLS Protocols. */
    @Nullable private List<String> tlsProtocols;
    
    /** TLS cipher suites. */
    @Nullable private List<String> tlsCipherSuites;
    
    /** The hostname verifier. */
    @Nullable private HostnameVerifier hostnameVerifier;
    
    /** The X509 credential used for client TLS. */
    @Nullable private X509Credential clientTLSCredential;
    
    /** Flag indicating whether failure of server TLS trust engine evaluation should be treated as 
     * a fatal error. */
    @Nullable private Boolean serverTLSFailureFatal;
    
    /**
     * Get an instance of {@link CredentialsProvider} used for authentication by the HttpClient instance.
     * 
     * @return the credentials provider, or null
     */
    @Nullable public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }
    
    /**
     * Set an instance of {@link CredentialsProvider} used for authentication by the HttpClient instance.
     * 
     * @param provider the credentials provider
     * 
     * @return this object
     */
    @Nonnull public HttpClientSecurityParameters setCredentialsProvider(@Nullable final CredentialsProvider provider) {
        credentialsProvider = provider;
        return this;
    }
    
    /**
     * Install a map of rules for preemptive basic authentication using the supplied hosts and credentials.
     * 
     * <p>Use of this feature requires that the eventual {@link HttpClientContext} used be built using
     * {@link HttpClientSecuritySupport#buildHttpClientContext(HttpClientSecurityParameters)}.</p>
     * 
     * @param map preemptive basic-auth map
     * 
     * @return this object
     * 
     * @since 5.0.0
     */
    @Nonnull public HttpClientSecurityParameters setPreemptiveBasicAuthMap(
            @Nullable final Map<HttpHost,UsernamePasswordCredentials> map) {
        preemptiveBasicAuthMap = map != null ? CollectionSupport.copyToMap(map) : null;
        return this;
    }
    
    /**
     * Get the map of rules for preemptive basic authentication using the supplied hosts and credentials.
     * 
     * @return basic-auth rule map or null
     * 
     * @since 5.0.0
     */
    @Nullable @Unmodifiable @NotLive public Map<HttpHost,UsernamePasswordCredentials> getPreemptiveBasicAuthMap() {
        return preemptiveBasicAuthMap;
    }
    
    /**
     * Get an instance of {@link AuthCache} used for authentication by the HttpClient instance.
     * 
     * @return the cache, or null
     * 
     * @since 3.4.0
     */
    @Nullable public AuthCache getAuthCache() {
        return authCache;
    }
    
    /**
     * Set an instance of {@link AuthCache} used for authentication by the HttpClient instance.
     * 
     * @param cache the auth cache
     * 
     * @return this object
     * 
     * @since 3.4.0
     * 
     * @deprecated use {@link #setPreemptiveBasicAuthMap(Map)}
     */
    @Deprecated
    @Nonnull public HttpClientSecurityParameters setAuthCache(@Nullable final AuthCache cache) {
        DeprecationSupport.warn(ObjectType.METHOD, "setAuthCache", "HttpClientSecurityParameters",
                "setPreemptiveBasicAuthMap(Map)");

        authCache = cache;
        
        return this;
    }
    
    
    /**
     * A convenience method to set a (single) username and password used for BASIC authentication.
     * To disable BASIC authentication pass null for the credentials instance.
     * 
     * <p>
     * An {@link AuthScope} will be generated which specifies any host, port, scheme and realm.
     * </p>
     * 
     * <p>To specify multiple usernames and passwords for multiple host, port, scheme, and realm combinations, instead 
     * provide an instance of {@link CredentialsProvider} via {@link #setCredentialsProvider(CredentialsProvider)}.</p>
     * 
     * @param credentials the username and password credentials
     * 
     * @deprecated use {@link #setPreemptiveBasicAuthMap(Map)} or {@link #setCredentialsProvider(CredentialsProvider)}
     */
    @Deprecated
    public void setBasicCredentials(@Nullable final UsernamePasswordCredentials credentials) {
        setBasicCredentialsWithScope(credentials, null);
    }
    
    /**
     * A convenience method to set a (single) username and password used for BASIC authentication.
     * To disable BASIC authentication pass null for the credentials instance.
     * 
     * <p>
     * If the <code>authScope</code> is null, an {@link AuthScope} will be generated which specifies
     * any host, port, scheme and realm.
     * </p>
     * 
     * <p>To specify multiple usernames and passwords for multiple host, port, scheme, and realm combinations, instead 
     * provide an instance of {@link CredentialsProvider} via {@link #setCredentialsProvider(CredentialsProvider)}.</p>
     * 
     * @param credentials the username and password credentials
     * @param scope the HTTP client auth scope with which to scope the credentials, may be null
     * 
     * @deprecated use {@link #setPreemptiveBasicAuthMap(Map)} or {@link #setCredentialsProvider(CredentialsProvider)}
     * 
     * @return this object
     */
    @Deprecated
    @Nonnull public HttpClientSecurityParameters setBasicCredentialsWithScope(
            @Nullable final UsernamePasswordCredentials credentials,
            @Nullable final AuthScope scope) {
        
        DeprecationSupport.warn(ObjectType.METHOD, "setBasicCredentialsWithScope", "HttpClientSecurityParameters",
                "setPreemptiveBasicAuthMap(Map) or setCredentialsProvider(CredentialsProvider)");

        if (credentials != null) {
            AuthScope authScope = scope;
            if (authScope == null) {
                authScope = new AuthScope(null, -1);
            }
            final BasicCredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(authScope, credentials);
            credentialsProvider = provider;
        } else {
            credentialsProvider = null;
        }

        return this;
    }
    
    /**
     * Sets the optional trust engine used in evaluating server TLS credentials.
     * 
     * @return the trust engine instance to use, or null
     */
    @Nullable public TrustEngine<? super X509Credential> getTLSTrustEngine() {
        return tlsTrustEngine;
    }
    
    /**
     * Sets the optional trust engine used in evaluating server TLS credentials.
     * 
     * @param engine the trust engine instance to use
     * 
     * @return this object
     */
    @Nonnull public HttpClientSecurityParameters setTLSTrustEngine(
            @Nullable final TrustEngine<? super X509Credential> engine) {
        tlsTrustEngine = engine;
        
        return this;
    }

    /**
     * Get the optional criteria set used in evaluating server TLS credentials.
     * 
     * @return the criteria set instance to use
     */
    @Nullable public CriteriaSet getTLSCriteriaSet() {
        return tlsCriteriaSet;
    }

    /**
     * Set the optional criteria set used in evaluating server TLS credentials.
     * 
     * @param criteriaSet the new criteria set instance to use
     * 
     * @return this object
     */
    @Nonnull public HttpClientSecurityParameters setTLSCriteriaSet(@Nullable final CriteriaSet criteriaSet) {
        tlsCriteriaSet = criteriaSet;
        
        return this;
    }

    /**
     * Get the optional list of TLS protocols. 
     * 
     * @return the TLS protocols, or null
     */
    @Nullable public List<String> getTLSProtocols() {
        return tlsProtocols;
    }

    /**
     * Set the optional list of TLS protocols. 
     * 
     * @param protocols the TLS protocols or null
     * 
     * @return this object
     */
    @Nonnull public HttpClientSecurityParameters setTLSProtocols(@Nullable final Collection<String> protocols) {
        tlsProtocols = new ArrayList<>(StringSupport.normalizeStringCollection(protocols));
        if (tlsProtocols.isEmpty()) {
            tlsProtocols = null;
        }
        
        return this;
    }

    /**
     * Get the optional list of TLS cipher suites.
     * 
     * @return the list of TLS cipher suites, or null
     */
    @Nullable public List<String> getTLSCipherSuites() {
        return tlsCipherSuites;
    }

    /**
     * Set the optional list of TLS cipher suites.
     * 
     * @param cipherSuites the TLS cipher suites, or null
     * 
     * @return this object
     */
    @Nonnull public HttpClientSecurityParameters setTLSCipherSuites(@Nullable final Collection<String> cipherSuites) {
        tlsCipherSuites = new ArrayList<>(StringSupport.normalizeStringCollection(cipherSuites));
        if (tlsCipherSuites.isEmpty()) {
            tlsCipherSuites = null;
        }
        
        return this;
    }

    /**
     * Get the optional hostname verifier.
     * 
     * @return the hostname verifier, or null
     */
    @Nullable public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    /**
     * Set the optional hostname verifier.
     * 
     * @param verifier the hostname verifier, or null
     * 
     * @return this object
     */
    @Nonnull public HttpClientSecurityParameters setHostnameVerifier(@Nullable final HostnameVerifier verifier) {
        hostnameVerifier = verifier;
        
        return this;
    }

    /**
     * Get the optional client TLS credential.
     * 
     * @return the client TLS credential, or null
     */
    @Nullable public X509Credential getClientTLSCredential() {
        return clientTLSCredential;
    }

    /**
     * Set the optional client TLS credential.
     * 
     * @param credential the client TLS credential, or null
     * 
     * @return this object
     */
    @Nonnull public HttpClientSecurityParameters setClientTLSCredential(@Nullable final X509Credential credential) {
        clientTLSCredential = credential;
        
        return this;
    }

    /**
     * Get the flag indicating whether failure of server TLS trust engine evaluation should be treated as 
     * a fatal error.
     * 
     * <p>
     * Note: a {@link Boolean} is used here rather than <code>boolean</code> to explicitly allow a 
     * non-configured value, allowing consuming components to implement their own internal defaults.
     * </p>
     * 
     * @return true if fatal, false if non-fatal, null if not explicitly configured
     * 
     */
    @Nullable public Boolean isServerTLSFailureFatal() {
        return serverTLSFailureFatal;
    }

    /**
     * Set the flag indicating whether failure of server TLS trust engine evaluation should be treated as 
     * a fatal error.
     * 
     * <p>
     * Note: a {@link Boolean} is used here rather than <code>boolean</code> to explicitly allow a 
     * non-configured value, allowing consuming components to implement their own internal defaults.
     * </p>
     * 
     * @param flag true if fatal, false if non-fatal, null if not explicitly configured
     * 
     * @return this object
     */
    @Nonnull public HttpClientSecurityParameters setServerTLSFailureFatal(@Nullable final Boolean flag) {
        serverTLSFailureFatal = flag;
        
        return this;
    }

}
