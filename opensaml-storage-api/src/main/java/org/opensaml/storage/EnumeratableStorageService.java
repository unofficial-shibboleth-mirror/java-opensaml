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

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * An extended {@link StorageService} able to enumerate the keys in a context.
 * 
 * <p>In principle, it is theoretically possible to implement this extension in a
 * stateful manner, thus the use of {@link Iterable}. In practice, this is exceedingly
 * unlikely to work given the locking requirements and risks of contention this would
 * create.</p>
 * 
 * @since 5.0.0
 */
public interface EnumeratableStorageService extends StorageService {

    /**
     * Return an iterable collection of the keys stored in a context.
     * 
     * @param context the context to enumerate
     * @param prefix optional prefix to filter keys
     * 
     * @return keys stored in a context
     * 
     * @throws IOException on error 
     */
    @Nonnull Iterable<String> getContextKeys(@Nonnull @NotEmpty final String context, @Nullable final String prefix)
            throws IOException;
    
}