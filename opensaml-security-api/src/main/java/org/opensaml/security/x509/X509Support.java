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

package org.opensaml.security.x509;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.security.auth.x500.X500Principal;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.codec.DecodingException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.cryptacular.EncodingException;
import org.cryptacular.util.CertUtil;
import org.cryptacular.util.CodecUtil;
import org.cryptacular.x509.GeneralNameType;
import org.cryptacular.x509.dn.NameReader;
import org.cryptacular.x509.dn.RDNSequence;
import org.cryptacular.x509.dn.StandardAttributeType;
import org.opensaml.security.SecurityException;
import org.opensaml.security.crypto.KeySupport;
import org.slf4j.Logger;

import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.net.InetAddresses;

/**
 * Utility class for working with X509 objects.
 */
public class X509Support {

    /** Common Name (CN) OID. */
    @Nonnull @NotEmpty public static final String CN_OID = "2.5.4.3";
    
    /** Subject Key Identifier (SKI) OID. */
    @Nonnull @NotEmpty public static final String SKI_OID = "2.5.29.14";

    /** RFC 2459 Other Subject Alt Name type. */
    @Nonnull public static final Integer OTHER_ALT_NAME = 0;

    /** RFC 2459 RFC 822 (email address) Subject Alt Name type. */
    @Nonnull public static final Integer RFC822_ALT_NAME = 1;

    /** RFC 2459 DNS Subject Alt Name type. */
    @Nonnull public static final Integer DNS_ALT_NAME = 2;

    /** RFC 2459 X.400 Address Subject Alt Name type. */
    @Nonnull public static final Integer X400ADDRESS_ALT_NAME = 3;

    /** RFC 2459 Directory Name Subject Alt Name type. */
    @Nonnull public static final Integer DIRECTORY_ALT_NAME = 4;

    /** RFC 2459 EDI Party Name Subject Alt Name type. */
    @Nonnull public static final Integer EDI_PARTY_ALT_NAME = 5;

    /** RFC 2459 URI Subject Alt Name type. */
    @Nonnull public static final Integer URI_ALT_NAME = 6;

    /** RFC 2459 IP Address Subject Alt Name type. */
    @Nonnull public static final Integer IP_ADDRESS_ALT_NAME = 7;

    /** RFC 2459 Registered ID Subject Alt Name type. */
    @Nonnull public static final Integer REGISTERED_ID_ALT_NAME = 8;
    
    /** Logger. */
    @Nonnull private static final Logger LOG = LoggerFactory.getLogger(X509Support.class);

    /** Constructed. */
    protected X509Support() {

    }

    /**
     * Determines the certificate, from the collection, associated with the private key.
     * 
     * @param certs certificates to check
     * @param privateKey entity's private key
     * 
     * @return the certificate associated with entity's private key or null if no certificate in the collection is
     *         associated with the given private key
     * 
     * @throws SecurityException thrown if the public or private keys checked are of an unsupported type
     * 
     * @since 1.2
     */
    @Nullable public static X509Certificate determineEntityCertificate(
            @Nonnull final Collection<X509Certificate> certs, @Nonnull final PrivateKey privateKey)
                    throws SecurityException {

        for (final X509Certificate certificate : certs) {
            try {
                if (KeySupport.matchKeyPair(certificate.getPublicKey(), privateKey)) {
                    return certificate;
                }
            } catch (final SecurityException e) {
                // An exception here is just a false match.
                // Java 7 apparently throws in this case.
            }
        }

        return null;
    }

