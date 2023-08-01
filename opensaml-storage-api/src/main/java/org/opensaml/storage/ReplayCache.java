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

package org.opensaml.storage;

import java.time.Instant;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Interface to a component that checks for replay of a value.
 */
@ThreadSafe
public interface ReplayCache {

    /**
     * Returns true iff the check value is not found in the cache, and stores it.
     * 
     * @param context   a context label to subdivide the cache
     * @param key       key to check
     * @param expires   time for disposal of value from cache
     * 
     * @return true iff the check value is not found in the cache
     */
    boolean check(@Nonnull @NotEmpty final String context, @Nonnull @NotEmpty final String key,
            @Nonnull final Instant expires);

}