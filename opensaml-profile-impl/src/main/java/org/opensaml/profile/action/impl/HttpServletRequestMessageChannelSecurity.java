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

package org.opensaml.profile.action.impl;

import javax.annotation.Nonnull;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.MessageChannelSecurityContext;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.component.ComponentInitializationException;

/**
 * Profile action which populates a {@link MessageChannelSecurityContext} based on a
 * {@link jakarta.servlet.http.HttpServletRequest}.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_PROFILE_CTX}
 */
public class HttpServletRequestMessageChannelSecurity extends AbstractMessageChannelSecurity {

    /** Flag controlling whether traffic on the default TLS port is "secure". */
    private boolean defaultPortInsecure;
    
    /** Constructor. */
    public HttpServletRequestMessageChannelSecurity() {
        defaultPortInsecure = true;
    }
    
    /**
     * Set whether traffic on the default TLS port is "secure" for the purposes of this action.
     * 
     * <p>Defaults to "true"</p>
     *
     * <p>Ordinarily TLS is considered a "secure" channel, but traffic to a default port meant
     * for browser access tends to rely on server certificates that are unsuited to secure messaging
     * use cases. This flag allows software layers to recognize traffic on this port as "insecure" and
     * needing additional security measures.</p>
     * 
     * @param flag flag to set
     */
    public void setDefaultPortInsecure(final boolean flag) {
        checkSetterPreconditions();
        
        defaultPortInsecure = flag;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        if (getHttpServletRequest() == null) {
            throw new ComponentInitializationException("HttpServletRequest is required");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        final BaseContext parent = getParentContext();
        final HttpServletRequest request = getHttpServletRequest();
        if (parent == null || request == null) {
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return;
        }
        
        final MessageChannelSecurityContext channelContext =
                parent.ensureSubcontext(MessageChannelSecurityContext.class);
        
        if (request.isSecure() && (!defaultPortInsecure || request.getLocalPort() != 443)) {
            channelContext.setConfidentialityActive(true);
            channelContext.setIntegrityActive(true);
        } else {
            channelContext.setConfidentialityActive(false);
            channelContext.setIntegrityActive(false);
        }
    }

}