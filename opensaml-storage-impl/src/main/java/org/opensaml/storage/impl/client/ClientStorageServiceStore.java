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

package org.opensaml.storage.impl.client;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.storage.MutableStorageRecord;
import org.opensaml.storage.impl.client.ClientStorageService.ClientStorageSource;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Abstraction for the storage and reconstitution of data for a {@link ClientStorageService}.
 * 
 * @since 4.1.0
 */
public interface ClientStorageServiceStore {

    /**
     * Get the data source.
     * 
     * @return data source
     */
    @Nullable ClientStorageSource getSource();

    /**
     * Get the dirty bit for the current data.
     * 
     * @return  status of dirty bit
     */
    boolean isDirty();
    
    /**
     * Set the dirty bit for the current data.
     * 
     * @param flag  dirty bit to set
     */
    void setDirty(final boolean flag);

    /**
     * Get the map of contexts to manipulate during operations.
     * 
     * @return map of contexts to manipulate
     */
    @Nonnull @NonnullElements @Live Map<String,Map<String,MutableStorageRecord<?>>> getContextMap();
    
    /**
     * Reconstitute stored data.
     * 
     * <p>The dirty bit is set based on the result. If successful, the bit is cleared,
     * but if an error occurs, it will be set.</p>
     * 
     * <p>By design this method should not throw under any non-catastrophic conditions.</p>
     * 
     * @param raw serialized data to load
     * @param src storage source
     */
    void load(@Nullable @NotEmpty final String raw, @Nonnull final ClientStorageSource src);
    
    /**
     * Serialize current state of stored data into a storage operation.
     * 
     * @param storageService storage service
     * 
     * @return the operation, or a null if the data has not been modified since loading or saving
     * 
     * @throws IOException if an error occurs
     */
    @Nullable ClientStorageServiceOperation save(@Nonnull final ClientStorageService storageService) throws IOException;
    
    /**
     * A factory for producing new {@link ClientStorageServiceStore} instances.
     */
    interface Factory {
        
        /**
         * Load raw data into a new {@link ClientStorageServiceStore} instance.
         * 
         * @param raw data to load
         * @param src data source
         * 
         * @return new store instance
         */
        @Nonnull ClientStorageServiceStore load(@Nullable @NotEmpty final String raw,
                @Nonnull final ClientStorageSource src);
    }
    
}