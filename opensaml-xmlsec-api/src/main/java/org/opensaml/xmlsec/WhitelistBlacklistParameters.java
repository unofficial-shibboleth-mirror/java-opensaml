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
import javax.annotation.Nullable;

import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.annotation.constraint.NotLive;
import net.shibboleth.shared.annotation.constraint.Unmodifiable;
import net.shibboleth.shared.primitive.DeprecationSupport;
import net.shibboleth.shared.primitive.DeprecationSupport.ObjectType;

/**
 * The whitelist and blacklist algorithm parameters.
 * 
 * <p>Replace with {@link AlgorithmPolicyParameters}.
 * 
 * @deprecated
 */
@Deprecated(forRemoval=true, since="4.1.0")
public class WhitelistBlacklistParameters extends AlgorithmPolicyParameters {
    
    /**
     * Get the list of whitelisted algorithm URI's.
     * 
     * @return the list of algorithms
     */
    @Nonnull @NonnullElements @NotLive @Unmodifiable public Collection<String> getWhitelistedAlgorithms() {
        DeprecationSupport.warn(ObjectType.METHOD, "getWhitelistedAlgorithms", null, "getIncludedAlgorithms");
        return getIncludedAlgorithms();
    }
    
    /**
     * Set the list of whitelisted algorithm URI's.
     * 
     * @param uris the list of algorithms
     */
    public void setWhitelistedAlgorithms(@Nullable final Collection<String> uris) {
        DeprecationSupport.warn(ObjectType.METHOD, "setWhitelistedAlgorithms", null, "setIncludedAlgorithms");
        setIncludedAlgorithms(uris);
    }
    
    /**
     * Get the list of blacklisted algorithm URI's.
     * 
     * @return the list of algorithms
     */
    @Nonnull @NonnullElements @NotLive @Unmodifiable public Collection<String> getBlacklistedAlgorithms() {
        DeprecationSupport.warn(ObjectType.METHOD, "getBlacklistedAlgorithms", null, "getExcludedAlgorithms");
        return getExcludedAlgorithms();
    }
    
    /**
     * Set the list of blacklisted algorithm URI's.
     * 
     * @param uris the list of algorithms
     */
    public void setBlacklistedAlgorithms(@Nonnull @NonnullElements final Collection<String> uris) {
        DeprecationSupport.warn(ObjectType.METHOD, "setBlacklistedAlgorithms", null, "setExcludedAlgorithms");
        setExcludedAlgorithms(uris);
    }
    
}