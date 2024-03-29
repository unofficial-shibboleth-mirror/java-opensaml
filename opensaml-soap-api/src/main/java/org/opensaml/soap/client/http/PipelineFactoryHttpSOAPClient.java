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

package org.opensaml.soap.client.http;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipeline;
import org.opensaml.messaging.pipeline.httpclient.HttpClientMessagePipelineFactory;
import org.opensaml.soap.client.SOAPClientContext;
import org.opensaml.soap.common.SOAPException;
import org.slf4j.Logger;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * SOAP client that is based on {@link HttpClientMessagePipeline}, produced at runtime from an instance of
 * {@link HttpClientMessagePipelineFactory}.
 */
@ThreadSafe
public class PipelineFactoryHttpSOAPClient extends AbstractPipelineHttpSOAPClient {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(PipelineFactoryHttpSOAPClient.class);
    
    /** Factory for the client message pipeline. */
    @NonnullAfterInit private HttpClientMessagePipelineFactory pipelineFactory;
    
    /** Strategy function used to resolve the pipeline name to execute. */
    @NonnullAfterInit private Function<InOutOperationContext,String> pipelineNameStrategy;
    
    /**
     * Set the message pipeline factory.
     * 
     * @param factory the message pipeline factory
     */
    public void setPipelineFactory(
            @Nonnull final HttpClientMessagePipelineFactory factory) {
        checkSetterPreconditions();
        
        pipelineFactory = Constraint.isNotNull(factory, "HttpClientPipelineFactory cannot be null"); 
    }
    
    /**
     * Set the strategy function used to resolve the name of the pipeline to use.  Null may be specified.
     * 
     * @param function the strategy function, or null
     */
    public void setPipelineNameStrategy(@Nullable final Function<InOutOperationContext,String> function) {
        checkSetterPreconditions();
        
        pipelineNameStrategy = function;
    }

    /** {@inheritDoc} */
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (pipelineFactory == null) {
            throw new ComponentInitializationException("HttpClientPipelineFactory cannot be null");
        } 
        
        if (pipelineNameStrategy == null) {
            pipelineNameStrategy = new DefaultPipelineNameStrategy();
        }
    }

    /**
     * Resolve and return a new instance of the {@link HttpClientMessagePipeline} to be processed.
     * 
     * <p>
     * Each call to this (factory) method MUST produce a new instance of the pipeline.
     * </p>
     * 
     * <p>
     * The behavior of this subclass specialization is to use a factory strategy using
     * a configured instance of {@link HttpClientMessagePipeline}.  See
     * {@link #resolvePipelineName(InOutOperationContext)} and 
     * {@link #newPipeline(String)}.
     * </p>
     * 
     * @param operationContext the current operation context
     * 
     * @return a new pipeline instance
     * 
     * @throws SOAPException if there is an error obtaining a new pipeline instance
     */
    @Nonnull protected HttpClientMessagePipeline resolvePipeline(@Nonnull final InOutOperationContext operationContext)
            throws SOAPException {
        
        String resolvedPipelineName = null;
        try {
            resolvedPipelineName = resolvePipelineName(operationContext);
            log.debug("Resolved pipeline name: {}", resolvedPipelineName);
            if (resolvedPipelineName != null) {
                return newPipeline(resolvedPipelineName);
            }
            return newPipeline();
        } catch (final SOAPException e) {
            log.warn("Problem resolving pipeline instance with name {}: {}", resolvedPipelineName, e.getMessage());
            throw e;
        } catch (final Exception e) {
            // This is to handle RuntimeExceptions, for example thrown by Spring dynamic factory approaches
            log.warn("Problem resolving pipeline instance with name {}: {}", resolvedPipelineName, e.getMessage());
            throw new SOAPException("Could not resolve pipeline with name: " + resolvedPipelineName, e);
        }
    }

    /** {@inheritDoc}
     * 
     * <p>
     * The behavior of this subclass specialization is to use a factory strategy using
     * a configured instance of {@link HttpClientMessagePipeline}. 
     * </p>
     * 
     */
    @Nonnull protected HttpClientMessagePipeline newPipeline() throws SOAPException {
        // Note: in a Spring environment, the actual factory impl might be a proxy via ServiceLocatorFactoryBean
        return pipelineFactory.newInstance();
    }
    
    /**
     * Get a new instance of the {@link HttpClientMessagePipeline} to be processed.
     * 
     * <p>
     * Each call to this (factory) method MUST produce a new instance of the pipeline.
     * </p>
     * 
     * <p>
     * The behavior of this subclass specialization is to use a factory strategy using
     * a configured instance of {@link HttpClientMessagePipeline}. 
     * </p>
     * 
     * @param name the name of pipeline to return
     * 
     * @return the new pipeline instance
     * 
     * @throws SOAPException if there is an error obtaining a new pipeline instance
     */
    @Nonnull protected HttpClientMessagePipeline newPipeline(@Nullable final String name) throws SOAPException {
        // Note: in a Spring environment, the actual factory impl might be a proxy via ServiceLocatorFactoryBean
        return pipelineFactory.newInstance(name);
    }
    
    /**
     * Resolve the name of the pipeline to use.
     * 
     * @param operationContext the current operation context
     * @return the pipeline name, may be null
     */
    @Nullable protected String resolvePipelineName(@Nonnull final InOutOperationContext operationContext) {
        if (pipelineNameStrategy != null) {
            return pipelineNameStrategy.apply(operationContext);
        }
        return null;
    }
    
    
    /**
     * Default strategy for resolving SOAP client message pipeline name from the 
     * {@link SOAPClientContext#getPipelineName()} which is a direct child of the input operation context.
     */
    public static class DefaultPipelineNameStrategy implements Function<InOutOperationContext,String> {

        /** {@inheritDoc} */
        @Nullable public String apply(@Nullable final InOutOperationContext opContext) {
            if (opContext == null) {
                return null;
            }
            final SOAPClientContext context = opContext.getSubcontext(SOAPClientContext.class);
            if (context != null) {
                return context.getPipelineName();
            }
            return null;
        }
        
    }

}