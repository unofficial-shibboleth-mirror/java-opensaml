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

package org.opensaml.soap.wssecurity.messaging.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.soap.messaging.AbstractHeaderGeneratingMessageHandler;
import org.opensaml.soap.wssecurity.Created;
import org.opensaml.soap.wssecurity.Expires;
import org.opensaml.soap.wssecurity.Timestamp;
import org.opensaml.soap.wssecurity.messaging.WSSecurityContext;
import org.opensaml.soap.wssecurity.messaging.WSSecurityMessagingSupport;
import org.slf4j.Logger;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Handler implementation that adds a wsse:Timestamp header to the wsse:Security header
 * of the outbound SOAP envelope.
 */
public class AddTimestampHandler extends AbstractHeaderGeneratingMessageHandler {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(AddTimestampHandler.class);
    
    /** Context lookup function for the Created time. */
    @Nullable private Function<MessageContext,Instant> createdLookup;
    
    /** Context lookup function for the Expires time. */
    @Nullable private Function<MessageContext,Instant> expiresLookup;
    
    /** Flag indicating whether to use the current time as the Created time, if no value
     * is explicitly supplied by the other supported mechanisms. */
    private boolean useCurrentTimeAsDefaultCreated;
    
    /** Parameter indicating the offset from Created used to calculate the Expires time, 
     * if no Expires value is explicitly supplied via the other supported mechanisms. */
    @Nullable private Duration expiresOffsetFromCreated;
    
    /** The effective Created value to use. */
    @Nullable private Instant createdValue;
    
    /** The effective Expires value to use. */
    @Nullable private Instant expiresValue;
    
    /**
     * Get the context lookup function for the Created time.
     * 
     * @return the lookup function
     */
    @Nullable public Function<MessageContext,Instant> getCreatedLookup() {
        return createdLookup;
    }

    /**
     * Set the context lookup function for the Created time.
     * 
     * @param lookup the lookup function
     */
    public void setCreatedLookup(@Nullable final Function<MessageContext,Instant> lookup) {
        checkSetterPreconditions();
        createdLookup = lookup;
    }

    /**
     * Get the context lookup function for the Expires time.
     * 
     * @return the lookup function
     */
    @Nullable public Function<MessageContext,Instant> getExpiresLookup() {
        return expiresLookup;
    }

    /**
     * Set the context lookup function for the Expires time.
     * 
     * @param lookup the lookup function
     */
    public void setExpiresLookup(@Nullable final Function<MessageContext,Instant> lookup) {
        checkSetterPreconditions();
        expiresLookup = lookup;
    }

    /**
     * Get the flag indicating whether to use the current time as the Created time, if no value
     * is explicitly supplied by the other supported mechanisms. 
     * 
     * @return true if should use current time, false if not
     */
    public boolean isUseCurrentTimeAsDefaultCreated() {
        return useCurrentTimeAsDefaultCreated;
    }

    /**
     * Set the flag indicating whether to use the current time as the Created time, if no value
     * is explicitly supplied by the other supported mechanisms. 
     * 
     * @param flag true if should use currnet time, false if not
     */
    public void setUseCurrentTimeAsDefaultCreated(final boolean flag) {
        checkSetterPreconditions();
        useCurrentTimeAsDefaultCreated = flag;
    }
    
    /**
     * Get the parameter indicating the offset from Created used to calculate the Expires time, 
     * if no Expires value is explicitly supplied via the other supported mechanisms. 
     * 
     * @return the expires offset, or null
     */
    @Nullable public Duration getExpiresOffsetFromCreated() {
        return expiresOffsetFromCreated;
    }

    /**
     * Set the parameter indicating the offset from Created used to calculate the Expires time, 
     * if no Expires value is explicitly supplied via the other supported mechanisms. 
     * 
     * @param value the expires offset, or null
     */
    public void setExpiresOffsetFromCreated(@Nullable final Duration value) {
        checkSetterPreconditions();
        expiresOffsetFromCreated = value;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        if (!super.doPreInvoke(messageContext)) {
            return false;
        }
        
        createdValue = getCreatedValue(messageContext);
        expiresValue = getExpiresValue(messageContext, createdValue);
        if (createdValue == null && expiresValue == null) {
            log.debug("No WS-Security Timestamp Created or Expires values available, skipping further processing");
            return false;
        }
        return true;
    }

    /** {@inheritDoc} */
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        log.debug("Processing addition of outbound WS-Security Timestamp");
        final Timestamp timestamp = (Timestamp) XMLObjectSupport.buildXMLObject(Timestamp.ELEMENT_NAME);
        
        if (createdValue != null) {
            log.debug("WS-Security Timestamp Created value added was: {}", createdValue);
            final Created created = (Created) XMLObjectSupport.buildXMLObject(Created.ELEMENT_NAME);
            created.setDateTime(createdValue);
            timestamp.setCreated(created);
        }
            
        if (expiresValue != null) {
            log.debug("WS-Security Timestamp Expires value added was: {}", createdValue);
            final Expires expires = (Expires) XMLObjectSupport.buildXMLObject(Expires.ELEMENT_NAME);
            expires.setDateTime(expiresValue);
            timestamp.setExpires(expires);
        }
        
        WSSecurityMessagingSupport.addSecurityHeaderBlock(messageContext, timestamp, isEffectiveMustUnderstand(),
                getEffectiveTargetNode(), true);
    }
    
    /**
     * Get the Created value.
     * 
     * @param messageContext the current message context
     * 
     * @return the effective Created DateTime value to use
     */
    @Nullable protected Instant getCreatedValue(@Nonnull final MessageContext messageContext) {
        Instant value = null;
        final WSSecurityContext security = messageContext.getSubcontext(WSSecurityContext.class);
        if (security != null) {
            value = security.getTimestampCreated();
        }
        
        if (value == null && createdLookup != null) {
            value = createdLookup.apply(messageContext);
        }
        
        if (value == null) {
            if (isUseCurrentTimeAsDefaultCreated()) {
                value = Instant.now();
            }
        }
        return value;
    }
    
    /**
     * Get the Expires value.
     * 
     * @param messageContext the current message context
     * @param created the created value, if any
     * 
     * @return the effective Expires DateTime value to use
     */
    @Nullable protected Instant getExpiresValue(@Nonnull final MessageContext messageContext, 
            @Nullable final Instant created) {
        Instant value = null;
        final WSSecurityContext security = messageContext.getSubcontext(WSSecurityContext.class);
        if (security != null) {
            value = security.getTimestampExpires();
        }
        
        if (value == null && expiresLookup != null) {
            value = expiresLookup.apply(messageContext);
        }
        
        if (value == null) {
            if (getExpiresOffsetFromCreated() != null && created != null) {
                return created.plus(getExpiresOffsetFromCreated());
            }
        }
        return value;
    }

}