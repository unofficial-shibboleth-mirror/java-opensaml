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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.X509TrustManager;

import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.X509Credential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

/**
 * An implementation of {@link X509TrustManager} which performs its evaluation using trust engine and criteria
 * instances available from {@link ThreadLocalX509TrustEngineContext}.
 */
public class ThreadLocalX509TrustManager implements X509TrustManager {
    
    /** Logger. */
    private Logger log = LoggerFactory.getLogger(ThreadLocalX509TrustManager.class);

    /** {@inheritDoc} */
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[]{};
    }
    
    /** {@inheritDoc} */
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        performTrustEval(chain, authType);
    }
    
    /** {@inheritDoc} */
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        performTrustEval(chain, authType);
    }

    /**
     * Perform trust evaluation on the specified certificate chain using the trust engine and criteria
     * available from {@link ThreadLocalX509TrustEngineContext}.
     * 
     * @param chain the peer certificate chain
     * @param authType the authentication type based on the client certificate
     * 
     * @throws CertificateException if the certificate chain is not trusted by this TrustManager.
     */
    protected void performTrustEval(final X509Certificate[] chain, final String authType) throws CertificateException {
        // These checks are per the documentation for this interface
        if (chain == null || chain.length == 0) {
            throw new IllegalArgumentException("Certificate chain was null or empty");
        }
        if (authType == null || authType.isEmpty()) {
            throw new IllegalArgumentException("AuthType was null or empty");
        }
        
        if (!ThreadLocalX509TrustEngineContext.haveCurrent()) {
            throw new CertificateException("Trust of X509Certificate could not be established, "
                    + "ThreadLocalX509TrustEngineContext is not populated");
        }
        
        if (performTrustEval(chain,
                ThreadLocalX509TrustEngineContext.getTrustEngine(),
                ThreadLocalX509TrustEngineContext.getCriteria())) {
            ThreadLocalX509TrustEngineContext.setTrusted(true);
        } else {
            ThreadLocalX509TrustEngineContext.setTrusted(false);
            if (ThreadLocalX509TrustEngineContext.isFailureFatal()) {
                log.debug("Credential evaluated as untrusted, failure indicated as fatal");
                throw new CertificateException("Trust engine could not establish trust of presented TLS credential");
            }
            log.debug("Credential evaluated as untrusted, failure indicated as non-fatal");
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
    protected boolean performTrustEval(@Nonnull final X509Certificate[] chain,
            @Nonnull final TrustEngine<? super X509Credential> trustEngine,
            @Nonnull final CriteriaSet criteriaSet) throws CertificateException {
        
        log.debug("Attempting to evaluate server TLS credential against supplied TrustEngine and CriteriaSet");
        
        final X509Credential credential = extractCredential(chain);
        
        log.trace("Saw trust engine of type: {}", trustEngine.getClass().getName());

        try {
            if (trustEngine.validate(credential, criteriaSet)) {
                log.debug("Credential evaluated as trusted");
                return true;
            }
            log.debug("Credential evaluated as untrusted");
            return false;
        } catch (final Throwable t) {
            log.error("Fatal trust engine error evaluating credential", t);
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
    @Nonnull protected X509Credential extractCredential(@Nonnull @NotEmpty final X509Certificate[] chain)
            throws CertificateException {
        
        final List<X509Certificate> certChain = Arrays.asList(chain);
        
        final X509Certificate entityCert = certChain.get(0);
        
        final BasicX509Credential credential = new BasicX509Credential(entityCert);
        credential.setEntityCertificateChain(certChain);
        
        return credential;
    }

}
