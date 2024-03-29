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

package org.opensaml.soap.wsaddressing.messaging.impl;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.wsaddressing.Action;
import org.opensaml.soap.wsaddressing.WSAddressingConstants;
import org.opensaml.soap.wsaddressing.messaging.WSAddressingContext;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Handler implementation that checks a wsa:Action header against an expected value.
 * 
 * <p>
 * If the header is present, the value is first checked against the value obtained from subcontext data
 * {@link WSAddressingContext#getActionURI()}. If that was not supplied, then the locally-configured value
 * from {@link #getExpectedActionURI()} is used. If neither expected value is available, the check is skipped.
 * </p>
 */
public class ValidateActionHandler extends AbstractMessageHandler {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(ValidateActionHandler.class);
    
    /** The expected Action URI value. */
    @Nullable private String expectedActionURI;

    /**
     * Get the expected Action URI.
     * 
     * @return the expected URI, or null
     */
    @Nullable public String getExpectedActionURI() {
        return expectedActionURI;
    }

    /**
     * Set the expected Action URI value. 
     * 
     * @param uri the new URI value
     */
    public void setExpectedActionURI(@Nullable final String uri) {
        checkSetterPreconditions();
        expectedActionURI = StringSupport.trimOrNull(uri);
    }

    /** {@inheritDoc} */
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        if (!super.doPreInvoke(messageContext)) {
            return false;
        }
        
        // A non-null subcontext value will override what is statically configured
        final WSAddressingContext addressing = messageContext.getSubcontext(WSAddressingContext.class);
        if (addressing != null && addressing.getActionURI() != null) {
            expectedActionURI = addressing.getActionURI();
        }
        if (expectedActionURI == null) {
            log.debug("No expected WS-Addressing Action URI found locally or in message context, skipping evaluation");
            return false;
        }
        return true;
    }

    /** {@inheritDoc} */
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        final Action header = getAction(messageContext);
        final String headerValue = header != null ? StringSupport.trimOrNull(header.getURI()) : null;
        log.debug("Checking inbound message WS-Addressing Action URI value: {}", headerValue);
        if (header != null && Objects.equals(getExpectedActionURI(), headerValue)) {
            log.debug("Inbound WS-Addressing Action URI matched expected value");
            SOAPMessagingSupport.registerUnderstoodHeader(messageContext, header);
        } else {
            log.warn("Inbound WS-Addressing Action URI '{}' did not match the expected value '{}'", headerValue, 
                    getExpectedActionURI());
            SOAPMessagingSupport.registerSOAP11Fault(messageContext, 
                    WSAddressingConstants.SOAP_FAULT_ACTION_NOT_SUPPORTED,
                    "Action URI not supported: " + headerValue, null, null, null);
            throw new MessageHandlerException("Inbound WS-Addressing Action URI did not match the expected value");
        }
    }
        
    /**
     * Get message Action header.
     * 
     * @param messageContext the current message context
     * @return the message Action header
     */
    @Nullable protected Action getAction(@Nonnull final MessageContext messageContext) {
        final List<XMLObject> actions = SOAPMessagingSupport.getInboundHeaderBlock(messageContext, Action.ELEMENT_NAME);
        if (actions != null && !actions.isEmpty()) {
            return (Action) actions.get(0);
        }
        return null; 
    }

}