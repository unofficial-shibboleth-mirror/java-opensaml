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

package org.opensaml.xmlsec.keyinfo.impl;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.InternalX500DNHandler;
import org.opensaml.security.x509.X500DNHandler;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.X509Support;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.X509CRL;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.xmlsec.signature.X509Data;
import org.opensaml.xmlsec.signature.X509SKI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import net.shibboleth.shared.collection.LazySet;
import net.shibboleth.shared.logic.Constraint;

/**
 * A factory implementation which produces instances of {@link KeyInfoGenerator} capable of 
 * handling the information contained within an {@link X509Credential}.
 * 
 * All boolean options default to false. The default implementation of {@link X500DNHandler} used is
 * {@link InternalX500DNHandler}. The default output format for subject and issuer DN's is RFC2253.
 * The default set of subject alternative names to process is empty.
 */
public class X509KeyInfoGeneratorFactory extends BasicKeyInfoGeneratorFactory {
    
    /** The set of options configured for the factory. */
    @Nonnull private final X509Options options;
    
    /** Constructor. */
    public X509KeyInfoGeneratorFactory() {
        options = (X509Options) super.getOptions();
    }
    
    /** {@inheritDoc} */
    @Nonnull public Class<? extends Credential> getCredentialType() {
        return X509Credential.class;
    }

    /** {@inheritDoc} */
    public boolean handles(@Nonnull final Credential credential) {
        return credential instanceof X509Credential;
    }

    /** {@inheritDoc} */
    @Nonnull public KeyInfoGenerator newInstance() {
        return newInstance(null);
    }
    
    /** {@inheritDoc} */
    @Nonnull public KeyInfoGenerator newInstance(@Nullable final Class<? extends KeyInfo> type) {
        return new X509KeyInfoGenerator(options.clone(), type);
    }
    
    /**
     * Get the option to emit the CRL list as sequence of X509CRL elements within X509Data.
     * 
     * @return the option value
     */
    public boolean emitCRLs() {
        return options.emitCRLs;
    }

    /**
     * Set the option to emit the CRL list as sequence of X509CRL elements within X509Data.
     * 
     * @param newValue the new option value
     */
    public void setEmitCRLs(final boolean newValue) {
        options.emitCRLs = newValue;
    }

    /**
     * Get the option to emit the entity certificate as an X509Certificate element within X509Data. 
     *
     * @return the option value
     */
    public boolean emitEntityCertificate() {
        return options.emitEntityCertificate;
    }

    /**
     * Set the option to emit the entity certificate as an X509Certificate element within X509Data. 
     *
     * @param newValue the new option value
     */
    public void setEmitEntityCertificate(final boolean newValue) {
        options.emitEntityCertificate = newValue;
    }

    /**
     * Get the option to emit the entity certificate chain as sequence of X509Certificate elements within X509Data.
     * 
     * @return the option value
     */
    public boolean emitEntityCertificateChain() {
        return options.emitEntityCertificateChain;
    }

    /**
     * Set the option to emit the entity certificate chain as sequence of X509Certificate elements within X509Data.
     * 
     * @param newValue the new option value
     */
    public void setEmitEntityCertificateChain(final boolean newValue) {
        options.emitEntityCertificateChain = newValue;
    }

    /**
     * Get the option to emit the entity certificate subject alternative name extension values as KeyName elements.
     * 
     * @return the option value
     */
    public boolean emitSubjectAltNamesAsKeyNames() {
        return options.emitSubjectAltNamesAsKeyNames;
    }

    /**
     * Set the option to emit the entity certificate subject alternative name extension values as KeyName elements.
     * 
     * @param newValue the new option value
     */
    public void setEmitSubjectAltNamesAsKeyNames(final boolean newValue) {
        options.emitSubjectAltNamesAsKeyNames = newValue;
    }

