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

import java.util.Objects;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.action.ActionSupport;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.profile.context.navigate.InboundMessageContextLookup;
import org.opensaml.profile.context.navigate.OutboundMessageContextLookup;
import org.opensaml.saml.common.messaging.context.ChannelBindingsContext;
import org.opensaml.saml.common.profile.SAMLEventIds;
import org.opensaml.saml.ext.saml2cb.ChannelBindings;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullBeforeExec;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Action that verifies two sets of {@link ChannelBindings} from two different {@link ChannelBindingsContext}
 * objects obtained via lookup functions, by default from below the inbound message context and from below
 * a {@link SOAP11Context} below the inbound message context.
 * 
 * <p>If neither function supplies a non-empty {@link ChannelBindingsContext}, then there is no verification
 * required, but if either one supplies a non-empty context, then a match must be achieved or an error event
 * is signaled.</p>
 * 
 * <p>If verification is successful, then the resulting match is stored in a new {@link ChannelBindingsContext}
 * object created from a lookup/creation function, by default below the outbound message context.</p>
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 * @event {@link SAMLEventIds#CHANNEL_BINDINGS_ERROR}
 * 
 * @pre {@link ChannelBindingsContext} objects to be returned from lookup functions must be populated.
 * @post Upon successful verification, a {@link ChannelBindingsContext} object will be created as described.
 */
public class VerifyChannelBindings extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(VerifyChannelBindings.class);

    /** Strategy used to locate the first set of bindings to operate on. */
    @Nonnull private Function<ProfileRequestContext,ChannelBindingsContext> channelBindingsLookupStrategy1;

    /** Strategy used to locate the second set of bindings to operate on. */
    @Nonnull private Function<ProfileRequestContext,ChannelBindingsContext> channelBindingsLookupStrategy2;
    
    /** Strategy used to locate or create the context to save the verified result in. */
    @Nonnull private Function<ProfileRequestContext,ChannelBindingsContext> channelBindingsCreationStrategy;
    
    /** The first set of bindings. */
    @NonnullBeforeExec private ChannelBindingsContext channelBindingsContext1;

    /** The second set of bindings. */
    @NonnullBeforeExec private ChannelBindingsContext channelBindingsContext2;

    /** Constructor. */
    public VerifyChannelBindings() {
        channelBindingsLookupStrategy1 =
                new ChildContextLookup<>(ChannelBindingsContext.class).compose(
                        new InboundMessageContextLookup());
        
        channelBindingsLookupStrategy2 =
                new ChildContextLookup<>(ChannelBindingsContext.class).compose(
                        new ChildContextLookup<>(SOAP11Context.class).compose(
                                new InboundMessageContextLookup()));
                
        channelBindingsCreationStrategy =
                new ChildContextLookup<>(ChannelBindingsContext.class, true).compose(
                        new OutboundMessageContextLookup());
    }
    
    /**
     * Set the strategy used to locate the first {@link ChannelBindingsContext} to operate on.
     * 
     * @param strategy lookup strategy
     */
    public void setChannelBindingsLookupStrategy1(
            @Nonnull final Function<ProfileRequestContext,ChannelBindingsContext> strategy) {
        checkSetterPreconditions();
        channelBindingsLookupStrategy1 = Constraint.isNotNull(strategy,
                "First ChannelBindingsContext lookup strategy cannot be null");
    }

    /**
     * Set the strategy used to locate the second {@link ChannelBindingsContext} to operate on.
     * 
     * @param strategy lookup strategy
     */
    public void setChannelBindingsLookupStrategy2(
            @Nonnull final Function<ProfileRequestContext,ChannelBindingsContext> strategy) {
        checkSetterPreconditions();
        channelBindingsLookupStrategy2 = Constraint.isNotNull(strategy,
                "Second ChannelBindingsContext lookup strategy cannot be null");
    }

    /**
     * Set the strategy used to create or locate the {@link ChannelBindingsContext} to save verified results in.
     * 
     * @param strategy lookup/creation strategy
     */
    public void setChannelBindingsCreationStrategy(
            @Nonnull final Function<ProfileRequestContext,ChannelBindingsContext> strategy) {
        checkSetterPreconditions();
        channelBindingsCreationStrategy = Constraint.isNotNull(strategy,
                "ChannelBindingsContext creation strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreExecute(@Nonnull final ProfileRequestContext profileRequestContext) {
        
        if (!super.doPreExecute(profileRequestContext)) {
            return false;
        }

        channelBindingsContext1 = channelBindingsLookupStrategy1.apply(profileRequestContext);
        channelBindingsContext2 = channelBindingsLookupStrategy2.apply(profileRequestContext);
        
        if (channelBindingsContext1 != null && channelBindingsContext1.getChannelBindings().isEmpty()) {
            channelBindingsContext1 = null;
        }
        
        if (channelBindingsContext2 != null && channelBindingsContext2.getChannelBindings().isEmpty()) {
            channelBindingsContext2 = null;
        }
        
        if (channelBindingsContext1 == null && channelBindingsContext2 == null) {
            log.debug("{} No channel bindings found to verify, nothing to do", getLogPrefix());
            return false;
        }

        if (channelBindingsContext1 == null || channelBindingsContext2 == null) {
            log.warn("{} Unable to verify channel bindings sent for comparison", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.CHANNEL_BINDINGS_ERROR);
            return false;
        }
        
        return true;
    }

// Checkstyle: ReturnCount OFF
    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        ChannelBindings matched = null;
        
        for (final ChannelBindings cb1 : channelBindingsContext1.getChannelBindings()) {
            for (final ChannelBindings cb2 : channelBindingsContext2.getChannelBindings()) {
                if (Objects.equals(cb1.getType(), cb2.getType())) {
                    final String cb1Data = StringSupport.trimOrNull(cb1.getValue());
                    final String cb2Data = StringSupport.trimOrNull(cb2.getValue());
                    if (Objects.equals(cb1Data, cb2Data)) {
                        matched = cb1;
                        break;
                    }
                }
            }
            if (matched != null) {
                break;
            }
        }
        
        if (matched == null) {
            log.warn("{} Unable to verify channel bindings sent for comparison", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.CHANNEL_BINDINGS_ERROR);
            return;
        }
        
        log.debug("{} Saving matched channel bindings for later use", getLogPrefix());
        
        final ChannelBindingsContext cbCtx = channelBindingsCreationStrategy.apply(profileRequestContext);
        if (cbCtx != null) {
            cbCtx.getChannelBindings().add(matched);
        } else {
            log.error("{} Unable to create ChannelBindingContext to store result", getLogPrefix());
            ActionSupport.buildEvent(profileRequestContext, SAMLEventIds.CHANNEL_BINDINGS_ERROR);
        }
    }
// Checkstyle: ReturnCount ON
    
}