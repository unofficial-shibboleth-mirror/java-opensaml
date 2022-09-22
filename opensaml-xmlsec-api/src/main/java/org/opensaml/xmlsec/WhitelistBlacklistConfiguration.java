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

import javax.annotation.Nonnull;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;

/**
 * Algorithm URI include/exclude policy configuration.
 * 
 * <p>Replace with {@link AlgorithmPolicyConfiguration}.</p> 
 * 
 * @deprecated
 */
@Deprecated(forRemoval=true, since="4.1.0")
public interface WhitelistBlacklistConfiguration extends AlgorithmPolicyConfiguration {

    /** Whitelist/blacklist precedence values. */
    public enum Precedence {
        /** Whitelist takes precedence over blacklist. */
        WHITELIST,
        
        /** Blacklist takes precedence over whitelist. */
        BLACKLIST
    }

    /**
     * Get the list of whitelisted algorithm URIs.
     * 
     * @return the list of algorithms
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive Collection<String> getWhitelistedAlgorithms();
    
    /**
     * Flag indicating whether to merge this configuration's whitelist with one of a lower order of precedence,
     * or to treat this whitelist as authoritative.
     * 
     * @return true if should merge, false otherwise
     */
    boolean isWhitelistMerge();
    
    /**
     * Get the list of blacklisted algorithm URIs.
     * 
     * @return the list of algorithms
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive Collection<String> getBlacklistedAlgorithms();
    
    /**
     * Flag indicating whether to merge this configuration's blacklist with one of a lower order of precedence,
     * or to treat this blacklist as authoritative.
     * 
     * @return true if should merge, false otherwise
     */
    boolean isBlacklistMerge();
    
    /**
     * Get preference value indicating which should take precedence when both whitelist and blacklist are non-empty.
     * 
     * @return the configured precedence value.
     */
    @Nonnull Precedence getWhitelistBlacklistPrecedence();

}