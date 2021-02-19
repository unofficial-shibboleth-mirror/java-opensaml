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

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.crypto.JCAConstants;
import org.opensaml.xmlsec.encryption.AgreementMethod;
import org.opensaml.xmlsec.encryption.EncryptedType;
import org.opensaml.xmlsec.encryption.EncryptionMethod;
import org.opensaml.xmlsec.encryption.KeySize;

/**
 * Support for key agreement operations.
 */
public final class KeyAgreementSupport {
    
    /** JCA key algorithms that support key agreement. */
    public static final Set<String> KEY_ALGORITHMS = Set.of(JCAConstants.KEY_ALGO_EC);
    
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
        if (agreementMethod.getParent() == null || agreementMethod.getParent().getParent() == null
                || ! EncryptedType.class.isInstance(agreementMethod.getParent().getParent())) {
            return null;
        }
        
        final EncryptedType et = EncryptedType.class.cast(agreementMethod.getParent().getParent());
        if (et.getEncryptionMethod() == null || et.getEncryptionMethod().getKeySize() == null) {
            return null;
        }
        
        return et.getEncryptionMethod().getKeySize().getValue();
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
        
        return credential.getPublicKey() != null && KEY_ALGORITHMS.contains(credential.getPublicKey().getAlgorithm());
    }
}
