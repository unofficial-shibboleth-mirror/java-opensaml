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

package org.opensaml.xmlsec.encryption.support;

import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.signature.KeyInfo;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;

/**
 * Implementation of {@link EncryptedKeyResolver} which finds {@link EncryptedKey} elements
 * within the {@link org.opensaml.xmlsec.signature.KeyInfo} of the {@link EncryptedData} context.
 */
public class InlineEncryptedKeyResolver extends AbstractEncryptedKeyResolver {
    
    /** Constructor. */
    public InlineEncryptedKeyResolver() {
        
    }

    /** 
     * Constructor. 
     * 
     * @param recipients the set of recipients
     */
    @Deprecated
    public InlineEncryptedKeyResolver(@Nullable final Set<String> recipients) {
        super(recipients);
    }

    /** 
     * Constructor. 
     * 
     * @param recipient the recipient
     */
    @Deprecated
    public InlineEncryptedKeyResolver(@Nullable final String recipient) {
        this(recipient != null ? CollectionSupport.singleton(recipient) : null);
    }

    /** {@inheritDoc} */
    @Nonnull public Iterable<EncryptedKey> resolve(@Nonnull final EncryptedData encryptedData,
            @Nullable final Set<String> recipients) {
        Constraint.isNotNull(encryptedData, "EncryptedData cannot be null");
        
        final KeyInfo keyInfo = encryptedData.getKeyInfo();
        if (keyInfo == null) {
            return CollectionSupport.emptyList();
        }
        
        return keyInfo.getEncryptedKeys().stream()
                .filter(ek -> matchRecipient(ek.getRecipient(), getEffectiveRecipients(recipients)))
                .collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableList())).get();
    }

}