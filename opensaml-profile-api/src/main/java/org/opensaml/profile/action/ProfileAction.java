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

package org.opensaml.profile.action;

import javax.annotation.Nonnull;

import org.opensaml.profile.context.ProfileRequestContext;

import net.shibboleth.shared.component.InitializableComponent;

/**
 * Interface for actions that operate on a {@link ProfileRequestContext}.
 * 
 * <p>Actions are expected to interact with the environment, access data,
 * and produce results using the context tree provided at execution time.
 * They signal unusual state transitions by attaching an {@link org.opensaml.profile.context.EventContext}
 * to the tree.</p>
 * 
 * <p>Actions may be stateful or stateless, and are therefore not inherently thread-safe.</p>
 */
public interface ProfileAction extends InitializableComponent {

    /**
     * Performs this action.
     * 
     * @param profileRequestContext the current IdP profile request context
     */
    void execute(@Nonnull final ProfileRequestContext profileRequestContext);
}