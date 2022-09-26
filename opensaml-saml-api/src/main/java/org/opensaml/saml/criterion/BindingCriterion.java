/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
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

import java.util.List;

import javax.annotation.Nonnull;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.resolver.Criterion;
import net.shibboleth.utilities.java.support.primitive.StringSupport;

/** {@link Criterion} representing an ordered list of SAML bindings. */
public final class BindingCriterion implements Criterion {

    /** The SAML binding URI. */
    @Nonnull @NonnullElements private final List<String> bindings;

    /**
     * Constructor.
     * 
     * @param bindingURIs list of SAML binding URIs
     */
    public BindingCriterion(@Nonnull @NonnullElements final List<String> bindingURIs) {
        bindings = List.copyOf(StringSupport.normalizeStringCollection(bindingURIs));
    }

    /**
     * Get ordered list of SAML binding URIs.
     * 
     * @return ordered list of SAML binding URIs
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public List<String> getBindings() {
        return bindings;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BindingCriterion [bindings=");
        builder.append(bindings);
        builder.append("]");
        return builder.toString();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return bindings.hashCode();
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

        if (obj instanceof BindingCriterion) {
            return bindings.equals(((BindingCriterion) obj).bindings);
        }

        return false;
    }
}