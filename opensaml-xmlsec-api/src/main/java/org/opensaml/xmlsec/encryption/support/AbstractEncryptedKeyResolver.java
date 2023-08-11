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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;

import org.opensaml.xmlsec.encryption.CarriedKeyName;
import org.opensaml.xmlsec.encryption.DataReference;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.ReferenceList;
import org.opensaml.xmlsec.keyinfo.KeyInfoSupport;
import org.opensaml.xmlsec.signature.KeyInfo;

import com.google.common.base.Strings;

/**
 * Abstract class implementation for {@link EncryptedKeyResolver}.
 */
public abstract class AbstractEncryptedKeyResolver implements EncryptedKeyResolver {
    
    /** Recipient attribute criteria against which to match.*/
    @Nonnull private final Set<String> recipients;
    
    /** Constructor. */
    public AbstractEncryptedKeyResolver() {
        recipients = CollectionSupport.emptySet();
    }

    /** 
     * Constructor. 
     * 
     * @param newRecipents set of recipients
     */
    @Deprecated
    public AbstractEncryptedKeyResolver(@Nullable final Set<String> newRecipents) {
        recipients = CollectionSupport.copyToSet(StringSupport.normalizeStringCollection(newRecipents));
    }

    /** 
     * Constructor. 
     * 
     * @param recipient the recipient
     */
    public AbstractEncryptedKeyResolver(@Nullable final String recipient) {
        final String trimmed = StringSupport.trimOrNull(recipient);
        if (trimmed != null) {
            recipients = CollectionSupport.singleton(trimmed);
        } else {
            recipients = CollectionSupport.emptySet();
        }
    }

    /** {@inheritDoc} */
    @Deprecated
    @Nonnull @Unmodifiable @NotLive public Set<String> getRecipients() {
        return recipients;
    }
    
    /**
     * Get the effective set of recipients by merging the passed recipients set
     * with the static set of recipients possibly configured on this resolver instance. 
     * 
     * @param values the recipients argument
     * @return the merged recipients
     */
    @Nonnull @Unmodifiable @NotLive
    protected Set<String> getEffectiveRecipients(@Nullable final Set<String> values) {
        final Set<String> temp = new HashSet<>();
        temp.addAll(getRecipients());
        if (values != null) {
            temp.addAll(values);
        }
        return CollectionSupport.copyToSet(temp);
    }
    
    /**
     * Evaluate whether the specified recipient attribute value matches this resolver's
     * recipient criteria.
     * 
     * @param recipient the recipient value to evaluate
     * @param validRecipients recipients to consider valid for matching purposes.
     *        If empty, then all recipients match
     * @return true if the recipient value matches the resolver's criteria, false otherwise
     */
    protected boolean matchRecipient(@Nullable final String recipient, @Nonnull final Set<String> validRecipients) {
        if (validRecipients.isEmpty()) {
            return true;
        }
        
        final String trimmedRecipient = StringSupport.trimOrNull(recipient);
        if (trimmedRecipient == null) {
            return true;
        }
        
        return validRecipients.contains(trimmedRecipient);
    }
    
    /**
     * Evaluate whether an EncryptedKey's CarriedKeyName matches one of the KeyName values
     * from the EncryptedData context.
     * 
     * @param encryptedData the EncryptedData context
     * @param encryptedKey the candidate Encryptedkey to evaluate
     * @return true if the encrypted key's carried key name matches that of the encrytped data, 
     *          false otherwise
     */
    protected boolean matchCarriedKeyName(@Nonnull final EncryptedData encryptedData,
            @Nonnull final EncryptedKey encryptedKey) {
        Constraint.isNotNull(encryptedData, "EncryptedData cannot be null");
        Constraint.isNotNull(encryptedKey, "EncryptedKey cannot be null");
        
        final CarriedKeyName carried = encryptedKey.getCarriedKeyName();
        if (carried == null || Strings.isNullOrEmpty(carried.getValue()) ) {
            return true;
        }
        
        final KeyInfo keyInfo = encryptedData.getKeyInfo(); 
        if (keyInfo == null || keyInfo.getKeyNames().isEmpty() ) {
            return false;
        }
        
        return KeyInfoSupport.getKeyNames(keyInfo).contains(carried.getValue());
    }
    
    /**
     * Evaluate whether any of the EncryptedKey's DataReferences refer to the EncryptedData
     * context.
     * 
     * @param encryptedData the EncryptedData context
     * @param encryptedKey the candidate Encryptedkey to evaluate
     * @return true if any of the encrypted key's data references refer to the encrypted data context,
     *          false otherwise
     */
    protected boolean matchDataReference(@Nonnull final EncryptedData encryptedData,
            @Nonnull final EncryptedKey encryptedKey) {
        Constraint.isNotNull(encryptedData, "EncryptedData cannot be null");
        Constraint.isNotNull(encryptedKey, "EncryptedKey cannot be null");

        final ReferenceList reflist = encryptedKey.getReferenceList();
        if (reflist == null || reflist.getDataReferences().isEmpty() ) {
            return true;
        } else if (Strings.isNullOrEmpty(encryptedData.getID())) {
            return false;
        }
        
        final List<DataReference> drlist = reflist.getDataReferences();
        for (final DataReference dr : drlist) {
            final String druri = dr.getURI();
            if (druri == null || !druri.startsWith("#") ) {
                continue;
            } else if (dr.resolveIDFromRoot(druri.substring(1)) == encryptedData) {
                return true;
            }
        }
        return false;
    }
    
}