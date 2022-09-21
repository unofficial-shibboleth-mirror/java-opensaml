/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.security.httpclient.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.httpclient.HttpClientSecurityConstants;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.TrustedNamesCriterion;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.tls.impl.ThreadLocalX509CredentialContext;
import org.opensaml.security.x509.tls.impl.ThreadLocalX509TrustEngineContext;
import org.opensaml.security.x509.tls.impl.ThreadLocalX509TrustEngineSupport;
import org.opensaml.security.x509.tls.impl.ThreadLocalX509TrustManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.shared.httpclient.HttpClientSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

/**
 * An security-enhanced implementation of HttpClient's TLS-capable {@link LayeredConnectionSocketFactory}.
 * 
 * <p>
 * This implementation wraps an existing TLS socket factory instance, decorating it with additional support for:
 * </p>
 * <ul>
 *     <li>Loading and clearing thread-local instances of
 *         {@link TrustEngine}<code>&lt;</code>{@link org.opensaml.security.credential.Credential}<code>&gt;</code>
 *         and {@link CriteriaSet} used for server TLS.</li>
 *         
 *     <li>Loading and clearing a thread-local instance of {@link X509Credential} used for client TLS.</li>
 * </ul>
 * 
 * <p>
 * The context keys used by this component are as follows, defined in {@link HttpClientSecurityConstants}:
 * </p>
 * <ul>
 *   <li>{@link HttpClientSecurityConstants#CONTEXT_KEY_TRUST_ENGINE}: The trust engine instance used. 
 *        Supplied by the HttpClient caller. Must be an instance of
 *        {@link TrustEngine}<code>&lt;</code>{@link org.opensaml.security.credential.Credential}<code>&gt;</code>.</li>
 *   <li>{@link HttpClientSecurityConstants#CONTEXT_KEY_CRITERIA_SET}: The criteria set instance used. 
 *        Supplied by the HttpClient caller. Must be an instance of {@link CriteriaSet}. </li>
 *   <li>{@link HttpClientSecurityConstants#CONTEXT_KEY_SERVER_TLS_CREDENTIAL_TRUSTED}: The result of the 
 *       trust evaluation, if it was performed.  Populated by this component.  Will be a {@link Boolean}, 
 *       where <code>true</code> means the server TLS was evaluated as trusted, <code>false</code> means 
 *       the credential was evaluated as untrusted.  A null or missing value means that trust engine 
 *       evaluation was not performed.</li>
 *   <li>{@link HttpClientSecurityConstants#CONTEXT_KEY_CLIENT_TLS_CREDENTIAL}: The client TLS credential used.
 *        Supplied by the HttpClient caller. Must be an instance of {@link X509Credential}.</li>
 * </ul>
 * 
 * <p>
 * Support for server TLS via trust engine evaluation requires use of a compatible {@link javax.net.ssl.TrustManager}
 * implementation configured in the
 * {@link javax.net.ssl.SSLContext} of the wrapped {@link LayeredConnectionSocketFactory}, such as
 * {@link org.opensaml.security.x509.tls.impl.ThreadLocalX509TrustManager}.
 * </p>
 * 
 * <p>
 * Support for client TLS requires use of a compatible {@link javax.net.ssl.KeyManager}
 * implementation configured in the
 * {@link javax.net.ssl.SSLContext} of the wrapped {@link LayeredConnectionSocketFactory}, such as
 * {@link org.opensaml.security.x509.tls.impl.ThreadLocalX509CredentialKeyManager}.
 * </p>
 * 
 * <p>
 * If the trust engine context attribute is not populated by the caller, then no server TLS thread-local
 * data is populated.  If the wrapped socket factory's {@link X509TrustManager} implementation requires
 * this data (for example {@link ThreadLocalX509TrustManager}), then a fatal exception is expected to be thrown.
 * </p>
 * 
 * <p>
 * If the client TLS credential context attribute is not populated by the caller, then no client TLS thread-local data
 * is populated, and client TLS will not be attempted.
 * </p>
 */