    /**
     * Get the option to emit the entity certificate subject DN common name (CN) fields as KeyName elements.
     * 
     * @return the option value
     */
    public boolean emitSubjectCNAsKeyName() {
        return options.emitSubjectCNAsKeyName;
    }

    /**
     * Set the option to emit the entity certificate subject DN common name (CN) fields as KeyName elements.
     * 
     * @param newValue the new option value
     */
    public void setEmitSubjectCNAsKeyName(final boolean newValue) {
        options.emitSubjectCNAsKeyName = newValue;
    }

    /**
     * Get the option to emit the entity certificate subject DN as a KeyName element.
     * 
     * @return the option value
     */
    public boolean emitSubjectDNAsKeyName() {
        return options.emitSubjectDNAsKeyName;
    }

    /**
     * Set the option to emit the entity certificate subject DN as a KeyName element.
     * 
     * @param newValue the new option value
     */
    public void setEmitSubjectDNAsKeyName(final boolean newValue) {
        options.emitSubjectDNAsKeyName = newValue;
    }

    /**
     * Get the option to emit the entity certificate issuer name and serial number as 
     * an X509IssuerSerial element within X509Data.
     * 
     * @return the option value
     */
    public boolean emitX509IssuerSerial() {
        return options.emitX509IssuerSerial;
    }

    /**
     * Set the option to emit the entity certificate issuer name and serial number as 
     * an X509IssuerSerial element within X509Data.
     * 
     * @param newValue the new option value
     */
    public void setEmitX509IssuerSerial(final boolean newValue) {
        options.emitX509IssuerSerial = newValue;
    }

    /**
     * Get the option to emit the entity certificate subject key identifier as an X509SKI element within X509Data.
     * 
     * @return the option value
     */
    public boolean emitX509SKI() {
        return options.emitX509SKI;
    }

    /**
     * Set the option to emit the entity certificate subject key identifier as an X509SKI element within X509Data.
     * 
     * @param newValue the new option value
     */
    public void setEmitX509SKI(final boolean newValue) {
        options.emitX509SKI = newValue;
    }

    /**
     * Get the option to emit the entity certificate digest as an X509Digest element within X509Data.
     * 
     * @return the option value
     */
    public boolean emitX509Digest() {
        return options.emitX509Digest;
    }

    /**
     * Set the option to emit the entity certificate digest as an X509Digest element within X509Data.
     * 
     * @param newValue the new option value
     */
    public void setEmitX509Digest(final boolean newValue) {
        options.emitX509Digest = newValue;
    }

    /**
     * Get the algorithm URI for X509Digest digests.
     * 
     * Defaults to SHA-256.
     * 
     * @return returns the digest algorithm URI
     */
    @Nonnull public String getX509DigestAlgorithmURI() {
        return options.x509DigestAlgorithmURI;
    }

    /**
     * Set the algorithm URI for X509Digest digests.
     * 
     * Defaults to SHA-256.
     * 
     * @param alg the new digest algorithmURI
     */
    public void setX509DigestAlgorithmURI(@Nonnull final String alg) {
        options.x509DigestAlgorithmURI = Constraint.isNotNull(alg, "Algorithm cannot be null");
    }    

    /**
     * Get the option to emit the entity certificate subject DN as an X509SubjectName element within X509Data.
     * 
     * @return the option value
     */
    public boolean emitX509SubjectName() {
        return options.emitX509SubjectName;
    }

    /**
     * Set the option to emit the entity certificate subject DN as an X509SubjectName element within X509Data.
     * 
     * @param newValue the new option value
     */
    public void setEmitX509SubjectName(final boolean newValue) {
        options.emitX509SubjectName = newValue;
    }

    /**
     * The set of types of subject alternative names to process.
     * 
     * Name types are represented using the constant OID tag name values defined 
     * in {@link X509Support}.
     * 
     * 
     * @return the modifiable set of alt name identifiers
     */
    @Nonnull public Set<Integer> getSubjectAltNames() {
        return options.subjectAltNames;
    }
    
