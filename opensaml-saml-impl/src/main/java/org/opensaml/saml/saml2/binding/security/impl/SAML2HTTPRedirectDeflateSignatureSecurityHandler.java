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

package org.opensaml.saml.saml2.binding.security.impl;

import javax.annotation.Nonnull;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.binding.security.impl.BaseSAMLSimpleSignatureSecurityHandler;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Message handler which evaluates simple "blob" signatures according to the SAML 2 HTTP-Redirect DEFLATE binding.
 */
public class SAML2HTTPRedirectDeflateSignatureSecurityHandler extends BaseSAMLSimpleSignatureSecurityHandler {

    /** Logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(SAML2HTTPRedirectDeflateSignatureSecurityHandler.class);

    /** {@inheritDoc} */
    @Override
    protected boolean ruleHandles(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        return "GET".equals(getHttpServletRequest().getMethod())
                && SAMLConstants.SAML2_REDIRECT_BINDING_URI.equals(
                        messageContext.ensureSubcontext(SAMLBindingContext.class).getBindingUri());
    }
    
}