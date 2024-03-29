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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.slf4j.Logger;

/**
 * An abstract implementation of {@link CredentialResolver} which chains together one or more underlying credential 
 * resolver implementations. Resolved credentials are returned from all underlying resolvers in the chain, 
 * in the order implied by the order of the resolvers in the chain.
 * 
 * @param <ResolverType> the subtype of CredentialResolver to be chained
 */
public abstract class AbstractChainingCredentialResolver<ResolverType extends CredentialResolver> 
        extends AbstractCredentialResolver {

    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ChainingCredentialResolver.class);

    /** List of credential resolvers in the chain. */
    @Nonnull private List<ResolverType> resolvers;

    /**
     * Constructor.
     * 
     * @param credResolvers the list of chained credential resolvers
     */
    public AbstractChainingCredentialResolver(@Nonnull final List<ResolverType> credResolvers) {
        resolvers = CollectionSupport.copyToList(
                Constraint.isNotNull(credResolvers, "CredentialResolver list cannot be null"));
    }

    /**
     * Get the unmodifiable list of credential resolvers which comprise the resolver chain.
     * 
     * @return the list of credential resolvers in the chain
     */
    @Nonnull @Unmodifiable @NotLive public List<ResolverType> getResolverChain() {
        return resolvers;
    }

    /** {@inheritDoc} */
    @Nonnull public Iterable<Credential> resolve(@Nullable final CriteriaSet criteriaSet) throws ResolverException {
        if (resolvers.isEmpty()) {
            log.warn("Chaining credential resolver resolution was attempted with an empty resolver chain");
            throw new IllegalStateException("The resolver chain is empty");
        }
        return new CredentialIterable(this, criteriaSet);
    }

    /**
     * Implementation of {@link Iterable} to be returned by {@link ChainingCredentialResolver}.
     */
    public class CredentialIterable implements Iterable<Credential> {

        /** The chaining credential resolver which owns this instance. */
        @Nonnull private AbstractChainingCredentialResolver<ResolverType> parent;

        /** The criteria set on which to base resolution. */
        @Nullable private CriteriaSet critSet;

        /**
         * Constructor.
         * 
         * @param resolver the chaining parent of this iterable
         * @param criteriaSet the set of criteria which is input to the underyling resolvers
         */
        public CredentialIterable(@Nonnull final AbstractChainingCredentialResolver<ResolverType> resolver,
                @Nullable final CriteriaSet criteriaSet) {
            parent = resolver;
            critSet = criteriaSet;
        }

        /** {@inheritDoc} */
        @Override
        @Nonnull public Iterator<Credential> iterator() {
            return new CredentialIterator(parent, critSet);
        }

    }

    /**
     * Implementation of {@link Iterator} to be returned (indirectly) by {@link ChainingCredentialResolver}.
     */
    public class CredentialIterator implements Iterator<Credential> {

        /** Logger. */
        @Nonnull private final Logger log = LoggerFactory.getLogger(CredentialIterator.class);

        /** The chaining credential resolver which owns this instance. */
        @Nonnull private AbstractChainingCredentialResolver<ResolverType> parent;

        /** The criteria set on which to base resolution. */
        @Nullable private CriteriaSet critSet;

        /** The iterator over resolvers in the chain. */
        @Nonnull private final Iterator<ResolverType> resolverIterator;

        /** The iterator over Credential instances from the current resolver. */
        private Iterator<Credential> credentialIterator;

        /** The current resolver which is returning credentials. */
        private CredentialResolver currentResolver;

        /** The next credential that is safe to return. */
        @Nullable private Credential nextCredential;

        /**
         * Constructor.
         * 
         * @param resolver the chaining parent of this iterable
         * @param criteriaSet the set of criteria which is input to the underyling resolvers
         */
        public CredentialIterator(@Nonnull final AbstractChainingCredentialResolver<ResolverType> resolver,
                @Nullable final CriteriaSet criteriaSet) {
            Constraint.isNotNull(resolver, "Parent resolver cannot be null");
            
            parent = resolver;
            critSet = criteriaSet;
            resolverIterator = parent.getResolverChain().iterator();
            credentialIterator = getNextCredentialIterator();
            nextCredential = null;
        }

        /** {@inheritDoc} */
        @Override
        public boolean hasNext() {
            if (nextCredential != null) {
                return true;
            }
            nextCredential = getNextCredential();
            if (nextCredential != null) {
                return true;
            }
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public Credential next() {
            Credential tempCred;
            if (nextCredential != null) {
                tempCred = nextCredential;
                nextCredential = null;
                return tempCred;
            }
            tempCred = getNextCredential();
            if (tempCred != null) {
                return tempCred;
            }
            throw new NoSuchElementException("No more Credential elements are available");
        }

        /** {@inheritDoc} */
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove operation is not supported by this iterator");
        }

        /**
         * Get the iterator from the next resolver in the chain.
         * 
         * @return an iterator of credentials, or null if none is available
         */
        @Nullable private Iterator<Credential> getNextCredentialIterator() {
            while (resolverIterator.hasNext()) {
                currentResolver = resolverIterator.next();
                log.debug("Getting credential iterator from next resolver in chain: {}",
                        currentResolver.getClass().toString());
                try {
                    return currentResolver.resolve(critSet).iterator();
                } catch (final ResolverException e) {
                    log.error(String.format("Error resolving credentials from chaining resolver member '%s'",
                            currentResolver.getClass().getName()), e);
                    if (resolverIterator.hasNext()) {
                        log.error("Will attempt to resolve credentials from next member of resolver chain");
                    }
                }
            }

            log.debug("No more credential resolvers available in the resolver chain");
            currentResolver = null;
            return null;
        }

        /**
         * Get the next credential that will be returned by this iterator.
         * 
         * @return the next credential to return, or null if none is available
         */
        @Nullable private Credential getNextCredential() {
            if (credentialIterator != null) {
                if (credentialIterator.hasNext()) {
                    return credentialIterator.next();
                }
            }

            credentialIterator = getNextCredentialIterator();
            while (credentialIterator != null) {
                if (credentialIterator.hasNext()) {
                    return credentialIterator.next();
                }
                credentialIterator = getNextCredentialIterator();
            }

            return null;
        }

    }

}