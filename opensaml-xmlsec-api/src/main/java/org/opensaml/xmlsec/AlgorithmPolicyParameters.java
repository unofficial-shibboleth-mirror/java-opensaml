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

package org.opensaml.xmlsec;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * The algorithm policy parameters.
 */
public class AlgorithmPolicyParameters {
    
    /** Included algorithm URIs. */
    @Nonnull private Collection<String> includedAlgorithmURIs;
    
    /** Excluded algorithm URIs. */
    @Nonnull private Collection<String> excludedAlgorithmURIs;
        
    /** Constructor. */
    public AlgorithmPolicyParameters() {
        includedAlgorithmURIs = CollectionSupport.emptySet();
        excludedAlgorithmURIs = CollectionSupport.emptySet();
    }
    
    /**
     * Get the included algorithm URIs.
     * 
     * @return the included algorithms
     */
    @Nonnull @NotLive @Unmodifiable public Collection<String> getIncludedAlgorithms() {
        return includedAlgorithmURIs;
    }
    
    /**
     * Set the included algorithm URIs.
     * 
     * @param uris the included algorithms
     */
    public void setIncludedAlgorithms(@Nullable final Collection<String> uris) {
        if (uris == null) {
            includedAlgorithmURIs = CollectionSupport.emptySet();
            return;
        }
        includedAlgorithmURIs = CollectionSupport.copyToSet(StringSupport.normalizeStringCollection(uris));
    }
    
    /**
     * Get the excluded algorithm URIs.
     * 
     * @return the excluded algorithms
     */
    @Nonnull @NotLive @Unmodifiable public Collection<String> getExcludedAlgorithms() {
        return excludedAlgorithmURIs;
    }
    
    /**
     * Set the excluded algorithm URIs.
     * 
     * @param uris the excluded algorithms
     */
    public void setExcludedAlgorithms(@Nullable final Collection<String> uris) {
        if (uris == null) {
            excludedAlgorithmURIs = CollectionSupport.emptySet();
            return;
        }
        excludedAlgorithmURIs = CollectionSupport.copyToSet(StringSupport.normalizeStringCollection(uris));
    }
    
}