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

package org.opensaml.messaging.error.servlet;

import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.shibboleth.utilities.java.support.primitive.DeprecationSupport;
import net.shibboleth.utilities.java.support.primitive.NonnullSupplier;
import net.shibboleth.utilities.java.support.primitive.DeprecationSupport.ObjectType;


/**
 * Abstract implementation of {@link HttpServletMessageErrorHandler}.
 */
@Deprecated(forRemoval = true, since="4.3")
public abstract class AbstractHttpServletMessageErrorHandler implements HttpServletMessageErrorHandler {
    
    /** The HTTP servlet request Supplier. */
    private Supplier<HttpServletRequest> requestSupplier;
    
    /** The HTTP servlet response Supplier. */
    private Supplier<HttpServletResponse> responseSupplier;

    /** {@inheritDoc} */
    @Override @Nullable public HttpServletRequest getHttpServletRequest() {
        return requestSupplier == null ? null : requestSupplier.get();
    }

    /** {@inheritDoc} */
    @Override @Nullable public HttpServletResponse getHttpServletResponse() {
        return responseSupplier == null ? null : responseSupplier.get();
    }

    /** {@inheritDoc} */
    public void setHttpServletRequest(@Nullable final HttpServletRequest request) {
        DeprecationSupport.warnOnce(ObjectType.METHOD, "setHttpServletRequest", null, "setHttpServletRequestSupplier");
        requestSupplier = NonnullSupplier.of(request);
    }

    /** {@inheritDoc} */
    @Override public void setHttpServletRequestSupplier(@Nullable final Supplier<HttpServletRequest> servletRequestSupplier) {
        requestSupplier = servletRequestSupplier;
    }

    /** {@inheritDoc} */
    @Override public void setHttpServletResponseSupplier(@Nullable final Supplier<HttpServletResponse> servletResponseSupplier) {
        responseSupplier = servletResponseSupplier;
    }

    /** {@inheritDoc} */
    public void setHttpServletResponse(@Nullable final HttpServletResponse response) {
        DeprecationSupport.warnOnce(ObjectType.METHOD, "setHttpServletResponse", null, "setHttpServletResponseSupplier");
        responseSupplier = NonnullSupplier.of(response);
    }
}
