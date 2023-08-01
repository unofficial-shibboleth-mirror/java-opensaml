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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.storage.MutableStorageRecord;
import org.opensaml.storage.impl.client.ClientStorageService.ClientStorageSource;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.logic.Constraint;

/**
 * Base class for the storage and reconstitution of data for a {@link ClientStorageService}.
 * 
 * @since 4.1.0
 */
public abstract class AbstractClientStorageServiceStore implements ClientStorageServiceStore {

    /** The underlying map of data records. */
    @Nonnull private final Map<String, Map<String, MutableStorageRecord<?>>> contextMap;
    
    /** Data source. */
    @Nullable private ClientStorageSource source;
    
    /** Dirty bit. */
    private boolean dirty;

    /**
     * Reconstitute stored data.
     */
    AbstractClientStorageServiceStore() {
        contextMap = new HashMap<>();
    }

    /** {@inheritDoc} */
    @Nullable public ClientStorageSource getSource() {
        return source;
    }

    /** {@inheritDoc} */
    public boolean isDirty() {
        return dirty;
    }
    
    /**
     * Set the dirty bit for the current data.
     * 
     * @param flag  dirty bit to set
     */
    public void setDirty(final boolean flag) {
        dirty = flag;
    }

    /** {@inheritDoc} */
    @Nonnull @Live public Map<String,Map<String,MutableStorageRecord<?>>> getContextMap() {
        return contextMap;
    }
    
    /** {@inheritDoc} */
    public void load(@Nullable @NotEmpty final String raw, @Nonnull final ClientStorageSource src) {
        
        contextMap.clear();
        source = Constraint.isNotNull(src, "ClientStorageSource cannot be null");
        
        if (raw != null) {
            try {
                doLoad(raw);
            } catch (final IOException e) {
                contextMap.clear();
                // Setting this should force corrupt data in the client to be overwritten.
                setDirty(true);
            }
        }
    }

    
    /**
     * Reconstitute stored data.
     * 
     * @param raw serialized data to load
     * 
     * @throws IOException if an error occurs
     */
    public abstract void doLoad(@Nullable @NotEmpty final String raw) throws IOException;
    
    /** {@inheritDoc} */
    @Nullable public abstract ClientStorageServiceOperation save(@Nonnull final ClientStorageService storageService)
            throws IOException;
    
}