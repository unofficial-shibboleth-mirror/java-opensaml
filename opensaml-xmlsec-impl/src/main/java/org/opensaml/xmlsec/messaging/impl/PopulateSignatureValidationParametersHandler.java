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

package org.opensaml.xmlsec.messaging.impl;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.SignatureValidationConfiguration;
import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.SignatureValidationParametersResolver;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.criterion.SignatureValidationConfigurationCriterion;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;

/**
 * Handler that resolves and populates {@link SignatureValidationParameters} on a {@link SecurityParametersContext}
 * created/accessed via a lookup function, by default as an immediate child context of the target
 * {@link MessageContext}.
 */
public class PopulateSignatureValidationParametersHandler extends AbstractMessageHandler {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(PopulateSignatureValidationParametersHandler.class);
    
    /** Strategy used to look up the {@link SecurityParametersContext} to set the parameters for. */
    @Nonnull private Function<MessageContext,SecurityParametersContext> securityParametersContextLookupStrategy;
    
    /** Strategy used to lookup a per-request {@link SignatureValidationConfiguration} list. */
    @NonnullAfterInit
    private Function<MessageContext,List<SignatureValidationConfiguration>> configurationLookupStrategy;
    
    /** Resolver for parameters to store into context. */
    @NonnullAfterInit private SignatureValidationParametersResolver resolver;
    
    /**
     * Constructor.
     */
    public PopulateSignatureValidationParametersHandler() {
        // Create context by default.
        securityParametersContextLookupStrategy = new ChildContextLookup<>(SecurityParametersContext.class, true);
    }

    /**
     * Set the strategy used to look up the {@link SecurityParametersContext} to set the parameters for.
     * 
     * @param strategy lookup strategy
     */
    public void setSecurityParametersContextLookupStrategy(
            @Nonnull final Function<MessageContext,SecurityParametersContext> strategy) {
        checkSetterPreconditions();

        securityParametersContextLookupStrategy = Constraint.isNotNull(strategy,
                "SecurityParametersContext lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to look up a per-request {@link SignatureValidationConfiguration} list.
     * 
     * @param strategy lookup strategy
     */
    public void setConfigurationLookupStrategy(
            @Nonnull final Function<MessageContext,List<SignatureValidationConfiguration>> strategy) {
        checkSetterPreconditions();
        
        configurationLookupStrategy = Constraint.isNotNull(strategy,
                "SignatureValidationConfiguration lookup strategy cannot be null");
    }
    
    /**
     * Set the resolver to use for the parameters to store into the context.
     * 
     * @param newResolver   resolver to use
     */
    public void setSignatureValidationParametersResolver(
            @Nonnull final SignatureValidationParametersResolver newResolver) {
        checkSetterPreconditions();
        
        resolver = Constraint.isNotNull(newResolver, "SignatureValidationParametersResolver cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (resolver == null) {
            throw new ComponentInitializationException("SignatureValidationParametersResolver cannot be null");
        } else if (configurationLookupStrategy == null) {
            configurationLookupStrategy = new Function<>() {
                public List<SignatureValidationConfiguration> apply(final MessageContext input) {
                    return CollectionSupport.singletonList(
                            SecurityConfigurationSupport.ensureGlobalSignatureValidationConfiguration());
                }
            };
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {

        log.debug("{} Resolving SignatureValidationParameters for request", getLogPrefix());
        
        final List<SignatureValidationConfiguration> configs = configurationLookupStrategy.apply(messageContext);
        if (configs == null || configs.isEmpty()) {
            log.error("{} No SignatureValidationConfiguration returned by lookup strategy", getLogPrefix());
            throw new MessageHandlerException("No SignatureValidationConfiguration returned by lookup strategy");
        }
        
        final SecurityParametersContext paramsCtx =
                securityParametersContextLookupStrategy.apply(messageContext);
        if (paramsCtx == null) {
            log.debug("{} No SecurityParametersContext returned by lookup strategy", getLogPrefix());
            throw new MessageHandlerException("SecurityParametersContext returned by lookup strategy");
        }
        
        try {
            final SignatureValidationParameters params = resolver.resolveSingle(
                    new CriteriaSet(new SignatureValidationConfigurationCriterion(configs)));
            paramsCtx.setSignatureValidationParameters(params);
            log.debug("{} {} SignatureValidationParameters", getLogPrefix(),
                    params != null ? "Resolved" : "Failed to resolve");
        } catch (final ResolverException e) {
            log.error("{} Error resolving SignatureValidationParameters: {}", getLogPrefix(), e.getMessage());
            throw new MessageHandlerException("Error resolving SignatureValidationParameters");
        }
    }
    
}