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

package org.opensaml.messaging.decoder.httpclient;

import javax.annotation.Nullable;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.opensaml.messaging.decoder.AbstractMessageDecoder;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;

/**
 * Abstract implementation of {@link HttpClientResponseMessageDecoder}.
 */
public abstract class AbstractHttpClientResponseMessageDecoder extends AbstractMessageDecoder
        implements HttpClientResponseMessageDecoder {

    /** The HTTP client response. */
    @NonnullAfterInit private ClassicHttpResponse response;

    /** {@inheritDoc} */
    @NonnullAfterInit public ClassicHttpResponse getHttpResponse() {
        return response;
    }

    /** {@inheritDoc} */
    public synchronized void setHttpResponse(@Nullable final ClassicHttpResponse clientResponse) {
        checkSetterPreconditions();

        response = clientResponse;
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();

        if (response == null) {
            throw new ComponentInitializationException("HTTP client response cannot be null");
        }
    }

}