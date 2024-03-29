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

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.security.messaging.ClientTLSSecurityParametersContext;
import org.opensaml.security.x509.tls.ClientTLSValidationConfiguration;
import org.opensaml.security.x509.tls.ClientTLSValidationConfigurationCriterion;
import org.opensaml.security.x509.tls.ClientTLSValidationParameters;
import org.opensaml.security.x509.tls.ClientTLSValidationParametersResolver;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/**
 * Action that resolves and populates {@link ClientTLSValidationParameters} on a 
 * {@link ClientTLSSecurityParametersContext} created/accessed via a lookup function, 
 * by default on the inbound message context.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_PROFILE_CTX}
 * @event {@link EventIds#INVALID_SEC_CFG}
 */
public class PopulateClientTLSValidationParameters extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(PopulateClientTLSValidationParameters.class);
    
    /** Strategy used to look up the {@link ClientTLSSecurityParametersContext} to set the parameters for. */
    @Nonnull private Function<ProfileRequestContext,ClientTLSSecurityParametersContext> 
        securityParametersContextLookupStrategy;
    
    /** Strategy used to lookup a per-request {@link ClientTLSValidationConfiguration} list. */
    @NonnullAfterInit
    private Function<ProfileRequestContext,List<ClientTLSValidationConfiguration>> configurationLookupStrategy;
    
    /** Resolver for parameters to store into context. */
    @NonnullAfterInit private ClientTLSValidationParametersResolver resolver;
    
    /**
     * Constructor.
     */
    public PopulateClientTLSValidationParameters() {
        // Create context by default.
        securityParametersContextLookupStrategy =
                new ChildContextLookup<>(ClientTLSSecurityParametersContext.class, true).compose(
                        new InboundMessageContextLookup());
    }

    /**
     * Set the strategy used to look up the {@link ClientTLSSecurityParametersContext} to set the parameters for.
     * 
     * @param strategy lookup strategy
     */
    public void setSecurityParametersContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,ClientTLSSecurityParametersContext> strategy) {
        checkSetterPreconditions();

        securityParametersContextLookupStrategy = Constraint.isNotNull(strategy,
                "ClientTLSSecurityParametersContext lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to look up a per-request {@link ClientTLSValidationConfiguration} list.
     * 
     * @param strategy lookup strategy
     */
    public void setConfigurationLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,List<ClientTLSValidationConfiguration>> strategy) {
        checkSetterPreconditions();
        
        configurationLookupStrategy = Constraint.isNotNull(strategy,
                "ClientTLSValidationConfiguration lookup strategy cannot be null");
    }
    
    /**
     * Set the resolver to use for the parameters to store into the context.
     * 
     * @param newResolver   resolver to use
     */
    public void setClientTLSValidationParametersResolver(
            @Nonnull final ClientTLSValidationParametersResolver newResolver) {
        checkSetterPreconditions();
        
        resolver = Constraint.isNotNull(newResolver, "ClientTLSValidationParametersResolver cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (resolver == null) {
            throw new ComponentInitializationException("ClientTLSValidationParametersResolver cannot be null");
        } else if (configurationLookupStrategy == null) {
            configurationLookupStrategy = new Function<>() {
                public List<ClientTLSValidationConfiguration> apply(final ProfileRequestContext input) {
                    return CollectionSupport.singletonList(
                            ConfigurationService.ensure(ClientTLSValidationConfiguration.class));
                }
            };
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        log.debug("{} Resolving ClientTLSValidationParameters for request", getLogPrefix());
        
        final List<ClientTLSValidationConfiguration> configs = configurationLookupStrategy.apply(profileRequestContext);
        if (configs == null || configs.isEmpty()) {
            log.error("{} No ClientTLSValidationConfiguration returned by lookup strategy", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_SEC_CFG);
            return;
        }
        
        final ClientTLSSecurityParametersContext paramsCtx =
                securityParametersContextLookupStrategy.apply(profileRequestContext);
        if (paramsCtx == null) {
            log.debug("{} No ClientTLSSecurityParametersContext returned by lookup strategy", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return;
        }
        
        try {
            final ClientTLSValidationParameters params = resolver.resolveSingle(
                    new CriteriaSet(new ClientTLSValidationConfigurationCriterion(configs)));
            paramsCtx.setValidationParameters(params);
            log.debug("{} {} ClientTLSValidationParameters", getLogPrefix(),
                    params != null ? "Resolved" : "Failed to resolve");
        } catch (final ResolverException e) {
            log.error("{} Error resolving ClientTLSValidationParameters", getLogPrefix(), e);
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_SEC_CFG);
        }
    }
    
}