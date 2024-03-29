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

import java.util.Objects;

import javax.annotation.Nonnull;

import org.opensaml.saml.saml2.metadata.Endpoint;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.Criterion;

/**
 * {@link Criterion} representing a SAML metadata endpoint object.
 *
 * @param <EndpointType> the type of endpoint
 */
public final class EndpointCriterion<EndpointType extends Endpoint> implements Criterion {

    /** Is this endpoint implicitly trusted? */
    private final boolean trusted;
    
    /** The endpoint. */
    @Nonnull private final EndpointType endpoint;
    
    /**
     * Constructor.
     * 
     * <p>
     * Endpoint is not implicitly trusted.
     * </p>
     * 
     * @param ep the endpoint
     */
    public EndpointCriterion(@Nonnull final EndpointType ep) {
        this(ep, false);
    }
    
    /**
     * Constructor.
     * 
     * @param ep the endpoint
     * @param trust if true, the endpoint should be implicitly trusted regardless of verification by other criteria
     */
    public EndpointCriterion(@Nonnull final EndpointType ep, final boolean trust) {
        endpoint = Constraint.isNotNull(ep, "Endpoint cannot be null");
        trusted = trust;
    }

    /**
     * Get the endpoint.
     * 
     * @return the endpoint type
     */
    @Nonnull public EndpointType getEndpoint() {
        return endpoint;
    }
    
    /**
     * Get the trust indicator for the endpoint.
     * 
     * @return true iff the endpoint does not require independent verification against a trusted source of endpoints
     */
    public boolean isTrusted() {
        return trusted;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("EndpointCriterion [type=")
            .append(endpoint.getElementQName());
        if (endpoint.getBinding() != null) {
            builder.append(", Binding=")
                .append(endpoint.getBinding());
        }
        if (endpoint.getLocation() != null) {
            builder.append(", Location=")
                .append(endpoint.getLocation());
        }
        if (endpoint.getResponseLocation() != null) {
            builder.append(", ResponseLocation=")
                .append(endpoint.getResponseLocation());
        }
        builder.append(", trusted=").append(trusted)
            .append(']');
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return toString().hashCode();
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

        if (obj instanceof EndpointCriterion<?> other) {
            final Endpoint endpoint2 = other.getEndpoint();
            if (!Objects.equals(endpoint.getElementQName(), endpoint2.getElementQName())) {
                return false;
            } else if (!Objects.equals(endpoint.getBinding(), endpoint2.getBinding())) {
                return false;
            } else if (!Objects.equals(endpoint.getLocation(), endpoint2.getLocation())) {
                return false;
            } else if (!Objects.equals(endpoint.getResponseLocation(), endpoint2.getResponseLocation())) {
                return false;
            }
            
            return true;
        }

        return false;
    }
    
}