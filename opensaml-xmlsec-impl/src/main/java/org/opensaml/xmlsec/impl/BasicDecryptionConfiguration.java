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

package org.opensaml.xmlsec.impl;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.xmlsec.DecryptionConfiguration;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;

/**
 * Basic implementation of {@link DecryptionConfiguration}.
 */
public class BasicDecryptionConfiguration extends BasicAlgorithmPolicyConfiguration 
        implements DecryptionConfiguration {
    
    /** The EncryptedData's KeyInfo credential resolver. */
    @Nullable private KeyInfoCredentialResolver dataKeyInfoCredentialResolver;
    
    /** The EncryptedKey's KeyInfo credential resolver. */
    @Nullable private KeyInfoCredentialResolver kekKeyInfoCredentialResolver;
    
    /** The EncryptedKey resolver. */
    @Nullable private EncryptedKeyResolver encryptedKeyResolver;
    
    /** The set of recipients against which to evaluate candidate EncryptedKey elements. */
    @Nullable private Set<String> recipients;

    //TODO chaining to parent config instance on getters? or use a wrapping proxy, etc?

    /**
     * Get the KeyInfoCredentialResolver to use when processing the EncryptedData/KeyInfo.
     * 
     * @return the KeyInfoCredentialResolver instance
     */
    @Nullable public KeyInfoCredentialResolver getDataKeyInfoCredentialResolver() {
        return dataKeyInfoCredentialResolver;
    }
    
    /**
     * Set the KeyInfoCredentialResolver to use when processing the EncryptedData/KeyInfo.
     * 
     * @param resolver the KeyInfoCredentialResolver instance
     * 
     * @return this object
     */
    @Nonnull public BasicDecryptionConfiguration setDataKeyInfoCredentialResolver(
            @Nullable final KeyInfoCredentialResolver resolver) {
        dataKeyInfoCredentialResolver = resolver;
        return this;
    }
    
    /**
     * Get the KeyInfoCredentialResolver to use when processing the EncryptedKey/KeyInfo (the
     * Key Encryption Key or KEK).
     * 
     * @return the KeyInfoCredentialResolver instance
     */
    @Nullable public KeyInfoCredentialResolver getKEKKeyInfoCredentialResolver() {
       return kekKeyInfoCredentialResolver; 
    }
    
    /**
     * Set the KeyInfoCredentialResolver to use when processing the EncryptedKey/KeyInfo (the
     * Key Encryption Key or KEK).
     * 
     * @param resolver the KeyInfoCredentialResolver instance
     * 
     * @return this object
     */
    @Nonnull public BasicDecryptionConfiguration setKEKKeyInfoCredentialResolver(
            @Nullable final KeyInfoCredentialResolver resolver) {
       kekKeyInfoCredentialResolver = resolver;
       return this;
    }
    
    /**
     * Get the EncryptedKeyResolver to use when resolving the EncryptedKey(s) to process.
     * 
     * @return the EncryptedKeyResolver instance
     */
    @Nullable public EncryptedKeyResolver getEncryptedKeyResolver() {
       return encryptedKeyResolver; 
    }
    
    /**
     * Get the EncryptedKeyResolver to use when resolving the EncryptedKey(s) to process.
     * 
     * @param resolver the EncryptedKeyResolver instance
     * 
     * @return this object
     */
    @Nonnull public BasicDecryptionConfiguration setEncryptedKeyResolver(
            @Nullable final EncryptedKeyResolver resolver) {
       encryptedKeyResolver = resolver;
       return this;
    }
    
    /**
     * Get the set of recipients against which to evaluate candidate EncryptedKey elements.
     * 
     * @return the recipients
     */
    public @Nullable Set<String> getRecipients() {
        return recipients;
    }

    /**
     * Set the set of recipients against which to evaluate candidate EncryptedKey elements.
     * 
     * @param newRecipients the recipients
     */
    public void setRecipients(@Nullable final Set<String> newRecipients) {
        recipients = newRecipients;
    }
    
}