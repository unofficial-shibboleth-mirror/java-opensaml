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

package org.opensaml.storage;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import javax.annotation.Nonnull;

import net.shibboleth.utilities.java.support.annotation.constraint.NonnullAfterInit;
import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;
import net.shibboleth.utilities.java.support.annotation.constraint.Positive;
import net.shibboleth.utilities.java.support.annotation.constraint.ThreadSafeAfterInit;
import net.shibboleth.utilities.java.support.component.AbstractIdentifiableInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.logic.Constraint;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores and checks for revocation entries.
 * 
 * <p>
 * This class is thread-safe and uses a synchronized method to prevent race conditions within the underlying store
 * (lacking an atomic "check and insert" operation).
 * </p>
 * 
 * @since 4.2.0
 */
@ThreadSafeAfterInit
public class RevocationCache extends AbstractIdentifiableInitializableComponent {

    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(RevocationCache.class);

    /** Backing storage for the replay cache. */
    @NonnullAfterInit private StorageService storage;

    /** Flag controlling behavior on storage failure. */
    private boolean strict;

    /** Default lifetime of revocation entry. Default value: 6 hours */
    @Nonnull @Positive private Duration expires;

    /**
     * Constructor.
     */
    public RevocationCache() {
        expires = Duration.ofHours(6);
    }

    /**
     * Set the default revocation entry expiration.
     * 
     * @param entryExpiration lifetime of an revocation entry in milliseconds
     */
    public void setEntryExpiration(@Positive final Duration entryExpiration) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
        
        Constraint.isTrue(entryExpiration != null && !entryExpiration.isNegative() && !entryExpiration.isZero(),
                "Revocation cache default entry expiration must be greater than 0");
        expires = entryExpiration;
    }

    /**
     * Get the backing store for the cache.
     * 
     * @return the backing store.
     */
    @NonnullAfterInit public StorageService getStorage() {
        return storage;
    }

    /**
     * Set the backing store for the cache.
     * 
     * @param storageService backing store to use
     */
    public void setStorage(@Nonnull final StorageService storageService) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        storage = Constraint.isNotNull(storageService, "StorageService cannot be null");
        final StorageCapabilities caps = storage.getCapabilities();
        if (caps instanceof StorageCapabilitiesEx) {
            Constraint.isTrue(((StorageCapabilitiesEx) caps).isServerSide(), "StorageService cannot be client-side");
        }
    }

    /**
     * Get the strictness flag.
     * 
     * @return true iff we should treat storage failures as a replay
     */
    public boolean isStrict() {
        return strict;
    }

    /**
     * Set the strictness flag.
     * 
     * @param flag true iff we should treat storage failures as a replay
     */
    public void setStrict(final boolean flag) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        strict = flag;
    }

    /** {@inheritDoc} */
    @Override
    public void doInitialize() throws ComponentInitializationException {
        if (storage == null) {
            throw new ComponentInitializationException("StorageService cannot be null");
        }
    }
    

    /**
     * Invokes {@link #revoke(String, String, Duration)} with a default expiration parameter.
     * 
     * @param context a context label to subdivide the cache
     * @param s value to revoke
     * 
     * @return true if value has successfully been listed as revoked in the cache.
     */
    public synchronized boolean revoke(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String s) {
        return revoke(context, s, expires);
    }    

    /**
     * Returns true if the value is successfully revoked. If value has already been revoked, expiration is updated.
     * 
     * @param context a context label to subdivide the cache
     * @param s value to revoke
     * @param exp entry expiration
     * 
     * @return true if value has successfully been listed as revoked in the cache.
     */
    public synchronized boolean revoke(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String s,
            @Nonnull final Duration exp) {
        final String key;

        final StorageCapabilities caps = storage.getCapabilities();
        if (context.length() > caps.getContextSize()) {
            log.error("context {} too long for StorageService (limit {})", context, caps.getContextSize());
            return false;
        } else if (s.length() > caps.getKeySize()) {
            key = DigestUtils.sha1Hex(s);
        } else {
            key = s;
        }
        try {
            final StorageRecord<?> entry = storage.read(context, key);
            if (entry == null) {
                log.debug("Entry '{}' of context '{}' is not yet on list of revoked entries,"
                        + " adding to cache with expiration time {}", key, context, expires);
                storage.create(context, key, "y", Instant.now().plus(exp).toEpochMilli());
                return true;
            }
            
            storage.update(context, key, "y", Instant.now().plus(exp).toEpochMilli());
            log.debug("Entry '{}' of context '{}' was already revoked, updating expiration", key, context);
            return true;
        } catch (final IOException e) {
            log.error("Exception reading/writing to storage service, returning {}", strict ? "failure" : "success", e);
            return !strict;
        }
    }

    /**
     * Returns false if the value has successfully been confirmed as not revoked.
     * 
     * @param context a context label to subdivide the cache
     * @param s value to revoke
     * 
     * @return false if the check value is not found in the cache
     */
    public synchronized boolean isRevoked(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String s) {
        final String key;
        final StorageCapabilities caps = storage.getCapabilities();
        if (context.length() > caps.getContextSize()) {
            log.error("context {} too long for StorageService (limit {})", context, caps.getContextSize());
            return true;
        } else if (s.length() > caps.getKeySize()) {
            key = DigestUtils.sha1Hex(s);
        } else {
            key = s;
        }

        try {
            final StorageRecord<?> entry = storage.read(context, key);
            if (entry == null) {
                log.debug("Entry '{}' is not revoked", key);
                return false;
            }
            
            log.debug("Entry '{}' is revoked", s);
            return true;
        } catch (final IOException e) {
            log.error("Exception reading/writing to storage service, returning {}", strict ? "failure" : "success", e);
            return !strict;
        }
    }

}