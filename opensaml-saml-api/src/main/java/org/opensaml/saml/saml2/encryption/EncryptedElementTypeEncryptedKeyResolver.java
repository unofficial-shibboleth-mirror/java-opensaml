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

package org.opensaml.saml.saml2.encryption;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.saml2.core.EncryptedElementType;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.support.AbstractEncryptedKeyResolver;

import net.shibboleth.shared.collection.CollectionSupport;

/**
 * An implementation of {@link org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver}
 * which resolves {@link EncryptedKey} elements which appear as immediate children of the
 * {@link EncryptedElementType} which is the parent of the {@link EncryptedData} context.
 */
public class EncryptedElementTypeEncryptedKeyResolver extends AbstractEncryptedKeyResolver {
    
    /** Constructor. */
    public EncryptedElementTypeEncryptedKeyResolver() {
        
    }

    /** 
     * Constructor. 
     * 
     * @param recipients the set of recipients
     */
    @Deprecated
    public EncryptedElementTypeEncryptedKeyResolver(@Nullable final Set<String> recipients) {
        super(recipients);
    }

    /** 
     * Constructor. 
     * 
     * @param recipient the recipient
     */
    @Deprecated
    public EncryptedElementTypeEncryptedKeyResolver(@Nullable final String recipient) {
        this(recipient != null ? CollectionSupport.singleton(recipient) : null);
    }

    /** {@inheritDoc} */
    @Nonnull public Iterable<EncryptedKey> resolve(@Nonnull final EncryptedData encryptedData,
            @Nullable final Set<String> recipients) {
        
        if (!(encryptedData.getParent() instanceof EncryptedElementType) ) {
            return CollectionSupport.emptyList();
        }
        
        final EncryptedElementType encElementType = (EncryptedElementType) encryptedData.getParent();
        assert encElementType != null;
        return  encElementType.getEncryptedKeys().stream()
                .filter(ek -> matchRecipient(ek.getRecipient(), getEffectiveRecipients(recipients)))
                .collect(CollectionSupport.nonnullCollector(Collectors.toUnmodifiableList())).get();
    }
    
}