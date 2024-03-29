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

import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_CLIENT_TLS_CREDENTIAL;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_CRITERIA_SET;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_HOSTNAME_VERIFIER;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_SERVER_TLS_CREDENTIAL_TRUSTED;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_TLS_CIPHER_SUITES;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_TLS_PROTOCOLS;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_TRUST_ENGINE;
import static org.opensaml.security.httpclient.HttpClientSecurityConstants.CONTEXT_KEY_SERVER_TLS_FAILURE_IS_FATAL;

import java.net.URISyntaxException;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.hc.client5.http.ContextBuilder;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.x509.TrustedNamesCriterion;
import org.slf4j.Logger;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;

/**
 * Support class for working with {@link org.apache.hc.client5.http.classic.HttpClient} security features.
 */
public final class HttpClientSecuritySupport {
    
    /** Logger. */
    @Nonnull private static final Logger LOG = LoggerFactory.getLogger(HttpClientSecuritySupport.class);
    
    /** Constructor. */
    private HttpClientSecuritySupport() {}
    
    /**
     * Get the global {@link HttpClientSecurityConfiguration}  instance.
     * 
     * @return the global HttpClient security configuration
     */
    public static HttpClientSecurityConfiguration getGlobalHttpClientSecurityConfiguration() {
        return ConfigurationService.get(HttpClientSecurityConfiguration.class);
    }
    
    /**
     * Add default trust engine criteria for TLS usage to the {@link HttpClientContext}.
     * 
     * @param context the current HTTP context instance in use
     * @param request the current HTTP request
     */
    public static void addDefaultTLSTrustEngineCriteria(@Nonnull final HttpClientContext context, 
            @Nonnull final HttpRequest request) {
        
        if ("https".equalsIgnoreCase(request.getScheme()) 
                && context.getAttribute(CONTEXT_KEY_TRUST_ENGINE) != null) {

            CriteriaSet criteria = (CriteriaSet) context.getAttribute(CONTEXT_KEY_CRITERIA_SET);
            if (criteria == null) {
                criteria = new CriteriaSet();
                context.setAttribute(CONTEXT_KEY_CRITERIA_SET, criteria);
            }

            if (!criteria.contains(UsageCriterion.class)) {
                criteria.add(new UsageCriterion(UsageType.SIGNING));
            }

            if (!criteria.contains(TrustedNamesCriterion.class)) {
                try {
                    criteria.add(new TrustedNamesCriterion(CollectionSupport.singleton(request.getUri().getHost())));
                } catch (final URISyntaxException e) {
                    LOG.error("HttpRequest URI was invalid, got not extract hostname for TrustedNamesCriterion", e);
                }
            }

        }
    }
    
    /**
     * Check that trust engine evaluation of the server TLS credential was actually performed when the 
     * scheme is HTTPS.
     * 
     * @param context the current HTTP context instance in use
     * @param scheme the HTTP request scheme
     * @throws SSLPeerUnverifiedException thrown if the TLS credential was not actually evaluated by the trust engine
     */
    public static void checkTLSCredentialEvaluated(@Nonnull final HttpClientContext context, 
            @Nonnull final String scheme) throws SSLPeerUnverifiedException {
        if (context.getAttribute(CONTEXT_KEY_TRUST_ENGINE) != null 
                && "https".equalsIgnoreCase(scheme)) {
            if (context.getAttribute(CONTEXT_KEY_SERVER_TLS_CREDENTIAL_TRUSTED) == null) {
                LOG.warn("Configured TLS trust engine was not used to verify server TLS credential, " 
                        + "the appropriate socket factory was likely not configured");
                throw new SSLPeerUnverifiedException(
                        "Evaluation of server TLS credential with configured TrustEngine was not performed");
            }
        }
    }
    
