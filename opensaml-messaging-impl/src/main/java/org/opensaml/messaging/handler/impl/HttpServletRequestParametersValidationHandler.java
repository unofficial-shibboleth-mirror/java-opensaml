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

package org.opensaml.messaging.handler.impl;

import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.AbstractMessageHandler;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.slf4j.Logger;

import jakarta.servlet.http.HttpServletRequest;
import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.collection.CollectionSupport;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.NonnullSupplier;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Message handler that validates HTTP request parameters for required presence, uniqueness and mutual exclusivity.
 */
public class HttpServletRequestParametersValidationHandler extends AbstractMessageHandler {
    
    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(HttpServletRequestParametersValidationHandler.class);
    
    /** Required parameters. */
    @Nonnull private Set<String> requiredParameters = CollectionSupport.emptySet();
    
    /** Unique parameters. */
    @Nonnull private Set<String> uniqueParameters = CollectionSupport.emptySet();
    
    /** Mutually exclusive parameters. */
    @Nonnull private Set<Set<String>> mutuallyExclusiveParameters = CollectionSupport.emptySet();
    
    /** The HttpServletRequest being processed. */
    @NonnullAfterInit private NonnullSupplier<HttpServletRequest> httpServletRequestSupplier;
    
    /**
     * Get the HTTP servlet request being processed.
     * 
     * @return Returns the request.
     */
    @NonnullAfterInit public HttpServletRequest getHttpServletRequest() {
        if (httpServletRequestSupplier == null) {
            return null;
        }
        return httpServletRequestSupplier.get();
    }

    /**
     * Get the supplier for  HTTP request if available.
     *
     * @return current HTTP request
     */
    @NonnullAfterInit public NonnullSupplier<HttpServletRequest> getHttpServletRequestSupplier() {
        return httpServletRequestSupplier;
    }

    /**
     * Set the current HTTP request Supplier.
     *
     * @param requestSupplier Supplier for the current HTTP request
     */
    public void setHttpServletRequestSupplier(@Nullable final NonnullSupplier<HttpServletRequest> requestSupplier) {
        checkSetterPreconditions();
        httpServletRequestSupplier = requestSupplier;
    }

    /**
     * Get the required parameters. 
     * 
     * <p>A required parameter must be present in the request.</p>
     * 
     * @return the required parameters
     */
    @Nonnull public Set<String> getRequiredParameters() {
        return requiredParameters;
    }

    /**
     * Set the required parameters. 
     * 
     * <p>A required parameter must be present in the request.</p>
     * 
     * @param params the required parameters
     */
    public void setRequiredParameters(@Nullable final Set<String> params) {
        checkSetterPreconditions();
        requiredParameters = CollectionSupport.copyToSet(StringSupport.normalizeStringCollection(params));
    }

    /**
     * Get the unique parameters. 
     * 
     * <p>A unique parameter must have at most 1 value.</p>
     * 
     * @return the unique parameters
     */
    @Nonnull public Set<String> getUniqueParameters() {
        return uniqueParameters;
    }

    /**
     * Set the unique parameters. 
     * 
     * <p>A unique parameter must have at most 1 value.</p>
     * 
     * @param params the unique parameters
     */
    public void setUniqueParameters(@Nullable final Set<String> params) {
        checkSetterPreconditions();
        uniqueParameters = CollectionSupport.copyToSet(StringSupport.normalizeStringCollection(params));
    }

    /**
     * Get the mutually exclusive parameters.
     * 
     * <p>A request may not contain more then 1 parameter from an exclusivity set (the "inner" set(s) configured).
     * Multiple exclusivity sets may be specified.</p>
     * 
     * @return the mutually exclusive parameters
     */
    @Nonnull public Set<Set<String>> getMutuallyExclusiveParameters() {
        return mutuallyExclusiveParameters;
    }

    /**
     * Set the mutually exclusive parameters.
     * 
     * <p>A request may not contain more then 1 parameter from an exclusivity set (the "inner" set(s) configured).
     * Multiple exclusivity sets may be specified.</p>
     * 
     * @param params the mutually exclusive parameters 
     */
    public void setMutuallyExclusiveParameters(@Nullable final Set<Set<String>> params) {
        checkSetterPreconditions();
        if (params == null) {
            mutuallyExclusiveParameters = CollectionSupport.emptySet();
        } else {
            mutuallyExclusiveParameters = params.stream()
                    .map(StringSupport::normalizeStringCollection)
                    .map(CollectionSupport::copyToSet)
                    .collect(CollectionSupport.nonnullCollector(Collectors.toSet())).get();
        }
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (getHttpServletRequest() == null) {
            throw new ComponentInitializationException("HttpServletRequest cannot be null");
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doInvoke(@Nonnull final MessageContext messageContext) throws MessageHandlerException {
        final HttpServletRequest request = getHttpServletRequest();
        final Set<String> requestParams = request.getParameterMap().keySet();

        log.debug("{} Evaluating request for required parameters: {}", getLogPrefix(), getRequiredParameters());
        for (final String param : getRequiredParameters()) {
            if (!requestParams.contains(param)) {
                log.warn("{} HTTP request did not contain required parameter: {}", getLogPrefix(), param);
                throw new MessageHandlerException("HTTP request did not contain required parameter: " + param);
            }
        }
        
        log.debug("{} Evaluating request for unique parameters: {}", getLogPrefix(), getUniqueParameters());
        for (final String param : getUniqueParameters()) {
            final String[] values = request.getParameterValues(param);
            if (values != null && values.length > 1) {
                log.warn("{} HTTP request contained {} values for parameter: {}", getLogPrefix(), values.length, param);
                throw new MessageHandlerException("HTTP request contained multiple values for parameter: " + param);
            }
        }
        
        log.debug("{} Evaluating request for mutually exclusive parameters: {}", getLogPrefix(),
                getMutuallyExclusiveParameters());
        for (final Set<String> group : getMutuallyExclusiveParameters())  {
            if (group.size() < 2) {
                log.debug("{} Exclusivity group had < 2 members, skipping evaluation: ", getLogPrefix(), group);
                continue;
            }
            
            final Set<String> groupIntersection = requestParams.stream()
                    .filter(p -> group.contains(p))
                    .collect(Collectors.toSet());

           if (groupIntersection.size() > 1) {
               log.warn("{} HTTP request contained mutuallly exclusive parameters: {}", getLogPrefix(),
                       groupIntersection);
               throw new MessageHandlerException("HTTP request contained mutually exclusivity parameters: "
                       + groupIntersection);
           }
       }
        
    }

}
