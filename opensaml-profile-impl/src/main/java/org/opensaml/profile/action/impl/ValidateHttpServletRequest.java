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
import javax.annotation.Nullable;

import org.opensaml.profile.action.AbstractConditionalProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

import jakarta.servlet.ServletException;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.servlet.HttpServletRequestValidator;

/**
 * Profile action that validates an HTTP request via an instance of {@link HttpServletRequestValidator}.
 */
public class ValidateHttpServletRequest extends AbstractConditionalProfileAction {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(ValidateHttpServletRequest.class);
    
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
        
        if (getHttpServletRequest() == null) {
            throw new ComponentInitializationException("HttpServletRequest was null");
        }
        
        if (getValidator() == null) {
            throw new ComponentInitializationException("HttpServletRequestValidator was null");
        }
            
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileContext) {
       try {
            getValidator().validate(ensureHttpServletRequest());
            ActionSupport.buildProceedEvent(profileContext);
        } catch (final ServletException e) {
            log.warn("HttpServletRequest failed validation", e);
            ActionSupport.buildEvent(profileContext, EventIds.INVALID_MESSAGE);
        }
    }

}
