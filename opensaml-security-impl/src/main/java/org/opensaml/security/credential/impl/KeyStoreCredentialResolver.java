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

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.UnrecoverableEntryException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.X509Credential;
import org.slf4j.Logger;

/**
 * A {@link org.opensaml.security.credential.CredentialResolver} that extracts {@link Credential}'s from a key store.
 * 
 * <p>If no key usage type is presented at construction time this resolver will return the key, if available, regardless
 * of the usage type provided to its resolve method.</p>
 * 
 * <p>Resolution will fail if an {@link EntityIdCriterion} is not part of the input criteria set.</p>
 */
public class KeyStoreCredentialResolver extends AbstractCriteriaFilteringCredentialResolver {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(KeyStoreCredentialResolver.class);

    /** Key store credentials are retrieved from. */
    @Nonnull private final KeyStore keyStore;

    /** Passwords for keys. The key must be the entityID, the value the password. */
    @Nonnull private final Map<String, String> keyPasswords;

    /** Usage type of all keys in the store. */
    @Nonnull private final UsageType keystoreUsage;

    /**
     * Constructor.
     * 
     * @param store key store credentials are retrieved from
     * @param passwords for key entries, map key is the entity id, map value is the password
     * 
     */
    public KeyStoreCredentialResolver(@Nonnull final KeyStore store, @Nonnull final Map<String, String> passwords) {
        this(store, passwords, null);
    }

    /**
     * Constructor.
     * 
     * @param store key store credentials are retrieved from
     * @param passwords for key entries, map key is the entity id, map value is the password
     * @param usage usage type of all keys in the store
     * 
     */
    public KeyStoreCredentialResolver(@Nonnull final KeyStore store, @Nonnull final Map<String, String> passwords,
            @Nullable final UsageType usage) {
        super();

        keyStore = Constraint.isNotNull(store, "Provided key store cannot be null");
        keyPasswords = Constraint.isNotNull(passwords, "Password map cannot be null");

        try {
            store.size();
        } catch (final KeyStoreException e) {
            throw new IllegalStateException("Keystore has not been initialized.");
        }

        if (usage != null) {
            keystoreUsage = usage;
        } else {
            keystoreUsage = UsageType.UNSPECIFIED;
        }
    }

    /** {@inheritDoc} */
    @Nonnull protected Iterable<Credential> resolveFromSource(@Nullable final CriteriaSet criteriaSet)
            throws ResolverException {

        final String entityID = checkCriteriaRequirements(criteriaSet).getEntityId();
        
        final UsageCriterion usageCriteria = criteriaSet != null ? criteriaSet.get(UsageCriterion.class) : null;
        final UsageType usage;
        if (usageCriteria != null) {
            usage = usageCriteria.getUsage();
        } else {
            usage = UsageType.UNSPECIFIED;
        }
        if (!matchUsage(keystoreUsage, usage)) {
            log.debug("Specified usage criteria {} does not match keystore usage {}", usage, keystoreUsage);
            log.debug("Can not resolve credentials from this keystore");
            return CollectionSupport.emptySet();
        }

        KeyStore.PasswordProtection keyPassword = null;
        if (keyPasswords.containsKey(entityID)) {
            keyPassword = new KeyStore.PasswordProtection(keyPasswords.get(entityID).toCharArray());
        }

        KeyStore.Entry keyStoreEntry = null;
        try {
            keyStoreEntry = keyStore.getEntry(entityID, keyPassword);
        } catch (final UnrecoverableEntryException e) {
            log.error("Unable to retrieve keystore entry for entityID (keystore alias): {}", entityID);
            log.error("Check for invalid keystore entityID/alias entry password");
            throw new ResolverException("Could not retrieve entry from keystore", e);
        } catch (final GeneralSecurityException e) {
            log.error("Unable to retrieve keystore entry for entityID (keystore alias): {}: {}", entityID,
                    e.getMessage());
            throw new ResolverException("Could not retrieve entry from keystore", e);
        }

        if (keyStoreEntry == null) {
            log.debug("Keystore entry for entityID (keystore alias) {} does not exist", entityID);
            return CollectionSupport.emptySet();
        }

        final Credential credential = buildCredential(keyStoreEntry, entityID, keystoreUsage);
        return CollectionSupport.singleton(credential);
    }

    /**
     * Check that required credential criteria are available.
     * 
     * @param criteriaSet the credential criteria set to evaluate
     * 
     * @return the required {@link EntityIdCriterion}. 
     */
    @Nonnull protected EntityIdCriterion checkCriteriaRequirements(@Nullable final CriteriaSet criteriaSet) {
        
        final EntityIdCriterion criterion = criteriaSet != null ? criteriaSet.get(EntityIdCriterion.class) : null;
        
        if (criterion == null) {
            log.error("EntityIDCriterion was not specified in the criteria set, resolution cannot be attempted");
            throw new IllegalArgumentException("No EntityIDCriterion was available in criteria set");
        }
        
        return criterion;
    }

