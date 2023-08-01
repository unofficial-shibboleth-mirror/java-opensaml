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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.trust.TrustedCredentialTrustEngine;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/**
 * Trust engine that evaluates a credential's key against key(s) expressed within a set of trusted credentials obtained
 * from a trusted credential resolver.
 * 
 * The credential being tested is valid if its public key or secret key matches the public key, or secret key
 * respectively, contained within any of the trusted credentials produced by the given credential resolver.
 */
public class ExplicitKeyTrustEngine implements TrustedCredentialTrustEngine<Credential> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ExplicitKeyTrustEngine.class);

    /** Resolver used for resolving trusted credentials. */
    @Nonnull private final CredentialResolver credentialResolver;

    /** Trust evaluator. */
    @Nonnull private final ExplicitKeyTrustEvaluator trustEvaluator;

    /**
     * Constructor.
     * 
     * @param resolver credential resolver which is used to resolve trusted credentials
     */
    public ExplicitKeyTrustEngine(@Nonnull @ParameterName(name="resolver") final CredentialResolver resolver) {
        credentialResolver = Constraint.isNotNull(resolver, "Credential resolver cannot be null");

        trustEvaluator = new ExplicitKeyTrustEvaluator();
    }

    /** {@inheritDoc} */
    @Override
    @Nonnull public CredentialResolver getCredentialResolver() {
        return credentialResolver;
    }

    /** {@inheritDoc} */
    @Override
    public boolean validate(@Nonnull final Credential untrustedCredential,
            @Nullable final CriteriaSet trustBasisCriteria) throws SecurityException {

        log.debug("Attempting to validate untrusted credential");
        try {
            final Iterable<Credential> trustedCredentials = getCredentialResolver().resolve(trustBasisCriteria);
            return trustEvaluator.validate(untrustedCredential, trustedCredentials);
        } catch (final ResolverException e) {
            throw new SecurityException("Error resolving trusted credentials", e);
        }
    }

}