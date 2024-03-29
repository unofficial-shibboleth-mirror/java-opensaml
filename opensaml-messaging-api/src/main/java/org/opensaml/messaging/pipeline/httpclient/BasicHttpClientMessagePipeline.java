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

import org.opensaml.messaging.decoder.MessageDecoder;
import org.opensaml.messaging.decoder.httpclient.HttpClientResponseMessageDecoder;
import org.opensaml.messaging.encoder.MessageEncoder;
import org.opensaml.messaging.encoder.httpclient.HttpClientRequestMessageEncoder;
import org.opensaml.messaging.pipeline.BasicMessagePipeline;

/**
 * Basic implementation of {@link HttpClientMessagePipeline}.
 */
public class BasicHttpClientMessagePipeline extends BasicMessagePipeline implements HttpClientMessagePipeline{
    
    /**
     * Constructor.
     *
     * @param newEncoder the message encoder instance
     * @param newDecoder the message decoder instance
     */
    public BasicHttpClientMessagePipeline(@Nonnull final MessageEncoder newEncoder, 
            @Nonnull final MessageDecoder newDecoder) {
        super(newEncoder, newDecoder);
    }

    /** {@inheritDoc} */
    @Nonnull public HttpClientRequestMessageEncoder getEncoder() {
        return (HttpClientRequestMessageEncoder) super.getEncoder();
    }

    /** {@inheritDoc} */
    protected void setEncoder(@Nonnull final MessageEncoder encoder) {
        if (!(encoder instanceof HttpClientRequestMessageEncoder)) {
            throw new IllegalArgumentException("HttpClientRequestMessageEncoder is required");
        }
        super.setEncoder(encoder);
    }

    /** {@inheritDoc} */
    @Nonnull public HttpClientResponseMessageDecoder getDecoder() {
        return (HttpClientResponseMessageDecoder) super.getDecoder();
    }

    /** {@inheritDoc} */
    protected void setDecoder(@Nonnull final MessageDecoder decoder) {
        if (!(decoder instanceof HttpClientResponseMessageDecoder)) {
            throw new IllegalArgumentException("HttpClientResponseMessageDecoder is required");
        }
        super.setDecoder(decoder);
    }
    
}