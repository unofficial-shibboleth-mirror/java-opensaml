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

import javax.annotation.Nonnull;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.Initializer;
import org.slf4j.Logger;

import com.codahale.metrics.MetricRegistry;

import net.shibboleth.shared.primitive.LoggerFactory;

/**
 * An initializer for the {@link MetricRegistry} held by the {@link ConfigurationService}.
 */
public class MetricRegistryInitializer implements Initializer {

    /** Logger. */
    @Nonnull private Logger log = LoggerFactory.getLogger(MetricRegistryInitializer.class);

    /** {@inheritDoc} */
    public void init() throws InitializationException {
        synchronized(ConfigurationService.class) {
            MetricRegistry registry = ConfigurationService.get(MetricRegistry.class);
            if (registry == null) {
                log.debug("MetricRegistry did not exist in ConfigurationService, a disabled one will be created");
                registry = new FilteredMetricRegistry();
                ConfigurationService.register(MetricRegistry.class, registry);
            }
        }
    }

}