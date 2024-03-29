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

package org.opensaml.saml.common.messaging.context.navigate;

import java.util.function.Function;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;

/**
 * Function that returns the entityID from a {@link SAMLPeerEntityContext} or {@link SAMLSelfEntityContext}.
 */
public class SAMLMessageContextIssuerFunction implements Function<MessageContext,String> {

    /** {@inheritDoc} */
    @Nullable public String apply(@Nullable final MessageContext input) {
        
        if (input != null) {
            final SAMLPeerEntityContext peerCtx = input.getSubcontext(SAMLPeerEntityContext.class);
            if (peerCtx != null) {
                return peerCtx.getEntityId();
            }
            
            final SAMLSelfEntityContext selfCtx = input.getSubcontext(SAMLSelfEntityContext.class);
            if (selfCtx != null) {
                return selfCtx.getEntityId();
            }
        }
        
        return null;
    }

}