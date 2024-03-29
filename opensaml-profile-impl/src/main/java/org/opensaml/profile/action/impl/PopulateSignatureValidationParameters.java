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

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.AbstractHandlerDelegatingProfileAction;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.SignatureValidationConfiguration;
import org.opensaml.xmlsec.SignatureValidationParameters;
import org.opensaml.xmlsec.SignatureValidationParametersResolver;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.messaging.impl.PopulateSignatureValidationParametersHandler;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;

/**
 * Action that resolves and populates {@link SignatureValidationParameters} on a {@link SecurityParametersContext}
 * created/accessed via a lookup function, by default on the inbound message context.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 * @event {@link EventIds#MESSAGE_PROC_ERROR}
 */
public class PopulateSignatureValidationParameters 
        extends AbstractHandlerDelegatingProfileAction<PopulateSignatureValidationParametersHandler> {
    
    /** Strategy used to look up the {@link SecurityParametersContext} to set the parameters for. */
    @Nonnull private Function<ProfileRequestContext,SecurityParametersContext> securityParametersContextLookupStrategy;
    
    /** Strategy used to lookup a per-request {@link SignatureValidationConfiguration} list. */
    @NonnullAfterInit
    private Function<ProfileRequestContext,List<SignatureValidationConfiguration>> configurationLookupStrategy;
    
    /** Resolver for parameters to store into context. */
    @NonnullAfterInit private SignatureValidationParametersResolver resolver;
    
    /**
     * Constructor.
     */
    public PopulateSignatureValidationParameters() {
        super(PopulateSignatureValidationParametersHandler.class, new InboundMessageContextLookup());
        
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
     * Set the strategy used to look up a per-request {@link SignatureValidationConfiguration} list.
     * 
     * @param strategy lookup strategy
     */
    public void setConfigurationLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,List<SignatureValidationConfiguration>> strategy) {
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
                public List<SignatureValidationConfiguration> apply(final ProfileRequestContext input) {
                    return CollectionSupport.singletonList(
                            SecurityConfigurationSupport.ensureGlobalSignatureValidationConfiguration());
                }
            };
        }
        
        final PopulateSignatureValidationParametersHandler delegate = getDelegate();
        assert resolver != null;
        delegate.setSignatureValidationParametersResolver(resolver);
        assert configurationLookupStrategy != null;
        delegate.setConfigurationLookupStrategy(adaptRequired(configurationLookupStrategy));
        delegate.setSecurityParametersContextLookupStrategy(adaptRequired(securityParametersContextLookupStrategy));
        delegate.initialize();
    }
    
}