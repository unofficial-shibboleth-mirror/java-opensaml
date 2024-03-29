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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.xmlsec.algorithm.AlgorithmDescriptor.AlgorithmType;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.slf4j.Logger;

import com.google.common.base.MoreObjects;

/**
 * A registry of {@link AlgorithmDescriptor} instances, to support various use cases for working with algorithm URIs.
 */
public class AlgorithmRegistry {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(AlgorithmRegistry.class);
    
    /** Map of registered algorithm descriptors. */
    @Nonnull private Map<String, AlgorithmDescriptor> descriptors;
    
    /** Index of registered AlgorithmType to algorithm URI. */
    @Nonnull private Map<AlgorithmType, Set<String>> types;

    /** Set containing algorithms which are supported by the runtime environment. */
    @Nonnull private Set<String> runtimeSupported;
    
    /** Index of digest type to AlgorithmDescriptor. */
    @Nonnull private Map<String, DigestAlgorithm> digestAlgorithms;
    
    /** Index of (KeyType,DigestType) to AlgorithmDescriptor. */
    @Nonnull private Map<SignatureAlgorithmIndex, SignatureAlgorithm> signatureAlgorithms;
    
    /** Constructor. */
    public  AlgorithmRegistry() {
        descriptors = new HashMap<>();
        types = new HashMap<>();
        runtimeSupported = new HashSet<>();
        digestAlgorithms = new HashMap<>();
        signatureAlgorithms = new HashMap<>();
    }
    
    /**
     * Get the algorithm descriptor instance associated with the specified algorithm URI.
     * @param algorithmURI the algorithm URI to resolve
     * 
     * @return the resolved algorithm descriptor or null
     */
    @Nullable public AlgorithmDescriptor get(@Nullable final String algorithmURI) {
        final String trimmed = StringSupport.trimOrNull(algorithmURI);
        if (trimmed == null) {
            return null;
        }
        
        return descriptors.get(trimmed);
    }
    
    /**
     * Retrieve indication of whether the runtime environment supports the algorithm. 
     * 
     * <p>
     * This evaluation is performed dynamically when the algorithm is registered.
     * </p> 
     * 
     * @param algorithmURI the algorithm URI to evaluate
     * 
     * @return true if the algorithm is supported by the current runtime environment, false otherwise
     */
    public boolean isRuntimeSupported(@Nullable final String algorithmURI) {
        final String trimmed = StringSupport.trimOrNull(algorithmURI);
        if (trimmed == null) {
            log.debug("Runtime support failed, algorithm URI was null or empty");
            return false;
        }
        
        final boolean supported = runtimeSupported.contains(trimmed);
        log.debug("Runtime support eval for algorithm URI '{}': {}", trimmed, supported ? "supported" : "unsupported");
        return supported;
    }
    
    /**
     * Clear all registered algorithms.
     */
    public void clear() {
        descriptors.clear();
        runtimeSupported.clear();
        digestAlgorithms.clear();
        signatureAlgorithms.clear();
    }
    
    /**
     * Register an algorithm.
     * 
     * @param descriptor the algorithm
     */
    public void register(@Nonnull final AlgorithmDescriptor descriptor) {
        Constraint.isNotNull(descriptor, "AlgorithmDescriptor was null");
        
        log.debug("Registering algorithm descriptor with URI: {}", descriptor.getURI());
        
        final AlgorithmDescriptor old = descriptors.get(descriptor.getURI());
        if (old != null) {
            log.debug("Registry contained existing descriptor with URI, removing old instance and re-registering: {}",
                    descriptor.getURI());
            deindex(old);
            deregister(old);
        }
        descriptors.put(descriptor.getURI(), descriptor);
        index(descriptor);
    }

    /**
     * Deregister an algorithm.
     * 
     * @param descriptor the algorithm
     */
    public void deregister(@Nonnull final AlgorithmDescriptor descriptor) {
        Constraint.isNotNull(descriptor, "AlgorithmDescriptor was null");
        if (descriptors.containsKey(descriptor.getURI())) {
            deindex(descriptor);
            descriptors.remove(descriptor.getURI());
        } else {
            log.debug("Registry did not contain descriptor with URI, nothing to do: {}", descriptor.getURI());
        }
    }
    
    /**
     * Deregister an algorithm.
     * 
     * @param uri the algorithm URI
     */
    public void deregister(@Nonnull final String uri) {
        Constraint.isNotNull(uri, "AlgorithmDescriptor URI was null");
        final AlgorithmDescriptor descriptor = get(uri);
        if (descriptor != null) {
            deregister(descriptor);
        }
    }
    
    /**
     * Lookup a digest method algorithm descriptor by the JCA digest method ID.
     * 
     * @param digestMethod the JCA digest method ID.
     * 
     * @return the algorithm descriptor, or null
     */
    @Nullable public DigestAlgorithm getDigestAlgorithm(@Nonnull final String digestMethod) {
        Constraint.isNotNull(digestMethod, "Digest method was null");
        return digestAlgorithms.get(digestMethod);
    }
    
