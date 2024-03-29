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

package org.opensaml.profile.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nonnull;

import org.opensaml.core.metrics.MetricsSupport;
import org.opensaml.messaging.context.BaseContext;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import net.shibboleth.shared.annotation.constraint.Live;
import net.shibboleth.shared.annotation.constraint.NotEmpty;
import net.shibboleth.shared.collection.Pair;
import net.shibboleth.shared.logic.Constraint;
import net.shibboleth.shared.primitive.StringSupport;

/**
 * Child context that supplies instructions to the runtime actions
 * about timers to start and stop to measure performance.
 */
public final class MetricContext extends BaseContext {
    
    /**
     * Map of objects to timer names to start and objects to stop the timer.
     * 
     * <p>The first member is the timer name, the second the object to associate with the timer.</p>
     */
    @Nonnull private final Multimap<String,Pair<String,String>> timerMap;
    
    /** Map of objects to contexts to perform a stop signal. */
    @Nonnull private final Multimap<String,Timer.Context> timerContextMap;
    
    /** Map of objects to counter names. */
    @Nonnull private final Map<String,String> counterMap;
    
    /** Constructor. */
    @SuppressWarnings("null")
    public MetricContext() {
        timerMap = ArrayListMultimap.create();
        timerContextMap = ArrayListMultimap.create();
        counterMap = new HashMap<>();
    }
        
    /**
     * Add an object/timer mapping.
     * 
     * @param timerName name of timer
     * @param startId ID of object to start timer with
     * @param stopId ID of object to stop timer
     * 
     * @return this context
     */
    @Nonnull public MetricContext addTimer(@Nonnull @NotEmpty final String timerName,
            @Nonnull @NotEmpty final String startId, @Nonnull @NotEmpty final String stopId) {
        
        final String key = Constraint.isNotNull(StringSupport.trimOrNull(startId),
                "Starting object ID cannot be null or empty");
        final String stop = Constraint.isNotNull(StringSupport.trimOrNull(stopId),
                "Stop object ID cannot be null or empty");
        final String name = Constraint.isNotNull(StringSupport.trimOrNull(timerName),
                "Timer name cannot be null or empty");
        timerMap.put(key, new Pair<>(name, stop));
        
        return this;
    }
    
    /**
     * Get a modifiable collection of timer name / stop object pairs for the supplied
     * start object ID.
     * 
     * @param objectId the object ID input
     * 
     * @return the collection of associated mappings
     */
    @Nonnull @Live public Collection<Pair<String,String>> getTimerMappings(
            @Nonnull @NotEmpty final String objectId) {
        return timerMap.get(objectId);
    }
    
    /**
     * Add an object/counter mapping.
     * 
     * @param counterName name of counter
     * @param objectId object ID
     * 
     * @return this context
     */
    @Nonnull public MetricContext addCounter(@Nonnull @NotEmpty final String counterName,
            @Nonnull @NotEmpty final String objectId) {
        final String key = Constraint.isNotNull(StringSupport.trimOrNull(objectId),
                "Starting object ID cannot be null or empty");
        final String name = Constraint.isNotNull(StringSupport.trimOrNull(counterName),
                "Counter name cannot be null or empty");
        counterMap.put(key, name);
        
        return this;
    }
    
    /**
     * Get a modifiable map of object/counter associations.
     * 
     * @return map of counters
     */
    @Nonnull @Live public Map<String,String> getCounterMappings() {
        return counterMap;
    }
    
    /**
     * Conditionally starts one or more timers based on the supplied object identifier.
     * 
     *  <p>The configured state of the context is used to determine whether, and which,
     *  timers to start, further influenced by the runtime state of the system with regard
     *  to enabling of metrics.</p>
     * 
     * @param objectId ID of the object being timed
     */
    public void start(@Nonnull @NotEmpty final String objectId) {
        
        final MetricRegistry registry = MetricsSupport.getMetricRegistry();
        if (registry == null) {
            return;
        }
        
        for (final Pair<String,String> timer : timerMap.get(objectId)) {
            if (timer != null) {
                timerContextMap.put(timer.getSecond(), registry.timer(timer.getFirst()).time());
            }
        }
    }

    /**
     * Stops any timers associated with the supplied object identifier and removes them
     * from the tracking map.
     * 
     * @param objectId ID of the object being timed
     */
    public void stop(@Nonnull @NotEmpty final String objectId) {
        final Iterator<Timer.Context> iter = timerContextMap.get(objectId).iterator();
        while (iter.hasNext()) {
            final Timer.Context tc = iter.next();
            if (tc != null) {
                tc.stop();
                iter.remove();
            }
        }
    }
    
    /**
     * Increment a counter associated with an object, if any.
     * 
     * @param objectId ID of object
     */
    public void inc(@Nonnull @NotEmpty final String objectId) {
        final MetricRegistry registry = MetricsSupport.getMetricRegistry();
        if (registry == null) {
            return;
        }

        final String name = counterMap.get(objectId);
        if (name != null) {
            registry.counter(name).inc();
        }
    }

    /**
     * Decrement a counter associated with an object, if any.
     * 
     * @param objectId ID of object
     */
    public void dec(@Nonnull @NotEmpty final String objectId) {
        final MetricRegistry registry = MetricsSupport.getMetricRegistry();
        if (registry == null) {
            return;
        }

        final String name = counterMap.get(objectId);
        if (name != null) {
            registry.counter(name).dec();
        }
    }

}