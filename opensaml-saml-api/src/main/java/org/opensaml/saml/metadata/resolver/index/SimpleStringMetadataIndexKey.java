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

package org.opensaml.saml.metadata.resolver.index;

import javax.annotation.Nonnull;

import com.google.common.base.MoreObjects;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * A simple implementation of {@link MetadataIndexKey} based on a single input string.
 */
public class SimpleStringMetadataIndexKey implements MetadataIndexKey {
    
    /** The indexed value. */
    @Nonnull private String value;
    
    /**
     * Constructor.
     *
     * @param newValue the string value to use as the index key
     */
    public SimpleStringMetadataIndexKey(@Nonnull final String newValue) {
        value = Constraint.isNotNull(StringSupport.trimOrNull(newValue), "String index value was null");
    }
    
    /**
     * Get the string value used as the index key.
     * 
     * @return the string value
     */
    @Nonnull public String getValue() {
        return value;
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(value).toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof SimpleStringMetadataIndexKey other) {
            return value.equals(other.value);
        }

        return false;
    }

}