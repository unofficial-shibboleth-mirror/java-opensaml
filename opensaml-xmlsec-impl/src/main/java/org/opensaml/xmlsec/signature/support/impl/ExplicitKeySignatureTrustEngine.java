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

package org.opensaml.xmlsec.signature.support.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.KeyAlgorithmCriterion;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.trust.TrustedCredentialTrustEngine;
import org.opensaml.security.trust.impl.ExplicitKeyTrustEvaluator;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.crypto.XMLSigningUtil;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.Signature;
import org.slf4j.Logger;

import com.google.common.base.Strings;

/**
 * An implementation of {@link org.opensaml.xmlsec.signature.support.SignatureTrustEngine} which evaluates the validity
 * and trustworthiness of XML and raw signatures.
 * 
 * <p>
 * Processing is first performed as described in {@link BaseSignatureTrustEngine}. If based on this processing, it is
 * determined that the Signature's KeyInfo is not present or does not contain a resolveable valid (and trusted) signing
 * key, then all trusted credentials obtained by the trusted credential resolver will be used to attempt to validate the
 * signature.
 * </p>
 */
public class ExplicitKeySignatureTrustEngine extends BaseSignatureTrustEngine<Iterable<Credential>> implements
        TrustedCredentialTrustEngine<Signature> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ExplicitKeySignatureTrustEngine.class);

    /** Resolver used for resolving trusted credentials. */
    @Nonnull private final CredentialResolver credentialResolver;

    /** The external explicit key trust engine to use as a basis for trust in this implementation. */
    @Nonnull private final ExplicitKeyTrustEvaluator keyTrust;

    /**
     * Constructor.
     * 
     * @param resolver credential resolver used to resolve trusted credentials.
     * @param keyInfoResolver KeyInfo credential resolver used to obtain the (advisory) signing credential from a
     *            Signature's KeyInfo element.
     */
    public ExplicitKeySignatureTrustEngine(@Nonnull final @ParameterName(name="resolver") CredentialResolver resolver,
            @Nonnull @ParameterName(name="keyInfoResolver") final KeyInfoCredentialResolver keyInfoResolver) {
        super(keyInfoResolver);

        credentialResolver = Constraint.isNotNull(resolver, "Credential resolver cannot be null");
        keyTrust = new ExplicitKeyTrustEvaluator();
    }

    /** {@inheritDoc} */
    @Nonnull public CredentialResolver getCredentialResolver() {
        return credentialResolver;
    }

    /** {@inheritDoc} */
    @Override protected boolean doValidate(@Nonnull final Signature signature,
            @Nullable final CriteriaSet trustBasisCriteria) throws SecurityException {

        final CriteriaSet criteriaSet = new CriteriaSet();
        criteriaSet.addAll(trustBasisCriteria);
        if (!criteriaSet.contains(UsageCriterion.class)) {
            criteriaSet.add(new UsageCriterion(UsageType.SIGNING));
        }
        
        final String signatureAlg = signature.getSignatureAlgorithm();
        final String jcaAlgorithm = signatureAlg != null ? AlgorithmSupport.getKeyAlgorithm(signatureAlg) : null;
        if (!Strings.isNullOrEmpty(jcaAlgorithm)) {
            assert jcaAlgorithm != null;
            criteriaSet.add(new KeyAlgorithmCriterion(jcaAlgorithm), true);
        }

        final Iterable<Credential> trustedCredentials;
        try {
            trustedCredentials = getCredentialResolver().resolve(criteriaSet);
        } catch (final ResolverException e) {
            throw new SecurityException("Error resolving trusted credentials", e);
        }

        if (validate(signature, trustedCredentials)) {
            return true;
        }

        // If the credentials extracted from Signature's KeyInfo (if any) did not verify the
        // signature and/or establish trust, as a fall back attempt verify the signature with
        // the trusted credentials directly.
        log.debug("Attempting to verify signature using trusted credentials");

        for (final Credential trustedCredential : trustedCredentials) {
            assert trustedCredential != null;
            if (verifySignature(signature, trustedCredential)) {
                log.debug("Successfully verified signature using resolved trusted credential");
                return true;
            }
        }
        log.debug("Failed to verify signature using either KeyInfo-derived or directly trusted credentials");
        return false;
    }

    /** {@inheritDoc} */
    // CheckStyle: CyclomaticComplexity OFF
    @Override protected boolean doValidate(@Nonnull final byte[] signature, @Nonnull final byte[] content,
            @Nonnull final String algorithmURI, @Nullable final CriteriaSet trustBasisCriteria,
            @Nullable final Credential candidateCredential) throws SecurityException {

        final CriteriaSet criteriaSet = new CriteriaSet();
        criteriaSet.addAll(trustBasisCriteria);
        if (!criteriaSet.contains(UsageCriterion.class)) {
            criteriaSet.add(new UsageCriterion(UsageType.SIGNING));
        }
        final String jcaAlgorithm = AlgorithmSupport.getKeyAlgorithm(algorithmURI);
        if (!Strings.isNullOrEmpty(jcaAlgorithm)) {
            assert jcaAlgorithm != null;
            criteriaSet.add(new KeyAlgorithmCriterion(jcaAlgorithm), true);
        }

        final Iterable<Credential> trustedCredentials;
        try {
            trustedCredentials = getCredentialResolver().resolve(criteriaSet);
        } catch (final ResolverException e) {
            throw new SecurityException("Error resolving trusted credentials", e);
        }

        // First try the optional supplied candidate credential
        if (candidateCredential != null) {
            log.debug("Attempting to verify raw signature using supplied candidate credential");
            try {
                if (XMLSigningUtil.verifyWithURI(candidateCredential, algorithmURI, signature, content)) {
                    log.debug("Successfully verified signature using supplied candidate credential");
                    log.debug("Attempting to establish trust of supplied candidate credential");
                    if (evaluateTrust(candidateCredential, trustedCredentials)) {
                        log.debug("Successfully established trust of supplied candidate credential");
                        return true;
                    }
                    log.debug("Failed to establish trust of supplied candidate credential");
                }
            } catch (final SecurityException e) {
                // Java 7 now throws this exception under conditions such as mismatched key sizes.
                log.debug("Saw fatal error attempting to verify raw signature with supplied candidate credential", e);
            }
        }

        // If the candidate verification credential did not verify the
        // signature and/or establish trust, or if no candidate was supplied,
        // as a fall back attempt to verify the signature with the trusted credentials directly.
        log.debug("Attempting to verify signature using trusted credentials");

        for (final Credential trustedCredential : trustedCredentials) {
            assert trustedCredential != null;
            try {
                if (XMLSigningUtil.verifyWithURI(trustedCredential, algorithmURI, signature, content)) {
                    log.debug("Successfully verified signature using resolved trusted credential");
                    return true;
                }
            } catch (final SecurityException e) {
                // Java 7 now throws this exception under conditions such as mismatched key sizes.
                log.debug("Saw fatal error attempting to verify raw signature with trusted credential", e);
            }
        }
        log.debug("Failed to verify signature using either supplied candidate credential"
                + " or directly trusted credentials");
        return false;
    }

    // CheckStyle: CyclomaticComplexity ON

    /** {@inheritDoc} */
    @Override protected boolean evaluateTrust(@Nonnull final Credential untrustedCredential,
            @Nullable final Iterable<Credential> trustedCredentials) throws SecurityException {

        if (trustedCredentials == null) {
            log.debug("No trusted credential supplied");
            return false;
        }
        
        return keyTrust.validate(untrustedCredential, trustedCredentials);
    }

}