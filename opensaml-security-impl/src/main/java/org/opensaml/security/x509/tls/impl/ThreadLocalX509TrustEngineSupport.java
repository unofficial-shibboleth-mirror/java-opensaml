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

package org.opensaml.security.x509.tls.impl;

import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;

import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.X509Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

/**
 * Support class for centralizing evaluation of a certificate chain using trust engine and criteria
 * from {@link ThreadLocalX509TrustEngineContext}.
 */
public final class ThreadLocalX509TrustEngineSupport {
    
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(ThreadLocalX509TrustEngineSupport.class);
    
    /** Constructor. */
    private ThreadLocalX509TrustEngineSupport() { }
    
    /**
     * Perform trust evaluation on the specified {@link SSLSocket} using the current data in
     * {@link ThreadLocalX509TrustEngineContext}.
     * 
     * @param sslSocket the socket whose certificates are to be evaluated
     * 
     * @throws SSLPeerUnverifiedException if the certificate chain was not trusted by the supplied TrustEngine
     */
    public static void evaluate(@Nonnull final SSLSocket sslSocket) throws SSLPeerUnverifiedException {
        final Certificate[] chain = sslSocket.getSession().getPeerCertificates();
        
        if (chain == null || chain.length == 0) {
            throw new IllegalArgumentException("Certificate chain was null or empty");
        }
        
        final X509Certificate[] x509Chain = new X509Certificate[chain.length];
        for (int i=0; i<chain.length; i++) {
            if (!X509Certificate.class.isInstance(chain[i])) {
                throw new SSLPeerUnverifiedException("Certificate chain contained non-X509Certificate");
            }
            x509Chain[i] = X509Certificate.class.cast(chain[i]);
        }

        try {
            evaluate(x509Chain);
        } catch (final CertificateException e) {
            throw new SSLPeerUnverifiedException(e.getMessage());
        }
    }

    /**
     * Perform trust evaluation on the specified certificate chain using the current data in
     * {@link ThreadLocalX509TrustEngineContext}.
     * 
     * @param chain the certificate chain to be evaluated
     * 
     * @throws CertificateException if the certificate chain is not trusted by the supplied TrustEngine
     */
    public static void evaluate(@Nonnull final X509Certificate[] chain) throws CertificateException {
        if (chain == null || chain.length == 0) {
            throw new IllegalArgumentException("Certificate chain was null or empty");
        }
        
        if (!ThreadLocalX509TrustEngineContext.haveCurrent()) {
            throw new CertificateException("Trust of X509Certificate could not be established, "
                    + "ThreadLocalX509TrustEngineContext is not populated");
        }
        
        LOG.trace("Evaluating X509Certificate[] chain against ThreadLocalX509TrustEngineContext");
        
        if (performTrustEval(chain,
                ThreadLocalX509TrustEngineContext.getTrustEngine(),
                ThreadLocalX509TrustEngineContext.getCriteria())) {
            ThreadLocalX509TrustEngineContext.setTrusted(true);
        } else {
            ThreadLocalX509TrustEngineContext.setTrusted(false);
            if (ThreadLocalX509TrustEngineContext.isFailureFatal()) {
                LOG.debug("Credential evaluated as untrusted, failure indicated as fatal");
                throw new CertificateException("Trust engine could not establish trust of presented TLS credential");
            }
            LOG.debug("Credential evaluated as untrusted, failure indicated as non-fatal");
        }
    }
    
    /**
     * Perform trust evaluation on the specified certificate chain using the supplied trust engine and criteria.
     * 
     * @param chain the certificate chain to be evaluated
     * @param trustEngine the trust engine
     * @param criteriaSet the criteria set
     * 
     * @return true if certificate was established as trusted, false if not
     * 
     * @throws CertificateException if the trust of the certificate
     */
    private static boolean performTrustEval(@Nonnull final X509Certificate[] chain,
            @Nonnull final TrustEngine<? super X509Credential> trustEngine,
            @Nonnull final CriteriaSet criteriaSet) throws CertificateException {
        
        LOG.debug("Attempting to evaluate server TLS credential against supplied TrustEngine and CriteriaSet");
        
        final X509Credential credential = extractCredential(chain);
        
        LOG.trace("Saw trust engine of type: {}", trustEngine.getClass().getName());

        try {
            if (trustEngine.validate(credential, criteriaSet)) {
                LOG.debug("Credential evaluated as trusted");
                return true;
            }
            LOG.debug("Credential evaluated as untrusted");
            return false;
        } catch (final Throwable t) {
            LOG.error("Fatal trust engine error evaluating credential", t);
            return false;
        }
        
    }

    /**
     * Extract the server TLS {@link X509Credential} from the supplied {@link SSLSocket}.
     * 
     * @param chain the chain of X509 certificates
     * @return an X509Credential representing the entity certificate as well as the 
     *          supplied supporting intermediate certificate chain (if any)
     * @throws CertificateException if credential data can not be extracted from the socket
     */
    @Nonnull private static X509Credential extractCredential(@Nonnull @NotEmpty final X509Certificate[] chain)
            throws CertificateException {
        
        final List<X509Certificate> certChain = Arrays.asList(chain);
        
        final X509Certificate entityCert = certChain.get(0);
        
        final BasicX509Credential credential = new BasicX509Credential(entityCert);
        credential.setEntityCertificateChain(certChain);
        
        return credential;
    }

}
