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

package org.opensaml.xmlsec.criterion;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.resolver.Criterion;

/**
 * A criterion implementation for conveying set of recipients against which to evaluate candidate
 * EncryptedKey elements.
 */
public class DecryptionRecipientsCriterion implements Criterion {
    
    /** The set of recipients. */
    @Nonnull private Set<String> recipients;
    
    /**
     * Constructor.
     *
     * @param values the set of recipients
     */
    public DecryptionRecipientsCriterion(@Nullable final Set<String> values)  {
        recipients = processValues(values);
    }
    
    /**
     * Get the set of recipients
     * 
     * @return the set of recipients
     */
    @Nonnull @NotLive @Unmodifiable public Set<String> getRecipients() {
        return recipients;
    }
    
    /**
     * Set the set of recipients
     * 
     * @param values the new recipients
     */
    public void setRecipients(@Nullable final Set<String> values) {
        recipients = processValues(values);
    }
    
    /** {@inheritDoc} */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("DecryptionRecipientsCriterion [values=");
        builder.append(recipients);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        int result = 17;  
        result = 37*result + recipients.hashCode();
        return result;
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof DecryptionRecipientsCriterion) {
            final DecryptionRecipientsCriterion other = (DecryptionRecipientsCriterion) obj;
            return recipients.equals(other.recipients);
        }

        return false;
    }
    
    /**
     * Sanitize input values.
     * 
     * @param values input values
     * 
     * @return sanitized set
     */
    @Nonnull @NotLive @Unmodifiable private Set<String> processValues(@Nullable final Set<String> values) {
        if (values!= null) {
            return CollectionSupport.copyToSet(StringSupport.normalizeStringCollection(values));
        } else {
            return CollectionSupport.emptySet();
        }
    }

}