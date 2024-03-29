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

package org.opensaml.security.credential.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.credential.Credential;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/**
 * An implementation of {@link org.opensaml.security.credential.CredentialResolver} that
 * uses a {@link Collection} as the underlying credential source.
 * 
 * <p>
 * The credentials returned are filtered based on any
 * {@link org.opensaml.security.credential.criteria.impl.EvaluableCredentialCriterion} that may
 * have been present in the specified criteria set, or that are resolved by lookup in the
 * {@link org.opensaml.security.credential.criteria.impl.EvaluableCredentialCriteriaRegistry}.
 * </p>
 */
public class CollectionCredentialResolver extends AbstractCriteriaFilteringCredentialResolver {
    
    /** The collection of credentials which is the underlying store for the resolver. */
    @Nonnull private final Collection<Credential> collection;
    
    /**
     * Constructor.
     * 
     * An {@link ArrayList} is used as the underlying collection implementation.
     *
     */
    public CollectionCredentialResolver() {
        collection = new ArrayList<>();
    }
    
    /**
     * Constructor.
     *
     * @param credentials the credential collection which is the backing store for the resolver
     */
    public CollectionCredentialResolver(@Nonnull @Live final Collection<Credential> credentials) {
        collection = credentials;
    }
    
    /**
     * Get the (modifiable) credential collection which is the backing store for the resolver.
     * 
     * @return the credential collection backing store
     */
    @Nonnull @Live public Collection<Credential> getCollection() {
        return collection;
    }

    /** {@inheritDoc} */
    @Nonnull @Live protected Iterable<Credential> resolveFromSource(@Nullable final CriteriaSet criteriaSet)
            throws ResolverException {
        return collection;
    }

}