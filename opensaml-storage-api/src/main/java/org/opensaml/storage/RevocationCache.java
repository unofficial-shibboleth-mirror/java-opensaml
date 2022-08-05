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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import net.shibboleth.utilities.java.support.annotation.constraint.NotEmpty;

/**
 * Interface to a cache that tracks revoked information.
 * 
 * <p>Revocation may include specific information for storage and retrieval,
 * or simply a tracking of revoked status.</p>
 */
@ThreadSafe
public interface RevocationCache {

    /**
     * Invokes {@link #revoke(String, String, Duration)} with a default expiration parameter.
     * 
     * @param context a context label to subdivide the cache
     * @param key key to revoke
     * 
     * @return true if key has successfully been listed as revoked in the cache
     */
    default boolean revoke(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final  String key) {
        return revoke(context, key, "y");
    }

    /**
     * Invokes {@link #revoke(String, String, String, Duration)} with a placeholder value parameter.
     * 
     * @param context a context label to subdivide the cache
     * @param key key to revoke
     * @param exp entry expiration
     * 
     * @return true if key has successfully been listed as revoked in the cache
     * 
     * @since 4.3.0
     */
    default boolean revoke(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull final Duration exp) {
        return revoke(context, key, "y", exp);
    }

    /**
     * Invokes {@link #revoke(String, String, String, Duration)} with a default expiration parameter.
     * 
     * <p>If the key has already been revoked, expiration is updated.</p>
     * 
     * @param context a context label to subdivide the cache
     * @param key key to revoke
     * @param value value to insert into revocation record
     * 
     * @return true if key has successfully been listed as revoked in the cache
     * 
     * @since 4.3.0
     */
    boolean revoke(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value);

    /**
     * Returns true if the value is successfully revoked.
     * 
     * <p>If the key has already been revoked, expiration is updated.</p>
     * 
     * @param context a context label to subdivide the cache
     * @param key key to revoke
     * @param value value to insert into revocation record
     * @param exp entry expiration
     * 
     * @return true if key has successfully been listed as revoked in the cache
     * 
     * @since 4.3.0
     */
    boolean revoke(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull @NotEmpty final String value, @Nonnull final Duration exp);

    /**
     * Remove a revocation record.
     * 
     * @param context a context label to subdivide the cache
     * @param key value to remove
     * 
     * @return true iff a record was removed
     * 
     * @since 4.3.0
     */
    boolean unrevoke(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key);

    /**
     * Returns true iff the value has been revoked.
     * 
     * @param context a context label to subdivide the cache
     * @param key value to check
     * 
     * @return true iff the check value is found in the cache
     */
    boolean isRevoked(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key);

    /**
     * Attempts to read back a revocation record for a given context and key.
     * 
     * <p>This alternative approach allows revocation records to include richer data,
     * rather than simple presence/absence as a signal.</p>
     * 
     * @param context revocation context
     * @param key revocation key
     * 
     * @return the matching record, if found, or null if absent
     * 
     * @throws IOException raised if an error occurs leading to an indeterminate result
     * 
     * @since 4.3.0
     */
    @Nullable String getRevocationRecord(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key)
            throws IOException;

}