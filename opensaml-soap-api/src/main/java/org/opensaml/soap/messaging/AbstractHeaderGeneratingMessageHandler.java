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

package org.opensaml.soap.messaging;

import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;

import net.shibboleth.shared.primitive.StringSupport;

/**
 * Abstract base class for message handlers that generate SOAP headers.
 */
public abstract class AbstractHeaderGeneratingMessageHandler extends AbstractMessageHandler {
    
    /** The statically configured value for mustUnderstand. */
    private boolean mustUnderstand;
    
    /** Predicate strategy for evaluating mustUnderstand from the message context. */
    @Nullable private Predicate<MessageContext> mustUnderstandStrategy;
    
    /** The effective mustUnderstand value to use. */
    private boolean effectiveMustUnderstand;
    
    /** The statically configured value for target node (SOAP 1.1 actor or SOAP 1.2 role). */
    @Nullable private String targetNode;
    
    /** Function strategy for resolving target node from the message context. */
    @Nullable private Function<MessageContext,String> targetNodeStrategy;
    
    /** The effective target node value to use. */
    @Nullable private String effectiveTargetNode;
    
    /**
     * Set the statically configured value for mustUnderstand.
     * 
     * @param flag true if header must be understood, false if not
     */
    public void setMustUnderstand(final boolean flag) {
        checkSetterPreconditions();
        mustUnderstand = flag;
    }
    
    /**
     * Set the predicate strategy for evaluating mustUnderstand from the message context.
     * 
     * @param strategy the predicate strategy
     */
    public void setMustUnderstandStrategy(@Nullable final Predicate<MessageContext> strategy) {
        checkSetterPreconditions();
        mustUnderstandStrategy = strategy;
    }
    
    /**
     * Get the effective value for mustUnderstand.
     * 
     * @return the effective value for mustUnderstand.
     */
    protected boolean isEffectiveMustUnderstand() {
        return effectiveMustUnderstand;
    }

    /**
     * Set the statically configured value for target node (SOAP 1.1 actor or SOAP 1.2 role).
     * 
     * @param node the target node, may be null
     */
    public void setTargetNode(@Nullable final String node) {
        checkSetterPreconditions();
        targetNode = StringSupport.trimOrNull(node);
    }
    
    /**
     * Set the predicate strategy for evaluating mustUnderstand from the message context.
     * 
     * @param strategy the predicate strategy
     */
    public void setTargetNodeStrategy(@Nullable final Function<MessageContext,String> strategy) {
        checkSetterPreconditions();
        targetNodeStrategy = strategy;
    }
    
    /**
     * Get the effective value for target node (SOAP 1.1 actor or SOAP 1.2 role).
     * 
     * @return the effective value for target node
     */
    protected String getEffectiveTargetNode() {
        return effectiveTargetNode;
    }
    
    /** {@inheritDoc} */
    protected boolean doPreInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        if (!super.doPreInvoke(messageContext)) {
            return false;
        }
        
        if (mustUnderstandStrategy != null) {
            effectiveMustUnderstand = mustUnderstandStrategy.test(messageContext);
        } else {
            effectiveMustUnderstand = mustUnderstand;
        }
        
        if (targetNodeStrategy != null) {
            effectiveTargetNode = targetNodeStrategy.apply(messageContext);
        } else {
            effectiveTargetNode = targetNode;
        }
        
        return true;
    }
    
    /**
     * Decorate the header based on configured and/or resolved values.
     * 
     * @param messageContext the current message context
     * @param header the header to decorate
     */
    protected void decorateGeneratedHeader(@Nonnull final MessageContext messageContext, 
            @Nonnull final XMLObject header) {
        if (isEffectiveMustUnderstand()) {
            SOAPMessagingSupport.addMustUnderstand(messageContext, header, true);
        }
        if (getEffectiveTargetNode() != null) {
            SOAPMessagingSupport.addTargetNode(messageContext, header, getEffectiveTargetNode());
        }
    }
    
}