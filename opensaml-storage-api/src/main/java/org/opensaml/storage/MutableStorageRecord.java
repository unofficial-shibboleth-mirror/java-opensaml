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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotEmpty;

/**
 * Exposes mutation of {@link StorageRecord} properties.
 * 
 * @param <T> type of record
 */
public class MutableStorageRecord<T> extends StorageRecord<T> {
    
    /**
     * Constructor.
     *
     * @param val   value
     * @param exp   expiration, or null if none
     */
    public MutableStorageRecord(@Nonnull @NotEmpty final String val, @Nullable final Long exp) {
        super(val, exp);
    }    

    /** {@inheritDoc} */
    @Override
    public void setValue(@Nonnull @NotEmpty final String val) {
        super.setValue(val);
    }

    /** {@inheritDoc} */
    @Override
    public void setExpiration(@Nullable final Long exp) {
        super.setExpiration(exp);
    }

    /** {@inheritDoc} */
    @Override
    public long incrementVersion() {
        return super.incrementVersion();
    }
    
}