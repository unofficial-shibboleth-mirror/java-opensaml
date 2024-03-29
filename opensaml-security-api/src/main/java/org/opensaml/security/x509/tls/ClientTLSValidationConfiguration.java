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

package org.opensaml.security.x509.tls;

import javax.annotation.Nullable;

import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.X509Credential;

/**
 * Configuration used in validating a client TLS {@link X509Credential}.
 */
public interface ClientTLSValidationConfiguration {

    /**
     * Get a {@link TrustEngine} instance used to validate a client TLS {@link X509Credential}.
     * 
     * @return a trust engine instance, may be null
     */
    @Nullable TrustEngine<? super X509Credential> getX509TrustEngine();

    /**
     * Get a {@link CertificateNameOptions} instance to use when evaluating a client TLS {@link X509Credential}.
     * 
     * @return an options instance, may be null
     */
    @Nullable CertificateNameOptions getCertificateNameOptions();

}