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

package org.opensaml.messaging.context.navigate;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.logic.Constraint;

/**
 * A lookup function for resolving either the inbound or outbound {@link MessageContext} relative to
 * a particular {@link BaseContext}.
 * 
 * <p>
 * This would usually be used in composing other lookup functions.
 * </p>
 * 
 * @param <StartContext> the starting context type
 */
public class MessageContextLookup<StartContext extends BaseContext>
        implements ContextDataLookupFunction<StartContext, MessageContext> {
    
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
    @Nonnull private Direction dir;
    
    /** The operation context lookup. Defaults to {@link RecursiveTypedParentContextLookup}. */
    @Nonnull private Function<BaseContext, InOutOperationContext> opContextLookup; 
    
    /**
     * Constructor.
     *
     * @param direction the direction in which to operate.
     */
    public MessageContextLookup(@Nonnull @ParameterName(name="direction") final Direction direction) {
        this(direction, new RecursiveTypedParentContextLookup<>(InOutOperationContext.class));
    }

    /**
     * Constructor.
     *
     * @param direction the direction in which to operate.
     * @param lookup the operation context lookup
     */
    public MessageContextLookup(@Nonnull @ParameterName(name="direction") final Direction direction,
            @Nonnull @ParameterName(name="lookup") final Function<BaseContext, InOutOperationContext> lookup) {
        dir = Constraint.isNotNull(direction, "Direction was null");
        opContextLookup = Constraint.isNotNull(lookup, "InOutOperationContext lookup was null");
    }

    /** {@inheritDoc} */
    public MessageContext apply(@Nullable final BaseContext baseContext) {
        if (baseContext == null) {
            return null;
        }
        
        final InOutOperationContext opContext = opContextLookup.apply(baseContext);
        if (opContext == null) {
            return null;
        }
        
        switch(dir) {
            case INBOUND:
                return opContext.getInboundMessageContext();
            case OUTBOUND:
                return opContext.getOutboundMessageContext();
            default:
                throw new IllegalArgumentException("Saw unsupported value: " + dir);
        }
    }
        
}