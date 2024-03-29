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

package org.opensaml.xmlsec.keyinfo.impl.provider;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.x500.X500Principal;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.DecodingException;
import net.shibboleth.shared.collection.LazySet;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialContext;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.InternalX500DNHandler;
import org.opensaml.security.x509.X500DNHandler;
import org.opensaml.security.x509.X509Support;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.keyinfo.impl.KeyInfoResolutionContext;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.X509Digest;
import org.opensaml.xmlsec.signature.X509IssuerName;
import org.opensaml.xmlsec.signature.X509IssuerSerial;
import org.opensaml.xmlsec.signature.X509SKI;
import org.opensaml.xmlsec.signature.X509SerialNumber;
import org.opensaml.xmlsec.signature.X509SubjectName;
import org.slf4j.Logger;

import com.google.common.base.Strings;

/**
 * Implementation of {@link org.opensaml.xmlsec.keyinfo.impl.KeyInfoProvider} which provides basic support for
 * extracting a {@link org.opensaml.security.x509.X509Credential} from an {@link X509Data} child of KeyInfo.
 * 
 * This provider supports only inline {@link X509Certificate}'s and {@link org.opensaml.xmlsec.signature.X509CRL}s.
 * If only one certificate is present, it is assumed to be the end-entity certificate containing the public key
 * represented by this KeyInfo. If multiple certificates are present, and any instances of {@link X509SubjectName},
 * {@link X509IssuerSerial}, {@link X509SKI}, or {@link X509Digest} are also present, they will be used to identify
 * the end-entity certificate, in accordance with the XML Signature specification. If a public key from a previously
 * resolved {@link org.opensaml.xmlsec.signature.KeyValue} is available in the resolution context, it will also be
 * used to identify the end-entity certificate. If the end-entity certificate can not otherwise be identified, the
 * cert contained in the first X509Certificate element will be treated as the end-entity certificate.
 */
public class InlineX509DataProvider extends AbstractKeyInfoProvider {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(InlineX509DataProvider.class);

    /** Responsible for parsing and serializing X.500 names to/from {@link X500Principal} instances. */
    @Nonnull private X500DNHandler x500DNHandler;

    /**
     * Constructor.
     */
    public InlineX509DataProvider() {
        x500DNHandler = new InternalX500DNHandler();
    }

    /**
     * Get the handler which process X.500 distinguished names.
     * 
     * @return returns the X500DNHandler instance
     */
    @Nonnull public X500DNHandler getX500DNHandler() {
        return x500DNHandler;
    }

    /**
     * Set the handler which process X.500 distinguished names.
     * 
     * @param handler the new X500DNHandler instance
     */
    public void setX500DNHandler(@Nonnull final X500DNHandler handler) {
        x500DNHandler = Constraint.isNotNull(handler, "X500DNHandler cannot be null");
    }

    /** {@inheritDoc} */
    public boolean handles(@Nonnull final XMLObject keyInfoChild) {
        return keyInfoChild instanceof X509Data;
    }

    /** {@inheritDoc} */
    @Nullable @Unmodifiable @NotLive public Collection<Credential> process(
            @Nonnull final KeyInfoCredentialResolver resolver, @Nonnull final XMLObject keyInfoChild,
            @Nullable final CriteriaSet criteriaSet, @Nonnull final KeyInfoResolutionContext kiContext)
                    throws SecurityException {

        if (!handles(keyInfoChild)) {
            return null;
        }

        final X509Data x509Data = (X509Data) keyInfoChild;

        log.debug("Attempting to extract credential from an X509Data");

        final List<X509Certificate> certs = extractCertificates(x509Data);
        if (certs.isEmpty()) {
            log.info("The X509Data contained no X509Certificate elements, skipping credential extraction");
            return null;
        }
        final List<X509CRL> crls = extractCRLs(x509Data);

        PublicKey resolvedPublicKey = null;
        if (kiContext.getKey() != null && kiContext.getKey() instanceof PublicKey) {
            resolvedPublicKey = (PublicKey) kiContext.getKey();
        }
        final X509Certificate entityCert = findEntityCert(certs, x509Data, resolvedPublicKey);
        if (entityCert == null) {
            log.warn("The end-entity cert could not be identified, skipping credential extraction");
            return null;
        }

        final BasicX509Credential cred = new BasicX509Credential(entityCert);
        cred.setCRLs(crls);
        cred.setEntityCertificateChain(certs);

        cred.getKeyNames().addAll(kiContext.getKeyNames());

        final CredentialContext credContext = buildCredentialContext(kiContext);
        if (credContext != null) {
            cred.getCredentialContextSet().add(credContext);
        }

        final LazySet<Credential> credentialSet = new LazySet<>();
        credentialSet.add(cred);
        return credentialSet;
    }

