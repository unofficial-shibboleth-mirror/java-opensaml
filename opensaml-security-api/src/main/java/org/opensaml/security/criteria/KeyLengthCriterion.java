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

package org.opensaml.security.criteria;

import net.shibboleth.shared.resolver.Criterion;

/**
 * An implementation of {@link Criterion} which specifies key length criteria.
 */
public final class KeyLengthCriterion implements Criterion {
    
    /** Key length of resolved credentials. */
    private int keyLength;
    
    /**
     * Constructor.
     *
     * @param length key length 
     */
    public KeyLengthCriterion(final int length) {
        setKeyLength(length);
    }

    /**
     * Get the key length.
     * 
     * @return Returns the keyLength.
     */
    public int getKeyLength() {
        return keyLength;
    }

    /**
     * Set the key length.
     * 
     * @param length The keyLength to set.
     */
    public void setKeyLength(final int length) {
        keyLength = length;
    }
    
    /** {@inheritDoc} */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("KeyLengthCriterion [keyLength=");
        builder.append(keyLength);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return Integer.valueOf(keyLength).hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof KeyLengthCriterion lencrit) {
            return keyLength == lencrit.keyLength;
        }

        return false;
    }

}