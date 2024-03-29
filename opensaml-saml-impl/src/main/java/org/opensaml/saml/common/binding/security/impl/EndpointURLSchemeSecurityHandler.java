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

package org.opensaml.saml.common.binding.security.impl;

import java.net.URI;

import javax.annotation.Nonnull;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.binding.BindingException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.SAMLMessageSecuritySupport;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Class which verifies that the {@link org.opensaml.saml.saml2.metadata.Endpoint}
 * to which a message will be delivered contains a valid URL scheme.
 */
public class EndpointURLSchemeSecurityHandler extends AbstractMessageHandler {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(EndpointURLSchemeSecurityHandler.class);

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        final URI endpointUrl;
        try {
            endpointUrl = SAMLBindingSupport.getEndpointURL(messageContext);
        } catch (final BindingException e) {
            throw new MessageHandlerException("Could not obtain message endpoint URL", e);
        }
        
        log.debug("{} Checking outbound endpoint for allowed URL scheme: {}", getLogPrefix(), endpointUrl);
        
        if (!SAMLMessageSecuritySupport.checkURLScheme(endpointUrl.getScheme())) {
            throw new MessageHandlerException("Relying party endpoint used the untrusted URL scheme "
                    + endpointUrl.getScheme());
        }
    }

}