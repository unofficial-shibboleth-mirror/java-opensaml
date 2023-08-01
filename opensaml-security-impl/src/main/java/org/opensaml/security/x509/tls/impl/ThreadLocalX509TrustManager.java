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

package org.opensaml.security.x509.tls.impl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.annotation.Nonnull;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * An implementation of {@link X509TrustManager} which performs its evaluation using trust engine and criteria
 * instances available from {@link ThreadLocalX509TrustEngineContext}.
 */
public class ThreadLocalX509TrustManager implements X509TrustManager {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(ThreadLocalX509TrustManager.class);

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

        log.trace("Evaluating certificate chain against ThreadLocalX509TrustEngineContext data");

        ThreadLocalX509TrustEngineSupport.evaluate(chain);
    }

}