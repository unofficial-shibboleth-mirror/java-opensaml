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

package org.opensaml.saml.common.binding.impl;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ChildContextLookup;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.messaging.context.ChannelBindingsContext;
import org.opensaml.saml.ext.saml2cb.ChannelBindings;
import org.opensaml.soap.messaging.SOAPMessagingSupport;
import org.opensaml.soap.soap11.ActorBearing;
import org.opensaml.soap.util.SOAPSupport;
import org.slf4j.Logger;

import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * MessageHandler to add {@link ChannelBindings} headers to an outgoing SOAP envelope.
 */
public class AddChannelBindingsHeaderHandler extends AbstractMessageHandler {
   
    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(AddChannelBindingsHeaderHandler.class);
    
    /** Strategy used to locate the {@link ChannelBindingsContext} to operate on. */
    @Nonnull private Function<MessageContext,ChannelBindingsContext> channelBindingsContextLookupStrategy;
    
    /** The ChannelBindingsContext to operate on. */
    @Nullable private ChannelBindingsContext channelBindingsContext;
    
    /** Constructor. */
    public AddChannelBindingsHeaderHandler() {
        channelBindingsContextLookupStrategy = new ChildContextLookup<>(ChannelBindingsContext.class);
    }
    
    /**
     * Set the strategy used to locate the {@link ChannelBindingsContext} to operate on.
     * 
     * @param strategy lookup strategy
     */
    public void setChannelBindingsContextLookupStrategy(
            @Nonnull final Function<MessageContext,ChannelBindingsContext> strategy) {
        checkSetterPreconditions();

        channelBindingsContextLookupStrategy = Constraint.isNotNull(strategy,
                "ChannelBindingsContext lookup strategy cannot be null");
    }
    
    /** {@inheritDoc} */
    @Override
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        
        if (!super.doPreInvoke(messageContext)) {
            return false;
        }
        
        final ChannelBindingsContext cbCtx = channelBindingsContextLookupStrategy.apply(messageContext);
        if (cbCtx == null || cbCtx.getChannelBindings().isEmpty()) {
            log.debug("{} No ChannelBindings to add, nothing to do", getLogPrefix());
            return false;
        }
        
        channelBindingsContext = cbCtx;
        
        return true;
    }

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {

        final SAMLObjectBuilder<ChannelBindings> cbBuilder = (SAMLObjectBuilder<ChannelBindings>)
                XMLObjectProviderRegistrySupport.getBuilderFactory().<ChannelBindings>ensureBuilder(
                        ChannelBindings.DEFAULT_ELEMENT_NAME);
        
        assert channelBindingsContext != null;
        for (final ChannelBindings cb : channelBindingsContext.getChannelBindings()) {
            final ChannelBindings header = cbBuilder.buildObject();
            header.setType(cb.getType());
            SOAPSupport.addSOAP11MustUnderstandAttribute(header, true);
            SOAPSupport.addSOAP11ActorAttribute(header, ActorBearing.SOAP11_ACTOR_NEXT);
            try {
                SOAPMessagingSupport.addHeaderBlock(messageContext, header);
            } catch (final Exception e) {
                throw new MessageHandlerException(e);
            }
        }
    }
    
}