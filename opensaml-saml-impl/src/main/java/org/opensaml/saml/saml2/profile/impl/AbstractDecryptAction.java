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

import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.messaging.context.navigate.MessageLookup;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.action.EventIds;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.EncryptedElementType;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.xmlsec.DecryptionParameters;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.logic.PredicateSupport;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * Abstract base class for actions that perform SAML decryption.
 * 
 * <p>The actual message to handle is obtained via strategy function, by default the inbound message.</p> 
 * 
 * <p>The {@link SecurityParametersContext} governing the decryption process is located by a lookup
 * strategy, by default a child of the inbound message context.</p>
 * 
 * @event {@link EventIds#PROCEED_EVENT_ID}
 * @event {@link EventIds#INVALID_MSG_CTX}
 */
public abstract class AbstractDecryptAction extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AbstractDecryptAction.class);
    
    /** Are decryption failures a fatal condition? */
    private boolean errorFatal;

    /** Strategy used to locate the {@link SecurityParametersContext}. */
    @Nonnull private Function<ProfileRequestContext,SecurityParametersContext> securityParamsLookupStrategy;

    /** Strategy used to locate the SAML message to operate on. */
    @Nonnull private Function<ProfileRequestContext,Object> messageLookupStrategy;
    
    /** Predicate determining whether to attempt decryption. */
    @Nonnull private Predicate<Pair<ProfileRequestContext,EncryptedElementType>> decryptionPredicate;
    
    /** The decryption object. */
    @Nullable private Decrypter decrypter;
    
    /** Message to operate on. */
    @NonnullBeforeExec private SAMLObject message;
    
    /** Constructor. */
    public AbstractDecryptAction() {
        errorFatal = true;
        securityParamsLookupStrategy =
                new ChildContextLookup<>(SecurityParametersContext.class).compose(
                        new InboundMessageContextLookup());
        messageLookupStrategy = new MessageLookup<>(Object.class).compose(
                new InboundMessageContextLookup());
        decryptionPredicate = PredicateSupport.alwaysTrue();
    }
    
    /**
     * Get whether decryption failure should be treated as an error or ignored.
     * 
     * @return whether decryption failure should be treated as an error or ignored
     */
    public boolean isErrorFatal() {
        return errorFatal;
    }
    
    /**
     * Set whether decryption failure should be treated as an error or ignored.
     * 
     * @param flag  true iff decryption failure should be fatal
     */
    public void setErrorFatal(final boolean flag) {
        checkSetterPreconditions();
        
        errorFatal = flag;
    }
    
    /**
     * Set the strategy used to locate the {@link SecurityParametersContext} associated with a given
     * {@link ProfileRequestContext}.
     * 
     * @param strategy strategy used to locate the {@link SecurityParametersContext} associated with a given
     *            {@link ProfileRequestContext}
     */
    public void setSecurityParametersContextLookupStrategy(
            @Nonnull final Function<ProfileRequestContext,SecurityParametersContext> strategy) {
        checkSetterPreconditions();

        securityParamsLookupStrategy =
                Constraint.isNotNull(strategy, "SecurityParametersContext lookup strategy cannot be null");
    }
    
    /**
     * Set the strategy used to locate the {@link SAMLObject} to operate on.
     * 
     * @param strategy strategy used to locate the {@link SAMLObject} to operate on
     */
    public void setMessageLookupStrategy(@Nonnull final Function<ProfileRequestContext,Object> strategy) {
        checkSetterPreconditions();

        messageLookupStrategy = Constraint.isNotNull(strategy, "Message lookup strategy cannot be null");
    }
    
    /**
     * Get the predicate used to determine whether to attempt decryption.
     * 
     * @return the predicate
     */
    @Nonnull public Predicate<Pair<ProfileRequestContext,EncryptedElementType>> getDecryptionPredicate() {
        return decryptionPredicate;
    }
    
    /**
     * Set the predicate used to determine whether to attempt decryption.
     * 
     * @param predicate predicate to use
     */
    public void setDecryptionPredicate(
            @Nonnull final Predicate<Pair<ProfileRequestContext,EncryptedElementType>> predicate) {
        checkSetterPreconditions();
        
        decryptionPredicate = Constraint.isNotNull(predicate, "Decryption predicate cannot be null");
    }
    
    /**
     * Get the decrypter.
     * 
     * @return  the decrypter
     */
    @Nullable public Decrypter getDecrypter() {
        return decrypter;
    }
    
    /**
     * Get the object to act on.
     * 
     * @return  the object to act on
     */
    @NonnullBeforeExec public SAMLObject getSAMLObject() {
        return message;
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }
        
        final Object theMessage = messageLookupStrategy.apply(profileRequestContext);
        if (theMessage == null) {
            log.debug("{} No message was returned by lookup strategy", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, EventIds.INVALID_MSG_CTX);
            return false;
        } else if (!(theMessage instanceof SAMLObject)) {
            log.debug("{} Message was not a SAML construct, nothing to do", getLogPrefix());
            return false;
        }
        
        message = (SAMLObject) theMessage;
        
        final SecurityParametersContext paramsCtx = securityParamsLookupStrategy.apply(profileRequestContext);
        final DecryptionParameters params = paramsCtx != null ? paramsCtx.getDecryptionParameters() : null;
        if (params == null) {
            log.debug("{} No security parameter context or decryption parameters", getLogPrefix());
        } else {
            decrypter = new Decrypter(params);
        }
        
        return true;
    }
    
}