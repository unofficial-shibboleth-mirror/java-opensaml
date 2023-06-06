/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensaml.xmlsec.agreement;

import java.security.PublicKey;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.encryption.EncryptedType;
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.KeySize;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Support for key agreement operations.
 */
public final class KeyAgreementSupport {
    
    /** JCA key algorithms that support key agreement. */
    @Nonnull public static final Set<String> KEY_ALGORITHMS =
            CollectionSupport.setOf(JCAConstants.KEY_ALGO_EC, JCAConstants.KEY_ALGO_DH);
    
    /** Constructor. */
    private KeyAgreementSupport() {}
    
    /**
     * Get the global {@link KeyAgreementProcessorRegistry} instance.
     * 
     * @return the global processor registry, or null if nothing registered
     */
    @Nullable public static KeyAgreementProcessorRegistry getGlobalProcessorRegistry() {
        return ConfigurationService.get(KeyAgreementProcessorRegistry.class);
    }
    
    /**
     * Get the global {@link KeyAgreementProcessorRegistry} instance, raising an exception
     * if unavailable.
     * 
     * @return the global processor registry
     * 
     * @since 5.0.0
     */
    @Nonnull public static KeyAgreementProcessorRegistry ensureGlobalProcessorRegistry() {
        return ConfigurationService.ensure(KeyAgreementProcessorRegistry.class);
    }
    
    /**
     * Lookup and return the {@link KeyAgreementProcessor} to use for the specified key
     * agreement algorithm.
     * 
     * @param algorithm the key agreement algorithm
     * 
     * @return the processor for that algorithm
     * 
     * @throws KeyAgreementException if global {@link KeyAgreementProcessorRegistry} is not configured
     *          or if no processor is registered for the specified algorithm
     */
    @Nonnull public static KeyAgreementProcessor getProcessor(@Nonnull final String algorithm)
            throws KeyAgreementException {
        
        final KeyAgreementProcessorRegistry registry = getGlobalProcessorRegistry();
        if (registry == null) {
            throw new KeyAgreementException("Global KeyAgreementProcessorRegistry not configured");
        }
        
        final KeyAgreementProcessor processor = registry.getProcessor(algorithm);
        if (processor == null) {
            throw new KeyAgreementException("No KeyAgreementProcessor registered for specified algorithm: "
                    + algorithm);
        }
        
        return processor;
    }
    
    /**
     * Look for an explicit key size via an {@link AgreementMethod}'s grandparent's {@link EncryptionMethod}
     * child's {@link KeySize} child element.
     * 
     * @param agreementMethod the AgreementMethod to process
     * 
     * @return the key size, or null if not present
     */
    @Nullable public static Integer getExplicitKeySize(@Nonnull final AgreementMethod agreementMethod) {
        final XMLObject parent = agreementMethod.getParent();
        if (parent == null || parent.getParent() == null || ! EncryptedType.class.isInstance(parent.getParent())) {
            return null;
        }
        
        final EncryptedType et = EncryptedType.class.cast(parent.getParent());
        final EncryptionMethod method = et.getEncryptionMethod();
        if (method == null) {
            return null;
        }
        
        final KeySize size = method.getKeySize();
        return size != null ? size.getValue() : null;
    }
    
    /** 
     * Validate the specified algorithm URI and key length for consistency.
     * 
     * <p>
     * If the algorithm URI does not imply a key length, then the specified key length must be non-null.
     * If the algorithm URI does imply a key length and the optional specified key length is non-null, 
     * they must be the same length. 
     * </p>
     *
     *  
     * @param algorithmURI the algorithm URI
     * @param specifiedKeyLength the optional specified key length
     * 
     * @throws KeyAgreementException if algorithm and specified key lengths are not consistent
     */
    public static void validateKeyAlgorithmAndSize(@Nonnull final String algorithmURI,
            @Nullable final Integer specifiedKeyLength) throws KeyAgreementException {
        
        final Integer algoKeyLength = AlgorithmSupport.getKeyLength(algorithmURI);
        
        if (algoKeyLength == null && specifiedKeyLength == null) {
            throw new KeyAgreementException("Key length was not specified and key algorithm does not imply a length: "
                    + algorithmURI);
        }
        
        if (algoKeyLength != null && specifiedKeyLength != null && ! algoKeyLength.equals(specifiedKeyLength)) {
            throw new KeyAgreementException(String.format("Algorithm URI '%s' key length (%d) "
                    + "does not match specified (%d)", algorithmURI, algoKeyLength, specifiedKeyLength));
        }
        
    }
    
    /**
     * Evaluate whether the specified credential contains a public key which supports key agreement.
     * 
     * @param credential the credential to evaluate
     * @return true if supports key agreement, false if does not
     */
    public static boolean supportsKeyAgreement(@Nullable final Credential credential) {
        if (credential == null) {
            return false;
        }
        
        final PublicKey pk = credential.getPublicKey();
        return pk != null && KEY_ALGORITHMS.contains(pk.getAlgorithm());
    }
    
}