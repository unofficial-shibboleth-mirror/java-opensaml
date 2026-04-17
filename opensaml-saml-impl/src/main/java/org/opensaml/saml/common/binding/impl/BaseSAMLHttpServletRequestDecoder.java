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

package org.opensaml.saml.common.binding.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.decoder.servlet.BaseHttpServletRequestXMLMessageDecoder;
import org.opensaml.saml.common.binding.MessageType;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.config.SAMLConfigurationSupport;
import org.slf4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Abstract base decoder for SAML decoders.
 */
public abstract class BaseSAMLHttpServletRequestDecoder extends BaseHttpServletRequestXMLMessageDecoder {
    
    /** Logger. */
    private final Logger log = LoggerFactory.getLogger(BaseSAMLHttpServletRequestDecoder.class);
    
    /** Cached message type. */
    private MessageType messageType;

    /** Constructor. */
    public BaseSAMLHttpServletRequestDecoder() {
        super();
        setProtocolMessageLoggerSubCategory("SAML");
    }

    /** {@inheritDoc} */
    @Override
    protected void validateHttpRequest(@Nonnull final HttpServletRequest request) throws MessageDecodingException {
        super.validateHttpRequest(request);
        
        evaluateMessageSizeLimit();
    }
    
    /**
     * Enforce the configured message size limit, if enabled.
     * 
     * @throws MessageDecodingException if message size exceeds the enabled and configured limit,
     *         or if evaluation encounters a fatal error
     */
    protected void evaluateMessageSizeLimit() throws MessageDecodingException {
        SAMLBindingSupport.evaluateMessageSizeLimit(isEnforceMessageSizeLimit(), getMessageSizeLimit(),
                validateAndGetMessageSize());
    }
    
    /**
     * Get the type of message being processed by the decoder.
     * 
     * @return the message type
     * 
     * @throws MessageDecodingException if the MessageType can not be determined
     */
    @Nonnull protected MessageType getMessageType() throws MessageDecodingException {
        if (messageType != null) {
            return messageType;
        }
        
        final HttpServletRequest httpRequest = getHttpServletRequest();
        if (httpRequest == null) {
            throw new MessageDecodingException("HttpServletRequest was null");
        }
        
        messageType = SAMLBindingSupport.getMessageType(httpRequest);
        if (messageType == null) {
            throw new MessageDecodingException("MessageType could not be determinied");
        }
        
        log.debug("MessageType evaluated to: {}", messageType);
        
        return messageType;
    }

    /**
     * Get whether enforcement of message size limit is enabled.
     * 
     * @return true if enabled, false if not
     * 
     * @throws MessageDecodingException if there is a fatal error during evaluation
     */
    protected boolean isEnforceMessageSizeLimit() throws MessageDecodingException{
        switch (getMessageType()) {
            case REQUEST:
                return SAMLConfigurationSupport.isEnforceDecoderRequestSizeLimit();
            case RESPONSE:
                return SAMLConfigurationSupport.isEnforceDecoderResponseSizeLimit();
            case ARTIFACT:
                return false;
            default:
                log.error("Saw invalid MessageType: {}", getMessageType());
                return false;
        }
    }

    /**
     * Get the configured message size limit, in bytes.
     * 
     * @return the message size limit in bytes, or null if not configured
     * 
     * @throws MessageDecodingException if there is a fatal error during evaluation
     */
    @Nullable protected Integer getMessageSizeLimit() throws MessageDecodingException {
        switch (getMessageType()) {
            case REQUEST:
                return SAMLConfigurationSupport.getDecoderRequestSizeLimit();
            case RESPONSE:
                return SAMLConfigurationSupport.getDecoderResponseSizeLimit();
            case ARTIFACT:
                return null;
            default:
                log.error("Saw invalid MessageType: {}", getMessageType());
                return null;
        }
    }

    /**
     * Validate message parameter values and return the message size in bytes.
     * 
     * @return the message size in bytes, or null if can not be determined
     * 
     * @throws MessageDecodingException if the HTTP request carries an invalid number
     *         of values for the specified message type
     */
    @Nullable protected Integer validateAndGetMessageSize() throws MessageDecodingException {
        final HttpServletRequest httpRequest = getHttpServletRequest();
        assert httpRequest != null;

        SAMLBindingSupport.validateMessageTypeValues(httpRequest, getMessageType());
        
        final Integer messageSize = getMessageSize();
        log.debug("Message size was evaluated to {} bytes", messageSize);

        return messageSize;
    }

    /**
     * Get the message size in bytes.
     * 
     * <p>
     * The default implementation is to return null. Specific decoder implementation MUST implement
     * this if message size limit enforcement is to be effectively enabled for that type of concrete decoder.
     * Implementations may assume that the parameter set has already been validated and the request
     * contains only 1 message parameter and 1 value for that parameter.
     * </p>
     * 
     * @return the message size in bytes, or null if can not be determined
     * 
     * @throws MessageDecodingException if there is a fatal error determining message size
     */
    @Nullable protected Integer getMessageSize() throws MessageDecodingException {
        return null;
    }

}
