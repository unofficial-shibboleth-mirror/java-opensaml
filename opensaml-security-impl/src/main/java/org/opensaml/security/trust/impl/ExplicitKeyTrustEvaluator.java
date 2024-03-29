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

import java.security.Key;

import javax.annotation.Nonnull;

import org.opensaml.security.credential.Credential;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Auxillary trust evaluator for evaluating an untrusted key or credential against a trusted key or credential. Trust is
 * established if the untrusted key (a public key or symmetric key from the untrusted credential) matches one of the
 * trusted keys supplied.
 */
public class ExplicitKeyTrustEvaluator {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ExplicitKeyTrustEvaluator.class);

    /**
     * Evaluate trust.
     * 
     * @param untrustedKey the untrusted key to evaluate
     * @param trustedKey basis for trust
     * @return true if trust can be established, false otherwise
     */
    public boolean validate(@Nonnull final Key untrustedKey, @Nonnull final Key trustedKey) {
        return untrustedKey.equals(trustedKey);
    }

    /**
     * Evaluate trust.
     * 
     * @param untrustedKey the untrusted key to evaluate
     * @param trustedKeys basis for trust
     * @return true if trust can be established, false otherwise
     */
    public boolean validate(@Nonnull final Key untrustedKey, @Nonnull final Iterable<Key> trustedKeys) {
        for (final Key trustedKey : trustedKeys) {
            if (untrustedKey.equals(trustedKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Evaluate trust.
     * 
     * @param untrustedCredential the untrusted credential to evaluate
     * @param trustedCredential basis for trust
     * @return true if trust can be established, false otherwise
     */
    public boolean validate(@Nonnull final Credential untrustedCredential,
            @Nonnull final Credential trustedCredential) {

        Key untrustedKey = null;
        Key trustedKey = null;
        if (untrustedCredential.getPublicKey() != null) {
            untrustedKey = untrustedCredential.getPublicKey();
            trustedKey = trustedCredential.getPublicKey();
        } else {
            untrustedKey = untrustedCredential.getSecretKey();
            trustedKey = trustedCredential.getSecretKey();
        }
        if (untrustedKey == null) {
            log.debug("Untrusted credential contained no key, unable to evaluate");
            return false;
        } else if (trustedKey == null) {
            log.debug("Trusted credential contained no key of the appropriate type, unable to evaluate");
            return false;
        }

        if (validate(untrustedKey, trustedKey)) {
            log.debug("Successfully validated untrusted credential against trusted key");
            return true;
        }

        log.debug("Failed to validate untrusted credential against trusted key");
        return false;
    }

    /**
     * Evaluate trust.
     * 
     * @param untrustedCredential the untrusted credential to evaluate
     * @param trustedCredentials basis for trust
     * @return true if trust can be established, false otherwise
     */
    public boolean validate(@Nonnull final Credential untrustedCredential,
            @Nonnull final Iterable<Credential> trustedCredentials) {

        for (final Credential trustedCredential : trustedCredentials) {
            assert trustedCredential != null;
            if (validate(untrustedCredential, trustedCredential)) {
                return true;
            }
        }
        return false;
    }

}