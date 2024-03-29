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
import org.opensaml.messaging.decoder.MessageDecoder;


/**
 * A specialization of {@link MessageDecoder} that operates on a source message data type of
 * {@link ClassicHttpResponse}.
 */
public interface HttpClientResponseMessageDecoder extends MessageDecoder {
    
    /**
     * Get the HTTP client response on which to operate.
     * 
     * @return the HTTP client response
     */
    @Nullable ClassicHttpResponse getHttpResponse();
    
    /**
     * Set the HTTP client response on which to operate.
     * 
     * @param response the HTTP client response 
     */
    void setHttpResponse(@Nullable final ClassicHttpResponse response);
    
}