    /**
     * Extract CRLs from the X509Data.
     * 
     * @param x509Data the X509Data element
     * @return a list of X509CRLs
     * @throws SecurityException thrown if there is an error extracting CRLs
     */
    @Nonnull private List<X509CRL> extractCRLs(@Nonnull final X509Data x509Data) throws SecurityException {
        List<X509CRL> crls = null;
        try {
            crls = KeyInfoSupport.getCRLs(x509Data);
        } catch (final CRLException e) {
            log.error("Error extracting CRLs from X509Data: {}", e.getMessage());
            throw new SecurityException("Error extracting CRLs from X509Data", e);
        }

        log.debug("Found {} X509CRLs", crls.size());
        return crls;
    }

    /**
     * Extract certificates from the X509Data.
     * 
     * @param x509Data the X509Data element
     * @return a list of X509Certificates
     * @throws SecurityException thrown if there is an error extracting certificates
     */
    @Nonnull private List<X509Certificate> extractCertificates(@Nonnull final X509Data x509Data)
            throws SecurityException {
        List<X509Certificate> certs = null;
        try {
            certs = KeyInfoSupport.getCertificates(x509Data);
        } catch (final CertificateException e) {
            log.error("Error extracting certificates from X509Data: {}", e.getMessage());
            throw new SecurityException("Error extracting certificates from X509Data", e);
        }
        log.debug("Found {} X509Certificates", certs.size());
        return certs;
    }

    /**
     * Find the end-entity cert in the list of certs contained in the X509Data.
     * 
     * @param certs list of {@link java.security.cert.X509Certificate}
     * @param x509Data X509Data element which might contain other info helping to finding the end-entity cert
     * @param resolvedKey a key which might have previously been resolved from a KeyValue
     * @return the end-entity certificate, if found
     */
    @Nullable protected X509Certificate findEntityCert(@Nullable final List<X509Certificate> certs,
            @Nonnull final X509Data x509Data, @Nullable final PublicKey resolvedKey) {
        if (certs == null || certs.isEmpty()) {
            return null;
        }

        // If there is only 1 certificate, treat it as the end-entity certificate
        if (certs.size() == 1) {
            log.debug("Single certificate was present, treating as end-entity certificate");
            return certs.get(0);
        }

        X509Certificate cert = null;

        // Check against public key already resolved in resolution context
        cert = findCertFromKey(certs, resolvedKey);
        if (cert != null) {
            log.debug("End-entity certificate resolved by matching previously resolved public key");
            return cert;
        }

        // Check against any subject names
        cert = findCertFromSubjectNames(certs, x509Data.getX509SubjectNames());
        if (cert != null) {
            log.debug("End-entity certificate resolved by matching X509SubjectName");
            return cert;
        }

        // Check against issuer serial
        cert = findCertFromIssuerSerials(certs, x509Data.getX509IssuerSerials());
        if (cert != null) {
            log.debug("End-entity certificate resolved by matching X509IssuerSerial");
            return cert;
        }

        // Check against any subject key identifiers
        cert = findCertFromSubjectKeyIdentifier(certs, x509Data.getX509SKIs());
        if (cert != null) {
            log.debug("End-entity certificate resolved by matching X509SKI");
            return cert;
        }

        // Check against any subject X509 digests
        cert = findCertFromDigest(certs, x509Data.getX509Digests());
        if (cert != null) {
            log.debug("End-entity certificate resolved by matching X509Digest");
            return cert;
        }
        
        // TODO use some heuristic algorithm to try and figure it out based on the cert list alone.
        // This would be in X509Utils or somewhere else external to this class.

        // As a final fallback, treat the first cert in the X509Data element as the entity cert
        log.debug("Treating the first certificate in the X509Data as the end-entity certificate");
        return certs.get(0);
    }

    /**
     * Find the certificate from the chain that contains the specified key.
     * 
     * @param certs list of certificates to evaluate
     * @param key key to use as search criteria
     * @return the matching certificate, or null
     */
    @Nullable protected X509Certificate findCertFromKey(@Nonnull final List<X509Certificate> certs,
            @Nullable final PublicKey key) {
        if (key != null) {
            for (final X509Certificate cert : certs) {
                if (cert.getPublicKey().equals(key)) {
                    return cert;
                }
            }
        }
        return null;
    }

