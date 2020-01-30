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

package org.opensaml.saml.common.binding.security.impl;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.context.navigate.ContextDataLookupFunction;
import org.opensaml.messaging.context.navigate.RecursiveTypedParentContextLookup;
import org.opensaml.saml.common.messaging.context.AbstractSAMLEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;

import net.shibboleth.utilities.java.support.logic.Constraint;

/**
 * A general purpose context data lookup function for resolving a SAML entity ID relative to a
 * starting input {@link MessageContext}, configurable for either the inbound or outbound direction,
 * and also to specify the concrete type of {@link AbstractSAMLEntityContext} child context to resolve.
 */
public class MessageContextEntityIDLookup implements ContextDataLookupFunction<MessageContext, String> {
    
    /** Used to indicate the target message context. */
    public enum Direction {
        /** Indicates to use the inbound message context, obtained via 
         * {@link InOutOperationContext#getInboundMessageContext()}. */
        INBOUND, 
        
        /** Indicates to use the outbound message context, obtained via
         * {@link InOutOperationContext#getOutboundMessageContext()}. */
        OUTBOUND,
        };
        
    /** The message context to evaluate as the entityContext parent. */    
    @Nonnull private Direction direction;
        
    /** The actual context class holding the authenticatable SAML entity. */
    @Nonnull private Class<? extends AbstractSAMLEntityContext> entityContextClass;
    
    /** Parent operation context lookup function. */
    @Nonnull private Function<MessageContext,MessageContext> parentLookup;
    
    /**
     * Constructor.
     * 
     * <p>
     * This constructor defaults to {@link SAMLPeerEntityContext} as the entity context class.
     * </p>
     */
    public MessageContextEntityIDLookup() {
        this(SAMLPeerEntityContext.class);
    }
    
    /**
     * Constructor.
     * 
     * @param clazz the entity context class.
     */
    public MessageContextEntityIDLookup(
            @Nonnull final Class<? extends AbstractSAMLEntityContext> clazz) {
        entityContextClass = Constraint.isNotNull(clazz, "The SAML Entity context class may not be null;");
        parentLookup = new MessageContextLookup()
                .compose(new RecursiveTypedParentContextLookup<>(InOutOperationContext.class));
    }
    
    /**
     * Set the direction of operation.
     * 
     * @param dir the direction of operation
     */
    public void setDirection(@Nonnull final Direction dir) {
        direction = Constraint.isNotNull(dir, "Direction was null");
    }

    /** {@inheritDoc} */
    public String apply(@Nullable final MessageContext messageContext) {
        if (messageContext == null) {
            return null;
        }

        final MessageContext msgContext = parentLookup.apply(messageContext);
        if (msgContext == null) {
            return null;
        }

        final AbstractSAMLEntityContext entityContext = msgContext.getSubcontext(entityContextClass);
        if (entityContext == null) {
            return null;
        }

        return entityContext.getEntityId();
    }
    
    /**
     * Class for picking either the inbound or outbound message context, depending on configuration.
     */
    private class MessageContextLookup implements ContextDataLookupFunction<InOutOperationContext, MessageContext> {

        /** {@inheritDoc} */
        public MessageContext apply(@Nullable final InOutOperationContext opContext) {
            if (opContext == null) {
                return null;
            }
            
            if (direction == null) {
                throw new IllegalArgumentException("Direction must be supplied");
            }
            
            switch(direction) {
                case INBOUND:
                    return opContext.getInboundMessageContext();
                case OUTBOUND:
                    return opContext.getOutboundMessageContext();
                default:
                    throw new IllegalArgumentException("Saw unsupported value: " + direction);
            }
        }
        
    }
        
}