    /**
     * Lookup a signature algorithm descriptor by the JCA key algorithm and digest method IDs.
     * 
     * @param keyType the JCA key algorithm ID.
     * @param digestMethod the JCA digest method ID.
     * 
     * @return the algorithm descriptor, or null
     * 
     * @deprecated Use instead {@link #getSignatureAlgorithms(String, String)}
     */
    @Deprecated
    @Nullable public SignatureAlgorithm getSignatureAlgorithm(@Nonnull final String keyType, 
            @Nonnull final String digestMethod) {
        Constraint.isNotNull(keyType, "Key type was null");
        Constraint.isNotNull(digestMethod, "Digest type was null");
        return signatureAlgorithms.get(new SignatureAlgorithmIndex(keyType, digestMethod));
    }

    /**
     * Lookup signature algorithm descriptors by the JCA key algorithm and digest method IDs.
     * 
     * @param keyType the JCA key algorithm ID.
     * @param digestMethod the JCA digest method ID.
     * 
     * @return the list of matching algorithm descriptors, possibly empty
     */
    @Nonnull public Set<SignatureAlgorithm> getSignatureAlgorithms(@Nonnull final String keyType, 
            @Nonnull final String digestMethod) {
        Constraint.isNotNull(keyType, "Key type was null");
        Constraint.isNotNull(digestMethod, "Digest type was null");
        return getRegisteredByType(AlgorithmType.Signature).stream()
                .map(SignatureAlgorithm.class::cast)
                .filter(alg -> alg.getKey().equals(keyType) && alg.getDigest().equals(digestMethod))
                .collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableSet())).get();
    }

    /**
     * Get the set of algorithm URIs registered for the given type.
     *
     * @param type the algorithm type
     *
     * @return the set of URIs for the given type, may be empty
     */
    @Nonnull @Unmodifiable @NotLive public Set<String> getRegisteredURIsByType(@Nonnull final AlgorithmType type) {
        Constraint.isNotNull(type, "AlgorithmType was null");
        final Set<String> byType = types.get(type);
        if (byType != null) {
            return CollectionSupport.copyToSet(byType);
        }
        return CollectionSupport.emptySet();
    }

    /**
     * Get the set of {@link AlgorithmDescriptor} registered for the given type.
     *
     * @param type the algorithm type
     *
     * @return the set of descriptors for the given type, may be empty
     */
    @Nonnull @Unmodifiable @NotLive
    public Set<AlgorithmDescriptor> getRegisteredByType(@Nonnull final AlgorithmType type) {
        return getRegisteredURIsByType(type).stream()
                .map(this::get)
                .filter(Objects::nonNull)
                .collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableSet())).get();
    }

    /**
     * Add the algorithm descriptor to the indexes which support the various lookup methods 
     * available via the registry's API.
     * 
     * @param descriptor the algorithm
     */
    private void index(@Nonnull final AlgorithmDescriptor descriptor) {
        Set<String> byType = types.get(descriptor.getType());
        if (byType == null) {
            byType = new HashSet<>();
            types.put(descriptor.getType(), byType);
        }
        byType.add(descriptor.getURI());

        if (checkRuntimeSupports(descriptor)) {
            runtimeSupported.add(descriptor.getURI());
        } else {
            log.info("Algorithm failed runtime support check, will not be usable: {}", descriptor.getURI());
            // Just for good measure, for case where environment has changed 
            // and algorithm is being re-registered.
            runtimeSupported.remove(descriptor.getURI());
        }
        
        if (descriptor instanceof DigestAlgorithm digest) {
            digestAlgorithms.put(digest.getJCAAlgorithmID(), digest);
        }
        if (descriptor instanceof SignatureAlgorithm sigAlg) {
            signatureAlgorithms.put(new SignatureAlgorithmIndex(sigAlg.getKey(), sigAlg.getDigest()), sigAlg);
        }
    }
    
    /**
     * Remove the algorithm descriptor from the indexes which support the various lookup methods 
     * available via the registry's API.
     * 
     * @param descriptor the algorithm
     */
    private void deindex(@Nonnull final AlgorithmDescriptor descriptor) {
        final Set<String> byType = types.get(descriptor.getType());
        if (byType != null) {
            byType.remove(descriptor.getURI());
        }

        runtimeSupported.remove(descriptor.getURI());
        
        if (descriptor instanceof DigestAlgorithm digest) {
            digestAlgorithms.remove(digest.getJCAAlgorithmID());
        }
        if (descriptor instanceof SignatureAlgorithm sigAlg) {
            signatureAlgorithms.remove(new SignatureAlgorithmIndex(sigAlg.getKey(), sigAlg.getDigest()));
        }
    }
    
    /**
     * Evaluate whether the algorithm is supported by the current runtime environment.
     * 
     * @param descriptor the algorithm
     * 
     * @return true if runtime supports the algorithm, false otherwise
     */
    // Checkstyle: CyclomaticComplexity OFF
    private boolean checkRuntimeSupports(@Nonnull final AlgorithmDescriptor descriptor) {
        
        try {
            switch(descriptor.getType()) {
                case BlockEncryption:
                case KeyTransport:
                case SymmetricKeyWrap:
                    Cipher.getInstance(descriptor.getJCAAlgorithmID());
                    if (!checkCipherSupportedKeyLength(descriptor)) {
                        return false;
                    }
                    break;
                    
                case Signature:
                    Signature.getInstance(descriptor.getJCAAlgorithmID());
                    // Have to special case and test the implicit digest method separately,
                    // since the Santuario and hence AlgorithmDescriptor methodology of "RSASSA-PSS"
                    // doesn't include the digest explicitly like the others do.  See OSJ-272 and OSJ-388.
                    if (JCAConstants.SIGNATURE_RSA_SSA_PSS.equals(descriptor.getJCAAlgorithmID())) {
                        MessageDigest.getInstance(SignatureAlgorithm.class.cast(descriptor).getDigest());
                    }
                    break;
                    
                case Mac:
                    Mac.getInstance(descriptor.getJCAAlgorithmID());
                    break;
                    
                case MessageDigest:
                    MessageDigest.getInstance(descriptor.getJCAAlgorithmID());
                    break;
                    
                case KeyAgreement:
                    KeyAgreement.getInstance(descriptor.getJCAAlgorithmID());
                    break;
                    
                default:
                    log.info("Saw unknown AlgorithmDescriptor type, failing runtime support check: {}",
                            descriptor.getClass().getName());
                    return false;
                
            }
        } catch (final NoSuchAlgorithmException | NoSuchPaddingException e) {
            if (!checkSpecialCasesRuntimeSupport(descriptor)) {
                log.debug(String.format("AlgorithmDescriptor failed runtime support check: %s", 
                        descriptor.getURI()), e);
                return false;
            }
        } catch (final Throwable t) {
            log.error("Fatal error evaluating algorithm runtime support", t);
            return false;
        }
        
        return true;
    }
    // Checkstyle: CyclomaticComplexity ON
    
    /**
     * Check if the key length of the specified {@link Cipher}-based algorithm, if known, is 
     * supported by the current runtime.
     * 
     * @param descriptor the algorithm
     * @return true if key length supported, false otherwise
     * @throws NoSuchAlgorithmException if the associated JCA algorithm is not supported by the runtime
     */
    private boolean checkCipherSupportedKeyLength(@Nonnull final AlgorithmDescriptor descriptor)
            throws NoSuchAlgorithmException {
        if (descriptor instanceof KeyLengthSpecifiedAlgorithm) {
            final int algoLength = ((KeyLengthSpecifiedAlgorithm)descriptor).getKeyLength();
            final int cipherMaxLength = Cipher.getMaxAllowedKeyLength(descriptor.getJCAAlgorithmID());
            if (algoLength > cipherMaxLength) {
                log.info("Cipher algorithm '{}' is not supported, its key length {} exceeds Cipher max key length {}",
                        descriptor.getURI(), algoLength, cipherMaxLength);
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check for special cases of runtime support which failed the initial simple service class load check.
     * 
     * @param descriptor the algorithm
     * 
     * @return true if algorithm is supported by the runtime environment, false otherwise
     */
    private boolean checkSpecialCasesRuntimeSupport(@Nonnull final AlgorithmDescriptor descriptor) {
        log.trace("Checking runtime support failure for special cases: {}", descriptor.getURI());
        try {
            // Per Santuario XMLCipher: Some JDKs don't support RSA/ECB/OAEPPadding.
            // So check specifically for OAEPPadding with explicit SHA-1 digest and MGF1.
            if (EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP.equals(descriptor.getURI())) {
                Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
                log.trace("RSA OAEP algorithm passed as special case with OAEPWithSHA1AndMGF1Padding");
                return true;
            }
            
        } catch (final NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.trace("Special case eval for algorithm failed with exception", e);
        }
        
        log.trace("Algorithm was not supported by any special cases: {}", descriptor.getURI());
        return false;
    }

    /**
     * Class used as index key for signature algorithm lookup.
     */
    protected class SignatureAlgorithmIndex {
        
        /** Key type. */
        @Nonnull private String key;
        
        /** Digest type. */
        @Nonnull private String digest;
        
        /**
         * Constructor.
         *
         * @param keyType the key type
         * @param digestType the digest type
         */
        public SignatureAlgorithmIndex(@Nonnull final String keyType, @Nonnull final String digestType) {
            key = Constraint.isNotNull(StringSupport.trim(keyType), "Key type was null");
            digest = Constraint.isNotNull(StringSupport.trim(digestType), "Digest type was null");
        }
        
        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int result = 17;  
            result = 37*result + key.hashCode();
            result = 37*result + digest.hashCode();
            return result;  
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            
            if (obj instanceof SignatureAlgorithmIndex) {
               final SignatureAlgorithmIndex other = (SignatureAlgorithmIndex) obj; 
               return Objects.equals(key, other.key) && Objects.equals(digest, other.digest);
            }
            
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                .add("Key", key)
                .add("Digest", digest).toString();
        }
        
    }

}
