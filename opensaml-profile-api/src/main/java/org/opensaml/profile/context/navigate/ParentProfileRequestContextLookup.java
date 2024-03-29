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

package org.opensaml.profile.context.navigate;

import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.navigate.RecursiveTypedParentContextLookup;
import org.opensaml.profile.context.ProfileRequestContext;

/**
 * A convenience subtype of {@link RecursiveTypedParentContextLookup} which returns the {@link ProfileRequestContext}
 * parent of the target {@link BaseContext}.
 * 
 * @param <StartContext> type of starting context
 */
public class ParentProfileRequestContextLookup<StartContext extends BaseContext> 
        extends RecursiveTypedParentContextLookup<StartContext, ProfileRequestContext> {

    /** Constructor. */
    public ParentProfileRequestContextLookup() {
        super(ProfileRequestContext.class);
    }

}