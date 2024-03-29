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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.BaseContext;

import net.shibboleth.shared.annotation.ParameterName;
import net.shibboleth.shared.logic.Constraint;

/**
 * A {@link ContextDataLookupFunction} that gets the child context of a given parent context.
 * 
 * @param <ParentContext> type of the parent context
 * @param <ChildContext> type of the child context
 */
public class ChildContextLookup<ParentContext extends BaseContext, ChildContext extends BaseContext> implements
        ContextDataLookupFunction<ParentContext, ChildContext> {

    /** Child context type to look up. */
    @Nonnull private final Class<ChildContext> childType;

    /**
     * Whether the child context should be created if it doesn't exist. This requires that the child context has a
     * no-arg constructor.
     */
    private boolean autocreate;

    /**
     * Constructor.
     * 
     * <p>Equivalent to calling the two-parameter constructor with false.</p>
     * 
     * @param type child context type to look up
     */
    public ChildContextLookup(@Nonnull @ParameterName(name="type") final Class<ChildContext> type) {
        childType = Constraint.isNotNull(type, "Child context type cannot be null");
        autocreate = false;
    }

    /**
     * Constructor.
     * 
     * @param type child context type to look up
     * @param createContext whether to create the child context if it does not exist
     */
    public ChildContextLookup(@Nonnull @ParameterName(name="type") final Class<ChildContext> type,
            @ParameterName(name="createContext") final boolean createContext) {
        childType = Constraint.isNotNull(type, "Child context type cannot be null");
        autocreate = createContext;
    }

    /** {@inheritDoc} */
    @Nullable public ChildContext apply(@Nullable final ParentContext input) {
        if (input == null) {
            return null;
        }

        if (autocreate) {
            return input.ensureSubcontext(childType);
        } else {
            return input.getSubcontext(childType);
        }
    }
    
}