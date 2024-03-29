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

import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.resolver.CriteriaSet;

@SuppressWarnings("javadoc")
public class SimpleStringCriteriaFunction implements Function<CriteriaSet, Set<MetadataIndexKey>> {
    
    @Nullable public Set<MetadataIndexKey> apply(@Nullable CriteriaSet input) {
        if (input == null) {
            return CollectionSupport.emptySet();
        }
        SimpleStringCriterion crit = input.get(SimpleStringCriterion.class);
        HashSet<MetadataIndexKey> result = new HashSet<>();
        if (crit != null) {
            result.add(new SimpleStringMetadataIndexKey(crit.getValue()));
        }
        return result;
    }
    
}