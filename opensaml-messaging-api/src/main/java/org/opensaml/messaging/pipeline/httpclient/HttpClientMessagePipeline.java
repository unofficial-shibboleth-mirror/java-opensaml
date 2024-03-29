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

package org.opensaml.messaging.pipeline.httpclient;

import javax.annotation.Nonnull;

import org.opensaml.messaging.decoder.httpclient.HttpClientResponseMessageDecoder;
import org.opensaml.messaging.encoder.httpclient.HttpClientRequestMessageEncoder;
import org.opensaml.messaging.pipeline.MessagePipeline;

/**
 * Specialization of {@link MessagePipeline} which narrows the type of allowed encoders and decoders.
 */
public interface HttpClientMessagePipeline extends MessagePipeline {
    
    /**
     * {@inheritDoc} 
     * 
     * <p>Narrows the super-interface return type to {@link HttpClientRequestMessageEncoder}.</p>
     */
    @Nonnull public HttpClientRequestMessageEncoder getEncoder();
    
    /**
     * {@inheritDoc} 
     * 
     * <p>Narrows the super-interface return type to {@link HttpClientResponseMessageDecoder}.</p>
     */
    @Nonnull public HttpClientResponseMessageDecoder getDecoder();

}
