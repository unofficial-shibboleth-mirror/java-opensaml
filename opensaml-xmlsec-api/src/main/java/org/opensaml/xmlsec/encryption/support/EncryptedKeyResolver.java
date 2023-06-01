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

package org.opensaml.xmlsec.encryption.support;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;

/**
 * Interface for resolving {@link EncryptedKey} elements based on a particular
 * {@link EncryptedData} context, primarily for use during the decryption process.
 * 
 * The resolved EncryptedKey element(s) will contain the data encryption key used to encrypt
 * the specified EncryptedData.
 */
public interface EncryptedKeyResolver {
    
    /**
     * Resolve the EncryptedKey elements containing the data encryption key used to 
     * encrypt the specified EncryptedData element.
     * 
     * @param encryptedData  the EncryptedData element context in which to resolve
     * @return an iterable of EncryptedKey elements
     */
    default @Nonnull Iterable<EncryptedKey> resolve(@Nonnull final EncryptedData encryptedData) {
        return resolve(encryptedData, CollectionSupport.emptySet());
    }
    
    /**
     * Resolve the EncryptedKey elements containing the data encryption key used to 
     * encrypt the specified EncryptedData element.
     * 
     * @param encryptedData  the EncryptedData element context in which to resolve
     * @param recipients the recipients to use during resolution
     * @return an iterable of EncryptedKey elements
     */
    @Nonnull Iterable<EncryptedKey> resolve(@Nonnull final EncryptedData encryptedData,
            @Nullable final Set<String> recipients);

    /**
     * Get the set of recipient criteria used by this resolver, and against which a candidate 
     * EncryptedKey's Recipient attribute is evaluated.
     * 
     * @return the collection of  recipient criteria
     */
    @Deprecated
    @Nonnull @Unmodifiable @NotLive Set<String> getRecipients();

}