    /**
     * Get the handler which process X.500 distinguished names.
     * 
     * Defaults to {@link InternalX500DNHandler}.
     * 
     * @return returns the X500DNHandler instance
     */
    @Nonnull public X500DNHandler getX500DNHandler() {
        return options.x500DNHandler;
    }

    /**
     * Set the handler which process X.500 distinguished names.
     * 
     * Defaults to {@link InternalX500DNHandler}.
     * 
     * @param handler the new X500DNHandler instance
     */
    public void setX500DNHandler(@Nonnull final X500DNHandler handler) {
        options.x500DNHandler = Constraint.isNotNull(handler, "X500DNHandler cannot be null");
    }
    
    /**
     * Get the output format specifier for X.500 subject names.
     * 
     * Defaults to RFC2253 format. The meaning of this format specifier value
     * is dependent upon the implementation of {@link X500DNHandler} which is used.
     * 
     * @return returns the format specifier
     */
    @Nullable public String getX500SubjectDNFormat() {
        return options.x500SubjectDNFormat;
    }

    /**
     * Set the output format specifier for X.500 subject names.
     * 
     * Defaults to RFC2253 format. The meaning of this format specifier value
     * is dependent upon the implementation of {@link X500DNHandler} which is used.
     * 
     * @param format the new X500DNHandler instance
     */
    public void setX500SubjectDNFormat(@Nullable final String format) {
        options.x500SubjectDNFormat = format;
    }
    
    /**
     * Get the output format specifier for X.500 issuer names.
     * 
     * Defaults to RFC2253 format. The meaning of this format specifier value
     * is dependent upon the implementation of {@link X500DNHandler} which is used.
     * 
     * @return returns the format specifier
     */
    @Nullable public String getX500IssuerDNFormat() {
        return options.x500IssuerDNFormat;
    }

    /**
     * Set the output format specifier for X.500 issuer names.
     * 
     * Defaults to RFC2253 format. The meaning of this format specifier value
     * is dependent upon the implementation of {@link X500DNHandler} which is used.
     * 
     * @param format the new X500DNHandler instance
     */
    public void setX500IssuerDNFormat(@Nullable final String format) {
        options.x500IssuerDNFormat = format;
    }

    /** {@inheritDoc} */
    @Nonnull protected X509Options getOptions() {
        return options;
    }

    /** {@inheritDoc} */
    @Nonnull protected X509Options newOptions() {
        return new X509Options();
    }

    /**
     * An implementation of {@link KeyInfoGenerator} capable of handling the information 
     * contained within a {@link X509Credential}.
     */
    public class X509KeyInfoGenerator extends BasicKeyInfoGenerator {

        /** Class logger. */
        private final Logger log = LoggerFactory.getLogger(X509KeyInfoGenerator.class);
        
        /** The set of options to be used by the generator.*/
        private X509Options options;
       
        /** Builder for X509Data objects. */
        private final XMLObjectBuilder<X509Data> x509DataBuilder;
       
        /**
         * Constructor.
         * 
         * @param newOptions the options to be used by the generator
         * @param type the KeyInfo elemet type
         */
        protected X509KeyInfoGenerator(final X509Options newOptions, final Class<? extends KeyInfo> type) {
            super(newOptions, type);
            options = newOptions;
            
            x509DataBuilder = XMLObjectProviderRegistrySupport.getBuilderFactory().ensureBuilder(
                    X509Data.DEFAULT_ELEMENT_NAME);
        }

