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

package org.opensaml.xmlsec.keyinfo.impl;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.criteria.KeyNameCriterion;
import org.opensaml.security.criteria.PublicKeyCriterion;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolutionMode;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolutionMode.Mode;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/**
 * A simple specialization of {@link BasicProviderKeyInfoCredentialResolver}
 * which is capable of using information from a {@link org.opensaml.xmlsec.signature.KeyInfo} to resolve
 * local credentials from a supplied {@link CredentialResolver} which manages local credentials.
 * 
 * <p>
 * The local credential resolver supplied should manage and return credentials
 * which contain either a secret (symmetric) key or the private key half of a
 * key pair.
 * </p>
 * 
 * <p>
 * A typical use case for this class would be as a resolver of decryption keys,
 * such as is needed by {@link org.opensaml.xmlsec.encryption.support.Decrypter}.
 * </p>
 * 
 * <p>
 * Resolution proceeds as follows:
 * </p>
 * <ol>
 *   <li>Any credential resolved via the standard {@link BasicProviderKeyInfoCredentialResolver}
 *       resolution process which is not a local credential will be removed
 *       from the effective set of credentials to be returned.  Note that a configured
 *       {@link KeyInfoProvider} may have itself already resolved local credentials using a
 *       different mechanism.  These will not be removed.</li>
 *   <li>If a credential so removed contained a public key, that key will be used as a
 *       resolution criteria input to the local credential resolver.  Any local credentials
 *       so resolved will be added to the set to be returned.</li>
 *   <li>Similarly, any key names from {@link KeyInfoResolutionContext#getKeyNames()} will also
 *       be used as resolution criteria for local credentials and the resultant credentials
 *       added to the set to be returned.</li>
 * </ol>
 */
public class LocalKeyInfoCredentialResolver extends BasicProviderKeyInfoCredentialResolver {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(LocalKeyInfoCredentialResolver.class);
    
    /** The resolver which is used to resolve local credentials. */
    @Nonnull private final CredentialResolver localCredResolver;

    /**
     * Constructor.
     *
     * @param keyInfoProviders the list of {@link KeyInfoProvider}s to use in this resolver
     * @param localCredentialResolver resolver of local credentials
     */
    public LocalKeyInfoCredentialResolver(
            @Nonnull @ParameterName(name="keyInfoProviders") final List<KeyInfoProvider> keyInfoProviders,
            @Nonnull @ParameterName(name="localCredentialResolver") final CredentialResolver localCredentialResolver) {
        super(keyInfoProviders);
        
        localCredResolver = Constraint.isNotNull(localCredentialResolver, "Local credential resolver cannot be null");
    }
    
    /**
     * Get the resolver for local credentials.
     * 
     * The credentials managed and returned by this resolver should all contain
     * either a secret (symmetric) or private key.
     *
     * @return resolver of local credentials
     */
    @Nonnull public CredentialResolver getLocalCredentialResolver() {
        return localCredResolver;
    }

    /** {@inheritDoc} */
    @Override
    protected void postProcess(@Nonnull final KeyInfoResolutionContext kiContext,
            @Nullable final CriteriaSet criteriaSet, @Nonnull final List<Credential> credentials)
                    throws ResolverException {
        
        final KeyInfoCredentialResolutionMode modeCriterion = criteriaSet != null
                ? criteriaSet.get(KeyInfoCredentialResolutionMode.class) : null;
        final Mode mode = modeCriterion != null ? modeCriterion.getMode() : Mode.LOCAL;

        log.debug("Resolution mode in effect is: {}", mode);

        if (Mode.PUBLIC == mode) {
            log.debug("Criteria indicates PUBLIC resolution mode, skipping explicit local credential resolution");
            return;
        }
        
        final ArrayList<Credential> results = new ArrayList<>();
        
        for (final Credential inputCred : credentials) {
            assert inputCred != null;
            if (isLocalCredential(inputCred)) {
                log.debug("Input credential was local, including in results");
                results.add(inputCred);
            } else {
                final PublicKey publicKey = inputCred.getPublicKey();
                if (publicKey != null) {
                    final Collection<? extends Credential> localCreds = resolveByPublicKey(publicKey);
                    if (!localCreds.isEmpty()) {
                        log.debug("Input credential was public, resolved to local credential(s), adding to results");
                        results.addAll(localCreds);
                    } else if (Mode.BOTH == mode) {
                        log.debug("Input credential was public, did not resolve to local credential(s), "
                                + "BOTH mode in effect, including in results");
                        results.add(inputCred);
                    } else {
                        log.debug("Input credential was public, did not resolve to local credential(s), "
                                + "LOCAL mode in effect, omitting from results");
                    }
                }
            }
        }
        
        // Also resolve local credentials based on any key names that are known
        for (final String keyName : kiContext.getKeyNames()) {
            assert keyName != null;
            results.addAll(resolveByKeyName(keyName));
        }
        
        credentials.clear();
        credentials.addAll(results);
    }
    
    /**
     * Determine whether the credential is a local credential.
     * 
     * A local credential will have either a private key or a secret (symmetric) key.
     * 
     * @param credential the credential to evaluate
     * @return true if the credential has either a private or secret key, false otherwise
     */
    protected boolean isLocalCredential(@Nonnull final Credential credential) {
        return credential.getPrivateKey() != null || credential.getSecretKey() != null;
    }

    /**
     * Resolve credentials from local resolver using key name criteria.
     * 
     * @param keyName the key name criteria
     * @return collection of local credentials identified by the specified key name
     * @throws ResolverException  thrown if there is a problem resolving credentials from the 
     *          local credential resolver
     */
    @Nonnull protected Collection<? extends Credential> resolveByKeyName(@Nonnull final String keyName)
            throws ResolverException {
        final ArrayList<Credential> localCreds = new ArrayList<>();
        
        final CriteriaSet criteriaSet = new CriteriaSet( new KeyNameCriterion(keyName) );
        for (final Credential cred : getLocalCredentialResolver().resolve(criteriaSet)) {
            assert cred != null;
            if (isLocalCredential(cred)) {
                localCreds.add(cred);
            }
        }
        
        return localCreds;
    }

    /**
     * Resolve credentials from local resolver using public key criteria.
     * 
     * @param publicKey the public key criteria
     * @return collection of local credentials which contain the private key
     *          corresponding to the specified public key
     * @throws ResolverException  thrown if there is a problem resolving credentials from the 
     *          local credential resolver
     */
    @Nonnull protected Collection<? extends Credential> resolveByPublicKey(@Nonnull final PublicKey publicKey)
            throws ResolverException {
        final ArrayList<Credential> localCreds = new ArrayList<>();
        
        final CriteriaSet criteriaSet = new CriteriaSet( new PublicKeyCriterion(publicKey) );
        for (final Credential cred : getLocalCredentialResolver().resolve(criteriaSet)) {
            assert cred != null;
            if (isLocalCredential(cred)) {
                localCreds.add(cred);
            }
        }
        
        return localCreds;
    }

}