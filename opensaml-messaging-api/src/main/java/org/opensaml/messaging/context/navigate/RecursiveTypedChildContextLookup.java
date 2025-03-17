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
 * A {@link ContextDataLookupFunction} that peforms a depth-first search of context's children 
 * and returns the first nested child context that is an instance of the specified type.
 * 
 * @param <ChildContext> type of the child context
 * 
 * @since 5.1.4
 */
public class RecursiveTypedChildContextLookup<ChildContext extends BaseContext>
        implements ContextDataLookupFunction<BaseContext, ChildContext> {
    
    /** The target parent class. */
    @Nonnull private Class<ChildContext> childClass;
    
    /**
     * Constructor.
     *
     * @param targetClass the target parent class 
     */
    public RecursiveTypedChildContextLookup(
            @Nonnull @ParameterName(name="targetClass") final Class<ChildContext> targetClass) {
        childClass = Constraint.isNotNull(targetClass, "Child Class cannot be null");
    }

    /** {@inheritDoc} */
    @Nullable public ChildContext apply(@Nullable final BaseContext input) {
        if (input == null) {
            return null;
        }

        for (final BaseContext subcontext : input) {
            if (childClass.isInstance(subcontext)) {
                return childClass.cast(subcontext);
            }
            
            final ChildContext found = apply(subcontext);
            if (found != null) {
                return found;
            }
        }

        return null;
    }
    
}