        /** {@inheritDoc} */
        @Nullable public KeyInfo generate(@Nullable final Credential credential) throws SecurityException {
            if (credential == null) {
                log.warn("X509KeyInfoGenerator was passed a null credential");
                return null;
            } else if (!(credential instanceof X509Credential)) {
                log.warn("X509KeyInfoGenerator was passed a credential that was not an instance of X509Credential: {}",
                        credential.getClass().getName());
                return null;
            }
            final X509Credential x509Credential = (X509Credential) credential;
            
            KeyInfo keyInfo =  super.generate(credential);
            if (keyInfo == null) {
                keyInfo = buildKeyInfo();
            }
            final X509Data x509Data = x509DataBuilder.buildObject(X509Data.DEFAULT_ELEMENT_NAME);
            
            processEntityCertificate(keyInfo, x509Data, x509Credential);
            processEntityCertificateChain(keyInfo, x509Data, x509Credential);
            processCRLs(keyInfo, x509Data, x509Credential);
            
            final List<XMLObject> x509DataChildren = x509Data.getOrderedChildren();
            if (x509DataChildren != null && x509DataChildren.size() > 0) {
                keyInfo.getX509Datas().add(x509Data);
            }
            
            final List<XMLObject> keyInfoChildren = keyInfo.getOrderedChildren();
            if (keyInfoChildren != null && keyInfoChildren.size() > 0) {
                return keyInfo;
            }
            return null;
        }
        
        /** Process the value of {@link X509Credential#getEntityCertificate()}.
         * 
         * @param keyInfo the KeyInfo that is being built
         * @param x509Data the X509Data that is being built
         * @param credential the Credential that is being processed
         * @throws SecurityException thrown if the certificate data can not be encoded from the Java certificate object
         */
        protected void processEntityCertificate(@Nonnull final KeyInfo keyInfo, @Nonnull final X509Data x509Data,
                @Nonnull final X509Credential credential) throws SecurityException {
            
            final java.security.cert.X509Certificate javaCert = credential.getEntityCertificate();
            
            processCertX509DataOptions(x509Data, javaCert);
            processCertKeyNameOptions(keyInfo, javaCert);
            
            // The cert chain includes the entity cert, so don't add a duplicate
            if (options.emitEntityCertificate && !options.emitEntityCertificateChain) {
                try {
                    final X509Certificate xmlCert = KeyInfoSupport.buildX509Certificate(javaCert);
                    x509Data.getX509Certificates().add(xmlCert);
                } catch (final CertificateEncodingException e) {
                    throw new SecurityException("Error generating X509Certificate element " 
                            + "from credential's end-entity certificate", e);
                }
            }
        }
        
        /**
         * Process the options related to generation of child elements of X509Data based on certificate data.
         * 
         * @param x509Data the X509Data element being processed.
         * @param cert the certificate being processed
         * @throws SecurityException if the certificate cannot be processed
         */
        protected void processCertX509DataOptions(@Nonnull final X509Data x509Data,
                @Nonnull final java.security.cert.X509Certificate cert) throws SecurityException {
            processCertX509SubjectName(x509Data, cert);
            processCertX509IssuerSerial(x509Data, cert);
            processCertX509SKI(x509Data, cert);
            processCertX509Digest(x509Data, cert);
        }
        
        /**
         * Process the options related to generation of KeyName elements based on certificate data.
         * 
         * @param keyInfo the KeyInfo element being processed.
         * @param cert the certificate being processed
         */
        protected void processCertKeyNameOptions(@Nonnull final KeyInfo keyInfo,
                @Nonnull final java.security.cert.X509Certificate cert) {
            processSubjectDNKeyName(keyInfo, cert);
            processSubjectCNKeyName(keyInfo, cert);
            processSubjectAltNameKeyNames(keyInfo, cert);
        }
        
        /**
         * Process the options related to generation of the X509SubjectDN child element of X509Data 
         * based on certificate data.
         * 
         * @param x509Data the X509Data element being processed.
         * @param cert the certificate being processed
         */
        protected void processCertX509SubjectName(@Nonnull final X509Data x509Data,
                @Nonnull final java.security.cert.X509Certificate cert) {
            if (options.emitX509SubjectName) {
                final String subjectNameValue = getSubjectName(cert);
                if (!Strings.isNullOrEmpty(subjectNameValue)) {
                    x509Data.getX509SubjectNames().add(KeyInfoSupport.buildX509SubjectName(subjectNameValue));
                }
            }
        }
        
