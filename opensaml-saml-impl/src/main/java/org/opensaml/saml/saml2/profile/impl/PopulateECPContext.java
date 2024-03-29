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

package org.opensaml.saml.saml2.profile.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.AbstractConditionalProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.messaging.context.ECPContext;
import org.opensaml.saml.saml2.profile.context.EncryptionContext;
import org.slf4j.Logger;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Action to create and populate an {@link ECPContext} based on the request and, when encryption is in use,
 * generating a session key.
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 * @event {@link EventIds#INVALID_SEC_CFG}
 */
public class PopulateECPContext extends AbstractConditionalProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(PopulateECPContext.class);

    /** Strategy used to locate the {@link ECPContext} to populate. */
    @Nonnull private Function<ProfileRequestContext,ECPContext> ecpContextCreationStrategy;
    
    /** Strategy used to locate the {@link EncryptionContext}. */
    @Nonnull private Function<ProfileRequestContext,EncryptionContext> encryptionContextLookupStrategy;
    
    /** Random number generator. */
    @Nullable private SecureRandom randomGenerator;
    
    /** Only generate a key if encryption is expected. */
    private boolean requireEncryption;

    /**
     * Constructor.
     *  
     * @throws NoSuchAlgorithmException if unable to construct default random generator
     */
    public PopulateECPContext() throws NoSuchAlgorithmException {
        ecpContextCreationStrategy =
                new ChildContextLookup<>(ECPContext.class, true).compose(new OutboundMessageContextLookup());
        
        encryptionContextLookupStrategy =
                new ChildContextLookup<>(EncryptionContext.class).compose(new OutboundMessageContextLookup());
        
        try {
            randomGenerator = SecureRandom.getInstance("SHA1PRNG");
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA1PRNG is required to be supported by the JVM but is not", e);
        }
        
        requireEncryption = true;
    }

    /**
     * Set the strategy used to locate the {@link ECPContext} to operate on.
     * 
     * @param strategy lookup strategy
     */
    public void setECPContextCreationStrategy(
            @Nonnull final Function<ProfileRequestContext,ECPContext> strategy) {
        checkSetterPreconditions();

        ecpContextCreationStrategy = Constraint.isNotNull(strategy, "ECPContext creation strategy cannot be null");
    }
    
    /**
     * Set the strategy used to locate the {@link EncryptionContext}.
     * 
     * @param strategy  lookup strategy
     */
    public void setEncryptionContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,EncryptionContext> strategy) {
        checkSetterPreconditions();

        encryptionContextLookupStrategy = Constraint.isNotNull(strategy,
                "EncryptionContext lookup strategy cannot be null");
    }
    
    /**
     * Set the source of randomness to use, or none to bypass key generation.
     * 
     * @param generator random number generator
     */
    public void setRandomGenerator(@Nullable final SecureRandom generator) {
        checkSetterPreconditions();
        
        randomGenerator = generator;
    }
    
    /**
     * Set whether to require assertion encryption or skip session key generation.
     * 
     * @param flag  flag to set
     */
    public void setRequireEncryption(final boolean flag) {
        checkSetterPreconditions();
        
        requireEncryption = flag;
    }

    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        final ECPContext ecpContext = ecpContextCreationStrategy.apply(profileRequestContext);
        if (ecpContext == null) {
            log.error("{} Error creating ECPContext", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return;
        }
        
        final MessageContext inbound = profileRequestContext.getInboundMessageContext();
        ecpContext.setRequestAuthenticated(inbound != null && SAMLBindingSupport.isMessageSigned(inbound));
        log.debug("{} RequestAuthenticated: {}", getLogPrefix(), ecpContext.isRequestAuthenticated());
        
        boolean generateKey = true;
        
        if (requireEncryption) {
            generateKey = false;
            final EncryptionContext encryptionCtx = encryptionContextLookupStrategy.apply(profileRequestContext);
            if (encryptionCtx != null) {
                generateKey = encryptionCtx.getAssertionEncryptionParameters() != null;
            }
        }
     
        if (generateKey) {
            if (randomGenerator == null) {
                log.warn("{} Unable to generate ECP session key, random source was null");
                ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_SEC_CFG);
                return;
            }
            log.debug("{} Generating session key for use by ECP peers", getLogPrefix());
            final byte[] key = new byte[32];
            assert randomGenerator != null;
            randomGenerator.nextBytes(key);
            ecpContext.setSessionKey(key);
        } else {
            log.debug("{} Assertion encryption is not enabled, skipping session key generation", getLogPrefix());
            ecpContext.setSessionKey(null);
        }
    }

}