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

import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.primitive.StringSupport;

import org.opensaml.messaging.context.BaseContext;

/**
 * Subcontext that carries information about the ongoing SAML protocol.
 */
public final class SAMLProtocolContext extends BaseContext {

    /** The SAML protocol in use. */
    @Nullable @NotEmpty private String protocol;

    /**
     * Gets the SAML protocol in use.
     * 
     * @return SAML protocol in use, may be null
     */
    @Nullable @NotEmpty public String getProtocol() {
        return protocol;
    }

    /**
     * Sets the SAML protocol in use.
     * 
     * @param samlProtocol SAML protocol in use
     */
    public void setProtocol(@Nullable final String samlProtocol) {
        protocol = StringSupport.trimOrNull(samlProtocol);
    }

}