    /**
     * Find the certificate from the chain that contains one of the specified subject names.
     * 
     * @param certs list of certificates to evaluate
     * @param names X509 subject names to use as search criteria
     * @return the matching certificate, or null
     */
    @Nullable protected X509Certificate findCertFromSubjectNames(@Nonnull final List<X509Certificate> certs,
            @Nonnull final List<X509SubjectName> names) {
        
        for (final X509SubjectName subjectName : names) {
            final String subjectNameVal = subjectName.getValue();
            if (!Strings.isNullOrEmpty(subjectNameVal)) {
                X500Principal subjectX500Principal = null;
                try {
                    assert subjectNameVal != null;
                    subjectX500Principal = x500DNHandler.parse(subjectNameVal);
                } catch (final IllegalArgumentException e) {
                    log.warn("X500 subject name '{}' could not be parsed by configured X500DNHandler '{}'",
                            subjectName.getValue(), x500DNHandler.getClass().getName());
                    return null;
                }
                for (final X509Certificate cert : certs) {
                    if (cert.getSubjectX500Principal().equals(subjectX500Principal)) {
                        return cert;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Find the certificate from the chain identified by one of the specified issuer serials.
     * 
     * @param certs list of certificates to evaluate
     * @param serials X509 issuer serials to use as search criteria
     * @return the matching certificate, or null
     */
    @Nullable protected X509Certificate findCertFromIssuerSerials(@Nonnull final List<X509Certificate> certs,
            @Nonnull final List<X509IssuerSerial> serials) {
        for (final X509IssuerSerial issuerSerial : serials) {
            
            final X509IssuerName issuerNameObj = issuerSerial.getX509IssuerName();
            final X509SerialNumber serialNumObj = issuerSerial.getX509SerialNumber();
            
            if (issuerNameObj == null || serialNumObj == null) {
                continue;
            }
            
            final String issuerNameValue = issuerNameObj.getValue();
            final BigInteger serialNumber = serialNumObj.getValue();
            if (!Strings.isNullOrEmpty(issuerNameValue)) {
                X500Principal issuerX500Principal = null;
                try {
                    assert issuerNameValue != null;
                    issuerX500Principal = x500DNHandler.parse(issuerNameValue);
                } catch (final IllegalArgumentException e) {
                    log.warn("X500 issuer name '{}' could not be parsed by configured X500DNHandler '{}'",
                            issuerNameValue, x500DNHandler.getClass().getName());
                    return null;
                }
                for (final X509Certificate cert : certs) {
                    if (cert.getIssuerX500Principal().equals(issuerX500Principal)
                            && cert.getSerialNumber().equals(serialNumber)) {
                        return cert;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Find the certificate from the chain that contains one of the specified subject key identifiers.
     * 
     * @param certs list of certificates to evaluate
     * @param skis X509 subject key identifiers to use as search criteria
     * @return the matching certificate, or null
     */
    @Nullable protected X509Certificate findCertFromSubjectKeyIdentifier(@Nonnull final List<X509Certificate> certs,
            @Nonnull final List<X509SKI> skis) {
        for (final X509SKI ski : skis) {
            final String skiValue = ski.getValue();
            if (!Strings.isNullOrEmpty(skiValue)) {
                assert skiValue != null;
                final byte[] xmlValue = base64DecodeOrNull(skiValue);
                if (xmlValue==null) {
                    log.warn("Could not base64 decode subject key identifier value, skipping");
                    continue;
                }
                for (final X509Certificate cert : certs) {
                    assert cert != null;
                    final byte[] certValue = X509Support.getSubjectKeyIdentifier(cert);
                    if (certValue != null && Arrays.equals(xmlValue, certValue)) {
                        return cert;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Base64 decode the input, returning {@literal null} if there is an issue with decoding. 
     * 
     * @param base64Encoded the base64 encoded string.
     * @return the base64 decoded byte array, or {@literal null} if there is an issue decoding. 
     */
    @Nullable private byte[] base64DecodeOrNull(@Nonnull final String base64Encoded) {
        try {
            return Base64Support.decode(base64Encoded);
        } catch (final DecodingException e) {
            //swallow exception, null is reported upstream and method is private.
          return null;
        }
    }
    

    /**
     * Find the certificate from the chain that matches one of the specified digests.
     * 
     * @param certs list of certificates to evaluate
     * @param digests X509 digests to use as search criteria
     * @return the matching certificate, or null
     */
    @Nullable protected X509Certificate findCertFromDigest(@Nonnull final List<X509Certificate> certs,
            @Nonnull final List<X509Digest> digests) {
        
        for (final X509Digest digest : digests) {
            final String digestVal = digest.getValue();
            final String digestAlg = digest.getAlgorithm();
            if (!Strings.isNullOrEmpty(digestVal) && !Strings.isNullOrEmpty(digestAlg)) {
                assert digestVal != null;
                assert digestAlg != null;
                final String alg = AlgorithmSupport.getAlgorithmID(digestAlg);
                if (alg == null) {
                    log.warn("Algorithm {} not supported", digestAlg);
                    continue;
                }
                final byte[] xmlValue = base64DecodeOrNull(digestVal);
                if (xmlValue==null) {
                    log.warn("Could not base64 decode digest, skipping");
                    continue;
                }
                for (final X509Certificate cert : certs) {
                    assert cert != null;
                    try {
                        final byte[] certValue = X509Support.getX509Digest(cert, alg);
                        if (certValue != null && Arrays.equals(xmlValue, certValue)) {
                            return cert;
                        }
                    } catch (final SecurityException e) {
                        // Ignore as no match.
                    }
                }
            }
        }
        return null;
    } 
}