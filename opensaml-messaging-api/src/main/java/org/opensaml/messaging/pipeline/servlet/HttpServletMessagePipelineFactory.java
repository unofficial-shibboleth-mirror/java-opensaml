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

package org.opensaml.messaging.pipeline.servlet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Factory for instances of {@link HttpServletMessagePipeline}.
 */
public interface HttpServletMessagePipelineFactory {
    
    /**
     * Return a new instance of {@link HttpServletMessagePipelineFactory}.
     * 
     * @return a new pipeline instance
     */
    @Nonnull HttpServletMessagePipeline newInstance();

    /**
     * Return a new instance of {@link HttpServletMessagePipelineFactory}.
     * 
     * @param pipelineName the name of the pipeline to return
     * 
     * @return a new pipeline instance
     */
    @Nonnull HttpServletMessagePipeline newInstance(@Nullable final String pipelineName);

}