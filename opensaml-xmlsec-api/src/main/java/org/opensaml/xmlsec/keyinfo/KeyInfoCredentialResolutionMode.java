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

import java.security.PrivateKey;

import javax.annotation.Nonnull;
import javax.crypto.SecretKey;

import org.apache.xml.security.encryption.AgreementMethod;
import org.opensaml.xmlsec.signature.KeyInfo;

import com.google.common.base.MoreObjects;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.Criterion;

/**
 * An implementation of {@link Criterion} which specifies criteria
 * to a {@link KeyInfoCredentialResolver} about whether to resolve
 * public credentials, local credentials, or both.
 * 
 * <p>
 * A local credential is defined as one carrying either a {@link PrivateKey} or a {@link SecretKey}.
 * </p>
 * 
 * <p>
 * This criterion is used with resolver implementations which are 
 * capable of local credential resolution using the (usually public) information available
 * directly within {@link KeyInfo}.
 * </p>
 * 
 * <p>
 * If <code>PUBLIC</code> then resolver implementations may skip 
 * local resolution and return any credentials extracted directly from
 * {@link KeyInfo}. This mode does not mean that a local credential will
 * absolutely not be returned, merely that the resolver is not obligated to
 * do so. For example, an {@link AgreementMethod} might produce a credential
 * containing a {@link SecretKey}, and this would be returned in this mode.
 * </p>
 * 
 * <p>
 * If <code>LOCAL</code> then resolver implementations which are capable
 * of resolving local credentials should attempt that local credential resolution,
 * and only those local credentials should be returned.
 * </p>
 * 
 * <p>
 * If <code>BOTH</code> then local credential resolution should be attempted
 * as in <code>LOCAL</code>. If a local credential based on a given public credential's info is
 * resolved it will be returned, otherwise the public credential itself will be returned.
 * </p>
 */
public final class KeyInfoCredentialResolutionMode implements Criterion {
    
    /** Credential resolution mode. */
    public enum Mode {
        /** Public credential mode. */
        PUBLIC,
        /** Local credential mode. */
        LOCAL,
        /** Public and local credential mode. */
        BOTH
    }
    
    /** Resolution mode. */
    @Nonnull private Mode mode;
    
    
    /**
     * Constructor.
     *
     * @param  resolutionMode the resolution mode
     */
    public KeyInfoCredentialResolutionMode(@Nonnull final Mode resolutionMode) {
        mode = Constraint.isNotNull(resolutionMode, "Resolution mode was null");
    }

    /**
     * Gets the resolution mode.
     * 
     * @return the resolution mode
     */
    @Nonnull public Mode getMode() {
        return mode;
    }
    
    /** {@inheritDoc} */
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("mode", mode)
                .toString();
    }

    /** {@inheritDoc} */
    public int hashCode() {
        return mode.hashCode();
    }

    /** {@inheritDoc} */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof KeyInfoCredentialResolutionMode other) {
            return mode.equals(other.mode);
        }

        return false;
    }

}