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

package org.opensaml.messaging.handler.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractHttpServletRequestMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.slf4j.Logger;

import jakarta.servlet.ServletException;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.servlet.HttpServletRequestValidator;

/**
 * Message handler that validates an HTTP request via an instance of {@link HttpServletRequestValidator}.
 */
public class HttpServletRequestValidationHandler extends AbstractHttpServletRequestMessageHandler {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(HttpServletRequestValidationHandler.class);
    
    /** Request validator. */
    @NonnullAfterInit private HttpServletRequestValidator validator;

    /**
     * Get the request validator. 
     * 
     * @return the validator
     */
    @NonnullAfterInit public HttpServletRequestValidator getValidator() {
        return validator;
    }

    /**
     * Set the request validator.
     * 
     * @param newValidator the request validator
     */
    public void setValidator(final @Nullable HttpServletRequestValidator newValidator) {
        checkSetterPreconditions();
        validator = newValidator;
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (getValidator() == null) {
            throw new ComponentInitializationException("HttpServletRequestValidator was null");
        }
            
    }

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        try {
            getValidator().validate(getHttpServletRequest());
        } catch (final ServletException e) {
            throw new MessageHandlerException("HttpServletRequest was invalid", e);
        }
        
    }

}
