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

package org.opensaml.xmlsec.impl;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.xmlsec.AlgorithmPolicyConfiguration;
import org.opensaml.xmlsec.WhitelistBlacklistConfiguration;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.primitive.DeprecationSupport;
import net.shibboleth.shared.primitive.DeprecationSupport.ObjectType;
import net.shibboleth.utilities.java.support.logic.Constraint;

/**
 * Basic implementation of {@link WhitelistBlacklistConfiguration}.
 * 
 * <p>
 * The value returned by {@link WhitelistBlacklistConfiguration#getWhitelistBlacklistPrecedence()} defaults to
 * {@link org.opensaml.xmlsec.WhitelistBlacklistConfiguration.Precedence#WHITELIST}.
 * </p>
 * 
 * @deprecated
 */
@Deprecated(forRemoval=true, since="4.1.0")
public class BasicWhitelistBlacklistConfiguration extends BasicAlgorithmPolicyConfiguration
        implements WhitelistBlacklistConfiguration {
    
    /**
     * Flag indicating whether to merge this configuration's whitelist with one of a lower order of precedence,
     * or to treat this whitelist as authoritative.
     * 
     * @return true if should merge, false otherwise
     */
    public boolean isWhitelistMerge() {
        DeprecationSupport.warn(ObjectType.METHOD, "isWhitelistMerge", null, "isIncludeMerge");
        return isIncludeMerge();
    }
    

    /**
     * Set the flag indicating whether to merge this configuration's whitelist with one of a lower order of precedence,
     * or to treat this whitelist as authoritative.
     * 
     * <p>Defaults to: <code>false</code>
     * 
     * @param flag true if should merge, false otherwise
     */
    public void setWhitelistMerge(final boolean flag) {
        DeprecationSupport.warn(ObjectType.METHOD, "setWhitelistMerge", null, "setIncludeMerge");
        setIncludeMerge(flag);
    }

    /**
     * Get the list of whitelisted algorithm URIs.
     * 
     * @return the list of algorithms
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public Collection<String> getWhitelistedAlgorithms() {
        DeprecationSupport.warn(ObjectType.METHOD, "getWhitelistedAlgorithms", null, "getIncludedAlgorithms");
        return getIncludedAlgorithms();
    }
    

    /**
     * Set the list of whitelisted algorithm URIs.
     * 
     * @param uris the list of algorithms
     */
    public void setWhitelistedAlgorithms(@Nullable final Collection<String> uris) {
        DeprecationSupport.warn(ObjectType.METHOD, "setWhitelistedAlgorithms", null, "setIncludedAlgorithms");
        setIncludedAlgorithms(uris);
    }

    /**
     * Flag indicating whether to merge this configuration's blacklist with one of a lower order of precedence,
     * or to treat this blacklist as authoritative.
     * 
     * @return true if should merge, false otherwise
     */
    public boolean isBlacklistMerge() {
        DeprecationSupport.warn(ObjectType.METHOD, "isBlacklistMerge", null, "isExcludeMerge");
        return isExcludeMerge();
    }
    

    /**
     * Set the flag indicating whether to merge this configuration's blacklist with one of a lower order of precedence,
     * or to treat this blacklist as authoritative.
     * 
     * <p>Defaults to: <code>true</code>
     * 
     * @param flag true if should merge, false otherwise
     */
    public void setBlacklistMerge(final boolean flag) {
        DeprecationSupport.warn(ObjectType.METHOD, "setBlacklistMerge", null, "setExcludeMerge");
        setExcludeMerge(flag);
    }

    /**
     * Get the list of blacklisted algorithm URIs.
     * 
     * @return the list of algorithms
     */
    @Nonnull @NonnullElements @Unmodifiable @NotLive public Collection<String> getBlacklistedAlgorithms() {
        DeprecationSupport.warn(ObjectType.METHOD, "getBlacklistedAlgorithms", null, "getExcludedAlgorithms");
        return getExcludedAlgorithms();
    }

    /**
     * Set the list of blacklisted algorithm URIs.
     * 
     * @param uris the list of algorithms
     */
    public void setBlacklistedAlgorithms(@Nullable final Collection<String> uris) {
        DeprecationSupport.warn(ObjectType.METHOD, "setBlacklistedAlgorithms", null, "setExcludedAlgorithms");
        setExcludedAlgorithms(uris);
    }

    /**
     * Get preference value indicating which should take precedence when both whitelist and blacklist are non-empty.
     * 
     * @return the configured precedence value.
     */
    @Nonnull public WhitelistBlacklistConfiguration.Precedence getWhitelistBlacklistPrecedence() {
        DeprecationSupport.warn(ObjectType.METHOD, "getWhitelistBlacklistPrecedence", null,
                "getIncludeExcludePrecedence");
        
        switch (getIncludeExcludePrecedence()) {
            case INCLUDE:
                return WhitelistBlacklistConfiguration.Precedence.WHITELIST;
            case EXCLUDE:
                return WhitelistBlacklistConfiguration.Precedence.BLACKLIST;
            default:
                throw new IllegalArgumentException("Unrecognized Precedence value");
        }
    }
    

    /**
     * Set preference value indicating which should take precedence when both whitelist and blacklist are non-empty.
     * 
     * @param value the precedence value
     */
    public void setWhitelistBlacklistPrecedence(@Nonnull final WhitelistBlacklistConfiguration.Precedence value) {
        DeprecationSupport.warn(ObjectType.METHOD, "setWhitelistBlacklistPrecedence", null,
                "setIncludeExcludePrecedence");
        
        switch(Constraint.isNotNull(value, "Precedence cannot be null")) {
            case WHITELIST:
                setIncludeExcludePrecedence(AlgorithmPolicyConfiguration.Precedence.INCLUDE);
                break;
                
            case BLACKLIST:
                setIncludeExcludePrecedence(AlgorithmPolicyConfiguration.Precedence.EXCLUDE);
                break;
                    
            default:
                throw new IllegalArgumentException("Unrecognized precedence value");
        }
    }

}