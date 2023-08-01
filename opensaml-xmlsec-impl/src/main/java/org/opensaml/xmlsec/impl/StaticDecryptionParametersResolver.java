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

package org.opensaml.xmlsec.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.xmlsec.DecryptionParameters;
import org.opensaml.xmlsec.DecryptionParametersResolver;

/**
 * Resolve an instance of {@link DecryptionParameters} statically.
 */
public class StaticDecryptionParametersResolver implements DecryptionParametersResolver {
    
    /** Static parameters. */
    @Nonnull private DecryptionParameters params;
    
    /**
     * Constructor.
     *
     * @param parameters the static parameters instance to return
     */
    public StaticDecryptionParametersResolver(@Nonnull final DecryptionParameters parameters) {
        params = Constraint.isNotNull(parameters, "Parameters instance may not be null");
    }

    /** {@inheritDoc} */
    @Nonnull public Iterable<DecryptionParameters> resolve(@Nullable final CriteriaSet criteria)
            throws ResolverException {
        return CollectionSupport.singleton(params);
    }

    /** {@inheritDoc} */
    @Nullable public DecryptionParameters resolveSingle(@Nullable final CriteriaSet criteria) throws ResolverException {
        return params;
    }

}