public class SecurityEnhancedTLSSocketFactory implements LayeredConnectionSocketFactory {
    
    /** Instance of {@link ThreadLocalClientTLSCredentialHandler} to use.  */
    private static final ThreadLocalServerTLSHandler SERVER_TLS_HANDLER =
            new ThreadLocalServerTLSHandler();

    /** Instance of {@link ThreadLocalClientTLSCredentialHandler} to use.  */
    private static final ThreadLocalClientTLSCredentialHandler CLIENT_TLS_HANDLER =
            new ThreadLocalClientTLSCredentialHandler();

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(SecurityEnhancedTLSSocketFactory.class);
    
    /** The HttpClient socket factory instance wrapped by this implementation. */
    @Nonnull private LayeredConnectionSocketFactory wrappedFactory;
    
    /**
     * Constructor. 
     * 
     * @param factory the underlying HttpClient socket factory wrapped by this implementation.
     */
    public SecurityEnhancedTLSSocketFactory(@Nonnull final LayeredConnectionSocketFactory factory) {
        wrappedFactory = Constraint.isNotNull(factory, "Socket factory was null");
    }

    /** {@inheritDoc} */
    public Socket createSocket(final HttpContext context) throws IOException {
        log.trace("In createSocket");
        return wrappedFactory.createSocket(context);
    }

// CheckStyle: ParameterNumber OFF
    /** {@inheritDoc} */
    public Socket connectSocket(final int connectTimeout, final Socket sock, final HttpHost host,
            final InetSocketAddress remoteAddress, final InetSocketAddress localAddress,
            final HttpContext context) throws IOException {
        
        log.trace("In connectSocket");
        try {
            setup(context, host.getHostName());
            final Socket socket =
                    wrappedFactory.connectSocket(connectTimeout, sock, host, remoteAddress, localAddress, context);
            checkAndEvaluateServerTLS(socket);
            return socket;
        } finally {
            teardown(context);
        }
    }
// CheckStyle: ParameterNumber ON

    /** {@inheritDoc} */
    public Socket createLayeredSocket(final Socket socket, final String target, final int port,
            final HttpContext context) throws IOException {
        log.trace("In createLayeredSocket");
        try {
            setup(context, target);
            final Socket layeredSocket = wrappedFactory.createLayeredSocket(socket, target, port, context);
            checkAndEvaluateServerTLS(socket);
            return layeredSocket;
        } finally {
            teardown(context);
        }
    }
    
    /**
     * Check that the evaluation of the socket certificate using the data in
     * {@link ThreadLocalX509TrustEngineContext} has been performed, if applicable,
     * and if not, evaluate it.
     *
     * <p>
     * This will usually be called only in the case of TLS session resumption, when the standard
     * JSSE trust manager evaluation has not run.
     * </p>
     *
     * @param socket the current socket being evaluated
     * @throws IOException
     */
    protected void checkAndEvaluateServerTLS(@Nonnull final Socket socket) throws IOException {
        if (!SSLSocket.class.isInstance(socket)) {
           return;
        }

        if (ThreadLocalX509TrustEngineContext.getTrustEngine() != null) {
            if (ThreadLocalX509TrustEngineContext.getTrusted() == null) {
                log.trace("Have TrustEngine but was not previously evaluated, likely due to TLS session resumption. "
                        + "Evaluating now.");
                ThreadLocalX509TrustEngineSupport.evaluate(SSLSocket.class.cast(socket));
            } else {
                log.trace("Had TrustEngine and was previously evaluated as trusted={}",
                        ThreadLocalX509TrustEngineContext.getTrusted());
            }
        }
    }

    /**
     * Setup calling execution environment for server TLS and client TLS based on information supplied in the
     * {@link HttpContext}.
     * 
     * @param context the HttpContext instance
     * @param hostname the hostname for the connection
     *  
     * @throws SSLPeerUnverifiedException if required data is not available from the context
     */
    protected void setup(@Nullable final HttpContext context, @Nonnull final String hostname)
            throws SSLPeerUnverifiedException {
        
        log.trace("Attempting to setup thread-local data for TLS evaluation");
        if (context == null) {
            log.trace("HttpContext was null, skipping thread-local setup");
            return;
        }
        
        setupServerTLS(context, hostname);

        setupClientTLS(context);
    }

