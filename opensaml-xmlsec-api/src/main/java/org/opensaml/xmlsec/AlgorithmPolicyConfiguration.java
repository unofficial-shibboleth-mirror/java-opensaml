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

import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * Algorithm URI include/exclude policy configuration.
 */
public interface AlgorithmPolicyConfiguration {
    
    /** Rule precedence values. */
    enum Precedence {
        /** Include takes precedence over exclude. */
        INCLUDE,
        
        /** Exclude takes precedence over include. */
        EXCLUDE
    }
    
    /**
     * Get the collection of included algorithm URIs.
     * 
     * @return the collection of algorithms
     */
    @Nonnull @Unmodifiable @NotLive Collection<String> getIncludedAlgorithms();
    
    /**
     * Flag indicating whether to merge this configuration's includes with one of a lower order of precedence,
     * or to treat this include collection as authoritative.
     * 
     * @return true if should merge, false otherwise
     */
    boolean isIncludeMerge();
    
    /**
     * Get the collection of excluded algorithm URIs.
     * 
     * @return the collection of algorithms
     */
    @Nonnull @Unmodifiable @NotLive Collection<String> getExcludedAlgorithms();
    
    /**
     * Flag indicating whether to merge this configuration's excludes with one of a lower order of precedence,
     * or to treat this exclude collection as authoritative.
     * 
     * @return true if should merge, false otherwise
     */
    boolean isExcludeMerge();
    
    /**
     * Get preference value indicating which should take precedence when both include and exclude collections
     * are non-empty.
     * 
     * @return the configured precedence value.
     */
    @Nonnull Precedence getIncludeExcludePrecedence();

}