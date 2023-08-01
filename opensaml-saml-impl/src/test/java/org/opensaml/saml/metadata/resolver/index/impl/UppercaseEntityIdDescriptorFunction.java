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

package org.opensaml.saml.metadata.resolver.index.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.opensaml.saml.metadata.resolver.index.MetadataIndexKey;
import org.opensaml.saml.metadata.resolver.index.SimpleStringMetadataIndexKey;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

import net.shibboleth.shared.collection.CollectionSupport;

@SuppressWarnings("javadoc")
public class UppercaseEntityIdDescriptorFunction implements Function<EntityDescriptor, Set<MetadataIndexKey>> {
    
    @Nullable public Set<MetadataIndexKey> apply(@Nullable EntityDescriptor input) {
        if (input == null) {
            return CollectionSupport.emptySet();
        }
        
        final String entityID = input.getEntityID();
        if (entityID == null) {
            return CollectionSupport.emptySet();
        }
        
        HashSet<MetadataIndexKey> result = new HashSet<>();
        if (input != null) {
            result.add(new SimpleStringMetadataIndexKey(entityID.toUpperCase()));
        }
        return result;
    }
    
}