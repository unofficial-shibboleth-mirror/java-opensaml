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

import javax.annotation.Nonnull;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.httpclient.TLSSocketFactory;


/**
 * Security-related constants for use with Apache HttpClient.
 */
public final class HttpClientSecurityConstants {
    
    /** Context key for a trust engine instance supplied by an HttpClient caller. 
     * Value must be an instance of
     * {@link org.opensaml.security.trust.TrustEngine}<code>&lt;? super </code>
     * {@link org.opensaml.security.x509.X509Credential}<code>&gt;</code>.
     */
    @Nonnull @NotEmpty public static final String CONTEXT_KEY_TRUST_ENGINE = "opensaml.TrustEngine";
    
    /** Context key for a criteria set instance supplied by an HttpClient caller. 
     * Value must be an instance of {@link net.shibboleth.shared.resolver.CriteriaSet}. */
    @Nonnull @NotEmpty public static final String CONTEXT_KEY_CRITERIA_SET = "opensaml.CriteriaSet";
    
    /** Context key for a server TLS credential evaluation result, populated by specialized instances 
     * of HttpClient socket factories. Type will be a {@link Boolean}. */
    @Nonnull @NotEmpty
    public static final String CONTEXT_KEY_SERVER_TLS_CREDENTIAL_TRUSTED = "opensaml.ServerTLSCredentialTrusted";
    
    /** Context key for indicating whether server TLS evaluation failure should be treated as a fatal error.
     * Type will be a {@link Boolean}. */
    @Nonnull @NotEmpty
    public static final String CONTEXT_KEY_SERVER_TLS_FAILURE_IS_FATAL = "opensaml.ServerTLSFailureIsFatal";
    
    /** HttpContext key for the client TLS credential. 
     * Must be an instance of {@link org.opensaml.security.x509.X509Credential}. */
    @Nonnull @NotEmpty public static final String CONTEXT_KEY_CLIENT_TLS_CREDENTIAL = "opensaml.ClientTLSCredential";
    
    /** HttpContext key for a a list of TLS protocols to enable on the socket.  
     * Must be an instance of {@link java.util.List}<code>&lt;</code>{@link String}<code>&gt;</code>. */
    @Nonnull @NotEmpty
    public static final String CONTEXT_KEY_TLS_PROTOCOLS = TLSSocketFactory.CONTEXT_KEY_TLS_PROTOCOLS;
    
    /** HttpContext key for a a list of TLS cipher suites to enable on the socket.  
     * Must be an instance of {@link java.util.List}<code>&lt;</code>{@link String}<code>&gt;</code>. */
    @Nonnull @NotEmpty
    public static final String CONTEXT_KEY_TLS_CIPHER_SUITES = TLSSocketFactory.CONTEXT_KEY_TLS_CIPHER_SUITES;
    
    /** HttpContext key for an instance of {@link javax.net.ssl.HostnameVerifier}. */
    @Nonnull @NotEmpty
    public static final String CONTEXT_KEY_HOSTNAME_VERIFIER = TLSSocketFactory.CONTEXT_KEY_HOSTNAME_VERIFIER;
    
    /** Constructor. */
    private HttpClientSecurityConstants() {}

}