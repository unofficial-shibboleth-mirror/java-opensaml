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
import java.util.Collections;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.xmlsec.AlgorithmPolicyConfiguration;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Basic implementation of {@link AlgorithmPolicyConfiguration}.
 * 
 * <p>
 * The value returned by {@link #getIncludeExcludePrecedence()} defaults to
 * {@link org.opensaml.xmlsec.AlgorithmPolicyConfiguration.Precedence#INCLUDE}.
 * </p>
 */
public class BasicAlgorithmPolicyConfiguration implements AlgorithmPolicyConfiguration {
    
    /** Default precedence. */
    public static final Precedence DEFAULT_PRECEDENCE = Precedence.INCLUDE;
    
    /** Included algorithm URIs. */
    private Collection<String> includes;
    
    /** Include merge flag. */
    private boolean includeMerge;
    
    /** Excluded algorithm URIs. */
    private Collection<String> excludes;
    
    /** Exclude merge flag. */
    private boolean excludeMerge;
    
    /** Precedence flag. */
    private Precedence precedence;
    
    /** Constructor. */
    public BasicAlgorithmPolicyConfiguration() {
        includes = Collections.emptySet();
        excludes = Collections.emptySet();
        precedence = DEFAULT_PRECEDENCE;
        
        // These merging defaults are intended to be the more secure/conservative approach:
        // - do merge excludes by default since don't want to unintentionally miss excludes from lower level
        // - do not merge includes by default since don't want to unintentionally include algos from lower level
        excludeMerge = true;
        includeMerge = false;
    }

    /** {@inheritDoc} */
    @Nonnull @NonnullElements @NotLive @Unmodifiable public Collection<String> getIncludedAlgorithms() {
        return includes;
    }
    
    /**
     * Set the list of included algorithm URIs.
     * 
     * @param uris the list of algorithms
     */
    public void setIncludedAlgorithms(@Nullable final Collection<String> uris) {
        if (uris == null) {
            includes = Collections.emptySet();
            return;
        }
        includes = Set.copyOf(StringSupport.normalizeStringCollection(uris));
    }

    /** 
     * {@inheritDoc}
     * 
     * <p>Defaults to: <code>false</code>.</p>
     */
    public boolean isIncludeMerge() {
        return includeMerge;
    }
    
    /**
     * Set the flag indicating whether to merge this configuration's includes with one of a lower order of precedence,
     * or to treat these includes as authoritative.
     * 
     * <p>Defaults to: <code>false</code>
     * 
     * @param flag true if should merge, false otherwise
     */
    public void setIncludeMerge(final boolean flag) {
        includeMerge = flag;
    }

    /** {@inheritDoc} */
    @Nonnull @NonnullElements @NotLive @Unmodifiable public Collection<String> getExcludedAlgorithms() {
        return excludes;
    }
    
    /**
     * Set the list of excluded algorithm URIs.
     * 
     * @param uris the list of algorithms
     */
    public void setExcludedAlgorithms(@Nullable final Collection<String> uris) {
        if (uris == null) {
            excludes = Collections.emptySet();
            return;
        }
        excludes = Set.copyOf(StringSupport.normalizeStringCollection(uris));
    }

    /** 
     * {@inheritDoc}
     * 
     * <p>Defaults to: <code>true</code>.</p>
     */
    public boolean isExcludeMerge() {
        return excludeMerge;
    }

    /**
     * Set the flag indicating whether to merge this configuration's excludes with one of a lower order of precedence,
     * or to treat these excludes as authoritative.
     * 
     * <p>Defaults to: <code>true</code>
     * 
     * @param flag true if should merge, false otherwise
     */
    public void setExcludeMerge(final boolean flag) {
        excludeMerge = flag;
    }

    /** {@inheritDoc} */
    @Nonnull public Precedence getIncludeExcludePrecedence() {
        return precedence;
    }
    
    /**
     * Set preference value indicating which should take precedence when both includes and excludes are non-empty.
     * 
     * @param value the precedence value
     */
    public void setIncludeExcludePrecedence(@Nonnull final Precedence value) {
        precedence = Constraint.isNotNull(value, "Precedence may not be null");
    }

}