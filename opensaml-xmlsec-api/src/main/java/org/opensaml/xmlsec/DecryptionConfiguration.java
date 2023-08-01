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

package org.opensaml.xmlsec;

import java.util.Set;

import javax.annotation.Nullable;

import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;

/**
 * The configuration information to use when decrypting encrypted XML.
 */
public interface DecryptionConfiguration extends AlgorithmPolicyConfiguration {
    
    /**
     * The KeyInfoCredentialResolver to use when processing the EncryptedData/KeyInfo.
     * 
     * @return the KeyInfoCredentialResolver instance
     */
    @Nullable KeyInfoCredentialResolver getDataKeyInfoCredentialResolver();
    
    /**
     * The KeyInfoCredentialResolver to use when processing the EncryptedKey/KeyInfo (the
     * Key Encryption Key or KEK).
     * 
     * @return the KeyInfoCredentialResolver instance
     */
    @Nullable KeyInfoCredentialResolver getKEKKeyInfoCredentialResolver();
    
    /**
     * Get the EncryptedKeyResolver to use when resolving the EncryptedKey(s) to process.
     * 
     * @return the EncryptedKeyResolver instance
     */
    @Nullable EncryptedKeyResolver getEncryptedKeyResolver();
    
    /**
     * Get the set of recipients against which to evaluate candidate EncryptedKey elements.
     * 
     * @return the recipients
     */
    @Nullable Set<String> getRecipients();
    
}