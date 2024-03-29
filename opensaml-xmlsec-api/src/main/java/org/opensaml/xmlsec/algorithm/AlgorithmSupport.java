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

package org.opensaml.xmlsec.algorithm;

import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialSupport;
import org.opensaml.security.crypto.KeySupport;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.slf4j.Logger;

import com.google.common.base.Strings;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Helper methods for working with XML security algorithm URI's.
 */
public final class AlgorithmSupport {
    
    /** Logger. */
    @Nonnull private static final Logger LOG = LoggerFactory.getLogger(AlgorithmSupport.class);
    
    /** Constructor. */
    private AlgorithmSupport() {
        
    }
    
    /**
     * Get the global {@link AlgorithmRegistry} instance.
     * 
     * @return the global algorithm registry, or null if nothing registered
     */
    @Nullable public static AlgorithmRegistry getGlobalAlgorithmRegistry() {
        return ConfigurationService.get(AlgorithmRegistry.class);
    }
    
    /**
     * Get the global {@link AlgorithmRegistry} instance, raising an exception if absent.
     * 
     * @return the global algorithm registry
     * 
     * @since 5.0.0
     */
    @Nonnull public static AlgorithmRegistry ensureGlobalAlgorithmRegistry() {
        return ConfigurationService.ensure(AlgorithmRegistry.class);
    }
    