        /**
         * Process the options related to generation of the X509IssuerSerial child element of X509Data 
         * based on certificate data.
         * 
         * @param x509Data the X509Data element being processed.
         * @param cert the certificate being processed
         */ 
        protected void processCertX509IssuerSerial(@Nonnull final X509Data x509Data,
                @Nonnull final java.security.cert.X509Certificate cert) {
            if (options.emitX509IssuerSerial) {
                final String issuerNameValue = getIssuerName(cert);
                if (!Strings.isNullOrEmpty(issuerNameValue)) {
                    x509Data.getX509IssuerSerials().add( 
                            KeyInfoSupport.buildX509IssuerSerial(issuerNameValue, cert.getSerialNumber()));
                }
            }
        }
        
        /**
         * Process the options related to generation of the X509SKI child element of X509Data 
         * based on certificate data.
         * 
         * @param x509Data the X509Data element being processed.
         * @param cert the certificate being processed
         * @throws SecurityException  if there is an error in generating the subject key identifier
         */ 
        protected void processCertX509SKI(@Nonnull final X509Data x509Data,
                @Nonnull final java.security.cert.X509Certificate cert) throws SecurityException {
            if (options.emitX509SKI) {
                final X509SKI xmlSKI = KeyInfoSupport.buildX509SKI(cert);
                if (xmlSKI != null) {
                    x509Data.getX509SKIs().add(xmlSKI);
                }
            }
        }

        /**
         * Process the options related to generation of the X509Digest child element of X509Data 
         * based on certificate data.
         * 
         * @param x509Data the X509Data element being processed.
         * @param cert the certificate being processed
         * @throws SecurityException if certificate cannot be digested
         */ 
        protected void processCertX509Digest(@Nonnull final X509Data x509Data,
                @Nonnull final java.security.cert.X509Certificate cert) throws SecurityException {
            if (options.emitX509Digest) {
                try {
                    x509Data.getX509Digests().add(KeyInfoSupport.buildX509Digest(cert, options.x509DigestAlgorithmURI));
                } catch (final CertificateEncodingException e) {
                    throw new SecurityException("Can't digest certificate, certificate encoding error", e);
                } catch (final NoSuchAlgorithmException e) {
                    throw new SecurityException("Can't digest certificate, unsupported digest algorithm", e);
                }
            }
        }
        
        /**
         * Get subject name from a certificate, using the currently configured X500DNHandler
         * and subject DN output format.
         * 
         * @param cert the certificate being processed
         * @return the subject name
         */
        @Nullable protected String getSubjectName(@Nullable final java.security.cert.X509Certificate cert) {
            if (cert == null) {
                return null;
            } else if (!Strings.isNullOrEmpty(options.x500SubjectDNFormat)) {
                return options.x500DNHandler.getName(cert.getSubjectX500Principal(), options.x500SubjectDNFormat);
            } else {
                return options.x500DNHandler.getName(cert.getSubjectX500Principal());
            }
        }
        
        /**
         * Get issuer name from a certificate, using the currently configured X500DNHandler
         * and issuer DN output format.
         * 
         * @param cert the certificate being processed
         * @return the issuer name
         */
        @Nullable protected String getIssuerName(@Nullable final java.security.cert.X509Certificate cert) {
            if (cert == null) {
                return null;
            } else if (!Strings.isNullOrEmpty(options.x500IssuerDNFormat)) {
                return options.x500DNHandler.getName(cert.getIssuerX500Principal(), options.x500IssuerDNFormat);
            } else {
                return options.x500DNHandler.getName(cert.getIssuerX500Principal());
            }
        }

