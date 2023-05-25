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

package org.opensaml.profile.action.impl;

import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.core.metrics.MetricsSupport;
import org.opensaml.profile.action.AbstractProfileAction;
import org.opensaml.profile.context.MetricContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.slf4j.Logger;

import com.codahale.metrics.MetricRegistry;

import net.shibboleth.shared.annotation.constraint.NonnullAfterInit;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.primitive.LoggerFactory;
import net.shibboleth.shared.primitive.StringSupport;


/**
 * An action that populates a {@link MetricContext} child of the {@link ProfileRequestContext} with
 * a set of rules for activating timer measurements and counters on associated objects during the execution
 * of a profile request.
 * 
 * <p>Unlike a more typical "lookup strategy" design used in most other places, the strategy function
 * supplied is free, and indeed expected, to directly manipulate the created child context directly
 * rather than returning the data to use. The function may return false to indicate a lack of success,
 * but this is merely logged.</p>
 * 
 * <p>A side effect of this action is the incrementing of a counter corresponding to the profile running.</p>
 * 
 * @event {@link org.opensaml.profile.action.EventIds#PROCEED_EVENT_ID}
 */
public class PopulateMetricContext extends AbstractProfileAction {

    /** Class logger. */
    @Nonnull private final Logger log = LoggerFactory.getLogger(PopulateMetricContext.class);
    
    /** Counter to increment on execution. */
    @Nullable private String counterName;
    
    /** Strategy function for establishing metric mappings to apply. */
    @NonnullAfterInit private Function<ProfileRequestContext,Boolean> metricStrategy;
    
    /**
     * Set a counter name to increment.
     * 
     * @param name counter name
     * 
     * @since 5.0.0
     */
    public void setCounterName(@Nullable final String name) {
        checkSetterPreconditions();
        
        counterName = StringSupport.trimOrNull(name);
    }
    
    /**
     * Set strategy to establish the metric mappings to use.
     * 
     * @param strategy  timer mapping strategy
     */
    public void setMetricStrategy(@Nullable final Function<ProfileRequestContext,Boolean> strategy) {
        checkSetterPreconditions();
        
        metricStrategy = strategy;
    }
    
    /** {@inheritDoc} */
    @Override
    protected void doInitialize() throws ComponentInitializationException {
        super.doInitialize();
        
        if (metricStrategy == null) {
            metricStrategy = new NullFunction();
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doExecute(@Nonnull final ProfileRequestContext profileRequestContext) {

        // Increment a counter if set.
        if (counterName != null) {
            final MetricRegistry registry = MetricsSupport.getMetricRegistry();
            if (registry != null) {
                registry.counter(counterName).inc();
            }
        }
        
        final MetricContext metricCtx = new MetricContext();
        profileRequestContext.addSubcontext(metricCtx, true);
        if (!metricStrategy.apply(profileRequestContext)) {
            log.warn("{} Configuration of metric mappings by supplied strategy function failed", getLogPrefix());
        }
    }

    /**
     * Default function to remove the context from the tree when no metrics are installed.
     */
    private class NullFunction implements Function<ProfileRequestContext,Boolean> {

        /** {@inheritDoc} */
        public Boolean apply(final ProfileRequestContext input) {
            input.removeSubcontext(MetricContext.class);
            return true;
        }
        
    }
    
}