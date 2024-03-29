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

package org.opensaml.storage.impl.client;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.opensaml.messaging.context.BaseContext;

import net.shibboleth.shared.annotation.constraint.Live;

/**
 * A subcontext for driving the loading of data from a client into one or more
 * instances of a {@link ClientStorageService}.
 */
public class ClientStorageLoadContext extends BaseContext {

    /** The collection of storage keys to load from the client. */
    @Nonnull private Collection<String> storageKeys;
    
    /** Constructor. */
    public ClientStorageLoadContext() {
        storageKeys = new ArrayList<>();
    }
    
    /**
     * Get the collection of storage keys to load from the client.
     * 
     * @return modifiable collection of storage keys to load from the client
     */
    @Nonnull @Live public Collection<String> getStorageKeys() {
        return storageKeys;
    }

}