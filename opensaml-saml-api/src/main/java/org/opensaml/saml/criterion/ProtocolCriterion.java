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

package org.opensaml.saml.criterion;

import javax.annotation.Nonnull;

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.resolver.Criterion;

/** {@link Criterion} representing a protocolSupportEnumeration value. */
public final class ProtocolCriterion implements Criterion {

    /** The SAML protocol. */
    @Nonnull @NotEmpty private final String protocol;

    /**
     * Constructor.
     * 
     * @param protocolUri the SAML protocol
     */
    public ProtocolCriterion(@Nonnull @NotEmpty final String protocolUri) {
        protocol = Constraint.isNotNull(StringSupport.trimOrNull(protocolUri), 
                "SAML protocol URI cannot be null or empty");
    }

    /**
     * Get the SAML protocol URI.
     * 
     * @return SAML protocol URI
     */
    @Nonnull @NotEmpty public String getProtocol() {
        return protocol;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("ProtocolCriterion [protocol=");
        builder.append(protocol);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return protocol.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj instanceof ProtocolCriterion other) {
            return protocol.equals(other.protocol);
        }

        return false;
    }

}