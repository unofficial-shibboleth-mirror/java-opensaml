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

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.x509.PKIXTrustEngine;
import org.opensaml.security.x509.PKIXTrustEvaluator;
import org.opensaml.security.x509.PKIXValidationInformation;
import org.opensaml.security.x509.PKIXValidationInformationResolver;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.impl.BasicX509CredentialNameEvaluator;
import org.opensaml.security.x509.impl.CertPathPKIXTrustEvaluator;
import org.opensaml.security.x509.impl.X509CredentialNameEvaluator;
import org.opensaml.xmlsec.crypto.XMLSigningUtil;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.Signature;
import org.slf4j.Logger;

/**
 * An implementation of {@link org.opensaml.xmlsec.signature.support.SignatureTrustEngine} which evaluates the validity
 * and trustworthiness of XML and raw signatures.
 * 
 * <p>
 * Processing is performed as described in {@link BaseSignatureTrustEngine}. If based on this processing, it is
 * determined that the Signature's KeyInfo is not present or does not contain a valid (and trusted) signing key, then
 * trust engine validation fails. Since the PKIX engine is based on the assumption that trusted signing keys are not
 * known in advance, the signing key must be present in, or derivable from, the information in the Signature's KeyInfo
 * element.
 * </p>
 */
public class PKIXSignatureTrustEngine extends
        BaseSignatureTrustEngine<Pair<Set<String>, Iterable<PKIXValidationInformation>>> implements
        PKIXTrustEngine<Signature> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(PKIXSignatureTrustEngine.class);

    /** Resolver used for resolving trusted credentials. */
    @Nonnull private final PKIXValidationInformationResolver pkixResolver;

    /** The external PKIX trust evaluator used to establish trust. */
    @Nonnull private final PKIXTrustEvaluator pkixTrustEvaluator;

    /** The external credential name evaluator used to establish trusted name compliance. */
    @Nullable private final X509CredentialNameEvaluator credNameEvaluator;

    /**
     * Constructor.
     * 
     * <p>
     * The PKIX trust evaluator used defaults to {@link CertPathPKIXTrustEvaluator}.
     * </p>
     * 
     * <p>
     * The X.509 credential name evaluator used defaults to {@link BasicX509CredentialNameEvaluator}.
     * </p>
     * 
     * @param resolver credential resolver used to resolve trusted credentials.
     * @param keyInfoResolver KeyInfo credential resolver used to obtain the (advisory) signing credential from a
     *            Signature's KeyInfo element.
     */
    public PKIXSignatureTrustEngine(
            @Nonnull @ParameterName(name="resolver") final PKIXValidationInformationResolver resolver,
            @Nonnull @ParameterName(name="keyInfoResolver") final KeyInfoCredentialResolver keyInfoResolver) {

        super(keyInfoResolver);

        pkixResolver = Constraint.isNotNull(resolver, "PKIX trust information resolver cannot be null");
        pkixTrustEvaluator = new CertPathPKIXTrustEvaluator();
        credNameEvaluator = new BasicX509CredentialNameEvaluator();
    }

    /**
     * Constructor.
     * 
     * @param resolver credential resolver used to resolve trusted credentials.
     * @param keyInfoResolver KeyInfo credential resolver used to obtain the (advisory) signing credential from a
     *            Signature's KeyInfo element. 
     * @param pkixEvaluator the PKIX trust evaluator to use
     * @param nameEvaluator the X.509 credential name evaluator to use (may be null)
     */
    public PKIXSignatureTrustEngine(
            @Nonnull @ParameterName(name="resolver") final PKIXValidationInformationResolver resolver,
            @Nonnull @ParameterName(name="keyInfoResolver") final KeyInfoCredentialResolver keyInfoResolver,
            @Nonnull @ParameterName(name="pkixEvaluator") final PKIXTrustEvaluator pkixEvaluator,
            @Nullable @ParameterName(name="nameEvaluator") final X509CredentialNameEvaluator nameEvaluator) {

        super(keyInfoResolver);

        pkixResolver = Constraint.isNotNull(resolver, "PKIX trust information resolver cannot be null");
        pkixTrustEvaluator = Constraint.isNotNull(pkixEvaluator, "PKIX trust evaluator cannot be null");
        credNameEvaluator = nameEvaluator;
    }

    /**
     * Get the PKIXTrustEvaluator instance used to evaluate trust.
     * 
     * <p>
     * The parameters of this evaluator may be modified to adjust trust evaluation processing.
     * </p>
     * 
     * @return the PKIX trust evaluator instance that will be used
     */
    @Nonnull public PKIXTrustEvaluator getPKIXTrustEvaluator() {
        return pkixTrustEvaluator;
    }

    /**
     * Get the X509CredentialNameEvaluator instance used to evaluate a credential against trusted names.
     * 
     * <p>
     * The parameters of this evaluator may be modified to adjust trust evaluation processing.
     * </p>
     * 
     * @return the PKIX trust evaluator instance that will be used
     */
    @Nullable public X509CredentialNameEvaluator getX509CredentialNameEvaluator() {
        return credNameEvaluator;
    }

    /** {@inheritDoc} */
    @Override @Nonnull public PKIXValidationInformationResolver getPKIXResolver() {
        return pkixResolver;
    }

    /** {@inheritDoc} */
    @Override protected boolean doValidate(@Nonnull final Signature signature,
            @Nullable final CriteriaSet trustBasisCriteria) throws SecurityException {

        final Pair<Set<String>, Iterable<PKIXValidationInformation>> validationPair =
                resolveValidationInfo(trustBasisCriteria);

        if (validate(signature, validationPair)) {
            return true;
        }

        log.debug("PKIX validation of signature failed, unable to resolve valid and trusted signing key");
        return false;
    }

    /** {@inheritDoc} */
    @Override protected boolean doValidate(@Nonnull final byte[] signature, @Nonnull final byte[] content,
            @Nonnull final String algorithmURI, @Nullable final CriteriaSet trustBasisCriteria,
            @Nullable final Credential candidateCredential) throws SecurityException {

        if (candidateCredential == null || CredentialSupport.extractVerificationKey(candidateCredential) == null) {
            log.debug("Candidate credential was either not supplied or did not contain verification key");
            log.debug("PKIX trust engine requires supplied key, skipping PKIX trust evaluation");
            return false;
        }

        final Pair<Set<String>, Iterable<PKIXValidationInformation>> validationPair =
                resolveValidationInfo(trustBasisCriteria);

        try {
            if (XMLSigningUtil.verifyWithURI(candidateCredential, algorithmURI, signature, content)) {
                log.debug("Successfully verified raw signature using supplied candidate credential");
                log.debug("Attempting to establish trust of supplied candidate credential");
                if (evaluateTrust(candidateCredential, validationPair)) {
                    log.debug("Successfully established trust of supplied candidate credential");
                    return true;
                }
                log.debug("Failed to establish trust of supplied candidate credential");
            } else {
                log.debug("Cryptographic verification of raw signature failed with candidate credential");
            }
        } catch (final SecurityException e) {
            // Java 7 now throws this exception under conditions such as mismatched key sizes.
            // Swallow this, it's logged by the verifyWithURI method already.
        }

        log.debug("PKIX validation of raw signature failed, "
                + "unable to establish trust of supplied verification credential");
        return false;
    }

    /** {@inheritDoc} */
    @Override protected boolean evaluateTrust(@Nonnull final Credential untrustedCredential,
            @Nullable final Pair<Set<String>, Iterable<PKIXValidationInformation>> validationPair)
            throws SecurityException {

        if (!(untrustedCredential instanceof X509Credential)) {
            log.debug("Can not evaluate trust of non-X509Credential");
            return false;
        }
        final X509Credential untrustedX509Credential = (X509Credential) untrustedCredential;

        if (validationPair == null) {
            log.debug("PKIX validation information not available. Aborting PKIX validation");
            return false;
        }
        
        final Set<String> trustedNames = validationPair.getFirst();
        final Iterable<PKIXValidationInformation> validationInfoSet = validationPair.getSecond();
        if (validationInfoSet == null) {
            log.debug("PKIX validation information not available. Aborting PKIX validation");
            return false;
        }

        if (!checkNames(trustedNames, untrustedX509Credential)) {
            log.debug("Evaluation of credential against trusted names failed. Aborting PKIX validation");
            return false;
        }

        for (final PKIXValidationInformation validationInfo : validationInfoSet) {
            assert validationInfo != null;
            try {
                if (pkixTrustEvaluator.validate(validationInfo, untrustedX509Credential)) {
                    log.debug("Signature trust established via PKIX validation of signing credential");
                    return true;
                }
            } catch (final SecurityException e) {
                // log the operational error, but allow other validation info sets to be tried
                log.debug("Error performing PKIX validation on untrusted credential", e);
            }
        }

        log.debug("Signature trust could not be established via PKIX validation of signing credential");
        return false;
    }

    /**
     * Resolve and return a set of trusted validation information.
     * 
     * @param trustBasisCriteria criteria used to describe and/or resolve the information which serves as the basis for
     *            trust evaluation
     * @return a pair consisting of an optional set of trusted names, and an iterable of trusted
     *         PKIXValidationInformation
     * @throws SecurityException thrown if there is an error resolving the information from the trusted resolver
     */
    @Nonnull protected Pair<Set<String>, Iterable<PKIXValidationInformation>> resolveValidationInfo(
            @Nullable final CriteriaSet trustBasisCriteria) throws SecurityException {

        Set<String> trustedNames = null;
        if (pkixResolver.supportsTrustedNameResolution()) {
            try {
                trustedNames = pkixResolver.resolveTrustedNames(trustBasisCriteria);
            } catch (final UnsupportedOperationException e) {
                throw new SecurityException("Error resolving trusted names", e);
            } catch (final ResolverException e) {
                throw new SecurityException("Error resolving trusted names", e);
            }
        } else {
            log.debug("PKIX resolver does not support resolution of trusted names, skipping name checking");
        }
        final Iterable<PKIXValidationInformation> validationInfoSet;
        try {
            validationInfoSet = pkixResolver.resolve(trustBasisCriteria);
        } catch (final ResolverException e) {
            throw new SecurityException("Error resolving trusted PKIX validation information", e);
        }

        return new Pair<>(trustedNames, validationInfoSet);
    }

    /**
     * Evaluate the credential against the set of trusted names.
     * 
     * <p>
     * Evaluates to true if no instance of {@link X509CredentialNameEvaluator} is configured.
     * </p>
     * 
     * @param trustedNames set of trusted names
     * @param untrustedCredential the credential being evaluated
     * @return true if evaluation is successful, false otherwise
     * @throws SecurityException thrown if there is an error evaluation the credential
     */
    protected boolean checkNames(@Nullable final Set<String> trustedNames,
            @Nonnull final X509Credential untrustedCredential) throws SecurityException {

        if (credNameEvaluator == null) {
            log.debug("No credential name evaluator was available, skipping trusted name evaluation");
            return true;
        } else if (trustedNames == null) {
            log.debug("Trusted names was null, signalling PKIX resolver does not support trusted names resolution, " 
                    + "skipping trusted name evaluation");
           return true; 
        } else {
            assert credNameEvaluator != null;
            return credNameEvaluator.evaluate(untrustedCredential, trustedNames);
        }

    }

}