    /**
     * Check whether the supplied descriptor represents an algorithm that my be used for
     * key encryption, i.e. a key transport or symmetric key wrap algorithm.
     * 
     * @param algorithm the algorithm descriptor to evaluate
     * @return true if the algorithm may be used for key encryption, false otherwise
     */
    public static boolean isKeyEncryptionAlgorithm(@Nullable final AlgorithmDescriptor algorithm) {    
        if (algorithm == null) {
            return false;
        }
        
        switch(algorithm.getType()) {
            case KeyTransport:
            case SymmetricKeyWrap:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Check whether the supplied descriptor represents an algorithm that my be used for
     * data encryption, i.e. a block encryption algorithm.
     * 
     * @param algorithm the algorithm descriptor to evaluate
     * @return true if the algorithm may be used for key encryption, false otherwise
     */
    public static boolean isDataEncryptionAlgorithm(@Nullable final AlgorithmDescriptor algorithm) {
        if (algorithm == null) {
            return false;
        }
        
        switch(algorithm.getType()) {
            case BlockEncryption:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Check whether the supplied credential may be used with the supplied algorithm for the purpose of
     * signing.  
     * 
     * <p>
     * This checks the consistency of the type of credential signing key and the algorithm type, as well
     * as the key algorithm and length where applicable.
     * </p>
     * 
     * @param credential the candidate signing credential to evaluate
     * @param algorithm the candidate signing algorithm to evaluate
     * @return true if the credential may be used with the algorithm for signing, false otherwise
     */
    public static boolean credentialSupportsAlgorithmForSigning(@Nullable final Credential credential, 
            @Nullable final AlgorithmDescriptor algorithm) {
        if (credential == null || algorithm == null) {
            return false;
        }
        
        final Key key = CredentialSupport.extractSigningKey(credential);
        if (key == null) {
            return false;
        }
        
        switch(algorithm.getType()) {
            case Signature:
                if (!(key instanceof PrivateKey)) {
                    return false;
                }
                break;
            case Mac:
                if (!(key instanceof SecretKey)) {
                    return false;
                }
                break;
            default:
                return false;
        }
        
        return checkKeyAlgorithmAndLength(key, algorithm);
    }

    /**
     * Check whether the supplied credential may be used with the supplied algorithm for the purpose of
     * encryption.  
     * 
     * <p>
     * This checks the consistency of the extracted credential encryption key and the algorithm type, as well
     * as the key algorithm and length where applicable.
     * </p>
     * 
     * @param credential the candidate encryption credential to evaluate
     * @param algorithm the candidate encryption algorithm to evaluate
     * @return true if the credential may be used with the algorithm for encryption, false otherwise
     */
    public static boolean credentialSupportsAlgorithmForEncryption(@Nullable final Credential credential, 
            @Nullable final AlgorithmDescriptor algorithm) {
        if (credential == null || algorithm == null) {
            return false;
        }
        
        final Key key = CredentialSupport.extractEncryptionKey(credential);
        if (key == null) {
            return false;
        }
        
        switch(algorithm.getType()) {
            case KeyTransport:
                if (!(key instanceof PublicKey)) {
                    return false;
                }
                break;
            case BlockEncryption:
            case SymmetricKeyWrap:
                if (!(key instanceof SecretKey)) {
                    return false;
                }
                break;
            default:
                return false;
        }
        
        return checkKeyAlgorithmAndLength(key, algorithm);
    }
    
    /**
     * Check that the supplied key is consistent with the supplied algorithm's specified key algorithm and key length,
     * where applicable.
     * 
     * @param key the key to evaluate
     * @param algorithm the algorithm to evaluate
     * @return true if the key is consistent with key algorithm and length specified by the algorithm (if any)
     *          false otherwise
     */
    public static boolean checkKeyAlgorithmAndLength(@Nonnull final Key key, 
            @Nonnull final AlgorithmDescriptor algorithm) {
        
        if (algorithm instanceof KeySpecifiedAlgorithm) {
            final String specifiedKey = ((KeySpecifiedAlgorithm)algorithm).getKey();
            if (!specifiedKey.equals(key.getAlgorithm())) {
                return false;
            }
        }
        
        if (algorithm instanceof KeyLengthSpecifiedAlgorithm) {
            final Integer specifiedKeyLength = ((KeyLengthSpecifiedAlgorithm)algorithm).getKeyLength();
            if (!specifiedKeyLength.equals(KeySupport.getKeyLength(key))) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Get the Java security JCA/JCE algorithm identifier associated with an algorithm URI.
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * @return the Java algorithm identifier, or null if the mapping is unavailable or indeterminable from the URI
     */
    @Nullable public static String getAlgorithmID(@Nonnull final String algorithmURI) {
        final AlgorithmRegistry registry = getGlobalAlgorithmRegistry();
        if (registry != null){
            final AlgorithmDescriptor descriptor = registry.get(algorithmURI);
            if (descriptor != null) {
                return descriptor.getJCAAlgorithmID();
            }
        }
        return null;
    }

    /**
     * Check whether the key transport encryption algorithm URI indicates RSA-OAEP.
     * 
     * @param keyTransportAlgorithm the key transport encryption algorithm URI
     * @return true if URI indicates RSA-OAEP, false otherwise
     */
    public static boolean isRSAOAEP(@Nonnull final String keyTransportAlgorithm) {
        return EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP.equals(keyTransportAlgorithm)
                || EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11.equals(keyTransportAlgorithm);
    }
    
    /**
     * Check whether the algorithm URI indicates block encryption.
     * 
     * @param algorithm the algorithm URI
     * @return true if URI indicates symmetric key wrap, false otherwise
     */
    public static boolean isBlockEncryption(@Nonnull final String algorithm) {
        final AlgorithmRegistry registry = getGlobalAlgorithmRegistry();
        if (registry != null){
            final AlgorithmDescriptor descriptor = registry.get(algorithm);
            if (descriptor != null) {
                return descriptor.getType().equals(AlgorithmDescriptor.AlgorithmType.BlockEncryption);
            }
        }
        return false;
    }
    
    /**
     * Check whether the algorithm URI indicates symmetric key wrap.
     * 
     * @param algorithm the algorithm URI
     * @return true if URI indicates symmetric key wrap, false otherwise
     */
    public static boolean isSymmetricKeyWrap(@Nonnull final String algorithm) {
        final AlgorithmRegistry registry = getGlobalAlgorithmRegistry();
        if (registry != null){
            final AlgorithmDescriptor descriptor = registry.get(algorithm);
            if (descriptor != null) {
                return descriptor.getType().equals(AlgorithmDescriptor.AlgorithmType.SymmetricKeyWrap);
            }
        }
        return false;
    }
    
    /**
     * Check whether the signature method algorithm URI indicates HMAC.
     * 
     * @param signatureAlgorithm the signature method algorithm URI
     * @return true if URI indicates HMAC, false otherwise
     */
    public static boolean isHMAC(@Nonnull final String signatureAlgorithm) {
        final AlgorithmRegistry registry = getGlobalAlgorithmRegistry();
        if (registry != null){
            final AlgorithmDescriptor descriptor = registry.get(signatureAlgorithm);
            if (descriptor != null) {
                return descriptor.getType().equals(AlgorithmDescriptor.AlgorithmType.Mac);
            }
        }
        return false;
    }
    
    /**
     * Get the Java security JCA/JCE key algorithm specifier associated with an algorithm URI.
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * @return the Java key algorithm specifier, or null if the mapping is unavailable or indeterminable from the URI
     */
    @Nullable public static String getKeyAlgorithm(@Nonnull final String algorithmURI) {
        final AlgorithmRegistry registry = getGlobalAlgorithmRegistry();
        if (registry != null){
            final AlgorithmDescriptor descriptor = registry.get(algorithmURI);
            if (descriptor != null && descriptor instanceof KeySpecifiedAlgorithm) {
                return ((KeySpecifiedAlgorithm)descriptor).getKey();
            }
        }
        return null;
    }

    /**
     * Get the length of the key indicated by the algorithm URI, if applicable and available.
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * @return the length of the key indicated by the algorithm URI, or null if the length is either unavailable or
     *         indeterminable from the URI
     */
    @Nullable public static Integer getKeyLength(@Nonnull final String algorithmURI) {
        final AlgorithmRegistry registry = getGlobalAlgorithmRegistry();
        if (registry != null){
            final AlgorithmDescriptor descriptor = registry.get(algorithmURI);
            if (descriptor != null && descriptor instanceof KeyLengthSpecifiedAlgorithm) {
                return ((KeyLengthSpecifiedAlgorithm)descriptor).getKeyLength();
            }
        }
    
        LOG.debug("Mapping from algorithm URI {} to key length not available", algorithmURI);
        return null;
    }

    /**
     * Generates a random Java JCE symmetric Key object from the specified XML Encryption algorithm URI.
     * 
     * @param algoURI The XML Encryption algorithm URI
     * @return a randomly-generated symmetric Key
     * @throws NoSuchAlgorithmException thrown if the specified algorithm is invalid
     * @throws KeyException thrown if the length of the key to generate could not be determined
     */
    @Nonnull public static SecretKey generateSymmetricKey(@Nonnull final String algoURI)
            throws NoSuchAlgorithmException, KeyException {
        final String jceAlgorithmName = getKeyAlgorithm(algoURI);
        if (Strings.isNullOrEmpty(jceAlgorithmName)) {
            LOG.error("Mapping from algorithm URI '" + algoURI
                    + "' to key algorithm not available, key generation failed");
            throw new NoSuchAlgorithmException("Algorithm URI'" + algoURI + "' is invalid for key generation");
        }
        
        Integer keyLength = null;
        switch(algoURI) {
            case EncryptionConstants.ALGO_ID_BLOCKCIPHER_TRIPLEDES:
            case EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES:
                // We have to special case this b/c a 3DES key is 192 bits, but with KeyGenerator the JCA providers
                // inconsistently allow either 112/168 (SunJCE) or 112/168/192 (BC). Per JCA docs they're all 
                // required to support 168. We don't do this in getKeyLength() b/c the 3DES key actually is 192 bits.
                keyLength = 168;
                break;
            default:
                keyLength = getKeyLength(algoURI);
        }
        
        if (keyLength == null) {
            LOG.error("Key length could not be determined from algorithm URI, can't generate key");
            throw new KeyException("Key length not determinable from algorithm URI, could not generate new key");
        }
        final KeyGenerator keyGenerator = KeyGenerator.getInstance(jceAlgorithmName);
        keyGenerator.init(keyLength);
        return keyGenerator.generateKey();
    }

    /**
     * Randomly generates a Java JCE KeyPair object from the specified XML Encryption algorithm URI.
     * 
     * @param algoURI  The XML Encryption algorithm URI
     * @param keyLength  the length of key to generate
     * @return a randomly-generated KeyPair
     * @throws NoSuchProviderException  provider not found
     * @throws NoSuchAlgorithmException  algorithm not found
     */
    @Nonnull public static KeyPair generateKeyPair(@Nonnull final String algoURI, final int keyLength) 
            throws NoSuchAlgorithmException, NoSuchProviderException {
        final String jceAlgorithmName = getKeyAlgorithm(algoURI);
        if (jceAlgorithmName == null) {
            throw new NoSuchAlgorithmException("No JCE algorithm found for " + algoURI);
        }
        return KeySupport.generateKeyPair(jceAlgorithmName, keyLength, null);
    }

    /**
     * Randomly generates a Java JCE KeyPair object from the specified XML Encryption algorithm URI.
     * 
     * @param algoURI  The XML Encryption algorithm URI
     * @param paramSpec the algorithm parameter specification
     * @return a randomly-generated KeyPair
     * @throws NoSuchProviderException  provider not found
     * @throws NoSuchAlgorithmException  algorithm not found
     * @throws InvalidAlgorithmParameterException algorithm parameter spec is unsupported
     */
    @Nonnull public static KeyPair generateKeyPair(@Nonnull final String algoURI,
            @Nonnull final AlgorithmParameterSpec paramSpec)
                    throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        final String jceAlgorithmName = getKeyAlgorithm(algoURI);
        if (jceAlgorithmName == null) {
            throw new NoSuchAlgorithmException("No JCE algorithm found for " + algoURI);
        }
        return KeySupport.generateKeyPair(jceAlgorithmName, paramSpec, null);
    }

    /**
     * Generate a random symmetric key and return in a BasicCredential.
     * 
     * @param algorithmURI The XML Encryption algorithm URI
     * @return a basic credential containing a randomly generated symmetric key
     * @throws KeyException thrown if the length of key to generate could not be determined
     * @throws NoSuchAlgorithmException algorithm not found
     */
    @Nonnull public static Credential generateSymmetricKeyAndCredential(@Nonnull final String algorithmURI) 
            throws NoSuchAlgorithmException, KeyException {
        final SecretKey key = generateSymmetricKey(algorithmURI);
        return new BasicCredential(key);
    }

    /**
     * Generate a random asymmetric key pair and return in a BasicCredential.
     * 
     * @param algorithmURI The XML Encryption algorithm URI
     * @param keyLength key length
     * @param includePrivate if true, the private key will be included as well
     * @return a basic credential containing a randomly generated asymmetric key pair
     * @throws NoSuchAlgorithmException algorithm not found
     * @throws NoSuchProviderException provider not found
     */
    @Nonnull public static Credential generateKeyPairAndCredential(@Nonnull final String algorithmURI,
            final int keyLength,
            final boolean includePrivate) throws NoSuchAlgorithmException, NoSuchProviderException {
        final KeyPair keyPair = generateKeyPair(algorithmURI, keyLength);
        final BasicCredential credential = new BasicCredential(keyPair.getPublic());
        if (includePrivate) {
            credential.setPrivateKey(keyPair.getPrivate());
        }
        return credential;
    }
    
    /**
     * Generate a random asymmetric key pair and return in a BasicCredential.
     * 
     * @param algorithmURI The XML Encryption algorithm URI
     * @param paramSpec the algorithm parameter specification
     * @param includePrivate if true, the private key will be included as well
     * @return a basic credential containing a randomly generated asymmetric key pair
     * @throws NoSuchAlgorithmException algorithm not found
     * @throws NoSuchProviderException provider not found
     * @throws InvalidAlgorithmParameterException algorithm parameter spec is unsupported
     */
    @Nonnull public static Credential generateKeyPairAndCredential(@Nonnull final String algorithmURI,
            @Nonnull final AlgorithmParameterSpec paramSpec,
            final boolean includePrivate)
                    throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        final KeyPair keyPair = generateKeyPair(algorithmURI, paramSpec);
        final BasicCredential credential = new BasicCredential(keyPair.getPublic());
        if (includePrivate) {
            credential.setPrivateKey(keyPair.getPrivate());
        }
        return credential;
    }
    
    /**
     * Validate the supplied algorithm URI against the specified includes and excludes.
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * @param includedAlgorithmURIs the algorithm include list
     * @param excludedAlgorithmURIs the algorithm exclude list
     * 
     * @return true if algorithm URI satisfies the specified includes and excludes, otherwise false
     */
    public static boolean validateAlgorithmURI(@Nonnull final String algorithmURI, 
            @Nullable final Collection<String> includedAlgorithmURIs,
            @Nullable final Collection<String> excludedAlgorithmURIs) {
        
        if (excludedAlgorithmURIs != null) {
            LOG.debug("Saw non-null algorithm exclude list: {}", excludedAlgorithmURIs);
            if (excludedAlgorithmURIs.contains(algorithmURI)) {
                LOG.warn("Algorithm failed exclude list validation: {}", algorithmURI);
                return false;
            }
            LOG.debug("Algorithm passed exclude list validation: {}", algorithmURI);
        } else {
            LOG.debug("Saw null algorithm exclude list, nothing to evaluate");
        }
        
        if (includedAlgorithmURIs != null) {
            LOG.debug("Saw non-null algorithm include list: {}", includedAlgorithmURIs);
            if (!includedAlgorithmURIs.isEmpty()) {
                if (!includedAlgorithmURIs.contains(algorithmURI)) {
                    LOG.warn("Algorithm failed include list validation: {}", algorithmURI);
                    return false;
                }
                LOG.debug("Algorithm passed include list validation: {}", algorithmURI);
            } else {
               LOG.debug("Non-null algorithm include list was empty, skipping evaluation");
            }
        } else {
            LOG.debug("Saw null algorithm include list, nothing to evaluate");
        }
        
        return true;
    }

}