        /**
         * Process the options related to generation of KeyName elements based on the certificate's
         * subject DN value.
         * 
         * @param keyInfo the KeyInfo element being processed.
         * @param cert the certificate being processed
         */
        protected void processSubjectDNKeyName(@Nonnull final KeyInfo keyInfo,
                @Nonnull final java.security.cert.X509Certificate cert) {
            if (options.emitSubjectDNAsKeyName) {
                final String subjectNameValue = getSubjectName(cert);
                if (!Strings.isNullOrEmpty(subjectNameValue)) {
                   KeyInfoSupport.addKeyName(keyInfo, subjectNameValue); 
                }
            }
        }
        
        /**
         * Process the options related to generation of KeyName elements based on the
         * the common name field(s) of the certificate's subject DN.
         * 
         * @param keyInfo the KeyInfo element being processed.
         * @param cert the certificate being processed
         */
        protected void processSubjectCNKeyName(@Nonnull final KeyInfo keyInfo,
                @Nonnull final java.security.cert.X509Certificate cert) {
            if (options.emitSubjectCNAsKeyName) {
                final List<String> cnames = X509Support.getCommonNames(cert.getSubjectX500Principal());
                if (cnames != null) {
                    for (final String name : cnames) {
                        if (!Strings.isNullOrEmpty(name)) {
                            KeyInfoSupport.addKeyName(keyInfo, name);
                        }
                    }
                }
            }
        }
        
        /**
         * Process the options related to generation of KeyName elements based on subject
         * alternative name information within the certificate data.
         * 
         * @param keyInfo the KeyInfo element being processed.
         * @param cert the certificate being processed
         */
        protected void processSubjectAltNameKeyNames(@Nonnull final KeyInfo keyInfo,
                @Nonnull final java.security.cert.X509Certificate cert) {
            if (options.emitSubjectAltNamesAsKeyNames && options.subjectAltNames.size() > 0) {
                final Integer[] nameTypes = new Integer[ options.subjectAltNames.size() ];
                options.subjectAltNames.toArray(nameTypes);
                final List<?> altnames = X509Support.getAltNames(cert, nameTypes);
                if (altnames != null) {
                    for (final Object altNameValue : altnames) {
                        // Each returned value should either be a String or a DER-encoded byte array.
                        // See X509Certificate#getSubjectAlternativeNames for the type rules.
                        if (altNameValue instanceof String) {
                            KeyInfoSupport.addKeyName(keyInfo, (String) altNameValue);
                        } else if (altNameValue instanceof byte[]){
                            log.warn("Certificate contained an alt name value as a DER-encoded byte[] (not supported)");
                        } else {
                            log.warn("Certificate contained an alt name value with an unexpected type: {}",
                                    altNameValue.getClass().getName());
                        }
                    }
                }
            }
        }
        
        /** Process the value of {@link X509Credential#getEntityCertificateChain()}.
         * 
         * @param keyInfo the KeyInfo that is being built
         * @param x509Data the X509Data that is being built
         * @param credential the Credential that is being processed
         * @throws SecurityException thrown if the certificate data can not be encoded from the Java certificate object
         */
        protected void processEntityCertificateChain(@Nonnull final KeyInfo keyInfo, @Nonnull final X509Data x509Data,
                @Nonnull final X509Credential credential) throws SecurityException {
            
            if (options.emitEntityCertificateChain) {
                for (final java.security.cert.X509Certificate javaCert : credential.getEntityCertificateChain()) {
                    try {
                        final X509Certificate xmlCert = KeyInfoSupport.buildX509Certificate(javaCert);
                        x509Data.getX509Certificates().add(xmlCert);
                    } catch (final CertificateEncodingException e) {
                        throw new SecurityException("Error generating X509Certificate element " 
                                + "from a certificate in credential's certificate chain", e);
                    }
                }
            }
        }

