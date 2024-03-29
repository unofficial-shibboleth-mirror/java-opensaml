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

package org.opensaml.saml.common.profile.impl;

import java.util.List;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.AbstractHandlerDelegatingProfileAction;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.binding.impl.PopulateSignatureSigningParametersHandler;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.SignatureSigningParametersResolver;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Action that resolves and populates {@link SignatureSigningParameters} on a {@link SecurityParametersContext}
 * created/accessed via a lookup function, by default on the outbound message context.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 * @event {@link EventIds#INVALID_SEC_CFG}
 */
public class PopulateSignatureSigningParameters 
        extends AbstractHandlerDelegatingProfileAction<PopulateSignatureSigningParametersHandler> {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(PopulateSignatureSigningParameters.class);
    
    /** Strategy used to look up the {@link SecurityParametersContext} to set the parameters for. */
    @Nonnull private Function<ProfileRequestContext,SecurityParametersContext> securityParametersContextLookupStrategy;

    /** Strategy used to look up an existing {@link SecurityParametersContext} to copy. */
    @Nullable private Function<ProfileRequestContext,SecurityParametersContext> existingParametersContextLookupStrategy;
    
    /** Strategy used to look up a per-request {@link SignatureSigningConfiguration} list. */
    @NonnullAfterInit
    private Function<ProfileRequestContext,List<SignatureSigningConfiguration>> configurationLookupStrategy;

    /** Strategy used to look up a SAML metadata context. */
    @Nullable private Function<ProfileRequestContext,SAMLMetadataContext> metadataContextLookupStrategy;
    
    /** Resolver for parameters to store into context. */
    @NonnullAfterInit private SignatureSigningParametersResolver resolver;
    
    /** Whether failure to resolve parameters should be raised as an error. */
    private boolean noResultIsError;
    
    /**
     * Constructor.
     */
    public PopulateSignatureSigningParameters() {
        super(PopulateSignatureSigningParametersHandler.class, new OutboundMessageContextLookup());

        // Create context by default.
        securityParametersContextLookupStrategy =
                new ChildContextLookup<>(SecurityParametersContext.class, true).compose(
                        new OutboundMessageContextLookup());

        // Default: outbound msg context -> SAMLPeerEntityContext -> SAMLMetadataContext
        metadataContextLookupStrategy =
                new ChildContextLookup<>(SAMLMetadataContext.class).compose(
                        new ChildContextLookup<>(SAMLPeerEntityContext.class).compose(
                                new OutboundMessageContextLookup()));
        
        setErrorEvent(EventIds.INVALID_SEC_CFG);
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
     * Set the strategy used to look up an existing {@link SecurityParametersContext} to copy instead
     * of actually resolving the parameters to set.
     * 
     * @param strategy lookup strategy
     */
    public void setExistingParametersContextLookupStrategy(
            @Nullable final Function<ProfileRequestContext,SecurityParametersContext> strategy) {
        checkSetterPreconditions();
        existingParametersContextLookupStrategy = strategy;
    }
    
    /**
     * Set lookup strategy for {@link SAMLMetadataContext} for input to resolution.
     * 
     * @param strategy  lookup strategy
     */
    public void setMetadataContextLookupStrategy(
            @Nullable final Function<ProfileRequestContext,SAMLMetadataContext> strategy) {
        checkSetterPreconditions();
        metadataContextLookupStrategy = strategy;
    }

    /**
     * Set the strategy used to look up a per-request {@link SignatureSigningConfiguration} list.
     * 
     * @param strategy lookup strategy
     */
    public void setConfigurationLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,List<SignatureSigningConfiguration>> strategy) {
        checkSetterPreconditions();
        configurationLookupStrategy = Constraint.isNotNull(strategy,
                "SignatureSigningConfiguration lookup strategy cannot be null");
    }
    
    /**
     * Set the resolver to use for the parameters to store into the context.
     * 
     * @param newResolver   resolver to use
     */
    public void setSignatureSigningParametersResolver(
            @Nonnull final SignatureSigningParametersResolver newResolver) {
        checkSetterPreconditions();
        resolver = Constraint.isNotNull(newResolver, "SignatureSigningParametersResolver cannot be null");
    }

    /**
     * Set whether a failure to resolve any parameters should be raised as an exception.
     * 
     * <p>Defaults to false.</p>
     * 
     * @param flag flag to set
     * 
     * @since 3.4.0
     */
    public void setNoResultIsError(final boolean flag) {
        checkSetterPreconditions();
        noResultIsError = flag;
    }

    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (resolver == null) {
            throw new ComponentInitializationException("SignatureSigningParametersResolver cannot be null");
        } else if (configurationLookupStrategy == null) {
            configurationLookupStrategy = new Function<>() {
                public List<SignatureSigningConfiguration> apply(final ProfileRequestContext input) {
                    return CollectionSupport.singletonList(
                            SecurityConfigurationSupport.ensureGlobalSignatureSigningConfiguration());
                }
            };
        }

        final PopulateSignatureSigningParametersHandler delegate = getDelegate();
        delegate.setNoResultIsError(noResultIsError);
        delegate.setSignatureSigningParametersResolver(resolver);
        delegate.setConfigurationLookupStrategy(adaptRequired(configurationLookupStrategy));
        delegate.setSecurityParametersContextLookupStrategy(adaptRequired(securityParametersContextLookupStrategy));
        delegate.setExistingParametersContextLookupStrategy(adapt(existingParametersContextLookupStrategy));
        delegate.setMetadataContextLookupStrategy(adapt(metadataContextLookupStrategy));
        delegate.initialize();
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (super.doPreExecute(profileRequestContext)) {
            log.debug("{} Signing enabled", getLogPrefix());
            return true;
        }
        log.debug("{} Signing not enabled", getLogPrefix());
        return false;
    }

}