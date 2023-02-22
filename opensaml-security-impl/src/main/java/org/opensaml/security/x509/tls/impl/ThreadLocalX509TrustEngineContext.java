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

package org.opensaml.security.x509.tls.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.X509Credential;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.resolver.CriteriaSet;

/**
 * Class which holds and makes available instances of
 * {@link TrustEngine}<code>&lt;? super </code>{@link org.opensaml.security.x509.X509Credential}<code>&gt;</code>
 * and {@link CriteriaSet} via ThreadLocal storage, typically used for server TLS authentication
 * via {@link ThreadLocalX509TrustManager}.
 */
public final class ThreadLocalX509TrustEngineContext {
    
    /** Unified class representing the data that will be managed by the thread local storage. */
    private static final class Data {
        
        /** Trust engine. */
        private TrustEngine<? super X509Credential> trustEngine;
        
        /** Criteria. */
        private CriteriaSet criteriaSet;
        
        /** Whether evaluation target was trusted. */
        private Boolean trusted;
        
        /** Whether trust engine evaluation failure should be treated as fatal. Defaults to true. */
        private Boolean failureFatal;
        
        /**
         * Constructor.
         *
         * @param engine the trust engine
         * @param criteria the trust criteria
         * @param fatal whether trust engine failure should be treated as fatal
         */
        private Data(@Nonnull final TrustEngine<? super X509Credential> engine, @Nonnull final CriteriaSet criteria,
                @Nullable final Boolean fatal) {
            
            trustEngine = engine;
            criteriaSet = criteria;
            failureFatal = fatal != null ? fatal : Boolean.TRUE;
        }
        
    }

    /** ThreadLocal storage for trust engine. */
    private static ThreadLocal<Data> current = new ThreadLocal<>();

    /** Constructor. */
    private ThreadLocalX509TrustEngineContext() { }

    /**
     * Load the thread-local storage with the current credential.
     * 
     * @param trustEngine the current trust engine
     * @param criteria the current criteria
     * @param fatal whether trust engine evaluation failure should be treated as fatal. Defaults to true. 
     */
    public static void loadCurrent(@Nonnull final TrustEngine<? super X509Credential> trustEngine,
            @Nonnull final CriteriaSet criteria, @Nullable final Boolean fatal) {
        
        Constraint.isNotNull(trustEngine, "TrustEngine may not be null");
        Constraint.isNotNull(criteria, "CriteriaSet may not be null");

        current.set(new Data(trustEngine, criteria, fatal));
    }

    /**
     * Clear the current thread-local credential.
     */
    public static void clearCurrent() {
        current.remove();
    }
    
    /**
     * Get whether the current thread-local is populated with non-null data.
     * 
     * @return true if thread-local has non-null data, false otherwise
     */
    public static boolean haveCurrent() {
        return current.get() != null;
    }

    /**
     * Return the current thread-local trust engine instance.
     * 
     * @return the current trust engine
     */
    @Nullable public static TrustEngine<? super X509Credential> getTrustEngine() {
        return current.get() != null ? current.get().trustEngine : null;
    }
    
    /**
     * Return the current thread-local criteria instance.
     * 
     * @return the current criteria
     */
    @Nullable public static CriteriaSet getCriteria() {
        return current.get() != null ? current.get().criteriaSet : null;
    }
    
    /**
     * Return whether failure of server TLS is to be treated as fatal.
     * 
     * @return true if fatal, false if not
     */
    @Nonnull public static Boolean isFailureFatal() {
        return current.get() != null ? current.get().failureFatal : Boolean.TRUE;
    }
    
    /**
     * Return the current thread-local trust evaluation data.
     * 
     * @return the current trust evaluation data
     */
    @Nullable public static Boolean getTrusted() {
        return current.get() != null ? current.get().trusted : null;
    }

    /**
     * Set the current thread-local trust evaluation data.
     * 
     * @param trusted the current trust evaluation data
     */
    public static void setTrusted(@Nullable final Boolean trusted) {
        if (current.get() != null) {
            current.get().trusted = trusted;
        }
    }

}
