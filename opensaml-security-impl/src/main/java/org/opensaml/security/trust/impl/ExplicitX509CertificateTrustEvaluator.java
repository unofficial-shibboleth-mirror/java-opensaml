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

import java.security.cert.X509Certificate;

import javax.annotation.Nonnull;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.X509Credential;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Auxillary trust evaluator for evaluating an untrusted X509 certificate or credential against a trusted certificate or
 * credential. Trust is established if the untrusted certificate supplied (or the certificate obtained from the
 * untrusted credential's {@link X509Credential#getEntityCertificate()}) matches one of the trusted certificates
 * supplied.
 */
public class ExplicitX509CertificateTrustEvaluator {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ExplicitX509CertificateTrustEvaluator.class);

    /**
     * Evaluate trust.
     * 
     * @param untrustedCertificate the untrusted certificate to evaluate
     * @param trustedCertificate basis for trust
     * @return true if trust can be established, false otherwise
     */
    public boolean validate(@Nonnull final X509Certificate untrustedCertificate,
            @Nonnull final X509Certificate trustedCertificate) {
        return untrustedCertificate.equals(trustedCertificate);
    }

    /**
     * Evaluate trust.
     * 
     * @param untrustedCertificate the untrusted certificate to evaluate
     * @param trustedCertificates basis for trust
     * @return true if trust can be established, false otherwise
     */
    public boolean validate(@Nonnull final X509Certificate untrustedCertificate,
            @Nonnull final Iterable<X509Certificate> trustedCertificates) {
        
        for (final X509Certificate trustedCertificate : trustedCertificates) {
            if (untrustedCertificate.equals(trustedCertificate)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Evaluate trust.
     * 
     * @param untrustedCredential the untrusted X509Credential to evaluate
     * @param trustedCredential basis for trust
     * @return true if trust can be established, false otherwise
     */
    public boolean validate(@Nonnull final X509Credential untrustedCredential,
            @Nonnull final X509Credential trustedCredential) {

        if (validate(untrustedCredential.getEntityCertificate(), trustedCredential.getEntityCertificate())) {
            log.debug("Successfully validated untrusted credential against trusted certificate");
            return true;
        }
        
        log.debug("Failed to validate untrusted credential against trusted certificate");
        return false;
    }

    /**
     * Evaluate trust.
     * 
     * @param untrustedCredential the untrusted X509Credential to evaluate
     * @param trustedCredentials basis for trust
     * @return true if trust can be established, false otherwise
     */
    public boolean validate(@Nonnull final X509Credential untrustedCredential,
            @Nonnull final Iterable<Credential> trustedCredentials) {

        for (final Credential trustedCredential : trustedCredentials) {
            if (!(trustedCredential instanceof X509Credential)) {
                log.debug("Skipping evaluation against trusted, non-X509Credential");
                continue;
            }
            final X509Credential trustedX509Credential = (X509Credential) trustedCredential;
            if (validate(untrustedCredential, trustedX509Credential)) {
                return true;
            }
        }

        return false;
    }

}