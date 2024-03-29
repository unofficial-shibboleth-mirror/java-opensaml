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

/**
 * Exposes capabilities of a {@link StorageService} implementation.
 */
public interface StorageCapabilities {
    
    /**
     * Gets max size of context labels in characters.
     * 
     * @return  max size of context labels in characters
     */
    int getContextSize();

    /**
     * Gets max size of keys in characters.
     * 
     * @return  max size of keys in characters
     */
    int getKeySize();
    
    /**
     * Gets max size of values in characters.
     * 
     * @return  max size of values in characters
     */
    long getValueSize();
    
    /**
     * Returns true iff the storage implementation manages data independent of the client.
     * 
     * @return  true iff the storage implementation manages data independent of the client
     * 
     * @since 5.0.0
     */
    boolean isServerSide();

    /**
     * Returns true iff the storage implementation manages data independent of a single server node.
     * 
     * @return true iff the storage implementation manages data independent of a single server node
     * 
     * @since 5.0.0
     */
    boolean isClustered();

}