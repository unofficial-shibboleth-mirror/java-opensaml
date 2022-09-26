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

package org.opensaml.xmlsec;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * The algorithm policy parameters.
 */
public class AlgorithmPolicyParameters {
    
    /** Included algorithm URIs. */
    @Nonnull @NonnullElements private Collection<String> includedAlgorithmURIs;
    
    /** Excluded algorithm URIs. */
    @Nonnull @NonnullElements private Collection<String> excludedAlgorithmURIs;
        
    /** Constructor. */
    public AlgorithmPolicyParameters() {
        includedAlgorithmURIs = Collections.emptySet();
        excludedAlgorithmURIs = Collections.emptySet();
    }
    
    /**
     * Get the included algorithm URIs.
     * 
     * @return the included algorithms
     */
    @Nonnull @NonnullElements @NotLive @Unmodifiable public Collection<String> getIncludedAlgorithms() {
        return includedAlgorithmURIs;
    }
    
    /**
     * Set the included algorithm URIs.
     * 
     * @param uris the included algorithms
     */
    public void setIncludedAlgorithms(@Nullable final Collection<String> uris) {
        if (uris == null) {
            includedAlgorithmURIs = Collections.emptySet();
            return;
        }
        includedAlgorithmURIs = Set.copyOf(StringSupport.normalizeStringCollection(uris));
    }
    
    /**
     * Get the excluded algorithm URIs.
     * 
     * @return the excluded algorithms
     */
    @Nonnull @NonnullElements @NotLive @Unmodifiable public Collection<String> getExcludedAlgorithms() {
        return excludedAlgorithmURIs;
    }
    
    /**
     * Set the excluded algorithm URIs.
     * 
     * @param uris the excluded algorithms
     */
    public void setExcludedAlgorithms(@Nonnull @NonnullElements final Collection<String> uris) {
        if (uris == null) {
            excludedAlgorithmURIs = Collections.emptySet();
            return;
        }
        excludedAlgorithmURIs = Set.copyOf(StringSupport.normalizeStringCollection(uris));
    }
    
}