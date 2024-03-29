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

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.MessageChannelSecurityContext;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.context.ProfileRequestContext;

import net.shibboleth.shared.logic.Constraint;

/**
 * Abstract base class for profile actions which populate a
 * {@link org.opensaml.messaging.context.MessageChannelSecurityContext} on a {@link BaseContext},
 * where the latter is located using a lookup strategy.
 */
public abstract class AbstractMessageChannelSecurity extends AbstractProfileAction {
    
    /**
     * Strategy used to look up the parent {@link BaseContext} on which the
     * {@link org.opensaml.messaging.context.MessageChannelSecurityContext} will be populated.
     */
    @Nonnull private Function<ProfileRequestContext,BaseContext> parentContextLookupStrategy;
    
    /** Parent for eventual context. */
    @Nullable private BaseContext parentContext;
    
    /** Constructor. */
    public AbstractMessageChannelSecurity() {
        //TODO this just returns the input PRC - need better default?
        parentContextLookupStrategy = input -> input;
    }
    
    /**
     * Set the strategy used to look up the parent {@link BaseContext} on which the
     * {@link org.opensaml.messaging.context.MessageChannelSecurityContext} will be populated.
     * 
     * @param strategy strategy used to look up the parent {@link BaseContext} on which to populate
     *          the {@link org.opensaml.messaging.context.MessageChannelSecurityContext}
     */
    public void setParentContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,BaseContext> strategy) {
        checkSetterPreconditions();

        parentContextLookupStrategy = Constraint.isNotNull(strategy, "Parent context lookup strategy cannot be null");
    }

    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        parentContext = parentContextLookupStrategy.apply(profileRequestContext);
        return parentContext != null;
    }
    
    /**
     * Get the parent context on which the {@link MessageChannelSecurityContext}
     * will be populated.
     * 
     * @return the parent context
     */
    @Nullable protected BaseContext getParentContext() {
        return parentContext;
    }

    /**
     * Get the parent context on which the {@link MessageChannelSecurityContext}
     * will be populated, raising an {@link IllegalStateException} if absent.
     * 
     * @return the parent context
     */
    @Nonnull protected BaseContext ensureParentContext() {
        if (parentContext != null) {
            return parentContext;
        }
        
        throw new IllegalStateException("Parent context was null");
    }

}