    /**
     * Match usage enum type values from keystore configured usage and from credential criteria.
     * 
     * @param keyStoreUsage the usage type configured for the keystore
     * @param criteriaUsage the value from credential criteria
     * @return true if the two usage specifiers match for purposes of resolving credentials, false otherwise
     */
    protected boolean matchUsage(@Nonnull final UsageType keyStoreUsage, @Nonnull final UsageType criteriaUsage) {
        if (keyStoreUsage == UsageType.UNSPECIFIED || criteriaUsage == UsageType.UNSPECIFIED) {
            return true;
        }
        return keyStoreUsage == criteriaUsage;
    }

    /**
     * Build a credential instance from the key store entry.
     * 
     * @param keyStoreEntry the key store entry to process
     * @param entityID the entityID to include in the credential
     * @param usage the usage type to include in the credential
     * @return the new credential instance, appropriate to the type of key store entry being processed
     * @throws ResolverException throw if there is a problem building a credential from the key store entry
     */
    @Nonnull protected Credential buildCredential(@Nonnull final KeyStore.Entry keyStoreEntry,
            @Nonnull final String entityID, @Nonnull final UsageType usage) throws ResolverException {

        log.debug("Building credential from keystore entry for entityID {}, usage type {}", entityID, usage);

        if (keyStoreEntry instanceof KeyStore.PrivateKeyEntry entry) {
            return processPrivateKeyEntry(entry, entityID, keystoreUsage);
        } else if (keyStoreEntry instanceof KeyStore.TrustedCertificateEntry entry) {
            return processTrustedCertificateEntry(entry, entityID, keystoreUsage);
        } else if (keyStoreEntry instanceof KeyStore.SecretKeyEntry entry) {
            return processSecretKeyEntry(entry, entityID, keystoreUsage);
        } else {
            throw new ResolverException("KeyStore entry was of an unsupported type: "
                    + keyStoreEntry.getClass().getName());
        }
    }

    /**
     * Build an X509Credential from a keystore trusted certificate entry.
     * 
     * @param trustedCertEntry the entry being processed
     * @param entityID the entityID to set
     * @param usage the usage type to set
     * @return new X509Credential instance
     */
    @Nonnull protected X509Credential processTrustedCertificateEntry(
            @Nonnull final KeyStore.TrustedCertificateEntry trustedCertEntry, @Nonnull final String entityID,
            @Nonnull final UsageType usage) {

        log.debug("Processing TrustedCertificateEntry from keystore");

        final X509Certificate cert = (X509Certificate) trustedCertEntry.getTrustedCertificate();

        final BasicX509Credential credential = new BasicX509Credential(cert);
        credential.setEntityId(entityID);
        credential.setUsageType(usage);

        final ArrayList<X509Certificate> certChain = new ArrayList<>();
        certChain.add(cert);
        credential.setEntityCertificateChain(certChain);

        return credential;
    }

    /**
     * Build an X509Credential from a keystore private key entry.
     * 
     * @param privateKeyEntry the entry being processed
     * @param entityID the entityID to set
     * @param usage the usage type to set
     * @return new X509Credential instance
     */
    @Nonnull protected X509Credential processPrivateKeyEntry(@Nonnull final KeyStore.PrivateKeyEntry privateKeyEntry,
            @Nonnull final String entityID, @Nonnull final UsageType usage) {

        log.debug("Processing PrivateKeyEntry from keystore");

        final BasicX509Credential credential = 
                new BasicX509Credential((X509Certificate) privateKeyEntry.getCertificate(), 
                        privateKeyEntry.getPrivateKey());
        credential.setEntityId(entityID);
        credential.setUsageType(usage);

        credential.setEntityCertificateChain(Arrays.asList((X509Certificate[]) privateKeyEntry.getCertificateChain()));

        return credential;
    }

    /**
     * Build a Credential from a keystore secret key entry.
     * 
     * @param secretKeyEntry the entry being processed
     * @param entityID the entityID to set
     * @param usage the usage type to set
     * @return new Credential instance
     */
    @Nonnull protected Credential processSecretKeyEntry(@Nonnull final SecretKeyEntry secretKeyEntry,
            @Nonnull final String entityID, @Nonnull final UsageType usage) {
        log.debug("Processing SecretKeyEntry from keystore");

        final BasicCredential credential = new BasicCredential(secretKeyEntry.getSecretKey());
        credential.setEntityId(entityID);
        credential.setUsageType(usage);

        return credential;
    }
}