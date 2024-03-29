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

package org.opensaml.messaging.pipeline;

import java.util.function.Function;

import javax.annotation.Nullable;

import org.opensaml.messaging.context.InOutOperationContext;

import net.shibboleth.shared.primitive.StringSupport;

/**
 * Static strategy function for resolving a pipeline name.
 */
public class StaticPipelineNameStrategy implements Function<InOutOperationContext, String> {
    
    /** The static pipeline name. */
    @Nullable private String pipelineName;

    /**
     * Constructor.
     * 
     * @param name the static pipeline name.
     */
    public StaticPipelineNameStrategy(@Nullable final String name) {
        pipelineName = StringSupport.trimOrNull(name);
    }
    
    /** {@inheritDoc} */
    @Nullable public String apply(@Nullable final InOutOperationContext input) {
        return pipelineName;
    }

}