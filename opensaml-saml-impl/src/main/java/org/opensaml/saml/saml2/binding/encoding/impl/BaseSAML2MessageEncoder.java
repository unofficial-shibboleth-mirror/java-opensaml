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

package org.opensaml.saml.saml2.binding.encoding.impl;

import java.net.URI;

import javax.annotation.Nonnull;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.binding.BindingException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.binding.encoding.SAMLMessageEncoder;
import org.opensaml.saml.common.binding.impl.BaseSAMLHttpServletResponseEncoder;

/**
 * Base class for SAML 2 message encoders.
 */
public abstract class BaseSAML2MessageEncoder extends BaseSAMLHttpServletResponseEncoder
        implements SAMLMessageEncoder {
    
    /**
     * Gets the response URL from the message context.
     * 
     * @param messageContext current message context
     * 
     * @return response URL from the message context
     * 
     * @throws MessageEncodingException throw if no relying party endpoint is available
     */
    @Nonnull protected URI getEndpointURL(@Nonnull final MessageContext messageContext)
            throws MessageEncodingException {
        try {
            return SAMLBindingSupport.getEndpointURL(messageContext);
        } catch (final BindingException e) {
            throw new MessageEncodingException("Could not obtain message endpoint URL", e);
        }
    }

}