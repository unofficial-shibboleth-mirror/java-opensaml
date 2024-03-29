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

package org.opensaml.saml.common.messaging.context;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.saml.saml2.metadata.Endpoint;

/**
 * Context that carries information about a SAML entity endpoint.
 */
public final class SAMLEndpointContext extends BaseContext {

    /** The SAML entity endpoint. */
    @Nullable private Endpoint endpoint;

    /**
     * Gets the endpoint of the SAML entity.
     * 
     * @return endpoint of the SAML entity, may be null
     */
    @Nullable public Endpoint getEndpoint() {
        return endpoint;
    }

    /**
     * Sets the endpoint of the SAML entity.
     * 
     * @param newEndpoint the new endpoint
     */
    public void setEndpoint(@Nullable final Endpoint newEndpoint) {
        endpoint = newEndpoint;
    }

}