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

import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.FunctionSupport;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;
import net.shibboleth.shared.security.AccessControlService;

/**
 * This action validates that a request comes from an authorized client, based on an injected service
 * and policy parameters.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#ACCESS_DENIED}
 */
public class CheckAccess extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(CheckAccess.class);

    /** Access control service. */
    @NonnullAfterInit private AccessControlService service;
    
    /** Lookup strategy for policy to apply. */
    @Nonnull private Function<ProfileRequestContext,String> policyNameLookupStrategy;

    /** Lookup strategy for operation. */
    @Nonnull private Function<ProfileRequestContext,String> operationLookupStrategy;
    
    /** Lookup strategy for resource. */
    @Nonnull private Function<ProfileRequestContext,String> resourceLookupStrategy;
    
    /** Constructor. */
    public CheckAccess() {
        policyNameLookupStrategy = FunctionSupport.constant(null);
        operationLookupStrategy = FunctionSupport.constant(null);
        resourceLookupStrategy = FunctionSupport.constant(null);
    }

    /**
     * Set the service to use.
     * 
     * @param acs service to use
     */
    public void setAccessControlService(@Nonnull final AccessControlService acs) {
        checkSetterPreconditions();
        
        service = Constraint.isNotNull(acs, "AccessControlService cannot be null");
    }
    
    /**
     * Set a lookup strategy to use to obtain the policy name to apply.
     * 
     * @param strategy  lookup strategy
     * 
     * @since 3.3.0
     */
    public void setPolicyNameLookupStrategy(@Nonnull final Function<ProfileRequestContext,String> strategy) {
        checkSetterPreconditions();
        
        policyNameLookupStrategy = Constraint.isNotNull(strategy, "Policy lookup strategy cannot be null");
    }

    /**
     * Set an explicit policy name to apply.
     * 
     * @param name  policy name
     */
    public void setPolicyName(@Nonnull @NotEmpty final String name) {
        checkSetterPreconditions();
        
        policyNameLookupStrategy = FunctionSupport.constant(
                Constraint.isNotNull(StringSupport.trimOrNull(name), "Policy name cannot be null or empty"));
    }

    /**
     * Set a lookup strategy to use to obtain the operation.
     * 
     * @param strategy  lookup strategy
     * 
     * @since 3.3.0
     */
    public void setOperationLookupStrategy(@Nonnull final Function<ProfileRequestContext,String> strategy) {
        checkSetterPreconditions();
        
        operationLookupStrategy = Constraint.isNotNull(strategy, "Policy lookup strategy cannot be null");
    }
    
    /**
     * Set operation.
     * 
     * @param op operation
     */
    public void setOperation(@Nullable final String op) {
        checkSetterPreconditions();
        
        operationLookupStrategy = FunctionSupport.constant(StringSupport.trimOrNull(op));
    }

    /**
     * Set a lookup strategy to use to obtain the resource.
     * 
     * @param strategy  lookup strategy
     * 
     * @since 3.3.0
     */
    public void setResourceLookupStrategy(@Nonnull final Function<ProfileRequestContext,String> strategy) {
        checkSetterPreconditions();
        
        resourceLookupStrategy = Constraint.isNotNull(strategy, "Policy lookup strategy cannot be null");
    }
    
    /**
     * Set resource.
     * 
     * @param res resource
     */
    public void setResource(@Nullable final String res) {
        checkSetterPreconditions();
        
        resourceLookupStrategy = FunctionSupport.constant(StringSupport.trimOrNull(res));
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (service == null) {
            throw new ComponentInitializationException("AccessControlService cannot be null");
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        final HttpServletRequest httpRequest = getHttpServletRequest(); 
        if (httpRequest == null) {
            log.warn("{} HttpServletRequest was null, disallowing access", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.ACCESS_DENIED);
            return;
        }
        
        final String policyName = policyNameLookupStrategy.apply(profileRequestContext);
        if (policyName == null) {
            log.warn("{} No policy name returned by lookup strategy, disallowing access", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.ACCESS_DENIED);
        } else if (!service.getInstance(policyName).checkAccess(httpRequest,
                operationLookupStrategy.apply(profileRequestContext),
                resourceLookupStrategy.apply(profileRequestContext))) {
            ActionSupport.buildEvent(profileRequestContext, EventIds.ACCESS_DENIED);
        }
    }

}