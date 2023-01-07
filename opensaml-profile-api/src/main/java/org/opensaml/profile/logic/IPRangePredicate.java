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

package org.opensaml.profile.logic;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;

import com.google.common.net.InetAddresses;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.annotation.constraint.NonnullElements;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.net.IPRange;
import net.shibboleth.shared.primitive.DeprecationSupport;
import net.shibboleth.shared.primitive.DeprecationSupport.ObjectType;
import net.shibboleth.shared.primitive.NonnullSupplier;
import net.shibboleth.shared.servlet.HttpServletSupport;

/**
 * A {@link Predicate} that checks if a request is from a set of one or more {@link IPRange}s.
 */
public class IPRangePredicate implements Predicate<BaseContext> {

    /** Servlet request to evaluate. */
    @Nullable private Supplier<HttpServletRequest> httpRequestSupplier;
    
    /** IP ranges to match against. */
    @Nonnull @NonnullElements private Collection<IPRange> addressRanges;

    /** Constructor. */
    IPRangePredicate() {
        addressRanges = Collections.emptyList();
    }
    
    /**
     * Set the address ranges to check against.
     *
     * @param ranges    address ranges to check against
     *
     * @since 3.3.0
     */
    public void setRanges(@Nonnull @NonnullElements final Collection<IPRange> ranges) {
        Constraint.isNotNull(ranges, "Address range collection cannot be null");
        
        addressRanges = List.copyOf(ranges);
    }

    /**
     * Set the Supplier for the servlet request to evaluate.
     *
     * @param request servlet request supplier to use
     */
    public void setHttpServletRequestSupplier(@Nonnull final Supplier<HttpServletRequest> supplier) {
        httpRequestSupplier = Constraint.isNotNull(supplier, "HttpServletRequestSupplier cannot be null");
    }

    /** {@inheritDoc} */
    public boolean test(@Nullable final BaseContext input) {
        final HttpServletRequest request = httpRequestSupplier != null ? httpRequestSupplier.get() : null;
        final String address = request != null ? HttpServletSupport.getRemoteAddr(request) : null;
        if (address == null || !InetAddresses.isInetAddress(address)) {
            return false;
        }
        
        for (final IPRange range : addressRanges) {
            if (range.contains(InetAddresses.forString(address))) {
                return true;
            }
        }
        
        return false;
    }
    
}