    /**
     * Setup thread-local data for server TLS and client TLS based on information supplied in the
     * {@link HttpContext}.
     * 
     * @param context the HttpContext instance
     */
    protected void setupClientTLS(@Nonnull final HttpContext context) {
        final X509Credential credential =
                (X509Credential) context.getAttribute(HttpClientSecurityConstants.CONTEXT_KEY_CLIENT_TLS_CREDENTIAL);
        
        if (credential != null) {
            log.trace("Loading ThreadLocalX509CredentialContext with client TLS credential: {}", credential);
            if (ThreadLocalX509CredentialContext.haveCurrent()) {
                log.trace("ThreadLocalX509CredentialContext was already loaded with client TLS credential, "
                        + "will be overwritten with data from HttpContext");
            }
            ThreadLocalX509CredentialContext.loadCurrent(credential);
        } else {
            log.trace("X509Credential not supplied by caller, skipping ThreadLocalX509CredentialContext population");
        }
    }

    /**
     * Setup thread-local data for server TLS.
     * 
     * @param context the HttpContext instance
     * @param hostname the hostname for the connection
     */
    protected void setupServerTLS(@Nonnull final HttpContext context, @Nonnull final String hostname) {
        
        @SuppressWarnings("unchecked")
        final TrustEngine<? super X509Credential> trustEngine =
            (TrustEngine<? super X509Credential>) context.getAttribute(
                    HttpClientSecurityConstants.CONTEXT_KEY_TRUST_ENGINE);
        
        if (trustEngine != null) {
            CriteriaSet criteriaSet = (CriteriaSet) context.getAttribute(
                    HttpClientSecurityConstants.CONTEXT_KEY_CRITERIA_SET);
            if (criteriaSet == null) {
                log.debug("No CriteriaSet supplied by caller, building new instance with signing " 
                        + "and trusted names criteria");
                criteriaSet = new CriteriaSet(new UsageCriterion(UsageType.SIGNING));
                criteriaSet.add(new TrustedNamesCriterion(Collections.singleton(hostname)));
            } else {
                log.trace("Saw CriteriaSet: {}", criteriaSet);
            }
            
            final Boolean isFailureFatal = (Boolean) context.getAttribute(
                    HttpClientSecurityConstants.CONTEXT_KEY_SERVER_TLS_FAILURE_IS_FATAL);
        
            if (ThreadLocalX509TrustEngineContext.haveCurrent()) {
                log.trace("ThreadLocalX509TrustEngineContext was already loaded with trust engine and criteria, "
                        + "will be overwritten with data from HttpContext");
            }
            
            ThreadLocalX509TrustEngineContext.loadCurrent(trustEngine, criteriaSet, isFailureFatal);
            
        } else {
            log.debug("TrustEngine not supplied by the caller, skipping ThreadLocalX509TrustEngineContext population");
        }
    }
    
    /**
     * Schedule the deferred clearing of the {@link ThreadLocalX509CredentialContext} of the client TLS credential
     * obtained from the {@link HttpContext}.
     * 
     * @param context the HttpContext instance
     */
    protected void teardown(@Nullable final HttpContext context) {
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        
        if (ThreadLocalX509TrustEngineContext.haveCurrent()) {
            log.trace("Scheduling deferred clearing of thread-local server TLS TrustEngine and CriteriaSet");
            HttpClientSupport.addDynamicContextHandlerLast(clientContext, SERVER_TLS_HANDLER, true);
        }
        
        if (ThreadLocalX509CredentialContext.haveCurrent()) {
            log.trace("Scheduling deferred clearing of thread-local client TLS X509Credential");
            HttpClientSupport.addDynamicContextHandlerLast(clientContext, CLIENT_TLS_HANDLER, true);
        }
    }

}
