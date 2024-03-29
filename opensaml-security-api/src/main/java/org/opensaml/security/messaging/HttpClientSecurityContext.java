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

package org.opensaml.security.messaging;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.security.httpclient.HttpClientSecurityParameters;

import net.shibboleth.shared.resolver.CriteriaSet;

/**
 * A context implementation holding parameters related to {@link org.apache.hc.client5.http.classic.HttpClient}
 * security features.
 */
public final class HttpClientSecurityContext extends BaseContext {
    
    /** The HttpClient security parameters instance. */
    @Nullable private HttpClientSecurityParameters securityParameters;
    
    /** TLS criteria strategy function. */
    @Nullable private Function<MessageContext,CriteriaSet> tlsCriteriaSetStrategy;
    
    /**
     * Get the {@link HttpClientSecurityParameters} instance.
     * 
     * @return the parameters instance, or null
     */
    @Nullable public HttpClientSecurityParameters getSecurityParameters() {
        return securityParameters;
    }
    
    /**
     * Set the {@link HttpClientSecurityParameters} instance.
     * 
     * @param parameters the parameters instance, or null
     * 
     * @return this context
     */
    @Nonnull public HttpClientSecurityContext setSecurityParameters(
            @Nullable final HttpClientSecurityParameters parameters) {
        securityParameters = parameters;
        
        return this;
    }

    /**
     * Get the TLS criteria strategy function.
     * 
     * @return the strategy function, or null
     */
    @Nullable public Function<MessageContext,CriteriaSet> getTLSCriteriaSetStrategy() {
        return tlsCriteriaSetStrategy;
    }
    
    /**
     * Set the TLS criteria strategy function.
     * 
     * @param strategy the strategy function instance, or null
     * 
     * @return this context
     */
    @Nonnull public HttpClientSecurityContext setTLSCriteriaSetStrategy(
            @Nullable final Function<MessageContext,CriteriaSet> strategy) {
        tlsCriteriaSetStrategy = strategy;
        
        return this;
    }

}