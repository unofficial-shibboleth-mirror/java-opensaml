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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;

import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/**
 * Abstract base class for {@link CredentialResolver} implementations.
 */
public abstract class AbstractCredentialResolver implements CredentialResolver {

    /** {@inheritDoc} */
    @Nullable public Credential resolveSingle(@Nullable final CriteriaSet criteriaSet) throws ResolverException {
        final Iterable<Credential> creds = resolve(criteriaSet);
        if (creds.iterator().hasNext()) {
            return creds.iterator().next();
        }
        return null;
    }

    /** {@inheritDoc} */
    @Nonnull public abstract Iterable<Credential> resolve(@Nullable CriteriaSet criteriaSet) throws ResolverException;

}