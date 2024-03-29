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

package org.opensaml.messaging.context.httpclient;

import javax.annotation.Nullable;

import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.opensaml.messaging.context.BaseContext;

/**
 * A context impl holding data related to the execution of an {@link org.apache.hc.client5.http.classic.HttpClient}
 * request.
 */
public final class HttpClientRequestContext extends BaseContext {
    
    /** The HttpClientContext instance. */
    @Nullable private HttpClientContext httpClientContext;

    /**
     * Get the {@link HttpClientContext} instance.
     * 
     * @return the HttpClientContext instance, or null
     */
    @Nullable public HttpClientContext getHttpClientContext() {
        return httpClientContext;
    }

    /**
     * Set the {@link HttpClientContext} instance.
     * 
     * @param context the client context instance to set, or null
     */
    public void setHttpClientContext(@Nullable final HttpClientContext context) {
        httpClientContext = context;
    }

}
