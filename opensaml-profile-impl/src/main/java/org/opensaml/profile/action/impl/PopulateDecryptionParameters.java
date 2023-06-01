/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
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
import javax.annotation.Nullable;

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.AbstractConditionalProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.xmlsec.DecryptionConfiguration;
import org.opensaml.xmlsec.DecryptionParameters;
import org.opensaml.xmlsec.DecryptionParametersResolver;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.criterion.DecryptionConfigurationCriterion;
import org.opensaml.xmlsec.criterion.DecryptionRecipientsCriterion;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/**
 * Action that resolves and populates {@link DecryptionParameters} on a {@link SecurityParametersContext}
 * created/accessed via a lookup function, by default on the inbound message context.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_PROFILE_CTX}
 * @event {@link EventIds#INVALID_SEC_CFG}
 */
public class PopulateDecryptionParameters extends AbstractConditionalProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(PopulateDecryptionParameters.class);
    
    /** Strategy used to look up the {@link SecurityParametersContext} to set the parameters for. */
    @Nonnull private Function<ProfileRequestContext,SecurityParametersContext> securityParametersContextLookupStrategy;
    
    /** Strategy used to lookup a per-request {@link DecryptionConfiguration} list. */
    @NonnullAfterInit private Function<ProfileRequestContext,List<DecryptionConfiguration>> configurationLookupStrategy;
    
    /** Resolver for parameters to store into context. */
    @NonnullAfterInit private DecryptionParametersResolver resolver;

    /** Strategy used to look up the {@link SecurityParametersContext} to set the parameters for. */
    @Nullable private Function<ProfileRequestContext,String> recipientLookupStrategy;
    
    /**
     * Constructor.
     */
    public PopulateDecryptionParameters() {
        // Create context by default.
        securityParametersContextLookupStrategy =
                new ChildContextLookup<>(SecurityParametersContext.class, true).compose(
                        new InboundMessageContextLookup());
    }

    /**
     * Set the strategy used to look up the {@link SecurityParametersContext} to set the parameters for.
     * 
     * @param strategy lookup strategy
     */
    public void setSecurityParametersContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,SecurityParametersContext> strategy) {
        checkSetterPreconditions();

        securityParametersContextLookupStrategy = Constraint.isNotNull(strategy,
                "SecurityParametersContext lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to look up a per-request {@link DecryptionConfiguration} list.
     * 
     * @param strategy lookup strategy
     */
    public void setConfigurationLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,List<DecryptionConfiguration>> strategy) {
        checkSetterPreconditions();
        
        configurationLookupStrategy = Constraint.isNotNull(strategy,
                "DecryptionConfiguration lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to look up the dynamic recipient to include in decryption criteria.
     * 
     * @param strategy to use
     */
    public void setRecipientLookupStrategy(
            @Nullable final Function<ProfileRequestContext,String> strategy) {
        checkSetterPreconditions();
        
        recipientLookupStrategy = strategy;
    }
    
    /**
     * Set the resolver to use for the parameters to store into the context.
     * 
     * @param newResolver   resolver to use
     */
    public void setDecryptionParametersResolver(@Nonnull final DecryptionParametersResolver newResolver) {
        checkSetterPreconditions();
        
        resolver = Constraint.isNotNull(newResolver, "DecryptionParametersResolver cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (resolver == null) {
            throw new ComponentInitializationException("DecryptionParametersResolver cannot be null");
        } else if (configurationLookupStrategy == null) {
            configurationLookupStrategy = new Function<>() {
                public List<DecryptionConfiguration> apply(final ProfileRequestContext input) {
                    return CollectionSupport.singletonList(
                            SecurityConfigurationSupport.ensureGlobalDecryptionConfiguration());
                }
            };
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        log.debug("{} Resolving DecryptionParameters for request", getLogPrefix());

        final List<DecryptionConfiguration> configs = configurationLookupStrategy.apply(profileRequestContext);
        if (configs == null || configs.isEmpty()) {
            log.error("{} No DecryptionConfigurations returned by lookup strategy", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_SEC_CFG);
            return;
        }
        
        final SecurityParametersContext paramsCtx =
                securityParametersContextLookupStrategy.apply(profileRequestContext);
        if (paramsCtx == null) {
            log.debug("{} No SecurityParametersContext returned by lookup strategy", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_PROFILE_CTX);
            return;
        }
        
        String recipient = null;
        if (recipientLookupStrategy != null) {
            recipient = recipientLookupStrategy.apply(profileRequestContext);
            log.debug("{} Resolved dynamic recipient for decryption: {}", getLogPrefix(), recipient);
        }
        
        try {
            final CriteriaSet criteria = new CriteriaSet(new DecryptionConfigurationCriterion(configs));
            if (recipient != null) {
                criteria.add(new DecryptionRecipientsCriterion(CollectionSupport.singleton(recipient)));
            }
            final DecryptionParameters params = resolver.resolveSingle(criteria);
            paramsCtx.setDecryptionParameters(params);
            log.debug("{} {} DecryptionParameters", getLogPrefix(),
                    params != null ? "Resolved" : "Failed to resolve");
        } catch (final ResolverException e) {
            log.error("{} Error resolving DecryptionParameters", getLogPrefix(), e);
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_SEC_CFG);
        }
    }
    
}