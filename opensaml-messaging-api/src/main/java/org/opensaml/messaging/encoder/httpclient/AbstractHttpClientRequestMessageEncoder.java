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

package org.opensaml.messaging.encoder.httpclient;

import javax.annotation.Nullable;

import org.apache.hc.core5.http.ClassicHttpRequest;
import org.opensaml.messaging.encoder.AbstractMessageEncoder;
import org.opensaml.messaging.encoder.servlet.HttpServletResponseMessageEncoder;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;

/**
 * Abstract implementation of {@link HttpServletResponseMessageEncoder}.
 */
public abstract class AbstractHttpClientRequestMessageEncoder extends AbstractMessageEncoder
        implements HttpClientRequestMessageEncoder {

    /** The HTTP client request. */
    @NonnullAfterInit private ClassicHttpRequest request;

    /** {@inheritDoc} */
    @NonnullAfterInit public ClassicHttpRequest getHttpRequest() {
        return request;
    }

    /** {@inheritDoc} */
    public synchronized void setHttpRequest(@Nullable final ClassicHttpRequest httpRequest) {
        checkSetterPreconditions();

        request = httpRequest;
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (request == null) {
            throw new ComponentInitializationException("HTTP client request cannot be null");
        }
    }

}