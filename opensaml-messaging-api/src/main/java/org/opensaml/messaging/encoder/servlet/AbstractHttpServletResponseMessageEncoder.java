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

package org.opensaml.messaging.encoder.servlet;

import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.messaging.encoder.AbstractMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.net.ThreadLocalHttpServletResponseProxy;
import net.shibboleth.utilities.java.support.primitive.DeprecationSupport;
import net.shibboleth.utilities.java.support.primitive.DeprecationSupport.ObjectType;

/**
 * Abstract implementation of {@link HttpServletResponseMessageEncoder}.
 */
public abstract class AbstractHttpServletResponseMessageEncoder extends AbstractMessageEncoder
        implements HttpServletResponseMessageEncoder {

    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(AbstractHttpServletResponseMessageEncoder.class);

    /** Supplier for the Current HTTP servlet response, if available. */
    @Nullable private Supplier<HttpServletResponse> httpServletResponseSupplier;

    /**
     * {@inheritDoc}
     */
    @Nullable public HttpServletResponse getHttpServletResponse() {
        if (httpServletResponseSupplier == null) {
            return null;
        }
        return httpServletResponseSupplier.get();
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setHttpServletResponseSupplier(@Nullable final Supplier<HttpServletResponse> supplier) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        httpServletResponseSupplier = supplier;
    }

    /**
     * Get the supplier for the current HTTP response if available.
     *
     * @return the supplier for the current HTTP response or null
     */
    @Nullable public Supplier<HttpServletResponse> getHttpServletResponseSupplier() {
        return httpServletResponseSupplier;
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated(since = "4.3", forRemoval = true)
    public synchronized void setHttpServletResponse(@Nullable final HttpServletResponse response) {
        ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);

        DeprecationSupport.warnOnce(ObjectType.METHOD, "setHttpServletResponse",
                getClass().getCanonicalName(), "setHttpServletResponseSupplier");
        if (response != null && !(response instanceof ThreadLocalHttpServletResponseProxy)) {
            log.warn("Unsafe HttpServletRequest injected");
        }
        httpServletResponseSupplier = new Supplier<>() {
            public HttpServletResponse get() {
                return response;
            };
        };
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (getHttpServletResponse() == null) {
            throw new ComponentInitializationException("HTTP servlet response cannot be null");
        }
    }
}
