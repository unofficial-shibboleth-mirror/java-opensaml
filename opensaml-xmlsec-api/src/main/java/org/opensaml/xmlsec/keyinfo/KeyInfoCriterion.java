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

package org.opensaml.xmlsec.keyinfo;

import java.util.Objects;

import javax.annotation.Nullable;

import org.opensaml.xmlsec.signature.KeyInfo;

import net.shibboleth.shared.resolver.Criterion;

/**
 * An implementation of {@link Criterion} which specifies criteria based
 * on the contents of a {@link KeyInfo} element.
 */
public final class KeyInfoCriterion implements Criterion {
    
    /** The KeyInfo which serves as the source for credential criteria. */
    @Nullable private KeyInfo keyInfo;
    
    /**
     * Constructor.
     *
     * @param newKeyInfo the KeyInfo credential criteria to use
     */
    public KeyInfoCriterion(@Nullable final KeyInfo newKeyInfo) {
       setKeyInfo(newKeyInfo);
    }

    /**
     * Gets the KeyInfo which is the source of credential criteria.
     * 
     * @return the KeyInfo credential criteria
     */
    @Nullable public KeyInfo getKeyInfo() {
        return keyInfo;
    }
    
    /**
     * Sets the KeyInfo which is the source of credential criteria.
     * 
     * @param newKeyInfo the KeyInfo to use as credential criteria
     * 
     */
    public void setKeyInfo(@Nullable final KeyInfo newKeyInfo) {
        // Note: we allow KeyInfo to be null to handle case where application context,
        // other accompanying criteria, etc should be used to resolve credentials.
        keyInfo = newKeyInfo;
    }
    
    /** {@inheritDoc} */
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("KeyInfoCriterion [keyInfo=");
        builder.append("<contents not displayable>");
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        if (keyInfo != null) {
            return keyInfo.hashCode();
        }
        return super.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof KeyInfoCriterion other) {
            return Objects.equals(keyInfo, other.keyInfo);
        }

        return false;
    }

}