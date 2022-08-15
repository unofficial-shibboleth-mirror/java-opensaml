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

package org.opensaml.messaging.decoder.servlet;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.opensaml.messaging.decoder.AbstractMessageDecoder;
import org.opensaml.messaging.decoder.MessageDecodingException;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

/**
 * Abstract implementation of {@link HttpServletRequestMessageDecoder}.
 */
public abstract class AbstractHttpServletRequestMessageDecoder extends AbstractMessageDecoder
        implements HttpServletRequestMessageDecoder {

    /** Current HTTP request, if available. */
    @Nullable private Supplier<HttpServletRequest> httpServletRequestSupplier;

    /** {@inheritDoc} */
    @Override
    @Nullable public HttpServletRequest getHttpServletRequest() {
        if (httpServletRequestSupplier == null) {
            return null;
        }
        return httpServletRequestSupplier.get();
    }

    /**
     * Get the supplier for  HTTP request if available.
     *
     * @return current HTTP request
     */
    @Nullable public Supplier<HttpServletRequest> getHttpServletRequestSupplier() {
        return httpServletRequestSupplier;
    }

    /** {@inheritDoc} */
    @Override
    public void setHttpServletRequestSupplier(@Nullable final Supplier<HttpServletRequest> requestSupplier) {
        checkSetterPreconditions();

        httpServletRequestSupplier = requestSupplier;
    }

    /** {@inheritDoc} */
    public void decode() throws MessageDecodingException {
        super.decode();
    }


    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (getHttpServletRequest() == null) {
            throw new ComponentInitializationException("HTTP Servlet request cannot be null");
        }
    }
}
