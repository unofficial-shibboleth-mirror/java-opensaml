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
import net.shibboleth.shared.annotation.constraint.ThreadSafeAfterInit;
import net.shibboleth.shared.component.InitializableComponent;

/**
 * Interface to a serialization/deserialization process used by a {@link StorageService} implementation
 * to optimize the handling of complex objects.
 * 
 * @param <Type> the type of object handled
 */
@ThreadSafeAfterInit
public interface StorageSerializer<Type> extends InitializableComponent {

    /**
     * Returns a string representing the input object.
     * 
     * @param instance object to serialize
     * @return a string
     * @throws IOException if an error occurs during serialization
     */
    @Nonnull @NotEmpty String serialize(@Nonnull final Type instance) throws IOException;
    
    /**
     * Returns an object recovered from a string produced through the {@link #serialize} method.
     * 
     * @param version record version
     * @param context context of record
     * @param key key of record
     * @param value data to deserialize
     * @param expiration expiration of record, if any
     * @return a deserialized object
     * @throws IOException if an error occurs during deserialization
     */
    @Nonnull Type deserialize(final long version, @Nonnull @NotEmpty final String context,
            @Nonnull @NotEmpty final String key, @Nonnull @NotEmpty final String value, @Nullable Long expiration)
                    throws IOException;
}