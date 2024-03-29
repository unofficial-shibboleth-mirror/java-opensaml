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

package org.opensaml.saml.metadata.resolver.index;

/**
 * Marker interface for a component which represents a key for an index defined by a {@link MetadataIndex}.
 * 
 * <p>
 * Implementations MUST override and implement {@link Object#hashCode()} and {@link Object#equals(Object)}
 * based on the semantics represented by the index key data.
 * </p>
 */
public interface MetadataIndexKey {
    
}