    /**
     * Builds a new {@link HttpClientContext} and marshals the supplied {@link HttpClientSecurityParameters}
     * into it.
     * 
     * @param securityParameters the parameters to apply to the context
     * 
     * @return the fresh context
     * 
     * @since 5.0.0
     */
    @Nonnull public static HttpClientContext buildHttpClientContext(
            @Nullable final HttpClientSecurityParameters securityParameters) {
        final ContextBuilder builder = ContextBuilder.create();
        
        if (securityParameters != null) {
            final Map<HttpHost,UsernamePasswordCredentials> basicAuthMap =
                    securityParameters.getPreemptiveBasicAuthMap();
            if (basicAuthMap != null) {
                basicAuthMap.forEach(builder::preemptiveBasicAuth);
            }
        }
        
        final HttpClientContext context = builder.build();
        assert context != null;
        
        marshalSecurityParameters(context, securityParameters, true);
        return context;
    }
    
    /**
     * Marshal the supplied {@link HttpClientSecurityParameters} to the supplied {@link HttpClientContext}.
     * 
     * <p>Existing context values will NOT be replaced by non-null parameter values.</p>
     * 
     * @param context the client context instance
     * @param securityParameters the security parameters instance
     */
    public static void marshalSecurityParameters(@Nonnull final HttpClientContext context, 
            @Nullable final HttpClientSecurityParameters securityParameters) {
        marshalSecurityParameters(context, securityParameters, false);
    }
    
    /**
     * Marshal the supplied {@link HttpClientSecurityParameters} to the supplied {@link HttpClientContext}.
     * 
     * @param context the client context instance
     * @param securityParameters the security parameters instance
     * @param replace whether a non-null security parameter value should replace an existing context value
     */
    public static void marshalSecurityParameters(@Nonnull final HttpClientContext context, 
            @Nullable final HttpClientSecurityParameters securityParameters, final boolean replace) {
        if (securityParameters == null) {
            return;
        }
        Constraint.isNotNull(context, "HttpClientContext was null");
        
        if (securityParameters.getCredentialsProvider() != null) {
            if (replace || context.getCredentialsProvider() == null) {
                context.setCredentialsProvider(securityParameters.getCredentialsProvider());
            }
        }
        
        if (securityParameters.getAuthCache() != null) {
            if (replace || context.getAuthCache() == null) {
                context.setAuthCache(securityParameters.getAuthCache());
            }
        }
        
        setContextValue(context, CONTEXT_KEY_TRUST_ENGINE,
                securityParameters.getTLSTrustEngine(), replace);
        
        setContextValue(context, CONTEXT_KEY_CRITERIA_SET,
                securityParameters.getTLSCriteriaSet(), replace);
        
        setContextValue(context, CONTEXT_KEY_TLS_PROTOCOLS,
                securityParameters.getTLSProtocols(), replace);
        
        setContextValue(context, CONTEXT_KEY_TLS_CIPHER_SUITES,
                securityParameters.getTLSCipherSuites(), replace);
        
        setContextValue(context, CONTEXT_KEY_HOSTNAME_VERIFIER,
                securityParameters.getHostnameVerifier(), replace);
        
        setContextValue(context, CONTEXT_KEY_CLIENT_TLS_CREDENTIAL,
                securityParameters.getClientTLSCredential(), replace);
        
        setContextValue(context, CONTEXT_KEY_SERVER_TLS_FAILURE_IS_FATAL,
                securityParameters.isServerTLSFailureFatal(), replace);
    }
    
    /**
     * Set the supplied attribute value in the client context.
     * 
     * @param context the client context instance
     * @param attributeName the context attribute name to 
     * @param attributeValue the context attribute value to set, may be null
     * @param replace whether a non-null argument value should replace an existing context value
     */
    public static void setContextValue(@Nonnull final HttpClientContext context, 
            @Nonnull final String attributeName, @Nullable final Object attributeValue, final boolean replace) {
        if (attributeValue == null) {
            return;
        }
        Constraint.isNotNull(context, "HttpClientContext was null");
        Constraint.isNotNull(attributeName, "Context attribute name was null");
        
        if (replace || context.getAttribute(attributeName) == null) {
            context.setAttribute(attributeName, attributeValue);
        }
    }

}
