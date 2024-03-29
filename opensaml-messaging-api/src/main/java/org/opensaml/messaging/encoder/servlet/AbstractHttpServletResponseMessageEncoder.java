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

package org.opensaml.messaging.encoder.servlet;

import javax.annotation.Nullable;

import org.opensaml.messaging.encoder.AbstractMessageEncoder;

import jakarta.servlet.http.HttpServletResponse;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.NonnullSupplier;

/**
 * Abstract implementation of {@link HttpServletResponseMessageEncoder}.
 */
public abstract class AbstractHttpServletResponseMessageEncoder extends AbstractMessageEncoder
        implements HttpServletResponseMessageEncoder {

    /** Supplier for the Current HTTP servlet response, if available. */
    @Nullable private NonnullSupplier<HttpServletResponse> httpServletResponseSupplier;

    /**
     * {@inheritDoc}
     */
    @NonnullAfterInit public HttpServletResponse getHttpServletResponse() {
        if (httpServletResponseSupplier != null) {
            return httpServletResponseSupplier.get();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setHttpServletResponseSupplier(
            @Nullable final NonnullSupplier<HttpServletResponse> supplier) {
        checkSetterPreconditions();
        httpServletResponseSupplier = supplier;
    }

    /**
     * Get the supplier for the current HTTP response if available.
     *
     * @return the supplier for the current HTTP response or null
     */
    @NonnullAfterInit public NonnullSupplier<HttpServletResponse> getHttpServletResponseSupplier() {
        return httpServletResponseSupplier;
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (getHttpServletResponse() == null) {
            throw new ComponentInitializationException("HTTP servlet response cannot be null");
        }
    }

}