        /** Process the value of {@link X509Credential#getCRLs()}.
         * 
         * @param keyInfo the KeyInfo that is being built
         * @param x509Data the X509Data that is being built
         * @param credential the Credential that is being processed
         * @throws SecurityException thrown if the CRL data can not be encoded from the Java certificate object
         */
        protected void processCRLs(@Nonnull final KeyInfo keyInfo, @Nonnull final X509Data x509Data,
                @Nonnull final X509Credential credential) throws SecurityException {
            
            if (options.emitCRLs && credential.getCRLs() != null) {
                final Collection<java.security.cert.X509CRL> crls = credential.getCRLs();
                if (crls != null) {
                    for (final java.security.cert.X509CRL javaCRL : crls) {
                        try {
                            final X509CRL xmlCRL = KeyInfoSupport.buildX509CRL(javaCRL);
                            x509Data.getX509CRLs().add(xmlCRL);
                        } catch (final CRLException e) {
                            throw new SecurityException("Error generating X509CRL element " 
                                    + "from a CRL in credential's CRL list", e);
                        }
                    }
                }
            }
        }
        
    }
    
    /**
    * Options to be used in the production of a {@link KeyInfo} from an {@link X509Credential}.
    */
   protected class X509Options extends BasicOptions {
       
       /** Emit the entity certificate as an X509Certificate element within X509Data. */
       private boolean emitEntityCertificate;
       
       /** Emit the entity certificate chain as sequence of X509Certificate elements within X509Data. */
       private boolean emitEntityCertificateChain;
       
       /** Emit the CRL list as sequence of X509CRL elements within X509Data. */
       private boolean emitCRLs;
       
       /** Emit the entity certificate subject DN as an X509SubjectName element within X509Data. */
       private boolean emitX509SubjectName;
       
       /** Emit the entity certificate issuer name and serial number as an X509IssuerSerial element within X509Data. */
       private boolean emitX509IssuerSerial;
       
       /** Emit the entity certificate subject key identifier as an X509SKI element within X509Data. */
       private boolean emitX509SKI;

       /** Emit the entity certificate digest as an X509Digest element within X509Data. */
       private boolean emitX509Digest;
       
       /** X509Digest digest algorithm URI. */
       @Nonnull private String x509DigestAlgorithmURI;
       
       /** Emit the entity certificate subject DN as a KeyName element. */
       private boolean emitSubjectDNAsKeyName;
       
       /** Emit the entity certificate subject DN common name (CN) fields as KeyName elements. */
       private boolean emitSubjectCNAsKeyName;
       
       /** Emit the entity certificate subject alternative name extension values as KeyName elements. */
       private boolean emitSubjectAltNamesAsKeyNames;
       
       /** The set of types of subject alternative names to process. */
       @Nonnull private Set<Integer> subjectAltNames;
       
       /**
        * Responsible for parsing and serializing X.500 names to/from
        * {@link javax.security.auth.x500.X500Principal} instances.
        */
       @Nonnull private X500DNHandler x500DNHandler;
       
       /** The format specifier for outputting X.500 subject names. */
       private String x500SubjectDNFormat;
       
       /** The format specifier for outputting X.500 issuer names. */
       private String x500IssuerDNFormat;
       
       /** Constructor. */
       protected X509Options() {
           x509DigestAlgorithmURI = EncryptionConstants.ALGO_ID_DIGEST_SHA256;
           subjectAltNames = new LazySet<>();
           x500DNHandler = new InternalX500DNHandler();
           x500SubjectDNFormat = X500DNHandler.FORMAT_RFC2253;
           x500IssuerDNFormat = X500DNHandler.FORMAT_RFC2253;
       }
       
       /** {@inheritDoc} */
       protected X509Options clone() {
           final X509Options clonedOptions = (X509Options) super.clone();
           
           clonedOptions.subjectAltNames = new LazySet<>();
           clonedOptions.subjectAltNames.addAll(subjectAltNames);
           
           clonedOptions.x500DNHandler = x500DNHandler.clone();
           
           return clonedOptions;
       }
       
   }

}