    /**
     * Gets the commons names that appear within the given distinguished name. 
     * 
     * <p>
     * The returned list provides the names in the order they appeared in the DN, according to 
     * RFC 1779/2253 encoding. In this encoding the "most specific" name would typically appear
     * in the left-most position, and would appear first in the returned list.
     * </p>
     * 
     * @param dn the DN to extract the common names from
     * 
     * @return the common names that appear in the DN in the order they appear, or null if the given DN is null
     */
    @Nonnull @Unmodifiable @NotLive public static List<String> getCommonNames(@Nonnull final X500Principal dn) {

        LOG.debug("Extracting CNs from the following DN: {}", dn.toString());
        final RDNSequence attrs = NameReader.readX500Principal(dn);
        // Have to copy because list returned from Attributes is unmodifiable, so can't reverse it.
        final List<String> values = new ArrayList<>(attrs.getValues(StandardAttributeType.CommonName));
        
        // Reverse the order so that the most-specific CN is first in the list, 
        // consistent with RFC 1779/2253 RDN ordering.
        Collections.reverse(values);
        
        return values;
    }

    /**
     * Gets the list of alternative names of a given name type.
     * 
     * @param certificate the certificate to extract the alternative names from
     * @param nameTypes the name types
     * 
     * @return the alt names, of the given type, within the cert
     */
    @Nonnull @Unmodifiable @NotLive public static List<?> getAltNames(@Nonnull final X509Certificate certificate,
            @Nonnull @NotEmpty final Integer[] nameTypes) {

        final List<Object> altNames = new LinkedList<>();
        final GeneralNameType[] types = new GeneralNameType[nameTypes.length];
        for (int i = 0; i < nameTypes.length; i++) {
            types[i]= GeneralNameType.fromTagNumber(nameTypes[i]);
        }
        
        try {
            final GeneralNames names = CertUtil.subjectAltNames(certificate, types);
            if (names != null) {
                for (final GeneralName name : names.getNames()) {
                    altNames.add(convertAltNameType(name.getTagNo(), name.getName().toASN1Primitive()));
                }
            }
            return altNames;
        } catch (final EncodingException e) {
            LOG.warn("Could not extract alt names from certificate: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Gets the common name components of the issuer and all the subject alt names of a given type.
     * 
     * @param certificate certificate to extract names from
     * @param altNameTypes type of alt names to extract
     * 
     * @return list of subject names in the certificate
     */
    @Nonnull @Unmodifiable @NotLive public static List<?> getSubjectNames(@Nonnull final X509Certificate certificate,
            @Nonnull @NotEmpty final Integer[] altNameTypes) {
        final List<Object> issuerNames = new LinkedList<>();
        
        final List<String> entityCertCNs = X509Support.getCommonNames(certificate.getSubjectX500Principal());
        if (entityCertCNs != null && !entityCertCNs.isEmpty()) {
            issuerNames.add(entityCertCNs.get(0));
        }
        final List<?> entityAltNames = X509Support.getAltNames(certificate, altNameTypes);
        if (entityAltNames != null) {
            issuerNames.addAll(entityAltNames);
        }

        return issuerNames;
    }

    /**
     * Get the plain (non-DER encoded) value of the Subject Key Identifier extension of an X.509 certificate, if
     * present.
     * 
     * @param certificate an X.509 certificate possibly containing a subject key identifier
     * @return the plain (non-DER encoded) value of the Subject Key Identifier extension, or null if the certificate
     *         does not contain the extension
     */
    @Nullable public static byte[] getSubjectKeyIdentifier(@Nonnull final X509Certificate certificate) {
        final byte[] derValue = certificate.getExtensionValue(SKI_OID);
        if (derValue == null || derValue.length == 0) {
            return null;
        }

        try {
            final ASN1Primitive ski = JcaX509ExtensionUtils.parseExtensionValue(derValue);
            return ((DEROctetString) ski).getOctets();
        } catch (final IOException e) {
            LOG.error("Unable to extract subject key identifier from certificate: ASN.1 parsing failed: " + e);
            return null;
        }
    }

    /**
     * Get the XML Signature-compliant digest of an X.509 certificate.
     * 
     * @param certificate an X.509 certificate
     * @param jcaAlgorithm JCA algorithm identifier
     * @return the raw digest of the certificate
     * @throws SecurityException is algorithm is unsupported or encoding is not possible
     */
    @Nonnull public static byte[] getX509Digest(@Nonnull final X509Certificate certificate,
            @Nonnull final String jcaAlgorithm) throws SecurityException {
        try {
            final MessageDigest hasher = MessageDigest.getInstance(jcaAlgorithm);
            return hasher.digest(certificate.getEncoded());
        } catch (final CertificateEncodingException e) {
            LOG.error("Unable to encode certificate for digest operation", e);
            throw new SecurityException("Unable to encode certificate for digest operation", e);
        } catch (final NoSuchAlgorithmException e) {
            LOG.error("Algorithm {} is unsupported", jcaAlgorithm);
            throw new SecurityException("Algorithm " + jcaAlgorithm + " is unsupported", e);
        }
    }
    
    /**
     * Decodes X.509 certificates in DER or PEM format.
     * 
     * @param certs encoded certs
     * 
     * @return decoded certs
     * 
     * @throws CertificateException thrown if the certificates cannot be decoded
     * 
     * @since 1.2
     */
    @Nonnull @Unmodifiable @NotLive
    public static Collection<X509Certificate> decodeCertificates(@Nonnull final File certs)
            throws CertificateException {
        Constraint.isNotNull(certs, "Input file cannot be null");
        if (!certs.exists()) {
            throw new CertificateException("Certificate file " + certs.getAbsolutePath() + " does not exist");
        } else if (!certs.canRead()) {
            throw new CertificateException("Certificate file " + certs.getAbsolutePath() + " is not readable");
        }
        
        try {
            return decodeCertificates(Files.toByteArray(certs));
        } catch(final IOException e) {
            throw new CertificateException("Error reading certificate file " + certs.getAbsolutePath(), e);
        }
    }
    
    /**
     * Decodes X.509 certificates in DER or PEM format. Note this does <strong>not</strong> close the inout handle
     * 
     * @param certs encoded certs
     * 
     * @return decoded certs
     * 
     * @throws CertificateException thrown if the certificates cannot be decoded
     * 
     * @since 1.2
     */
    @Nonnull @Unmodifiable @NotLive
    public static Collection<X509Certificate> decodeCertificates(@Nonnull final InputStream certs)
            throws CertificateException {
        Constraint.isNotNull(certs, "Input Stream cannot be null");
        
        try {
            return decodeCertificates(ByteStreams.toByteArray(certs));
        } catch(final IOException e) {
            throw new CertificateException("Error reading certificate file", e);
        }
    }



    /**
     * Decodes X.509 certificates in DER or PEM format.
     * 
     * @param certs encoded certs
     * 
     * @return decoded certs
     * 
     * @throws CertificateException thrown if the certificates cannot be decoded
     */
    @Nonnull @Unmodifiable @NotLive
    public static Collection<X509Certificate> decodeCertificates(@Nonnull final byte[] certs)
            throws CertificateException {
        try {
            return Arrays.asList(CertUtil.decodeCertificateChain(certs));
        } catch (final EncodingException e) {
            throw new CertificateException("Error deocding certificates", e);
        }
    }
    
    /**
     * Decodes a single X.509 certificate in DER or PEM format.
     * 
     * @param cert encoded cert
     * 
     * @return decoded cert
     * 
     * @throws CertificateException thrown if the certificate can not be decoded
     * 
     * @since 1.2
     */
    @Nonnull public static X509Certificate decodeCertificate(@Nonnull final File cert) throws CertificateException {
        Constraint.isNotNull(cert, "Input file cannot be null");
        if (!cert.exists()) {
            throw new CertificateException("Certificate file " + cert.getAbsolutePath() + " does not exist");
        } else if (!cert.canRead()) {
            throw new CertificateException("Certificate file " + cert.getAbsolutePath() + " is not readable");
        }
        
        try {
            return decodeCertificate(Files.toByteArray(cert));
        } catch(final IOException e) {
            throw new CertificateException("Error reading certificate file " + cert.getAbsolutePath(), e);
        }
    }
    
    /**
     * Decodes a single X.509 certificate in DER or PEM format.
     * 
     * @param cert encoded cert
     * 
     * @return decoded cert
     * 
     * @throws CertificateException thrown if the certificate cannot be decoded
     */
    @Nonnull public static X509Certificate decodeCertificate(@Nonnull final byte[] cert) throws CertificateException {
        try {
            return CertUtil.decodeCertificate(cert);
        } catch (final IllegalArgumentException | EncodingException e) {
            throw new CertificateException(e);
        }
    }
    
    /**
     * Decode a single Java certificate from base64 encoded form without PEM headers and footers.
     * 
     * @param base64Cert base64-encoded certificate
     * @return a native Java X509 certificate
     * @throws CertificateException thrown if there is an error constructing certificate
     */
    @Nonnull public static X509Certificate decodeCertificate(@Nonnull final String base64Cert)
            throws CertificateException {
        try {
            return decodeCertificate(Base64Support.decode(base64Cert));
        } catch (final DecodingException e) {
           throw new CertificateException(e);
        }
    }
    
    /**
     * Decodes CRLs in DER or PKCS#7 format. If in PKCS#7 format only the CRLs are decoded; the rest of the content is
     * ignored.
     * 
     * @param crls encoded CRLs
     * 
     * @return decoded CRLs
     * 
     * @throws CRLException thrown if the CRLs can not be decoded
     * 
     * @since 1.2
     */
    @Nonnull @Unmodifiable @NotLive
    public static Collection<X509CRL> decodeCRLs(@Nonnull final File crls) throws CRLException{
        Constraint.isNotNull(crls, "Input file cannot be null");
        if (!crls.exists()) {
            throw new CRLException("CRL file " + crls.getAbsolutePath() + " does not exist");
        } else if (!crls.canRead()) {
            throw new CRLException("CRL file " + crls.getAbsolutePath() + " is not readable");
        }
        
        try {
            return decodeCRLs(Files.toByteArray(crls));
        } catch(final IOException e) {
            throw new CRLException("Error reading CRL file " + crls.getAbsolutePath(), e);
        }
    }
    
    /**
     * Decodes CRLs in DER or PKCS#7 format. If in PKCS#7 format only the CRLs are decoded; the rest of the content is
     * ignored. Note, this does <strong>not</strong> close the inout stream
     * 
     * @param crls encoded CRLs
     * 
     * @return decoded CRLs
     * 
     * @throws CRLException thrown if the CRLs can not be decoded
     * 
     * @since 1.2
     */
    @Nonnull @Unmodifiable @NotLive
    public static Collection<X509CRL> decodeCRLs(@Nonnull final InputStream crls) throws CRLException{
        Constraint.isNotNull(crls, "Input stream cannot be null");
        
        try {
            return decodeCRLs(ByteStreams.toByteArray(crls));
        } catch(final IOException e) {
            throw new CRLException("Error reading CRL", e);
        }
    }


    /**
     * Decodes CRLs in DER or PKCS#7 format. If in PKCS#7 format only the CRLs are decoded; the rest of the content is
     * ignored.
     * 
     * @param crls encoded CRLs
     * 
     * @return decoded CRLs
     * 
     * @throws CRLException thrown if the CRLs can not be decoded
     */
    @Nonnull @Unmodifiable @NotLive
    public static Collection<X509CRL> decodeCRLs(@Nonnull final byte[] crls) throws CRLException {
        try {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (Collection<X509CRL>) cf.generateCRLs(new ByteArrayInputStream(crls));
        } catch (final GeneralSecurityException e) {
            throw new CRLException("Unable to decode X.509 certificates");
        }
    }
    
    /**
     * Decode CRL in base64 encoded form without PEM headers and footers.
     * 
     * @param base64CRL base64-encoded CRL
     * @return a native Java X509 CRL
     * @throws CertificateException thrown if there is an error constructing certificate
     * @throws CRLException thrown if there is an error constructing CRL
     */
    @Nonnull public static X509CRL decodeCRL(@Nonnull final String base64CRL)
            throws CertificateException, CRLException {
        try {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final ByteArrayInputStream input = new ByteArrayInputStream(Base64Support.decode(base64CRL));
            return (java.security.cert.X509CRL) cf.generateCRL(input);
        } catch (final DecodingException e) {
            throw new CRLException("Unable to base64 decode CRL",e);
        }       
    }

    /**
     * Gets a formatted string representing identifier information from the supplied credential.
     * 
     * <p>
     * This could for example be used in logging messages.
     * </p>
     * 
     * <p>
     * Often it will be the case that a given credential that is being evaluated will NOT have a value for the entity ID
     * property. So extract the certificate subject DN, and if present, the credential's entity ID.
     * </p>
     * 
     * @param credential the credential for which to produce a token.
     * @param handler the X.500 DN handler to use. If null, a new instance of {@link InternalX500DNHandler} will be
     *            used.
     * 
     * @return a formatted string containing identifier information present in the credential
     */
    @Nonnull public static String getIdentifiersToken(@Nonnull final X509Credential credential,
            @Nullable final X500DNHandler handler) {
        Constraint.isNotNull(credential, "Credential cannot be null");
        
        final X500DNHandler x500DNHandler;
        if (handler != null) {
            x500DNHandler = handler;
        } else {
            x500DNHandler = new InternalX500DNHandler();
        }
        final X500Principal x500Principal = credential.getEntityCertificate().getSubjectX500Principal();
        final StringBuilder builder = new StringBuilder();
        builder.append('[');
        builder.append(String.format("subjectName='%s'", x500DNHandler.getName(x500Principal)));
        if (!Strings.isNullOrEmpty(credential.getEntityId())) {
            builder.append(String.format(" |credential entityID='%s'", StringSupport.trimOrNull(credential
                    .getEntityId())));
        }
        builder.append(']');
        return builder.toString();
    }

// Checkstyle: CyclomaticComplexity OFF
    /**
     * Convert types returned by Bouncy Castle X509ExtensionUtil.getSubjectAlternativeNames(X509Certificate) to be
     * consistent with what is documented for: java.security.cert.X509Certificate#getSubjectAlternativeNames.
     * 
     * @param nameType the alt name type
     * @param nameValue the alt name value
     * @return converted representation of name value, based on type
     */
    @Nullable private static Object convertAltNameType(@Nonnull final Integer nameType,
            @Nonnull final ASN1Primitive nameValue) {
        
        if (DIRECTORY_ALT_NAME.equals(nameType) || DNS_ALT_NAME.equals(nameType) || RFC822_ALT_NAME.equals(nameType)
                || URI_ALT_NAME.equals(nameType) || REGISTERED_ID_ALT_NAME.equals(nameType)) {

            // these are just strings in the appropriate format already, return as-is
            return nameValue.toString();
        } else if (IP_ADDRESS_ALT_NAME.equals(nameType)) {
            // this is a byte[], IP addr in network byte order
            final byte [] nameValueBytes = ((DEROctetString) nameValue).getOctets();
            try {
                return InetAddresses.toAddrString(InetAddress.getByAddress(nameValueBytes));
            } catch (final UnknownHostException e) {
                LOG.warn("Was unable to convert IP address alt name byte[] to string: " +
                        CodecUtil.hex(nameValueBytes, true), e);
                return null;
            }
        } else if (EDI_PARTY_ALT_NAME.equals(nameType) || X400ADDRESS_ALT_NAME.equals(nameType)
                || OTHER_ALT_NAME.equals(nameType)) {

            // these have no defined representation, just return a DER-encoded byte[]
            return nameValue;
        } else {
            LOG.warn("Encountered unknown alt name type '{}', adding as-is", nameType);
            return nameValue;
        }
    }
// Checkstyle: CyclomaticComplexity ON
    
}