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

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;

import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.core5.http.HttpHost;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.X509Credential;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * The security configuration information to use when performing HTTP client requests.
 */
public interface HttpClientSecurityConfiguration {
    
    /**
     * Get an instance of {@link CredentialsProvider} used for authentication by the HttpClient instance.
     * 
     * @return the credentials provider, or null
     */
    @Nullable CredentialsProvider getCredentialsProvider();
    
    /**
     * Get the map of rules for preemptive basic authentication using the supplied hosts and credentials.
     * 
     * @return basic-auth rule map or null
     * 
     * @since 5.0.0
     */
    @Nullable @Unmodifiable @NotLive Map<HttpHost,UsernamePasswordCredentials> getPreemptiveBasicAuthMap();
    
    /**
     * Sets the optional trust engine used in evaluating server TLS credentials.
     * 
     * @return the trust engine instance to use, or null
     */
    @Nullable TrustEngine<? super X509Credential> getTLSTrustEngine();
    
    /**
     * Get the optional list of TLS protocols. 
     * 
     * @return the TLS protocols, or null
     */
    @Nullable List<String> getTLSProtocols();
    
    /**
     * Get the optional list of TLS cipher suites.
     * 
     * @return the list of TLS cipher suites, or null
     */
    @Nullable List<String> getTLSCipherSuites();
    
    /**
     * Get the optional hostname verifier.
     * 
     * @return the hostname verifier, or null
     */
    @Nullable HostnameVerifier getHostnameVerifier();
    
    /**
     * Get the optional client TLS credential.
     * 
     * @return the client TLS credential, or null
     */
    @Nullable X509Credential getClientTLSCredential();
    
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
    @Nullable Boolean isServerTLSFailureFatal();

}