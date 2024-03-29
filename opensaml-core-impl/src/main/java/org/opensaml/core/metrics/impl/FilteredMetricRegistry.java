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

package org.opensaml.core.metrics.impl;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.Timer;

import net.shibboleth.shared.logic.Constraint;

/**
 * {@link MetricRegistry} that returns a metric or a disabled wrapper for a metric based
 * on a supplied {@link MetricFilter}.
 * 
 * <p>If no filter is supplied, then all metrics are disabled.</p>
 * 
 * @since 3.3.0
 */
public class FilteredMetricRegistry extends MetricRegistry {
    
    /** Filter to apply. */
    @Nullable private MetricFilter metricFilter;
    
    /** Dummy object. */
    @Nonnull private final DisabledCounter disabledCounter;

    /** Dummy object. */
    @Nonnull private final DisabledHistogram disabledHistogram;

    /** Dummy object. */
    @Nonnull private final DisabledMeter disabledMeter;

    /** Dummy object. */
    @Nonnull private final DisabledTimer disabledTimer;

    /**
     * Constructor.
     */
    public FilteredMetricRegistry() {
        disabledCounter = new DisabledCounter();
        disabledHistogram = new DisabledHistogram();
        disabledMeter = new DisabledMeter();
        disabledTimer = new DisabledTimer();
    }
    
    /**
     * Set the filter to use.
     * 
     * @param filter filter to apply, if any
     */
    public void setMetricFilter(@Nullable final MetricFilter filter) {        
        metricFilter = filter;
    }

    /** {@inheritDoc} */
    @Override public Counter counter(final String name) {
        if (metricFilter != null && metricFilter.matches(name, null)) {
            return super.counter(name);
        }
        return disabledCounter;
    }

    /** {@inheritDoc} */
    @Override public Histogram histogram(final String name) {
        if (metricFilter != null && metricFilter.matches(name, null)) {
            return super.histogram(name);
        }
        return disabledHistogram;
    }

    /** {@inheritDoc} */
    @Override public Meter meter(final String name) {
        if (metricFilter != null && metricFilter.matches(name, null)) {
            return super.meter(name);
        }
        return disabledMeter;
    }

    /** {@inheritDoc} */
    @Override public Timer timer(final String name) {
        if (metricFilter != null && metricFilter.matches(name, null)) {
            return super.timer(name);
        }
        return disabledTimer;
    }

    /**
     * Given multiple metric sets, registers them.
     *
     * @param metricSets any number of metric sets
     * 
     * @throws IllegalArgumentException if any of the names are already registered
     */
    public void registerMultiple(@Nonnull final Collection<MetricSet> metricSets)
            throws IllegalArgumentException {
        Constraint.isNotNull(metricSets, "Collection cannot be null").forEach(this::registerAll);
    }

}