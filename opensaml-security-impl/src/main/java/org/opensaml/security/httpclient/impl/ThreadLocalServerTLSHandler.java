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

package org.opensaml.security.httpclient.impl;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.opensaml.security.httpclient.HttpClientSecurityConstants;
import org.opensaml.security.x509.tls.impl.ThreadLocalX509TrustEngineContext;
import org.slf4j.Logger;

import net.shibboleth.shared.httpclient.HttpClientContextHandler;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * An implementation of {@link HttpClientContextHandler} which handles cleanup and transfer of
 * data used for server TLS held by {@link ThreadLocalX509TrustEngineContext}.
 */
public class ThreadLocalServerTLSHandler implements HttpClientContextHandler {
    
    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(ThreadLocalServerTLSHandler.class);

    /** {@inheritDoc} */
    public void invokeBefore(@Nonnull final HttpClientContext context, @Nonnull final ClassicHttpRequest request)
            throws IOException {
        // Do nothing here
        
    }

    /** {@inheritDoc} */
    public void invokeAfter(@Nonnull final HttpClientContext context, @Nonnull final ClassicHttpRequest request)
            throws IOException {
        
        log.trace("Saw ThreadLocalX509TrustEngineContext.getTrusted: {}",
                ThreadLocalX509TrustEngineContext.getTrusted());
        context.setAttribute(HttpClientSecurityConstants.CONTEXT_KEY_SERVER_TLS_CREDENTIAL_TRUSTED,
                ThreadLocalX509TrustEngineContext.getTrusted());
        
        log.trace("Clearing ThreadLocalX509TrustEngineContext");
        ThreadLocalX509TrustEngineContext.clearCurrent();
    }

}